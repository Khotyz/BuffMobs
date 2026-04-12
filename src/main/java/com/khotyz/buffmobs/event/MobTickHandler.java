package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.util.CombatDraftHandler;
import com.khotyz.buffmobs.util.MobBuffUtil;
import com.khotyz.buffmobs.util.RangedMobAIManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MobTickHandler {

    private static final Set<UUID> INITIALIZED_MOBS = ConcurrentHashMap.newKeySet();
    private static final ConcurrentHashMap<UUID, ResourceKey<Level>> PENDING_INIT = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<ResourceKey<Level>, Integer> DIM_TICK = new ConcurrentHashMap<>();

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            INITIALIZED_MOBS.clear();
            PENDING_INIT.clear();
            DIM_TICK.clear();
            int count = 0;
            for (ServerLevel world : server.getAllLevels()) {
                for (Entity entity : world.getAllEntities()) {
                    if (entity instanceof Mob mob) { initializeMob(mob, true); count++; }
                }
            }
            BuffMobsMod.LOGGER.info("[BuffMobs] Initialized {} existing mobs on startup", count);
        });

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!(entity instanceof Mob mob)) return;
            if (!INITIALIZED_MOBS.contains(mob.getUUID())) {
                PENDING_INIT.put(mob.getUUID(), world.dimension());
            }
        });

        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (!(entity instanceof Mob mob)) return;
            UUID uuid = mob.getUUID();
            INITIALIZED_MOBS.remove(uuid);
            PENDING_INIT.remove(uuid);
            RangedMobAIManager.cleanup(mob);
            CombatDraftHandler.onMobRemoved(mob);
        });

        ServerTickEvents.END_LEVEL_TICK.register(serverLevel -> {
            if (!BuffMobsConfig.INSTANCE.enabled) return;

            ResourceKey<Level> dimKey = serverLevel.dimension();
            int tick = DIM_TICK.merge(dimKey, 1, Integer::sum);

            if (!PENDING_INIT.isEmpty()) {
                Set<UUID> toInit = ConcurrentHashMap.newKeySet();
                for (ConcurrentHashMap.Entry<UUID, ResourceKey<Level>> entry : PENDING_INIT.entrySet()) {
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
                    toInit.forEach(PENDING_INIT::remove);
                }
            }

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
        });
    }

    private static void initializeMob(Mob mob, boolean forceReapply) {
        if (!BuffMobsConfig.INSTANCE.enabled) { MobBuffUtil.removeAllModifiers(mob); return; }
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