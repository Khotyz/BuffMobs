package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.config.BuffMobsConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

import static com.khotyz.buffmobs.util.DimensionUtil.getDimensionId;

public class MeleeWeaponManager {
    private static final RandomSource RANDOM = RandomSource.create();

    public static ItemStack generateMeleeWeapon(Mob mob) {
        Level level = mob.level();
        String dim      = getDimensionId(level);
        long   worldDays = level.getDayTime() / 24000L;

        ItemStack weapon = createWeapon(dim, worldDays);
        if (BuffMobsConfig.INSTANCE.rangedMeleeSwitching.enchantmentsEnabled) {
            applyEnchantments(weapon, worldDays, level);
        }
        return weapon;
    }

    private static ItemStack createWeapon(String dim, long days) {
        List<WeaponTier> tiers = getAvailableTiers(dim, days);
        if (tiers.isEmpty()) return new ItemStack(Items.STONE_SWORD);

        double total = tiers.stream().mapToDouble(t -> t.weight).sum();
        double roll  = RANDOM.nextDouble() * total;
        double cur   = 0;
        for (WeaponTier tier : tiers) {
            cur += tier.weight;
            if (roll <= cur) return new ItemStack(tier.item);
        }
        return new ItemStack(tiers.get(0).item);
    }

    private static List<WeaponTier> getAvailableTiers(String dim, long days) {
        List<WeaponTier> t = new ArrayList<>();
        BuffMobsConfig.RangedMeleeSwitching c = BuffMobsConfig.INSTANCE.rangedMeleeSwitching;

        if (dim.equals("minecraft:the_nether")) {
            if (days >= c.goldenAxeUnlockDay)    t.add(new WeaponTier(Items.GOLDEN_AXE,    50.0));
            if (days >= c.diamondAxeUnlockDay)   t.add(new WeaponTier(Items.DIAMOND_AXE,   10.0));
            if (days >= c.netheriteAxeUnlockDay) t.add(new WeaponTier(Items.NETHERITE_AXE,  1.0));
        } else if (dim.equals("minecraft:overworld")) {
            if (days >= c.stoneSwordUnlockDay)   t.add(new WeaponTier(Items.STONE_SWORD,   50.0));
            if (days >= c.ironSwordUnlockDay)    t.add(new WeaponTier(Items.IRON_SWORD,    30.0));
            if (days >= c.diamondSwordUnlockDay) t.add(new WeaponTier(Items.DIAMOND_SWORD,  5.0));
        } else if (dim.equals("minecraft:the_end")) {
            if (days >= c.stoneSwordUnlockDay)     t.add(new WeaponTier(Items.STONE_SWORD,    40.0));
            if (days >= c.ironSwordUnlockDay)      t.add(new WeaponTier(Items.IRON_SWORD,     30.0));
            if (days >= c.diamondSwordUnlockDay)   t.add(new WeaponTier(Items.DIAMOND_SWORD,  15.0));
            if (days >= c.netheriteSwordUnlockDay) t.add(new WeaponTier(Items.NETHERITE_SWORD, 1.0));
        } else {
            t.add(new WeaponTier(Items.STONE_SWORD, 60.0));
            t.add(new WeaponTier(Items.IRON_SWORD,  40.0));
        }
        return t;
    }

    private static void applyEnchantments(ItemStack weapon, long days, Level level) {
        var reg  = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        int maxE = calcMaxEnchants(days);
        List<EnchantTier> avail = new ArrayList<>();

        BuffMobsConfig.RangedMeleeSwitching c = BuffMobsConfig.INSTANCE.rangedMeleeSwitching;
        addIfUnlocked(avail, days, c.sharpnessUnlockDay,    c.sharpnessMaxLevel,    Enchantments.SHARPNESS,     40.0);
        addIfUnlocked(avail, days, c.fireAspectUnlockDay,   c.fireAspectMaxLevel,   Enchantments.FIRE_ASPECT,   25.0);
        addIfUnlocked(avail, days, c.knockbackUnlockDay,    c.knockbackMaxLevel,    Enchantments.KNOCKBACK,     20.0);
        addIfUnlocked(avail, days, c.sweepingEdgeUnlockDay, c.sweepingEdgeMaxLevel, Enchantments.SWEEPING_EDGE, 15.0);

        List<EnchantTier> remaining = new ArrayList<>(avail);
        int toApply = Math.min(maxE, remaining.size());

        for (int i = 0; i < toApply && !remaining.isEmpty(); i++) {
            double total = remaining.stream().mapToDouble(e -> e.weight).sum();
            double roll  = RANDOM.nextDouble() * total;
            double cur   = 0;
            EnchantTier sel = null;
            for (EnchantTier e : remaining) { cur += e.weight; if (roll <= cur) { sel = e; break; } }
            if (sel != null) {
                final EnchantTier finalSel = sel;
                reg.get(finalSel.key).ifPresent(entry -> weapon.enchant(entry, finalSel.level));
                remaining.remove(sel);
            }
        }
    }

    private static void addIfUnlocked(List<EnchantTier> list, long days, int unlockDay,
                                      int maxLevel, ResourceKey<Enchantment> key, double weight) {
        if (days >= unlockDay) {
            int level = calcEnchantLevel(maxLevel, unlockDay, days);
            list.add(new EnchantTier(key, level, weight));
        }
    }

    private static int calcMaxEnchants(long days) {
        int max      = BuffMobsConfig.INSTANCE.rangedMeleeSwitching.maxEnchantmentsPerWeapon;
        int interval = BuffMobsConfig.INSTANCE.dayScaling.interval;
        if (days >= interval * 20L) return Math.min(4, max);
        if (days >= interval * 10L) return Math.min(3, max);
        if (days >= interval * 5L)  return Math.min(2, max);
        return Math.min(1, max);
    }

    private static int calcEnchantLevel(int maxLevel, int unlockDay, long days) {
        if (maxLevel <= 1) return 1;
        long after   = Math.max(0, days - unlockDay);
        int perLevel = BuffMobsConfig.INSTANCE.rangedMeleeSwitching.daysPerEnchantmentLevel;
        return Math.min(1 + (int)(after / perLevel), maxLevel);
    }

    private static class WeaponTier {
        final net.minecraft.world.item.Item item;
        final double weight;
        WeaponTier(net.minecraft.world.item.Item item, double weight) { this.item = item; this.weight = weight; }
    }

    private static class EnchantTier {
        final ResourceKey<Enchantment> key;
        final int level;
        final double weight;
        EnchantTier(ResourceKey<Enchantment> key, int level, double weight) {
            this.key = key; this.level = level; this.weight = weight;
        }
    }
}
