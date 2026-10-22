package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.data.BuffMobsWorldData;
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
import java.util.List;

import static com.khotyz.buffmobs.util.DimensionUtil.getDimensionId;

public class MobArmorManager {

    private static final RandomSource RANDOM = RandomSource.create();
    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public static void equipArmor(Mob mob) {
        equipArmor(mob, false);
    }

    public static void equipArmor(Mob mob, boolean forceReapply) {
        if (!isEligible(mob)) return;
        BuffMobsWorldData data = BuffMobsWorldData.get(mob.level());
        if (data == null) return;
        BuffMobsWorldData.MobState state = data.getMobState(mob.getUUID());
        int previousMask = state.armorSlotsMask();
        if (state.armorEvaluated() && !forceReapply) return;
        if (forceReapply && previousMask != 0) clearArmorSlots(mob, previousMask);
        if (forceReapply) {
            data.updateMobState(mob.getUUID(), current -> {
                current.setArmorSlotsMask(0);
                current.setArmorEvaluated(false);
            });
        }

        BuffMobsConfig.MobArmor armorCfg = BuffMobsConfig.INSTANCE.mobArmor;
        if (RANDOM.nextDouble() >= armorCfg.equipChance.get()) {
            data.updateMobState(mob.getUUID(), current -> current.setArmorEvaluated(true));
            return;
        }

        Level level = mob.level();
        long worldDays = MobBuffUtil.getEffectiveDays(level);
        List<ArmorTier> availableTiers = getAvailableTiers(getDimensionId(level), worldDays);
        if (availableTiers.isEmpty() || availableTiers.stream().mapToDouble(tier -> tier.weight).sum() <= 0.0) {
            data.updateMobState(mob.getUUID(), current -> current.setArmorEvaluated(false));
            return;
        }
        List<Integer> selectedSlots = selectArmorSlots(armorCfg);
        ArmorTier sharedTier = armorCfg.mixedTiersEnabled.get() ? null : rollTier(getDimensionId(level), worldDays);
        if (!armorCfg.mixedTiersEnabled.get() && sharedTier == null) {
            data.updateMobState(mob.getUUID(), current -> current.setArmorEvaluated(false));
            return;
        }

        int equippedMask = 0;
        for (int i : selectedSlots) {
            EquipmentSlot slot = ARMOR_SLOTS[i];
            if (!mob.getItemBySlot(slot).isEmpty()) continue;
            ArmorTier tier = armorCfg.mixedTiersEnabled.get() ? rollTier(getDimensionId(level), worldDays) : sharedTier;
            if (tier == null) continue;
            ItemStack piece = new ItemStack(tier.pieces[i]);
            if (armorCfg.enchantmentsEnabled.get() && RANDOM.nextDouble() < armorCfg.enchantedPieceChance.get()) {
                applyEnchantments(piece, slot, worldDays, level);
            }
            if (armorCfg.cursesEnabled.get()) {
                applyCurses(piece, worldDays, level);
            }
            mob.setItemSlot(slot, piece);
            mob.setDropChance(slot, resolveDropChance(armorCfg));
            equippedMask |= 1 << i;
        }
        int finalMask = equippedMask;
        data.updateMobState(mob.getUUID(), current -> {
            current.setArmorSlotsMask(finalMask);
            current.setArmorEvaluated(true);
        });
    }

    public static void stripArmor(Mob mob) {
        BuffMobsWorldData data = BuffMobsWorldData.get(mob.level());
        if (data == null) return;
        BuffMobsWorldData.MobState state = data.getMobState(mob.getUUID());
        if (state.armorSlotsMask() != 0) clearArmorSlots(mob, state.armorSlotsMask());
        data.updateMobState(mob.getUUID(), current -> {
            current.setArmorSlotsMask(0);
            current.setArmorEvaluated(false);
        });
    }

    public static void onMobRemoved(Mob mob) {
    }

    public static void clearCaches() {
    }

    public static boolean canWearArmor(Mob mob) {
        if (mob instanceof Zombie || mob instanceof AbstractSkeleton || mob instanceof AbstractPiglin) return true;
        String mobId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        return BuffMobsConfig.INSTANCE.mobArmor.additionalArmorMobs.get().contains(mobId);
    }

    private static boolean isEligible(Mob mob) {
        if (!BuffMobsConfig.INSTANCE.enabled.get()) return false;
        if (!BuffMobsConfig.INSTANCE.mobArmor.enabled.get()) return false;
        if (!MobBuffUtil.isValidMob(mob)) return false;
        return canWearArmor(mob);
    }

    private static void clearArmorSlots(Mob mob, int mask) {
        for (int index = 0; index < ARMOR_SLOTS.length; index++) {
            if ((mask & 1 << index) == 0) continue;
            EquipmentSlot slot = ARMOR_SLOTS[index];
            mob.setItemSlot(slot, ItemStack.EMPTY);
            mob.setDropChance(slot, 0.0f);
        }
    }

