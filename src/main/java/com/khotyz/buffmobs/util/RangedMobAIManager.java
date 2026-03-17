package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class RangedMobAIManager {

    private static final double MELEE_CHASE_SPEED  = 1.2;
    private static final double KITE_FLEE_SPEED    = 1.35;
    private static final double KITE_SAFE_DISTANCE = 12.0;
    private static final int    SWITCH_COOLDOWN    = 60;
    private static final double MELEE_ATTACK_REACH = 2.5;
    private static final double HYSTERESIS         = 2.5;

    // Speed boost modifier applied during melee mode
    private static final ResourceLocation MELEE_SPEED_ID =
            ResourceLocation.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "melee_speed");

    private static final Map<UUID, MobState> MOB_STATES = new HashMap<>();
    private static final Random RAND = new Random();

    public static void initializeMob(Mob mob) {
        if (!BuffMobsConfig.INSTANCE.rangedMeleeSwitching.enabled.get()) return;
        if (!isRangedMob(mob) || MOB_STATES.containsKey(mob.getUUID())) return;

        MobState state = new MobState();
        state.originalWeapon = mob.getMainHandItem().copy();

        BuffMobsConfig.RangedMeleeSwitching.BehaviorMode mode =
                BuffMobsConfig.INSTANCE.rangedMeleeSwitching.behaviorMode.get();
        if (mode == BuffMobsConfig.RangedMeleeSwitching.BehaviorMode.RANDOM) {
            state.usesMelee = RAND.nextBoolean();
        } else {
            state.usesMelee = (mode == BuffMobsConfig.RangedMeleeSwitching.BehaviorMode.MELEE);
        }

        MOB_STATES.put(mob.getUUID(), state);
        BuffMobsMod.LOGGER.debug("[BuffMobs] RangedAI {} -> {}",
                BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()),
                state.usesMelee ? "MELEE" : "KITE");
    }

    // Called every tick — handles both melee drive and kite flee
    public static void driveTick(Mob mob) {
        if (!BuffMobsConfig.INSTANCE.rangedMeleeSwitching.enabled.get()) return;
        MobState state = MOB_STATES.get(mob.getUUID());
        if (state == null || !state.active) return;

        Player target = mob.level().getNearestPlayer(mob, 32.0);
        if (target == null || !target.isAlive() || target.isSpectator() || target.isCreative()) return;

        if (state.usesMelee) {
            driveMeleeAttack(mob, target, state, mob.level().getGameTime());
        } else {
            // Kite: flee every 10 ticks or when navigation is done
            if (mob.tickCount % 10 == 0 || mob.getNavigation().isDone()) {
                driveKite(mob, target);
            }
        }
    }

    public static void updateMobBehavior(Mob mob) {
        if (!BuffMobsConfig.INSTANCE.rangedMeleeSwitching.enabled.get() || !isRangedMob(mob)) return;

        MobState state = MOB_STATES.get(mob.getUUID());
        if (state == null) { initializeMob(mob); return; }

        Player target = mob.level().getNearestPlayer(mob, 32.0);
        if (target == null || target.isSpectator() || target.isCreative() || !target.isAlive()) {
            if (state.active) exitActiveMode(mob, state);
            return;
        }

        double dist       = mob.distanceTo(target);
        double switchDist = BuffMobsConfig.INSTANCE.rangedMeleeSwitching.switchDistance.get();
        long   now        = mob.level().getGameTime();
        boolean tooClose  = dist <= switchDist;
        boolean farEnough = dist > switchDist + HYSTERESIS;

        if (state.usesMelee) {
            if (!state.active && tooClose && now - state.lastSwitchTime >= SWITCH_COOLDOWN) {
                enterMeleeMode(mob, state, target, now);
            } else if (state.active && farEnough && now - state.lastSwitchTime >= SWITCH_COOLDOWN) {
                exitActiveMode(mob, state);
                state.lastSwitchTime = now;
            }
        } else {
            if (tooClose && !state.active && now - state.lastSwitchTime >= SWITCH_COOLDOWN) {
                enterKiteMode(mob, state, now);
            } else if (!tooClose && state.active && now - state.lastSwitchTime >= SWITCH_COOLDOWN) {
                exitActiveMode(mob, state);
                state.lastSwitchTime = now;
            }
        }
    }

    public static boolean isRangedMob(Mob mob) {
        String id = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        if (BuffMobsConfig.INSTANCE.rangedMeleeSwitching.customRangedMobs.get().contains(id)) return true;
        return mob instanceof AbstractSkeleton || mob instanceof Stray
                || mob instanceof Piglin || mob instanceof Pillager;
    }

    public static boolean isInMeleeMode(Mob mob) {
        MobState s = MOB_STATES.get(mob.getUUID());
        return s != null && s.active && s.usesMelee;
    }

    public static void cleanup(Mob mob) {
        MobState s = MOB_STATES.remove(mob.getUUID());
        if (s == null) return;
        removeMeleeSpeedModifier(mob);
    }

    private static void enterMeleeMode(Mob mob, MobState state, Player target, long now) {
        mob.setItemSlot(EquipmentSlot.MAINHAND, MeleeWeaponManager.generateMeleeWeapon(mob));
        mob.setTarget(target);

        // Apply speed modifier so the mob is visibly faster while chasing
        double meleeSpeedMult = BuffMobsConfig.INSTANCE.rangedMeleeSwitching.meleeSpeedMultiplier.get();
        AttributeInstance speedAttr = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.removeModifier(MELEE_SPEED_ID);
            if (meleeSpeedMult != 1.0) {
                speedAttr.addTransientModifier(new AttributeModifier(
                        MELEE_SPEED_ID, meleeSpeedMult - 1.0,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }
        }

        state.active         = true;
        state.lastSwitchTime = now;
        state.lastAttackTime = 0;
        BuffMobsMod.LOGGER.debug("[BuffMobs] {} -> MELEE", mob.getType());
    }

    private static void driveMeleeAttack(Mob mob, Player target, MobState state, long now) {
        if (mob.getTarget() != target) mob.setTarget(target);
        double dist = mob.distanceTo(target);
        if (dist > MELEE_ATTACK_REACH) {
            mob.getNavigation().moveTo(target, MELEE_CHASE_SPEED);
        } else {
            mob.getNavigation().stop();
            mob.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ());
            if (now - state.lastAttackTime >= 20) {
                performMeleeHit(mob, target);
                state.lastAttackTime = now;
            }
        }
    }

    private static void performMeleeHit(Mob mob, LivingEntity target) {
        if (!(mob.level() instanceof ServerLevel)) return;
        Vec3 dir = new Vec3(target.getX() - mob.getX(), 0, target.getZ() - mob.getZ())
                .normalize().scale(0.15);
        mob.setDeltaMovement(dir.x, 0.1, dir.z);
        mob.hurtMarked = true;
        mob.doHurtTarget(target);
        mob.swing(InteractionHand.MAIN_HAND);
    }

    private static void enterKiteMode(Mob mob, MobState state, long now) {
        state.active         = true;
        state.lastSwitchTime = now;
        BuffMobsMod.LOGGER.debug("[BuffMobs] {} -> KITE", mob.getType());
    }

    private static void driveKite(Mob mob, Player target) {
        mob.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ());
        Vec3 away = mob.position().subtract(target.position());
        if (away.lengthSqr() < 0.001)
            away = new Vec3(RAND.nextDouble() - 0.5, 0, RAND.nextDouble() - 0.5);
        Vec3 dest = mob.position().add(away.normalize().scale(KITE_SAFE_DISTANCE));
        mob.getNavigation().moveTo(dest.x, dest.y, dest.z, KITE_FLEE_SPEED);
    }

    private static void exitActiveMode(Mob mob, MobState state) {
        ItemStack rangedWeapon = !state.originalWeapon.isEmpty()
                ? state.originalWeapon.copy()
                : getDefaultRangedWeapon(mob);
        mob.setItemSlot(EquipmentSlot.MAINHAND, rangedWeapon);
        removeMeleeSpeedModifier(mob);
        mob.setTarget(null);
        mob.getNavigation().stop();
        state.active         = false;
        state.lastSwitchTime = mob.level().getGameTime();
        BuffMobsMod.LOGGER.debug("[BuffMobs] {} -> RANGED", mob.getType());
    }

    private static void removeMeleeSpeedModifier(Mob mob) {
        AttributeInstance speedAttr = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) speedAttr.removeModifier(MELEE_SPEED_ID);
    }

    private static ItemStack getDefaultRangedWeapon(Mob mob) {
        if (mob instanceof AbstractSkeleton || mob instanceof Stray)   return new ItemStack(Items.BOW);
        if (mob instanceof Piglin           || mob instanceof Pillager) return new ItemStack(Items.CROSSBOW);
        return ItemStack.EMPTY;
    }

    private static class MobState {
        boolean   usesMelee      = true;
        boolean   active         = false;
        ItemStack originalWeapon = ItemStack.EMPTY;
        long      lastSwitchTime = 0;
        long      lastAttackTime = 0;
    }
}