package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.util.goals.EnhancedMeleeAttackGoal;
import com.khotyz.buffmobs.BuffMobsMod;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.ai.goal.CrossbowAttackGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.Difficulty;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class MeleeEquipmentHandler {
    private static final Map<MobEntity, WeaponState> WEAPON_STATES = new WeakHashMap<>();
    private static final Random RANDOM = new Random();
    private static final double BASE_SWITCH_DISTANCE = 6.0;

    private static class WeaponState {
        ItemStack originalWeapon;
        boolean isMelee;
        int switchCooldown;
        EnhancedMeleeAttackGoal meleeGoal;

        WeaponState(ItemStack original) {
            this.originalWeapon = original;
            this.isMelee = false;
            this.switchCooldown = 0;
            this.meleeGoal = null;
        }
    }

    public static void handleMobTick(MobEntity mob) {
        if (!BuffMobsConfig.isRangedEquipmentEnabled() || !mob.isAlive() || !isValidRangedMob(mob)) {
            return;
        }

        WeaponState state = WEAPON_STATES.computeIfAbsent(mob, k -> {
            ItemStack current = mob.getEquippedStack(EquipmentSlot.MAINHAND);
            return new WeaponState(current.isEmpty() ? ItemStack.EMPTY : current.copy());
        });

        if (state.switchCooldown > 0) {
            state.switchCooldown--;
            return;
        }

        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) {
            if (state.isMelee) {
                switchToRanged(mob, state);
            }
            return;
        }

        double distance = mob.distanceTo(target);
        double switchDistance = BuffMobsConfig.getRangedEquipmentDistance();

        if (!state.isMelee && distance <= switchDistance) {
            switchToMelee(mob, state);
        } else if (state.isMelee && distance > switchDistance * 1.5) {
            switchToRanged(mob, state);
        }
    }

    private static void switchToMelee(MobEntity mob, WeaponState state) {
        try {
            ItemStack meleeWeapon = createMeleeWeapon(mob);
            mob.equipStack(EquipmentSlot.MAINHAND, meleeWeapon);
            mob.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0f);

            GoalSelectorHelper.removeAttackGoals(mob);

            state.meleeGoal = new EnhancedMeleeAttackGoal(mob, 1.2, false);
            GoalSelectorHelper.addGoal(mob, 2, state.meleeGoal);

            state.isMelee = true;
            state.switchCooldown = 40;

            BuffMobsMod.LOGGER.debug("Switched {} to melee weapon: {}",
                    mob.getType().getUntranslatedName(), meleeWeapon.getItem());
        } catch (Exception e) {
            BuffMobsMod.LOGGER.error("Error switching to melee", e);
        }
    }

    private static void switchToRanged(MobEntity mob, WeaponState state) {
        try {
            ItemStack rangedWeapon;
            if (state.originalWeapon != null && !state.originalWeapon.isEmpty()) {
                rangedWeapon = state.originalWeapon.copy();
            } else {
                rangedWeapon = getDefaultRangedWeapon(mob);
            }

            mob.equipStack(EquipmentSlot.MAINHAND, rangedWeapon);
            mob.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.085f);

            if (state.meleeGoal != null) {
                GoalSelectorHelper.removeGoal(mob, state.meleeGoal);
                state.meleeGoal = null;
            }

            addDefaultRangedGoals(mob);

            state.isMelee = false;
            state.switchCooldown = 40;

            BuffMobsMod.LOGGER.debug("Switched {} back to ranged weapon: {}",
                    mob.getType().getUntranslatedName(), rangedWeapon.getItem());
        } catch (Exception e) {
            BuffMobsMod.LOGGER.error("Error switching to ranged", e);
        }
    }

    private static void addDefaultRangedGoals(MobEntity mob) {
        if (mob instanceof AbstractSkeletonEntity skeleton) {
            GoalSelectorHelper.addGoal(mob, 4, new BowAttackGoal<>(skeleton, 1.0, 20, 15.0f));
        } else if (mob instanceof PillagerEntity pillager) {
            GoalSelectorHelper.addGoal(mob, 2, new CrossbowAttackGoal<>(pillager, 1.0, 8.0f));
        } else if (mob instanceof PiglinEntity piglin) {
            GoalSelectorHelper.addGoal(mob, 3, new CrossbowAttackGoal<>(piglin, 1.0, 8.0f));
        }
    }

    private static ItemStack getDefaultRangedWeapon(MobEntity mob) {
        if (mob instanceof AbstractSkeletonEntity) {
            return new ItemStack(Items.BOW);
        } else if (mob instanceof PillagerEntity) {
            return new ItemStack(Items.CROSSBOW);
        } else if (mob instanceof PiglinEntity) {
            return new ItemStack(Items.CROSSBOW);
        }
        return new ItemStack(Items.BOW);
    }

    private static ItemStack createMeleeWeapon(MobEntity mob) {
        Difficulty difficulty = mob.getWorld().getDifficulty();
        String dimension = mob.getWorld().getRegistryKey().getValue().toString();

        double baseTierChance = switch (difficulty) {
            case PEACEFUL -> 0.0;
            case EASY -> 0.15;
            case NORMAL -> 0.25;
            case HARD -> 0.4;
        };

        if (MobBuffUtil.isDayScalingEnabled()) {
            baseTierChance *= MobBuffUtil.getDayMultiplier(mob.getWorld().getTimeOfDay());
        }

        ItemStack weapon;
        double roll = RANDOM.nextDouble();

        if (dimension.contains("nether")) {
            if (roll < baseTierChance * BuffMobsConfig.getRangedNetheriteChance()) {
                weapon = new ItemStack(Items.NETHERITE_SWORD);
            } else if (roll < baseTierChance * 0.1) {
                weapon = new ItemStack(Items.GOLDEN_SWORD);
            } else {
                weapon = new ItemStack(Items.IRON_SWORD);
            }
        } else {
            double netheriteChance = baseTierChance * BuffMobsConfig.getRangedNetheriteChance();
            double diamondChance = baseTierChance * BuffMobsConfig.getRangedDiamondChance();
            double ironChance = baseTierChance * BuffMobsConfig.getRangedIronChance();

            if (roll < netheriteChance) {
                weapon = new ItemStack(Items.NETHERITE_SWORD);
            } else if (roll < diamondChance) {
                weapon = new ItemStack(Items.DIAMOND_SWORD);
            } else if (roll < ironChance) {
                weapon = new ItemStack(Items.IRON_SWORD);
            } else {
                weapon = new ItemStack(Items.STONE_SWORD);
            }
        }

        if (BuffMobsConfig.isRangedEnchantmentsEnabled() &&
                RANDOM.nextDouble() < BuffMobsConfig.getRangedEnchantmentChance()) {
            addEnchantments(weapon, mob);
        }

        return weapon;
    }

    private static void addEnchantments(ItemStack weapon, MobEntity mob) {
        try {
            var enchantRegistry = mob.getWorld().getRegistryManager()
                    .get(net.minecraft.registry.RegistryKeys.ENCHANTMENT);

            List<RegistryEntry<Enchantment>> enchants = new ArrayList<>();
            enchantRegistry.getEntry(Enchantments.SHARPNESS).ifPresent(enchants::add);
            enchantRegistry.getEntry(Enchantments.FIRE_ASPECT).ifPresent(enchants::add);
            enchantRegistry.getEntry(Enchantments.KNOCKBACK).ifPresent(enchants::add);

            if (!enchants.isEmpty()) {
                RegistryEntry<Enchantment> chosen = enchants.get(RANDOM.nextInt(enchants.size()));
                int maxLevel = Math.min(2, chosen.value().getMaxLevel());
                int level = 1 + RANDOM.nextInt(maxLevel);
                weapon.addEnchantment(chosen, level);
            }
        } catch (Exception e) {
            BuffMobsMod.LOGGER.warn("Failed to add enchantments", e);
        }
    }

    private static boolean isValidRangedMob(MobEntity mob) {
        return mob instanceof AbstractSkeletonEntity ||
                mob instanceof PillagerEntity ||
                mob instanceof PiglinEntity ||
                mob instanceof WitherSkeletonEntity ||
                mob instanceof StrayEntity;
    }

    public static void cleanup(MobEntity mob) {
        WeaponState state = WEAPON_STATES.remove(mob);
        if (state != null && state.meleeGoal != null) {
            GoalSelectorHelper.removeGoal(mob, state.meleeGoal);
        }
    }

    public static boolean isSwitchedToMelee(MobEntity mob) {
        WeaponState state = WEAPON_STATES.get(mob);
        return state != null && state.isMelee;
    }

    public static double getSwitchDistance() {
        return Math.min(BASE_SWITCH_DISTANCE, BuffMobsConfig.getRangedEquipmentDistance());
    }

    public static double getRevertDistance() {
        return Math.min(BASE_SWITCH_DISTANCE, BuffMobsConfig.getRangedEquipmentDistance()) * 1.8;
    }
}