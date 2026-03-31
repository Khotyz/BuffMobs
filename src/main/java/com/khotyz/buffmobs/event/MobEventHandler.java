package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.util.MobBuffUtil;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;

import java.util.HashMap;
import java.util.Map;

import static com.khotyz.buffmobs.util.DimensionUtil.getDimensionId;

public class MobEventHandler {

    private static final Map<String, Long> lastDayCheck = new HashMap<>();
    private static final RandomSource random = RandomSource.create();

    public static void register() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
            if (!BuffMobsConfig.INSTANCE.enabled || !BuffMobsConfig.INSTANCE.harmfulEffects.enabled) return;
            if (!(entity instanceof Player player)) return;

            Entity attacker = source.getEntity();
            Entity direct   = source.getDirectEntity();

            Mob attackingMob = null;
            if (attacker instanceof Mob mob && MobBuffUtil.isValidMob(mob)) {
                attackingMob = mob;
            } else if (direct instanceof Projectile proj && proj.getOwner() instanceof Mob mob
                    && MobBuffUtil.isValidMob(mob)) {
                attackingMob = mob;
            }

            if (attackingMob != null
                    && random.nextFloat() < (float) BuffMobsConfig.INSTANCE.harmfulEffects.chance) {
                applyRandomHarmfulEffect(player);
            }
        });

        ServerTickEvents.END_WORLD_TICK.register(serverLevel -> {
            if (!BuffMobsConfig.INSTANCE.enabled) return;
            handleDayScaling(serverLevel);
        });
    }

    private static void handleDayScaling(ServerLevel world) {
        if (!BuffMobsConfig.INSTANCE.dayScaling.enabled
                || !BuffMobsConfig.INSTANCE.dayScaling.showNotifications
                || world.players().isEmpty()) return;

        long currentTime = world.getDayTime();
        long currentDay  = currentTime / 24000L;
        String worldKey  = getDimensionId(world);

        Long lastChecked = lastDayCheck.get(worldKey);
        if (lastChecked == null || currentDay > lastChecked) {
            lastDayCheck.put(worldKey, currentDay);

            if (lastChecked != null && currentDay > 0) {
                boolean notify  = false;
                int scalingInt  = BuffMobsConfig.INSTANCE.dayScaling.interval;

                switch (BuffMobsConfig.INSTANCE.dayScaling.notificationMode) {
                    case EVERY_DAY             -> notify = true;
                    case SCALING_INCREASE_ONLY -> notify = currentDay % scalingInt == 0;
                }

                if (notify) sendDayScalingNotification(world, currentDay);
            }
        }
    }

    private static void sendDayScalingNotification(ServerLevel world, long currentDay) {
        double mult     = MobBuffUtil.getDayMultiplier(world.getDayTime());
        double maxMult  = BuffMobsConfig.INSTANCE.dayScaling.maxMultiplier;
        int    interval = BuffMobsConfig.INSTANCE.dayScaling.interval;
        long   daysUntil = interval - (currentDay % interval);
        if (daysUntil == interval) daysUntil = 0;

        final Component msg;
        if (mult >= maxMult) {
            msg = Component.translatable("buffmobs.notify.day_scaling.max", currentDay, mult);
        } else {
            final long du = daysUntil;
            msg = Component.translatable("buffmobs.notify.day_scaling",
                    currentDay, mult, du, du != 1 ? "s" : "");
        }

        world.players().forEach(p -> p.sendSystemMessage(msg));
    }

    private static void applyRandomHarmfulEffect(Player player) {
        switch (random.nextInt(3)) {
            case 0 -> MobBuffUtil.applyPoisonToPlayer  (player, BuffMobsConfig.INSTANCE.harmfulEffects.poisonDuration);
            case 1 -> MobBuffUtil.applySlownessToPlayer(player, BuffMobsConfig.INSTANCE.harmfulEffects.slownessDuration);
            case 2 -> MobBuffUtil.applyWitherToPlayer  (player, BuffMobsConfig.INSTANCE.harmfulEffects.witherDuration);
        }
    }
}