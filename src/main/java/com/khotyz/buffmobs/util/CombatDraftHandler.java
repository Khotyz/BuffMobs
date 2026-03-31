package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CombatDraftHandler {

    private static final Map<UUID, MobDraftState> STATES = new HashMap<>();

    private static final List<String> HARDCODED_BLACKLIST = List.of(
            "minecraft:slime",
            "minecraft:magma_cube",
            "minecraft:ghast"
    );

    public static void onMobInitialized(Mob mob) {
        if (!isEligible(mob)) return;
        STATES.putIfAbsent(mob.getUUID(), new MobDraftState());
    }

    public static void onMobRemoved(Mob mob) {
        STATES.remove(mob.getUUID());
    }

    public static void tickRestore(Mob mob) {
        // no-op: kept for compatibility with MobTickHandler call sites
    }

    public static void tick(Mob mob) {
        if (!BuffMobsConfig.INSTANCE.combatDraft.enabled) return;

        MobDraftState state = STATES.get(mob.getUUID());
        if (state == null || !mob.isAlive() || mob.isRemoved()) return;

        int maxUses = BuffMobsConfig.INSTANCE.combatDraft.maxUses;
        if (maxUses > 0 && state.useCount >= maxUses) return;

        long now = mob.level().getGameTime();
        if (now - state.lastUseTick < BuffMobsConfig.INSTANCE.combatDraft.cooldownTicks) return;

        float healthPct = mob.getHealth() / mob.getMaxHealth();
        if (healthPct > (float) BuffMobsConfig.INSTANCE.combatDraft.healthThreshold) return;

        useDraft(mob, state, now);
    }

    private static void useDraft(Mob mob, MobDraftState state, long now) {
        int draftAmp = BuffMobsConfig.INSTANCE.combatDraft.regenAmplifier;
        int duration = BuffMobsConfig.INSTANCE.combatDraft.regenDuration * 20;
        boolean isUndead = mob.getType().is(EntityTypeTags.UNDEAD);

        mob.level().playSound(null,
                mob.getX(), mob.getY(), mob.getZ(),
                SoundEvents.GENERIC_DRINK,
                mob.getSoundSource(),
                1.0f, 0.9f + mob.level().random.nextFloat() * 0.2f);

        if (mob.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    mob.getX(), mob.getY() + mob.getBbHeight() * 0.8, mob.getZ(),
                    8, 0.3, 0.3, 0.3, 0.0);
        }

        if (isUndead) {
            int existingAbs = 0;
            MobEffectInstance cur = mob.getEffect(MobEffects.ABSORPTION);
            if (cur != null) existingAbs = cur.getAmplifier() + 1;
            mob.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, duration,
                    existingAbs + draftAmp - 1, false, true, true));
            mob.setAbsorptionAmount(mob.getAbsorptionAmount() + draftAmp * 4.0f);
            mob.heal(draftAmp * 4.0f);
        } else {
            int existingRegen = 0;
            MobEffectInstance cur = mob.getEffect(MobEffects.REGENERATION);
            if (cur != null) existingRegen = cur.getAmplifier() + 1;
            mob.addEffect(new MobEffectInstance(MobEffects.REGENERATION, duration,
                    existingRegen + draftAmp - 1, false, true, true));
        }

        state.useCount++;
        state.lastUseTick = now;

        BuffMobsMod.LOGGER.debug("[BuffMobs] CombatDraft: {} ({}) used potion (amp +{}) [{}/{}]",
                mob.getType().getDescriptionId(),
                isUndead ? "undead" : "living",
                draftAmp,
                state.useCount,
                BuffMobsConfig.INSTANCE.combatDraft.maxUses == 0
                        ? "∞" : BuffMobsConfig.INSTANCE.combatDraft.maxUses);
    }

    private static boolean isEligible(Mob mob) {
        if (!MobBuffUtil.isValidMob(mob)) return false;

        String mobId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        BuffMobsConfig.CombatDraft cfg = BuffMobsConfig.INSTANCE.combatDraft;

        if (HARDCODED_BLACKLIST.contains(mobId)) return false;
        if (cfg.blacklist.contains(mobId)) return false;
        if (cfg.useWhitelist) return cfg.whitelist.contains(mobId);
        return true;
    }

    private static class MobDraftState {
        long lastUseTick = -99999;
        int  useCount    = 0;
    }
}