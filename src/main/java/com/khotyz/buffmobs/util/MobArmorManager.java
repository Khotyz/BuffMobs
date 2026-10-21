package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.config.BuffMobsConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.khotyz.buffmobs.util.DimensionUtil.getDimensionId;

public class MobArmorManager {

    private static final RandomSource RANDOM = RandomSource.create();
    private static final Set<UUID> ARMORED_MOBS = new HashSet<>();
    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public static void equipArmor(Mob mob) {
        equipArmor(mob, false);
    }

    public static void equipArmor(Mob mob, boolean forceReapply) {
        if (!isEligible(mob)) return;
        UUID uuid = mob.getUUID();
        boolean alreadyArmored = ARMORED_MOBS.contains(uuid);
        if (alreadyArmored && !forceReapply) return;
        if (!alreadyArmored && RANDOM.nextDouble() >= BuffMobsConfig.INSTANCE.mobArmor.equipChance) return;
        if (alreadyArmored && forceReapply) clearArmorSlots(mob);

        Level level = mob.level();
        long worldDays = MobBuffUtil.getEffectiveDays(level);
        BuffMobsConfig.MobArmor armorCfg = BuffMobsConfig.INSTANCE.mobArmor;
        List<Integer> selectedSlots = selectArmorSlots(armorCfg);
        ArmorTier sharedTier = armorCfg.mixedTiersEnabled ? null : rollTier(getDimensionId(level), worldDays);
        if (!armorCfg.mixedTiersEnabled && sharedTier == null) return;

        boolean equippedAny = false;
        for (int i : selectedSlots) {
            EquipmentSlot slot = ARMOR_SLOTS[i];
            if (!alreadyArmored && !mob.getItemBySlot(slot).isEmpty()) continue;
            ArmorTier tier = armorCfg.mixedTiersEnabled ? rollTier(getDimensionId(level), worldDays) : sharedTier;
            if (tier == null) continue;
            ItemStack piece = new ItemStack(tier.pieces[i]);
            if (armorCfg.enchantmentsEnabled && RANDOM.nextDouble() < armorCfg.enchantedPieceChance) {
                applyEnchantments(piece, slot, worldDays, level);
            }
            if (armorCfg.cursesEnabled) {
                applyCurses(piece, worldDays, level);
            }
            mob.setItemSlot(slot, piece);
            mob.setDropChance(slot, resolveDropChance(armorCfg));
            equippedAny = true;
        }
        if (equippedAny || alreadyArmored) ARMORED_MOBS.add(uuid);
    }

    public static void stripArmor(Mob mob) {
        if (ARMORED_MOBS.remove(mob.getUUID())) {
            clearArmorSlots(mob);
        }
    }

    public static void onMobRemoved(Mob mob) {
        ARMORED_MOBS.remove(mob.getUUID());
    }

    public static void clearCaches() {
        ARMORED_MOBS.clear();
    }

    public static boolean canWearArmor(Mob mob) {
        if (mob instanceof Zombie || mob instanceof AbstractSkeleton || mob instanceof AbstractPiglin) return true;
        String mobId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        return BuffMobsConfig.INSTANCE.mobArmor.additionalArmorMobs.contains(mobId);
    }

    private static boolean isEligible(Mob mob) {
        if (!BuffMobsConfig.INSTANCE.enabled) return false;
        if (!BuffMobsConfig.INSTANCE.mobArmor.enabled) return false;
        if (!MobBuffUtil.isValidMob(mob)) return false;
        return canWearArmor(mob);
    }

