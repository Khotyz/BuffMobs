package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
        STATES.remove(mob.getUUID());
    }

    public static void tickRestore(Mob mob) {
        MobDraftState state = STATES.get(mob.getUUID());
        if (state == null || state.pendingRestore == null) return;
        long now = mob.level().getGameTime();
        if (now >= state.restoreAtTick) {
            mob.stopUsingItem();
            mob.setItemSlot(EquipmentSlot.OFFHAND, state.pendingRestore);
            mob.setDropChance(EquipmentSlot.OFFHAND, 0.0f);
            state.pendingRestore = null;
        }
    }

    public static void tick(Mob mob) {
        if (!BuffMobsConfig.INSTANCE.combatDraft.enabled) return;

        MobDraftState state = STATES.get(mob.getUUID());
        if (state == null) return;

        if (!mob.isAlive() || mob.isRemoved()) return;

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
        boolean isUndead = mob.getType().builtInRegistryHolder().is(EntityTypeTags.UNDEAD);

        state.pendingRestore = mob.getOffhandItem().copy();
        state.restoreAtTick  = now + DRINK_ANIMATION_TICKS;

        mob.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.POTION));
        mob.setDropChance(EquipmentSlot.OFFHAND, 0.0f);

        mob.startUsingItem(net.minecraft.world.InteractionHand.OFF_HAND);

        mob.level().playSound(null,
                mob.getX(), mob.getY(), mob.getZ(),
                SoundEvents.GENERIC_DRINK,
                mob.getSoundSource(),
                1.0f, 0.9f + mob.level().getRandom().nextFloat() * 0.2f);

        if (isUndead) {
            int existingAbs = 0;
            MobEffectInstance cur = mob.getEffect(MobEffects.ABSORPTION);
            if (cur != null) existingAbs = cur.getAmplifier() + 1;
            int totalAmp = existingAbs + draftAmp;
            mob.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, duration, totalAmp - 1, false, true, true));
            mob.setAbsorptionAmount(mob.getAbsorptionAmount() + draftAmp * 4.0f);
            mob.heal(draftAmp * 4.0f);
        } else {
            int existingRegen = 0;
            MobEffectInstance cur = mob.getEffect(MobEffects.REGENERATION);
            if (cur != null) existingRegen = cur.getAmplifier() + 1;
            int totalAmp = existingRegen + draftAmp;
            mob.addEffect(new MobEffectInstance(MobEffects.REGENERATION, duration, totalAmp - 1, false, true, true));
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
        long      lastUseTick    = -99999;
        int       useCount       = 0;
        ItemStack pendingRestore = null;
        long      restoreAtTick  = 0;
    }
}