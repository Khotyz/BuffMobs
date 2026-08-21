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
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PassiveMobAggressionHandler {

    private static final Identifier DAMAGE_MODIFIER_ID =
            Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "passive_aggression_damage");

    private static final Set<UUID> INITIALIZED     = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> PARTICLES_SHOWN = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> ENRAGED         = ConcurrentHashMap.newKeySet();

    private static Field goalSelectorField;
    private static Field targetSelectorField;

    static {
        try {
            goalSelectorField = Mob.class.getDeclaredField("goalSelector");
            goalSelectorField.setAccessible(true);
            targetSelectorField = Mob.class.getDeclaredField("targetSelector");
            targetSelectorField.setAccessible(true);
            BuffMobsMod.LOGGER.info("[BuffMobs] GoalSelector fields successfully accessed via reflection");
        } catch (NoSuchFieldException e) {
            BuffMobsMod.LOGGER.error("[BuffMobs] Failed to find GoalSelector fields - check access widener!");
        }
    }

    private static GoalSelector getGoalSelector(Mob mob) {
        try {
            return (GoalSelector) goalSelectorField.get(mob);
        } catch (Exception e) {
            BuffMobsMod.LOGGER.warn("[BuffMobs] Failed to get goalSelector for {}: {}", mob, e.getMessage());
            return null;
        }
    }

    private static GoalSelector getTargetSelector(Mob mob) {
        try {
            return (GoalSelector) targetSelectorField.get(mob);
        } catch (Exception e) {
            BuffMobsMod.LOGGER.warn("[BuffMobs] Failed to get targetSelector for {}: {}", mob, e.getMessage());
            return null;
        }
    }

    public static void onMobInitialized(Mob mob) {
        BuffMobsConfig.PassiveMobAggression.Mode mode = BuffMobsConfig.INSTANCE.passiveMobAggression.mode;
        if (mode == BuffMobsConfig.PassiveMobAggression.Mode.OFF) return;
        if (!MobBuffUtil.isPassiveAggressiveMob(mob)) return;
        if (INITIALIZED.contains(mob.getUUID())) return;

        applyDamageModifierIfPresent(mob);
        replaceAIGoals(mob);

        // Se for HOSTILE, já considera o mob "enraivecido" para atacar proativamente
        if (mode == BuffMobsConfig.PassiveMobAggression.Mode.HOSTILE) {
            ENRAGED.add(mob.getUUID());
        }

        INITIALIZED.add(mob.getUUID());
        BuffMobsMod.LOGGER.debug("[BuffMobs] PassiveAggression initialized: {} (mode={})",
                BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()), mode);
    }

    public static void onMobRemoved(Mob mob) {
        INITIALIZED.remove(mob.getUUID());
        PARTICLES_SHOWN.remove(mob.getUUID());
        ENRAGED.remove(mob.getUUID());
    }

    public static void onMobHurtByPlayer(Mob mob, Player player) {
        BuffMobsConfig.PassiveMobAggression.Mode mode = BuffMobsConfig.INSTANCE.passiveMobAggression.mode;
        if (mode == BuffMobsConfig.PassiveMobAggression.Mode.OFF) return;
        if (!MobBuffUtil.isPassiveAggressiveMob(mob)) return;

        // No modo HOSTILE, o mob já está enraivecido, então não precisa ativar novamente
        if (mode == BuffMobsConfig.PassiveMobAggression.Mode.NEUTRAL) {
            if (!ENRAGED.contains(mob.getUUID())) {
                ENRAGED.add(mob.getUUID());
                removePanicGoals(mob);
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
        BuffMobsConfig.PassiveMobAggression.Mode mode = BuffMobsConfig.INSTANCE.passiveMobAggression.mode;
        if (mode == BuffMobsConfig.PassiveMobAggression.Mode.OFF) return;

        // No modo HOSTILE, não precisa gerenciar ENRAGED, pois o mob ataca por conta própria
        if (mode == BuffMobsConfig.PassiveMobAggression.Mode.NEUTRAL) {
            if (!ENRAGED.contains(mob.getUUID())) return;

            if (mob.getTarget() == null || !mob.getTarget().isAlive()) {
                ENRAGED.remove(mob.getUUID());
                PARTICLES_SHOWN.remove(mob.getUUID());
                mob.setTarget(null);
                return;
            }
            removePanicGoals(mob);
        }
        // No modo HOSTILE, apenas garantimos que os panic goals continuem removidos
        if (mode == BuffMobsConfig.PassiveMobAggression.Mode.HOSTILE) {
            removePanicGoals(mob);
        }
    }

    public static void forceReinit() {
        INITIALIZED.clear();
        PARTICLES_SHOWN.clear();
        ENRAGED.clear();
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

    private static void replaceAIGoals(Mob mob) {
        if (!(mob instanceof PathfinderMob pathMob)) return;

        GoalSelector goalSelector = getGoalSelector(mob);
        GoalSelector targetSelector = getTargetSelector(mob);
        if (goalSelector == null || targetSelector == null) {
            BuffMobsMod.LOGGER.error("[BuffMobs] Cannot modify AI for {}: GoalSelector not accessible",
                    BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()));
            return;
        }

        // Remove panic e flee goals
        goalSelector.getAvailableGoals().removeIf(wrapped -> {
            Goal g = wrapped.getGoal();
            return g instanceof PanicGoal ||
                    g.getClass().getSimpleName().contains("Flee") ||
                    g.getClass().getSimpleName().contains("Avoid") ||
                    g.getClass().getSimpleName().contains("Escape");
        });

        // Remove todos os target goals existentes para evitar conflitos
        targetSelector.getAvailableGoals().clear();

        // Adiciona o goal de ataque melee
        BuffMobsConfig.PassiveMobAggression.Mode mode = BuffMobsConfig.INSTANCE.passiveMobAggression.mode;
        goalSelector.addGoal(0, new PassiveAggressionMeleeGoal(pathMob, 1.2, mode));

        // Adiciona o target goal apropriado
        if (mode == BuffMobsConfig.PassiveMobAggression.Mode.NEUTRAL) {
            targetSelector.addGoal(0, new HurtByTargetGoal(pathMob));
        } else if (mode == BuffMobsConfig.PassiveMobAggression.Mode.HOSTILE) {
            targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(pathMob, Player.class, true));
        }

        BuffMobsMod.LOGGER.debug("[BuffMobs] AI goals replaced for {} (mode={})",
                BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()), mode);
    }

    private static void removePanicGoals(Mob mob) {
        if (!(mob instanceof PathfinderMob)) return;
        GoalSelector goalSelector = getGoalSelector(mob);
        if (goalSelector == null) return;
        goalSelector.getAvailableGoals().removeIf(wrapped -> wrapped.getGoal() instanceof PanicGoal);
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
        private final BuffMobsConfig.PassiveMobAggression.Mode mode;
        private int attackCooldown;

        PassiveAggressionMeleeGoal(PathfinderMob mob, double speedModifier,
                                   BuffMobsConfig.PassiveMobAggression.Mode mode) {
            this.owner = mob;
            this.speedModifier = speedModifier;
            this.mode = mode;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.owner.getTarget();
            if (target == null || !target.isAlive()) return false;

            if (this.mode == BuffMobsConfig.PassiveMobAggression.Mode.HOSTILE) {
                // No modo HOSTILE, ataca qualquer jogador que esteja no target
                return target instanceof Player;
            } else if (this.mode == BuffMobsConfig.PassiveMobAggression.Mode.NEUTRAL) {
                // No modo NEUTRAL, só ataca se estiver enraivecido
                return ENRAGED.contains(this.owner.getUUID()) && target instanceof Player;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.owner.getTarget();
            if (target == null || !target.isAlive()) return false;

            if (this.mode == BuffMobsConfig.PassiveMobAggression.Mode.HOSTILE) {
                return target instanceof Player;
            } else if (this.mode == BuffMobsConfig.PassiveMobAggression.Mode.NEUTRAL) {
                return ENRAGED.contains(this.owner.getUUID()) && target instanceof Player;
            }
            return false;
        }

        @Override
        public void start() {
            this.attackCooldown = 0;
            removePanicGoals(this.owner);
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