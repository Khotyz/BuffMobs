package com.khotyz.buffmobs.events;

import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.util.MobBuffUtil;
import com.khotyz.buffmobs.util.FilterUtil;
import com.khotyz.buffmobs.util.DimensionScalingUtil;
import com.khotyz.buffmobs.util.RegenHandler;
import com.khotyz.buffmobs.util.MeleeEquipmentHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.MinecraftServer;
import java.util.WeakHashMap;
import java.util.Map;

public class TickHandler {
    private static final Map<MobEntity, AttackData> ATTACK_TRACKERS = new WeakHashMap<>();

    private static class AttackData {
        int cooldown = 0;
        long lastAttackTime = 0;
    }

    public void onServerTick(MinecraftServer server) {
        if (!BuffMobsConfig.isEnabled()) {
            return;
        }

        server.getWorlds().forEach(world -> {
            if (!world.isClient) {
                world.iterateEntities().forEach(entity -> {
                    if (entity instanceof MobEntity mob && mob.isAlive()) {
                        // Handle custom regeneration for all buffed mobs
                        if (FilterUtil.isValidMob(mob)) {
                            RegenHandler.handleRegenTick(mob);
                        }

                        // Handle melee weapon switching for ALL ranged mobs (not just buffed ones)
                        MeleeEquipmentHandler.handleMobTick(mob);

                        // Handle attack speed override for buffed mobs only
                        if (FilterUtil.isValidMob(mob) &&
                                BuffMobsConfig.shouldOverrideAttackTimers() &&
                                !MeleeEquipmentHandler.isSwitchedToMelee(mob)) {
                            handleAttackSpeedOverride(mob);
                        }
                    }
                });
            }
        });
    }

    private void handleAttackSpeedOverride(MobEntity mob) {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) {
            ATTACK_TRACKERS.remove(mob);
            return;
        }

        double attackSpeedMultiplier = DimensionScalingUtil.getEffectiveAttackSpeedMultiplier(mob);
        if (attackSpeedMultiplier <= 1.0) {
            ATTACK_TRACKERS.remove(mob);
            return;
        }

        if (MobBuffUtil.isDayScalingEnabled()) {
            attackSpeedMultiplier *= MobBuffUtil.getDayMultiplier(mob.getWorld().getTimeOfDay());
        }

        AttackData data = ATTACK_TRACKERS.computeIfAbsent(mob, k -> new AttackData());
        long currentTime = mob.getWorld().getTime();

        int baseInterval = 20;
        int attackInterval = Math.max(3, (int)(baseInterval / attackSpeedMultiplier));

        if (data.cooldown > 0) {
            data.cooldown--;
        }

        boolean shouldTryAttack = data.cooldown <= 0 &&
                (currentTime - data.lastAttackTime >= attackInterval) &&
                canMobReachTarget(mob, target);

        if (shouldTryAttack && attemptVanillaAttack(mob, target)) {
            data.lastAttackTime = currentTime;
            data.cooldown = attackInterval;
        }
    }

    private boolean canMobReachTarget(MobEntity mob, LivingEntity target) {
        double reachDistance = 4.0 + mob.getBoundingBox().getLengthX() * mob.getBoundingBox().getLengthX();
        double distanceToTarget = mob.squaredDistanceTo(target);
        return distanceToTarget <= reachDistance;
    }

    private boolean attemptVanillaAttack(MobEntity mob, LivingEntity target) {
        try {
            if (mob.getVisibilityCache().canSee(target) && canMobReachTarget(mob, target)) {
                return mob.tryAttack(target);
            }
        } catch (Exception e) {
            // Silent fallback
        }
        return false;
    }
}