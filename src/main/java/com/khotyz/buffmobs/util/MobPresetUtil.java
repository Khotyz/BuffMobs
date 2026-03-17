package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Mob;

public class MobPresetUtil {

    public static PresetMultipliers getPresetForMob(Mob mob) {
        if (!BuffMobsConfig.INSTANCE.mobPresets.enabled.get()) return null;

        String mobId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        String presetName = null;

        for (String mapping : BuffMobsConfig.INSTANCE.mobPresets.mobMapping.get()) {
            String[] parts = mapping.split(":");
            if (parts.length >= 3) {
                String mappedMobId = parts[0] + ":" + parts[1];
                if (mappedMobId.equals(mobId)) {
                    presetName = parts[2];
                    break;
                }
            } else {
                BuffMobsMod.LOGGER.warn("Invalid preset mapping format: '{}' (expected modid:mob_id:preset_name)", mapping);
            }
        }

        if (presetName == null || presetName.isEmpty()) return null;

        BuffMobsConfig.MobPresets.PresetSlot preset = findPresetByName(presetName);
        if (preset == null || preset.presetName.get().isEmpty()) {
            BuffMobsMod.LOGGER.warn("Preset '{}' not found for mob '{}'", presetName, mobId);
            return null;
        }

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
                BuffMobsConfig.INSTANCE.mobPresets.preset1,
                BuffMobsConfig.INSTANCE.mobPresets.preset2,
                BuffMobsConfig.INSTANCE.mobPresets.preset3,
                BuffMobsConfig.INSTANCE.mobPresets.preset4,
                BuffMobsConfig.INSTANCE.mobPresets.preset5
        };
        for (BuffMobsConfig.MobPresets.PresetSlot preset : presets) {
            if (preset.presetName.get().equals(name)) return preset;
        }
        return null;
    }

    public static class PresetMultipliers {
        public final double health, damage, speed, attackSpeed, armor, armorToughness;

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
