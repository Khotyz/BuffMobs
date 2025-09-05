package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.effects.BuffMobEffects;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.Identifier;

public class MobBuffUtil {
    private static final Identifier HEALTH_ID = Identifier.of(BuffMobsMod.MOD_ID, "health_buff");
    private static final Identifier DAMAGE_ID = Identifier.of(BuffMobsMod.MOD_ID, "damage_buff");
    private static final Identifier ATTACK_SPEED_ID = Identifier.of(BuffMobsMod.MOD_ID, "attack_speed_buff");
    private static final Identifier MOVEMENT_SPEED_ID = Identifier.of(BuffMobsMod.MOD_ID, "movement_speed_buff");
    private static final Identifier ARMOR_ID = Identifier.of(BuffMobsMod.MOD_ID, "armor_buff");
    private static final Identifier ARMOR_TOUGHNESS_ID = Identifier.of(BuffMobsMod.MOD_ID, "armor_toughness_buff");

    public static void applyBuffs(MobEntity mob) {
        if (mob == null || !mob.isAlive()) return;

        try {
            double dayMultiplier = calculateDayMultiplier(mob.getWorld().getTimeOfDay());
            applyAttributeBuffs(mob, dayMultiplier);
            applyPotionEffects(mob);
            applyCustomVisualEffects(mob, dayMultiplier);
            mob.setHealth(mob.getMaxHealth());
        } catch (Exception e) {
            BuffMobsMod.LOGGER.error("Error applying buffs to mob", e);
        }
    }

