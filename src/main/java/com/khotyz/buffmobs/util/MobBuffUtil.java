package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class MobBuffUtil {

    private static final ResourceLocation HEALTH_MOD_ID = ResourceLocation.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "health");
    private static final ResourceLocation DAMAGE_MOD_ID = ResourceLocation.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "damage");
    private static final ResourceLocation SPEED_MOD_ID = ResourceLocation.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "speed");
    private static final ResourceLocation ATTACK_SPEED_MOD_ID = ResourceLocation.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "attack_speed");
    private static final ResourceLocation ARMOR_MOD_ID = ResourceLocation.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "armor");
    private static final ResourceLocation TOUGHNESS_MOD_ID = ResourceLocation.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "toughness");

    public static void applyBuffs(Mob mob) {
        if (!BuffMobsConfig.enabled.get() || !isValidMob(mob)) {
            return;
        }

        double dayMultiplier = getDayMultiplier(mob.level().getDayTime());

        MobPresetUtil.PresetMultipliers presetMult = MobPresetUtil.getPresetForMob(mob);

        if (presetMult != null) {
            BuffMobsMod.LOGGER.debug("Applying preset buffs to {} - Day mult: {}, Preset: health={}, damage={}",
                    mob.getType(), dayMultiplier, presetMult.health, presetMult.damage);
            applyAttributeModifiersWithPreset(mob, dayMultiplier, presetMult);
        } else {
            DimensionMultipliers dimMultipliers = getDimensionMultipliers(mob);
            BuffMobsMod.LOGGER.debug("Applying standard buffs to {} - Day mult: {}, Dim mult: health={}, damage={}",
                    mob.getType(), dayMultiplier, dimMultipliers.health, dimMultipliers.damage);
            applyAttributeModifiers(mob, dayMultiplier, dimMultipliers);
        }

        applyVanillaStatusEffects(mob);
        mob.setHealth(mob.getMaxHealth());
    }

    public static double getDayMultiplier(long worldTime) {
        if (!BuffMobsConfig.DayScaling.enabled.get()) return 1.0;

        long days = worldTime / 24000L;
        long intervals = days / BuffMobsConfig.DayScaling.interval.get();

        double multiplier = 1.0 + (intervals * BuffMobsConfig.DayScaling.multiplier.get());
        return Math.min(multiplier, BuffMobsConfig.DayScaling.maxMultiplier.get());
    }

    public static DimensionMultipliers getDimensionMultipliers(Mob mob) {
        String dimensionName = mob.level().dimension().location().toString();

        BuffMobsConfig.DimensionScaling.DimensionSlot[] slots = {
                BuffMobsConfig.DimensionScaling.slot1,
                BuffMobsConfig.DimensionScaling.slot2,
                BuffMobsConfig.DimensionScaling.slot3,
                BuffMobsConfig.DimensionScaling.slot4,
                BuffMobsConfig.DimensionScaling.slot5
        };

        for (BuffMobsConfig.DimensionScaling.DimensionSlot slot : slots) {
            String slotDimName = slot.dimensionName.get();
            if (!slotDimName.isEmpty() && slotDimName.equals(dimensionName)) {
                DimensionMultipliers multipliers = new DimensionMultipliers(
                        slot.healthMultiplier.get() / 100.0,
                        slot.damageMultiplier.get() / 100.0,
                        slot.speedMultiplier.get() / 100.0,
                        slot.attackSpeedMultiplier.get() / 100.0,
                        slot.armorAddition.get(),
                        slot.armorToughnessAddition.get()
                );

                BuffMobsMod.LOGGER.debug("Dimension {} has custom scaling: health={}, damage={}",
                        dimensionName, multipliers.health, multipliers.damage);

                return multipliers;
            }
        }

        BuffMobsMod.LOGGER.debug("Dimension {} using default scaling", dimensionName);
        return new DimensionMultipliers(1.0, 1.0, 1.0, 1.0, 0.0, 0.0);
    }

    public static void refreshInfiniteEffects(Mob mob) {
        if (BuffMobsConfig.Effects.duration.get() != -1 || !isValidMob(mob)) return;

        boolean showParticles = BuffMobsConfig.visualEffects.get();
        int infiniteDuration = MobEffectInstance.INFINITE_DURATION;

        refreshEffect(mob, MobEffects.STRENGTH,
                BuffMobsConfig.Effects.strengthAmplifier.get(), infiniteDuration, showParticles);
        refreshEffect(mob, MobEffects.RESISTANCE,
                BuffMobsConfig.Effects.resistanceAmplifier.get(), infiniteDuration, showParticles);

        if (BuffMobsConfig.Effects.regenerationAmplifier.get() > 0 &&
                !mob.getType().is(EntityTypeTags.UNDEAD)) {
            refreshEffect(mob, MobEffects.REGENERATION,
                    BuffMobsConfig.Effects.regenerationAmplifier.get(), infiniteDuration, showParticles);
        }
    }

    public static void applyPoisonToPlayer(Player player, int duration) {
        player.addEffect(new MobEffectInstance(MobEffects.POISON, duration * 20, 0));
    }

    public static void applySlownessToPlayer(Player player, int duration) {
        player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, duration * 20, 0));
    }

    public static void applyWitherToPlayer(Player player, int duration) {
        player.addEffect(new MobEffectInstance(MobEffects.WITHER, duration * 20, 0));
    }

    public static boolean isValidMob(Mob mob) {
        if (mob.isRemoved() || !mob.isAlive()) {
            return false;
        }

        if (mob instanceof TamableAnimal tameable && tameable.isTame()) {
            return false;
        }

        boolean isHostileOrNeutral = mob instanceof Enemy ||
                mob.getType().is(EntityTypeTags.RAIDERS) ||
                mob.getType().is(EntityTypeTags.SKELETONS) ||
                isNeutralMob(mob) ||
                hasAttackDamageAttribute(mob);

        if (!isHostileOrNeutral) {
            if (BuffMobsMod.LOGGER.isDebugEnabled()) {
                BuffMobsMod.LOGGER.debug("Mob {} is not hostile/neutral",
                        mob.getType().toString());
            }
            return false;
        }

        String mobId = mob.getType().toString();
        String modId = ResourceLocation.parse(mobId).getNamespace();
        String dimensionName = mob.level().dimension().location().toString();

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

    private static boolean hasAttackDamageAttribute(Mob mob) {
        AttributeInstance attackAttr = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        return attackAttr != null && attackAttr.getBaseValue() > 0.0;
    }

    private static boolean isNeutralMob(Mob mob) {
        String mobId = mob.getType().toString();

        return switch (mobId) {
            case "minecraft:enderman", "minecraft:piglin", "minecraft:zombified_piglin",
                 "minecraft:iron_golem", "minecraft:spider", "minecraft:cave_spider",
                 "minecraft:wolf", "minecraft:polar_bear", "minecraft:bee",
                 "minecraft:panda", "minecraft:llama", "minecraft:dolphin",
                 "minecraft:trader_llama", "minecraft:slime", "minecraft:magma_cube" -> true;
            default -> false;
        };
    }

    private static void applyAttributeModifiers(Mob mob, double dayMultiplier,
                                                DimensionMultipliers dimMultipliers) {
        applyHealthModifier(mob, dayMultiplier, dimMultipliers.health,
                BuffMobsConfig.Attributes.healthMultiplier.get());
        applyDamageModifier(mob, dayMultiplier, dimMultipliers.damage,
                BuffMobsConfig.Attributes.damageMultiplier.get());
        applySpeedModifier(mob, dayMultiplier, dimMultipliers.speed,
                BuffMobsConfig.Attributes.speedMultiplier.get());
        applyAttackSpeedModifier(mob, dayMultiplier, dimMultipliers.attackSpeed,
                BuffMobsConfig.Attributes.attackSpeedMultiplier.get());
        applyArmorModifier(mob, dayMultiplier, dimMultipliers.armor,
                BuffMobsConfig.Attributes.armorAddition.get());
        applyToughnessModifier(mob, dayMultiplier, dimMultipliers.armorToughness,
                BuffMobsConfig.Attributes.armorToughnessAddition.get());
    }

    private static void applyAttributeModifiersWithPreset(Mob mob, double dayMultiplier,
                                                          MobPresetUtil.PresetMultipliers presetMult) {
        applyHealthModifier(mob, dayMultiplier, 1.0, presetMult.health);
        applyDamageModifier(mob, dayMultiplier, 1.0, presetMult.damage);
        applySpeedModifier(mob, dayMultiplier, 1.0, presetMult.speed);
        applyAttackSpeedModifier(mob, dayMultiplier, 1.0, presetMult.attackSpeed);
        applyArmorModifier(mob, dayMultiplier, 0.0, presetMult.armor);
        applyToughnessModifier(mob, dayMultiplier, 0.0, presetMult.armorToughness);
    }

    private static void applyHealthModifier(Mob mob, double dayMult, double dimMult, double baseMult) {
        double mult = calculateFinalMultiplier(baseMult, dimMult, dayMult);

        AttributeInstance attr = mob.getAttribute(Attributes.MAX_HEALTH);
        if (attr != null) {
            attr.removeModifier(HEALTH_MOD_ID);
            if (mult > 1.0) {
                attr.addPermanentModifier(new AttributeModifier(
                        HEALTH_MOD_ID, mult - 1.0,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

                BuffMobsMod.LOGGER.debug("Applied health mult {} to {}", mult, mob.getType());
            }
        }
    }

    private static void applyDamageModifier(Mob mob, double dayMult, double dimMult, double baseMult) {
        double mult = calculateFinalMultiplier(baseMult, dimMult, dayMult);

        AttributeInstance attr = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attr != null) {
            attr.removeModifier(DAMAGE_MOD_ID);
            if (mult > 1.0) {
                attr.addPermanentModifier(new AttributeModifier(
                        DAMAGE_MOD_ID, mult - 1.0,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }
        }
    }

    private static void applySpeedModifier(Mob mob, double dayMult, double dimMult, double baseMult) {
        double mult = calculateFinalMultiplier(baseMult, dimMult, dayMult);

        AttributeInstance attr = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null) {
            attr.removeModifier(SPEED_MOD_ID);
            if (mult > 1.0) {
                double cappedMult = Math.min(mult, 2.0);
                double bonus = (cappedMult - 1.0) * 0.3;
                attr.addPermanentModifier(new AttributeModifier(
                        SPEED_MOD_ID, bonus,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }
        }
    }

    private static void applyAttackSpeedModifier(Mob mob, double dayMult, double dimMult, double baseMult) {
        double mult = calculateFinalMultiplier(baseMult, dimMult, dayMult);

        AttributeInstance attr = mob.getAttribute(Attributes.ATTACK_SPEED);
        if (attr != null) {
            attr.removeModifier(ATTACK_SPEED_MOD_ID);
            if (mult > 1.0) {
                double cappedMult = Math.min(mult, 2.5);
                attr.addPermanentModifier(new AttributeModifier(
                        ATTACK_SPEED_MOD_ID, cappedMult - 1.0,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }
        }
    }

    private static void applyArmorModifier(Mob mob, double dayMult, double dimAdd, double baseAdd) {
        double add = calculateFinalAddition(baseAdd, dimAdd, dayMult);

        AttributeInstance attr = mob.getAttribute(Attributes.ARMOR);
        if (attr != null) {
            attr.removeModifier(ARMOR_MOD_ID);
            if (add > 0.0) {
                attr.addPermanentModifier(new AttributeModifier(
                        ARMOR_MOD_ID, add, AttributeModifier.Operation.ADD_VALUE));
            }
        }
    }

    private static void applyToughnessModifier(Mob mob, double dayMult, double dimAdd, double baseAdd) {
        double add = calculateFinalAddition(baseAdd, dimAdd, dayMult);

        AttributeInstance attr = mob.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (attr != null) {
            attr.removeModifier(TOUGHNESS_MOD_ID);
            if (add > 0.0) {
                attr.addPermanentModifier(new AttributeModifier(
                        TOUGHNESS_MOD_ID, add, AttributeModifier.Operation.ADD_VALUE));
            }
        }
    }

    private static void applyVanillaStatusEffects(Mob mob) {
        int duration = BuffMobsConfig.Effects.duration.get() == -1 ?
                MobEffectInstance.INFINITE_DURATION : BuffMobsConfig.Effects.duration.get() * 20;
        boolean showParticles = BuffMobsConfig.visualEffects.get();

        addEffect(mob, MobEffects.STRENGTH,
                BuffMobsConfig.Effects.strengthAmplifier.get(), duration, showParticles);
        addEffect(mob, MobEffects.RESISTANCE,
                BuffMobsConfig.Effects.resistanceAmplifier.get(), duration, showParticles);

        if (BuffMobsConfig.Effects.regenerationAmplifier.get() > 0 &&
                !mob.getType().is(EntityTypeTags.UNDEAD)) {
            addEffect(mob, MobEffects.REGENERATION,
                    BuffMobsConfig.Effects.regenerationAmplifier.get(), duration, showParticles);
        }
    }

    private static void refreshEffect(Mob mob, Holder<MobEffect> effect,
                                      int amplifier, int duration, boolean showParticles) {
        if (amplifier <= 0) return;

        MobEffectInstance current = mob.getEffect(effect);
        if (current == null || current.getDuration() < 1200) {
            mob.removeEffect(effect);
            mob.addEffect(new MobEffectInstance(
                    effect, duration, amplifier - 1, false, showParticles, true));
        }
    }

    private static void addEffect(Mob mob, Holder<MobEffect> effect,
                                  int amplifier, int duration, boolean showParticles) {
        if (amplifier > 0) {
            mob.addEffect(new MobEffectInstance(
                    effect, duration, amplifier - 1, false, showParticles, true));
        }
    }

    private static boolean isValidDimension(String dimensionName) {
        List<? extends String> blacklist = BuffMobsConfig.DimensionFilter.blacklist.get();
        if (blacklist.contains(dimensionName)) return false;

        if (BuffMobsConfig.DimensionFilter.useWhitelist.get()) {
            return BuffMobsConfig.DimensionFilter.whitelist.get().contains(dimensionName);
        }

        return true;
    }

    private static boolean isValidModId(String modId) {
        List<? extends String> blacklist = BuffMobsConfig.ModIdFilter.blacklist.get();
        if (blacklist.contains(modId)) return false;

        if (BuffMobsConfig.ModIdFilter.useWhitelist.get()) {
            return BuffMobsConfig.ModIdFilter.whitelist.get().contains(modId);
        }

        return true;
    }

    private static boolean isValidMobId(String mobId) {
        List<? extends String> blacklist = BuffMobsConfig.MobFilter.blacklist.get();
        if (blacklist.contains(mobId)) return false;

        if (BuffMobsConfig.MobFilter.useWhitelist.get()) {
            return BuffMobsConfig.MobFilter.whitelist.get().contains(mobId);
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