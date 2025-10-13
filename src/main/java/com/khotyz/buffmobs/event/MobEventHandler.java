package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.util.MobBuffUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.Map;
import java.util.WeakHashMap;

public class MobEventHandler {
    private final Map<ServerWorld, Long> lastDayCheck = new WeakHashMap<>();
    private final Map<ServerWorld, Long> lastInfiniteEffectCheck = new WeakHashMap<>();
    private final Random random = Random.create();

    public void onLivingDamage(LivingEntity entity, DamageSource source, float damageTaken) {
        if (!BuffMobsMod.CONFIG.enabled || !BuffMobsMod.CONFIG.harmfulEffects.enabled) return;

        if (entity instanceof PlayerEntity player) {
            Entity attacker = source.getAttacker();
            Entity directEntity = source.getSource();

            MobEntity attackingMob = null;

            if (attacker instanceof MobEntity mob && MobBuffUtil.isValidMob(mob)) {
                attackingMob = mob;
            } else if (directEntity instanceof ProjectileEntity projectile &&
                    projectile.getOwner() instanceof MobEntity mob &&
                    MobBuffUtil.isValidMob(mob)) {
                attackingMob = mob;
            }

            if (attackingMob != null && random.nextFloat() < BuffMobsMod.CONFIG.harmfulEffects.chance) {
                applyRandomHarmfulEffect(player);
            }
        }
    }

    public void onWorldTick(ServerWorld world) {
        if (!BuffMobsMod.CONFIG.enabled) return;

        long currentTime = world.getTime();

        handleDayScaling(world);
        handleInfiniteEffects(world, currentTime);
    }

    public ActionResult onPlayerAttack(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
        return ActionResult.PASS;
    }

    private void handleDayScaling(ServerWorld world) {
        if (!BuffMobsMod.CONFIG.dayScaling.enabled ||
                !BuffMobsMod.CONFIG.dayScaling.showNotifications ||
                world.getPlayers().isEmpty()) return;

        long currentTime = world.getTimeOfDay();
        long currentDay = currentTime / 24000L;
        Long lastCheckedDay = lastDayCheck.get(world);

        if (lastCheckedDay == null || currentDay > lastCheckedDay) {
            lastDayCheck.put(world, currentDay);

            if (lastCheckedDay != null && currentDay > 0) {
                boolean shouldNotify = false;
                int scalingInterval = BuffMobsMod.CONFIG.dayScaling.interval;

                switch (BuffMobsMod.CONFIG.dayScaling.notificationMode) {
                    case EVERY_DAY -> shouldNotify = true;
                    case SCALING_INCREASE_ONLY -> shouldNotify = currentDay % scalingInterval == 0;
                }

                if (shouldNotify) {
                    sendDayScalingNotification(world, currentDay);
                }
            }
        }
    }

    private void sendDayScalingNotification(ServerWorld world, long currentDay) {
        double currentMultiplier = MobBuffUtil.getDayMultiplier(world.getTimeOfDay());
        double maxMultiplier = BuffMobsMod.CONFIG.dayScaling.maxMultiplier;
        int scalingInterval = BuffMobsMod.CONFIG.dayScaling.interval;

        long daysUntilNextScaling = scalingInterval - (currentDay % scalingInterval);
        if (daysUntilNextScaling == scalingInterval) daysUntilNextScaling = 0;

        boolean isMaxed = currentMultiplier >= maxMultiplier;

        Text message;
        if (isMaxed) {
            message = Text.literal("Day " + currentDay + " - Mob Scaling: " +
                    String.format("%.1fx", currentMultiplier) + " (MAXIMUM)");
        } else {
            message = Text.literal("Day " + currentDay + " - Mob Scaling: " +
                    String.format("%.1fx", currentMultiplier) + " | Next increase in " +
                    daysUntilNextScaling + " day" + (daysUntilNextScaling != 1 ? "s" : ""));
        }

        world.getPlayers().forEach(player -> player.sendMessage(message, false));
    }

    private void handleInfiniteEffects(ServerWorld world, long currentTime) {
        if (BuffMobsMod.CONFIG.effects.duration != -1) return;

        Long lastCheck = lastInfiniteEffectCheck.get(world);
        if (lastCheck != null && (currentTime - lastCheck) < 600) return;

        lastInfiniteEffectCheck.put(world, currentTime);

        world.iterateEntities().forEach(entity -> {
            if (entity instanceof MobEntity mob && MobBuffUtil.isValidMob(mob)) {
                try {
                    MobBuffUtil.refreshInfiniteEffects(mob);
                } catch (Exception e) {
                    BuffMobsMod.LOGGER.warn("Failed to refresh infinite effects for mob: " + mob.getType().toString(), e);
                }
            }
        });
    }

    private void applyRandomHarmfulEffect(PlayerEntity player) {
        int effectType = random.nextInt(3);

        try {
            switch (effectType) {
                case 0 -> MobBuffUtil.applyPoisonToPlayer(player, BuffMobsMod.CONFIG.harmfulEffects.poisonDuration);
                case 1 -> MobBuffUtil.applySlownessToPlayer(player, BuffMobsMod.CONFIG.harmfulEffects.slownessDuration);
                case 2 -> MobBuffUtil.applyWitherToPlayer(player, BuffMobsMod.CONFIG.harmfulEffects.witherDuration);
            }
        } catch (Exception e) {
            BuffMobsMod.LOGGER.warn("Failed to apply harmful effect to player: " + player.getName().getString(), e);
        }
    }
}