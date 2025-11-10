package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.util.MobBuffUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.util.RandomSource;

import java.util.HashMap;
import java.util.Map;

public class MobEventHandler {
    private final Map<String, Long> lastDayCheck = new HashMap<>();
    private final RandomSource random = RandomSource.create();

    public void onLivingDamage(LivingEntity entity, DamageSource source, float damageTaken) {
        if (!BuffMobsConfig.enabled.get() || !BuffMobsConfig.HarmfulEffects.enabled.get()) return;

        if (entity instanceof Player player) {
            Entity attacker = source.getEntity();
            Entity directEntity = source.getDirectEntity();

            Mob attackingMob = null;

            if (attacker instanceof Mob mob && MobBuffUtil.isValidMob(mob)) {
                attackingMob = mob;
            } else if (directEntity instanceof Projectile projectile &&
                    projectile.getOwner() instanceof Mob mob &&
                    MobBuffUtil.isValidMob(mob)) {
                attackingMob = mob;
            }

            if (attackingMob != null &&
                    random.nextFloat() < BuffMobsConfig.HarmfulEffects.chance.get()) {
                applyRandomHarmfulEffect(player);
            }
        }
    }

    public void onWorldTick(ServerLevel world) {
        if (!BuffMobsConfig.enabled.get()) return;
        handleDayScaling(world);
    }

    private void handleDayScaling(ServerLevel world) {
        if (!BuffMobsConfig.DayScaling.enabled.get() ||
                !BuffMobsConfig.DayScaling.showNotifications.get() ||
                world.players().isEmpty()) return;

        long currentTime = world.getDayTime();
        long currentDay = currentTime / 24000L;
        String worldKey = world.dimension().location().toString();

        Long lastCheckedDay = lastDayCheck.get(worldKey);

        if (lastCheckedDay == null || currentDay > lastCheckedDay) {
            lastDayCheck.put(worldKey, currentDay);

            if (lastCheckedDay != null && currentDay > 0) {
                boolean shouldNotify = false;
                int scalingInterval = BuffMobsConfig.DayScaling.interval.get();

                switch (BuffMobsConfig.DayScaling.notificationMode.get()) {
                    case EVERY_DAY -> shouldNotify = true;
                    case SCALING_INCREASE_ONLY -> shouldNotify = currentDay % scalingInterval == 0;
                }

                if (shouldNotify) {
                    sendDayScalingNotification(world, currentDay);
                }
            }
        }
    }

    private void sendDayScalingNotification(ServerLevel world, long currentDay) {
        double currentMultiplier = MobBuffUtil.getDayMultiplier(world.getDayTime());
        double maxMultiplier = BuffMobsConfig.DayScaling.maxMultiplier.get();
        int scalingInterval = BuffMobsConfig.DayScaling.interval.get();

        long daysUntilNextScaling = scalingInterval - (currentDay % scalingInterval);
        if (daysUntilNextScaling == scalingInterval) daysUntilNextScaling = 0;

        boolean isMaxed = currentMultiplier >= maxMultiplier;

        Component message;
        if (isMaxed) {
            message = Component.literal(String.format(
                    "Day %d - Mob Scaling: %.1fx (MAXIMUM)", currentDay, currentMultiplier));
        } else {
            message = Component.literal(String.format(
                    "Day %d - Mob Scaling: %.1fx | Next increase in %d day%s",
                    currentDay, currentMultiplier, daysUntilNextScaling,
                    daysUntilNextScaling != 1 ? "s" : ""));
        }

        world.players().forEach(player -> player.sendSystemMessage(message));
    }

    private void applyRandomHarmfulEffect(Player player) {
        int effectType = random.nextInt(3);

        switch (effectType) {
            case 0 -> MobBuffUtil.applyPoisonToPlayer(player,
                    BuffMobsConfig.HarmfulEffects.poisonDuration.get());
            case 1 -> MobBuffUtil.applySlownessToPlayer(player,
                    BuffMobsConfig.HarmfulEffects.slownessDuration.get());
            case 2 -> MobBuffUtil.applyWitherToPlayer(player,
                    BuffMobsConfig.HarmfulEffects.witherDuration.get());
        }
    }
}