// MobEventHandler.java
package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.data.BuffMobsWorldData;
import com.khotyz.buffmobs.util.MobBuffUtil;
import com.khotyz.buffmobs.util.PassiveMobAggressionHandler;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.khotyz.buffmobs.util.DimensionUtil.getDimensionId;

public class MobEventHandler {

    private static final Map<String, Long> lastDayCheck = new HashMap<>();
    private static final RandomSource random = RandomSource.create();

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) ->
                !TraitsHandler.cancelIncomingDamage(entity, source));

        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
            if (!BuffMobsConfig.INSTANCE.enabled) return;

            TraitsHandler.onDamageApplied(entity, source, damageTaken);

            if (entity instanceof Mob damagedMob) {
                PackAggressionHandler.onMobDamaged(damagedMob, source);
                SpecialAbilityHandler.onMobDamaged(damagedMob, source);
            }

            if (entity instanceof Mob mob && MobBuffUtil.isPassiveAggressiveMob(mob)) {
                Entity attacker = source.getEntity();
                if (attacker instanceof Player player) {
                    PassiveMobAggressionHandler.onMobHurtByPlayer(mob, player);
                }
            }

            if (!BuffMobsConfig.INSTANCE.harmfulEffects.enabled) return;
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

        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            if (!BuffMobsConfig.INSTANCE.enabled) return true;
            if (entity instanceof Player player) AdaptiveDifficultyHandler.onPlayerDeath(player);
            return true;
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (!(entity instanceof Mob mob)) return;
            if (BuffMobsConfig.INSTANCE.enabled) {
                SpecialAbilityHandler.onMobDeath(mob, damageSource);
                AdaptiveDifficultyHandler.onMobDeath(mob, damageSource);
            }
            BuffMobsWorldData data = BuffMobsWorldData.get(mob.level());
            if (data != null) data.removeMobState(mob.getUUID());
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (!BuffMobsConfig.INSTANCE.enabled) return;

            ServerLevel overworld = server.getLevel(Level.OVERWORLD);
            if (overworld != null) {
                handleDayScaling(overworld);
            }
        });
    }

    private static void handleDayScaling(ServerLevel world) {
        if (!BuffMobsConfig.INSTANCE.dayScaling.enabled
                || !BuffMobsConfig.INSTANCE.dayScaling.showNotifications
                || world.players().isEmpty()) return;

        long currentDay  = MobBuffUtil.getEffectiveDays(world);
        String worldKey  = getDimensionId(world);

        Long lastChecked = lastDayCheck.get(worldKey);
        if (lastChecked == null || currentDay > lastChecked) {
            lastDayCheck.put(worldKey, currentDay);

            if (lastChecked != null && currentDay > 0) {
                boolean notify  = false;
                int scalingInt  = Math.max(1, BuffMobsConfig.INSTANCE.dayScaling.interval);

                switch (BuffMobsConfig.INSTANCE.dayScaling.notificationMode) {
                    case EVERY_DAY             -> notify = true;
                    case SCALING_INCREASE_ONLY -> notify = currentDay % scalingInt == 0;
                }

                if (notify) sendDayScalingNotification(world, currentDay);
            }
        }
    }

    private static void sendDayScalingNotification(ServerLevel world, long currentDay) {
        double mult     = MobBuffUtil.getDayMultiplier(world);
        double maxMult  = BuffMobsConfig.INSTANCE.dayScaling.maxMultiplier;
        int    interval = Math.max(1, BuffMobsConfig.INSTANCE.dayScaling.interval);
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
        BuffMobsConfig.HarmfulEffects cfg = BuffMobsConfig.INSTANCE.harmfulEffects;
        List<Integer> enabled = new ArrayList<>();
        if (cfg.poisonEnabled) enabled.add(0);
        if (cfg.slownessEnabled) enabled.add(1);
        if (cfg.witherEnabled) enabled.add(2);
        if (enabled.isEmpty()) return;

        switch (enabled.get(random.nextInt(enabled.size()))) {
            case 0 -> MobBuffUtil.applyPoisonToPlayer  (player, cfg.poisonDuration);
            case 1 -> MobBuffUtil.applySlownessToPlayer(player, cfg.slownessDuration);
            default -> MobBuffUtil.applyWitherToPlayer (player, cfg.witherDuration);
        }
    }
}
