package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;

import java.util.Map;
import java.util.WeakHashMap;

public class RangedMobAIManager {
    private static final Map<MobEntity, MeleeState> MELEE_STATES = new WeakHashMap<>();
    private static final double MELEE_SPEED_MULTIPLIER = 1.0;

    public static void initializeMob(MobEntity mob) {
        if (!BuffMobsMod.CONFIG.rangedMeleeSwitching.enabled) return;
        if (!isRangedMob(mob)) return;
        if (MELEE_STATES.containsKey(mob)) return;

        MeleeState state = new MeleeState();
        state.originalMainHand = mob.getStackInHand(Hand.MAIN_HAND).copy();
        MELEE_STATES.put(mob, state);
    }

    public static void updateMobBehavior(MobEntity mob) {
        if (!BuffMobsMod.CONFIG.rangedMeleeSwitching.enabled) return;
        if (!isRangedMob(mob)) return;

        MeleeState state = MELEE_STATES.get(mob);
        if (state == null) return;

        PlayerEntity nearestPlayer = mob.getWorld().getClosestPlayer(mob, 32.0);
        if (nearestPlayer == null || nearestPlayer.isSpectator() || nearestPlayer.isCreative()) {
            if (state.inMeleeMode) {
                switchToRangedMode(mob, state);
            }
            return;
        }

        double distance = mob.distanceTo(nearestPlayer);
        double switchDistance = BuffMobsMod.CONFIG.rangedMeleeSwitching.switchDistance;

        long currentTime = mob.getWorld().getTime();
        long timeSinceSwitch = currentTime - state.lastSwitchTime;

        if (timeSinceSwitch < 40) return;

        if (!state.inMeleeMode && distance <= switchDistance) {
            switchToMeleeMode(mob, state);
        } else if (state.inMeleeMode && distance > switchDistance + 2.0) {
            switchToRangedMode(mob, state);
        }
    }

    private static void switchToMeleeMode(MobEntity mob, MeleeState state) {
        if (state.originalMainHand.isEmpty()) {
            state.originalMainHand = mob.getStackInHand(Hand.MAIN_HAND).copy();
        }

        ItemStack meleeWeapon = MeleeWeaponManager.generateMeleeWeapon(mob);

        mob.equipStack(net.minecraft.entity.EquipmentSlot.MAINHAND, meleeWeapon);
        mob.setStackInHand(Hand.MAIN_HAND, meleeWeapon);

        if (state.meleeGoal == null && mob instanceof PathAwareEntity pathAwareMob) {
            state.meleeGoal = new MeleeAttackGoal(pathAwareMob, MELEE_SPEED_MULTIPLIER, false);
            try {
                MobEntityAccessor.addGoal(mob, 2, state.meleeGoal);
            } catch (Exception e) {
                // Silent fail
            }
        }

        state.inMeleeMode = true;
        state.lastSwitchTime = mob.getWorld().getTime();
    }

    private static void switchToRangedMode(MobEntity mob, MeleeState state) {
        ItemStack rangedWeapon;

        if (!state.originalMainHand.isEmpty()) {
            rangedWeapon = state.originalMainHand.copy();
        } else {
            rangedWeapon = getDefaultRangedWeapon(mob);
        }

        mob.equipStack(net.minecraft.entity.EquipmentSlot.MAINHAND, rangedWeapon);
        mob.setStackInHand(Hand.MAIN_HAND, rangedWeapon);

        if (state.meleeGoal != null) {
            try {
                MobEntityAccessor.removeGoal(mob, state.meleeGoal);
            } catch (Exception e) {
                // Silent fail
            }
            state.meleeGoal = null;
        }

        state.inMeleeMode = false;
        state.lastSwitchTime = mob.getWorld().getTime();
    }

    private static ItemStack getDefaultRangedWeapon(MobEntity mob) {
        if (mob instanceof SkeletonEntity || mob instanceof StrayEntity || mob instanceof WitherSkeletonEntity) {
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

        if (mob instanceof SkeletonEntity ||
                mob instanceof StrayEntity ||
                mob instanceof WitherSkeletonEntity ||
                mob instanceof PiglinEntity ||
                mob instanceof PillagerEntity) {
            return true;
        }

        try {
            for (var goal : MobEntityAccessor.getGoals(mob)) {
                var prioritizedGoal = goal.getGoal();
                if (prioritizedGoal instanceof BowAttackGoal ||
                        prioritizedGoal instanceof CrossbowAttackGoal ||
                        prioritizedGoal instanceof ProjectileAttackGoal) {
                    return true;
                }
            }
        } catch (Exception e) {
            // Silent fail
        }

        return false;
    }

    public static boolean isInMeleeMode(MobEntity mob) {
        MeleeState state = MELEE_STATES.get(mob);
        return state != null && state.inMeleeMode;
    }

    private static class MeleeState {
        boolean inMeleeMode = false;
        ItemStack originalMainHand = ItemStack.EMPTY;
        long lastSwitchTime = 0;
        Goal meleeGoal = null;
    }
}