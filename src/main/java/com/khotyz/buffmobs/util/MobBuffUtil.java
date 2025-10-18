package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.util.Identifier;

import java.util.List;

public class MobBuffUtil {

    private static final Identifier HEALTH_MOD_ID = Identifier.of(BuffMobsMod.MOD_ID, "health");
    private static final Identifier DAMAGE_MOD_ID = Identifier.of(BuffMobsMod.MOD_ID, "damage");
    private static final Identifier SPEED_MOD_ID = Identifier.of(BuffMobsMod.MOD_ID, "speed");
    private static final Identifier ATTACK_SPEED_MOD_ID = Identifier.of(BuffMobsMod.MOD_ID, "attack_speed");
    private static final Identifier ARMOR_MOD_ID = Identifier.of(BuffMobsMod.MOD_ID, "armor");
    private static final Identifier TOUGHNESS_MOD_ID = Identifier.of(BuffMobsMod.MOD_ID, "toughness");

    public static void applyBuffs(MobEntity mob) {
        if (!BuffMobsMod.CONFIG.enabled || !isValidMob(mob)) {
            return;
        }

        double dayMultiplier = getDayMultiplier(mob.getEntityWorld().getTimeOfDay());
        DimensionMultipliers dimMultipliers = getDimensionMultipliers(mob);

        BuffMobsMod.LOGGER.debug("Applying buffs to {} - Day mult: {}, Dim mult: health={}, damage={}",
                mob.getType(), dayMultiplier, dimMultipliers.health, dimMultipliers.damage);

        applyAttributeModifiers(mob, dayMultiplier, dimMultipliers);
        applyVanillaStatusEffects(mob);

        mob.setHealth(mob.getMaxHealth());
    }

    public static double getDayMultiplier(long worldTime) {
        if (!BuffMobsMod.CONFIG.dayScaling.enabled) return 1.0;

        long days = worldTime / 24000L;
        long intervals = days / BuffMobsMod.CONFIG.dayScaling.interval;

        double multiplier = 1.0 + (intervals * BuffMobsMod.CONFIG.dayScaling.multiplier);
        return Math.min(multiplier, BuffMobsMod.CONFIG.dayScaling.maxMultiplier);
    }

    public static DimensionMultipliers getDimensionMultipliers(MobEntity mob) {
        String dimensionName = mob.getEntityWorld().getRegistryKey().getValue().toString();

        BuffMobsConfig.DimensionScaling.DimensionSlot[] slots = {
                BuffMobsMod.CONFIG.dimensionScaling.slot1,
                BuffMobsMod.CONFIG.dimensionScaling.slot2,
                BuffMobsMod.CONFIG.dimensionScaling.slot3,
                BuffMobsMod.CONFIG.dimensionScaling.slot4,
                BuffMobsMod.CONFIG.dimensionScaling.slot5
        };

        for (BuffMobsConfig.DimensionScaling.DimensionSlot slot : slots) {
            if (!slot.dimensionName.isEmpty() && slot.dimensionName.equals(dimensionName)) {
                DimensionMultipliers multipliers = new DimensionMultipliers(
                        slot.healthMultiplier / 100.0,
                        slot.damageMultiplier / 100.0,
                        slot.speedMultiplier / 100.0,
                        slot.attackSpeedMultiplier / 100.0,
                        slot.armorAddition,
                        slot.armorToughnessAddition
                );

                BuffMobsMod.LOGGER.debug("Dimension {} has custom scaling: health={}, damage={}",
                        dimensionName, multipliers.health, multipliers.damage);

                return multipliers;
            }
        }

        BuffMobsMod.LOGGER.debug("Dimension {} using default scaling", dimensionName);
        return new DimensionMultipliers(1.0, 1.0, 1.0, 1.0, 0.0, 0.0);
    }

