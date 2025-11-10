package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class MeleeWeaponManager {
    private static final RandomSource RANDOM = RandomSource.create();

    public static ItemStack generateMeleeWeapon(Mob mob) {
        Level world = mob.level();
        String dimensionName = world.dimension().location().toString();
        double dayMultiplier = MobBuffUtil.getDayMultiplier(world.getDayTime());

        ItemStack weapon = createWeaponForDimension(dimensionName, dayMultiplier);

        if (BuffMobsConfig.RangedMeleeSwitching.enchantmentsEnabled.get()) {
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
            if (worldDays >= BuffMobsConfig.RangedMeleeSwitching.goldenAxeUnlockDay.get()) {
                tiers.add(new WeaponTier(Items.GOLDEN_AXE, 50.0));
            }
            if (worldDays >= BuffMobsConfig.RangedMeleeSwitching.diamondAxeUnlockDay.get()) {
                tiers.add(new WeaponTier(Items.DIAMOND_AXE, 10.0));
            }
            if (worldDays >= BuffMobsConfig.RangedMeleeSwitching.netheriteAxeUnlockDay.get()) {
                tiers.add(new WeaponTier(Items.NETHERITE_AXE, 1.0));
            }
        } else if (dimension.equals("minecraft:overworld")) {
            if (worldDays >= BuffMobsConfig.RangedMeleeSwitching.stoneSwordUnlockDay.get()) {
                tiers.add(new WeaponTier(Items.STONE_SWORD, 50.0));
            }
            if (worldDays >= BuffMobsConfig.RangedMeleeSwitching.ironSwordUnlockDay.get()) {
                tiers.add(new WeaponTier(Items.IRON_SWORD, 30.0));
            }
            if (worldDays >= BuffMobsConfig.RangedMeleeSwitching.diamondSwordUnlockDay.get()) {
                tiers.add(new WeaponTier(Items.DIAMOND_SWORD, 5.0));
            }
        } else if (dimension.equals("minecraft:the_end")) {
            if (worldDays >= BuffMobsConfig.RangedMeleeSwitching.stoneSwordUnlockDay.get()) {
                tiers.add(new WeaponTier(Items.STONE_SWORD, 40.0));
            }
            if (worldDays >= BuffMobsConfig.RangedMeleeSwitching.ironSwordUnlockDay.get()) {
                tiers.add(new WeaponTier(Items.IRON_SWORD, 30.0));
            }
            if (worldDays >= BuffMobsConfig.RangedMeleeSwitching.diamondSwordUnlockDay.get()) {
                tiers.add(new WeaponTier(Items.DIAMOND_SWORD, 15.0));
            }
            if (worldDays >= BuffMobsConfig.RangedMeleeSwitching.netheriteSwordUnlockDay.get()) {
                tiers.add(new WeaponTier(Items.NETHERITE_SWORD, 1.0));
            }
        } else {
            tiers.add(new WeaponTier(Items.STONE_SWORD, 60.0));
            tiers.add(new WeaponTier(Items.IRON_SWORD, 40.0));
        }

        return tiers;
    }

    private static void applyEnchantments(ItemStack weapon, double dayMultiplier, Level world) {
        int maxEnchantments = calculateMaxEnchantments(dayMultiplier);
        long worldDays = calculateWorldDays(dayMultiplier);

        List<EnchantmentTier> availableEnchantments = new ArrayList<>();
        var enchantmentRegistry = world.registryAccess().registryOrThrow(Registries.ENCHANTMENT);

        addEnchantmentIfUnlocked(availableEnchantments, enchantmentRegistry,
                Enchantments.SHARPNESS, worldDays,
                BuffMobsConfig.RangedMeleeSwitching.sharpnessUnlockDay.get(),
                BuffMobsConfig.RangedMeleeSwitching.sharpnessMaxLevel.get(),
                40.0, dayMultiplier);

        addEnchantmentIfUnlocked(availableEnchantments, enchantmentRegistry,
                Enchantments.FIRE_ASPECT, worldDays,
                BuffMobsConfig.RangedMeleeSwitching.fireAspectUnlockDay.get(),
                BuffMobsConfig.RangedMeleeSwitching.fireAspectMaxLevel.get(),
                25.0, dayMultiplier);

        addEnchantmentIfUnlocked(availableEnchantments, enchantmentRegistry,
                Enchantments.KNOCKBACK, worldDays,
                BuffMobsConfig.RangedMeleeSwitching.knockbackUnlockDay.get(),
                BuffMobsConfig.RangedMeleeSwitching.knockbackMaxLevel.get(),
                20.0, dayMultiplier);

        addEnchantmentIfUnlocked(availableEnchantments, enchantmentRegistry,
                Enchantments.SWEEPING_EDGE, worldDays,
                BuffMobsConfig.RangedMeleeSwitching.sweepingEdgeUnlockDay.get(),
                BuffMobsConfig.RangedMeleeSwitching.sweepingEdgeMaxLevel.get(),
                15.0, dayMultiplier);

        applyRandomEnchantments(weapon, availableEnchantments, maxEnchantments, world);
    }

    private static void addEnchantmentIfUnlocked(
            List<EnchantmentTier> list,
            net.minecraft.core.Registry<Enchantment> registry,
            ResourceKey<Enchantment> key,
            long worldDays,
            int unlockDay,
            int maxLevel,
            double weight,
            double dayMultiplier
    ) {
        if (worldDays >= unlockDay) {
            int level = calculateEnchantmentLevel(maxLevel, unlockDay, dayMultiplier);
            var enchantment = registry.getHolder(key).orElse(null);
            if (enchantment != null) {
                list.add(new EnchantmentTier(enchantment, level, weight));
            }
        }
    }

    private static void applyRandomEnchantments(
            ItemStack weapon,
            List<EnchantmentTier> available,
            int maxCount,
            Level world
    ) {
        int toApply = Math.min(maxCount, available.size());
        List<EnchantmentTier> remaining = new ArrayList<>(available);

        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

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
                enchantments.set(selected.enchantment, selected.level);
                remaining.remove(selected);
            }
        }

        EnchantmentHelper.setEnchantments(weapon, enchantments.toImmutable());
    }

    private static int calculateMaxEnchantments(double dayMultiplier) {
        int baseMax = BuffMobsConfig.RangedMeleeSwitching.maxEnchantmentsPerWeapon.get();

        if (dayMultiplier >= 3.0) return Math.min(4, baseMax);
        if (dayMultiplier >= 2.0) return Math.min(3, baseMax);
        if (dayMultiplier >= 1.5) return Math.min(2, baseMax);

        return Math.min(1, baseMax);
    }

    private static int calculateEnchantmentLevel(int maxLevel, int unlockDay, double dayMultiplier) {
        if (maxLevel <= 1) return 1;

        long worldDays = calculateWorldDays(dayMultiplier);
        long daysAfterUnlock = Math.max(0, worldDays - unlockDay);
        int daysPerLevel = BuffMobsConfig.RangedMeleeSwitching.daysPerEnchantmentLevel.get();

        int level = 1 + (int) (daysAfterUnlock / daysPerLevel);
        return Math.min(level, maxLevel);
    }

    private static long calculateWorldDays(double dayMultiplier) {
        if (dayMultiplier <= 1.0) return 0;
        return (long) ((dayMultiplier - 1.0) / BuffMobsConfig.DayScaling.multiplier.get())
                * BuffMobsConfig.DayScaling.interval.get();
    }

    private static class WeaponTier {
        final net.minecraft.world.item.Item item;
        final double weight;

        WeaponTier(net.minecraft.world.item.Item item, double weight) {
            this.item = item;
            this.weight = weight;
        }
    }

    private static class EnchantmentTier {
        final Holder<Enchantment> enchantment;
        final int level;
        final double weight;

        EnchantmentTier(Holder<Enchantment> enchantment, int level, double weight) {
            this.enchantment = enchantment;
            this.level = level;
            this.weight = weight;
        }
    }
}