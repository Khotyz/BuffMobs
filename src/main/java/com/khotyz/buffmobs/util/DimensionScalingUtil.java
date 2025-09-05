package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.config.BuffMobsConfig;
import net.minecraft.entity.mob.MobEntity;

public class DimensionScalingUtil {

    public static double getEffectiveHealthMultiplier(MobEntity mob) {
        String dimensionName = mob.getWorld().getRegistryKey().getValue().toString();
        BuffMobsConfig.ConfigData.DimensionScalingSettings.DimensionScaling dimensionScaling =
                BuffMobsConfig.getDimensionScaling(dimensionName);

        double baseMultiplier = BuffMobsConfig.getHealthMultiplier();

        if (dimensionScaling != null && !dimensionScaling.dimensionName.isEmpty()) {
            return baseMultiplier * dimensionScaling.healthMultiplier;
        }

        return baseMultiplier;
    }

    public static double getEffectiveDamageMultiplier(MobEntity mob) {
        String dimensionName = mob.getWorld().getRegistryKey().getValue().toString();
        BuffMobsConfig.ConfigData.DimensionScalingSettings.DimensionScaling dimensionScaling =
                BuffMobsConfig.getDimensionScaling(dimensionName);

        double baseMultiplier = BuffMobsConfig.getDamageMultiplier();

        if (dimensionScaling != null && !dimensionScaling.dimensionName.isEmpty()) {
            return baseMultiplier * dimensionScaling.damageMultiplier;
        }

        return baseMultiplier;
    }

    public static double getEffectiveMovementSpeedMultiplier(MobEntity mob) {
        String dimensionName = mob.getWorld().getRegistryKey().getValue().toString();
        BuffMobsConfig.ConfigData.DimensionScalingSettings.DimensionScaling dimensionScaling =
                BuffMobsConfig.getDimensionScaling(dimensionName);

        double baseMultiplier = BuffMobsConfig.getSpeedMultiplier();

        if (dimensionScaling != null && !dimensionScaling.dimensionName.isEmpty()) {
            return baseMultiplier * dimensionScaling.speedMultiplier;
        }

        return baseMultiplier;
    }

    public static double getEffectiveAttackSpeedMultiplier(MobEntity mob) {
        String dimensionName = mob.getWorld().getRegistryKey().getValue().toString();
        BuffMobsConfig.ConfigData.DimensionScalingSettings.DimensionScaling dimensionScaling =
                BuffMobsConfig.getDimensionScaling(dimensionName);

        double baseMultiplier = BuffMobsConfig.getAttackSpeedMultiplier();

        if (dimensionScaling != null && !dimensionScaling.dimensionName.isEmpty()) {
            return baseMultiplier * dimensionScaling.attackSpeedMultiplier;
        }

        return baseMultiplier;
    }

    public static double getEffectiveArmorAddition(MobEntity mob) {
        String dimensionName = mob.getWorld().getRegistryKey().getValue().toString();
        BuffMobsConfig.ConfigData.DimensionScalingSettings.DimensionScaling dimensionScaling =
                BuffMobsConfig.getDimensionScaling(dimensionName);

        double baseAddition = BuffMobsConfig.getArmorAddition();

        if (dimensionScaling != null && !dimensionScaling.dimensionName.isEmpty()) {
            return baseAddition + dimensionScaling.armorAddition;
        }

        return baseAddition;
    }

    public static double getEffectiveArmorToughnessAddition(MobEntity mob) {
        String dimensionName = mob.getWorld().getRegistryKey().getValue().toString();
        BuffMobsConfig.ConfigData.DimensionScalingSettings.DimensionScaling dimensionScaling =
                BuffMobsConfig.getDimensionScaling(dimensionName);

        double baseAddition = BuffMobsConfig.getArmorToughnessAddition();

        if (dimensionScaling != null && !dimensionScaling.dimensionName.isEmpty()) {
            return baseAddition + dimensionScaling.armorToughnessAddition;
        }

        return baseAddition;
    }
}