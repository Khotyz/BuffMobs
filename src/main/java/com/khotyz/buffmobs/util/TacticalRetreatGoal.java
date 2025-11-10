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
    private static final int COOLDOWN_TIME = 20;

    public TacticalRetreatGoal(Mob mob, double retreatDistance, double retreatSpeed, int retreatDuration) {
        this.mob = mob;
        this.retreatDistance = retreatDistance;
        this.retreatSpeed = retreatSpeed;
        this.retreatDuration = retreatDuration;
        this.retreatTimer = 0;
        this.cooldownTimer = 0;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
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

        double distanceToTarget = mob.distanceTo(target);

        if (distanceToTarget > retreatDistance) {
            return false;
        }

        Vec3 retreatPos = findRetreatPosition();
        if (retreatPos == null) {
            return false;
        }

        PathNavigation navigation = mob.getNavigation();
        this.retreatPath = navigation.createPath(retreatPos.x, retreatPos.y, retreatPos.z, 0);

        return this.retreatPath != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (target == null || !target.isAlive()) {
            return false;
        }

        if (retreatTimer >= retreatDuration) {
            return false;
        }

        double distanceToTarget = mob.distanceTo(target);
        return distanceToTarget < (retreatDistance + 3.0);
    }

    @Override
    public void start() {
        if (retreatPath != null) {
            mob.getNavigation().moveTo(retreatPath, retreatSpeed);
        }
        retreatTimer = 0;

        com.khotyz.buffmobs.BuffMobsMod.LOGGER.debug("{} starting tactical retreat from {}",
                mob.getType(), target != null ? target.getName().getString() : "unknown");
    }

    @Override
    public void stop() {
        retreatTimer = 0;
        cooldownTimer = COOLDOWN_TIME;
        target = null;
        retreatPath = null;
    }

    @Override
    public void tick() {
        retreatTimer++;

        if (target != null) {
            mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (mob.getNavigation().isDone() || retreatTimer % 3 == 0) {
                Vec3 retreatPos = findRetreatPosition();
                if (retreatPos != null) {
                    mob.getNavigation().moveTo(retreatPos.x, retreatPos.y, retreatPos.z, retreatSpeed);
                }
            }

            double distanceToTarget = mob.distanceTo(target);
            if (distanceToTarget < 2.0) {
                Vec3 awayVector = mob.position().subtract(target.position()).normalize().scale(0.3);
                mob.setDeltaMovement(awayVector.x, mob.getDeltaMovement().y, awayVector.z);
            }
        }
    }

    private Vec3 findRetreatPosition() {
        if (target == null) {
            return null;
        }

        Vec3 mobPos = mob.position();
        Vec3 targetPos = target.position();
        Vec3 awayFromTarget = mobPos.subtract(targetPos).normalize();

        if (mob instanceof PathfinderMob pathfinderMob) {
            for (int distance = 12; distance >= 6; distance -= 2) {
                Vec3 validPos = DefaultRandomPos.getPosTowards(
                        pathfinderMob,
                        10,
                        6,
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
            }

            Vec3 fallback = DefaultRandomPos.getPosAway(pathfinderMob, 10, 6, targetPos);
            if (fallback != null) {
                return fallback;
            }
        }

        return mobPos.add(awayFromTarget.scale(10));
    }

    public void resetCooldown() {
        this.cooldownTimer = 0;
    }
}