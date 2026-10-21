package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.util.MobBuffUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TraitsHandler {

    public enum Trait {
        FLAME, FROST, TOXIC, VOLTAIC, VAMPIRIC, ARMORED;

        public String translationKey() {
            return "buffmobs.trait." + name().toLowerCase(Locale.ROOT);
        }
    }

    private static final Identifier TRAIT_ARMOR_MOD_ID = Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "trait_armor");
    private static final Identifier TRAIT_TOUGHNESS_MOD_ID = Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "trait_toughness");
    private static final RandomSource RANDOM = RandomSource.create();

    private static final Set<UUID> EVALUATED_MOBS = new HashSet<>();
    private static final Map<UUID, Trait> MOB_TRAITS = new HashMap<>();
    private static boolean applyingChainDamage = false;

    public static boolean isEnabled() {
        return BuffMobsConfig.INSTANCE.enabled && BuffMobsConfig.INSTANCE.traits.enabled;
    }

    public static Trait getTrait(Mob mob) {
        return MOB_TRAITS.get(mob.getUUID());
    }

    public static void onMobInitialized(Mob mob) {
        if (!isEnabled()) return;
        if (!MobBuffUtil.isValidMob(mob)) return;
        if (!EVALUATED_MOBS.add(mob.getUUID())) return;
        if (RANDOM.nextDouble() >= BuffMobsConfig.INSTANCE.traits.chance) return;
        Trait trait = rollTrait();
        if (trait != null) assignTrait(mob, trait);
    }

    public static void onMobRemoved(Mob mob) {
        EVALUATED_MOBS.remove(mob.getUUID());
        removeTrait(mob);
    }

    public static void clearCaches() {
        EVALUATED_MOBS.clear();
        MOB_TRAITS.clear();
    }

    public static void tick(Mob mob) {
        Trait trait = MOB_TRAITS.get(mob.getUUID());
        if (trait == null) return;
        applyPassiveTick(mob, trait);
        if (!BuffMobsConfig.INSTANCE.visualEffects) return;
        if (!(mob.level() instanceof ServerLevel serverLevel)) return;
        if (mob.tickCount % 10 != 0) return;
        ParticleOptions particle = resolveParticle(trait, mob);
        if (particle == null) return;
        serverLevel.sendParticles(particle,
                mob.getX(), mob.getY() + mob.getBbHeight() * 0.5, mob.getZ(),
                1, mob.getBbWidth() * 0.5, mob.getBbHeight() * 0.5, mob.getBbWidth() * 0.5, 0.0);
    }

    public static boolean cancelIncomingDamage(LivingEntity entity, DamageSource source) {
        if (!(entity instanceof Mob mob)) return false;
        Trait trait = MOB_TRAITS.get(mob.getUUID());
        if (trait == null) return false;
        boolean immune = switch (trait) {
            case FLAME -> source.is(DamageTypeTags.IS_FIRE);
            case FROST -> source.is(DamageTypes.FREEZE);
            case VOLTAIC -> source.is(DamageTypes.LIGHTNING_BOLT);
            default -> false;
        };
        return immune;
    }

    public static void onDamageApplied(LivingEntity victim, DamageSource source, float damageTaken) {
        if (applyingChainDamage) return;
        if (!isEnabled()) return;
        if (!BuffMobsConfig.INSTANCE.traits.onHitEffectsEnabled) return;
        Mob attacker = resolveAttackingMob(source);
        if (attacker == null) return;
        Trait trait = MOB_TRAITS.get(attacker.getUUID());
        if (trait == null) return;
        if (victim == attacker || !victim.isAlive()) return;
        applyOnHit(attacker, victim, trait, damageTaken);
    }

    private static Trait rollTrait() {
        BuffMobsConfig.Traits cfg = BuffMobsConfig.INSTANCE.traits;
        Trait[] traits = Trait.values();
        int[] weights = {
                cfg.flameWeight, cfg.frostWeight, cfg.toxicWeight,
                cfg.voltaicWeight, cfg.vampiricWeight, cfg.armoredWeight
        };
        int total = 0;
        for (int weight : weights) total += Math.max(0, weight);
        if (total <= 0) return null;
        int roll = RANDOM.nextInt(total);
        int current = 0;
        for (int i = 0; i < traits.length; i++) {
            current += Math.max(0, weights[i]);
            if (roll < current) return traits[i];
        }
        return null;
    }

    private static void assignTrait(Mob mob, Trait trait) {
        MOB_TRAITS.put(mob.getUUID(), trait);
        if (trait == Trait.ARMORED) {
            applyAddition(mob, TRAIT_ARMOR_MOD_ID, true, BuffMobsConfig.INSTANCE.traits.armoredArmorBonus);
            applyAddition(mob, TRAIT_TOUGHNESS_MOD_ID, false, BuffMobsConfig.INSTANCE.traits.armoredToughnessBonus);
        }
        BuffMobsMod.LOGGER.debug("[BuffMobs] Trait {} assigned to {}", trait, mob.getType().getDescriptionId());
    }

    private static void removeTrait(Mob mob) {
        Trait trait = MOB_TRAITS.remove(mob.getUUID());
        if (trait != Trait.ARMORED) return;
        AttributeInstance armor = mob.getAttribute(Attributes.ARMOR);
        if (armor != null) armor.removeModifier(TRAIT_ARMOR_MOD_ID);
        AttributeInstance toughness = mob.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (toughness != null) toughness.removeModifier(TRAIT_TOUGHNESS_MOD_ID);
    }

    private static void applyAddition(Mob mob, Identifier id, boolean armor, double amount) {
        AttributeInstance instance = mob.getAttribute(armor ? Attributes.ARMOR : Attributes.ARMOR_TOUGHNESS);
        if (instance == null) return;
        instance.removeModifier(id);
        if (amount > 0.0) {
            instance.addPermanentModifier(new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    private static void applyPassiveTick(Mob mob, Trait trait) {
        switch (trait) {
            case FLAME -> {
                if (mob.isOnFire()) mob.clearFire();
            }
            case FROST -> {
                if (mob.getTicksFrozen() > 0) mob.setTicksFrozen(0);
            }
            case TOXIC -> {
                if (mob.hasEffect(MobEffects.POISON)) mob.removeEffect(MobEffects.POISON);
            }
            case VAMPIRIC -> {
                if (mob.hasEffect(MobEffects.WITHER)) mob.removeEffect(MobEffects.WITHER);
            }
            default -> {
            }
        }
    }

    private static Mob resolveAttackingMob(DamageSource source) {
        Entity attacker = source.getEntity();
        if (attacker instanceof Mob mob) return mob;
        Entity direct = source.getDirectEntity();
        if (direct instanceof Projectile projectile && projectile.getOwner() instanceof Mob mob) return mob;
        return null;
    }

    private static void applyOnHit(Mob attacker, LivingEntity victim, Trait trait, float dealtDamage) {
        BuffMobsConfig.Traits cfg = BuffMobsConfig.INSTANCE.traits;
        switch (trait) {
            case FLAME -> victim.igniteForTicks(cfg.flameFireSeconds * 20);
            case FROST -> victim.addEffect(new MobEffectInstance(MobEffects.SLOWNESS,
                    cfg.frostSlownessSeconds * 20, Math.max(0, cfg.frostSlownessLevel - 1), false, true, true));
            case TOXIC -> victim.addEffect(new MobEffectInstance(MobEffects.POISON,
                    cfg.toxicPoisonSeconds * 20, Math.max(0, cfg.toxicPoisonLevel - 1), false, true, true));
            case VOLTAIC -> applyVoltaicChain(attacker, victim, cfg);
            case VAMPIRIC -> {
                float heal = dealtDamage * (float) cfg.vampiricHealPercent;
                if (heal > 0.0f) attacker.heal(heal);
            }
            case ARMORED -> {
            }
        }
    }

    private static void applyVoltaicChain(Mob attacker, LivingEntity victim, BuffMobsConfig.Traits cfg) {
        if (RANDOM.nextDouble() >= cfg.voltaicChainChance) return;
        if (!(victim.level() instanceof ServerLevel serverLevel)) return;
        double radius = cfg.voltaicChainRadius;
        List<LivingEntity> candidates = serverLevel.getEntitiesOfClass(LivingEntity.class,
                victim.getBoundingBox().inflate(radius),
                candidate -> isChainTarget(attacker, victim, candidate));
        applyingChainDamage = true;
        try {
            int chained = 0;
            for (LivingEntity target : candidates) {
                if (chained >= cfg.voltaicMaxTargets) break;
                target.hurt(serverLevel.damageSources().indirectMagic(attacker, attacker),
                        (float) cfg.voltaicChainDamage);
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                        4, 0.3, 0.3, 0.3, 0.05);
                chained++;
            }
        } finally {
            applyingChainDamage = false;
        }
    }

    private static boolean isChainTarget(Mob attacker, LivingEntity victim, LivingEntity candidate) {
        if (candidate == attacker || candidate == victim || !candidate.isAlive()) return false;
        if (candidate instanceof Player) return true;
        if (candidate instanceof Mob mob) {
            return mob.getTarget() == attacker || attacker.getTarget() == mob;
        }
        return false;
    }

    private static ParticleOptions resolveParticle(Trait trait, Mob mob) {
        BuffMobsConfig.Traits cfg = BuffMobsConfig.INSTANCE.traits;
        BuffMobsConfig.EliteMobs.EliteParticle selected = switch (trait) {
            case FLAME -> cfg.flameParticle;
            case FROST -> cfg.frostParticle;
            case TOXIC -> cfg.toxicParticle;
            case VOLTAIC -> cfg.voltaicParticle;
            case VAMPIRIC -> cfg.vampiricParticle;
            case ARMORED -> cfg.armoredParticle;
        };
        if (selected == BuffMobsConfig.EliteMobs.EliteParticle.NONE) return null;
        if (selected == BuffMobsConfig.EliteMobs.EliteParticle.RANDOM) selected = MobBuffUtil.randomParticleFor(mob);
        return switch (selected) {
            case ENCHANT -> ParticleTypes.ENCHANT;
            case FLAME -> ParticleTypes.FLAME;
            case SOUL_FIRE_FLAME -> ParticleTypes.SOUL_FIRE_FLAME;
            case PORTAL -> ParticleTypes.PORTAL;
            case END_ROD -> ParticleTypes.END_ROD;
            case HAPPY_VILLAGER -> ParticleTypes.HAPPY_VILLAGER;
            case HEART -> ParticleTypes.HEART;
            case SNOWFLAKE -> ParticleTypes.SNOWFLAKE;
            case ELECTRIC_SPARK -> ParticleTypes.ELECTRIC_SPARK;
            case RANDOM, NONE -> null;
        };
    }
}
