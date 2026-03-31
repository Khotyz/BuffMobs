package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.util.MobBuffUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.HashMap;
import java.util.Map;

import static com.khotyz.buffmobs.util.DimensionUtil.getDimensionId;

public class MobEventHandler {
    private final Map<String, Long> lastDayCheck = new HashMap<>();
    private final net.minecraft.util.RandomSource random = net.minecraft.util.RandomSource.create();

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent.Post event) {
        if (!BuffMobsConfig.INSTANCE.enabled.get() || !BuffMobsConfig.INSTANCE.harmfulEffects.enabled.get()) return;
        if (!(event.getEntity() instanceof Player player)) return;

        Entity attacker = event.getSource().getEntity();
        Entity direct   = event.getSource().getDirectEntity();

        Mob attackingMob = null;
        if (attacker instanceof Mob mob && MobBuffUtil.isValidMob(mob)) {
            attackingMob = mob;
        } else if (direct instanceof Projectile proj && proj.getOwner() instanceof Mob mob && MobBuffUtil.isValidMob(mob)) {
            attackingMob = mob;
        }

        if (attackingMob != null && random.nextFloat() < BuffMobsConfig.INSTANCE.harmfulEffects.chance.get()) {
            applyRandomHarmfulEffect(player);
        }
    }

    @SubscribeEvent
    public void onWorldTick(LevelTickEvent.Post event) {
        if (!BuffMobsConfig.INSTANCE.enabled.get()) return;
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        handleDayScaling(serverLevel);
    }

    private void handleDayScaling(ServerLevel world) {
        if (!BuffMobsConfig.INSTANCE.dayScaling.enabled.get()
                || !BuffMobsConfig.INSTANCE.dayScaling.showNotifications.get()
                || world.players().isEmpty()) return;

        long currentTime = world.getDayTime();
        long currentDay  = currentTime / 24000L;
        String worldKey  = getDimensionId(world);

        Long lastChecked = lastDayCheck.get(worldKey);
        if (lastChecked == null || currentDay > lastChecked) {
            lastDayCheck.put(worldKey, currentDay);

            if (lastChecked != null && currentDay > 0) {
                boolean notify  = false;
                int scalingInt  = BuffMobsConfig.INSTANCE.dayScaling.interval.get();

                switch (BuffMobsConfig.INSTANCE.dayScaling.notificationMode.get()) {
                    case EVERY_DAY             -> notify = true;
                    case SCALING_INCREASE_ONLY -> notify = currentDay % scalingInt == 0;
                }

                if (notify) sendDayScalingNotification(world, currentDay);
            }
        }
    }

    private void sendDayScalingNotification(ServerLevel world, long currentDay) {
        double mult      = MobBuffUtil.getDayMultiplier(world.getDayTime());
        double maxMult   = BuffMobsConfig.INSTANCE.dayScaling.maxMultiplier.get();
        int    interval  = BuffMobsConfig.INSTANCE.dayScaling.interval.get();
        long   daysUntil = interval - (currentDay % interval);
        if (daysUntil == interval) daysUntil = 0;

        final Component msg;
        if (mult >= maxMult) {
            msg = Component.translatable("buffmobs.notify.day_scaling.max", currentDay, mult);
        } else {
            final long du = daysUntil;
            msg = Component.translatable("buffmobs.notify.day_scaling", currentDay, mult, du, du != 1 ? "s" : "");
        }

        world.players().forEach(p -> p.sendSystemMessage(msg));
    }

    private void applyRandomHarmfulEffect(Player player) {
        switch (random.nextInt(3)) {
            case 0 -> MobBuffUtil.applyPoisonToPlayer  (player, BuffMobsConfig.INSTANCE.harmfulEffects.poisonDuration.get());
            case 1 -> MobBuffUtil.applySlownessToPlayer(player, BuffMobsConfig.INSTANCE.harmfulEffects.slownessDuration.get());
            case 2 -> MobBuffUtil.applyWitherToPlayer  (player, BuffMobsConfig.INSTANCE.harmfulEffects.witherDuration.get());
        }
    }
}