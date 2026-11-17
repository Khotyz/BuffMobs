package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.util.CombatDraftHandler;
import com.khotyz.buffmobs.util.MobArmorManager;
import com.khotyz.buffmobs.util.MobBuffUtil;
import com.khotyz.buffmobs.util.RangedMobAIManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
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
    private static final Set<String> LOGGED_REJECTIONS = ConcurrentHashMap.newKeySet();
    private static final int REJECTION_LOG_LIMIT = 8;

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            INITIALIZED_MOBS.clear();
            PENDING_INIT.clear();
            DIM_TICK.clear();
            LOGGED_REJECTIONS.clear();
            BloodMoonHandler.clearCaches();
            logGateState();
            int count = 0;
            for (ServerLevel world : server.getAllLevels()) {
                for (Entity entity : snapshot(world)) {
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
            EliteMobHandler.onMobRemoved(mob);
            TraitsHandler.onMobRemoved(mob);
            SpecialAbilityHandler.onMobRemoved(mob);
            PackAggressionHandler.onMobRemoved(mob);
            MobArmorManager.onMobRemoved(mob);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            BloodMoonHandler.tickServer(server);
            if (!BuffMobsConfig.INSTANCE.enabled) return;

            for (ServerLevel serverLevel : server.getAllLevels()) {
                ResourceKey<Level> dimKey = serverLevel.dimension();
                int tick = DIM_TICK.merge(dimKey, 1, Integer::sum);

                if (!PENDING_INIT.isEmpty()) {
                    Set<UUID> toInit = ConcurrentHashMap.newKeySet();
                    for (ConcurrentHashMap.Entry<UUID, ResourceKey<Level>> entry : PENDING_INIT.entrySet()) {
                        if (entry.getValue().equals(dimKey)) toInit.add(entry.getKey());
                    }
                    if (!toInit.isEmpty()) {
                        for (Entity e : snapshot(serverLevel)) {
                            if (!(e instanceof Mob mob) || mob.isRemoved()) continue;
                            if (toInit.remove(mob.getUUID())) {
                                PENDING_INIT.remove(mob.getUUID());
                                initializeMob(mob, false);
                            }
                        }
                        toInit.forEach(PENDING_INIT::remove);
                    }
                }

                for (Entity e : snapshot(serverLevel)) {
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
                    for (Entity e : snapshot(serverLevel)) {
                        if (!(e instanceof Mob mob) || mob.isRemoved()) continue;
                        UUID uuid = mob.getUUID();
                        if (!INITIALIZED_MOBS.contains(uuid)) {
                            initializeMob(mob, false);
                        } else {
                            try {
                                RangedMobAIManager.updateMobBehavior(mob);
                                MobBuffUtil.refreshInfiniteEffects(mob);
                                CombatDraftHandler.tick(mob);
                                MobBuffUtil.enforceDimensionMaxHealth(mob);
                                EliteMobHandler.tick(mob);
                                TraitsHandler.tick(mob);
                                SpecialAbilityHandler.tick(mob);
                            } catch (Exception ex) {
                                BuffMobsMod.LOGGER.warn("[BuffMobs] Error updating mob behavior", ex);
                            }
                        }
                    }
                }
            }
        });
    }

    private static String initializeMob(Mob mob, boolean forceReapply) {
        try {
            step("bloodmoon", () -> BloodMoonHandler.onMobLoaded(mob));
            if (!BuffMobsConfig.INSTANCE.enabled) {
                MobBuffUtil.removeAllModifiers(mob);
                MobBuffUtil.removeAllBuffEffects(mob);
                RangedMobAIManager.cleanup(mob);
                EliteMobHandler.deactivateMob(mob);
                TraitsHandler.deactivateMob(mob);
                return "mod disabled";
            }
            UUID uuid = mob.getUUID();
            if (!forceReapply && INITIALIZED_MOBS.contains(uuid)) return null;
            String reason = MobBuffUtil.rejectionReason(mob);
            if (reason == null) {
                MobBuffUtil.applyBuffs(mob);
                INITIALIZED_MOBS.add(uuid);
                step("ranged", () -> RangedMobAIManager.initializeMob(mob));
                step("draft", () -> CombatDraftHandler.onMobInitialized(mob));
                step("armor", () -> MobArmorManager.equipArmor(mob));
                step("elite", () -> EliteMobHandler.onMobInitialized(mob));
                step("traits", () -> TraitsHandler.onMobInitialized(mob));
                step("abilities", () -> SpecialAbilityHandler.onMobInitialized(mob));
                return null;
            }
            MobBuffUtil.removeAllModifiers(mob);
            MobBuffUtil.enforceDimensionMaxHealth(mob);
            logRejection(mob);
            return "rejected: " + reason;
        } catch (Throwable e) {
            BuffMobsMod.LOGGER.error("[BuffMobs] Failed to initialize mob: {}", mob.getType(), e);
            return "error: " + e;
        }
    }

    private static void step(String name, Runnable action) {
        try {
            action.run();
        } catch (Throwable t) {
            if (LOGGED_REJECTIONS.size() < 64 && LOGGED_REJECTIONS.add("step:" + name)) {
                BuffMobsMod.LOGGER.error("[BuffMobs] Initialization step '" + name + "' failed", t);
            }
        }
    }

    private static java.util.List<Entity> snapshot(ServerLevel level) {
        java.util.List<Entity> list = new java.util.ArrayList<>();
        for (Entity entity : level.getAllEntities()) list.add(entity);
        return list;
    }

    private static void logRejection(Mob mob) {
        if (LOGGED_REJECTIONS.size() >= REJECTION_LOG_LIMIT) return;
        Identifier typeId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
        if (typeId == null) return;
        if (!LOGGED_REJECTIONS.add(typeId.toString())) return;
        BuffMobsMod.LOGGER.info("[BuffMobs] Skipped {} ({})", typeId, MobBuffUtil.rejectionReason(mob));
    }

    private static void logGateState() {
        BuffMobsConfig cfg = BuffMobsConfig.INSTANCE;
        BuffMobsMod.LOGGER.info("[BuffMobs] Gate state: enabled={} buffNeutralMobs={} mobFilter[blacklist={} whitelist={}] dimensionFilter[useWhitelist={} whitelist={} blacklist={}] modidFilter[useWhitelist={} whitelist={} blacklist={}]",
                cfg.enabled, cfg.buffNeutralMobs,
                cfg.mobFilter.blacklist.size(), cfg.mobFilter.whitelist.size(),
                cfg.dimensionFilter.useWhitelist, cfg.dimensionFilter.whitelist, cfg.dimensionFilter.blacklist,
                cfg.modidFilter.useWhitelist, cfg.modidFilter.whitelist, cfg.modidFilter.blacklist);
    }

    public static int getInitializedCount() { return INITIALIZED_MOBS.size(); }

    public static boolean isInitialized(UUID uuid) {
        return INITIALIZED_MOBS.contains(uuid);
    }

    public static String reinitializeMob(Mob mob) {
        return initializeMob(mob, true);
    }

    public static void clearCaches() {
        INITIALIZED_MOBS.clear();
        PENDING_INIT.clear();
    }

    public static void markInitialized(Mob mob) {
        INITIALIZED_MOBS.add(mob.getUUID());
        PENDING_INIT.remove(mob.getUUID());
    }

    public static void forceReinitAll() {
        INITIALIZED_MOBS.clear();
        PENDING_INIT.clear();
    }
}
