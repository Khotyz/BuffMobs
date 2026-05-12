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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CombatDraftHandler {

    private static final int DRINK_ANIMATION_TICKS = 32;

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
        MobDraftState state = STATES.remove(mob.getUUID());
        if (state != null && state.pendingRestore != null) {
            mob.stopUsingItem();
            mob.setItemSlot(EquipmentSlot.OFFHAND, state.pendingRestore);
            state.pendingRestore = null;
        }
    }

    public static void tick(Mob mob) {
        if (!BuffMobsConfig.INSTANCE.combatDraft.enabled.get()) return;
        if (!isEligible(mob)) return;

        MobDraftState state = STATES.computeIfAbsent(mob.getUUID(), k -> new MobDraftState());

        int maxUses = BuffMobsConfig.INSTANCE.combatDraft.maxUses.get();
        if (maxUses > 0 && state.useCount >= maxUses) return;

        long now = mob.level().getGameTime();

        if (state.animationActive && now >= state.restoreAtTick) {
            spawnDrinkParticles(mob);
            state.animationActive = false;
        }

        if (now - state.lastUseTick < BuffMobsConfig.INSTANCE.combatDraft.cooldownTicks.get()) return;
        if (mob.getHealth() > mob.getMaxHealth() * BuffMobsConfig.INSTANCE.combatDraft.healthThreshold.get()) return;

        useDraft(mob, state, now);
    }

    private static void useDraft(Mob mob, MobDraftState state, long now) {
        int amp      = BuffMobsConfig.INSTANCE.combatDraft.regenAmplifier.get() - 1;
        int duration = BuffMobsConfig.INSTANCE.combatDraft.regenDuration.get() * 20;
        boolean isUndead = mob.getType().is(EntityTypeTags.UNDEAD);

        // Schedule particle burst after the "drink" delay
        state.restoreAtTick   = now + DRINK_ANIMATION_TICKS;
        state.animationActive = true;

        mob.level().playSound(null,
                mob.getX(), mob.getY(), mob.getZ(),
                SoundEvents.GENERIC_DRINK,
                mob.getSoundSource(),
                1.0f, 0.9f + mob.level().random.nextFloat() * 0.2f);

        if (isUndead) {
            mob.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, duration, amp, false, true, true));
            float healAmount = (amp + 1) * 4.0f;
            mob.heal(healAmount);
        } else {
            mob.addEffect(new MobEffectInstance(MobEffects.REGENERATION, duration, amp, false, true, true));
        }

        state.useCount++;
        state.lastUseTick = now;

        BuffMobsMod.LOGGER.debug("[BuffMobs] CombatDraft: {} ({}) used draft [{}/{}]",
                mob.getType().getDescriptionId(),
                isUndead ? "undead" : "living",
                state.useCount,
                BuffMobsConfig.INSTANCE.combatDraft.maxUses.get() == 0
                        ? "∞" : BuffMobsConfig.INSTANCE.combatDraft.maxUses.get());
    }

    // Spawns happy villager particles to visually signal the heal
    private static void spawnDrinkParticles(Mob mob) {
        if (!(mob.level() instanceof ServerLevel sl)) return;
        for (int i = 0; i < 8; i++) {
            double ox = (mob.level().random.nextDouble() - 0.5) * mob.getBbWidth();
            double oy = mob.level().random.nextDouble() * mob.getBbHeight();
            double oz = (mob.level().random.nextDouble() - 0.5) * mob.getBbWidth();
            sl.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    mob.getX() + ox, mob.getY() + oy, mob.getZ() + oz,
                    1, 0, 0, 0, 0);
        }
    }

    private static boolean isEligible(Mob mob) {
        if (mob.isRemoved() || !mob.isAlive()) return false;
        if (mob instanceof TamableAnimal t && t.isTame()) return false;

        boolean hostile = mob instanceof Enemy
                || mob.getType().is(EntityTypeTags.RAIDERS)
                || mob.getType().is(EntityTypeTags.SKELETONS)
                || mob.getType().is(EntityTypeTags.ZOMBIES)
                || isNeutral(mob);
        if (!hostile) return false;

        String mobId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        BuffMobsConfig.CombatDraft cfg = BuffMobsConfig.INSTANCE.combatDraft;

        if (HARDCODED_BLACKLIST.contains(mobId)) return false;
        if (cfg.blacklist.get().contains(mobId)) return false;
        if (cfg.useWhitelist.get()) return cfg.whitelist.get().contains(mobId);
        return true;
    }

    private static boolean isNeutral(Mob mob) {
        String id = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        return switch (id) {
            case "minecraft:enderman", "minecraft:piglin", "minecraft:zombified_piglin",
                 "minecraft:iron_golem", "minecraft:spider", "minecraft:cave_spider",
                 "minecraft:wolf", "minecraft:polar_bear", "minecraft:bee",
                 "minecraft:panda", "minecraft:llama", "minecraft:dolphin",
                 "minecraft:trader_llama" -> true;
            default -> false;
        };
    }

    private static class MobDraftState {
        long lastUseTick      = -99999;
        int  useCount         = 0;
        boolean animationActive = false;
        long restoreAtTick    = 0;
        // pendingRestore removed — no fake potion item is placed in offhand
        ItemStack pendingRestore = null;
    }
}