    private static List<Integer> selectArmorSlots(BuffMobsConfig.MobArmor armorCfg) {
        List<Integer> selected = new ArrayList<>();
        if (armorCfg.setupMode.get() == BuffMobsConfig.MobArmor.EquipmentSetupMode.FULL_SET) {
            selected.add(0);
            selected.add(1);
            selected.add(2);
            selected.add(3);
            return selected;
        }
        double[] slotChances = {
                armorCfg.helmetChance.get(),
                armorCfg.chestplateChance.get(),
                armorCfg.leggingsChance.get(),
                armorCfg.bootsChance.get()
        };
        for (int index = 0; index < slotChances.length; index++) {
            if (RANDOM.nextDouble() < slotChances[index]) selected.add(index);
        }
        int minPieces = Math.max(0, Math.min(4, armorCfg.minPieces.get()));
        int maxPieces = Math.max(minPieces, Math.min(4, armorCfg.maxPieces.get()));
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
        if (!armorCfg.dropChanceEnabled.get()) return 0.0f;
        return (float) Math.max(0.0, Math.min(1.0, armorCfg.dropChance.get()));
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

        double goldWeight = c.goldWeight.get();
        double diamondWeight = c.diamondWeight.get();
        double netheriteWeight = c.netheriteWeight.get();
        if (dim.equals("minecraft:the_nether")) {
            goldWeight *= 2.0;
        } else if (dim.equals("minecraft:the_end")) {
            diamondWeight *= 2.0;
            netheriteWeight *= 2.0;
        }

        if (days >= c.leatherUnlockDay.get()) {
            tiers.add(new ArmorTier(c.leatherWeight.get(),
                    Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS));
        }
        if (days >= c.goldUnlockDay.get()) {
            tiers.add(new ArmorTier(goldWeight,
                    Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS));
        }
        if (days >= c.ironUnlockDay.get()) {
            tiers.add(new ArmorTier(c.ironWeight.get(),
                    Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS));
        }
        if (days >= c.diamondUnlockDay.get()) {
            tiers.add(new ArmorTier(diamondWeight,
                    Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS));
        }
        if (days >= c.netheriteUnlockDay.get()) {
            tiers.add(new ArmorTier(netheriteWeight,
                    Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS));
        }
        return tiers;
    }

    private static void applyEnchantments(ItemStack piece, EquipmentSlot slot, long days, Level level) {
        var registry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        BuffMobsConfig.MobArmor c = BuffMobsConfig.INSTANCE.mobArmor;
        List<ArmorEnchant> available = new ArrayList<>();
        addIfUnlocked(available, days, c.protectionUnlockDay.get(), c.protectionMaxLevel.get(), Enchantments.PROTECTION, 40.0, "protection");
        addIfUnlocked(available, days, c.fireProtectionUnlockDay.get(), c.fireProtectionMaxLevel.get(), Enchantments.FIRE_PROTECTION, 15.0, "protection");
        addIfUnlocked(available, days, c.blastProtectionUnlockDay.get(), c.blastProtectionMaxLevel.get(), Enchantments.BLAST_PROTECTION, 15.0, "protection");
        addIfUnlocked(available, days, c.projectileProtectionUnlockDay.get(), c.projectileProtectionMaxLevel.get(), Enchantments.PROJECTILE_PROTECTION, 15.0, "protection");
        addIfUnlocked(available, days, c.thornsUnlockDay.get(), c.thornsMaxLevel.get(), Enchantments.THORNS, 20.0, "");
        addIfUnlocked(available, days, c.unbreakingUnlockDay.get(), c.unbreakingMaxLevel.get(), Enchantments.UNBREAKING, 20.0, "");
        if (slot == EquipmentSlot.HEAD) {
            addIfUnlocked(available, days, c.respirationUnlockDay.get(), c.respirationMaxLevel.get(), Enchantments.RESPIRATION, 10.0, "");
            addIfUnlocked(available, days, c.aquaAffinityUnlockDay.get(), c.aquaAffinityMaxLevel.get(), Enchantments.AQUA_AFFINITY, 8.0, "");
        }
        if (slot == EquipmentSlot.FEET) {
            addIfUnlocked(available, days, c.featherFallingUnlockDay.get(), c.featherFallingMaxLevel.get(), Enchantments.FEATHER_FALLING, 25.0, "");
            addIfUnlocked(available, days, c.depthStriderUnlockDay.get(), c.depthStriderMaxLevel.get(), Enchantments.DEPTH_STRIDER, 15.0, "boots_movement");
            addIfUnlocked(available, days, c.frostWalkerUnlockDay.get(), c.frostWalkerMaxLevel.get(), Enchantments.FROST_WALKER, 10.0, "boots_movement");
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
        if (days < armorCfg.curseUnlockDay.get()) return;
        var registry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        if (armorCfg.bindingCurseEnabled.get() && RANDOM.nextDouble() < armorCfg.curseChance.get()) {
            registry.get(Enchantments.BINDING_CURSE).ifPresent(entry -> piece.enchant(entry, 1));
        }
        if (armorCfg.vanishingCurseEnabled.get() && RANDOM.nextDouble() < armorCfg.curseChance.get()) {
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
        int max = BuffMobsConfig.INSTANCE.mobArmor.maxEnchantmentsPerPiece.get();
        int interval = Math.max(1, BuffMobsConfig.INSTANCE.dayScaling.interval.get());
        if (days >= interval * 20L) return Math.min(4, max);
        if (days >= interval * 10L) return Math.min(3, max);
        if (days >= interval * 5L) return Math.min(2, max);
        return Math.min(1, max);
    }

    private static int calcEnchantLevel(int maxLevel, int unlockDay, long days) {
        if (maxLevel <= 1) return 1;
        long after = Math.max(0, days - unlockDay);
        int perLevel = Math.max(1, BuffMobsConfig.INSTANCE.mobArmor.daysPerEnchantmentLevel.get());
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
