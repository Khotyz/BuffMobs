package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;

public class MobPresetUtil {

    public static PresetMultipliers getPresetForMob(MobEntity mob) {
        if (!BuffMobsMod.CONFIG.mobPresets.enabled) {
            BuffMobsMod.LOGGER.debug("Mob presets disabled in config");
            return null;
        }

        String mobId = Registries.ENTITY_TYPE.getId(mob.getType()).toString();
        String presetName = null;

        BuffMobsMod.LOGGER.debug("Checking preset for mob: {}", mobId);
        BuffMobsMod.LOGGER.debug("Available mappings: {}", BuffMobsMod.CONFIG.mobPresets.mobMapping);

        for (String mapping : BuffMobsMod.CONFIG.mobPresets.mobMapping) {
            String[] parts = mapping.split(":");
            if (parts.length >= 3) {
                String mappedMobId = parts[0] + ":" + parts[1];
                String mappedPreset = parts[2];

                BuffMobsMod.LOGGER.debug("Checking mapping: {} -> {}", mappedMobId, mappedPreset);

                if (mappedMobId.equals(mobId)) {
                    presetName = mappedPreset;
                    BuffMobsMod.LOGGER.debug("Found matching preset: {}", presetName);
                    break;
                }
            } else {
                BuffMobsMod.LOGGER.warn("Invalid mapping format: '{}' (expected format: 'mobid:presetname')", mapping);
            }
        }

        if (presetName == null || presetName.isEmpty()) {
            BuffMobsMod.LOGGER.debug("No preset found for mob: {}", mobId);
            return null;
        }

        BuffMobsConfig.MobPresets.PresetSlot preset = findPresetByName(presetName);
        if (preset == null || preset.presetName.isEmpty()) {
            BuffMobsMod.LOGGER.warn("Preset '{}' not found or empty for mob '{}'", presetName, mobId);
            return null;
        }

        BuffMobsMod.LOGGER.info("Applied preset '{}' to mob '{}': health={}, damage={}",
                presetName, mobId, preset.healthMultiplier, preset.damageMultiplier);

        return new PresetMultipliers(
                preset.healthMultiplier,
                preset.damageMultiplier,
                preset.speedMultiplier,
                preset.attackSpeedMultiplier,
                preset.armorAddition,
                preset.armorToughnessAddition
        );
    }

    private static BuffMobsConfig.MobPresets.PresetSlot findPresetByName(String name) {
        BuffMobsConfig.MobPresets.PresetSlot[] presets = {
                BuffMobsMod.CONFIG.mobPresets.preset1,
                BuffMobsMod.CONFIG.mobPresets.preset2,
                BuffMobsMod.CONFIG.mobPresets.preset3,
                BuffMobsMod.CONFIG.mobPresets.preset4,
                BuffMobsMod.CONFIG.mobPresets.preset5
        };

        for (BuffMobsConfig.MobPresets.PresetSlot preset : presets) {
            BuffMobsMod.LOGGER.debug("Comparing preset name '{}' with '{}'", preset.presetName, name);
            if (preset.presetName.equals(name)) {
                BuffMobsMod.LOGGER.debug("Preset match found!");
                return preset;
            }
        }

        BuffMobsMod.LOGGER.debug("No preset matched for name: {}", name);
        return null;
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