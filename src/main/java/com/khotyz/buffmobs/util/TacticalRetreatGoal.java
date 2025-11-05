package com.khotyz.buffmobs.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class TacticalRetreatGoal extends Goal {
    private final Mob mob;
    private final double retreatDistance;
    private final double retreatSpeed;
    private final int retreatDuration;
    private LivingEntity target;
    private Path retreatPath;
    private int retreatTimer;
    private int cooldownTimer;
    private static final int COOLDOWN_TIME = 100; // 5 seconds between retreats

    public TacticalRetreatGoal(Mob mob, double retreatDistance, double retreatSpeed, int retreatDuration) {
        this.mob = mob;
        this.retreatDistance = retreatDistance;
        this.retreatSpeed = retreatSpeed;
        this.retreatDuration = retreatDuration;
        this.retreatTimer = 0;
        this.cooldownTimer = 0;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (cooldownTimer > 0) {
            cooldownTimer--;
            return false;
        }

        this.target = mob.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }

        double distanceToTarget = mob.distanceToSqr(target);
        double retreatDistanceSqr = retreatDistance * retreatDistance;

        // Only retreat if target is too close
        if (distanceToTarget > retreatDistanceSqr) {
            return false;
        }

        // Find a retreat position away from target
        Vec3 retreatPos = findRetreatPosition();
        if (retreatPos == null) {
            return false;
        }

        PathNavigation navigation = mob.getNavigation();
        this.retreatPath = navigation.createPath(retreatPos.x, retreatPos.y, retreatPos.z, 0);

        boolean canRetreat = this.retreatPath != null;

        if (canRetreat) {
            com.khotyz.buffmobs.BuffMobsMod.LOGGER.debug("{} activating retreat! Distance: {}, Target: {}",
                    mob.getType(), Math.sqrt(distanceToTarget), target.getName().getString());
        }

        return canRetreat;
    }

    @Override
    public boolean canContinueToUse() {
        if (target == null || !target.isAlive()) {
            return false;
        }

        if (retreatTimer >= retreatDuration) {
            return false;
        }

        // Stop if we've moved far enough
        double distanceToTarget = mob.distanceToSqr(target);
        double safeDistanceSqr = (retreatDistance + 3.0) * (retreatDistance + 3.0);

        return distanceToTarget < safeDistanceSqr && !mob.getNavigation().isDone();
    }

    @Override
    public void start() {
        mob.getNavigation().moveTo(retreatPath, retreatSpeed);
        retreatTimer = 0;
    }

    @Override
    public void stop() {
        retreatTimer = 0;
        cooldownTimer = COOLDOWN_TIME;
        target = null;
        retreatPath = null;
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        retreatTimer++;

        if (target != null) {
            // Keep looking at target while retreating
            mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

            // Continue moving away if path is done but still too close
            if (mob.getNavigation().isDone()) {
                Vec3 retreatPos = findRetreatPosition();
                if (retreatPos != null) {
                    mob.getNavigation().moveTo(retreatPos.x, retreatPos.y, retreatPos.z, retreatSpeed);
                }
            }
        }
    }

    private Vec3 findRetreatPosition() {
        if (target == null) {
            return null;
        }

        // Calculate direction away from target
        Vec3 mobPos = mob.position();
        Vec3 targetPos = target.position();
        Vec3 awayFromTarget = mobPos.subtract(targetPos).normalize();

        // Try to find a position 8-12 blocks away from target
        for (int distance = 12; distance >= 8; distance -= 2) {
            // Calculate potential escape position
            Vec3 potentialPos = mobPos.add(awayFromTarget.scale(distance));

            // Check if mob is PathfinderMob for better pathfinding
            if (mob instanceof PathfinderMob pathfinderMob) {
                Vec3 validPos = DefaultRandomPos.getPosTowards(
                        pathfinderMob,
                        10,
                        7,
                        Vec3.atBottomCenterOf(mob.blockPosition().offset(
                                (int) (awayFromTarget.x * distance),
                                0,
                                (int) (awayFromTarget.z * distance)
                        )),
                        Math.PI / 2
                );

                if (validPos != null) {
                    return validPos;
                }
            } else {
                // Fallback for non-PathfinderMob: direct calculation
                if (mob.getNavigation().isStableDestination(mob.blockPosition().offset(
                        (int) (awayFromTarget.x * distance),
                        0,
                        (int) (awayFromTarget.z * distance)
                ))) {
                    return potentialPos;
                }
            }
        }

        // Last resort: try DefaultRandomPos.getPosAway if PathfinderMob
        if (mob instanceof PathfinderMob pathfinderMob) {
            return DefaultRandomPos.getPosAway(pathfinderMob, 10, 7, targetPos);
        }

        // Final fallback: simple vector math
        return mobPos.add(awayFromTarget.scale(10));
    }

    public void resetCooldown() {
        this.cooldownTimer = 0;
    }
}