    private static void clearArmorSlots(Mob mob) {
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            mob.setItemSlot(slot, ItemStack.EMPTY);
            mob.setDropChance(slot, 0.0f);
        }
    }

    private static List<Integer> selectArmorSlots(BuffMobsConfig.MobArmor armorCfg) {
        List<Integer> selected = new ArrayList<>();
        if (armorCfg.setupMode == BuffMobsConfig.MobArmor.EquipmentSetupMode.FULL_SET) {
            selected.add(0);
            selected.add(1);
            selected.add(2);
            selected.add(3);
            return selected;
        }
        double[] slotChances = {
                armorCfg.helmetChance,
                armorCfg.chestplateChance,
                armorCfg.leggingsChance,
                armorCfg.bootsChance
        };
        for (int index = 0; index < slotChances.length; index++) {
            if (RANDOM.nextDouble() < slotChances[index]) selected.add(index);
        }
        int minPieces = Math.max(0, Math.min(4, armorCfg.minPieces));
        int maxPieces = Math.max(minPieces, Math.min(4, armorCfg.maxPieces));
        if (selected.size() < minPieces) {
            List<Integer> missing = new ArrayList<>();
            for (int index = 0; index < ARMOR_SLOTS.length; index++) {
                if (!selected.contains(index)) missing.add(index);
            }
            shuffleIndexes(missing);
            for (int index : missing) {
                if (selected.size() >= minPieces) break;
                selected.add(index);
            }
        }
        if (selected.size() > maxPieces) {
            shuffleIndexes(selected);
            return new ArrayList<>(selected.subList(0, maxPieces));
        }
        return selected;
    }

    private static void shuffleIndexes(List<Integer> indexes) {
        for (int index = indexes.size() - 1; index > 0; index--) {
            int swapIndex = RANDOM.nextInt(index + 1);
            int current = indexes.get(index);
            indexes.set(index, indexes.get(swapIndex));
            indexes.set(swapIndex, current);
        }
    }

    private static float resolveDropChance(BuffMobsConfig.MobArmor armorCfg) {
        if (!armorCfg.dropChanceEnabled) return 0.0f;
        return (float) Math.max(0.0, Math.min(1.0, armorCfg.dropChance));
    }

    private static ArmorTier rollTier(String dim, long days) {
        List<ArmorTier> tiers = getAvailableTiers(dim, days);
        if (tiers.isEmpty()) return null;
        double total = tiers.stream().mapToDouble(t -> t.weight).sum();
        if (total <= 0.0) return null;
        double roll = RANDOM.nextDouble() * total;
        double cur = 0;
        for (ArmorTier tier : tiers) {
            cur += tier.weight;
            if (roll <= cur) return tier;
        }
        return tiers.get(0);
    }

    private static List<ArmorTier> getAvailableTiers(String dim, long days) {
        List<ArmorTier> tiers = new ArrayList<>();
        BuffMobsConfig.MobArmor c = BuffMobsConfig.INSTANCE.mobArmor;

        double goldWeight = c.goldWeight;
        double diamondWeight = c.diamondWeight;
        double netheriteWeight = c.netheriteWeight;
        if (dim.equals("minecraft:the_nether")) {
            goldWeight *= 2.0;
        } else if (dim.equals("minecraft:the_end")) {
            diamondWeight *= 2.0;
            netheriteWeight *= 2.0;
        }

        if (days >= c.leatherUnlockDay) {
            tiers.add(new ArmorTier(c.leatherWeight,
                    Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS));
        }
        if (days >= c.goldUnlockDay) {
            tiers.add(new ArmorTier(goldWeight,
                    Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS));
        }
        if (days >= c.ironUnlockDay) {
            tiers.add(new ArmorTier(c.ironWeight,
                    Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS));
        }
        if (days >= c.diamondUnlockDay) {
            tiers.add(new ArmorTier(diamondWeight,
                    Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS));
        }
        if (days >= c.netheriteUnlockDay) {
            tiers.add(new ArmorTier(netheriteWeight,
                    Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS));
        }
        return tiers;
    }

    private static void applyEnchantments(ItemStack piece, EquipmentSlot slot, long days, Level level) {
        var registry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        BuffMobsConfig.MobArmor c = BuffMobsConfig.INSTANCE.mobArmor;
        List<ArmorEnchant> available = new ArrayList<>();
        addIfUnlocked(available, days, c.protectionUnlockDay, c.protectionMaxLevel, Enchantments.PROTECTION, 40.0, "protection");
        addIfUnlocked(available, days, c.fireProtectionUnlockDay, c.fireProtectionMaxLevel, Enchantments.FIRE_PROTECTION, 15.0, "protection");
        addIfUnlocked(available, days, c.blastProtectionUnlockDay, c.blastProtectionMaxLevel, Enchantments.BLAST_PROTECTION, 15.0, "protection");
        addIfUnlocked(available, days, c.projectileProtectionUnlockDay, c.projectileProtectionMaxLevel, Enchantments.PROJECTILE_PROTECTION, 15.0, "protection");
        addIfUnlocked(available, days, c.thornsUnlockDay, c.thornsMaxLevel, Enchantments.THORNS, 20.0, "");
        addIfUnlocked(available, days, c.unbreakingUnlockDay, c.unbreakingMaxLevel, Enchantments.UNBREAKING, 20.0, "");
        if (slot == EquipmentSlot.HEAD) {
            addIfUnlocked(available, days, c.respirationUnlockDay, c.respirationMaxLevel, Enchantments.RESPIRATION, 10.0, "");
            addIfUnlocked(available, days, c.aquaAffinityUnlockDay, c.aquaAffinityMaxLevel, Enchantments.AQUA_AFFINITY, 8.0, "");
        }
        if (slot == EquipmentSlot.FEET) {
            addIfUnlocked(available, days, c.featherFallingUnlockDay, c.featherFallingMaxLevel, Enchantments.FEATHER_FALLING, 25.0, "");
            addIfUnlocked(available, days, c.depthStriderUnlockDay, c.depthStriderMaxLevel, Enchantments.DEPTH_STRIDER, 15.0, "boots_movement");
            addIfUnlocked(available, days, c.frostWalkerUnlockDay, c.frostWalkerMaxLevel, Enchantments.FROST_WALKER, 10.0, "boots_movement");
        }

        List<ArmorEnchant> remaining = new ArrayList<>(available);
        int maxToApply = Math.min(calcMaxEnchants(days), remaining.size());
        int toApply = maxToApply <= 0 ? 0 : 1 + RANDOM.nextInt(maxToApply);
        for (int i = 0; i < toApply && !remaining.isEmpty(); i++) {
            double total = remaining.stream().mapToDouble(e -> e.weight).sum();
            double roll = RANDOM.nextDouble() * total;
            double cur = 0;
            ArmorEnchant selected = null;
            for (ArmorEnchant candidate : remaining) {
                cur += candidate.weight;
                if (roll <= cur) {
                    selected = candidate;
                    break;
                }
            }
            if (selected != null) {
                final ArmorEnchant finalSelected = selected;
                registry.get(finalSelected.key).ifPresent(entry -> piece.enchant(entry, finalSelected.level));
                if (!finalSelected.exclusiveGroup.isEmpty()) {
                    remaining.removeIf(candidate -> finalSelected.exclusiveGroup.equals(candidate.exclusiveGroup));
                } else {
                    remaining.remove(finalSelected);
                }
            }
        }
    }

    private static void applyCurses(ItemStack piece, long days, Level level) {
        BuffMobsConfig.MobArmor armorCfg = BuffMobsConfig.INSTANCE.mobArmor;
        if (days < armorCfg.curseUnlockDay) return;
        var registry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        if (armorCfg.bindingCurseEnabled && RANDOM.nextDouble() < armorCfg.curseChance) {
            registry.get(Enchantments.BINDING_CURSE).ifPresent(entry -> piece.enchant(entry, 1));
        }
        if (armorCfg.vanishingCurseEnabled && RANDOM.nextDouble() < armorCfg.curseChance) {
            registry.get(Enchantments.VANISHING_CURSE).ifPresent(entry -> piece.enchant(entry, 1));
        }
    }

    private static void addIfUnlocked(List<ArmorEnchant> list, long days, int unlockDay,
                                      int maxLevel, ResourceKey<Enchantment> key, double weight, String exclusiveGroup) {
        if (days >= unlockDay) {
            list.add(new ArmorEnchant(key, calcEnchantLevel(maxLevel, unlockDay, days), weight, exclusiveGroup));
        }
    }

    private static int calcMaxEnchants(long days) {
        int max = BuffMobsConfig.INSTANCE.mobArmor.maxEnchantmentsPerPiece;
        int interval = BuffMobsConfig.INSTANCE.dayScaling.interval;
        if (days >= interval * 20L) return Math.min(4, max);
        if (days >= interval * 10L) return Math.min(3, max);
        if (days >= interval * 5L) return Math.min(2, max);
        return Math.min(1, max);
    }

    private static int calcEnchantLevel(int maxLevel, int unlockDay, long days) {
        if (maxLevel <= 1) return 1;
        long after = Math.max(0, days - unlockDay);
        int perLevel = BuffMobsConfig.INSTANCE.mobArmor.daysPerEnchantmentLevel;
        return Math.min(1 + (int) (after / perLevel), maxLevel);
    }

    private static class ArmorTier {
        final double weight;
        final Item[] pieces;

        ArmorTier(double weight, Item helmet, Item chestplate, Item leggings, Item boots) {
            this.weight = weight;
            this.pieces = new Item[]{helmet, chestplate, leggings, boots};
        }
    }

    private static class ArmorEnchant {
        final ResourceKey<Enchantment> key;
        final int level;
        final double weight;
        final String exclusiveGroup;

        ArmorEnchant(ResourceKey<Enchantment> key, int level, double weight, String exclusiveGroup) {
            this.key = key;
            this.level = level;
            this.weight = weight;
            this.exclusiveGroup = exclusiveGroup;
        }
    }
}
