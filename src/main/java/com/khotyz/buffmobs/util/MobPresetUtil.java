package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import net.minecraft.world.entity.Mob;

public class MobPresetUtil {

    public static PresetMultipliers getPresetForMob(Mob mob) {
        if (!BuffMobsConfig.MobPresets.enabled.get()) {
            BuffMobsMod.LOGGER.debug("Mob presets disabled in config");
            return null;
        }

        String mobId = mob.getType().toString();
        String presetName = null;

        BuffMobsMod.LOGGER.debug("Checking preset for mob: {}", mobId);

        for (String mapping : BuffMobsConfig.MobPresets.mobMapping.get()) {
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
                BuffMobsMod.LOGGER.warn("Invalid mapping format: '{}'", mapping);
            }
        }

        if (presetName == null || presetName.isEmpty()) {
            BuffMobsMod.LOGGER.debug("No preset found for mob: {}", mobId);
            return null;
        }

        BuffMobsConfig.MobPresets.PresetSlot preset = findPresetByName(presetName);
        if (preset == null || preset.presetName.get().isEmpty()) {
            BuffMobsMod.LOGGER.warn("Preset '{}' not found for mob '{}'", presetName, mobId);
            return null;
        }

        BuffMobsMod.LOGGER.info("Applied preset '{}' to mob '{}': health={}, damage={}",
                presetName, mobId, preset.healthMultiplier.get(), preset.damageMultiplier.get());

        return new PresetMultipliers(
                preset.healthMultiplier.get(),
                preset.damageMultiplier.get(),
                preset.speedMultiplier.get(),
                preset.attackSpeedMultiplier.get(),
                preset.armorAddition.get(),
                preset.armorToughnessAddition.get()
        );
    }

    private static BuffMobsConfig.MobPresets.PresetSlot findPresetByName(String name) {
        BuffMobsConfig.MobPresets.PresetSlot[] presets = {
                BuffMobsConfig.MobPresets.preset1,
                BuffMobsConfig.MobPresets.preset2,
                BuffMobsConfig.MobPresets.preset3,
                BuffMobsConfig.MobPresets.preset4,
                BuffMobsConfig.MobPresets.preset5
        };

        for (BuffMobsConfig.MobPresets.PresetSlot preset : presets) {
            if (preset.presetName.get().equals(name)) {
                return preset;
            }
        }

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