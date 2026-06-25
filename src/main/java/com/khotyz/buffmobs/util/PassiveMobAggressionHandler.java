package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PassiveMobAggressionHandler {

    private static final Identifier DAMAGE_MODIFIER_ID =
            Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "passive_aggression_damage");

    private static final Set<UUID> INITIALIZED      = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> PARTICLES_SHOWN  = ConcurrentHashMap.newKeySet();

    public static void onMobInitialized(Mob mob) {
        if (!BuffMobsConfig.INSTANCE.passiveMobAggression.enabled) return;
        if (!MobBuffUtil.isPassiveAggressiveMob(mob)) return;
        if (INITIALIZED.contains(mob.getUUID())) return;

        applyDamageModifierIfPresent(mob);
        addAIGoals(mob);

        INITIALIZED.add(mob.getUUID());
        BuffMobsMod.LOGGER.debug("[BuffMobs] PassiveAggression initialized: {}",
                BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()));
    }

    public static void onMobRemoved(Mob mob) {
        INITIALIZED.remove(mob.getUUID());
        PARTICLES_SHOWN.remove(mob.getUUID());
    }

    public static void onMobHurtByPlayer(Mob mob, Player player) {
        if (!BuffMobsConfig.INSTANCE.passiveMobAggression.enabled) return;
        if (!MobBuffUtil.isPassiveAggressiveMob(mob)) return;
        if (mob.getTarget() != null && PARTICLES_SHOWN.contains(mob.getUUID())) return;

        if (mob.getTarget() != null && !PARTICLES_SHOWN.contains(mob.getUUID())) {
            spawnAngryParticles(mob);
            PARTICLES_SHOWN.add(mob.getUUID());
        }
    }

    public static void tick(Mob mob) {
    }

    public static void forceReinit() {
        INITIALIZED.clear();
        PARTICLES_SHOWN.clear();
    }

    public static double getConfiguredDamage(Mob mob) {
        BuffMobsConfig.PassiveMobAggression cfg = BuffMobsConfig.INSTANCE.passiveMobAggression;
        double totalDamage = cfg.baseDamage;
        if (cfg.scaleWithHealth) {
            totalDamage += mob.getMaxHealth() * cfg.healthScaleFactor;
        }
        return totalDamage;
    }

    private static void applyDamageModifierIfPresent(Mob mob) {
        AttributeInstance dmgAttr = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmgAttr == null) return;
        dmgAttr.removeModifier(DAMAGE_MODIFIER_ID);

        double totalDamage = getConfiguredDamage(mob);
        double base = dmgAttr.getBaseValue();
        double bonus = totalDamage - base;
        if (bonus > 0) {
            dmgAttr.addPermanentModifier(new AttributeModifier(
                    DAMAGE_MODIFIER_ID, bonus, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    private static void addAIGoals(Mob mob) {
        try {
            mob.targetSelector.addGoal(1, new HurtByTargetGoal((PathfinderMob) mob));
            mob.goalSelector.addGoal(2, new PassiveAggressionMeleeGoal((PathfinderMob) mob, 1.2));
        } catch (Exception e) {
            BuffMobsMod.LOGGER.warn("[BuffMobs] Could not add AI goals to {}: {}",
                    BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()), e.getMessage());
        }
    }

    private static void spawnAngryParticles(Mob mob) {
        if (!(mob.level() instanceof ServerLevel serverLevel)) return;
        serverLevel.sendParticles(
                ParticleTypes.ANGRY_VILLAGER,
                mob.getX(), mob.getY() + mob.getBbHeight() * 0.75, mob.getZ(),
                8, 0.3, 0.3, 0.3, 0.0);
    }

    private static class PassiveAggressionMeleeGoal extends Goal {
        private final PathfinderMob owner;
        private final double speedModifier;
        private int attackCooldown;

        PassiveAggressionMeleeGoal(PathfinderMob mob, double speedModifier) {
            this.owner = mob;
            this.speedModifier = speedModifier;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.owner.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.owner.getTarget();
            return target != null && target.isAlive() && this.owner.getTarget() == target;
        }

        @Override
        public void start() {
            this.attackCooldown = 0;
        }

        @Override
        public void stop() {
            this.owner.getNavigation().stop();
        }

        @Override
        public void tick() {
            LivingEntity target = this.owner.getTarget();
            if (target == null) return;

            this.owner.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ());

            double combinedWidth = this.owner.getBbWidth() + target.getBbWidth();
            double reach = combinedWidth * combinedWidth * 0.5 + 1.0;
            double distSqr = this.owner.distanceToSqr(target.getX(), target.getY(), target.getZ());

            if (this.attackCooldown > 0) this.attackCooldown--;

            if (distSqr > reach) {
                this.owner.getNavigation().moveTo(target, this.speedModifier);
            } else {
                this.owner.getNavigation().stop();
                if (this.attackCooldown <= 0) {
                    this.attackCooldown = 20;
                    this.owner.swing(InteractionHand.MAIN_HAND);
                    double damage = getConfiguredDamage(this.owner);
                    target.hurt(this.owner.level().damageSources().mobAttack(this.owner), (float) damage);
                }
            }
        }
    }
}