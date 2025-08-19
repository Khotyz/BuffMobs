package com.khotyz.buffmobs.events;

import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.util.FilterUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import java.util.Random;

public class DamageHandler {
    private static final Random RANDOM = new Random();

    public boolean onLivingDamage(LivingEntity entity, DamageSource damageSource, float damageAmount) {
        if (!BuffMobsConfig.isEnabled() || !BuffMobsConfig.applyHarmfulEffects() || !(entity instanceof PlayerEntity player)) {
            return true;
        }

        MobEntity attacker = null;

        // Direct attack
        if (damageSource.getAttacker() instanceof MobEntity mob) {
            attacker = mob;
        }
        // Projectile attack
        else if (damageSource.getSource() instanceof ProjectileEntity projectile) {
            if (projectile.getOwner() instanceof MobEntity mob) {
                attacker = mob;
            }
        }

        if (attacker != null && FilterUtil.isValidMob(attacker) &&
                RANDOM.nextDouble() < BuffMobsConfig.getHarmfulEffectChance()) {
            applyRandomHarmfulEffect(player);
        }

        return true;
    }

    private void applyRandomHarmfulEffect(PlayerEntity player) {
        int effectType = RANDOM.nextInt(3);
        boolean showParticles = BuffMobsConfig.showVisualEffects();

        switch (effectType) {
            case 0 -> {
                int duration = BuffMobsConfig.getPoisonDuration() * 20;
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.POISON, duration, 0, false, showParticles, showParticles));
            }
            case 1 -> {
                int duration = BuffMobsConfig.getSlownessDuration() * 20;
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.SLOWNESS, duration, 0, false, showParticles, showParticles));
            }
            case 2 -> {
                int duration = BuffMobsConfig.getWitherDuration() * 20;
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.WITHER, duration, 0, false, showParticles, showParticles));
            }
        }
    }
}