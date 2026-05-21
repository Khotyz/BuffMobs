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
import net.minecraft.world.entity.player.Player;
import static com.khotyz.buffmobs.util.DimensionUtil.getDimensionId;

import java.util.List;

public class MobBuffUtil {

    private static final Identifier HEALTH_MOD_ID       = Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "health");
    private static final Identifier DAMAGE_MOD_ID       = Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "damage");
    private static final Identifier SPEED_MOD_ID        = Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "speed");
    private static final Identifier ATTACK_SPEED_MOD_ID = Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "attack_speed");
    private static final Identifier ARMOR_MOD_ID        = Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "armor");
    private static final Identifier TOUGHNESS_MOD_ID    = Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "toughness");

    public static void applyBuffs(Mob mob) {
        if (!BuffMobsConfig.INSTANCE.enabled.get() || !isValidMob(mob)) {
            removeAllModifiers(mob);
            return;
        }

        double dayMult = getDayMultiplier(mob.level().getDayTime());
        DimensionMultipliers dim = getDimensionMultipliers(mob);
        MobPresetUtil.PresetMultipliers preset = MobPresetUtil.getPresetForMob(mob);

        BuffMobsMod.LOGGER.debug("[BuffMobs] Applying buffs to {} | day={} dimHP={} preset={}",
                BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()), dayMult, dim.health, preset != null);
        applyAllLayers(mob, dayMult, dim, preset);

        applyStatusEffects(mob);
        mob.setHealth(mob.getMaxHealth());
    }

    public static void removeAllModifiers(Mob mob) {
        removeModifier(mob, Attributes.MAX_HEALTH,      HEALTH_MOD_ID);
        removeModifier(mob, Attributes.ATTACK_DAMAGE,   DAMAGE_MOD_ID);
        removeModifier(mob, Attributes.MOVEMENT_SPEED,  SPEED_MOD_ID);
        removeModifier(mob, Attributes.ATTACK_SPEED,    ATTACK_SPEED_MOD_ID);
        removeModifier(mob, Attributes.ARMOR,           ARMOR_MOD_ID);
        removeModifier(mob, Attributes.ARMOR_TOUGHNESS, TOUGHNESS_MOD_ID);
    }

    private static void removeModifier(Mob mob,
                                       Holder<net.minecraft.world.entity.ai.attributes.Attribute> attr, Identifier id) {
        AttributeInstance inst = mob.getAttribute(attr);
        if (inst != null) inst.removeModifier(id);
    }

    public static double getDayMultiplier(long worldTime) {
        if (!BuffMobsConfig.INSTANCE.dayScaling.enabled.get()) return 1.0;
        long days      = worldTime / 24000L;
        long intervals = days / Math.max(1, BuffMobsConfig.INSTANCE.dayScaling.interval.get());
        double mult    = 1.0 + (intervals * BuffMobsConfig.INSTANCE.dayScaling.multiplier.get());
        return Math.min(mult, BuffMobsConfig.INSTANCE.dayScaling.maxMultiplier.get());
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
        boolean undead = mob.getType().is(EntityTypeTags.UNDEAD);

        refreshEffect(mob, MobEffects.STRENGTH,   BuffMobsConfig.INSTANCE.effects.strengthAmplifier.get(),   show);
        refreshEffect(mob, MobEffects.SPEED,      BuffMobsConfig.INSTANCE.effects.speedAmplifier.get(),      show);
        refreshEffect(mob, MobEffects.RESISTANCE, BuffMobsConfig.INSTANCE.effects.resistanceAmplifier.get(), show);
        refreshEffect(mob, MobEffects.ABSORPTION, BuffMobsConfig.INSTANCE.effects.absorptionAmplifier.get(), show);

        if (!undead)
            refreshEffect(mob, MobEffects.REGENERATION, BuffMobsConfig.INSTANCE.effects.regenerationAmplifier.get(), show);
    }

    public static void applyPoisonToPlayer(Player player, int duration) {
        player.addEffect(new MobEffectInstance(MobEffects.POISON,      duration * 20, 0));
    }
    public static void applySlownessToPlayer(Player player, int duration) {
        player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS,    duration * 20, 0));
    }
    public static void applyWitherToPlayer(Player player, int duration) {
        player.addEffect(new MobEffectInstance(MobEffects.WITHER,      duration * 20, 0));
    }

    public static boolean isValidMob(Mob mob) {
        if (mob.isRemoved() || !mob.isAlive()) return false;
        if (mob instanceof TamableAnimal t && t.isTame()) return false;
        String mobId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        String modId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).getNamespace();
        String dimId = getDimensionId(mob.level());

        boolean hostile = mob instanceof Enemy
                || mob.getType().is(EntityTypeTags.RAIDERS)
                || mob.getType().is(EntityTypeTags.SKELETONS)
                || mob.getType().is(EntityTypeTags.ZOMBIES)
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
        double presetHp    = preset != null ? preset.health      : 1.0;
        double presetDmg   = preset != null ? preset.damage      : 1.0;
        double presetSpd   = preset != null ? preset.speed       : 1.0;
        double presetAspd  = preset != null ? preset.attackSpeed : 1.0;
        double presetArm   = preset != null ? preset.armor       : 0.0;
        double presetTough = preset != null ? preset.armorToughness : 0.0;

        applyMultiplier(mob, Attributes.MAX_HEALTH,      HEALTH_MOD_ID,       attrHp   * dim.health      * presetHp   * dayMult);
        applyMultiplier(mob, Attributes.ATTACK_DAMAGE,   DAMAGE_MOD_ID,       attrDmg  * dim.damage      * presetDmg  * dayMult);
        applySpeedBonus(mob,                             SPEED_MOD_ID,        attrSpd  * dim.speed       * presetSpd  * dayMult);
        applyMultiplier(mob, Attributes.ATTACK_SPEED,    ATTACK_SPEED_MOD_ID, attrAspd * dim.attackSpeed * presetAspd * dayMult);
        applyAddition  (mob, Attributes.ARMOR,           ARMOR_MOD_ID,       (attrArm   + dim.armor           + presetArm)   * dayMult);
        applyAddition  (mob, Attributes.ARMOR_TOUGHNESS, TOUGHNESS_MOD_ID,   (attrTough + dim.armorToughness  + presetTough) * dayMult);
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
        boolean show = BuffMobsConfig.INSTANCE.visualEffects.get();
        boolean undead = mob.getType().is(EntityTypeTags.UNDEAD);

        addEffect(mob, MobEffects.STRENGTH,    BuffMobsConfig.INSTANCE.effects.strengthAmplifier.get(),    duration, show);
        addEffect(mob, MobEffects.SPEED,       BuffMobsConfig.INSTANCE.effects.speedAmplifier.get(),       duration, show);
        addEffect(mob, MobEffects.RESISTANCE,  BuffMobsConfig.INSTANCE.effects.resistanceAmplifier.get(),  duration, show);
        addEffect(mob, MobEffects.ABSORPTION,  BuffMobsConfig.INSTANCE.effects.absorptionAmplifier.get(),  duration, show);
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