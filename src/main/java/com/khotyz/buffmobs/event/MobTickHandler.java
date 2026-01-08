package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.util.MobBuffUtil;
import com.khotyz.buffmobs.util.RangedMobAIManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MobTickHandler {
    private static final Set<UUID> INITIALIZED_MOBS = new HashSet<>();
    private static int globalTickCounter = 0;
    private static boolean initialScanDone = false;

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            BuffMobsMod.LOGGER.info("Server started - scanning for existing mobs...");
            int count = 0;
            for (ServerWorld world : server.getWorlds()) {
                for (Entity entity : world.iterateEntities()) {
                    if (entity instanceof MobEntity mob) {
                        initializeMob(mob);
                        count++;
                    }
                }
            }
            BuffMobsMod.LOGGER.info("Initialized {} existing mobs", count);
            initialScanDone = true;
        });

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof MobEntity mob && !world.isClient()) {
                if (!INITIALIZED_MOBS.contains(mob.getUuid())) {
                    initializeMob(mob);
                }
            }
        });

        ServerTickEvents.END_WORLD_TICK.register(MobTickHandler::onWorldTick);

        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity instanceof MobEntity mob) {
                INITIALIZED_MOBS.remove(mob.getUuid());
                RangedMobAIManager.cleanup(mob);
            }
        });
    }

    private static void initializeMob(MobEntity mob) {
        if (!BuffMobsMod.CONFIG.enabled) return;

        UUID uuid = mob.getUuid();
        if (INITIALIZED_MOBS.contains(uuid)) return;

        try {
            if (MobBuffUtil.isValidMob(mob)) {
                MobBuffUtil.applyBuffs(mob);
                RangedMobAIManager.initializeMob(mob);
                INITIALIZED_MOBS.add(uuid);

                if (BuffMobsMod.LOGGER.isDebugEnabled()) {
                    BuffMobsMod.LOGGER.debug("Initialized mob: {} ({}) in {}",
                            Registries.ENTITY_TYPE.getId(mob.getType()),
                            mob.getUuid(),
                            mob.getWorld().getRegistryKey().getValue());
                }
            } else {
                if (BuffMobsMod.LOGGER.isDebugEnabled()) {
                    BuffMobsMod.LOGGER.debug("Skipped invalid mob: {} in {}",
                            Registries.ENTITY_TYPE.getId(mob.getType()),
                            mob.getWorld().getRegistryKey().getValue());
                }
            }
        } catch (Exception e) {
            BuffMobsMod.LOGGER.error("Failed to initialize mob: {}", mob.getType(), e);
        }
    }

    private static void onWorldTick(ServerWorld world) {
        if (!BuffMobsMod.CONFIG.enabled) return;

        globalTickCounter++;

        if (globalTickCounter % 100 == 0 && !initialScanDone) {
            for (Entity entity : world.iterateEntities()) {
                if (entity instanceof MobEntity mob && !INITIALIZED_MOBS.contains(mob.getUuid())) {
                    initializeMob(mob);
                }
            }
        }

        world.iterateEntities().forEach(entity -> {
            if (entity instanceof MobEntity mob && !mob.isRemoved()) {
                UUID uuid = mob.getUuid();

                if (!INITIALIZED_MOBS.contains(uuid)) {
                    initializeMob(mob);
                } else {
                    try {
                        RangedMobAIManager.updateMobBehavior(mob);
                        MobBuffUtil.refreshInfiniteEffects(mob);
                    } catch (Exception e) {
                        BuffMobsMod.LOGGER.warn("Error updating mob behavior", e);
                    }
                }
            }
        });
    }

    public static int getInitializedCount() {
        return INITIALIZED_MOBS.size();
    }
}