    public static void refreshInfiniteEffects(MobEntity mob) {
        if (BuffMobsMod.CONFIG.effects.duration != -1 || !isValidMob(mob)) return;

        boolean showParticles = BuffMobsMod.CONFIG.visualEffects;
        int infiniteDuration = StatusEffectInstance.INFINITE;

        refreshEffect(mob, StatusEffects.STRENGTH,
                BuffMobsMod.CONFIG.effects.strengthAmplifier, infiniteDuration, showParticles);
        refreshEffect(mob, StatusEffects.RESISTANCE,
                BuffMobsMod.CONFIG.effects.resistanceAmplifier, infiniteDuration, showParticles);

        if (BuffMobsMod.CONFIG.effects.regenerationAmplifier > 0 &&
                !mob.getType().isIn(EntityTypeTags.UNDEAD)) {
            refreshEffect(mob, StatusEffects.REGENERATION,
                    BuffMobsMod.CONFIG.effects.regenerationAmplifier, infiniteDuration, showParticles);
        }
    }

    public static void applyPoisonToPlayer(PlayerEntity player, int duration) {
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.POISON, duration * 20, 0));
    }

    public static void applySlownessToPlayer(PlayerEntity player, int duration) {
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SLOWNESS, duration * 20, 0));
    }

    public static void applyWitherToPlayer(PlayerEntity player, int duration) {
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.WITHER, duration * 20, 0));
    }

    public static boolean isValidMob(MobEntity mob) {
        if (mob.isRemoved() || !mob.isAlive()) {
            return false;
        }

        if (mob instanceof TameableEntity tameable && tameable.isTamed()) {
            return false;
        }

        boolean isHostileOrNeutral = mob instanceof HostileEntity ||
                mob.getType().isIn(EntityTypeTags.RAIDERS) ||
                mob.getType().isIn(EntityTypeTags.SKELETONS) ||
                mob.getType().isIn(EntityTypeTags.ZOMBIES) ||
                isNeutralMob(mob) ||
                hasAttackDamageAttribute(mob);

        if (!isHostileOrNeutral) {
            if (BuffMobsMod.LOGGER.isDebugEnabled()) {
                BuffMobsMod.LOGGER.debug("Mob {} is not hostile/neutral",
                        Registries.ENTITY_TYPE.getId(mob.getType()));
            }
            return false;
        }

        String mobId = Registries.ENTITY_TYPE.getId(mob.getType()).toString();
        String modId = Registries.ENTITY_TYPE.getId(mob.getType()).getNamespace();
        String dimensionName = mob.getEntityWorld().getRegistryKey().getValue().toString();

        boolean validDim = isValidDimension(dimensionName);
        boolean validMod = isValidModId(modId);
        boolean validMob = isValidMobId(mobId);

        if (!validDim && BuffMobsMod.LOGGER.isDebugEnabled()) {
            BuffMobsMod.LOGGER.debug("Mob {} filtered by dimension: {}", mobId, dimensionName);
        }
        if (!validMod && BuffMobsMod.LOGGER.isDebugEnabled()) {
            BuffMobsMod.LOGGER.debug("Mob {} filtered by mod: {}", mobId, modId);
        }
        if (!validMob && BuffMobsMod.LOGGER.isDebugEnabled()) {
            BuffMobsMod.LOGGER.debug("Mob {} filtered by mob filter", mobId);
        }

        return validDim && validMod && validMob;
    }

    private static boolean hasAttackDamageAttribute(MobEntity mob) {
        EntityAttributeInstance attackAttr = mob.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE);
        return attackAttr != null && attackAttr.getBaseValue() > 0.0;
    }

    private static boolean isNeutralMob(MobEntity mob) {
        String mobId = Registries.ENTITY_TYPE.getId(mob.getType()).toString();

        return switch (mobId) {
            case "minecraft:enderman", "minecraft:piglin", "minecraft:zombified_piglin",
                 "minecraft:iron_golem", "minecraft:spider", "minecraft:cave_spider",
                 "minecraft:wolf", "minecraft:polar_bear", "minecraft:bee",
                 "minecraft:panda", "minecraft:llama", "minecraft:dolphin",
                 "minecraft:trader_llama", "minecraft:slime", "minecraft:magma_cube" -> true;
            default -> false;
        };
    }

    private static void applyAttributeModifiers(MobEntity mob, double dayMultiplier,
                                                DimensionMultipliers dimMultipliers) {
        applyHealthModifier(mob, dayMultiplier, dimMultipliers);
        applyDamageModifier(mob, dayMultiplier, dimMultipliers);
        applySpeedModifier(mob, dayMultiplier, dimMultipliers);
        applyAttackSpeedModifier(mob, dayMultiplier, dimMultipliers);
        applyArmorModifier(mob, dayMultiplier, dimMultipliers);
        applyToughnessModifier(mob, dayMultiplier, dimMultipliers);
    }

    private static void applyHealthModifier(MobEntity mob, double dayMult, DimensionMultipliers dimMult) {
        double mult = calculateFinalMultiplier(
                BuffMobsMod.CONFIG.attributes.healthMultiplier, dimMult.health, dayMult);

        if (mult > 1.0) {
            EntityAttributeInstance attr = mob.getAttributeInstance(EntityAttributes.MAX_HEALTH);
            if (attr != null) {
                attr.removeModifier(HEALTH_MOD_ID);
                attr.addPersistentModifier(new EntityAttributeModifier(
                        HEALTH_MOD_ID, mult - 1.0,
                        EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));

                BuffMobsMod.LOGGER.debug("Applied health mult {} to {}", mult, mob.getType());
            }
        }
    }

    private static void applyDamageModifier(MobEntity mob, double dayMult, DimensionMultipliers dimMult) {
        double mult = calculateFinalMultiplier(
                BuffMobsMod.CONFIG.attributes.damageMultiplier, dimMult.damage, dayMult);

        if (mult > 1.0) {
            EntityAttributeInstance attr = mob.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE);
            if (attr != null) {
                attr.removeModifier(DAMAGE_MOD_ID);
                attr.addPersistentModifier(new EntityAttributeModifier(
                        DAMAGE_MOD_ID, mult - 1.0,
                        EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }
        }
    }

    private static void applySpeedModifier(MobEntity mob, double dayMult, DimensionMultipliers dimMult) {
        double mult = calculateFinalMultiplier(
                BuffMobsMod.CONFIG.attributes.speedMultiplier, dimMult.speed, dayMult);

        if (mult > 1.0) {
            EntityAttributeInstance attr = mob.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
            if (attr != null) {
                attr.removeModifier(SPEED_MOD_ID);
                double cappedMult = Math.min(mult, 2.0);
                double bonus = (cappedMult - 1.0) * 0.3;
                attr.addPersistentModifier(new EntityAttributeModifier(
                        SPEED_MOD_ID, bonus,
                        EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }
        }
    }

    private static void applyAttackSpeedModifier(MobEntity mob, double dayMult, DimensionMultipliers dimMult) {
        double mult = calculateFinalMultiplier(
                BuffMobsMod.CONFIG.attributes.attackSpeedMultiplier, dimMult.attackSpeed, dayMult);

        if (mult > 1.0) {
            EntityAttributeInstance attr = mob.getAttributeInstance(EntityAttributes.ATTACK_SPEED);
            if (attr != null) {
                attr.removeModifier(ATTACK_SPEED_MOD_ID);
                double cappedMult = Math.min(mult, 2.5);
                attr.addPersistentModifier(new EntityAttributeModifier(
                        ATTACK_SPEED_MOD_ID, cappedMult - 1.0,
                        EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }
        }
    }

    private static void applyArmorModifier(MobEntity mob, double dayMult, DimensionMultipliers dimMult) {
        double add = calculateFinalAddition(
                BuffMobsMod.CONFIG.attributes.armorAddition, dimMult.armor, dayMult);

        if (add > 0.0) {
            EntityAttributeInstance attr = mob.getAttributeInstance(EntityAttributes.ARMOR);
            if (attr != null) {
                attr.removeModifier(ARMOR_MOD_ID);
                attr.addPersistentModifier(new EntityAttributeModifier(
                        ARMOR_MOD_ID, add, EntityAttributeModifier.Operation.ADD_VALUE));
            }
        }
    }

    private static void applyToughnessModifier(MobEntity mob, double dayMult, DimensionMultipliers dimMult) {
        double add = calculateFinalAddition(
                BuffMobsMod.CONFIG.attributes.armorToughnessAddition, dimMult.armorToughness, dayMult);

        if (add > 0.0) {
            EntityAttributeInstance attr = mob.getAttributeInstance(EntityAttributes.ARMOR_TOUGHNESS);
            if (attr != null) {
                attr.removeModifier(TOUGHNESS_MOD_ID);
                attr.addPersistentModifier(new EntityAttributeModifier(
                        TOUGHNESS_MOD_ID, add, EntityAttributeModifier.Operation.ADD_VALUE));
            }
        }
    }

    private static void applyVanillaStatusEffects(MobEntity mob) {
        int duration = BuffMobsMod.CONFIG.effects.duration == -1 ?
                StatusEffectInstance.INFINITE : BuffMobsMod.CONFIG.effects.duration * 20;
        boolean showParticles = BuffMobsMod.CONFIG.visualEffects;

        addEffect(mob, StatusEffects.STRENGTH,
                BuffMobsMod.CONFIG.effects.strengthAmplifier, duration, showParticles);
        addEffect(mob, StatusEffects.RESISTANCE,
                BuffMobsMod.CONFIG.effects.resistanceAmplifier, duration, showParticles);

        if (BuffMobsMod.CONFIG.effects.regenerationAmplifier > 0 &&
                !mob.getType().isIn(EntityTypeTags.UNDEAD)) {
            addEffect(mob, StatusEffects.REGENERATION,
                    BuffMobsMod.CONFIG.effects.regenerationAmplifier, duration, showParticles);
        }
    }

    private static void refreshEffect(MobEntity mob, RegistryEntry<StatusEffect> effect,
                                      int amplifier, int duration, boolean showParticles) {
        if (amplifier <= 0) return;

        StatusEffectInstance current = mob.getStatusEffect(effect);
        if (current == null || current.getDuration() < 1200) {
            mob.removeStatusEffect(effect);
            mob.addStatusEffect(new StatusEffectInstance(
                    effect, duration, amplifier - 1, false, showParticles, true));
        }
    }

    private static void addEffect(MobEntity mob, RegistryEntry<StatusEffect> effect,
                                  int amplifier, int duration, boolean showParticles) {
        if (amplifier > 0) {
            mob.addStatusEffect(new StatusEffectInstance(
                    effect, duration, amplifier - 1, false, showParticles, true));
        }
    }

    private static boolean isValidDimension(String dimensionName) {
        List<String> blacklist = BuffMobsMod.CONFIG.dimensionFilter.blacklist;
        if (blacklist.contains(dimensionName)) return false;

        if (BuffMobsMod.CONFIG.dimensionFilter.useWhitelist) {
            return BuffMobsMod.CONFIG.dimensionFilter.whitelist.contains(dimensionName);
        }

        return true;
    }

    private static boolean isValidModId(String modId) {
        List<String> blacklist = BuffMobsMod.CONFIG.modidFilter.blacklist;
        if (blacklist.contains(modId)) return false;

        if (BuffMobsMod.CONFIG.modidFilter.useWhitelist) {
            return BuffMobsMod.CONFIG.modidFilter.whitelist.contains(modId);
        }

        return true;
    }

    private static boolean isValidMobId(String mobId) {
        List<String> blacklist = BuffMobsMod.CONFIG.mobFilter.blacklist;
        if (blacklist.contains(mobId)) return false;

        if (BuffMobsMod.CONFIG.mobFilter.useWhitelist) {
            return BuffMobsMod.CONFIG.mobFilter.whitelist.contains(mobId);
        }

        return true;
    }

    public static double calculateFinalMultiplier(double baseMultiplier,
                                                  double dimensionMultiplier,
                                                  double dayMultiplier) {
        if (baseMultiplier > 1.0) {
            return baseMultiplier * dimensionMultiplier * dayMultiplier;
        } else if (dimensionMultiplier > 1.0) {
            return dimensionMultiplier * dayMultiplier;
        }
        return 1.0;
    }

    public static double calculateFinalAddition(double baseAddition,
                                                double dimensionAddition,
                                                double dayMultiplier) {
        if (baseAddition > 0.0) {
            return (baseAddition + dimensionAddition) * dayMultiplier;
        } else if (dimensionAddition > 0.0) {
            return dimensionAddition * dayMultiplier;
        }
        return 0.0;
    }

    public static class DimensionMultipliers {
        public final double health;
        public final double damage;
        public final double speed;
        public final double attackSpeed;
        public final double armor;
        public final double armorToughness;

        public DimensionMultipliers(double health, double damage, double speed,
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