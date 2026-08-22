package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
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
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;

import java.util.List;

import static com.khotyz.buffmobs.util.DimensionUtil.getDimensionId;

public class MobBuffUtil {

    private static final Identifier HEALTH_MOD_ID       = Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "health");
    private static final Identifier DAMAGE_MOD_ID       = Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "damage");
    private static final Identifier SPEED_MOD_ID        = Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "speed");
    private static final Identifier ATTACK_SPEED_MOD_ID = Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "attack_speed");
    private static final Identifier ARMOR_MOD_ID        = Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "armor");
    private static final Identifier TOUGHNESS_MOD_ID    = Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "toughness");
    private static final Identifier LEADER_ZOMBIE_BONUS_ID = Identifier.fromNamespaceAndPath("minecraft", "leader_zombie_bonus");

    public static void applyBuffs(Mob mob) {
        if (!BuffMobsConfig.INSTANCE.enabled || !isValidMob(mob)) {
            removeAllModifiers(mob);
            removeAllBuffEffects(mob);
            enforceDimensionMaxHealth(mob);
            return;
        }

        handleZombieLeaderBonus(mob);

        double dayMult = getDayMultiplier(mob.level().getOverworldClockTime());
        DimensionMultipliers dim = getDimensionMultipliers(mob);
        MobPresetUtil.PresetMultipliers preset = MobPresetUtil.getPresetForMob(mob);

        BuffMobsMod.LOGGER.debug("[BuffMobs] Applying buffs to {} | day={} dimHP={} preset={}",
                BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()), dayMult, dim.health, preset != null);

        double oldMaxHealth = mob.getMaxHealth();
        double oldHealth = mob.getHealth();

        applyAllLayers(mob, dayMult, dim, preset);

        removeAllBuffEffects(mob);
        applyStatusEffects(mob);
        syncHealth(mob, oldMaxHealth, oldHealth);

        int absAmp = BuffMobsConfig.INSTANCE.effects.absorptionAmplifier;
        if (absAmp > 0) mob.setAbsorptionAmount(absAmp * 4.0f);

        enforceDimensionMaxHealth(mob);
    }

    private static void syncHealth(Mob mob, double oldMaxHealth, double oldHealth) {
        if (!BuffMobsConfig.INSTANCE.healthSync.enabled) {
            mob.setHealth(mob.getMaxHealth());
            return;
        }

        double newMaxHealth = mob.getMaxHealth();
        if (BuffMobsConfig.INSTANCE.healthSync.mode == BuffMobsConfig.HealthSync.Mode.STACK) {
            double diff = newMaxHealth - oldMaxHealth;
            double target = oldHealth + Math.max(0.0, diff);
            mob.setHealth((float) Math.min(newMaxHealth, target));
        } else {
            mob.setHealth((float) newMaxHealth);
        }
    }

    private static void handleZombieLeaderBonus(Mob mob) {
        if (!(mob instanceof Zombie)) return;
        if (!BuffMobsConfig.INSTANCE.zombieHandling.disableLeaderZombies) return;
        AttributeInstance hpAttr = mob.getAttribute(Attributes.MAX_HEALTH);
        if (hpAttr != null) hpAttr.removeModifier(LEADER_ZOMBIE_BONUS_ID);
    }

    public static void enforceDimensionMaxHealth(Mob mob) {
        Double override = getDimensionMaxHealthOverride(mob);
        if (override == null) return;
        AttributeInstance hpAttr = mob.getAttribute(Attributes.MAX_HEALTH);
        if (hpAttr == null) return;
        hpAttr.removeModifier(HEALTH_MOD_ID);
        hpAttr.setBaseValue(override);
        if (mob.getHealth() > override) mob.setHealth(override.floatValue());
    }

    private static Double getDimensionMaxHealthOverride(Mob mob) {
        if (!BuffMobsConfig.INSTANCE.dimensionMaxHealth.enabled) return null;

        String dim = getDimensionId(mob.level());
        String mobId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();

        BuffMobsConfig.DimensionMaxHealth.DimensionMaxHealthSlot[] slots = {
                BuffMobsConfig.INSTANCE.dimensionMaxHealth.slot1,
                BuffMobsConfig.INSTANCE.dimensionMaxHealth.slot2,
                BuffMobsConfig.INSTANCE.dimensionMaxHealth.slot3,
                BuffMobsConfig.INSTANCE.dimensionMaxHealth.slot4,
                BuffMobsConfig.INSTANCE.dimensionMaxHealth.slot5
        };

        for (BuffMobsConfig.DimensionMaxHealth.DimensionMaxHealthSlot slot : slots) {
            if (slot.dimensionId == null || slot.dimensionId.isEmpty() || !slot.dimensionId.equals(dim)) continue;
            if (slot.denylist.contains(mobId)) continue;
            if (slot.useAllowlist && !slot.allowlist.contains(mobId)) continue;
            return slot.maxHealth;
        }
        return null;
    }

    public static void removeAllBuffEffects(Mob mob) {
        mob.removeEffect(MobEffects.STRENGTH);
        mob.removeEffect(MobEffects.SPEED);
        mob.removeEffect(MobEffects.RESISTANCE);
        mob.removeEffect(MobEffects.ABSORPTION);
        mob.removeEffect(MobEffects.REGENERATION);
    }

    public static void removeAllModifiers(Mob mob) {
        removeModifier(mob, Attributes.MAX_HEALTH,      HEALTH_MOD_ID);
        removeModifier(mob, Attributes.ATTACK_DAMAGE,   DAMAGE_MOD_ID);
        removeModifier(mob, Attributes.MOVEMENT_SPEED,  SPEED_MOD_ID);
        removeModifier(mob, Attributes.ATTACK_SPEED,    ATTACK_SPEED_MOD_ID);
        removeModifier(mob, Attributes.ARMOR,           ARMOR_MOD_ID);
        removeModifier(mob, Attributes.ARMOR_TOUGHNESS, TOUGHNESS_MOD_ID);
        mob.setAbsorptionAmount(0);
    }

    private static void removeModifier(Mob mob,
                                       Holder<net.minecraft.world.entity.ai.attributes.Attribute> attr,
                                       Identifier id) {
        AttributeInstance inst = mob.getAttribute(attr);
        if (inst != null) inst.removeModifier(id);
    }

    public static double getDayMultiplier(long worldTime) {
        if (!BuffMobsConfig.INSTANCE.dayScaling.enabled) return 1.0;
        long days      = worldTime / 24000L;
        long intervals = days / Math.max(1, BuffMobsConfig.INSTANCE.dayScaling.interval);
        double mult    = 1.0 + (intervals * BuffMobsConfig.INSTANCE.dayScaling.multiplier);
        double max     = BuffMobsConfig.INSTANCE.dayScaling.maxMultiplier;
        return max == 0.0 ? mult : Math.min(mult, max);
    }

    public static DimensionMultipliers getDimensionMultipliers(Mob mob) {
        String dim = getDimensionId(mob.level());
        BuffMobsConfig.DimensionScaling.DimensionSlot[] slots = {
                BuffMobsConfig.INSTANCE.dimensionScaling.slot1,
                BuffMobsConfig.INSTANCE.dimensionScaling.slot2,
                BuffMobsConfig.INSTANCE.dimensionScaling.slot3,
                BuffMobsConfig.INSTANCE.dimensionScaling.slot4,
                BuffMobsConfig.INSTANCE.dimensionScaling.slot5
        };
        BuffMobsConfig.DimensionScaling.Mode mode = BuffMobsConfig.INSTANCE.dimensionScaling.mode;
        boolean override = (mode == BuffMobsConfig.DimensionScaling.Mode.OVERRIDE);

        for (BuffMobsConfig.DimensionScaling.DimensionSlot slot : slots) {
            if (slot.dimensionName != null && !slot.dimensionName.isEmpty() && slot.dimensionName.equals(dim)) {
                if (override) {
                    return new DimensionMultipliers(
                            slot.healthMultiplier,
                            slot.damageMultiplier,
                            slot.speedMultiplier,
                            slot.attackSpeedMultiplier,
                            slot.armorAddition,
                            slot.armorToughnessAddition,
                            true
                    );
                } else {
                    return new DimensionMultipliers(
                            slot.healthMultiplier / 100.0,
                            slot.damageMultiplier / 100.0,
                            slot.speedMultiplier / 100.0,
                            slot.attackSpeedMultiplier / 100.0,
                            slot.armorAddition,
                            slot.armorToughnessAddition,
                            false
                    );
                }
            }
        }
        if (override) {
            return new DimensionMultipliers(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, true);
        } else {
            return new DimensionMultipliers(1.0, 1.0, 1.0, 1.0, 0.0, 0.0, false);
        }
    }

    public static void refreshInfiniteEffects(Mob mob) {
        if (BuffMobsConfig.INSTANCE.effects.duration != -1 || !isValidMob(mob)) return;
        boolean show   = BuffMobsConfig.INSTANCE.visualEffects;
        boolean undead = mob.getType().builtInRegistryHolder().is(EntityTypeTags.UNDEAD);

        refreshEffect(mob, MobEffects.STRENGTH,       BuffMobsConfig.INSTANCE.effects.strengthAmplifier,   show);
        refreshEffect(mob, MobEffects.SPEED, BuffMobsConfig.INSTANCE.effects.speedAmplifier,      show);
        refreshEffect(mob, MobEffects.RESISTANCE,     BuffMobsConfig.INSTANCE.effects.resistanceAmplifier, show);

        int absAmp = BuffMobsConfig.INSTANCE.effects.absorptionAmplifier;
        if (absAmp > 0) {
            float expected = absAmp * 4.0f;
            if (undead) {
                MobEffectInstance cur = mob.getEffect(MobEffects.ABSORPTION);
                if (cur == null || (cur.getAmplifier() <= absAmp - 1 && cur.getDuration() < 1200)) {
                    mob.removeEffect(MobEffects.ABSORPTION);
                    mob.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, -1, absAmp - 1, false, show, true));
                }
                if (mob.getAbsorptionAmount() < expected * 0.5f && (cur == null || cur.getAmplifier() <= absAmp - 1)) {
                    mob.setAbsorptionAmount(expected);
                }
            } else {
                MobEffectInstance cur = mob.getEffect(MobEffects.ABSORPTION);
                if (cur == null || (cur.getAmplifier() <= absAmp - 1 && cur.getDuration() < 1200)) {
                    refreshEffect(mob, MobEffects.ABSORPTION, absAmp, show);
                }
                if (mob.getAbsorptionAmount() < expected * 0.5f) {
                    mob.setAbsorptionAmount(expected);
                }
            }
        }

        if (!undead)
            refreshEffect(mob, MobEffects.REGENERATION, BuffMobsConfig.INSTANCE.effects.regenerationAmplifier, show);
    }

    public static void applyPoisonToPlayer(Player player, int duration) {
        player.addEffect(new MobEffectInstance(MobEffects.POISON,   duration * 20, 0));
    }
    public static void applySlownessToPlayer(Player player, int duration) {
        player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, duration * 20, 0));
    }
    public static void applyWitherToPlayer(Player player, int duration) {
        player.addEffect(new MobEffectInstance(MobEffects.WITHER,   duration * 20, 0));
    }

    public static boolean isValidMob(Mob mob) {
        if (mob instanceof TamableAnimal ta && ta.isTame()) return false;

        boolean isHostile = mob instanceof Enemy;
        boolean isNeutral = isNeutralMob(mob);
        boolean isPassiveAggressive = isPassiveAggressiveMob(mob);
        String mobId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        if (!isHostile && !isNeutral && !isPassiveAggressive && !isExplicitlyAllowed(mobId)) return false;

        String dim   = getDimensionId(mob.level());
        String modId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).getNamespace();

        boolean validDim = isValidDimension(dim);
        boolean validMod = isValidModId(modId);
        boolean validMob = isValidMobId(mobId);

        if (!validDim) BuffMobsMod.LOGGER.debug("[BuffMobs] {} in invalid dimension {}", mobId, dim);
        if (!validMod) BuffMobsMod.LOGGER.debug("[BuffMobs] {} mod filtered",            mobId);
        if (!validMob) BuffMobsMod.LOGGER.debug("[BuffMobs] {} in blacklist",             mobId);

        return validDim && validMod && validMob;
    }

    public static boolean isPassiveAggressiveMob(Mob mob) {
        BuffMobsConfig.PassiveMobAggression.Mode mode = BuffMobsConfig.INSTANCE.passiveMobAggression.mode;
        if (mode == BuffMobsConfig.PassiveMobAggression.Mode.OFF) return false;
        if (mob instanceof TamableAnimal ta && ta.isTame()) return false;
        if (mob instanceof Enemy) return false;
        if (isNeutralMob(mob)) return false;

        String mobId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        BuffMobsConfig.PassiveMobAggression cfg = BuffMobsConfig.INSTANCE.passiveMobAggression;

        if (cfg.blacklist.contains(mobId)) return false;
        if (!cfg.whitelist.isEmpty()) return cfg.whitelist.contains(mobId);
        return true;
    }

    public static double getPassiveDamageForMob(Mob mob) {
        BuffMobsConfig.PassiveMobAggression cfg = BuffMobsConfig.INSTANCE.passiveMobAggression;
        double base = cfg.baseDamage;
        if (cfg.scaleWithHealth) {
            base += mob.getMaxHealth() * cfg.healthScaleFactor;
        }
        return base;
    }

    private static boolean isExplicitlyAllowed(String mobId) {
        if (BuffMobsConfig.INSTANCE.mobFilter.whitelist.contains(mobId)) return true;
        if (BuffMobsConfig.INSTANCE.mobPresets.enabled) {
            for (String mapping : BuffMobsConfig.INSTANCE.mobPresets.mobMapping) {
                String[] parts = mapping.split(":");
                if (parts.length >= 3 && (parts[0] + ":" + parts[1]).equals(mobId)) return true;
            }
        }
        return false;
    }

    private static boolean isNeutralMob(Mob mob) {
        String id = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        return switch (id) {
            case "minecraft:enderman", "minecraft:piglin", "minecraft:zombified_piglin",
                 "minecraft:iron_golem", "minecraft:spider", "minecraft:cave_spider",
                 "minecraft:wolf", "minecraft:polar_bear", "minecraft:bee",
                 "minecraft:panda", "minecraft:llama", "minecraft:dolphin",
                 "minecraft:trader_llama", "minecraft:slime", "minecraft:magma_cube" -> true;
            default -> false;
        };
    }

    private static void applyAllLayers(Mob mob, double dayMult, DimensionMultipliers dim,
                                       MobPresetUtil.PresetMultipliers preset) {
        double attrHp    = BuffMobsConfig.INSTANCE.attributes.healthMultiplier;
        double attrDmg   = BuffMobsConfig.INSTANCE.attributes.damageMultiplier;
        double attrSpd   = BuffMobsConfig.INSTANCE.attributes.speedMultiplier;
        double attrAspd  = BuffMobsConfig.INSTANCE.attributes.attackSpeedMultiplier;
        double attrArm   = BuffMobsConfig.INSTANCE.attributes.armorAddition;
        double attrTough = BuffMobsConfig.INSTANCE.attributes.armorToughnessAddition;

        double presetHp    = preset != null ? preset.health         : 1.0;
        double presetDmg   = preset != null ? preset.damage         : 1.0;
        double presetSpd   = preset != null ? preset.speed          : 1.0;
        double presetAspd  = preset != null ? preset.attackSpeed    : 1.0;
        double presetArm   = preset != null ? preset.armor          : 0.0;
        double presetTough = preset != null ? preset.armorToughness : 0.0;

        boolean presetOverridesDim = preset != null && BuffMobsConfig.INSTANCE.mobPresets.overrideDimensionScaling;
        double dimHealth      = presetOverridesDim ? 1.0 : dim.health;
        double dimDamage      = presetOverridesDim ? 1.0 : dim.damage;
        double dimSpeed       = presetOverridesDim ? 1.0 : dim.speed;
        double dimAttackSpeed = presetOverridesDim ? 1.0 : dim.attackSpeed;
        double dimArmor       = presetOverridesDim ? 0.0 : dim.armor;
        double dimToughness   = presetOverridesDim ? 0.0 : dim.armorToughness;

        boolean isZombie  = mob instanceof Zombie;
        boolean isPassive = isPassiveAggressiveMob(mob);

        // Aplica todos os buffs de atributos para todos os mobs (inclusive passivos)
        if (dim.override) {
            applyHealthOverride(mob, dimHealth, isZombie);
            applyOverride(mob, Attributes.ATTACK_DAMAGE,   DAMAGE_MOD_ID,       dimDamage);
            applyOverride(mob, Attributes.MOVEMENT_SPEED,  SPEED_MOD_ID,        dimSpeed);
            applyOverride(mob, Attributes.ATTACK_SPEED,    ATTACK_SPEED_MOD_ID, dimAttackSpeed);
            applyAddition  (mob, Attributes.ARMOR,           ARMOR_MOD_ID,       (attrArm   + presetArm + dimArmor)   * dayMult);
            applyAddition  (mob, Attributes.ARMOR_TOUGHNESS, TOUGHNESS_MOD_ID,   (attrTough + presetTough + dimToughness) * dayMult);
        } else {
            applyHealthMultiplier(mob, attrHp * dimHealth * presetHp * dayMult, isZombie);
            applyMultiplier(mob, Attributes.ATTACK_DAMAGE,   DAMAGE_MOD_ID,       attrDmg  * dimDamage      * presetDmg  * dayMult);
            applySpeedBonus(mob,                             SPEED_MOD_ID,        attrSpd  * dimSpeed       * presetSpd  * dayMult);
            applyMultiplier(mob, Attributes.ATTACK_SPEED,    ATTACK_SPEED_MOD_ID, attrAspd * dimAttackSpeed * presetAspd * dayMult);
            applyAddition  (mob, Attributes.ARMOR,           ARMOR_MOD_ID,       (attrArm   + dimArmor      + presetArm)   * dayMult);
            applyAddition  (mob, Attributes.ARMOR_TOUGHNESS, TOUGHNESS_MOD_ID,   (attrTough + dimToughness  + presetTough) * dayMult);
        }

        // Se for passivo, sobrescreve o dano com o valor específico da agressão passiva
        if (isPassive) {
            applyPassiveAggressionDamage(mob);
        }
    }

    private static void applyHealthOverride(Mob mob, double value, boolean isZombie) {
        AttributeInstance inst = mob.getAttribute(Attributes.MAX_HEALTH);
        if (inst == null) return;
        inst.removeModifier(HEALTH_MOD_ID);
        if (value > 0) {
            double base = inst.getBaseValue();
            double bonus = value - base;
            inst.addPermanentModifier(new AttributeModifier(HEALTH_MOD_ID, bonus,
                    AttributeModifier.Operation.ADD_VALUE));
        }
    }

    private static void applyOverride(Mob mob,
                                      Holder<net.minecraft.world.entity.ai.attributes.Attribute> attr,
                                      Identifier id, double value) {
        AttributeInstance inst = mob.getAttribute(attr);
        if (inst == null) return;
        inst.removeModifier(id);
        if (value > 0) {
            double base = inst.getBaseValue();
            double bonus = value - base;
            inst.addPermanentModifier(new AttributeModifier(id, bonus,
                    AttributeModifier.Operation.ADD_VALUE));
        }
    }

    private static void applyPassiveAggressionDamage(Mob mob) {
        AttributeInstance dmgAttr = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmgAttr == null) return;
        dmgAttr.removeModifier(DAMAGE_MOD_ID);
        double baseDmg = BuffMobsConfig.INSTANCE.passiveMobAggression.baseDamage;
        if (BuffMobsConfig.INSTANCE.passiveMobAggression.scaleWithHealth) {
            baseDmg += mob.getMaxHealth() * BuffMobsConfig.INSTANCE.passiveMobAggression.healthScaleFactor;
        }
        double current = dmgAttr.getBaseValue();
        if (baseDmg > current) {
            double bonus = baseDmg - current;
            dmgAttr.addPermanentModifier(new AttributeModifier(DAMAGE_MOD_ID, bonus,
                    AttributeModifier.Operation.ADD_VALUE));
        }
    }

    private static void applyHealthMultiplier(Mob mob, double finalMult, boolean isZombie) {
        AttributeInstance inst = mob.getAttribute(Attributes.MAX_HEALTH);
        if (inst == null) return;
        inst.removeModifier(HEALTH_MOD_ID);
        if (finalMult <= 1.0) return;

        boolean excludeLeaderBonus = isZombie && BuffMobsConfig.INSTANCE.zombieHandling.excludeLeaderBonusFromMultiplier;
        if (excludeLeaderBonus) {
            double bonus = inst.getBaseValue() * (finalMult - 1.0);
            inst.addPermanentModifier(new AttributeModifier(HEALTH_MOD_ID, bonus,
                    AttributeModifier.Operation.ADD_VALUE));
        } else {
            inst.addPermanentModifier(new AttributeModifier(HEALTH_MOD_ID, finalMult - 1.0,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
    }

    private static void applyMultiplier(Mob mob,
                                        Holder<net.minecraft.world.entity.ai.attributes.Attribute> attr,
                                        Identifier id, double finalMult) {
        AttributeInstance inst = mob.getAttribute(attr);
        if (inst == null) return;
        inst.removeModifier(id);
        if (finalMult > 1.0) {
            inst.addPermanentModifier(new AttributeModifier(id, finalMult - 1.0,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
    }

    private static void applySpeedBonus(Mob mob, Identifier id, double finalMult) {
        AttributeInstance inst = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (inst == null) return;
        inst.removeModifier(id);
        if (finalMult > 1.0) {
            double bonus = (finalMult - 1.0) * 0.3;
            inst.addPermanentModifier(new AttributeModifier(id, bonus,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
    }

    private static void applyAddition(Mob mob,
                                      Holder<net.minecraft.world.entity.ai.attributes.Attribute> attr,
                                      Identifier id, double amount) {
        AttributeInstance inst = mob.getAttribute(attr);
        if (inst == null) return;
        inst.removeModifier(id);
        if (amount > 0.0) {
            inst.addPermanentModifier(new AttributeModifier(id, amount,
                    AttributeModifier.Operation.ADD_VALUE));
        }
    }

    private static void applyStatusEffects(Mob mob) {
        int duration = BuffMobsConfig.INSTANCE.effects.duration == -1
                ? -1 : BuffMobsConfig.INSTANCE.effects.duration * 20;
        boolean show   = BuffMobsConfig.INSTANCE.visualEffects;
        boolean undead = mob.getType().builtInRegistryHolder().is(EntityTypeTags.UNDEAD);

        addEffect(mob, MobEffects.STRENGTH,       BuffMobsConfig.INSTANCE.effects.strengthAmplifier,   duration, show);
        addEffect(mob, MobEffects.SPEED , BuffMobsConfig.INSTANCE.effects.speedAmplifier,      duration, show);
        addEffect(mob, MobEffects.RESISTANCE,     BuffMobsConfig.INSTANCE.effects.resistanceAmplifier, duration, show);

        int absAmp = BuffMobsConfig.INSTANCE.effects.absorptionAmplifier;
        if (absAmp > 0) {
            float absAmount = absAmp * 4.0f;
            if (undead) {
                mob.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, duration, absAmp - 1, false, show, true));
                mob.setAbsorptionAmount(absAmount);
            } else {
                addEffect(mob, MobEffects.ABSORPTION, absAmp, duration, show);
                mob.setAbsorptionAmount(absAmount);
            }
        }

        if (!undead)
            addEffect(mob, MobEffects.REGENERATION, BuffMobsConfig.INSTANCE.effects.regenerationAmplifier, duration, show);
    }

    private static void refreshEffect(Mob mob, Holder<MobEffect> effect, int amp, boolean show) {
        if (amp <= 0) return;
        MobEffectInstance cur = mob.getEffect(effect);
        if (cur != null && cur.getAmplifier() >= amp - 1 && cur.getDuration() >= 1200) return;
        if (cur != null && cur.getAmplifier() > amp - 1) return;
        mob.removeEffect(effect);
        mob.addEffect(new MobEffectInstance(effect, -1, amp - 1, false, show, true));
    }

    private static void addEffect(Mob mob, Holder<MobEffect> effect, int amp, int duration, boolean show) {
        if (amp > 0) mob.addEffect(new MobEffectInstance(effect, duration, amp - 1, false, show, true));
    }

    private static boolean isValidDimension(String dim) {
        List<String> bl = BuffMobsConfig.INSTANCE.dimensionFilter.blacklist;
        if (bl.contains(dim)) return false;
        if (BuffMobsConfig.INSTANCE.dimensionFilter.useWhitelist)
            return BuffMobsConfig.INSTANCE.dimensionFilter.whitelist.contains(dim);
        return true;
    }

    private static boolean isValidModId(String modId) {
        List<String> bl = BuffMobsConfig.INSTANCE.modidFilter.blacklist;
        if (bl.contains(modId)) return false;
        if (BuffMobsConfig.INSTANCE.modidFilter.useWhitelist)
            return BuffMobsConfig.INSTANCE.modidFilter.whitelist.contains(modId);
        return true;
    }

    private static boolean isValidMobId(String mobId) {
        List<String> bl = BuffMobsConfig.INSTANCE.mobFilter.blacklist;
        if (bl.contains(mobId)) return false;
        if (BuffMobsConfig.INSTANCE.mobFilter.useWhitelist)
            return BuffMobsConfig.INSTANCE.mobFilter.whitelist.contains(mobId);
        return true;
    }

    public static double calculateFinalMultiplier(double base, double dim, double day) { return base * dim * day; }
    public static double calculateFinalAddition(double base, double dim, double day)   { return (base + dim) * day; }

    public static class DimensionMultipliers {
        public final double health, damage, speed, attackSpeed, armor, armorToughness;
        public final boolean override;

        public DimensionMultipliers(double health, double damage, double speed,
                                    double attackSpeed, double armor, double armorToughness,
                                    boolean override) {
            this.health = health;
            this.damage = damage;
            this.speed = speed;
            this.attackSpeed = attackSpeed;
            this.armor = armor;
            this.armorToughness = armorToughness;
            this.override = override;
        }
    }
}