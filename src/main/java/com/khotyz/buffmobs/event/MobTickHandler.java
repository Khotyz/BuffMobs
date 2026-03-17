package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.util.CombatDraftHandler;
import com.khotyz.buffmobs.util.MobBuffUtil;
import com.khotyz.buffmobs.util.RangedMobAIManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MobTickHandler {
    private static final Set<UUID> INITIALIZED_MOBS = new HashSet<>();
    // Maps mob UUID → dimension key where the mob is waiting for init
    private static final Map<UUID, ResourceKey<Level>> PENDING_INIT = new HashMap<>();
    // Per-dimension tick counter so each dimension ticks at its own rate
    private static final Map<ResourceKey<Level>, Integer> DIM_TICK = new HashMap<>();

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        INITIALIZED_MOBS.clear();
        PENDING_INIT.clear();
        DIM_TICK.clear();
        int count = 0;
        for (ServerLevel world : event.getServer().getAllLevels()) {
            for (Entity entity : world.getAllEntities()) {
                if (entity instanceof Mob mob) { initializeMob(mob, true); count++; }
            }
        }
        BuffMobsMod.LOGGER.info("[BuffMobs] Initialized {} existing mobs on startup", count);
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof Mob mob)) return;
        if (!INITIALIZED_MOBS.contains(mob.getUUID())) {
            PENDING_INIT.put(mob.getUUID(), event.getLevel().dimension());
        }
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

        ResourceKey<Level> dimKey = serverLevel.dimension();
        int tick = DIM_TICK.merge(dimKey, 1, Integer::sum);

        // Process pending inits for this specific dimension
        if (!PENDING_INIT.isEmpty()) {
            Set<UUID> toInit = new HashSet<>();
            for (Map.Entry<UUID, ResourceKey<Level>> entry : PENDING_INIT.entrySet()) {
                if (entry.getValue().equals(dimKey)) toInit.add(entry.getKey());
            }
            if (!toInit.isEmpty()) {
                for (Entity e : serverLevel.getAllEntities()) {
                    if (!(e instanceof Mob mob) || mob.isRemoved()) continue;
                    if (toInit.remove(mob.getUUID())) {
                        PENDING_INIT.remove(mob.getUUID());
                        initializeMob(mob, false);
                    }
                }
                // Remove any remaining UUIDs that didn't match (mob left already)
                toInit.forEach(PENDING_INIT::remove);
            }
        }

        // High-frequency tick every tick
        for (Entity e : serverLevel.getAllEntities()) {
            if (!(e instanceof Mob mob) || mob.isRemoved()) continue;
            if (!INITIALIZED_MOBS.contains(mob.getUUID())) continue;
            try {
                RangedMobAIManager.driveTick(mob);
                CombatDraftHandler.tickRestore(mob);
            } catch (Exception ex) {
                BuffMobsMod.LOGGER.warn("[BuffMobs] Error in high-frequency tick", ex);
            }
        }

        // Low-frequency tick every 20 ticks per dimension
        if (tick % 20 == 0) {
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

    public static void markInitialized(Mob mob) {
        INITIALIZED_MOBS.add(mob.getUUID());
        PENDING_INIT.remove(mob.getUUID());
    }

    public static void forceReinitAll() {
        INITIALIZED_MOBS.clear();
        PENDING_INIT.clear();
    }
}