    private static void applyCustomVisualEffects(MobEntity mob, double dayMultiplier) {
        if (!BuffMobsConfig.showVisualEffects()) return;

        try {
            int duration = BuffMobsConfig.getEffectDuration();
            int effectDuration = duration == -1 ? StatusEffectInstance.INFINITE : duration * 20;

            double healthMult = DimensionScalingUtil.getEffectiveHealthMultiplier(mob) * dayMultiplier;
            if (healthMult > 1.0) {
                int amplifier = Math.min(9, Math.max(0, (int) Math.floor(healthMult - 1.0)));
                mob.addStatusEffect(new StatusEffectInstance(
                        net.minecraft.registry.Registries.STATUS_EFFECT.getEntry(BuffMobEffects.ENHANCED_VITALITY),
                        effectDuration, amplifier, false, true, true
                ));
            }

            double attackSpeedMult = DimensionScalingUtil.getEffectiveAttackSpeedMultiplier(mob) * dayMultiplier;
            if (attackSpeedMult > 1.0) {
                int amplifier = Math.min(9, Math.max(0, (int) Math.floor(attackSpeedMult - 1.0)));
                mob.addStatusEffect(new StatusEffectInstance(
                        net.minecraft.registry.Registries.STATUS_EFFECT.getEntry(BuffMobEffects.COMBAT_FURY),
                        effectDuration, amplifier, false, true, true
                ));
            }

            double damageMult = DimensionScalingUtil.getEffectiveDamageMultiplier(mob) * dayMultiplier;
            if (damageMult > 1.0) {
                int amplifier = Math.min(9, Math.max(0, (int) Math.floor(damageMult - 1.0)));
                mob.addStatusEffect(new StatusEffectInstance(
                        net.minecraft.registry.Registries.STATUS_EFFECT.getEntry(BuffMobEffects.DESTRUCTIVE_POWER),
                        effectDuration, amplifier, false, true, true
                ));
            }
        } catch (Exception e) {
            BuffMobsMod.LOGGER.error("Error applying visual effects to mob", e);
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
        // Health
        double healthMult = DimensionScalingUtil.getEffectiveHealthMultiplier(mob);
        if (healthMult > 1.0) {
            applyAttributeModifier(mob, EntityAttributes.GENERIC_MAX_HEALTH, HEALTH_ID,
                    (healthMult * dayMultiplier) - 1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }

        // Damage
        double damageMult = DimensionScalingUtil.getEffectiveDamageMultiplier(mob);
        if (damageMult > 1.0) {
            applyAttributeModifier(mob, EntityAttributes.GENERIC_ATTACK_DAMAGE, DAMAGE_ID,
                    (damageMult * dayMultiplier) - 1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }

        // Movement Speed (separate from attack speed)
        double movementSpeedMult = DimensionScalingUtil.getEffectiveMovementSpeedMultiplier(mob);
        if (movementSpeedMult > 1.0) {
            applyAttributeModifier(mob, EntityAttributes.GENERIC_MOVEMENT_SPEED, MOVEMENT_SPEED_ID,
                    (movementSpeedMult * dayMultiplier) - 1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }

        // Attack Speed (separate from movement speed)
        double attackSpeedMult = DimensionScalingUtil.getEffectiveAttackSpeedMultiplier(mob);
        if (attackSpeedMult > 1.0) {
            applyAttributeModifier(mob, EntityAttributes.GENERIC_ATTACK_SPEED, ATTACK_SPEED_ID,
                    (attackSpeedMult * dayMultiplier) - 1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }

        // Armor
        double armorValue = DimensionScalingUtil.getEffectiveArmorAddition(mob) * dayMultiplier;
        if (armorValue > 0.0) {
            applyAttributeModifier(mob, EntityAttributes.GENERIC_ARMOR, ARMOR_ID,
                    armorValue, EntityAttributeModifier.Operation.ADD_VALUE);
        }

        // Armor Toughness
        double toughnessValue = DimensionScalingUtil.getEffectiveArmorToughnessAddition(mob) * dayMultiplier;
        if (toughnessValue > 0.0) {
            applyAttributeModifier(mob, EntityAttributes.GENERIC_ARMOR_TOUGHNESS, ARMOR_TOUGHNESS_ID,
                    toughnessValue, EntityAttributeModifier.Operation.ADD_VALUE);
        }
    }

    private static void applyAttributeModifier(MobEntity mob,
                                               net.minecraft.registry.entry.RegistryEntry<net.minecraft.entity.attribute.EntityAttribute> attribute,
                                               Identifier id, double value, EntityAttributeModifier.Operation operation) {

        try {
            EntityAttributeInstance instance = mob.getAttributeInstance(attribute);
            if (instance != null && Math.abs(value) > 0.001) {
                instance.removeModifier(id);
                instance.addPersistentModifier(new EntityAttributeModifier(id, value, operation));
            }
        } catch (Exception e) {
            BuffMobsMod.LOGGER.error("Error applying attribute modifier", e);
        }
    }

    private static void applyPotionEffects(MobEntity mob) {
        try {
            int duration = BuffMobsConfig.getEffectDuration();
            int effectDuration = duration == -1 ? StatusEffectInstance.INFINITE : duration * 20;
            boolean showParticles = BuffMobsConfig.showVisualEffects();

            int strengthAmp = BuffMobsConfig.getStrengthAmplifier();
            if (strengthAmp > 0) {
                mob.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.STRENGTH, effectDuration, strengthAmp - 1,
                        false, showParticles, showParticles
                ));
            }

            int resistanceAmp = BuffMobsConfig.getResistanceAmplifier();
            if (resistanceAmp > 0) {
                mob.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.RESISTANCE, effectDuration, resistanceAmp - 1,
                        false, showParticles, showParticles
                ));
            }

            int regenAmp = BuffMobsConfig.getRegenerationAmplifier();
            if (regenAmp > 0) {
                RegenHandler.startCustomRegen(mob, regenAmp);

                if (!isUndeadMob(mob)) {
                    mob.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.REGENERATION, effectDuration, regenAmp - 1,
                            false, showParticles, showParticles
                    ));
                }
            }
        } catch (Exception e) {
            BuffMobsMod.LOGGER.error("Error applying potion effects", e);
        }
    }

    private static boolean isUndeadMob(MobEntity mob) {
        if (mob instanceof AbstractSkeletonEntity ||
                mob instanceof ZombieEntity ||
                mob instanceof ZombifiedPiglinEntity ||
                mob instanceof DrownedEntity ||
                mob instanceof HuskEntity ||
                mob instanceof StrayEntity ||
                mob instanceof WitherEntity ||
                mob instanceof PhantomEntity ||
                mob instanceof WitherSkeletonEntity) {
            return true;
        }

        String mobName = mob.getType().toString().toLowerCase();
        return mobName.contains("zombie") ||
                mobName.contains("skeleton") ||
                mobName.contains("undead") ||
                mobName.contains("wither") ||
                mobName.contains("phantom") ||
                mobName.contains("wraith") ||
                mobName.contains("ghost") ||
                mobName.contains("spirit") ||
                mobName.contains("lich") ||
                mobName.contains("mummy") ||
                mobName.contains("draugr") ||
                mobName.contains("revenant") ||
                mobName.contains("bone") ||
                mobName.contains("skull");
    }
}