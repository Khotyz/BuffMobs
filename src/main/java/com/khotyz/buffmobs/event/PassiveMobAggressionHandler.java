// FILE: com/khotyz/buffmobs/event/PassiveMobAggressionHandler.java
package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.util.MobBuffUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PassiveMobAggressionHandler {

    private static final Identifier DAMAGE_MOD_ID =
            Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "passive_aggression_damage");

    private static final Set<UUID> INITIALIZED_MOBS = new HashSet<>();
    private static final Set<UUID> PARTICLE_SHOWN_MOBS = new HashSet<>();
    private static final Map<UUID, List<Goal>> REMOVED_GOALS = new HashMap<>();
    private static final Map<UUID, AddedGoals> ADDED_GOALS = new HashMap<>();

    public static void register() {
        NeoForge.EVENT_BUS.addListener(PassiveMobAggressionHandler::onEntityJoin);
        NeoForge.EVENT_BUS.addListener(PassiveMobAggressionHandler::onEntityLeave);
        NeoForge.EVENT_BUS.addListener(PassiveMobAggressionHandler::onLivingDamage);
    }

    public static void forceReinit() {
        INITIALIZED_MOBS.clear();
        PARTICLE_SHOWN_MOBS.clear();
        REMOVED_GOALS.clear();
        ADDED_GOALS.clear();
    }

    public static void reload(Iterable<Entity> entities) {
        for (Entity e : entities) {
            if (!(e instanceof Mob mob) || !(mob instanceof PathfinderMob pathfinderMob)) continue;
            UUID uuid = mob.getUUID();

            AddedGoals added = ADDED_GOALS.remove(uuid);
            if (added != null) {
                pathfinderMob.targetSelector.removeGoal(added.targetGoal);
                pathfinderMob.goalSelector.removeGoal(added.meleeGoal);
            }

            List<Goal> removedFlee = REMOVED_GOALS.remove(uuid);
            if (removedFlee != null) {
                for (Goal g : removedFlee) pathfinderMob.goalSelector.addGoal(2, g);
            }

            INITIALIZED_MOBS.remove(uuid);
            PARTICLE_SHOWN_MOBS.remove(uuid);
        }

        BuffMobsConfig.PassiveMobAggression.PassiveMobMode mode =
                BuffMobsConfig.INSTANCE.passiveMobAggression.mode.get();
        if (mode == BuffMobsConfig.PassiveMobAggression.PassiveMobMode.OFF) return;

        for (Entity e : entities) {
            if (e.level().isClientSide()) continue;
            if (!(e instanceof Mob mob)) continue;
            if (!MobBuffUtil.isPassiveAggressiveMob(mob)) continue;
            if (INITIALIZED_MOBS.contains(mob.getUUID())) continue;
            if (!(mob instanceof PathfinderMob pathfinderMob)) continue;

            initMob(pathfinderMob, mode);
        }
    }

    private static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof Mob mob)) return;
        BuffMobsConfig.PassiveMobAggression.PassiveMobMode mode =
                BuffMobsConfig.INSTANCE.passiveMobAggression.mode.get();
        if (mode == BuffMobsConfig.PassiveMobAggression.PassiveMobMode.OFF) return;
        if (!MobBuffUtil.isPassiveAggressiveMob(mob)) return;
        if (INITIALIZED_MOBS.contains(mob.getUUID())) return;
        if (!(mob instanceof PathfinderMob pathfinderMob)) return;

        initMob(pathfinderMob, mode);
    }

    private static void initMob(PathfinderMob pathfinderMob,
                                BuffMobsConfig.PassiveMobAggression.PassiveMobMode mode) {
        Mob mob = pathfinderMob;
        removeFleeGoals(pathfinderMob);

        double damage = resolveDamage(mob);
        Goal targetGoal;
        if (mode == BuffMobsConfig.PassiveMobAggression.PassiveMobMode.HOSTILE) {
            targetGoal = new NearestAttackableTargetGoal<>(pathfinderMob, Player.class, true);
        } else {
            targetGoal = new HurtByTargetGoal(pathfinderMob);
        }
        PassiveMeleeGoal meleeGoal = new PassiveMeleeGoal(pathfinderMob, damage);
        mob.targetSelector.addGoal(1, targetGoal);
        mob.goalSelector.addGoal(2, meleeGoal);

        ADDED_GOALS.put(mob.getUUID(), new AddedGoals(targetGoal, meleeGoal));
        INITIALIZED_MOBS.add(mob.getUUID());
    }

    private static void onEntityLeave(EntityLeaveLevelEvent event) {
        if (!(event.getEntity() instanceof Mob mob)) return;
        UUID uuid = mob.getUUID();
        INITIALIZED_MOBS.remove(uuid);
        PARTICLE_SHOWN_MOBS.remove(uuid);
        REMOVED_GOALS.remove(uuid);
        ADDED_GOALS.remove(uuid);
    }

    private static void onLivingDamage(LivingDamageEvent.Post event) {
        BuffMobsConfig.PassiveMobAggression.PassiveMobMode mode =
                BuffMobsConfig.INSTANCE.passiveMobAggression.mode.get();
        if (mode == BuffMobsConfig.PassiveMobAggression.PassiveMobMode.OFF) return;
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

    private static double resolveDamage(Mob mob) {
        BuffMobsConfig.PassiveMobAggression cfg = BuffMobsConfig.INSTANCE.passiveMobAggression;
        double damage = cfg.baseDamage.get();
        if (cfg.scaleWithHealth.get()) {
            damage += mob.getMaxHealth() * cfg.healthScaleFactor.get();
        }
        return damage;
    }

    private static class AddedGoals {
        final Goal targetGoal;
        final PassiveMeleeGoal meleeGoal;

        AddedGoals(Goal targetGoal, PassiveMeleeGoal meleeGoal) {
            this.targetGoal = targetGoal;
            this.meleeGoal = meleeGoal;
        }
    }

    private static class PassiveMeleeGoal extends Goal {
        private static final double CHASE_SPEED = 1.2;
        private static final double ATTACK_REACH_SQ = 4.0;
        private static final int ATTACK_COOLDOWN = 20;

        private final PathfinderMob mob;
        private final double damage;
        private int ticksUntilNextAttack = 0;

        PassiveMeleeGoal(PathfinderMob mob, double damage) {
            this.mob = mob;
            this.damage = damage;
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
                    target.hurt(src, (float) damage);
                }
            }
        }
    }
}