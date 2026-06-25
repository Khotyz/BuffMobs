package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PassiveMobAggressionHandler {

    private static final Set<UUID> INITIALIZED   = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> ENRAGED       = ConcurrentHashMap.newKeySet();
    private static final Map<UUID, Long> LAST_ATTACK = new ConcurrentHashMap<>();
    private static final Map<UUID, UUID> MOB_TARGET  = new ConcurrentHashMap<>();

    private static final double CHASE_SPEED     = 1.2;
    private static final double ATTACK_RANGE    = 2.5;
    private static final long   ATTACK_COOLDOWN = 20L;

    private static final Set<String> FLEE_GOAL_NAMES = Set.of(
            "PanicGoal", "FleeEntityGoal", "FleeGoal", "AvoidEntityGoal",
            "RunAroundLikeCrazyGoal", "HorseFleesGoal"
    );

    public static void onMobInitialized(Mob mob) {
        if (!BuffMobsConfig.INSTANCE.passiveMobAggression.enabled) return;
        if (!MobBuffUtil.isPassiveAggressiveMob(mob)) return;
        if (INITIALIZED.contains(mob.getUUID())) return;
        INITIALIZED.add(mob.getUUID());
        BuffMobsMod.LOGGER.debug("[BuffMobs] PassiveAggression registered: {}",
                BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()));
    }

    public static void onMobRemoved(Mob mob) {
        INITIALIZED.remove(mob.getUUID());
        ENRAGED.remove(mob.getUUID());
        LAST_ATTACK.remove(mob.getUUID());
        MOB_TARGET.remove(mob.getUUID());
    }

    public static void onMobHurtByPlayer(Mob mob, Player player) {
        if (!BuffMobsConfig.INSTANCE.passiveMobAggression.enabled) return;
        if (!MobBuffUtil.isPassiveAggressiveMob(mob)) return;
        if (ENRAGED.contains(mob.getUUID())) return;

        removePeacefulGoals(mob);
        ENRAGED.add(mob.getUUID());
        MOB_TARGET.put(mob.getUUID(), player.getUUID());
        spawnAngryParticles(mob);

        BuffMobsMod.LOGGER.debug("[BuffMobs] PassiveAggression enraged: {} -> {}",
                BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()), player.getName().getString());
    }

    public static void tick(Mob mob) {
        if (!BuffMobsConfig.INSTANCE.passiveMobAggression.enabled) return;
        if (!MobBuffUtil.isPassiveAggressiveMob(mob)) return;
        if (!ENRAGED.contains(mob.getUUID())) return;

        UUID targetId = MOB_TARGET.get(mob.getUUID());
        if (targetId == null) { calmDown(mob); return; }

        Player target = findPlayerById(mob, targetId);

        if (target == null || !target.isAlive()) {
            calmDown(mob);
            return;
        }

        mob.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ());
        mob.getNavigation().moveTo(target, CHASE_SPEED);

        double dist = mob.distanceTo(target);
        if (dist <= ATTACK_RANGE) {
            long now  = mob.level().getGameTime();
            long last = LAST_ATTACK.getOrDefault(mob.getUUID(), 0L);
            if (now - last >= ATTACK_COOLDOWN && mob.level() instanceof ServerLevel) {
                double dmg = MobBuffUtil.getPassiveDamageForMob(mob);
                target.hurt(mob.level().damageSources().mobAttack(mob), (float) dmg);
                LAST_ATTACK.put(mob.getUUID(), now);
                spawnAngryParticles(mob);
            }
        }
    }

    public static void forceReinit() {
        INITIALIZED.clear();
        ENRAGED.clear();
        LAST_ATTACK.clear();
        MOB_TARGET.clear();
    }

    private static void calmDown(Mob mob) {
        ENRAGED.remove(mob.getUUID());
        LAST_ATTACK.remove(mob.getUUID());
        MOB_TARGET.remove(mob.getUUID());
        mob.getNavigation().stop();
        mob.setTarget(null);
        BuffMobsMod.LOGGER.debug("[BuffMobs] PassiveAggression calmed: {}",
                BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()));
    }

    private static void removePeacefulGoals(Mob mob) {
        try {
            mob.goalSelector.getAvailableGoals().removeIf(wrapped -> {
                if (FLEE_GOAL_NAMES.contains(wrapped.getGoal().getClass().getSimpleName())) return true;
                return wrapped.getGoal() instanceof PanicGoal
                        || wrapped.getGoal() instanceof TemptGoal
                        || wrapped.getGoal() instanceof FollowParentGoal
                        || wrapped.getGoal() instanceof BreedGoal
                        || wrapped.getGoal() instanceof EatBlockGoal;
            });
        } catch (Exception e) {
            BuffMobsMod.LOGGER.warn("[BuffMobs] Could not remove peaceful goals from {}: {}",
                    BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()), e.getMessage());
        }
    }

    private static Player findPlayerById(Mob mob, UUID id) {
        for (Player p : mob.level().players()) {
            if (p.getUUID().equals(id)) return p;
        }
        return null;
    }

    private static void spawnAngryParticles(Mob mob) {
        if (!(mob.level() instanceof ServerLevel serverLevel)) return;
        serverLevel.sendParticles(
                ParticleTypes.ANGRY_VILLAGER,
                mob.getX(), mob.getY() + mob.getBbHeight() * 0.9, mob.getZ(),
                5, 0.3, 0.2, 0.3, 0.0);
    }
}