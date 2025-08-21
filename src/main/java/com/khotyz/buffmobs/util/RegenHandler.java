package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.config.BuffMobsConfig;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.server.world.ServerWorld;
import java.util.WeakHashMap;
import java.util.Map;

public class RegenHandler {
    private static final Map<MobEntity, RegenData> REGEN_TRACKERS = new WeakHashMap<>();

    private static class RegenData {
        int tickCounter = 0;
        int amplifier = 0;
    }

    public static void startCustomRegen(MobEntity mob, int amplifier) {
        if (amplifier <= 0) return;

        RegenData data = REGEN_TRACKERS.computeIfAbsent(mob, k -> new RegenData());
        data.amplifier = amplifier;
        data.tickCounter = 0;
    }

    public static void handleRegenTick(MobEntity mob) {
        if (!mob.isAlive() || !FilterUtil.isValidMob(mob)) {
            REGEN_TRACKERS.remove(mob);
            return;
        }

        RegenData data = REGEN_TRACKERS.get(mob);
        if (data == null || data.amplifier <= 0) return;

        data.tickCounter++;

        int regenInterval = Math.max(10, 50 - (data.amplifier * 10));

        if (data.tickCounter >= regenInterval) {
            data.tickCounter = 0;

            float healAmount = 1.0f + (data.amplifier * 0.5f);
            float currentHealth = mob.getHealth();
            float maxHealth = mob.getMaxHealth();

            if (currentHealth < maxHealth) {
                // Direct healing works for both undead and living mobs
                if (isUndeadMob(mob)) {
                    // For undead, use direct healing without status effects
                    mob.setHealth(Math.min(maxHealth, currentHealth + healAmount));
                } else {
                    // For living mobs, use heal() method which handles all edge cases
                    mob.heal(healAmount);
                }

                // Spawn particles if visual effects enabled
                if (BuffMobsConfig.showVisualEffects() && mob.getWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(
                            net.minecraft.particle.ParticleTypes.HAPPY_VILLAGER,
                            mob.getX(), mob.getY() + mob.getHeight() / 2, mob.getZ(),
                            3, 0.3, 0.3, 0.3, 0.02
                    );
                }
            }
        }
    }

    public static void stopCustomRegen(MobEntity mob) {
        REGEN_TRACKERS.remove(mob);
    }

    public static boolean hasCustomRegen(MobEntity mob) {
        RegenData data = REGEN_TRACKERS.get(mob);
        return data != null && data.amplifier > 0;
    }

    private static boolean isUndeadMob(MobEntity mob) {
        // Check vanilla undead mobs
        if (mob instanceof AbstractSkeletonEntity ||
                mob instanceof ZombieEntity ||
                mob instanceof ZombifiedPiglinEntity ||
                mob instanceof DrownedEntity ||
                mob instanceof HuskEntity ||
                mob instanceof StrayEntity ||
                mob instanceof WitherEntity ||
                mob instanceof PhantomEntity ||
                mob instanceof WitherSkeletonEntity) {
            return true;
        }

        // Check for modded undead mobs by name patterns
        String mobName = mob.getType().toString().toLowerCase();
        return mobName.contains("zombie") ||
                mobName.contains("skeleton") ||
                mobName.contains("undead") ||
                mobName.contains("wither") ||
                mobName.contains("phantom") ||
                mobName.contains("wraith") ||
                mobName.contains("ghost") ||
                mobName.contains("spirit") ||
                mobName.contains("lich") ||
                mobName.contains("mummy") ||
                mobName.contains("draugr") ||
                mobName.contains("revenant") ||
                mobName.contains("bone") ||
                mobName.contains("skull");
    }
}