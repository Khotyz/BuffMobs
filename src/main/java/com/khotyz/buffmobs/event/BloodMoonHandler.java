package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.data.BuffMobsWorldData;
import com.khotyz.buffmobs.util.MobBuffUtil;
import com.khotyz.buffmobs.util.ResetManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

public class BloodMoonHandler {

    private static final long NIGHT_START_TICK = 13000L;
    private static final long NIGHT_END_TICK = 23000L;

    public static boolean isEnabled() {
        return BuffMobsConfig.INSTANCE.enabled && BuffMobsConfig.INSTANCE.bloodMoon.enabled;
    }

    public static boolean isActive(Level level) {
        if (!(level instanceof ServerLevel) || !isEnabled()) return false;
        BuffMobsWorldData data = BuffMobsWorldData.get(level);
        return data != null && data.getBloodMoonActiveDay() >= 0L;
    }

    public static boolean isActiveForLoot(Level level) {
        return isActive(level) && BuffMobsConfig.INSTANCE.bloodMoon.doubleLootAndXp;
    }

    public static double getActiveMultiplier(Level level) {
        if (!isActive(level)) return 1.0;
        return BuffMobsConfig.INSTANCE.bloodMoon.extraMultiplier;
    }

    public static void resetCycle(MinecraftServer server) {
        BuffMobsWorldData data = BuffMobsWorldData.get(server);
        if (data != null) {
            data.setLastBloodMoonDay(getCurrentDayIndex(server));
            data.setBloodMoonActiveDay(-1L);
        }
        clearGlowing(server);
    }

    public static void clearCaches() {
    }

    public static void onMobLoaded(Mob mob) {
        BuffMobsWorldData data = BuffMobsWorldData.get(mob.level());
        if (data == null) return;
        BuffMobsWorldData.MobState state = data.getMobState(mob.getUUID());
        if (state.bloodMoonGlowing() && !isActive(mob.level())) {
            mob.removeEffect(MobEffects.GLOWING);
            data.updateMobState(mob.getUUID(), current -> current.setBloodMoonGlowing(false));
        } else if (isActive(mob.level()) && BuffMobsConfig.INSTANCE.bloodMoon.glowingMobs) {
            applyGlowing(mob, data);
        }
    }

    public static void tickServer(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        if (overworld == null || overworld.getGameTime() % 20L != 0L) return;
        BuffMobsWorldData data = BuffMobsWorldData.get(server);
        if (data == null) return;
        long savedActiveDay = data.getBloodMoonActiveDay();
        boolean active = savedActiveDay >= 0L;

        if (!BuffMobsConfig.INSTANCE.enabled || !BuffMobsConfig.INSTANCE.bloodMoon.enabled) {
            if (active) endBloodMoon(server, false);
            return;
        }

        long dayTime = overworld.getOverworldClockTime();
        long dayIndex = dayTime / 24000L;
        long timeOfDay = dayTime % 24000L;
        boolean night = timeOfDay >= NIGHT_START_TICK && timeOfDay <= NIGHT_END_TICK;

        if (!active && night && isEligible(server, dayIndex)) {
            startBloodMoon(server, dayIndex);
        } else if (active && (!night || dayIndex != savedActiveDay)) {
            endBloodMoon(server, true);
        }

        if (data.getBloodMoonActiveDay() >= 0L && BuffMobsConfig.INSTANCE.bloodMoon.glowingMobs
                && overworld.getGameTime() % 100L == 0L) {
            applyGlowing(server);
        }
    }

    private static long getCurrentDayIndex(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        if (overworld == null) return 0L;
        return overworld.getOverworldClockTime() / 24000L;
    }

    private static boolean isEligible(MinecraftServer server, long dayIndex) {
        BuffMobsWorldData data = BuffMobsWorldData.get(server);
        if (data == null) return false;
        int interval = Math.max(1, BuffMobsConfig.INSTANCE.bloodMoon.intervalDays);
        return dayIndex - data.getLastBloodMoonDay() >= interval;
    }

    private static void startBloodMoon(MinecraftServer server, long dayIndex) {
        BuffMobsWorldData data = BuffMobsWorldData.get(server);
        if (data != null) {
            data.setLastBloodMoonDay(dayIndex);
            data.setBloodMoonActiveDay(dayIndex);
        }
        BuffMobsMod.LOGGER.info("[BuffMobs] Blood Moon started on day {}", dayIndex);
        if (BuffMobsConfig.INSTANCE.bloodMoon.notifyPlayers) {
            server.getPlayerList().broadcastSystemMessage(
                    Component.translatable("buffmobs.notify.blood_moon.start"), false);
        }
        ResetManager.reapplyAllMobs(server);
        if (BuffMobsConfig.INSTANCE.bloodMoon.glowingMobs) applyGlowing(server);
    }

    private static void endBloodMoon(MinecraftServer server, boolean notify) {
        BuffMobsWorldData data = BuffMobsWorldData.get(server);
        if (data != null) data.setBloodMoonActiveDay(-1L);
        clearGlowing(server);
        BuffMobsMod.LOGGER.info("[BuffMobs] Blood Moon ended");
        if (notify && BuffMobsConfig.INSTANCE.bloodMoon.notifyPlayers) {
            server.getPlayerList().broadcastSystemMessage(
                    Component.translatable("buffmobs.notify.blood_moon.end"), false);
        }
        if (BuffMobsConfig.INSTANCE.enabled) ResetManager.reapplyAllMobs(server);
    }

    private static void applyGlowing(MinecraftServer server) {
        for (ServerLevel level : server.getAllLevels()) {
            BuffMobsWorldData data = BuffMobsWorldData.get(level);
            if (data == null) continue;
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof Mob mob && !mob.isRemoved() && MobBuffUtil.isValidMob(mob)) {
                    applyGlowing(mob, data);
                }
            }
        }
    }

    private static void applyGlowing(Mob mob, BuffMobsWorldData data) {
        if (mob.getEffect(MobEffects.GLOWING) == null) {
            mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, 120, 0, false, false, true));
            data.updateMobState(mob.getUUID(), state -> state.setBloodMoonGlowing(true));
        } else if (data.getMobState(mob.getUUID()).bloodMoonGlowing()) {
            mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, 120, 0, false, false, true));
        }
    }

    private static void clearGlowing(MinecraftServer server) {
        for (ServerLevel level : server.getAllLevels()) {
            BuffMobsWorldData data = BuffMobsWorldData.get(level);
            if (data == null) continue;
            for (Entity entity : level.getAllEntities()) {
                if (!(entity instanceof Mob mob) || mob.isRemoved()) continue;
                if (!data.getMobState(mob.getUUID()).bloodMoonGlowing()) continue;
                mob.removeEffect(MobEffects.GLOWING);
                data.updateMobState(mob.getUUID(), state -> state.setBloodMoonGlowing(false));
            }
        }
    }
}
