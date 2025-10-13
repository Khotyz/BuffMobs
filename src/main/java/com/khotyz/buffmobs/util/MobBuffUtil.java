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

    private static final Identifier HEALTH_MODIFIER_ID = Identifier.of(BuffMobsMod.MOD_ID, "temp_health");

    public static void applyBuffs(MobEntity mob) {
        if (!BuffMobsMod.CONFIG.enabled || !isValidMob(mob)) {
            return;
        }

        double dayMultiplier = getDayMultiplier(mob.getWorld().getTimeOfDay());
        DimensionMultipliers dimMultipliers = getDimensionMultipliers(mob);

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
        String dimensionName = mob.getWorld().getRegistryKey().getValue().toString();

        BuffMobsConfig.DimensionScaling.DimensionSlot[] slots = {
                BuffMobsMod.CONFIG.dimensionScaling.slot1,
                BuffMobsMod.CONFIG.dimensionScaling.slot2,
                BuffMobsMod.CONFIG.dimensionScaling.slot3,
                BuffMobsMod.CONFIG.dimensionScaling.slot4,
                BuffMobsMod.CONFIG.dimensionScaling.slot5
        };

        for (BuffMobsConfig.DimensionScaling.DimensionSlot slot : slots) {
            if (!slot.dimensionName.isEmpty() && slot.dimensionName.equals(dimensionName)) {
                return new DimensionMultipliers(
                        slot.healthMultiplier / 100.0, slot.damageMultiplier / 100.0,
                        slot.speedMultiplier / 100.0, slot.attackSpeedMultiplier / 100.0,
                        slot.armorAddition, slot.armorToughnessAddition
                );
            }
        }

        return new DimensionMultipliers(1.0, 1.0, 1.0, 1.0, 0.0, 0.0);
    }

    public static void refreshInfiniteEffects(MobEntity mob) {
        if (BuffMobsMod.CONFIG.effects.duration != -1 || !isValidMob(mob)) return;

        boolean showParticles = BuffMobsMod.CONFIG.visualEffects;
        int infiniteDuration = StatusEffectInstance.INFINITE;

        refreshEffect(mob, StatusEffects.STRENGTH, BuffMobsMod.CONFIG.effects.strengthAmplifier,
                infiniteDuration, showParticles);
        refreshEffect(mob, StatusEffects.RESISTANCE, BuffMobsMod.CONFIG.effects.resistanceAmplifier,
                infiniteDuration, showParticles);

        if (BuffMobsMod.CONFIG.effects.regenerationAmplifier > 0 &&
                !mob.getType().isIn(EntityTypeTags.UNDEAD)) {
            refreshEffect(mob, StatusEffects.REGENERATION, BuffMobsMod.CONFIG.effects.regenerationAmplifier,
                    infiniteDuration, showParticles);
        }
    }

    public static void applyPoisonToPlayer(PlayerEntity player, int duration) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, duration * 20, 0));
    }

    public static void applySlownessToPlayer(PlayerEntity player, int duration) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, duration * 20, 0));
    }

    public static void applyWitherToPlayer(PlayerEntity player, int duration) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, duration * 20, 0));
    }

    public static boolean isValidMob(MobEntity mob) {
        if (mob instanceof TameableEntity tameable && tameable.isTamed()) return false;

        boolean isHostileOrNeutral = mob instanceof HostileEntity ||
                mob.getType().isIn(EntityTypeTags.RAIDERS) ||
                isNeutralMob(mob);

        if (!isHostileOrNeutral) return false;

        String mobId = Registries.ENTITY_TYPE.getId(mob.getType()).toString();
        String modId = Registries.ENTITY_TYPE.getId(mob.getType()).getNamespace();
        String dimensionName = mob.getWorld().getRegistryKey().getValue().toString();

        return isValidDimension(dimensionName) &&
                isValidModId(modId) &&
                isValidMobId(mobId);
    }

    private static boolean isNeutralMob(MobEntity mob) {
        String mobId = Registries.ENTITY_TYPE.getId(mob.getType()).toString();

        return mobId.equals("minecraft:enderman") ||
                mobId.equals("minecraft:piglin") ||
                mobId.equals("minecraft:zombified_piglin") ||
                mobId.equals("minecraft:iron_golem") ||
                mobId.equals("minecraft:spider") ||
                mobId.equals("minecraft:cave_spider") ||
                mobId.equals("minecraft:wolf") ||
                mobId.equals("minecraft:polar_bear") ||
                mobId.equals("minecraft:bee") ||
                mobId.equals("minecraft:panda") ||
                mobId.equals("minecraft:llama") ||
                mobId.equals("minecraft:dolphin") ||
                mobId.equals("minecraft:trader_llama") ||
                mobId.equals("minecraft:slime") ||
                mobId.equals("minecraft:magma_cube") ||
                hasAttackGoal(mob);
    }

    private static boolean hasAttackGoal(MobEntity mob) {
        try {
            var goals = net.minecraft.entity.ai.goal.GoalSelector.class
                    .getDeclaredField("goals");
            goals.setAccessible(true);

            var mobGoalSelector = MobEntity.class.getDeclaredField("goalSelector");
            mobGoalSelector.setAccessible(true);

            var goalSelector = mobGoalSelector.get(mob);
            var goalSet = (java.util.Set<?>) goals.get(goalSelector);

            for (var prioritizedGoal : goalSet) {
                var goal = prioritizedGoal.getClass().getMethod("getGoal").invoke(prioritizedGoal);
                String goalName = goal.getClass().getSimpleName();

                if (goalName.contains("Attack") || goalName.contains("Melee") ||
                        goalName.contains("Ranged") || goalName.contains("Combat")) {
                    return true;
                }
            }
        } catch (Exception e) {
            // Silent fail
        }
        return false;
    }

    private static void applyAttributeModifiers(MobEntity mob, double dayMultiplier,
                                                DimensionMultipliers dimMultipliers) {
        double healthMult = calculateFinalMultiplier(
                BuffMobsMod.CONFIG.attributes.healthMultiplier,
                dimMultipliers.health, dayMultiplier);

        if (healthMult > 1.0) {
            EntityAttributeInstance healthAttr = mob.getAttributeInstance(EntityAttributes.MAX_HEALTH);
            if (healthAttr != null) {
                healthAttr.removeModifier(HEALTH_MODIFIER_ID);
                healthAttr.addTemporaryModifier(new EntityAttributeModifier(
                        HEALTH_MODIFIER_ID,
                        healthMult - 1.0,
                        EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                ));
            }
        }

        double damageMult = calculateFinalMultiplier(
                BuffMobsMod.CONFIG.attributes.damageMultiplier,
                dimMultipliers.damage, dayMultiplier);

        if (damageMult > 1.0) {
            EntityAttributeInstance damageAttr = mob.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE);
            if (damageAttr != null) {
                Identifier damageId = Identifier.of(BuffMobsMod.MOD_ID, "temp_damage");
                damageAttr.removeModifier(damageId);
                damageAttr.addTemporaryModifier(new EntityAttributeModifier(
                        damageId,
                        damageMult - 1.0,
                        EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                ));
            }
        }

        double speedMult = calculateFinalMultiplier(
                BuffMobsMod.CONFIG.attributes.speedMultiplier,
                dimMultipliers.speed, dayMultiplier);

        if (speedMult > 1.0 && speedMult < 3.0) {
            EntityAttributeInstance speedAttr = mob.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
            if (speedAttr != null) {
                Identifier speedId = Identifier.of(BuffMobsMod.MOD_ID, "temp_speed");
                speedAttr.removeModifier(speedId);
                double cappedMult = Math.min(speedMult, 1.5);
                double speedBonus = (cappedMult - 1.0) * 0.2;
                speedAttr.addTemporaryModifier(new EntityAttributeModifier(
                        speedId,
                        speedBonus,
                        EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                ));
            }
        }

        double attackSpeedMult = calculateFinalMultiplier(
                BuffMobsMod.CONFIG.attributes.attackSpeedMultiplier,
                dimMultipliers.attackSpeed, dayMultiplier);

        if (attackSpeedMult > 1.0) {
            EntityAttributeInstance attackSpeedAttr = mob.getAttributeInstance(EntityAttributes.ATTACK_SPEED);
            if (attackSpeedAttr != null) {
                Identifier attackSpeedId = Identifier.of(BuffMobsMod.MOD_ID, "temp_attack_speed");
                attackSpeedAttr.removeModifier(attackSpeedId);
                double cappedAttackMult = Math.min(attackSpeedMult, 2.5);
                attackSpeedAttr.addTemporaryModifier(new EntityAttributeModifier(
                        attackSpeedId,
                        cappedAttackMult - 1.0,
                        EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                ));
            }
        }

        double armorAdd = calculateFinalAddition(
                BuffMobsMod.CONFIG.attributes.armorAddition,
                dimMultipliers.armor, dayMultiplier);

        if (armorAdd > 0.0) {
            EntityAttributeInstance armorAttr = mob.getAttributeInstance(EntityAttributes.ARMOR);
            if (armorAttr != null) {
                Identifier armorId = Identifier.of(BuffMobsMod.MOD_ID, "temp_armor");
                armorAttr.removeModifier(armorId);
                armorAttr.addTemporaryModifier(new EntityAttributeModifier(
                        armorId,
                        armorAdd,
                        EntityAttributeModifier.Operation.ADD_VALUE
                ));
            }
        }

        double toughnessAdd = calculateFinalAddition(
                BuffMobsMod.CONFIG.attributes.armorToughnessAddition,
                dimMultipliers.armorToughness, dayMultiplier);

        if (toughnessAdd > 0.0) {
            EntityAttributeInstance toughnessAttr = mob.getAttributeInstance(EntityAttributes.ARMOR_TOUGHNESS);
            if (toughnessAttr != null) {
                Identifier toughnessId = Identifier.of(BuffMobsMod.MOD_ID, "temp_toughness");
                toughnessAttr.removeModifier(toughnessId);
                toughnessAttr.addTemporaryModifier(new EntityAttributeModifier(
                        toughnessId,
                        toughnessAdd,
                        EntityAttributeModifier.Operation.ADD_VALUE
                ));
            }
        }
    }

    private static void applyVanillaStatusEffects(MobEntity mob) {
        int duration = BuffMobsMod.CONFIG.effects.duration == -1 ?
                StatusEffectInstance.INFINITE : BuffMobsMod.CONFIG.effects.duration * 20;
        boolean showParticles = BuffMobsMod.CONFIG.visualEffects;

        addEffect(mob, StatusEffects.STRENGTH, BuffMobsMod.CONFIG.effects.strengthAmplifier,
                duration, showParticles);
        addEffect(mob, StatusEffects.RESISTANCE, BuffMobsMod.CONFIG.effects.resistanceAmplifier,
                duration, showParticles);

        if (BuffMobsMod.CONFIG.effects.regenerationAmplifier > 0 &&
                !mob.getType().isIn(EntityTypeTags.UNDEAD)) {
            addEffect(mob, StatusEffects.REGENERATION, BuffMobsMod.CONFIG.effects.regenerationAmplifier,
                    duration, showParticles);
        }
    }

    private static void refreshEffect(MobEntity mob, RegistryEntry<StatusEffect> effect,
                                      int amplifier, int duration, boolean showParticles) {
        if (amplifier <= 0) return;

        StatusEffectInstance current = mob.getStatusEffect(effect);
        if (current == null || current.getDuration() < 1200) {
            mob.removeStatusEffect(effect);
            mob.addStatusEffect(new StatusEffectInstance(effect, duration, amplifier - 1,
                    false, showParticles, true));
        }
    }

    private static void addEffect(MobEntity mob, RegistryEntry<StatusEffect> effect,
                                  int amplifier, int duration, boolean showParticles) {
        if (amplifier > 0) {
            mob.addStatusEffect(new StatusEffectInstance(effect, duration, amplifier - 1,
                    false, showParticles, true));
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

    public static double calculateFinalMultiplier(double baseMultiplier, double dimensionMultiplier,
                                                  double dayMultiplier) {
        if (baseMultiplier > 1.0) {
            return baseMultiplier * dimensionMultiplier * dayMultiplier;
        } else if (dimensionMultiplier > 1.0) {
            return dimensionMultiplier * dayMultiplier;
        }
        return 1.0;
    }

    public static double calculateFinalAddition(double baseAddition, double dimensionAddition,
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