package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RangedMobAIManager {
    private static final Map<UUID, MobState> MOB_STATES = new HashMap<>();
    private static final double MELEE_SPEED_MULTIPLIER = 1.3;
    private static final int SWITCH_COOLDOWN = 40;
    private static final double MELEE_ATTACK_RANGE = 3.0;
    private static Field goalSelectorField;
    private static Field goalsField;

    static {
        try {
            goalSelectorField = MobEntity.class.getDeclaredField("goalSelector");
            goalSelectorField.setAccessible(true);

            goalsField = GoalSelector.class.getDeclaredField("goals");
            goalsField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            BuffMobsMod.LOGGER.error("Failed to access goalSelector field", e);
        }
    }

    public static void initializeMob(MobEntity mob) {
        if (!BuffMobsMod.CONFIG.rangedMeleeSwitching.enabled) return;
        if (!isRangedMob(mob)) return;
        if (MOB_STATES.containsKey(mob.getUuid())) return;

        MobState state = new MobState();
        state.originalWeapon = mob.getStackInHand(Hand.MAIN_HAND).copy();
        MOB_STATES.put(mob.getUuid(), state);

        disableRangedGoals(mob);
    }

    public static void updateMobBehavior(MobEntity mob) {
        if (!BuffMobsMod.CONFIG.rangedMeleeSwitching.enabled) return;
        if (!isRangedMob(mob)) return;

        MobState state = MOB_STATES.get(mob.getUuid());
        if (state == null) {
            initializeMob(mob);
            return;
        }

        PlayerEntity target = mob.getWorld().getClosestPlayer(mob, 32.0);
        if (target == null || target.isSpectator() || target.isCreative() || !target.isAlive()) {
            if (state.inMeleeMode) {
                switchToRangedMode(mob, state);
            }
            return;
        }

        double distance = mob.distanceTo(target);
        double switchDistance = BuffMobsMod.CONFIG.rangedMeleeSwitching.switchDistance;
        long currentTime = mob.getWorld().getTime();

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

    private static void handleMeleeAttack(MobEntity mob, PlayerEntity target, MobState state) {
        if (mob.getTarget() != target) {
            mob.setTarget(target);
        }

        double distance = mob.distanceTo(target);

        if (distance > MELEE_ATTACK_RANGE) {
            mob.getNavigation().startMovingTo(target, MELEE_SPEED_MULTIPLIER);
        } else {
            mob.getNavigation().stop();

            mob.lookAt(net.minecraft.command.argument.EntityAnchorArgumentType.EntityAnchor.EYES, target.getPos());
            mob.getLookControl().lookAt(target.getX(), target.getEyeY(), target.getZ());

            long currentTime = mob.getWorld().getTime();
            if (currentTime - state.lastAttackTime >= 20) {
                performMeleeAttack(mob, target);
                state.lastAttackTime = currentTime;
            }
        }
    }

    private static void performMeleeAttack(MobEntity mob, LivingEntity target) {
        if (!mob.getWorld().isClient && mob.getWorld() instanceof net.minecraft.server.world.ServerWorld serverWorld) {
            Vec3d vec3d = target.getPos().subtract(mob.getPos()).normalize();
            mob.setVelocity(vec3d.x * 0.1, 0.1, vec3d.z * 0.1);
            mob.velocityModified = true;

            mob.tryAttack(serverWorld, target);
            mob.swingHand(Hand.MAIN_HAND, true);

            BuffMobsMod.LOGGER.debug("{} performed melee attack on {}",
                    mob.getType(), target.getName().getString());
        }
    }

    private static void switchToMeleeMode(MobEntity mob, MobState state) {
        ItemStack meleeWeapon = MeleeWeaponManager.generateMeleeWeapon(mob);

        mob.equipStack(EquipmentSlot.MAINHAND, meleeWeapon);
        mob.setStackInHand(Hand.MAIN_HAND, meleeWeapon);

        disableRangedGoals(mob);

        if (state.meleeGoal == null) {
            if (mob instanceof PathAwareEntity pathAware) {
                state.meleeGoal = new MeleeAttackGoal(pathAware, MELEE_SPEED_MULTIPLIER, false);
                addGoal(mob, 0, state.meleeGoal);
            } else {
                state.meleeGoal = new CustomMeleeGoal(mob);
                addGoal(mob, 0, state.meleeGoal);
            }
        }

        PlayerEntity target = mob.getWorld().getClosestPlayer(mob, 32.0);
        if (target != null) {
            mob.setTarget(target);
        }

        state.inMeleeMode = true;
        state.lastSwitchTime = mob.getWorld().getTime();
        state.lastAttackTime = 0;

        BuffMobsMod.LOGGER.info("Switched {} to MELEE mode", mob.getType());
    }

    private static void switchToRangedMode(MobEntity mob, MobState state) {
        ItemStack rangedWeapon = state.originalWeapon.isEmpty() ?
                getDefaultRangedWeapon(mob) : state.originalWeapon.copy();

        mob.equipStack(EquipmentSlot.MAINHAND, rangedWeapon);
        mob.setStackInHand(Hand.MAIN_HAND, rangedWeapon);

        if (state.meleeGoal != null) {
            removeGoal(mob, state.meleeGoal);
            state.meleeGoal = null;
        }

        enableRangedGoals(mob);

        state.inMeleeMode = false;
        state.lastSwitchTime = mob.getWorld().getTime();

        BuffMobsMod.LOGGER.info("Switched {} to RANGED mode", mob.getType());
    }

    private static void disableRangedGoals(MobEntity mob) {
        try {
            GoalSelector selector = (GoalSelector) goalSelectorField.get(mob);
            @SuppressWarnings("unchecked")
            Set<PrioritizedGoal> goals = (Set<PrioritizedGoal>) goalsField.get(selector);

            for (PrioritizedGoal prioritizedGoal : goals) {
                Goal goal = prioritizedGoal.getGoal();
                if (goal instanceof BowAttackGoal ||
                        goal instanceof CrossbowAttackGoal ||
                        goal instanceof ProjectileAttackGoal) {
                    prioritizedGoal.stop();
                }
            }
        } catch (Exception e) {
            BuffMobsMod.LOGGER.warn("Failed to disable ranged goals", e);
        }
    }

    private static void enableRangedGoals(MobEntity mob) {
        try {
            GoalSelector selector = (GoalSelector) goalSelectorField.get(mob);
            selector.tick();
        } catch (Exception e) {
            BuffMobsMod.LOGGER.warn("Failed to enable ranged goals", e);
        }
    }

    private static void addGoal(MobEntity mob, int priority, Goal goal) {
        try {
            GoalSelector selector = (GoalSelector) goalSelectorField.get(mob);
            selector.add(priority, goal);
            BuffMobsMod.LOGGER.debug("Added melee goal to {}", mob.getType());
        } catch (Exception e) {
            BuffMobsMod.LOGGER.warn("Failed to add goal to mob", e);
        }
    }

    private static void removeGoal(MobEntity mob, Goal goal) {
        try {
            GoalSelector selector = (GoalSelector) goalSelectorField.get(mob);
            selector.remove(goal);
            BuffMobsMod.LOGGER.debug("Removed melee goal from {}", mob.getType());
        } catch (Exception e) {
            BuffMobsMod.LOGGER.warn("Failed to remove goal from mob", e);
        }
    }

    private static ItemStack getDefaultRangedWeapon(MobEntity mob) {
        if (mob instanceof SkeletonEntity || mob instanceof StrayEntity ||
                mob instanceof WitherSkeletonEntity) {
            return new ItemStack(Items.BOW);
        } else if (mob instanceof PiglinEntity || mob instanceof PillagerEntity) {
            return new ItemStack(Items.CROSSBOW);
        }
        return ItemStack.EMPTY;
    }

    public static boolean isRangedMob(MobEntity mob) {
        String mobId = Registries.ENTITY_TYPE.getId(mob.getType()).toString();

        if (BuffMobsMod.CONFIG.rangedMeleeSwitching.customRangedMobs.contains(mobId)) {
            return true;
        }

        return mob instanceof SkeletonEntity ||
                mob instanceof StrayEntity ||
                mob instanceof WitherSkeletonEntity ||
                mob instanceof PiglinEntity ||
                mob instanceof PillagerEntity;
    }

    public static boolean isInMeleeMode(MobEntity mob) {
        MobState state = MOB_STATES.get(mob.getUuid());
        return state != null && state.inMeleeMode;
    }

    public static void cleanup(MobEntity mob) {
        MOB_STATES.remove(mob.getUuid());
    }

    private static class MobState {
        boolean inMeleeMode = false;
        ItemStack originalWeapon = ItemStack.EMPTY;
        long lastSwitchTime = 0;
        long lastAttackTime = 0;
        Goal meleeGoal = null;
    }

    private static class CustomMeleeGoal extends Goal {
        private final MobEntity mob;

        public CustomMeleeGoal(MobEntity mob) {
            this.mob = mob;
        }

        @Override
        public boolean canStart() {
            LivingEntity target = mob.getTarget();
            return target != null && target.isAlive() &&
                    mob.distanceTo(target) <= MELEE_ATTACK_RANGE + 5.0;
        }

        @Override
        public boolean shouldContinue() {
            return canStart();
        }

        @Override
        public void tick() {
            LivingEntity target = mob.getTarget();
            if (target != null && mob.getWorld() instanceof net.minecraft.server.world.ServerWorld serverWorld) {
                mob.getLookControl().lookAt(target.getX(), target.getEyeY(), target.getZ());

                double distance = mob.distanceTo(target);
                if (distance > MELEE_ATTACK_RANGE) {
                    mob.getNavigation().startMovingTo(target, MELEE_SPEED_MULTIPLIER);
                } else {
                    mob.getNavigation().stop();
                    if (mob.age % 20 == 0) {
                        mob.tryAttack(serverWorld, target);
                        mob.swingHand(Hand.MAIN_HAND, true);
                    }
                }
            }
        }
    }
}