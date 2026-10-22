package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.data.BuffMobsWorldData;
import com.khotyz.buffmobs.event.AdaptiveDifficultyHandler;
import com.khotyz.buffmobs.event.BloodMoonHandler;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import static com.khotyz.buffmobs.util.DimensionUtil.getDimensionId;

import java.util.List;

public class MobBuffUtil {

    private static final Identifier HEALTH_MOD_ID       = Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "health");
    private static final Identifier DAMAGE_MOD_ID       = Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "damage");
    private static final Identifier SPEED_MOD_ID        = Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "speed");
    private static final Identifier ATTACK_SPEED_MOD_ID = Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "attack_speed");
    private static final Identifier ARMOR_MOD_ID        = Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "armor");
    private static final Identifier TOUGHNESS_MOD_ID    = Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "toughness");
    private static final Identifier LEADER_ZOMBIE_BONUS_ID = Identifier.fromNamespaceAndPath("minecraft", "leader_zombie_bonus");

    private static final BuffMobsConfig.EliteMobs.EliteParticle[] RANDOM_PARTICLES = {
            BuffMobsConfig.EliteMobs.EliteParticle.ENCHANT,
            BuffMobsConfig.EliteMobs.EliteParticle.FLAME,
            BuffMobsConfig.EliteMobs.EliteParticle.SOUL_FIRE_FLAME,
            BuffMobsConfig.EliteMobs.EliteParticle.PORTAL,
            BuffMobsConfig.EliteMobs.EliteParticle.END_ROD,
            BuffMobsConfig.EliteMobs.EliteParticle.HAPPY_VILLAGER,
            BuffMobsConfig.EliteMobs.EliteParticle.HEART,
            BuffMobsConfig.EliteMobs.EliteParticle.SNOWFLAKE,
            BuffMobsConfig.EliteMobs.EliteParticle.ELECTRIC_SPARK
    };

    public static BuffMobsConfig.EliteMobs.EliteParticle randomParticleFor(Mob mob) {
        return RANDOM_PARTICLES[Math.floorMod(mob.getUUID().hashCode(), RANDOM_PARTICLES.length)];
    }

    public static long getOverworldDayTime(Level level) {
        if (level instanceof ServerLevel sl) {
            MinecraftServer server = sl.getServer();
            if (server != null) {
                ServerLevel overworld = server.overworld();
                if (overworld != null) {
                    return overworld.getOverworldClockTime();
                }
            }
            return sl.getOverworldClockTime();
        }
        return level.getOverworldClockTime();
    }

    public static void applyBuffs(Mob mob) {
        if (!BuffMobsConfig.INSTANCE.enabled.get()) {
            removeAllModifiers(mob);
            removeAllBuffEffects(mob);
            return;
        }

        if (!isValidMob(mob)) {
            removeAllModifiers(mob);
            removeAllBuffEffects(mob);
            enforceDimensionMaxHealth(mob);
            return;
        }

        double dayMult = getDayMultiplier(mob.level())
                * AdaptiveDifficultyHandler.getAdaptiveMultiplier(mob.level())
                * BloodMoonHandler.getActiveMultiplier(mob.level());
        DimensionMultipliers dim = getDimensionMultipliers(mob);
        MobPresetUtil.PresetMultipliers preset = MobPresetUtil.getPresetForMob(mob);

        BuffMobsMod.LOGGER.debug("[BuffMobs] Applying buffs to {} | day={} dimHP={} preset={}",
                BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()), dayMult, dim.health, preset != null);

        AttributeInstance hpInstBefore = mob.getAttribute(Attributes.MAX_HEALTH);
        double oldMaxHealth = hpInstBefore != null ? hpInstBefore.getValue() : mob.getMaxHealth();
        double oldHealth = mob.getHealth();

        applyAllLayers(mob, dayMult, dim, preset);

        removeAllBuffEffects(mob);
        applyStatusEffects(mob);
        syncHealth(mob, oldMaxHealth, oldHealth);
        enforceDimensionMaxHealth(mob);

        int absAmp = BuffMobsConfig.INSTANCE.effects.absorptionAmplifier.get();
        if (absAmp > 0) mob.setAbsorptionAmount(absAmp * 4.0f);
    }

    public static void enforceDimensionMaxHealth(Mob mob) {
        if (!BuffMobsConfig.INSTANCE.enabled.get()) return;
        Double target = getDimensionMaxHealthOverride(mob);
        if (target != null) applyDimensionMaxHealthOverride(mob, target);
    }

    private static void syncHealth(Mob mob, double oldMaxHealth, double oldHealth) {
        double newMaxHealth = mob.getMaxHealth();
        BuffMobsConfig.HealthSync sync = BuffMobsConfig.INSTANCE.healthSync;
        if (!sync.enabled.get()) {
            mob.setHealth((float) newMaxHealth);
            return;
        }
        if (sync.mode.get() == BuffMobsConfig.HealthSync.Mode.STACK) {
            double delta = Math.max(0.0, newMaxHealth - oldMaxHealth);
            mob.setHealth((float) Math.min(newMaxHealth, oldHealth + delta));
        } else {
            mob.setHealth((float) newMaxHealth);
        }
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
        return computeDayMultiplier(worldTime / 24000L);
    }

    public static double getDayMultiplier(Level level) {
        return computeDayMultiplier(getEffectiveDays(level));
    }

    public static long getRawDays(Level level) {
        return level.getOverworldClockTime() / 24000L;
    }

    public static long getEffectiveDays(Level level) {
        long rawDays = getRawDays(level);
        BuffMobsWorldData data = BuffMobsWorldData.get(level);
        long offset = data != null ? data.getScalingDayOffset() : 0L;
        return Math.max(0L, rawDays - offset);
    }

    private static double computeDayMultiplier(long days) {
        if (!BuffMobsConfig.INSTANCE.dayScaling.enabled.get()) return 1.0;
        long intervals = days / Math.max(1, BuffMobsConfig.INSTANCE.dayScaling.interval.get());
        double mult    = 1.0 + (intervals * BuffMobsConfig.INSTANCE.dayScaling.multiplier.get());
        double max = BuffMobsConfig.INSTANCE.dayScaling.maxMultiplier.get();
        return max == 0.0 ? mult : Math.min(mult, max);
    }

    public static double getEffectiveHealthMultiplier(Mob mob) {
        if (!BuffMobsConfig.INSTANCE.enabled.get()) return 1.0;
        Double fixedHealthTarget = getDimensionMaxHealthOverride(mob);
        if (fixedHealthTarget != null && fixedHealthTarget > 0.0) {
            AttributeInstance health = mob.getAttribute(Attributes.MAX_HEALTH);
            double baseHealth = health != null ? health.getBaseValue() : 0.0;
            if (baseHealth > 0.0) return Math.max(1.0, fixedHealthTarget / baseHealth);
        }
        DimensionMultipliers dim = getDimensionMultipliers(mob);
        MobPresetUtil.PresetMultipliers preset = MobPresetUtil.getPresetForMob(mob);
        if (preset != null && BuffMobsConfig.INSTANCE.mobPresets.overrideDimensionScaling.get()) {
            dim = DimensionMultipliers.IDENTITY;
        }
        double progression = getDayMultiplier(mob.level())
                * AdaptiveDifficultyHandler.getAdaptiveMultiplier(mob.level());
        if (BuffMobsConfig.INSTANCE.dimensionScaling.mode.get()
                == BuffMobsConfig.DimensionScaling.DimensionScalingMode.OVERRIDE) {
            return Math.max(1.0, dim.health * progression);
        }
        double presetHealth = preset != null ? preset.health : 1.0;
        return Math.max(1.0, BuffMobsConfig.INSTANCE.attributes.healthMultiplier.get()
                * dim.health * presetHealth * progression);
    }

    public static double getEffectiveDamageMultiplier(Mob mob) {
        if (!BuffMobsConfig.INSTANCE.enabled.get()) return 1.0;
        DimensionMultipliers dim = getDimensionMultipliers(mob);
        MobPresetUtil.PresetMultipliers preset = MobPresetUtil.getPresetForMob(mob);
        if (preset != null && BuffMobsConfig.INSTANCE.mobPresets.overrideDimensionScaling.get()) {
            dim = DimensionMultipliers.IDENTITY;
        }
        double progression = getDayMultiplier(mob.level())
                * AdaptiveDifficultyHandler.getAdaptiveMultiplier(mob.level())
                * BloodMoonHandler.getActiveMultiplier(mob.level());
        if (BuffMobsConfig.INSTANCE.dimensionScaling.mode.get()
                == BuffMobsConfig.DimensionScaling.DimensionScalingMode.OVERRIDE) {
            return Math.max(1.0, dim.damage * progression);
        }
        double presetDamage = preset != null ? preset.damage : 1.0;
        return Math.max(1.0, BuffMobsConfig.INSTANCE.attributes.damageMultiplier.get()
                * dim.damage * presetDamage * progression);
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
        for (BuffMobsConfig.DimensionScaling.DimensionSlot slot : slots) {
            String name = slot.dimensionName.get();
            if (!name.isEmpty() && name.equals(dim)) {
                return new DimensionMultipliers(
                        slot.healthMultiplier.get()      / 100.0,
                        slot.damageMultiplier.get()      / 100.0,
                        slot.speedMultiplier.get()       / 100.0,
                        slot.attackSpeedMultiplier.get() / 100.0,
                        (double) slot.armorAddition.get(),
                        (double) slot.armorToughnessAddition.get()
                );
            }
        }
        return DimensionMultipliers.IDENTITY;
    }

    public static void refreshInfiniteEffects(Mob mob) {
        if (BuffMobsConfig.INSTANCE.effects.duration.get() != -1 || !isValidMob(mob)
                || isPassiveAggressiveMob(mob)) return;
        boolean show   = BuffMobsConfig.INSTANCE.visualEffects.get();
        boolean undead = mob.getType().builtInRegistryHolder().is(EntityTypeTags.UNDEAD);

        refreshEffect(mob, MobEffects.STRENGTH,   BuffMobsConfig.INSTANCE.effects.strengthAmplifier.get(),   show);
        refreshEffect(mob, MobEffects.SPEED,      BuffMobsConfig.INSTANCE.effects.speedAmplifier.get(),      show);
        refreshEffect(mob, MobEffects.RESISTANCE, BuffMobsConfig.INSTANCE.effects.resistanceAmplifier.get(), show);

        int absAmp = BuffMobsConfig.INSTANCE.effects.absorptionAmplifier.get();
        if (absAmp > 0) {
            float expected = absAmp * 4.0f;
            MobEffectInstance cur = mob.getEffect(MobEffects.ABSORPTION);
            if (undead) {
                if (cur == null || (cur.getAmplifier() <= absAmp - 1 && cur.getDuration() < 1200)) {
                    mob.removeEffect(MobEffects.ABSORPTION);
                    mob.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, -1, absAmp - 1, false, show, true));
                }
                if (mob.getAbsorptionAmount() < expected * 0.5f && (cur == null || cur.getAmplifier() <= absAmp - 1)) {
                    mob.setAbsorptionAmount(expected);
                }
            } else {
                if (cur == null || (cur.getAmplifier() <= absAmp - 1 && cur.getDuration() < 1200)) {
                    refreshEffect(mob, MobEffects.ABSORPTION, absAmp, show);
                }
                if (mob.getAbsorptionAmount() < expected * 0.5f) {
                    mob.setAbsorptionAmount(expected);
                }
            }
        }

        if (!undead)
            refreshEffect(mob, MobEffects.REGENERATION, BuffMobsConfig.INSTANCE.effects.regenerationAmplifier.get(), show);
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
        if (mob.isRemoved() || !mob.isAlive()) return false;
        if (mob instanceof TamableAnimal t && t.isTame()) return false;

        String mobId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        String modId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).getNamespace();
        String dimId = getDimensionId(mob.level());

        boolean hostile = isHostileType(mob)
                || mob.getType().builtInRegistryHolder().is(EntityTypeTags.RAIDERS)
                || mob.getType().builtInRegistryHolder().is(EntityTypeTags.SKELETONS)
                || mob.getType().builtInRegistryHolder().is(EntityTypeTags.ZOMBIES)
                || isNeutralMob(mob);

        if (!hostile) {
            BuffMobsConfig.PassiveMobAggression.PassiveMobMode mode =
                    BuffMobsConfig.INSTANCE.passiveMobAggression.mode.get();
            if (mode != BuffMobsConfig.PassiveMobAggression.PassiveMobMode.OFF && isPassiveAggressiveMob(mob)) {
                hostile = true;
            }
        }

        if (!hostile && !isExplicitlyAllowed(mobId)) return false;

        boolean validDim = isValidDimension(dimId);
        boolean validMod = isValidModId(modId);
        boolean validMob = isValidMobId(mobId);

        if (!validDim) BuffMobsMod.LOGGER.debug("[BuffMobs] {} filtered by dimension: {}", mobId, dimId);
        if (!validMod) BuffMobsMod.LOGGER.debug("[BuffMobs] {} filtered by mod: {}",       mobId, modId);
        if (!validMob) BuffMobsMod.LOGGER.debug("[BuffMobs] {} in blacklist",               mobId);

        return validDim && validMod && validMob;
    }

    public static boolean isHostileType(Mob mob) {
        return mob instanceof Enemy || mob.getType().getCategory() == net.minecraft.world.entity.MobCategory.MONSTER;
    }

    private static boolean isExplicitlyAllowed(String mobId) {
        if (BuffMobsConfig.INSTANCE.mobFilter.whitelist.get().contains(mobId)) return true;
        if (BuffMobsConfig.INSTANCE.mobPresets.enabled.get()) {
            for (String mapping : BuffMobsConfig.INSTANCE.mobPresets.mobMapping.get()) {
                String[] parts = mapping.split(":");
                if (parts.length >= 3 && (parts[0] + ":" + parts[1]).equals(mobId)) return true;
            }
        }
        return false;
    }

    public static boolean isPassiveAggressiveMob(Mob mob) {
        if (mob.isRemoved() || !mob.isAlive()) return false;
        if (mob instanceof TamableAnimal t && t.isTame()) return false;
        if (isHostileType(mob)) return false;
        if (mob instanceof NeutralMob) return false;

        String mobId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        BuffMobsConfig.PassiveMobAggression cfg = BuffMobsConfig.INSTANCE.passiveMobAggression;

        if (cfg.blacklist.get().contains(mobId)) return false;
        if (!cfg.whitelist.get().isEmpty()) return cfg.whitelist.get().contains(mobId);
        return true;
    }

    public static double getPassiveDamageForMob(Mob mob) {
        BuffMobsConfig.PassiveMobAggression cfg = BuffMobsConfig.INSTANCE.passiveMobAggression;
        double base = cfg.baseDamage.get();
        if (cfg.scaleWithHealth.get()) {
            base += mob.getMaxHealth() * cfg.healthScaleFactor.get();
        }
        return base;
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
        double attrHp    = BuffMobsConfig.INSTANCE.attributes.healthMultiplier.get();
        double attrDmg   = BuffMobsConfig.INSTANCE.attributes.damageMultiplier.get();
        double attrSpd   = BuffMobsConfig.INSTANCE.attributes.speedMultiplier.get();
        double attrAspd  = BuffMobsConfig.INSTANCE.attributes.attackSpeedMultiplier.get();
        double attrArm   = BuffMobsConfig.INSTANCE.attributes.armorAddition.get();
        double attrTough = BuffMobsConfig.INSTANCE.attributes.armorToughnessAddition.get();

        double presetHp    = preset != null ? preset.health         : 1.0;
        double presetDmg   = preset != null ? preset.damage         : 1.0;
        double presetSpd   = preset != null ? preset.speed          : 1.0;
        double presetAspd  = preset != null ? preset.attackSpeed    : 1.0;
        double presetArm   = preset != null ? preset.armor          : 0.0;
        double presetTough = preset != null ? preset.armorToughness : 0.0;

        boolean overrideDim = preset != null && BuffMobsConfig.INSTANCE.mobPresets.overrideDimensionScaling.get();
        DimensionMultipliers effectiveDim = overrideDim ? DimensionMultipliers.IDENTITY : dim;

        BuffMobsConfig.DimensionScaling.DimensionScalingMode dimMode =
                BuffMobsConfig.INSTANCE.dimensionScaling.mode.get();

        boolean isPassive = isPassiveAggressiveMob(mob);

        handleZombieLeaderBonus(mob);

        double finalHealth, finalDamage, finalSpeed, finalAttackSpeed;
        double finalArmor, finalToughness;

        if (dimMode == BuffMobsConfig.DimensionScaling.DimensionScalingMode.OVERRIDE) {
            finalHealth = dim.health * dayMult;
            finalDamage = dim.damage * dayMult;
            finalSpeed = dim.speed * dayMult;
            finalAttackSpeed = dim.attackSpeed * dayMult;
            finalArmor = dim.armor * dayMult;
            finalToughness = dim.armorToughness * dayMult;
        } else {
            finalHealth = attrHp * effectiveDim.health * presetHp * dayMult;
            finalDamage = attrDmg * effectiveDim.damage * presetDmg * dayMult;
            finalSpeed = attrSpd * effectiveDim.speed * presetSpd * dayMult;
            finalAttackSpeed = attrAspd * effectiveDim.attackSpeed * presetAspd * dayMult;
            finalArmor = (attrArm + effectiveDim.armor + presetArm) * dayMult;
            finalToughness = (attrTough + effectiveDim.armorToughness + presetTough) * dayMult;
        }

        applyMultiplier(mob, Attributes.MAX_HEALTH, HEALTH_MOD_ID,
                finalHealth, getHealthMultiplierOperation(mob));
        applySpeedBonus(mob, SPEED_MOD_ID, finalSpeed);
        applyMultiplier(mob, Attributes.ATTACK_SPEED, ATTACK_SPEED_MOD_ID, finalAttackSpeed);
        applyAddition(mob, Attributes.ARMOR, ARMOR_MOD_ID, finalArmor);
        applyAddition(mob, Attributes.ARMOR_TOUGHNESS, TOUGHNESS_MOD_ID, finalToughness);

        if (isPassive) {
            applyPassiveAggressionDamage(mob);
        } else {
            applyMultiplier(mob, Attributes.ATTACK_DAMAGE, DAMAGE_MOD_ID, finalDamage);
        }
    }

    private static void handleZombieLeaderBonus(Mob mob) {
        if (!(mob instanceof Zombie)) return;
        BuffMobsConfig.ZombieHandling cfg = BuffMobsConfig.INSTANCE.zombieHandling;
        if (!cfg.disableLeaderZombies.get()) return;
        AttributeInstance hpInst = mob.getAttribute(Attributes.MAX_HEALTH);
        if (hpInst != null) hpInst.removeModifier(LEADER_ZOMBIE_BONUS_ID);
    }

    private static AttributeModifier.Operation getHealthMultiplierOperation(Mob mob) {
        if (!(mob instanceof Zombie)) return AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
        BuffMobsConfig.ZombieHandling cfg = BuffMobsConfig.INSTANCE.zombieHandling;
        if (cfg.disableLeaderZombies.get() || cfg.excludeLeaderBonusFromMultiplier.get()) {
            return AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
        }
        AttributeInstance hpInst = mob.getAttribute(Attributes.MAX_HEALTH);
        if (hpInst == null || hpInst.getModifier(LEADER_ZOMBIE_BONUS_ID) == null) {
            return AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
        }
        return AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
    }

    public static Double getDimensionMaxHealthOverride(Mob mob) {
        BuffMobsConfig.DimensionMaxHealth cfg = BuffMobsConfig.INSTANCE.dimensionMaxHealth;
        if (!cfg.enabled.get()) return null;

        String dim = getDimensionId(mob.level());
        String mobId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();

        BuffMobsConfig.DimensionMaxHealth.Slot[] slots = {
                cfg.slot1, cfg.slot2, cfg.slot3, cfg.slot4, cfg.slot5
        };

        for (BuffMobsConfig.DimensionMaxHealth.Slot slot : slots) {
            String name = slot.dimensionId.get();
            if (name.isEmpty() || !name.equals(dim)) continue;
            if (slot.denylist.get().contains(mobId)) return null;
            if (slot.useAllowlist.get() && !slot.allowlist.get().contains(mobId)) return null;
            return slot.maxHealth.get();
        }
        return null;
    }

    private static void applyDimensionMaxHealthOverride(Mob mob, double targetMaxHealth) {
        AttributeInstance inst = mob.getAttribute(Attributes.MAX_HEALTH);
        if (inst == null) return;
        inst.removeModifier(HEALTH_MOD_ID);
        inst.setBaseValue(targetMaxHealth);
        if (mob.getHealth() > inst.getValue()) {
            mob.setHealth((float) inst.getValue());
        }
    }

    private static void applyPassiveAggressionDamage(Mob mob) {
        AttributeInstance dmgAttr = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmgAttr == null) return;
        dmgAttr.removeModifier(DAMAGE_MOD_ID);
        double baseDmg = getPassiveDamageForMob(mob);
        double current = dmgAttr.getBaseValue();
        if (baseDmg > current) {
            double bonus = baseDmg - current;
            dmgAttr.addPermanentModifier(new AttributeModifier(DAMAGE_MOD_ID, bonus,
                    AttributeModifier.Operation.ADD_VALUE));
        }
    }

    private static void applyMultiplier(Mob mob,
                                        Holder<net.minecraft.world.entity.ai.attributes.Attribute> attr,
                                        Identifier id, double finalMult) {
        applyMultiplier(mob, attr, id, finalMult, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    private static void applyMultiplier(Mob mob,
                                        Holder<net.minecraft.world.entity.ai.attributes.Attribute> attr,
                                        Identifier id, double finalMult, AttributeModifier.Operation operation) {
        AttributeInstance inst = mob.getAttribute(attr);
        if (inst == null) return;
        inst.removeModifier(id);
        if (finalMult > 1.0) {
            inst.addPermanentModifier(new AttributeModifier(id, finalMult - 1.0, operation));
        }
    }

    private static void applySpeedBonus(Mob mob, Identifier id, double finalMult) {
        AttributeInstance inst = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (inst == null) return;
        inst.removeModifier(id);
        if (finalMult > 1.0) {
            double capped = Math.min(finalMult, 2.0);
            double bonus  = (capped - 1.0) * 0.3;
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
        int duration = BuffMobsConfig.INSTANCE.effects.duration.get() == -1
                ? -1 : BuffMobsConfig.INSTANCE.effects.duration.get() * 20;
        boolean show   = BuffMobsConfig.INSTANCE.visualEffects.get();
        boolean undead = mob.getType().builtInRegistryHolder().is(EntityTypeTags.UNDEAD);

        if (!isPassiveAggressiveMob(mob)) {
            addEffect(mob, MobEffects.STRENGTH,   BuffMobsConfig.INSTANCE.effects.strengthAmplifier.get(),   duration, show);
            addEffect(mob, MobEffects.SPEED,      BuffMobsConfig.INSTANCE.effects.speedAmplifier.get(),      duration, show);
            addEffect(mob, MobEffects.RESISTANCE, BuffMobsConfig.INSTANCE.effects.resistanceAmplifier.get(), duration, show);

            int absAmp = BuffMobsConfig.INSTANCE.effects.absorptionAmplifier.get();
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
                addEffect(mob, MobEffects.REGENERATION, BuffMobsConfig.INSTANCE.effects.regenerationAmplifier.get(), duration, show);
        }
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
        List<? extends String> bl = BuffMobsConfig.INSTANCE.dimensionFilter.blacklist.get();
        if (bl.contains(dim)) return false;
        if (BuffMobsConfig.INSTANCE.dimensionFilter.useWhitelist.get())
            return BuffMobsConfig.INSTANCE.dimensionFilter.whitelist.get().contains(dim);
        return true;
    }

    private static boolean isValidModId(String modId) {
        List<? extends String> bl = BuffMobsConfig.INSTANCE.modidFilter.blacklist.get();
        if (bl.contains(modId)) return false;
        if (BuffMobsConfig.INSTANCE.modidFilter.useWhitelist.get())
            return BuffMobsConfig.INSTANCE.modidFilter.whitelist.get().contains(modId);
        return true;
    }

    private static boolean isValidMobId(String mobId) {
        List<? extends String> bl = BuffMobsConfig.INSTANCE.mobFilter.blacklist.get();
        if (bl.contains(mobId)) return false;
        if (BuffMobsConfig.INSTANCE.mobFilter.useWhitelist.get())
            return BuffMobsConfig.INSTANCE.mobFilter.whitelist.get().contains(mobId);
        return true;
    }

    public static double calculateFinalMultiplier(double base, double dim, double day) { return base * dim * day; }
    public static double calculateFinalAddition(double base, double dim, double day)   { return (base + dim) * day; }

    public static class DimensionMultipliers {
        public static final DimensionMultipliers IDENTITY = new DimensionMultipliers(1.0, 1.0, 1.0, 1.0, 0.0, 0.0);

        public final double health, damage, speed, attackSpeed, armor, armorToughness;
        public DimensionMultipliers(double health, double damage, double speed,
                                    double attackSpeed, double armor, double armorToughness) {
            this.health = health; this.damage = damage; this.speed = speed;
            this.attackSpeed = attackSpeed; this.armor = armor; this.armorToughness = armorToughness;
        }
    }
}
