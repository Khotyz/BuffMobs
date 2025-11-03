package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Field;
import java.util.*;

public class RangedMobAIManager {
    private static final Map<UUID, MobState> MOB_STATES = new HashMap<>();
    private static final double MELEE_SPEED_MULTIPLIER = 1.3;
    private static final int SWITCH_COOLDOWN = 40;
    private static final double MELEE_ATTACK_RANGE = 3.0;
    private static Field goalSelectorField;
    private static Field goalsField;

    static {
        try {
            goalSelectorField = Mob.class.getDeclaredField("goalSelector");
            goalSelectorField.setAccessible(true);

            goalsField = GoalSelector.class.getDeclaredField("availableGoals");
            goalsField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            BuffMobsMod.LOGGER.error("Failed to access goalSelector field", e);
        }
    }

    public static void initializeMob(Mob mob) {
        if (!BuffMobsConfig.RangedMeleeSwitching.enabled.get()) return;
        if (!isRangedMob(mob)) return;
        if (MOB_STATES.containsKey(mob.getUUID())) return;

        MobState state = new MobState();
        state.originalWeapon = mob.getItemInHand(InteractionHand.MAIN_HAND).copy();
        MOB_STATES.put(mob.getUUID(), state);

        disableRangedGoals(mob);
    }

    public static void updateMobBehavior(Mob mob) {
        if (!BuffMobsConfig.RangedMeleeSwitching.enabled.get()) return;
        if (!isRangedMob(mob)) return;

        MobState state = MOB_STATES.get(mob.getUUID());
        if (state == null) {
            initializeMob(mob);
            return;
        }

        Player target = mob.level().getNearestPlayer(mob, 32.0);
        if (target == null || target.isSpectator() || target.isCreative() || !target.isAlive()) {
            if (state.inMeleeMode) {
                switchToRangedMode(mob, state);
            }
            return;
        }

        double distance = mob.distanceTo(target);
        double switchDistance = BuffMobsConfig.RangedMeleeSwitching.switchDistance.get();
        long currentTime = mob.level().getGameTime();

        if (currentTime - state.lastSwitchTime < SWITCH_COOLDOWN) {
            if (state.inMeleeMode) {
                handleMeleeAttack(mob, target, state);
            }
            return;
        }

        if (!state.inMeleeMode && distance <= switchDistance) {
            switchToMeleeMode(mob, state);
        } else if (state.inMeleeMode && distance > switchDistance + 2.0) {
            switchToRangedMode(mob, state);
        } else if (state.inMeleeMode) {
            handleMeleeAttack(mob, target, state);
        }
    }

