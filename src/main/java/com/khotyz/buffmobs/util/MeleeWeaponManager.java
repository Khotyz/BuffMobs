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
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class MeleeWeaponManager {
    private static final Random RANDOM = Random.create();

    public static ItemStack generateMeleeWeapon(MobEntity mob) {
        World world = mob.getEntityWorld();
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
        long worldDays = calculateWorldDays(dayMultiplier);

        if (dimension.equals("minecraft:the_nether")) {
            if (worldDays >= BuffMobsMod.CONFIG.rangedMeleeSwitching.goldenAxeUnlockDay) {
                tiers.add(new WeaponTier(Items.GOLDEN_AXE, 50.0));
            }
            if (worldDays >= BuffMobsMod.CONFIG.rangedMeleeSwitching.diamondAxeUnlockDay) {
                tiers.add(new WeaponTier(Items.DIAMOND_AXE, 10.0));
            }
            if (worldDays >= BuffMobsMod.CONFIG.rangedMeleeSwitching.netheriteAxeUnlockDay) {
                tiers.add(new WeaponTier(Items.NETHERITE_AXE, 1.0));
            }
        } else if (dimension.equals("minecraft:overworld")) {
            if (worldDays >= BuffMobsMod.CONFIG.rangedMeleeSwitching.stoneSwordUnlockDay) {
                tiers.add(new WeaponTier(Items.STONE_SWORD, 50.0));
            }
            if (worldDays >= BuffMobsMod.CONFIG.rangedMeleeSwitching.ironSwordUnlockDay) {
                tiers.add(new WeaponTier(Items.IRON_SWORD, 30.0));
            }
            if (worldDays >= BuffMobsMod.CONFIG.rangedMeleeSwitching.diamondSwordUnlockDay) {
                tiers.add(new WeaponTier(Items.DIAMOND_SWORD, 5.0));
            }
        } else if (dimension.equals("minecraft:the_end")) {
            if (worldDays >= BuffMobsMod.CONFIG.rangedMeleeSwitching.stoneSwordUnlockDay) {
                tiers.add(new WeaponTier(Items.STONE_SWORD, 40.0));
            }
            if (worldDays >= BuffMobsMod.CONFIG.rangedMeleeSwitching.ironSwordUnlockDay) {
                tiers.add(new WeaponTier(Items.IRON_SWORD, 30.0));
            }
            if (worldDays >= BuffMobsMod.CONFIG.rangedMeleeSwitching.diamondSwordUnlockDay) {
                tiers.add(new WeaponTier(Items.DIAMOND_SWORD, 15.0));
            }
            if (worldDays >= BuffMobsMod.CONFIG.rangedMeleeSwitching.netheriteSwordUnlockDay) {
                tiers.add(new WeaponTier(Items.NETHERITE_SWORD, 1.0));
            }
        } else {
            tiers.add(new WeaponTier(Items.STONE_SWORD, 60.0));
            tiers.add(new WeaponTier(Items.IRON_SWORD, 40.0));
        }

        return tiers;
    }

    private static void applyEnchantments(ItemStack weapon, double dayMultiplier, World world) {
        int maxEnchantments = calculateMaxEnchantments(dayMultiplier);
        long worldDays = calculateWorldDays(dayMultiplier);

        List<EnchantmentTier> availableEnchantments = new ArrayList<>();
        var enchantmentRegistry = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);

        addEnchantmentIfUnlocked(availableEnchantments, enchantmentRegistry,
                Enchantments.SHARPNESS, worldDays,
                BuffMobsMod.CONFIG.rangedMeleeSwitching.sharpnessUnlockDay,
                BuffMobsMod.CONFIG.rangedMeleeSwitching.sharpnessMaxLevel,
                40.0, dayMultiplier);

        addEnchantmentIfUnlocked(availableEnchantments, enchantmentRegistry,
                Enchantments.FIRE_ASPECT, worldDays,
                BuffMobsMod.CONFIG.rangedMeleeSwitching.fireAspectUnlockDay,
                BuffMobsMod.CONFIG.rangedMeleeSwitching.fireAspectMaxLevel,
                25.0, dayMultiplier);

        addEnchantmentIfUnlocked(availableEnchantments, enchantmentRegistry,
                Enchantments.KNOCKBACK, worldDays,
                BuffMobsMod.CONFIG.rangedMeleeSwitching.knockbackUnlockDay,
                BuffMobsMod.CONFIG.rangedMeleeSwitching.knockbackMaxLevel,
                20.0, dayMultiplier);

        addEnchantmentIfUnlocked(availableEnchantments, enchantmentRegistry,
                Enchantments.SWEEPING_EDGE, worldDays,
                BuffMobsMod.CONFIG.rangedMeleeSwitching.sweepingEdgeUnlockDay,
                BuffMobsMod.CONFIG.rangedMeleeSwitching.sweepingEdgeMaxLevel,
                15.0, dayMultiplier);

        applyRandomEnchantments(weapon, availableEnchantments, maxEnchantments, enchantmentRegistry);
    }

    private static void addEnchantmentIfUnlocked(
            List<EnchantmentTier> list,
            net.minecraft.registry.Registry<Enchantment> registry,
            RegistryKey<Enchantment> key,
            long worldDays,
            int unlockDay,
            int maxLevel,
            double weight,
            double dayMultiplier
    ) {
        if (worldDays >= unlockDay) {
            int level = calculateEnchantmentLevel(maxLevel, unlockDay, dayMultiplier);
            list.add(new EnchantmentTier(key, level, weight));
        }
    }

    private static void applyRandomEnchantments(
            ItemStack weapon,
            List<EnchantmentTier> available,
            int maxCount,
            net.minecraft.registry.Registry<Enchantment> registry
    ) {
        int toApply = Math.min(maxCount, available.size());
        List<EnchantmentTier> remaining = new ArrayList<>(available);

        for (int i = 0; i < toApply && !remaining.isEmpty(); i++) {
            double totalWeight = remaining.stream().mapToDouble(e -> e.weight).sum();
            double roll = RANDOM.nextDouble() * totalWeight;
            double current = 0;

            EnchantmentTier selected = null;
            for (EnchantmentTier ench : remaining) {
                current += ench.weight;
                if (roll <= current) {
                    selected = ench;
                    break;
                }
            }

            if (selected != null) {
                Enchantment enchantment = registry.get(selected.enchantmentKey);

                if (enchantment != null) {
                    RegistryEntry<Enchantment> entry = registry.getEntry(enchantment);
                    if (entry != null) {
                        weapon.addEnchantment(entry, selected.level);
                    }
                }
                remaining.remove(selected);
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

    private static int calculateEnchantmentLevel(int maxLevel, int unlockDay, double dayMultiplier) {
        if (maxLevel <= 1) return 1;

        long worldDays = calculateWorldDays(dayMultiplier);
        long daysAfterUnlock = Math.max(0, worldDays - unlockDay);
        int daysPerLevel = BuffMobsMod.CONFIG.rangedMeleeSwitching.daysPerEnchantmentLevel;

        int level = 1 + (int) (daysAfterUnlock / daysPerLevel);
        return Math.min(level, maxLevel);
    }

    private static long calculateWorldDays(double dayMultiplier) {
        if (dayMultiplier <= 1.0) return 0;
        return (long) ((dayMultiplier - 1.0) / BuffMobsMod.CONFIG.dayScaling.multiplier)
                * BuffMobsMod.CONFIG.dayScaling.interval;
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

        EnchantmentTier(RegistryKey<Enchantment> key, int level, double weight) {
            this.enchantmentKey = key;
            this.level = level;
            this.weight = weight;
        }
    }
}