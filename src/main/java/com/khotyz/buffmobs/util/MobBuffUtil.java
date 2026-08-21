package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import static com.khotyz.buffmobs.util.DimensionUtil.getDimensionId;

import java.util.List;

public class MobBuffUtil {

    private static final ResourceLocation HEALTH_MOD_ID       = ResourceLocation.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "health");
    private static final ResourceLocation DAMAGE_MOD_ID       = ResourceLocation.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "damage");
    private static final ResourceLocation SPEED_MOD_ID        = ResourceLocation.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "speed");
    private static final ResourceLocation ATTACK_SPEED_MOD_ID = ResourceLocation.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "attack_speed");
    private static final ResourceLocation ARMOR_MOD_ID        = ResourceLocation.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "armor");
    private static final ResourceLocation TOUGHNESS_MOD_ID    = ResourceLocation.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "toughness");
    private static final ResourceLocation DIMENSION_HEALTH_OVERRIDE_ID = ResourceLocation.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "dimension_health_override");
    private static final ResourceLocation LEADER_ZOMBIE_BONUS_ID = ResourceLocation.withDefaultNamespace("leader_zombie_bonus");
    private static final DimensionMultipliers NEUTRAL_DIMENSION_MULTIPLIERS = new DimensionMultipliers(1.0, 1.0, 1.0, 1.0, 0.0, 0.0);

    public static long getOverworldDayTime(Level level) {
        if (level instanceof ServerLevel sl) {
            MinecraftServer server = sl.getServer();
            if (server != null) {
                ServerLevel overworld = server.overworld();
                if (overworld != null) {
                    return overworld.getGameTime();
                }
            }
            return sl.getGameTime();
        }
        return 0L;
    }

    public static void applyBuffs(Mob mob) {
        if (!BuffMobsConfig.INSTANCE.enabled.get()) {
            removeAllModifiers(mob);
            return;
        }

        double dimensionMaxHealthTarget = resolveActiveDimensionMaxHealth(mob);

        if (!isValidMob(mob)) {
            removeModifier(mob, Attributes.MAX_HEALTH,      HEALTH_MOD_ID);
            removeModifier(mob, Attributes.ATTACK_DAMAGE,   DAMAGE_MOD_ID);
            removeModifier(mob, Attributes.MOVEMENT_SPEED,  SPEED_MOD_ID);
            removeModifier(mob, Attributes.ATTACK_SPEED,    ATTACK_SPEED_MOD_ID);
            removeModifier(mob, Attributes.ARMOR,           ARMOR_MOD_ID);
            removeModifier(mob, Attributes.ARMOR_TOUGHNESS, TOUGHNESS_MOD_ID);
            applyDimensionMaxHealthOverride(mob, dimensionMaxHealthTarget);
            return;
        }

        double dayMult = getDayMultiplier(getOverworldDayTime(mob.level()));
        DimensionMultipliers dim = getDimensionMultipliers(mob);
        MobPresetUtil.PresetMultipliers preset = MobPresetUtil.getPresetForMob(mob);

        if (preset != null && BuffMobsConfig.INSTANCE.mobPresets.overrideDimensionScaling.get()) {
            dim = NEUTRAL_DIMENSION_MULTIPLIERS;
        }

        BuffMobsMod.LOGGER.debug("[BuffMobs] Applying buffs to {} | day={} dimHP={} preset={}",
                BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()), dayMult, dim.health, preset != null);

        double oldMaxHealth = mob.getMaxHealth();
        double oldHealth = mob.getHealth();

        AttributeModifier strippedLeaderBonus = handleLeaderZombieBonus(mob);

        applyAllLayers(mob, dayMult, dim, preset, dimensionMaxHealthTarget > 0.0);

        if (strippedLeaderBonus != null) restoreLeaderZombieBonus(mob, strippedLeaderBonus);

        applyDimensionMaxHealthOverride(mob, dimensionMaxHealthTarget);

        applyStatusEffects(mob);
        syncHealth(mob, oldMaxHealth, oldHealth);
    }

    public static void removeAllModifiers(Mob mob) {
        removeModifier(mob, Attributes.MAX_HEALTH,      HEALTH_MOD_ID);
        removeModifier(mob, Attributes.ATTACK_DAMAGE,   DAMAGE_MOD_ID);
        removeModifier(mob, Attributes.MOVEMENT_SPEED,  SPEED_MOD_ID);
        removeModifier(mob, Attributes.ATTACK_SPEED,    ATTACK_SPEED_MOD_ID);
        removeModifier(mob, Attributes.ARMOR,           ARMOR_MOD_ID);
        removeModifier(mob, Attributes.ARMOR_TOUGHNESS, TOUGHNESS_MOD_ID);
        removeModifier(mob, Attributes.MAX_HEALTH,      DIMENSION_HEALTH_OVERRIDE_ID);
    }

    private static void removeModifier(Mob mob,
                                       Holder<net.minecraft.world.entity.ai.attributes.Attribute> attr,
                                       ResourceLocation id) {
        AttributeInstance inst = mob.getAttribute(attr);
        if (inst != null) inst.removeModifier(id);
    }

    public static double getDayMultiplier(long overworldDayTime) {
        if (!BuffMobsConfig.INSTANCE.dayScaling.enabled.get()) return 1.0;
        long days      = overworldDayTime / 24000L;
        long intervals = days / Math.max(1, BuffMobsConfig.INSTANCE.dayScaling.interval.get());
        double mult    = 1.0 + (intervals * BuffMobsConfig.INSTANCE.dayScaling.multiplier.get());
        double max = BuffMobsConfig.INSTANCE.dayScaling.maxMultiplier.get();
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
        return new DimensionMultipliers(1.0, 1.0, 1.0, 1.0, 0.0, 0.0);
    }

    public static void refreshInfiniteEffects(Mob mob) {
        if (BuffMobsConfig.INSTANCE.effects.duration.get() != -1 || !isValidMob(mob)) return;
        boolean show   = BuffMobsConfig.INSTANCE.visualEffects.get();
        boolean undead = mob.getType().builtInRegistryHolder().is(EntityTypeTags.UNDEAD);

        refreshEffect(mob, MobEffects.DAMAGE_BOOST,    BuffMobsConfig.INSTANCE.effects.strengthAmplifier.get(),   show);
        refreshEffect(mob, MobEffects.MOVEMENT_SPEED,  BuffMobsConfig.INSTANCE.effects.speedAmplifier.get(),      show);
        refreshEffect(mob, MobEffects.DAMAGE_RESISTANCE, BuffMobsConfig.INSTANCE.effects.resistanceAmplifier.get(), show);
        refreshEffect(mob, MobEffects.ABSORPTION,      BuffMobsConfig.INSTANCE.effects.absorptionAmplifier.get(), show);

        if (!undead)
            refreshEffect(mob, MobEffects.REGENERATION, BuffMobsConfig.INSTANCE.effects.regenerationAmplifier.get(), show);
    }

    public static void applyPoisonToPlayer(Player player, int duration) {
        player.addEffect(new MobEffectInstance(MobEffects.POISON,             duration * 20, 0));
    }
    public static void applySlownessToPlayer(Player player, int duration) {
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,  duration * 20, 0));
    }
    public static void applyWitherToPlayer(Player player, int duration) {
        player.addEffect(new MobEffectInstance(MobEffects.WITHER,             duration * 20, 0));
    }

    public static boolean isValidMob(Mob mob) {
        if (mob.isRemoved() || !mob.isAlive()) return false;
        if (mob instanceof TamableAnimal t && t.isTame()) return false;

        String mobId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        String modId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).getNamespace();
        String dimId = getDimensionId(mob.level());

        boolean hostile = mob instanceof Enemy
                || mob.getType().builtInRegistryHolder().is(EntityTypeTags.RAIDERS)
                || mob.getType().builtInRegistryHolder().is(EntityTypeTags.SKELETONS)
                || mob.getType().builtInRegistryHolder().is(EntityTypeTags.ZOMBIES)
                || isNeutralMob(mob);

        if (!hostile && !isExplicitlyAllowed(mobId)) return false;

        boolean validDim = isValidDimension(dimId);
        boolean validMod = isValidModId(modId);
        boolean validMob = isValidMobId(mobId);

        if (!validDim) BuffMobsMod.LOGGER.debug("[BuffMobs] {} filtered by dimension: {}", mobId, dimId);
        if (!validMod) BuffMobsMod.LOGGER.debug("[BuffMobs] {} filtered by mod: {}",       mobId, modId);
        if (!validMob) BuffMobsMod.LOGGER.debug("[BuffMobs] {} in blacklist",               mobId);

        return validDim && validMod && validMob;
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
        if (mob instanceof Enemy) return false;
        if (mob instanceof NeutralMob) return false;

        BuffMobsConfig.PassiveMobAggression cfg = BuffMobsConfig.INSTANCE.passiveMobAggression;
        if (cfg.mode.get() == BuffMobsConfig.PassiveMobAggression.PassiveMode.OFF) return false;

        String mobId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        if (cfg.blacklist.get().contains(mobId)) return false;
        if (!cfg.whitelist.get().isEmpty()) return cfg.whitelist.get().contains(mobId);
        return true;
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

    private static AttributeModifier handleLeaderZombieBonus(Mob mob) {
        if (!(mob instanceof Zombie)) return null;

        AttributeInstance hp = mob.getAttribute(Attributes.MAX_HEALTH);
        if (hp == null) return null;

        AttributeModifier existing = hp.getModifier(LEADER_ZOMBIE_BONUS_ID);
        if (existing == null) return null;

        BuffMobsConfig.ZombieHandling cfg = BuffMobsConfig.INSTANCE.zombieHandling;

        if (cfg.disableLeaderZombies.get()) {
            hp.removeModifier(LEADER_ZOMBIE_BONUS_ID);
            BuffMobsMod.LOGGER.debug("[BuffMobs] Removed leader zombie bonus from {}", mob.getType());
            return null;
        }

        if (cfg.excludeLeaderBonusFromMultiplier.get()) {
            hp.removeModifier(LEADER_ZOMBIE_BONUS_ID);
            return existing;
        }

        return null;
    }

    private static void restoreLeaderZombieBonus(Mob mob, AttributeModifier modifier) {
        AttributeInstance hp = mob.getAttribute(Attributes.MAX_HEALTH);
        if (hp == null) return;
        if (hp.getModifier(modifier.id()) == null) {
            hp.addPermanentModifier(modifier);
        }
    }

    private static double resolveActiveDimensionMaxHealth(Mob mob) {
        BuffMobsConfig.DimensionMaxHealth cfg = BuffMobsConfig.INSTANCE.dimensionMaxHealth;
        if (!cfg.enabled.get()) return 0.0;

        String mobId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        if (cfg.denylist.get().contains(mobId)) return 0.0;
        if (cfg.useAllowlist.get() && !cfg.allowlist.get().contains(mobId)) return 0.0;

        String dim = getDimensionId(mob.level());
        return resolveDimensionMaxHealth(cfg, dim);
    }

    private static void applyDimensionMaxHealthOverride(Mob mob, double target) {
        AttributeInstance hp = mob.getAttribute(Attributes.MAX_HEALTH);
        if (hp == null) return;

        hp.removeModifier(DIMENSION_HEALTH_OVERRIDE_ID);
        if (target <= 0.0) return;

        double current = hp.getValue();
        double deltaAmount = target - current;
        if (deltaAmount != 0.0) {
            hp.addPermanentModifier(new AttributeModifier(DIMENSION_HEALTH_OVERRIDE_ID, deltaAmount,
                    AttributeModifier.Operation.ADD_VALUE));
        }
    }

    private static double resolveDimensionMaxHealth(BuffMobsConfig.DimensionMaxHealth cfg, String dim) {
        BuffMobsConfig.DimensionMaxHealth.DimensionHealthSlot[] slots = {
                cfg.slot1, cfg.slot2, cfg.slot3, cfg.slot4, cfg.slot5
        };
        for (BuffMobsConfig.DimensionMaxHealth.DimensionHealthSlot slot : slots) {
            String name = slot.dimensionName.get();
            if (!name.isEmpty() && name.equals(dim)) return slot.maxHealth.get();
        }
        return 0.0;
    }

    private static void syncHealth(Mob mob, double oldMaxHealth, double oldHealth) {
        if (!BuffMobsConfig.INSTANCE.healthSync.enabled.get()) {
            mob.setHealth((float) mob.getMaxHealth());
            return;
        }

        double newMaxHealth = mob.getMaxHealth();

        switch (BuffMobsConfig.INSTANCE.healthSync.mode.get()) {
            case OVERRIDE -> mob.setHealth((float) newMaxHealth);
            case STACK -> {
                double delta = newMaxHealth - oldMaxHealth;
                double newHealth = oldHealth + delta;
                newHealth = Math.max(1.0, Math.min(newHealth, newMaxHealth));
                mob.setHealth((float) newHealth);
            }
        }
    }

    private static void applyAllLayers(Mob mob, double dayMult, DimensionMultipliers dim,
                                       MobPresetUtil.PresetMultipliers preset, boolean dimensionHealthCapActive) {
        BuffMobsConfig.DimensionScaling.DimensionScalingMode dimMode = BuffMobsConfig.INSTANCE.dimensionScaling.mode.get();

        if (dimMode == BuffMobsConfig.DimensionScaling.DimensionScalingMode.OVERRIDE) {
            if (dimensionHealthCapActive) {
                removeModifier(mob, Attributes.MAX_HEALTH, HEALTH_MOD_ID);
            } else {
                applyMultiplier(mob, Attributes.MAX_HEALTH, HEALTH_MOD_ID, dim.health * dayMult);
            }
            applyMultiplier(mob, Attributes.ATTACK_DAMAGE,   DAMAGE_MOD_ID,       dim.damage * dayMult);
            applySpeedBonus(mob,                             SPEED_MOD_ID,        dim.speed * dayMult);
            applyMultiplier(mob, Attributes.ATTACK_SPEED,    ATTACK_SPEED_MOD_ID, dim.attackSpeed * dayMult);
            applyAddition  (mob, Attributes.ARMOR,           ARMOR_MOD_ID,        dim.armor * dayMult);
            applyAddition  (mob, Attributes.ARMOR_TOUGHNESS, TOUGHNESS_MOD_ID,    dim.armorToughness * dayMult);
            return;
        }

        double attrHp    = BuffMobsConfig.INSTANCE.attributes.healthMultiplier.get();
        double attrDmg   = BuffMobsConfig.INSTANCE.attributes.damageMultiplier.get();
        double attrSpd   = BuffMobsConfig.INSTANCE.attributes.speedMultiplier.get();
        double attrAspd  = BuffMobsConfig.INSTANCE.attributes.attackSpeedMultiplier.get();
        double attrArm   = BuffMobsConfig.INSTANCE.attributes.armorAddition.get();
        double attrTough = BuffMobsConfig.INSTANCE.attributes.armorToughnessAddition.get();

        double presetHp    = preset != null ? preset.health      : 1.0;
        double presetDmg   = preset != null ? preset.damage      : 1.0;
        double presetSpd   = preset != null ? preset.speed       : 1.0;
        double presetAspd  = preset != null ? preset.attackSpeed : 1.0;
        double presetArm   = preset != null ? preset.armor       : 0.0;
        double presetTough = preset != null ? preset.armorToughness : 0.0;

        if (dimensionHealthCapActive) {
            removeModifier(mob, Attributes.MAX_HEALTH, HEALTH_MOD_ID);
        } else {
            applyMultiplier(mob, Attributes.MAX_HEALTH, HEALTH_MOD_ID, attrHp * dim.health * presetHp * dayMult);
        }
        applyMultiplier(mob, Attributes.ATTACK_DAMAGE,   DAMAGE_MOD_ID,       attrDmg  * dim.damage      * presetDmg  * dayMult);
        applySpeedBonus(mob,                             SPEED_MOD_ID,        attrSpd  * dim.speed       * presetSpd  * dayMult);
        applyMultiplier(mob, Attributes.ATTACK_SPEED,    ATTACK_SPEED_MOD_ID, attrAspd * dim.attackSpeed * presetAspd * dayMult);
        applyAddition  (mob, Attributes.ARMOR,           ARMOR_MOD_ID,       (attrArm   + dim.armor           + presetArm)   * dayMult);
        applyAddition  (mob, Attributes.ARMOR_TOUGHNESS, TOUGHNESS_MOD_ID,   (attrTough + dim.armorToughness  + presetTough) * dayMult);
    }

    private static void applyMultiplier(Mob mob,
                                        Holder<net.minecraft.world.entity.ai.attributes.Attribute> attr,
                                        ResourceLocation id, double finalMult) {
        AttributeInstance inst = mob.getAttribute(attr);
        if (inst == null) return;
        inst.removeModifier(id);
        if (finalMult > 1.0) {
            inst.addPermanentModifier(new AttributeModifier(id, finalMult - 1.0,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
    }

    private static void applySpeedBonus(Mob mob, ResourceLocation id, double finalMult) {
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
                                      ResourceLocation id, double amount) {
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
        boolean show = BuffMobsConfig.INSTANCE.visualEffects.get();
        boolean undead = mob.getType().builtInRegistryHolder().is(EntityTypeTags.UNDEAD);

        addEffect(mob, MobEffects.DAMAGE_BOOST,      BuffMobsConfig.INSTANCE.effects.strengthAmplifier.get(),    duration, show);
        addEffect(mob, MobEffects.MOVEMENT_SPEED,    BuffMobsConfig.INSTANCE.effects.speedAmplifier.get(),       duration, show);
        addEffect(mob, MobEffects.DAMAGE_RESISTANCE, BuffMobsConfig.INSTANCE.effects.resistanceAmplifier.get(),  duration, show);
        addEffect(mob, MobEffects.ABSORPTION,        BuffMobsConfig.INSTANCE.effects.absorptionAmplifier.get(),  duration, show);

        if (!undead)
            addEffect(mob, MobEffects.REGENERATION, BuffMobsConfig.INSTANCE.effects.regenerationAmplifier.get(), duration, show);
    }

    private static void refreshEffect(Mob mob, Holder<MobEffect> effect, int amp, boolean show) {
        if (amp <= 0) return;
        MobEffectInstance cur = mob.getEffect(effect);
        if (cur == null || cur.getDuration() < 1200) {
            mob.removeEffect(effect);
            mob.addEffect(new MobEffectInstance(effect, -1, amp - 1, false, show, true));
        }
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
        public final double health, damage, speed, attackSpeed, armor, armorToughness;
        public DimensionMultipliers(double health, double damage, double speed,
                                    double attackSpeed, double armor, double armorToughness) {
            this.health = health; this.damage = damage; this.speed = speed;
            this.attackSpeed = attackSpeed; this.armor = armor; this.armorToughness = armorToughness;
        }
    }
}