    private static void handleMeleeAttack(Mob mob, Player target, MobState state) {
        if (mob.getTarget() != target) {
            mob.setTarget(target);
        }

        double distance = mob.distanceTo(target);

        if (distance > MELEE_ATTACK_RANGE) {
            mob.getNavigation().moveTo(target, MELEE_SPEED_MULTIPLIER);
        } else {
            mob.getNavigation().stop();

            Vec3 targetPos = new Vec3(target.getX(), target.getEyeY(), target.getZ());
            mob.lookAt(EntityAnchorArgument.Anchor.EYES, targetPos);
            mob.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ());

            long currentTime = mob.level().getGameTime();
            if (currentTime - state.lastAttackTime >= 20) {
                performMeleeAttack(mob, target);
                state.lastAttackTime = currentTime;
            }
        }
    }

    private static void performMeleeAttack(Mob mob, LivingEntity target) {
        if (mob.level() instanceof ServerLevel serverWorld) {
            Vec3 mobPos = new Vec3(mob.getX(), mob.getY(), mob.getZ());
            Vec3 targetPos = new Vec3(target.getX(), target.getY(), target.getZ());
            Vec3 vec3d = targetPos.subtract(mobPos).normalize();

            mob.setDeltaMovement(vec3d.x * 0.1, 0.1, vec3d.z * 0.1);
            mob.hurtMarked = true;

            mob.doHurtTarget(serverWorld, target);
            mob.swing(InteractionHand.MAIN_HAND, true);

            BuffMobsMod.LOGGER.debug("{} performed melee attack on {}",
                    mob.getType(), target.getName().getString());
        }
    }

    private static void switchToMeleeMode(Mob mob, MobState state) {
        ItemStack meleeWeapon = MeleeWeaponManager.generateMeleeWeapon(mob);

        mob.setItemSlot(EquipmentSlot.MAINHAND, meleeWeapon);
        mob.setItemInHand(InteractionHand.MAIN_HAND, meleeWeapon);

        disableRangedGoals(mob);

        if (state.meleeGoal == null) {
            if (mob instanceof PathfinderMob pathAware) {
                state.meleeGoal = new MeleeAttackGoal(pathAware, MELEE_SPEED_MULTIPLIER, false);
                addGoal(mob, 0, state.meleeGoal);
            } else {
                state.meleeGoal = new CustomMeleeGoal(mob);
                addGoal(mob, 0, state.meleeGoal);
            }
        }

        Player target = mob.level().getNearestPlayer(mob, 32.0);
        if (target != null) {
            mob.setTarget(target);
        }

        state.inMeleeMode = true;
        state.lastSwitchTime = mob.level().getGameTime();
        state.lastAttackTime = 0;

        BuffMobsMod.LOGGER.info("Switched {} to MELEE mode", mob.getType());
    }

    private static void switchToRangedMode(Mob mob, MobState state) {
        ItemStack rangedWeapon = state.originalWeapon.isEmpty() ?
                getDefaultRangedWeapon(mob) : state.originalWeapon.copy();

        mob.setItemSlot(EquipmentSlot.MAINHAND, rangedWeapon);
        mob.setItemInHand(InteractionHand.MAIN_HAND, rangedWeapon);

        if (state.meleeGoal != null) {
            removeGoal(mob, state.meleeGoal);
            state.meleeGoal = null;
        }

        enableRangedGoals(mob);

        state.inMeleeMode = false;
        state.lastSwitchTime = mob.level().getGameTime();

        BuffMobsMod.LOGGER.info("Switched {} to RANGED mode", mob.getType());
    }

    private static void disableRangedGoals(Mob mob) {
        try {
            GoalSelector selector = (GoalSelector) goalSelectorField.get(mob);
            @SuppressWarnings("unchecked")
            Set<WrappedGoal> goals = (Set<WrappedGoal>) goalsField.get(selector);

            for (WrappedGoal prioritizedGoal : goals) {
                Goal goal = prioritizedGoal.getGoal();
                if (goal instanceof RangedBowAttackGoal ||
                        goal instanceof RangedCrossbowAttackGoal) {
                    prioritizedGoal.stop();
                }
            }
        } catch (Exception e) {
            BuffMobsMod.LOGGER.warn("Failed to disable ranged goals", e);
        }
    }

    private static void enableRangedGoals(Mob mob) {
        try {
            GoalSelector selector = (GoalSelector) goalSelectorField.get(mob);
            selector.tick();
        } catch (Exception e) {
            BuffMobsMod.LOGGER.warn("Failed to enable ranged goals", e);
        }
    }

    private static void addGoal(Mob mob, int priority, Goal goal) {
        try {
            GoalSelector selector = (GoalSelector) goalSelectorField.get(mob);
            selector.addGoal(priority, goal);
            BuffMobsMod.LOGGER.debug("Added melee goal to {}", mob.getType());
        } catch (Exception e) {
            BuffMobsMod.LOGGER.warn("Failed to add goal to mob", e);
        }
    }

    private static void removeGoal(Mob mob, Goal goal) {
        try {
            GoalSelector selector = (GoalSelector) goalSelectorField.get(mob);
            selector.removeGoal(goal);
            BuffMobsMod.LOGGER.debug("Removed melee goal from {}", mob.getType());
        } catch (Exception e) {
            BuffMobsMod.LOGGER.warn("Failed to remove goal from mob", e);
        }
    }

    private static ItemStack getDefaultRangedWeapon(Mob mob) {
        if (mob instanceof AbstractSkeleton) {
            return new ItemStack(Items.BOW);
        } else if (mob instanceof Piglin || mob instanceof Pillager) {
            return new ItemStack(Items.CROSSBOW);
        }
        return ItemStack.EMPTY;
    }

    public static boolean isRangedMob(Mob mob) {
        String mobId = mob.getType().toString();

        if (BuffMobsConfig.RangedMeleeSwitching.customRangedMobs.get().contains(mobId)) {
            return true;
        }

        return mob instanceof AbstractSkeleton ||
                mob instanceof Piglin ||
                mob instanceof Pillager;
    }

    public static boolean isInMeleeMode(Mob mob) {
        MobState state = MOB_STATES.get(mob.getUUID());
        return state != null && state.inMeleeMode;
    }

    public static void cleanup(Mob mob) {
        MOB_STATES.remove(mob.getUUID());
    }

    private static class MobState {
        boolean inMeleeMode = false;
        boolean inRetreatMode = false;
        RangedBehaviorMode behaviorMode = RangedBehaviorMode.MELEE;
        ItemStack originalWeapon = ItemStack.EMPTY;
        long lastSwitchTime = 0;
        long lastAttackTime = 0;
        Goal meleeGoal = null;
        TacticalRetreatGoal retreatGoal = null;
    }

    private static class CustomMeleeGoal extends Goal {
        private final Mob mob;

        public CustomMeleeGoal(Mob mob) {
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            LivingEntity target = mob.getTarget();
            return target != null && target.isAlive() &&
                    mob.distanceTo(target) <= MELEE_ATTACK_RANGE + 5.0;
        }

        @Override
        public boolean canContinueToUse() {
            return canUse();
        }

        @Override
        public void tick() {
            LivingEntity target = mob.getTarget();
            if (target != null && mob.level() instanceof ServerLevel serverWorld) {
                mob.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ());

                double distance = mob.distanceTo(target);
                if (distance > MELEE_ATTACK_RANGE) {
                    mob.getNavigation().moveTo(target, MELEE_SPEED_MULTIPLIER);
                } else {
                    mob.getNavigation().stop();
                    if (mob.tickCount % 20 == 0) {
                        mob.doHurtTarget(serverWorld, target);
                        mob.swing(InteractionHand.MAIN_HAND, true);
                    }
                }
            }
        }
    }
}