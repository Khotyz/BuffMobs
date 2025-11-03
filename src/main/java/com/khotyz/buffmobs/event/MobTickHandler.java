package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.util.MobBuffUtil;
import com.khotyz.buffmobs.util.RangedMobAIManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@EventBusSubscriber(modid = BuffMobsMod.MOD_ID)
public class MobTickHandler {
    private static final Set<UUID> INITIALIZED_MOBS = new HashSet<>();
    private static int globalTickCounter = 0;
    private static boolean initialScanDone = false;

    public static void register() {
        // Registration happens via @EventBusSubscriber annotation
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        BuffMobsMod.LOGGER.info("Server started - scanning for existing mobs...");
        int count = 0;
        for (ServerLevel world : event.getServer().getAllLevels()) {
            for (Entity entity : world.getAllEntities()) {
                if (entity instanceof Mob mob) {
                    initializeMob(mob);
                    count++;
                }
            }
        }
        BuffMobsMod.LOGGER.info("Initialized {} existing mobs", count);
        initialScanDone = true;
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Mob mob && !event.getLevel().isClientSide()) {
            if (!INITIALIZED_MOBS.contains(mob.getUUID())) {
                initializeMob(mob);
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (!BuffMobsConfig.enabled.get()) return;

        globalTickCounter++;

        for (ServerLevel level : event.getServer().getAllLevels()) {
            if (globalTickCounter % 100 == 0 && !initialScanDone) {
                for (Entity entity : level.getAllEntities()) {
                    if (entity instanceof Mob mob && !INITIALIZED_MOBS.contains(mob.getUUID())) {
                        initializeMob(mob);
                    }
                }
            }

            if (globalTickCounter % 20 == 0) {
                level.getAllEntities().forEach(entity -> {
                    if (entity instanceof Mob mob && !mob.isRemoved()) {
                        UUID uuid = mob.getUUID();

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
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof Mob mob) {
            INITIALIZED_MOBS.remove(mob.getUUID());
            RangedMobAIManager.cleanup(mob);
        }
    }

    private static void initializeMob(Mob mob) {
        if (!BuffMobsConfig.enabled.get()) return;

        UUID uuid = mob.getUUID();
        if (INITIALIZED_MOBS.contains(uuid)) return;

        try {
            if (MobBuffUtil.isValidMob(mob)) {
                MobBuffUtil.applyBuffs(mob);
                RangedMobAIManager.initializeMob(mob);
                INITIALIZED_MOBS.add(uuid);

                if (BuffMobsMod.LOGGER.isDebugEnabled()) {
                    BuffMobsMod.LOGGER.debug("Initialized mob: {} ({}) in {}",
                            mob.getType().toString(),
                            mob.getUUID(),
                            mob.level().dimension().location());
                }
            }
        } catch (Exception e) {
            BuffMobsMod.LOGGER.error("Failed to initialize mob: {}", mob.getType(), e);
        }
    }

    public static int getInitializedCount() {
        return INITIALIZED_MOBS.size();
    }
}