package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

public class MobPresetUtil {

    public static PresetMultipliers getPresetForMob(Mob mob) {
        if (BuffMobsConfig.MobPresets.enabled.get() != BuffMobsConfig.MobPresets.PresetToggle.ENABLED) {
            return null;
        }

        ResourceLocation mobType = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
        String mobId = mobType.toString();

        BuffMobsMod.LOGGER.info("[PRESET] Checking mob: {}", mobId);

        for (String mapping : BuffMobsConfig.MobPresets.mobMapping.get()) {
            String[] parts = mapping.split(":");
            if (parts.length < 3) {
                BuffMobsMod.LOGGER.warn("[PRESET] Invalid mapping format: {}", mapping);
                continue;
            }

            String mappedNamespace = parts[0];
            String mappedPath = parts[1];
            String presetName = parts[2];

            String mappedMobId = mappedNamespace + ":" + mappedPath;

            BuffMobsMod.LOGGER.info("[PRESET] Checking mapping '{}' -> preset '{}'", mappedMobId, presetName);

            if (mappedMobId.equals(mobId)) {
                BuffMobsMod.LOGGER.info("[PRESET] *** MATCH FOUND! ***");

                PresetMultipliers result = getPresetByName(presetName);
                if (result != null) {
                    BuffMobsMod.LOGGER.info("[PRESET] Applied preset '{}' to '{}': HP={}x, DMG={}x",
                            presetName, mobId, result.health, result.damage);
                    return result;
                } else {
                    BuffMobsMod.LOGGER.warn("[PRESET] Preset '{}' not found!", presetName);
                }
            }
        }

        BuffMobsMod.LOGGER.info("[PRESET] No preset found for {}", mobId);
        return null;
    }

    public static PresetMultipliers getPresetByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        BuffMobsConfig.MobPresets.PresetSlot slot = null;

        if (BuffMobsConfig.MobPresets.preset1.presetName.get().equals(name)) {
            slot = BuffMobsConfig.MobPresets.preset1;
        } else if (BuffMobsConfig.MobPresets.preset2.presetName.get().equals(name)) {
            slot = BuffMobsConfig.MobPresets.preset2;
        } else if (BuffMobsConfig.MobPresets.preset3.presetName.get().equals(name)) {
            slot = BuffMobsConfig.MobPresets.preset3;
        } else if (BuffMobsConfig.MobPresets.preset4.presetName.get().equals(name)) {
            slot = BuffMobsConfig.MobPresets.preset4;
        } else if (BuffMobsConfig.MobPresets.preset5.presetName.get().equals(name)) {
            slot = BuffMobsConfig.MobPresets.preset5;
        }

        if (slot == null) {
            return null;
        }

        return new PresetMultipliers(
                slot.healthMultiplier.get(),
                slot.damageMultiplier.get(),
                slot.speedMultiplier.get(),
                slot.attackSpeedMultiplier.get(),
                slot.armorAddition.get(),
                slot.armorToughnessAddition.get()
        );
    }

    public static class PresetMultipliers {
        public final double health;
        public final double damage;
        public final double speed;
        public final double attackSpeed;
        public final double armor;
        public final double armorToughness;

        public PresetMultipliers(double health, double damage, double speed,
                                 double attackSpeed, double armor, double armorToughness) {
            this.health = health;
            this.damage = damage;
            this.speed = speed;
            this.attackSpeed = attackSpeed;
            this.armor = armor;
            this.armorToughness = armorToughness;
        }
    }
}