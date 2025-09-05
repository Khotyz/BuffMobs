package com.khotyz.buffmobs.util.goals;

import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.util.DimensionScalingUtil;
import com.khotyz.buffmobs.util.MobBuffUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Hand;

import java.util.EnumSet;

public class EnhancedMeleeAttackGoal extends Goal {
    private final MobEntity mob;
    private final double speed;
    private final boolean pauseWhenMobIdle;
    private LivingEntity target;
    private int ticksUntilNextAttack;
    private final int maxAttackCooldown = 20;
    private int pathUpdateCountdown;

    public EnhancedMeleeAttackGoal(MobEntity mob, double speed, boolean pauseWhenMobIdle) {
        this.mob = mob;
        this.speed = speed;
        this.pauseWhenMobIdle = pauseWhenMobIdle;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        this.target = target;
        return true;
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }

        if (this.pauseWhenMobIdle) {
            return !this.mob.getNavigation().isIdle() || this.isInAttackRange();
        }

        return this.mob.squaredDistanceTo(target) <= 256.0; // 16 block follow range
    }

    @Override
    public void start() {
        this.mob.getNavigation().startMovingTo(this.target, this.speed);
        this.mob.setAttacking(true);
        this.ticksUntilNextAttack = 0;
        this.pathUpdateCountdown = 0;
    }

    @Override
    public void stop() {
        this.mob.setAttacking(false);
        this.mob.getNavigation().stop();
        this.target = null;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) {
            return;
        }

        this.mob.getLookControl().lookAt(target, 30.0f, 30.0f);

        double distanceToTarget = this.mob.squaredDistanceTo(target);
        boolean inAttackRange = this.isInAttackRange();

        --this.pathUpdateCountdown;
        if (this.pathUpdateCountdown <= 0) {
            this.pathUpdateCountdown = 4 + this.mob.getRandom().nextInt(7);
            if (!inAttackRange) {
                this.mob.getNavigation().startMovingTo(target, this.speed);
            }
        }

        --this.ticksUntilNextAttack;

        if (inAttackRange && this.ticksUntilNextAttack <= 0) {
            this.resetAttackCooldown();
            this.mob.swingHand(Hand.MAIN_HAND);
            this.mob.tryAttack(target);
        }
    }

    private void resetAttackCooldown() {
        double attackSpeedMultiplier = 1.0;

        try {
            attackSpeedMultiplier = DimensionScalingUtil.getEffectiveAttackSpeedMultiplier(this.mob);
            if (MobBuffUtil.isDayScalingEnabled()) {
                attackSpeedMultiplier *= MobBuffUtil.getDayMultiplier(this.mob.getWorld().getTimeOfDay());
            }
        } catch (Exception e) {
            // Fallback to default
        }

        int cooldown = Math.max(3, (int) (this.maxAttackCooldown / attackSpeedMultiplier));
        this.ticksUntilNextAttack = cooldown;
    }

    private boolean isInAttackRange() {
        if (this.target == null) return false;

        double reachSq = this.getSquaredMaxAttackDistance();
        double distanceSq = this.mob.squaredDistanceTo(this.target);

        return distanceSq <= reachSq;
    }

    private double getSquaredMaxAttackDistance() {
        float mobWidth = this.mob.getWidth();
        float targetWidth = this.target != null ? this.target.getWidth() : 1.0f;
        double reach = mobWidth * 2.0f + targetWidth + 0.5;
        return reach * reach;
    }
}