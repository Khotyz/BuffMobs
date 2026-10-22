package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.util.Guard;
import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.data.BuffMobsWorldData;
import com.khotyz.buffmobs.util.MobBuffUtil;
import com.khotyz.buffmobs.util.SwingUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PassiveMobAggressionHandler {

    private static final Identifier DAMAGE_MODIFIER_ID =
            Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "passive_aggression_damage");

    private static final Set<UUID> INITIALIZED     = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> PARTICLES_SHOWN = ConcurrentHashMap.newKeySet();
    private static final Map<UUID, MobAIState> AI_STATES = new ConcurrentHashMap<>();

    public static void register() {
    }

    public static void onMobInitialized(Mob mob) {
        BuffMobsConfig.PassiveMobAggression.PassiveMobMode mode = BuffMobsConfig.INSTANCE.passiveMobAggression.mode.get();
        if (mode == BuffMobsConfig.PassiveMobAggression.PassiveMobMode.OFF || !MobBuffUtil.isPassiveAggressiveMob(mob)) {
            deactivateMob(mob);
            return;
        }
        UUID uuid = mob.getUUID();
        MobAIState state = AI_STATES.get(uuid);
        if (INITIALIZED.contains(uuid) && state != null && state.mode == mode) return;
        BuffMobsConfig.PassiveMobAggression.PassiveMobMode previousMode = state == null ? null : state.mode;

        applyDamageModifierIfPresent(mob);
        replaceAIGoals(mob, mode);

        if (mode == BuffMobsConfig.PassiveMobAggression.PassiveMobMode.HOSTILE) {
            setEnraged(mob, true);
        } else if (previousMode == BuffMobsConfig.PassiveMobAggression.PassiveMobMode.HOSTILE) {
            setEnraged(mob, false);
        }

        INITIALIZED.add(uuid);
        BuffMobsMod.LOGGER.debug("[BuffMobs] PassiveAggression initialized: {} (mode={})",
                BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()), mode);
    }

    public static void onMobRemoved(Mob mob) {
        INITIALIZED.remove(mob.getUUID());
        PARTICLES_SHOWN.remove(mob.getUUID());
        AI_STATES.remove(mob.getUUID());
    }

    public static void onMobHurtByPlayer(Mob mob, Player player) {
        BuffMobsConfig.PassiveMobAggression.PassiveMobMode mode = BuffMobsConfig.INSTANCE.passiveMobAggression.mode.get();
        if (mode == BuffMobsConfig.PassiveMobAggression.PassiveMobMode.OFF) return;
        if (!MobBuffUtil.isPassiveAggressiveMob(mob)) return;

        if (mode == BuffMobsConfig.PassiveMobAggression.PassiveMobMode.NEUTRAL) {
            if (!isEnraged(mob)) {
                setEnraged(mob, true);
                mob.setTarget(player);
                spawnAngryParticles(mob);
            }
        }

        if (!PARTICLES_SHOWN.contains(mob.getUUID())) {
            spawnAngryParticles(mob);
            PARTICLES_SHOWN.add(mob.getUUID());
        }
    }

    public static void tick(Mob mob) {
        BuffMobsConfig.PassiveMobAggression.PassiveMobMode mode = BuffMobsConfig.INSTANCE.passiveMobAggression.mode.get();
        if (mode == BuffMobsConfig.PassiveMobAggression.PassiveMobMode.OFF || !MobBuffUtil.isPassiveAggressiveMob(mob)) {
            boolean wasConverted = INITIALIZED.contains(mob.getUUID()) || AI_STATES.containsKey(mob.getUUID());
            deactivateMob(mob);
            if (wasConverted && BuffMobsConfig.INSTANCE.enabled.get()) MobBuffUtil.applyBuffs(mob);
            return;
        }
        MobAIState state = AI_STATES.get(mob.getUUID());
        if (!INITIALIZED.contains(mob.getUUID()) || state == null || state.mode != mode) {
            onMobInitialized(mob);
            return;
        }

        if (mode == BuffMobsConfig.PassiveMobAggression.PassiveMobMode.NEUTRAL) {
            if (!isEnraged(mob)) return;
            if (mob.getTarget() == null || !mob.getTarget().isAlive()) {
                setEnraged(mob, false);
                PARTICLES_SHOWN.remove(mob.getUUID());
                mob.setTarget(null);
            }
        }
    }

    public static void forceReinit() {
        INITIALIZED.clear();
        PARTICLES_SHOWN.clear();
    }

    public static void clearStates() {
        forceReinit();
    }

    public static void resetMob(Mob mob) {
        deactivateMob(mob);
    }

    public static void reload(Iterable<Entity> entities) {
        forceReinit();
        AI_STATES.clear();
        BuffMobsConfig.PassiveMobAggression.PassiveMobMode mode = BuffMobsConfig.INSTANCE.passiveMobAggression.mode.get();
        if (mode == BuffMobsConfig.PassiveMobAggression.PassiveMobMode.OFF) return;
        for (Entity entity : entities) {
            if (entity.level().isClientSide()) continue;
            if (!(entity instanceof Mob mob) || mob.isRemoved()) continue;
            if (!MobBuffUtil.isPassiveAggressiveMob(mob)) continue;
            onMobInitialized(mob);
        }
    }

    private static boolean isEnraged(Mob mob) {
        BuffMobsWorldData data = BuffMobsWorldData.get(mob.level());
        return data != null && data.getMobState(mob.getUUID()).passiveAggressionEnraged();
    }

    private static void setEnraged(Mob mob, boolean enraged) {
        BuffMobsWorldData data = BuffMobsWorldData.get(mob.level());
        if (data != null) data.updateMobState(mob.getUUID(), state -> state.setPassiveAggressionEnraged(enraged));
    }

    public static boolean isConvertedMob(Mob mob) {
        return INITIALIZED.contains(mob.getUUID()) && MobBuffUtil.isPassiveAggressiveMob(mob);
    }

    public static double getConfiguredDamage(Mob mob) {
        double damage = MobBuffUtil.getPassiveDamageForMob(mob);
        damage *= MobBuffUtil.getEffectiveDamageMultiplier(mob);
        if (EliteMobHandler.isElite(mob)) {
            damage *= BuffMobsConfig.INSTANCE.eliteMobs.damageMultiplier.get();
        }
        MobEffectInstance strength = mob.getEffect(MobEffects.STRENGTH);
        if (strength != null) damage += (strength.getAmplifier() + 1) * 3.0;
        MobEffectInstance weakness = mob.getEffect(MobEffects.WEAKNESS);
        if (weakness != null) damage -= (weakness.getAmplifier() + 1) * 4.0;
        Difficulty difficulty = mob.level() instanceof ServerLevel serverLevel
                ? serverLevel.getDifficulty() : Difficulty.NORMAL;
        if (difficulty == Difficulty.EASY) damage = Math.min(damage / 2.0 + 1.0, damage);
        if (difficulty == Difficulty.HARD) damage *= 1.5;
        return Math.max(0.0, damage);
    }

    private static void applyDamageModifierIfPresent(Mob mob) {
        AttributeInstance dmgAttr = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmgAttr == null) return;
        dmgAttr.removeModifier(DAMAGE_MODIFIER_ID);

        double totalDamage = configBaseDamage(mob);
        double base = dmgAttr.getBaseValue();
        double bonus = totalDamage - base;
        if (bonus > 0) {
            dmgAttr.addPermanentModifier(new AttributeModifier(
                    DAMAGE_MODIFIER_ID, bonus, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    private static double configBaseDamage(Mob mob) {
        BuffMobsConfig.PassiveMobAggression cfg = BuffMobsConfig.INSTANCE.passiveMobAggression;
        double totalDamage = cfg.baseDamage.get();
        if (cfg.scaleWithHealth.get()) {
            totalDamage += mob.getMaxHealth() * cfg.healthScaleFactor.get();
        }
        return totalDamage;
    }

    private static void replaceAIGoals(Mob mob, BuffMobsConfig.PassiveMobAggression.PassiveMobMode mode) {
        if (!(mob instanceof PathfinderMob pathMob)) return;

        GoalSelector goalSelector = mob.goalSelector;
        GoalSelector targetSelector = mob.targetSelector;

        UUID uuid = mob.getUUID();
        MobAIState state = AI_STATES.computeIfAbsent(uuid, ignored -> new MobAIState());
        for (Goal goal : state.addedGoals) goalSelector.removeGoal(goal);
        for (Goal goal : state.addedTargetGoals) targetSelector.removeGoal(goal);
        state.addedGoals.clear();
        state.addedTargetGoals.clear();

        for (WrappedGoal wrapped : new ArrayList<>(goalSelector.getAvailableGoals())) {
            Goal goal = wrapped.getGoal();
            String name = goal.getClass().getSimpleName();
            boolean replaceable = goal instanceof PanicGoal || name.contains("Flee")
                    || name.contains("Avoid") || name.contains("Escape");
            if (!replaceable) continue;
            boolean recorded = state.removedGoals.stream().anyMatch(removed -> removed.goal == goal);
            if (!recorded) state.removedGoals.add(new GoalRecord(wrapped.getPriority(), goal));
            goalSelector.removeGoal(goal);
        }

        Goal meleeGoal = new PassiveAggressionMeleeGoal(pathMob, 1.2, mode);
        goalSelector.addGoal(0, meleeGoal);
        state.addedGoals.add(meleeGoal);

        if (mode == BuffMobsConfig.PassiveMobAggression.PassiveMobMode.NEUTRAL) {
            Goal targetGoal = new HurtByTargetGoal(pathMob);
            targetSelector.addGoal(0, targetGoal);
            state.addedTargetGoals.add(targetGoal);
        } else if (mode == BuffMobsConfig.PassiveMobAggression.PassiveMobMode.HOSTILE) {
            Goal targetGoal = new NearestAttackableTargetGoal<>(pathMob, Player.class, true);
            targetSelector.addGoal(0, targetGoal);
            state.addedTargetGoals.add(targetGoal);
        }

        state.mode = mode;
        BuffMobsMod.LOGGER.debug("[BuffMobs] AI goals replaced for {} (mode={})",
                BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()), mode);
    }

    private static void deactivateMob(Mob mob) {
        UUID uuid = mob.getUUID();
        MobAIState state = AI_STATES.remove(uuid);
        if (state != null && mob instanceof PathfinderMob) {
            GoalSelector goalSelector = mob.goalSelector;
            GoalSelector targetSelector = mob.targetSelector;
            for (Goal goal : state.addedGoals) goalSelector.removeGoal(goal);
            for (GoalRecord removed : state.removedGoals) {
                boolean present = goalSelector.getAvailableGoals().stream()
                        .anyMatch(wrapped -> wrapped.getGoal() == removed.goal);
                if (!present) goalSelector.addGoal(removed.priority, removed.goal);
            }
            for (Goal goal : state.addedTargetGoals) targetSelector.removeGoal(goal);
            if (mob.getTarget() instanceof Player) mob.setTarget(null);
        }
        INITIALIZED.remove(uuid);
        PARTICLES_SHOWN.remove(uuid);
        if (state != null || isEnraged(mob)) setEnraged(mob, false);
        AttributeInstance damage = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage != null) damage.removeModifier(DAMAGE_MODIFIER_ID);
    }

    private static void spawnAngryParticles(Mob mob) {
        if (!(mob.level() instanceof ServerLevel serverLevel)) return;
        serverLevel.sendParticles(
                ParticleTypes.ANGRY_VILLAGER,
                mob.getX(), mob.getY() + mob.getBbHeight() * 0.75, mob.getZ(),
                8, 0.3, 0.3, 0.3, 0.0);
    }

    private static final class MobAIState {
        private final List<GoalRecord> removedGoals = new ArrayList<>();
        private final List<Goal> addedGoals = new ArrayList<>();
        private final List<Goal> addedTargetGoals = new ArrayList<>();
        private BuffMobsConfig.PassiveMobAggression.PassiveMobMode mode;
    }

    private static final class GoalRecord {
        private final int priority;
        private final Goal goal;

        private GoalRecord(int priority, Goal goal) {
            this.priority = priority;
            this.goal = goal;
        }
    }

    private static class PassiveAggressionMeleeGoal extends Goal {
        private final PathfinderMob owner;
        private final double speedModifier;
        private final BuffMobsConfig.PassiveMobAggression.PassiveMobMode mode;
        private int attackCooldown;

        PassiveAggressionMeleeGoal(PathfinderMob mob, double speedModifier,
                                   BuffMobsConfig.PassiveMobAggression.PassiveMobMode mode) {
            this.owner = mob;
            this.speedModifier = speedModifier;
            this.mode = mode;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            try {
                LivingEntity target = this.owner.getTarget();
                if (target == null || !target.isAlive()) return false;

                if (this.mode == BuffMobsConfig.PassiveMobAggression.PassiveMobMode.HOSTILE) {
                    return target instanceof Player;
                } else if (this.mode == BuffMobsConfig.PassiveMobAggression.PassiveMobMode.NEUTRAL) {
                    return isEnraged(this.owner) && target instanceof Player;
                }
                return false;
            } catch (Throwable throwable) {
                Guard.report("PassiveAggressionMeleeGoal.canUse", throwable);
                return false;
            }
        }

        @Override
        public boolean canContinueToUse() {
            try {
                LivingEntity target = this.owner.getTarget();
                if (target == null || !target.isAlive()) return false;

                if (this.mode == BuffMobsConfig.PassiveMobAggression.PassiveMobMode.HOSTILE) {
                    return target instanceof Player;
                } else if (this.mode == BuffMobsConfig.PassiveMobAggression.PassiveMobMode.NEUTRAL) {
                    return isEnraged(this.owner) && target instanceof Player;
                }
                return false;
            } catch (Throwable throwable) {
                Guard.report("PassiveAggressionMeleeGoal.canContinueToUse", throwable);
                return false;
            }
        }

        @Override
        public void start() {
            try {
                this.attackCooldown = 0;
            } catch (Throwable throwable) {
                Guard.report("PassiveAggressionMeleeGoal.start", throwable);
            }
        }

        @Override
        public void stop() {
            try {
                this.owner.getNavigation().stop();
            } catch (Throwable throwable) {
                Guard.report("PassiveAggressionMeleeGoal.stop", throwable);
            }
        }

        @Override
        public void tick() {
            try {
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
                        SwingUtil.swing(this.owner, InteractionHand.MAIN_HAND);
                        double damage = getConfiguredDamage(this.owner);
                        target.hurt(this.owner.level().damageSources().mobAttack(this.owner), (float) damage);
                    }
                }
            } catch (Throwable throwable) {
                Guard.report("PassiveAggressionMeleeGoal.tick", throwable);
            }
        }
    }
}
