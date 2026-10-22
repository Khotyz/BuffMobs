package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.util.Guard;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.data.BuffMobsWorldData;
import com.khotyz.buffmobs.util.DamageEventCompat;
import com.khotyz.buffmobs.util.MobBuffUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import static com.khotyz.buffmobs.util.DimensionUtil.getDimensionId;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobEventHandler {
    private final Map<String, Long> lastDayCheck = new HashMap<>();
    private final net.minecraft.util.RandomSource random = net.minecraft.util.RandomSource.create();

    @SubscribeEvent
    public void onIncomingDamage(LivingIncomingDamageEvent event) {
        try {
            if (!BuffMobsConfig.INSTANCE.enabled.get()) return;
            if (TraitsHandler.cancelIncomingDamage(event.getEntity(), event.getSource())) {
                event.setCanceled(true);
            }
        } catch (Throwable throwable) {
            Guard.report("MobEventHandler.onIncomingDamage", throwable);
        }
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent.Post event) {
        try {
            if (!BuffMobsConfig.INSTANCE.enabled.get()) return;

            TraitsHandler.onDamageApplied(event.getEntity(), event.getSource(), DamageEventCompat.damage(event));

            if (event.getEntity() instanceof Mob damagedMob) {
                PackAggressionHandler.onMobDamaged(damagedMob, event.getSource());
                SpecialAbilityHandler.onMobDamaged(damagedMob, event.getSource());
            }

            if (event.getEntity() instanceof Mob mob && MobBuffUtil.isPassiveAggressiveMob(mob)) {
                Entity attacker = event.getSource().getEntity();
                if (attacker instanceof Player player) {
                    PassiveMobAggressionHandler.onMobHurtByPlayer(mob, player);
                }
            }

            if (!BuffMobsConfig.INSTANCE.harmfulEffects.enabled.get()) return;
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
        } catch (Throwable throwable) {
            Guard.report("MobEventHandler.onLivingDamage", throwable);
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        try {
            if (!(event.getEntity() instanceof Mob mob)) return;
            if (BuffMobsConfig.INSTANCE.enabled.get()) {
                SpecialAbilityHandler.onMobDeath(mob, event.getSource());
            }
            BuffMobsWorldData data = BuffMobsWorldData.get(mob.level());
            if (data != null) data.removeMobState(mob.getUUID());
        } catch (Throwable throwable) {
            Guard.report("MobEventHandler.onLivingDeath", throwable);
        }
    }

    @SubscribeEvent
    public void onWorldTick(LevelTickEvent.Post event) {
        try {
            if (!BuffMobsConfig.INSTANCE.enabled.get()) return;
            if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
            if (serverLevel != serverLevel.getServer().overworld()) return;
            handleDayScaling(serverLevel);
        } catch (Throwable throwable) {
            Guard.report("MobEventHandler.onWorldTick", throwable);
        }
    }

    private void handleDayScaling(ServerLevel world) {
        if (!BuffMobsConfig.INSTANCE.dayScaling.enabled.get()
                || !BuffMobsConfig.INSTANCE.dayScaling.showNotifications.get()
                || world.players().isEmpty()) return;

        long currentDay  = MobBuffUtil.getEffectiveDays(world);
        String worldKey  = getDimensionId(world);

        Long lastChecked = lastDayCheck.get(worldKey);
        if (lastChecked == null || currentDay > lastChecked) {
            lastDayCheck.put(worldKey, currentDay);

            if (lastChecked != null && currentDay > 0) {
                boolean notify     = false;
                int scalingInt     = Math.max(1, BuffMobsConfig.INSTANCE.dayScaling.interval.get());

                switch (BuffMobsConfig.INSTANCE.dayScaling.notificationMode.get()) {
                    case EVERY_DAY             -> notify = true;
                    case SCALING_INCREASE_ONLY -> notify = currentDay % scalingInt == 0;
                }

                if (notify) sendDayScalingNotification(world, currentDay);
            }
        }
    }

    private void sendDayScalingNotification(ServerLevel world, long currentDay) {
        double mult      = MobBuffUtil.getDayMultiplier(world);
        double maxMult   = BuffMobsConfig.INSTANCE.dayScaling.maxMultiplier.get();
        int interval     = Math.max(1, BuffMobsConfig.INSTANCE.dayScaling.interval.get());
        long daysUntil   = interval - (currentDay % interval);
        if (daysUntil == interval) daysUntil = 0;

        final long du = daysUntil;
        final Component msg = (mult >= maxMult)
                ? Component.translatable("buffmobs.notify.day_scaling.max", currentDay, mult)
                : Component.translatable("buffmobs.notify.day_scaling", currentDay, mult, du, du != 1 ? "s" : "");

        world.players().forEach(p -> p.sendSystemMessage(msg));
    }

    private void applyRandomHarmfulEffect(Player player) {
        BuffMobsConfig.HarmfulEffects cfg = BuffMobsConfig.INSTANCE.harmfulEffects;
        List<Integer> enabledEffects = new ArrayList<>();
        if (cfg.poisonEnabled.get()) enabledEffects.add(0);
        if (cfg.slownessEnabled.get()) enabledEffects.add(1);
        if (cfg.witherEnabled.get()) enabledEffects.add(2);
        if (enabledEffects.isEmpty()) return;

        switch (enabledEffects.get(random.nextInt(enabledEffects.size()))) {
            case 0 -> MobBuffUtil.applyPoisonToPlayer  (player, cfg.poisonDuration.get());
            case 1 -> MobBuffUtil.applySlownessToPlayer(player, cfg.slownessDuration.get());
            default -> MobBuffUtil.applyWitherToPlayer (player, cfg.witherDuration.get());
        }
    }
}
