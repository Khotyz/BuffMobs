package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.util.MobBuffUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PassiveMobAggressionHandler {

    private static final ResourceLocation DAMAGE_MOD_ID =
            ResourceLocation.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "passive_aggression_damage");
    private static final float PASSIVE_MELEE_DAMAGE_MARKER = -777777.0f;

    private static final Set<UUID> INITIALIZED_MOBS = new HashSet<>();
    private static final Set<UUID> PARTICLE_SHOWN_MOBS = new HashSet<>();
    private static final Map<UUID, List<Goal>> REMOVED_GOALS = new HashMap<>();

    public static void register() {
        NeoForge.EVENT_BUS.addListener(PassiveMobAggressionHandler::onEntityJoin);
        NeoForge.EVENT_BUS.addListener(PassiveMobAggressionHandler::onEntityLeave);
        NeoForge.EVENT_BUS.addListener(PassiveMobAggressionHandler::onLivingDamage);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, PassiveMobAggressionHandler::onIncomingDamage);
    }

    public static void forceReinit() {
        INITIALIZED_MOBS.clear();
        PARTICLE_SHOWN_MOBS.clear();
        REMOVED_GOALS.clear();
    }

    public static void reloadMob(Mob mob) {
        UUID uuid = mob.getUUID();
        BuffMobsConfig.PassiveMobAggression.PassiveMode mode = BuffMobsConfig.INSTANCE.passiveMobAggression.mode.get();
        boolean shouldBeAggressive = mode != BuffMobsConfig.PassiveMobAggression.PassiveMode.OFF
                && MobBuffUtil.isPassiveAggressiveMob(mob)
                && mob instanceof PathfinderMob;
        boolean isAggressive = INITIALIZED_MOBS.contains(uuid);

        if (shouldBeAggressive && !isAggressive) {
            enablePassiveAggression((PathfinderMob) mob, mode);
        } else if (!shouldBeAggressive && isAggressive) {
            disablePassiveAggression(mob);
        }
    }

    private static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof Mob mob)) return;
        BuffMobsConfig.PassiveMobAggression.PassiveMode mode = BuffMobsConfig.INSTANCE.passiveMobAggression.mode.get();
        if (mode == BuffMobsConfig.PassiveMobAggression.PassiveMode.OFF) return;
        if (!MobBuffUtil.isPassiveAggressiveMob(mob)) return;
        if (INITIALIZED_MOBS.contains(mob.getUUID())) return;
        if (!(mob instanceof PathfinderMob pathfinderMob)) return;

        enablePassiveAggression(pathfinderMob, mode);
    }

    private static void onEntityLeave(EntityLeaveLevelEvent event) {
        if (!(event.getEntity() instanceof Mob mob)) return;
        UUID uuid = mob.getUUID();
        INITIALIZED_MOBS.remove(uuid);
        PARTICLE_SHOWN_MOBS.remove(uuid);
        REMOVED_GOALS.remove(uuid);
    }

    private static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getOriginalAmount() >= 0.0f) return;
        if (!(event.getSource().getDirectEntity() instanceof Mob attacker)) return;
        if (!isConvertedPassiveMob(attacker)) return;
        double damage = resolveAttackDamage(attacker);
        if (event.getEntity() instanceof Player) {
            Difficulty difficulty = event.getEntity().level().getDifficulty();
            if (difficulty == Difficulty.EASY) damage = Math.min(damage / 2.0 + 1.0, damage);
            if (difficulty == Difficulty.HARD) damage *= 1.5;
        }
        event.setAmount((float) damage);
    }

    private static boolean isConvertedPassiveMob(Mob mob) {
        return INITIALIZED_MOBS.contains(mob.getUUID()) && MobBuffUtil.isPassiveAggressiveMob(mob);
    }

    private static void onLivingDamage(LivingDamageEvent.Post event) {
        BuffMobsConfig.PassiveMobAggression.PassiveMode mode = BuffMobsConfig.INSTANCE.passiveMobAggression.mode.get();
        if (mode == BuffMobsConfig.PassiveMobAggression.PassiveMode.OFF) return;
        if (!(event.getEntity() instanceof Mob mob)) return;
        if (!MobBuffUtil.isPassiveAggressiveMob(mob)) return;
        if (mob.getTarget() == null) return;
        if (PARTICLE_SHOWN_MOBS.contains(mob.getUUID())) return;
        if (!(mob.level() instanceof ServerLevel serverLevel)) return;

        serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER,
                mob.getX(), mob.getY() + mob.getBbHeight() * 0.75, mob.getZ(),
                8, 0.3, 0.3, 0.3, 0.0);

        PARTICLE_SHOWN_MOBS.add(mob.getUUID());
    }

    private static void enablePassiveAggression(PathfinderMob pathfinderMob, BuffMobsConfig.PassiveMobAggression.PassiveMode mode) {
        removeFleeGoals(pathfinderMob);

        if (mode == BuffMobsConfig.PassiveMobAggression.PassiveMode.NEUTRAL) {
            pathfinderMob.targetSelector.addGoal(1, new HurtByTargetGoal(pathfinderMob));
        } else if (mode == BuffMobsConfig.PassiveMobAggression.PassiveMode.HOSTILE) {
            pathfinderMob.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(pathfinderMob, Player.class, true));
            pathfinderMob.targetSelector.addGoal(2, new HurtByTargetGoal(pathfinderMob));
        }

        pathfinderMob.goalSelector.addGoal(2, new PassiveMeleeGoal(pathfinderMob));

        INITIALIZED_MOBS.add(pathfinderMob.getUUID());
        BuffMobsMod.LOGGER.debug("[BuffMobs] PassiveAggression: enabled for {} with mode {}",
                pathfinderMob.getType().getDescriptionId(), mode);
    }

    private static void disablePassiveAggression(Mob mob) {
        UUID uuid = mob.getUUID();
        if (mob instanceof PathfinderMob pathfinderMob) {
            List<WrappedGoal> goalsToRemove = new ArrayList<>();
            for (WrappedGoal wrapped : pathfinderMob.goalSelector.getAvailableGoals()) {
                if (wrapped.getGoal() instanceof PassiveMeleeGoal) goalsToRemove.add(wrapped);
            }
            for (WrappedGoal wrapped : goalsToRemove) pathfinderMob.goalSelector.removeGoal(wrapped.getGoal());

            List<WrappedGoal> targetsToRemove = new ArrayList<>();
            for (WrappedGoal wrapped : pathfinderMob.targetSelector.getAvailableGoals()) {
                Goal g = wrapped.getGoal();
                if (g instanceof HurtByTargetGoal || g instanceof NearestAttackableTargetGoal) {
                    targetsToRemove.add(wrapped);
                }
            }
            for (WrappedGoal wrapped : targetsToRemove) pathfinderMob.targetSelector.removeGoal(wrapped.getGoal());

            List<Goal> removedFleeGoals = REMOVED_GOALS.remove(uuid);
            if (removedFleeGoals != null) {
                for (Goal goal : removedFleeGoals) pathfinderMob.goalSelector.addGoal(2, goal);
            }
        }

        INITIALIZED_MOBS.remove(uuid);
        PARTICLE_SHOWN_MOBS.remove(uuid);
        BuffMobsMod.LOGGER.debug("[BuffMobs] PassiveAggression: disabled for {}", mob.getType().getDescriptionId());
    }

    private static void removeFleeGoals(PathfinderMob mob) {
        List<WrappedGoal> toRemove = new ArrayList<>();
        for (WrappedGoal wrapped : mob.goalSelector.getAvailableGoals()) {
            Goal goal = wrapped.getGoal();
            if (goal instanceof PanicGoal) {
                toRemove.add(wrapped);
            }
            String className = goal.getClass().getSimpleName();
            if (className.contains("Flee") || className.contains("Avoid") || className.contains("Escape")) {
                toRemove.add(wrapped);
            }
        }
        List<Goal> removed = new ArrayList<>();
        for (WrappedGoal wrapped : toRemove) {
            mob.goalSelector.removeGoal(wrapped.getGoal());
            removed.add(wrapped.getGoal());
        }
        if (!removed.isEmpty()) {
            REMOVED_GOALS.put(mob.getUUID(), removed);
            BuffMobsMod.LOGGER.debug("[BuffMobs] PassiveAggression: removed {} flee goals from {}",
                    removed.size(), mob.getType().getDescriptionId());
        }
    }

    private static double resolveAttackDamage(Mob mob) {
        BuffMobsConfig.PassiveMobAggression cfg = BuffMobsConfig.INSTANCE.passiveMobAggression;
        double damage = cfg.baseDamage.get();
        AttributeInstance attackDamage = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null) damage = Math.max(damage, attackDamage.getBaseValue());
        if (cfg.scaleWithHealth.get()) {
            AttributeInstance maxHealth = mob.getAttribute(Attributes.MAX_HEALTH);
            double baseMaxHealth = maxHealth != null ? maxHealth.getBaseValue() : mob.getMaxHealth();
            damage += baseMaxHealth * cfg.healthScaleFactor.get();
        }
        damage *= MobBuffUtil.getEffectiveDamageMultiplier(mob);
        if (EliteMobHandler.isElite(mob)) {
            damage *= BuffMobsConfig.INSTANCE.eliteMobs.damageMultiplier.get();
        }
        MobEffectInstance strength = mob.getEffect(MobEffects.DAMAGE_BOOST);
        if (strength != null) damage += (strength.getAmplifier() + 1) * 3.0;
        MobEffectInstance weakness = mob.getEffect(MobEffects.WEAKNESS);
        if (weakness != null) damage -= (weakness.getAmplifier() + 1) * 4.0;
        return Math.max(0.0, damage);
    }

    private static class PassiveMeleeGoal extends Goal {
        private static final double CHASE_SPEED = 1.2;
        private static final double ATTACK_REACH_SQ = 4.0;
        private static final int ATTACK_COOLDOWN = 20;

        private final PathfinderMob mob;
        private int ticksUntilNextAttack = 0;

        PassiveMeleeGoal(PathfinderMob mob) {
            this.mob = mob;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = mob.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = mob.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public void start() {
            ticksUntilNextAttack = 0;
        }

        @Override
        public void stop() {
            mob.getNavigation().stop();
        }

        @Override
        public void tick() {
            LivingEntity target = mob.getTarget();
            if (target == null) return;

            mob.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ());
            mob.getNavigation().moveTo(target, CHASE_SPEED);

            ticksUntilNextAttack = Math.max(0, ticksUntilNextAttack - 1);

            double distSq = mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
            if (distSq <= ATTACK_REACH_SQ && ticksUntilNextAttack == 0) {
                ticksUntilNextAttack = ATTACK_COOLDOWN;
                if (mob.level() instanceof ServerLevel sl) {
                    DamageSource src = sl.damageSources().mobAttack(mob);
                    target.hurt(src, PASSIVE_MELEE_DAMAGE_MARKER);
                }
            }
        }
    }
}