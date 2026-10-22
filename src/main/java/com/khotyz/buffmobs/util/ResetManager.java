package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.data.BuffMobsWorldData;
import com.khotyz.buffmobs.event.BloodMoonHandler;
import com.khotyz.buffmobs.event.EliteMobHandler;
import com.khotyz.buffmobs.event.MobTickHandler;
import com.khotyz.buffmobs.event.PackAggressionHandler;
import com.khotyz.buffmobs.event.PassiveMobAggressionHandler;
import com.khotyz.buffmobs.event.SpecialAbilityHandler;
import com.khotyz.buffmobs.event.TraitsHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

import java.util.ArrayList;
import java.util.List;

public class ResetManager {

    public static int resetAll(MinecraftServer server) {
        applyScalingOffset(server);
        BuffMobsWorldData data = BuffMobsWorldData.get(server);
        if (data != null) data.resetAdaptiveCounters();
        BloodMoonHandler.resetCycle(server);
        if (data != null) data.clearMobStates();
        int count = resetMobs(server);
        BuffMobsMod.LOGGER.info("[BuffMobs] Full progression reset completed, {} mobs reinitialized", count);
        return count;
    }

    public static int resetScaling(MinecraftServer server) {
        applyScalingOffset(server);
        int count = reapplyAllMobs(server);
        BuffMobsMod.LOGGER.info("[BuffMobs] Scaling reset to day 0, {} mobs reapplied", count);
        return count;
    }

    public static int resetAdaptive(MinecraftServer server) {
        BuffMobsWorldData data = BuffMobsWorldData.get(server);
        if (data != null) data.resetAdaptiveCounters();
        int count = reapplyAllMobs(server);
        BuffMobsMod.LOGGER.info("[BuffMobs] Adaptive difficulty counters reset, {} mobs reapplied", count);
        return count;
    }

    public static int resetBloodMoon(MinecraftServer server) {
        BloodMoonHandler.resetCycle(server);
        int count = reapplyAllMobs(server);
        BuffMobsMod.LOGGER.info("[BuffMobs] Blood Moon cycle reset, {} mobs reapplied", count);
        return count;
    }

    public static int resetMobs(MinecraftServer server) {
        List<Mob> mobs = collectLoadedMobs(server);
        BuffMobsWorldData data = BuffMobsWorldData.get(server);
        for (Mob mob : mobs) {
            EliteMobHandler.removeElite(mob);
            TraitsHandler.removeTrait(mob);
            SpecialAbilityHandler.resetMob(mob);
            MobArmorManager.stripArmor(mob);
            PassiveMobAggressionHandler.resetMob(mob);
            MobBuffUtil.removeAllModifiers(mob);
            MobBuffUtil.removeAllBuffEffects(mob);
            RangedMobAIManager.cleanup(mob);
            CombatDraftHandler.onMobRemoved(mob);
            if (data != null) data.resetMobState(mob.getUUID());
        }
        MobTickHandler.clearCaches();
        EliteMobHandler.clearCaches();
        TraitsHandler.clearCaches();
        SpecialAbilityHandler.clearCaches();
        PackAggressionHandler.clearCaches();
        MobArmorManager.clearCaches();
        RangedMobAIManager.clearAllStates();
        CombatDraftHandler.clearStates();
        PassiveMobAggressionHandler.forceReinit();

        int count = 0;
        for (Mob mob : mobs) {
            if (mob.isRemoved() || !mob.isAlive()) continue;
            MobTickHandler.reinitializeMob(mob);
            count++;
        }
        BuffMobsMod.LOGGER.info("[BuffMobs] Mob caches cleared, {} mobs reinitialized", count);
        return count;
    }

    public static int reapplyAllMobs(MinecraftServer server) {
        int count = 0;
        for (Mob mob : collectLoadedMobs(server)) {
            if (mob.isRemoved() || !mob.isAlive()) continue;
            try {
                MobBuffUtil.applyBuffs(mob);
                count++;
            } catch (Exception ex) {
                BuffMobsMod.LOGGER.warn("[BuffMobs] Failed to reapply buffs during reset", ex);
            }
        }
        return count;
    }

    private static void applyScalingOffset(MinecraftServer server) {
        BuffMobsWorldData data = BuffMobsWorldData.get(server);
        ServerLevel overworld = server.overworld();
        if (data == null || overworld == null) return;
        data.setScalingDayOffset(MobBuffUtil.getRawDays(overworld));
    }

    private static List<Mob> collectLoadedMobs(MinecraftServer server) {
        List<Mob> mobs = new ArrayList<>();
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof Mob mob) mobs.add(mob);
            }
        }
        return mobs;
    }
}
