package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class MeleeWeaponManager {
    private static final Random RANDOM = Random.create();

    public static ItemStack generateMeleeWeapon(MobEntity mob) {
        World world = mob.getWorld();
        String dimensionName = world.getRegistryKey().getValue().toString();

        double dayMultiplier = MobBuffUtil.getDayMultiplier(world.getTimeOfDay());

        ItemStack weapon = createWeaponForDimension(dimensionName, dayMultiplier);

        if (BuffMobsMod.CONFIG.rangedMeleeSwitching.enchantmentsEnabled) {
            applyEnchantments(weapon, dayMultiplier, world);
        }

        return weapon;
    }

    private static ItemStack createWeaponForDimension(String dimension, double dayMultiplier) {
        List<WeaponTier> availableTiers = getAvailableTiers(dimension, dayMultiplier);

        if (availableTiers.isEmpty()) {
            return new ItemStack(Items.STONE_SWORD);
        }

        double totalWeight = availableTiers.stream().mapToDouble(t -> t.weight).sum();
        double roll = RANDOM.nextDouble() * totalWeight;
        double current = 0;

        for (WeaponTier tier : availableTiers) {
            current += tier.weight;
            if (roll <= current) {
                return new ItemStack(tier.item);
            }
        }

        return new ItemStack(availableTiers.get(0).item);
    }

    private static List<WeaponTier> getAvailableTiers(String dimension, double dayMultiplier) {
        List<WeaponTier> tiers = new ArrayList<>();

        boolean isOverworld = dimension.equals("minecraft:overworld");
        boolean isNether = dimension.equals("minecraft:the_nether");
        boolean isEnd = dimension.equals("minecraft:the_end");

        if (isNether) {
            if (shouldUnlockTier(BuffMobsMod.CONFIG.rangedMeleeSwitching.goldenAxeUnlockDay, dayMultiplier)) {
                tiers.add(new WeaponTier(Items.GOLDEN_AXE, 50.0));
            }
            if (shouldUnlockTier(BuffMobsMod.CONFIG.rangedMeleeSwitching.diamondAxeUnlockDay, dayMultiplier)) {
                tiers.add(new WeaponTier(Items.DIAMOND_AXE, 10.0));
            }
            if (shouldUnlockTier(BuffMobsMod.CONFIG.rangedMeleeSwitching.netheriteAxeUnlockDay, dayMultiplier)) {
                tiers.add(new WeaponTier(Items.NETHERITE_AXE, 1.0));
            }
        } else if (isOverworld) {
            if (shouldUnlockTier(BuffMobsMod.CONFIG.rangedMeleeSwitching.stoneSwordUnlockDay, dayMultiplier)) {
                tiers.add(new WeaponTier(Items.STONE_SWORD, 50.0));
            }
            if (shouldUnlockTier(BuffMobsMod.CONFIG.rangedMeleeSwitching.ironSwordUnlockDay, dayMultiplier)) {
                tiers.add(new WeaponTier(Items.IRON_SWORD, 30.0));
            }
            if (shouldUnlockTier(BuffMobsMod.CONFIG.rangedMeleeSwitching.diamondSwordUnlockDay, dayMultiplier)) {
                tiers.add(new WeaponTier(Items.DIAMOND_SWORD, 5.0));
            }
        } else if (isEnd) {
            if (shouldUnlockTier(BuffMobsMod.CONFIG.rangedMeleeSwitching.stoneSwordUnlockDay, dayMultiplier)) {
                tiers.add(new WeaponTier(Items.STONE_SWORD, 40.0));
            }
            if (shouldUnlockTier(BuffMobsMod.CONFIG.rangedMeleeSwitching.ironSwordUnlockDay, dayMultiplier)) {
                tiers.add(new WeaponTier(Items.IRON_SWORD, 30.0));
            }
            if (shouldUnlockTier(BuffMobsMod.CONFIG.rangedMeleeSwitching.diamondSwordUnlockDay, dayMultiplier)) {
                tiers.add(new WeaponTier(Items.DIAMOND_SWORD, 15.0));
            }
            if (shouldUnlockTier(BuffMobsMod.CONFIG.rangedMeleeSwitching.netheriteSwordUnlockDay, dayMultiplier)) {
                tiers.add(new WeaponTier(Items.NETHERITE_SWORD, 1.0));
            }
        } else {
            tiers.add(new WeaponTier(Items.STONE_SWORD, 60.0));
            tiers.add(new WeaponTier(Items.IRON_SWORD, 40.0));
        }

        return tiers;
    }

    private static boolean shouldUnlockTier(int unlockDay, double dayMultiplier) {
        if (unlockDay <= 0) return true;

        long worldDays = (long) ((dayMultiplier - 1.0) / BuffMobsMod.CONFIG.dayScaling.multiplier)
                * BuffMobsMod.CONFIG.dayScaling.interval;

        return worldDays >= unlockDay;
    }

    private static void applyEnchantments(ItemStack weapon, double dayMultiplier, World world) {
        int maxEnchantments = calculateMaxEnchantments(dayMultiplier);

        List<EnchantmentTier> availableEnchantments = new ArrayList<>();

        if (shouldUnlockEnchantment(BuffMobsMod.CONFIG.rangedMeleeSwitching.sharpnessUnlockDay, dayMultiplier)) {
            int level = calculateEnchantmentLevel(
                    BuffMobsMod.CONFIG.rangedMeleeSwitching.sharpnessMaxLevel,
                    BuffMobsMod.CONFIG.rangedMeleeSwitching.sharpnessUnlockDay,
                    dayMultiplier
            );
            availableEnchantments.add(new EnchantmentTier(Enchantments.SHARPNESS, level, 40.0));
        }

        if (shouldUnlockEnchantment(BuffMobsMod.CONFIG.rangedMeleeSwitching.fireAspectUnlockDay, dayMultiplier)) {
            int level = calculateEnchantmentLevel(
                    BuffMobsMod.CONFIG.rangedMeleeSwitching.fireAspectMaxLevel,
                    BuffMobsMod.CONFIG.rangedMeleeSwitching.fireAspectUnlockDay,
                    dayMultiplier
            );
            availableEnchantments.add(new EnchantmentTier(Enchantments.FIRE_ASPECT, level, 25.0));
        }

        if (shouldUnlockEnchantment(BuffMobsMod.CONFIG.rangedMeleeSwitching.knockbackUnlockDay, dayMultiplier)) {
            int level = calculateEnchantmentLevel(
                    BuffMobsMod.CONFIG.rangedMeleeSwitching.knockbackMaxLevel,
                    BuffMobsMod.CONFIG.rangedMeleeSwitching.knockbackUnlockDay,
                    dayMultiplier
            );
            availableEnchantments.add(new EnchantmentTier(Enchantments.KNOCKBACK, level, 20.0));
        }

        if (shouldUnlockEnchantment(BuffMobsMod.CONFIG.rangedMeleeSwitching.sweepingEdgeUnlockDay, dayMultiplier)) {
            int level = calculateEnchantmentLevel(
                    BuffMobsMod.CONFIG.rangedMeleeSwitching.sweepingEdgeMaxLevel,
                    BuffMobsMod.CONFIG.rangedMeleeSwitching.sweepingEdgeUnlockDay,
                    dayMultiplier
            );
            availableEnchantments.add(new EnchantmentTier(Enchantments.SWEEPING_EDGE, level, 15.0));
        }

        int enchantmentsToApply = Math.min(maxEnchantments, availableEnchantments.size());
        List<EnchantmentTier> shuffled = new ArrayList<>(availableEnchantments);

        for (int i = 0; i < enchantmentsToApply && !shuffled.isEmpty(); i++) {
            double totalWeight = shuffled.stream().mapToDouble(e -> e.weight).sum();
            double roll = RANDOM.nextDouble() * totalWeight;
            double current = 0;

            EnchantmentTier selected = null;
            for (EnchantmentTier ench : shuffled) {
                current += ench.weight;
                if (roll <= current) {
                    selected = ench;
                    break;
                }
            }

            if (selected != null) {
                try {
                    var enchantmentRegistry = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
                    var enchantment = enchantmentRegistry.get(selected.enchantmentKey);

                    if (enchantment != null) {
                        var enchantmentEntry = enchantmentRegistry.getEntry(enchantment);
                        weapon.addEnchantment(enchantmentEntry, selected.level);
                    }
                } catch (Exception e) {
                    // Fail silently if enchantment not found
                }
                shuffled.remove(selected);
            }
        }
    }

    private static int calculateMaxEnchantments(double dayMultiplier) {
        int baseMax = BuffMobsMod.CONFIG.rangedMeleeSwitching.maxEnchantmentsPerWeapon;

        if (dayMultiplier >= 3.0) return Math.min(4, baseMax);
        if (dayMultiplier >= 2.0) return Math.min(3, baseMax);
        if (dayMultiplier >= 1.5) return Math.min(2, baseMax);

        return Math.min(1, baseMax);
    }

    private static boolean shouldUnlockEnchantment(int unlockDay, double dayMultiplier) {
        if (unlockDay <= 0) return true;

        long worldDays = (long) ((dayMultiplier - 1.0) / BuffMobsMod.CONFIG.dayScaling.multiplier)
                * BuffMobsMod.CONFIG.dayScaling.interval;

        return worldDays >= unlockDay;
    }

    private static int calculateEnchantmentLevel(int maxLevel, int unlockDay, double dayMultiplier) {
        if (maxLevel <= 1) return 1;

        long worldDays = (long) ((dayMultiplier - 1.0) / BuffMobsMod.CONFIG.dayScaling.multiplier)
                * BuffMobsMod.CONFIG.dayScaling.interval;

        long daysAfterUnlock = Math.max(0, worldDays - unlockDay);
        int daysPerLevel = BuffMobsMod.CONFIG.rangedMeleeSwitching.daysPerEnchantmentLevel;

        int level = 1 + (int) (daysAfterUnlock / daysPerLevel);
        return Math.min(level, maxLevel);
    }

    private static class WeaponTier {
        final net.minecraft.item.Item item;
        final double weight;

        WeaponTier(net.minecraft.item.Item item, double weight) {
            this.item = item;
            this.weight = weight;
        }
    }

    private static class EnchantmentTier {
        final RegistryKey<Enchantment> enchantmentKey;
        final int level;
        final double weight;

        EnchantmentTier(RegistryKey<Enchantment> enchantmentKey, int level, double weight) {
            this.enchantmentKey = enchantmentKey;
            this.level = level;
            this.weight = weight;
        }
    }
}