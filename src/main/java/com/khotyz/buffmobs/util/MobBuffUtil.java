package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.config.BuffMobsConfig;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import com.khotyz.buffmobs.BuffMobsMod;

public class MobBuffUtil {
    private static final Identifier HEALTH_ID = Identifier.of(BuffMobsMod.MOD_ID, "health_buff");
    private static final Identifier DAMAGE_ID = Identifier.of(BuffMobsMod.MOD_ID, "damage_buff");
    private static final Identifier SPEED_ID = Identifier.of(BuffMobsMod.MOD_ID, "speed_buff");
    private static final Identifier ATTACK_SPEED_ID = Identifier.of(BuffMobsMod.MOD_ID, "attack_speed_buff");
    private static final Identifier ARMOR_ID = Identifier.of(BuffMobsMod.MOD_ID, "armor_buff");
    private static final Identifier ARMOR_TOUGHNESS_ID = Identifier.of(BuffMobsMod.MOD_ID, "armor_toughness_buff");

    public static void applyBuffs(MobEntity mob) {
        if (mob == null || !mob.isAlive()) return;

        try {
            double dayMultiplier = calculateDayMultiplier(mob.getWorld().getTimeOfDay());

            applyAttributeBuffs(mob, dayMultiplier);
            applyPotionEffects(mob);

            // Heal to max health after applying buffs
            mob.setHealth(mob.getMaxHealth());
        } catch (Exception e) {
            BuffMobsMod.LOGGER.error("Error applying buffs to mob", e);
        }
    }

    public static boolean isDayScalingEnabled() {
        return BuffMobsConfig.isDayScalingEnabled();
    }

    public static double getDayMultiplier(long worldTime) {
        return calculateDayMultiplier(worldTime);
    }

    private static double calculateDayMultiplier(long worldTime) {
        if (!BuffMobsConfig.isDayScalingEnabled()) {
            return 1.0;
        }

        long days = worldTime / 24000L;
        long intervals = days / BuffMobsConfig.getDayScalingInterval();

        double multiplier = 1.0 + (intervals * BuffMobsConfig.getDayScalingMultiplier());
        return Math.min(multiplier, BuffMobsConfig.getDayScalingMax());
    }

    private static void applyAttributeBuffs(MobEntity mob, double dayMultiplier) {
        double healthMult = BuffMobsConfig.getHealthMultiplier();
        if (healthMult > 1.0) {
            applyAttributeModifier(mob, EntityAttributes.GENERIC_MAX_HEALTH, HEALTH_ID,
                    (healthMult * dayMultiplier) - 1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }

        double damageMult = BuffMobsConfig.getDamageMultiplier();
        if (damageMult > 1.0) {
            applyAttributeModifier(mob, EntityAttributes.GENERIC_ATTACK_DAMAGE, DAMAGE_ID,
                    (damageMult * dayMultiplier) - 1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }

        double speedMult = BuffMobsConfig.getSpeedMultiplier();
        if (speedMult > 1.0) {
            applyAttributeModifier(mob, EntityAttributes.GENERIC_MOVEMENT_SPEED, SPEED_ID,
                    (speedMult * dayMultiplier) - 1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }

        double attackSpeedMult = BuffMobsConfig.getAttackSpeedMultiplier();
        if (attackSpeedMult > 1.0) {
            applyAttributeModifier(mob, EntityAttributes.GENERIC_ATTACK_SPEED, ATTACK_SPEED_ID,
                    (attackSpeedMult * dayMultiplier) - 1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }

        double armorValue = BuffMobsConfig.getArmorAddition() * dayMultiplier;
        if (armorValue > 0.0) {
            applyAttributeModifier(mob, EntityAttributes.GENERIC_ARMOR, ARMOR_ID,
                    armorValue, EntityAttributeModifier.Operation.ADD_VALUE);
        }

        double toughnessValue = BuffMobsConfig.getArmorToughnessAddition() * dayMultiplier;
        if (toughnessValue > 0.0) {
            applyAttributeModifier(mob, EntityAttributes.GENERIC_ARMOR_TOUGHNESS, ARMOR_TOUGHNESS_ID,
                    toughnessValue, EntityAttributeModifier.Operation.ADD_VALUE);
        }
    }

    private static void applyAttributeModifier(MobEntity mob,
                                               net.minecraft.registry.entry.RegistryEntry<net.minecraft.entity.attribute.EntityAttribute> attribute,
                                               Identifier id, double value, EntityAttributeModifier.Operation operation) {

        EntityAttributeInstance instance = mob.getAttributeInstance(attribute);
        if (instance != null && Math.abs(value) > 0.001) {
            instance.removeModifier(id);
            instance.addPersistentModifier(new EntityAttributeModifier(id, value, operation));
        }
    }

    private static void applyPotionEffects(MobEntity mob) {
        int duration = BuffMobsConfig.getEffectDuration();
        int effectDuration = duration == -1 ? StatusEffectInstance.INFINITE : duration * 20;
        boolean showParticles = BuffMobsConfig.showVisualEffects();

        int strengthAmp = BuffMobsConfig.getStrengthAmplifier();
        if (strengthAmp > 0) {
            mob.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH,
                    effectDuration, strengthAmp - 1, false, showParticles, showParticles));
        }

        int speedAmp = BuffMobsConfig.getSpeedAmplifier();
        if (speedAmp > 0) {
            mob.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED,
                    effectDuration, speedAmp - 1, false, showParticles, showParticles));
        }

        int resistanceAmp = BuffMobsConfig.getResistanceAmplifier();
        if (resistanceAmp > 0) {
            mob.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE,
                    effectDuration, resistanceAmp - 1, false, showParticles, showParticles));
        }

        int regenAmp = BuffMobsConfig.getRegenerationAmplifier();
        if (regenAmp > 0) {
            mob.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION,
                    effectDuration, regenAmp - 1, false, showParticles, showParticles));
        }
    }
}