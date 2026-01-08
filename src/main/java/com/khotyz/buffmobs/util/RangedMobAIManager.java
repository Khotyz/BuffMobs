package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class RangedMobAIManager {
    private static final Map<UUID, MobState> MOB_STATES = new HashMap<>();
    private static final double MELEE_SPEED_MULTIPLIER = 1.3;
    private static final double MELEE_ATTACK_RANGE = 3.0;

    public static void initializeMob(MobEntity mob) {
        if (!BuffMobsMod.CONFIG.rangedMeleeSwitching.enabled) return;
        if (!isRangedMob(mob)) return;
        if (MOB_STATES.containsKey(mob.getUuid())) return;

        MobState state = new MobState();
        state.originalWeapon = mob.getStackInHand(Hand.MAIN_HAND).copy();
        state.behaviorMode = determineBehaviorMode();

        EntityAttributeInstance speedAttr = mob.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            state.baseSpeed = speedAttr.getBaseValue();
        }

        MOB_STATES.put(mob.getUuid(), state);

        BuffMobsMod.LOGGER.info("Initialized {} with behavior: {}", mob.getType(), state.behaviorMode);
    }

    private static BehaviorMode determineBehaviorMode() {
        var mode = BuffMobsMod.CONFIG.rangedMeleeSwitching.behaviorMode;

        return switch (mode) {
            case MELEE_ONLY -> BehaviorMode.MELEE_ONLY;
            case RANGED_ONLY -> BehaviorMode.RANGED_ONLY;
            case RANDOM -> Math.random() < 0.5 ? BehaviorMode.MELEE_ONLY : BehaviorMode.RANGED_ONLY;
            default -> BehaviorMode.ADAPTIVE;
        };
    }

    public static void updateMobBehavior(MobEntity mob) {
        if (!BuffMobsMod.CONFIG.rangedMeleeSwitching.enabled) return;
        if (!isRangedMob(mob)) return;

        MobState state = MOB_STATES.get(mob.getUuid());
        if (state == null) {
            initializeMob(mob);
            state = MOB_STATES.get(mob.getUuid());
            if (state == null) return;
        }

        PlayerEntity target = mob.getWorld().getClosestPlayer(mob, 32.0);
        if (target == null || target.isSpectator() || target.isCreative() || !target.isAlive()) {
            if (state.isRetreating || state.inMeleeMode) {
                resetState(mob, state);
            }
            return;
        }

        double distance = mob.distanceTo(target);
        double switchDistance = BuffMobsMod.CONFIG.rangedMeleeSwitching.switchDistance;
        long currentTime = mob.getWorld().getTime();

        switch (state.behaviorMode) {
            case MELEE_ONLY -> handleMeleeOnly(mob, state, target, distance, switchDistance, currentTime);
            case RANGED_ONLY -> handleRangedOnly(mob, state, target, distance);
            case ADAPTIVE -> handleAdaptive(mob, state, target, distance, switchDistance, currentTime);
        }
    }

    private static void handleMeleeOnly(MobEntity mob, MobState state, PlayerEntity target,
                                        double distance, double switchDistance, long currentTime) {
        if (!state.inMeleeMode) {
            switchToMeleeMode(mob, state);
        }
        handleMeleeAttack(mob, target, state, distance, currentTime);
    }

    private static void handleRangedOnly(MobEntity mob, MobState state, PlayerEntity target, double distance) {
        if (state.inMeleeMode) {
            switchToRangedMode(mob, state);
        }

        if (!BuffMobsMod.CONFIG.rangedMeleeSwitching.tacticalMovement.enabled) {
            if (mob.getTarget() != target) {
                mob.setTarget(target);
            }
            return;
        }

        var config = BuffMobsMod.CONFIG.rangedMeleeSwitching.tacticalMovement;
        double maintainDist = config.maintainDistance;

        boolean tooClose = distance < maintainDist * 0.7;
        boolean shouldMaintain = distance < maintainDist && distance >= maintainDist * 0.7;

        if (tooClose || (shouldMaintain && state.isRetreating)) {
            handleTacticalRetreat(mob, state, target, distance);
        } else {
            if (state.isRetreating) {
                stopRetreating(mob, state);
            }

            if (mob.getTarget() != target) {
                mob.setTarget(target);
            }
        }
    }

    private static void handleAdaptive(MobEntity mob, MobState state, PlayerEntity target,
                                       double distance, double switchDistance, long currentTime) {
        if (currentTime - state.lastSwitchTime < 60) {
            if (state.inMeleeMode) {
                handleMeleeAttack(mob, target, state, distance, currentTime);
            }
            return;
        }

        if (!state.inMeleeMode && distance <= switchDistance) {
            switchToMeleeMode(mob, state);
        } else if (state.inMeleeMode && distance > switchDistance + 2.0) {
            switchToRangedMode(mob, state);
        } else if (state.inMeleeMode) {
            handleMeleeAttack(mob, target, state, distance, currentTime);
        } else {
            handleRangedOnly(mob, state, target, distance);
        }
    }

    private static void handleTacticalRetreat(MobEntity mob, MobState state, PlayerEntity target, double distance) {
        if (!state.isRetreating) {
            state.isRetreating = true;
            state.retreatStartDistance = distance;
            state.strafeDirection = mob.getRandom().nextBoolean() ? 1 : -1;
            state.isRunningAway = false;
            BuffMobsMod.LOGGER.debug("{} started tactical retreat", mob.getType());
        }

        var config = BuffMobsMod.CONFIG.rangedMeleeSwitching.tacticalMovement;
        double maintainDist = config.maintainDistance;
        boolean isPanicking = distance <= config.panicDistance;
        boolean tooClose = distance < maintainDist * 0.7;
        boolean atSafeDistance = distance >= maintainDist * 0.9;

        // If too close, run away without shooting
        if (tooClose) {
            if (!state.isRunningAway) {
                state.isRunningAway = true;
                BuffMobsMod.LOGGER.debug("{} too close, running away", mob.getType());
            }

            mob.stopUsingItem();
            mob.clearActiveItem();
            mob.setTarget(null);

            double speedMult = isPanicking ? config.panicSpeed : config.retreatSpeed;

            Vec3d mobPos = mob.getPos();
            Vec3d targetPos = target.getPos();
            Vec3d retreatDir = mobPos.subtract(targetPos).normalize();
            Vec3d retreatTarget = mobPos.add(retreatDir.multiply(6.0));

            mob.getNavigation().startMovingTo(retreatTarget.x, retreatTarget.y, retreatTarget.z, speedMult);
            mob.getLookControl().lookAt(target.getX(), target.getEyeY(), target.getZ());

            return;
        }

        // At safe distance - stop running and start shooting
        if (state.isRunningAway && atSafeDistance) {
            state.isRunningAway = false;
            mob.getNavigation().stop();
            BuffMobsMod.LOGGER.debug("{} reached safe distance", mob.getType());
        }

        // Safe distance behavior
        if (!state.isRunningAway) {
            if (mob.getTarget() != target) {
                mob.setTarget(target);
            }

            // Kiting enabled - move and shoot
            if (config.enableStrafing) {
                state.strafeTicks++;
                if (state.strafeTicks >= config.strafeInterval) {
                    state.strafeTicks = 0;
                    state.strafeDirection *= -1;
                }

                Vec3d mobPos = mob.getPos();
                Vec3d targetPos = target.getPos();
                Vec3d retreatDir = mobPos.subtract(targetPos).normalize();
                Vec3d strafeDir = new Vec3d(-retreatDir.z, 0, retreatDir.x).multiply(state.strafeDirection);

                Vec3d kiteTarget = mobPos.add(retreatDir.multiply(1.5)).add(strafeDir.multiply(2.5));

                mob.getNavigation().startMovingTo(kiteTarget.x, kiteTarget.y, kiteTarget.z, config.retreatSpeed * 0.6);
            } else {
                // No kiting - stand still
                mob.getNavigation().stop();
            }

            mob.getLookControl().lookAt(target.getX(), target.getEyeY(), target.getZ());

            // Shoot
            state.retreatTicks++;
            if (state.retreatTicks >= state.nextShootTick) {
                if (tryShootArrow(mob, target, distance)) {
                    state.nextShootTick = state.retreatTicks + (30 + mob.getRandom().nextInt(20));
                }
            }
        }
    }

    private static void handleMeleeAttack(MobEntity mob, PlayerEntity target, MobState state,
                                          double distance, long currentTime) {
        if (mob.getTarget() != target) {
            mob.setTarget(target);
        }

        if (distance > MELEE_ATTACK_RANGE) {
            mob.getNavigation().startMovingTo(target, MELEE_SPEED_MULTIPLIER);
        } else {
            mob.getNavigation().stop();

            Vec3d targetPos = new Vec3d(target.getX(), target.getEyeY(), target.getZ());
            mob.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, targetPos);
            mob.getLookControl().lookAt(target.getX(), target.getEyeY(), target.getZ());

            if (currentTime - state.lastAttackTime >= 20) {
                performMeleeAttack(mob, target);
                state.lastAttackTime = currentTime;
            }
        }
    }

    private static void performMeleeAttack(MobEntity mob, LivingEntity target) {
        if (mob.getWorld() instanceof ServerWorld serverWorld) {
            mob.tryAttack(serverWorld, target);
            mob.swingHand(Hand.MAIN_HAND, true);
        }
    }

    private static void switchToMeleeMode(MobEntity mob, MobState state) {
        ItemStack meleeWeapon = MeleeWeaponManager.generateMeleeWeapon(mob);

        mob.equipStack(EquipmentSlot.MAINHAND, meleeWeapon);
        mob.setStackInHand(Hand.MAIN_HAND, meleeWeapon);

        PlayerEntity target = mob.getWorld().getClosestPlayer(mob, 32.0);
        if (target != null) {
            mob.setTarget(target);
        }

        state.inMeleeMode = true;
        state.isRetreating = false;
        state.lastSwitchTime = mob.getWorld().getTime();
        state.lastAttackTime = 0;

        BuffMobsMod.LOGGER.debug("{} switched to MELEE mode", mob.getType());
    }

    private static void switchToRangedMode(MobEntity mob, MobState state) {
        ItemStack rangedWeapon = state.originalWeapon.isEmpty() ?
                getDefaultRangedWeapon(mob) : state.originalWeapon.copy();

        mob.equipStack(EquipmentSlot.MAINHAND, rangedWeapon);
        mob.setStackInHand(Hand.MAIN_HAND, rangedWeapon);

        state.inMeleeMode = false;
        state.lastSwitchTime = mob.getWorld().getTime();

        BuffMobsMod.LOGGER.debug("{} switched to RANGED mode", mob.getType());
    }

    private static void stopRetreating(MobEntity mob, MobState state) {
        state.isRetreating = false;
        state.isRunningAway = false;
        state.retreatTicks = 0;
        state.nextShootTick = 0;
        state.strafeTicks = 0;

        mob.getNavigation().stop();

        BuffMobsMod.LOGGER.debug("{} stopped retreating", mob.getType());
    }

    private static void resetState(MobEntity mob, MobState state) {
        if (state.inMeleeMode) {
            switchToRangedMode(mob, state);
        }
        stopRetreating(mob, state);
    }

    private static boolean tryShootArrow(MobEntity mob, PlayerEntity target, double distance) {
        ItemStack weapon = mob.getMainHandStack();
        if (!(weapon.getItem() instanceof RangedWeaponItem)) return false;
        if (!(mob.getWorld() instanceof ServerWorld world)) return false;

        mob.setCurrentHand(Hand.MAIN_HAND);

        world.getServer().execute(() -> {
            if (mob.isRemoved() || !mob.isAlive()) return;

            mob.clearActiveItem();

            PersistentProjectileEntity arrow = new net.minecraft.entity.projectile.ArrowEntity(world, mob, weapon, null);

            if (mob instanceof WitherSkeletonEntity) {
                arrow.setDamage(2.5);
            } else {
                arrow.setDamage(2.0);
            }

            double dx = target.getX() - mob.getX();
            double dy = target.getBodyY(0.3333333333333333) - arrow.getY();
            double dz = target.getZ() - mob.getZ();
            double horizontalDist = Math.sqrt(dx * dx + dz * dz);

            float power = 1.6F;
            float divergence = 12.0F;

            if (distance > 10.0) {
                power = 2.0F;
                divergence = 10.0F;
            }

            arrow.setVelocity(dx, dy + horizontalDist * 0.2, dz, power, divergence);

            if (mob instanceof SkeletonEntity || mob instanceof WitherSkeletonEntity || mob instanceof StrayEntity) {
                mob.playSound(net.minecraft.sound.SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F);
            } else {
                mob.playSound(net.minecraft.sound.SoundEvents.ITEM_CROSSBOW_SHOOT, 1.0F, 1.0F);
            }

            world.spawnEntity(arrow);
            mob.swingHand(Hand.MAIN_HAND);

            BuffMobsMod.LOGGER.debug("{} shot arrow (dist: {})", mob.getType(), String.format("%.1f", distance));
        });

        return true;
    }

    private static ItemStack getDefaultRangedWeapon(MobEntity mob) {
        if (mob instanceof SkeletonEntity || mob instanceof StrayEntity ||
                mob instanceof WitherSkeletonEntity) {
            return new ItemStack(Items.BOW);
        } else if (mob instanceof PiglinEntity || mob instanceof PillagerEntity) {
            return new ItemStack(Items.CROSSBOW);
        }
        return ItemStack.EMPTY;
    }

    public static boolean shouldCancelRangedAttack(MobEntity mob) {
        MobState state = MOB_STATES.get(mob.getUuid());
        return state != null && state.isRetreating;
    }

    public static boolean isRangedMob(MobEntity mob) {
        String mobId = Registries.ENTITY_TYPE.getId(mob.getType()).toString();

        if (BuffMobsMod.CONFIG.rangedMeleeSwitching.customRangedMobs.contains(mobId)) {
            return true;
        }

        return mob instanceof SkeletonEntity ||
                mob instanceof StrayEntity ||
                mob instanceof WitherSkeletonEntity ||
                mob instanceof PiglinEntity ||
                mob instanceof PillagerEntity;
    }

    public static boolean isInMeleeMode(MobEntity mob) {
        MobState state = MOB_STATES.get(mob.getUuid());
        return state != null && state.inMeleeMode;
    }

    public static void cleanup(MobEntity mob) {
        MOB_STATES.remove(mob.getUuid());
    }

    private enum BehaviorMode {
        ADAPTIVE,
        MELEE_ONLY,
        RANGED_ONLY
    }

    private static class MobState {
        BehaviorMode behaviorMode = BehaviorMode.ADAPTIVE;
        boolean inMeleeMode = false;
        boolean isRetreating = false;
        boolean isRunningAway = false;
        ItemStack originalWeapon = ItemStack.EMPTY;
        double baseSpeed = 0.25;

        long lastSwitchTime = 0;
        long lastAttackTime = 0;

        int retreatTicks = 0;
        int nextShootTick = 0;
        int strafeTicks = 0;
        int strafeDirection = 1;
        double retreatStartDistance = 0;
    }
}