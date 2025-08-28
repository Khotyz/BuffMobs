package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.BuffMobsMod;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.Difficulty;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class MeleeEquipmentHandler {
    private static final Map<MobEntity, ItemStack> ORIGINAL_WEAPONS = new WeakHashMap<>();
    private static final Map<MobEntity, Boolean> SWITCHED_TO_MELEE = new WeakHashMap<>();
    private static final Random RANDOM = new Random();

    private static final double SWITCH_DISTANCE = BuffMobsConfig.getRangedEquipmentDistance() * 0.4375; // ~3.5 when default is 8.0
    private static final double REVERT_DISTANCE = BuffMobsConfig.getRangedEquipmentDistance();

    public static boolean shouldEquipMeleeWeapon(MobEntity mob) {
        if (!BuffMobsConfig.isRangedEquipmentEnabled() || !isRangedMob(mob)) {
            return false;
        }

        if (mob.getTarget() == null) {
            return false;
        }

        double distance = mob.distanceTo(mob.getTarget());
        boolean isSwitched = SWITCHED_TO_MELEE.getOrDefault(mob, false);

        if (!isSwitched && distance <= SWITCH_DISTANCE) {
            return true;
        } else if (isSwitched && distance >= REVERT_DISTANCE) {
            revertToOriginalWeapon(mob);
            return false;
        }

        return isSwitched;
    }

    public static void equipMeleeWeapon(MobEntity mob) {
        if (!shouldEquipMeleeWeapon(mob) || SWITCHED_TO_MELEE.getOrDefault(mob, false)) {
            return;
        }

        try {
            ItemStack originalWeapon = mob.getEquippedStack(EquipmentSlot.MAINHAND);
            ORIGINAL_WEAPONS.put(mob, originalWeapon.copy());

            ItemStack meleeWeapon = generateMeleeWeapon(mob);
            if (meleeWeapon != null) {
                mob.equipStack(EquipmentSlot.MAINHAND, meleeWeapon);
                mob.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0f);
                SWITCHED_TO_MELEE.put(mob, true);
            }
        } catch (Exception e) {
            BuffMobsMod.LOGGER.error("Failed to equip melee weapon", e);
        }
    }

    public static void revertToOriginalWeapon(MobEntity mob) {
        if (!SWITCHED_TO_MELEE.getOrDefault(mob, false)) {
            return;
        }

        try {
            ItemStack originalWeapon = ORIGINAL_WEAPONS.get(mob);
            if (originalWeapon != null) {
                mob.equipStack(EquipmentSlot.MAINHAND, originalWeapon);
                mob.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.085f);
            } else {
                mob.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            }

            SWITCHED_TO_MELEE.put(mob, false);
            ORIGINAL_WEAPONS.remove(mob);
        } catch (Exception e) {
            BuffMobsMod.LOGGER.error("Failed to revert to original weapon", e);
        }
    }

    private static ItemStack generateMeleeWeapon(MobEntity mob) {
        String dimensionName = mob.getWorld().getRegistryKey().getValue().toString();
        boolean isNether = dimensionName.contains("nether");

        ItemStack weapon;
        if (isNether) {
            weapon = generateNetherWeapon(mob);
        } else {
            weapon = generateOverworldWeapon(mob);
        }

        if (weapon != null && BuffMobsConfig.isRangedEnchantmentsEnabled() &&
                RANDOM.nextDouble() < getEnchantmentChance(mob)) {
            applyOffensiveEnchantments(weapon, mob);
        }

        return weapon;
    }

    private static ItemStack generateOverworldWeapon(MobEntity mob) {
        double baseTierChance = calculateTierChance(mob);

        // Apply config multipliers to base chance
        double netheriteChance = baseTierChance * BuffMobsConfig.getRangedNetheriteChance();
        double diamondChance = baseTierChance * BuffMobsConfig.getRangedDiamondChance();
        double ironChance = baseTierChance * BuffMobsConfig.getRangedIronChance();

        double roll = RANDOM.nextDouble();

        if (roll < netheriteChance) {
            return new ItemStack(Items.NETHERITE_SWORD);
        } else if (roll < diamondChance) {
            return new ItemStack(Items.DIAMOND_SWORD);
        } else if (roll < ironChance) {
            return new ItemStack(Items.IRON_SWORD);
        } else {
            return new ItemStack(Items.STONE_SWORD);
        }
    }

    private static ItemStack generateNetherWeapon(MobEntity mob) {
        double baseTierChance = calculateTierChance(mob);
        double netheriteChance = baseTierChance * BuffMobsConfig.getRangedNetheriteChance();

        if (RANDOM.nextDouble() < netheriteChance) {
            return new ItemStack(Items.NETHERITE_AXE);
        } else {
            return new ItemStack(Items.GOLDEN_AXE);
        }
    }

    private static double calculateTierChance(MobEntity mob) {
        double baseTierChance = 0.1;

        Difficulty difficulty = mob.getWorld().getDifficulty();
        double difficultyMultiplier = switch (difficulty) {
            case PEACEFUL -> 0.0;
            case EASY -> 0.5;
            case NORMAL -> 1.0;
            case HARD -> 2.0;
        };

        double dayMultiplier = 1.0;
        if (MobBuffUtil.isDayScalingEnabled()) {
            dayMultiplier = MobBuffUtil.getDayMultiplier(mob.getWorld().getTimeOfDay());
        }

        return Math.min(0.999, baseTierChance * difficultyMultiplier * dayMultiplier);
    }

    private static double getEnchantmentChance(MobEntity mob) {
        double baseChance = BuffMobsConfig.getRangedEnchantmentChance();

        Difficulty difficulty = mob.getWorld().getDifficulty();
        double difficultyMultiplier = switch (difficulty) {
            case PEACEFUL -> 0.0;
            case EASY -> 0.5;
            case NORMAL -> 1.0;
            case HARD -> 1.5;
        };

        double dayMultiplier = 1.0;
        if (MobBuffUtil.isDayScalingEnabled()) {
            dayMultiplier = Math.min(2.0, 1.0 + (MobBuffUtil.getDayMultiplier(mob.getWorld().getTimeOfDay()) - 1.0) * 0.5);
        }

        return Math.min(0.8, baseChance * difficultyMultiplier * dayMultiplier);
    }

    private static void applyOffensiveEnchantments(ItemStack weapon, MobEntity mob) {
        try {
            // Get enchantment entries from the dynamic registry with safety checks
            var registryManager = mob.getWorld().getRegistryManager();
            var enchantmentRegistry = registryManager.get(net.minecraft.registry.RegistryKeys.ENCHANTMENT);

            List<RegistryEntry<Enchantment>> possibleEnchantments = new ArrayList<>();

            // Safe enchantment loading with null checks - REMOVED BANE_OF_ARTHROPODS
            enchantmentRegistry.getEntry(Enchantments.SHARPNESS).ifPresent(possibleEnchantments::add);
            enchantmentRegistry.getEntry(Enchantments.SMITE).ifPresent(possibleEnchantments::add);
            enchantmentRegistry.getEntry(Enchantments.FIRE_ASPECT).ifPresent(possibleEnchantments::add);
            enchantmentRegistry.getEntry(Enchantments.KNOCKBACK).ifPresent(possibleEnchantments::add);

            if (possibleEnchantments.isEmpty()) {
                return; // No enchantments available
            }

            int enchantmentCount = 1 + RANDOM.nextInt(Math.min(3, possibleEnchantments.size()));

            // Create a copy to avoid ConcurrentModificationException
            List<RegistryEntry<Enchantment>> availableEnchantments = new ArrayList<>(possibleEnchantments);

            for (int i = 0; i < enchantmentCount && !availableEnchantments.isEmpty(); i++) {
                int randomIndex = RANDOM.nextInt(availableEnchantments.size());
                RegistryEntry<Enchantment> enchantment = availableEnchantments.get(randomIndex);
                availableEnchantments.remove(randomIndex);

                int level = 1 + RANDOM.nextInt(getMaxEnchantmentLevel(enchantment, mob));
                weapon.addEnchantment(enchantment, level);
            }
        } catch (Exception e) {
            // Log error but don't crash the game
            BuffMobsMod.LOGGER.warn("Failed to apply enchantments to melee weapon: {}", e.getMessage());
        }
    }

    private static int getMaxEnchantmentLevel(RegistryEntry<Enchantment> enchantment, MobEntity mob) {
        Difficulty difficulty = mob.getWorld().getDifficulty();
        int baseLevel = switch (difficulty) {
            case PEACEFUL -> 1;
            case EASY -> 2;
            case NORMAL -> 3;
            case HARD -> 4;
        };

        if (MobBuffUtil.isDayScalingEnabled()) {
            double dayMultiplier = MobBuffUtil.getDayMultiplier(mob.getWorld().getTimeOfDay());
            if (dayMultiplier > 2.0) {
                baseLevel = Math.min(5, baseLevel + 1);
            }
        }

        return Math.min(baseLevel, enchantment.value().getMaxLevel());
    }

    private static boolean isRangedMob(MobEntity mob) {
        if (mob instanceof AbstractSkeletonEntity ||
                mob instanceof PillagerEntity ||
                mob instanceof PiglinEntity ||
                mob instanceof WitherSkeletonEntity ||
                mob instanceof StrayEntity) {
            return true;
        }

        String mobName = Registries.ENTITY_TYPE.getId(mob.getType()).toString().toLowerCase();
        return mobName.contains("archer") ||
                mobName.contains("crossbow") ||
                mobName.contains("bow") ||
                mobName.contains("shooter") ||
                mobName.contains("ranger") ||
                mobName.contains("gunner") ||
                mobName.contains("marksman") ||
                mobName.contains("sniper");
    }

    public static void handleMobTick(MobEntity mob) {
        if (!BuffMobsConfig.isRangedEquipmentEnabled() || !isRangedMob(mob) || !mob.isAlive()) {
            return;
        }

        if (shouldEquipMeleeWeapon(mob)) {
            equipMeleeWeapon(mob);
        }
    }

    public static void cleanup(MobEntity mob) {
        ORIGINAL_WEAPONS.remove(mob);
        SWITCHED_TO_MELEE.remove(mob);
    }

    public static boolean isSwitchedToMelee(MobEntity mob) {
        return SWITCHED_TO_MELEE.getOrDefault(mob, false);
    }
}