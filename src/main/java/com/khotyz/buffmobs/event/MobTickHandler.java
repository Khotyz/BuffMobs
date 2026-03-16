package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.util.CombatDraftHandler;
import com.khotyz.buffmobs.util.MobBuffUtil;
import com.khotyz.buffmobs.util.RangedMobAIManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MobTickHandler {
    private static final Set<UUID> INITIALIZED_MOBS = new HashSet<>();
    private static final Set<UUID> PENDING_INIT     = new HashSet<>();
    private static int     globalTickCounter = 0;
    private static boolean initialScanDone   = false;

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        INITIALIZED_MOBS.clear();
        PENDING_INIT.clear();
        initialScanDone = false;
        int count = 0;
        for (ServerLevel world : event.getServer().getAllLevels()) {
            for (Entity entity : world.getAllEntities()) {
                if (entity instanceof Mob mob) { initializeMob(mob, true); count++; }
            }
        }
        BuffMobsMod.LOGGER.info("[BuffMobs] Initialized {} existing mobs on startup", count);
        initialScanDone = true;
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof Mob mob)) return;
        if (!INITIALIZED_MOBS.contains(mob.getUUID())) PENDING_INIT.add(mob.getUUID());
    }

    @SubscribeEvent
    public void onEntityLeave(EntityLeaveLevelEvent event) {
        if (!(event.getEntity() instanceof Mob mob)) return;
        UUID uuid = mob.getUUID();
        INITIALIZED_MOBS.remove(uuid);
        PENDING_INIT.remove(uuid);
        RangedMobAIManager.cleanup(mob);
        CombatDraftHandler.onMobRemoved(mob);
    }

    @SubscribeEvent
    public void onWorldTick(LevelTickEvent.Post event) {
        if (!BuffMobsConfig.INSTANCE.enabled.get()) return;
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        globalTickCounter++;

        if (!PENDING_INIT.isEmpty()) {
            for (Entity e : serverLevel.getAllEntities()) {
                if (!(e instanceof Mob mob) || mob.isRemoved()) continue;
                if (PENDING_INIT.contains(mob.getUUID())) {
                    PENDING_INIT.remove(mob.getUUID());
                    initializeMob(mob, false);
                }
            }
            PENDING_INIT.clear();
        }

        if (globalTickCounter % 20 == 0) {
            for (Entity e : serverLevel.getAllEntities()) {
                if (!(e instanceof Mob mob) || mob.isRemoved()) continue;
                UUID uuid = mob.getUUID();
                if (!INITIALIZED_MOBS.contains(uuid)) {
                    initializeMob(mob, false);
                } else {
                    try {
                        RangedMobAIManager.updateMobBehavior(mob);
                        MobBuffUtil.refreshInfiniteEffects(mob);
                        CombatDraftHandler.tick(mob);
                    } catch (Exception ex) {
                        BuffMobsMod.LOGGER.warn("[BuffMobs] Error updating mob behavior", ex);
                    }
                }
            }
        }
    }

    private static void initializeMob(Mob mob, boolean forceReapply) {
        if (!BuffMobsConfig.INSTANCE.enabled.get()) { MobBuffUtil.removeAllModifiers(mob); return; }
        UUID uuid = mob.getUUID();
        if (!forceReapply && INITIALIZED_MOBS.contains(uuid)) return;
        try {
            if (MobBuffUtil.isValidMob(mob)) {
                MobBuffUtil.applyBuffs(mob);
                RangedMobAIManager.initializeMob(mob);
                CombatDraftHandler.onMobInitialized(mob);
                INITIALIZED_MOBS.add(uuid);
                BuffMobsMod.LOGGER.debug("[BuffMobs] Buffed: {}", mob.getType().getDescriptionId());
            } else {
                MobBuffUtil.removeAllModifiers(mob);
            }
        } catch (Exception e) {
            BuffMobsMod.LOGGER.error("[BuffMobs] Failed to initialize mob: {}", mob.getType(), e);
        }
    }

    public static int getInitializedCount() { return INITIALIZED_MOBS.size(); }
}