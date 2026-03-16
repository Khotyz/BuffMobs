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
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CombatDraftHandler {

    // Duração da animação de beber do Minecraft vanilla (Items do tipo DRINK = 32 ticks)
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

    public static void tick(Mob mob) {
        if (!BuffMobsConfig.INSTANCE.combatDraft.enabled.get()) return;
        if (!isEligible(mob)) return;

        MobDraftState state = STATES.computeIfAbsent(mob.getUUID(), k -> new MobDraftState());

        int maxUses = BuffMobsConfig.INSTANCE.combatDraft.maxUses.get();
        if (maxUses > 0 && state.useCount >= maxUses) return;

        long now = mob.level().getGameTime();

        // Processar restauração da offhand quando a animação terminar
        if (state.pendingRestore != null && now >= state.restoreAtTick) {
            mob.stopUsingItem();
            mob.setItemSlot(EquipmentSlot.OFFHAND, state.pendingRestore);
            state.pendingRestore = null;
        }

        if (now - state.lastUseTick < BuffMobsConfig.INSTANCE.combatDraft.cooldownTicks.get()) return;
        if (mob.getHealth() > mob.getMaxHealth() * BuffMobsConfig.INSTANCE.combatDraft.healthThreshold.get()) return;

        useDraft(mob, state, now);
    }

    private static void useDraft(Mob mob, MobDraftState state, long now) {
        int amp      = BuffMobsConfig.INSTANCE.combatDraft.regenAmplifier.get() - 1;
        int duration = BuffMobsConfig.INSTANCE.combatDraft.regenDuration.get() * 20;
        boolean isUndead = mob.getType().is(EntityTypeTags.UNDEAD);

        // Guardar offhand atual e colocar a poção
        state.pendingRestore = mob.getOffhandItem().copy();
        state.restoreAtTick  = now + DRINK_ANIMATION_TICKS;
        mob.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.POTION));

        // Disparar animação nativa de beber (levanta o braço exatamente como o player)
        mob.startUsingItem(net.minecraft.world.InteractionHand.OFF_HAND);

        // Som de beber idêntico ao do player
        mob.level().playSound(null,
                mob.getX(), mob.getY(), mob.getZ(),
                SoundEvents.GENERIC_DRINK,
                mob.getSoundSource(),
                1.0f, 0.9f + mob.level().random.nextFloat() * 0.2f);

        // Efeito de cura: undead não se beneficia de Regeneration — usar Absorption + heal direto
        if (isUndead) {
            mob.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, duration, amp, false, true, true));
            // Cura imediata proporcional ao amplifier para complementar a absorção
            float healAmount = (amp + 1) * 4.0f;
            mob.heal(healAmount);
        } else {
            mob.addEffect(new MobEffectInstance(MobEffects.REGENERATION, duration, amp, false, true, true));
        }

        state.useCount++;
        state.lastUseTick = now;

        BuffMobsMod.LOGGER.debug("[BuffMobs] CombatDraft: {} ({}) used potion [{}/{}]",
                mob.getType().getDescriptionId(),
                isUndead ? "undead" : "living",
                state.useCount,
                BuffMobsConfig.INSTANCE.combatDraft.maxUses.get() == 0
                        ? "∞" : BuffMobsConfig.INSTANCE.combatDraft.maxUses.get());
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
        long      lastUseTick    = -99999;
        int       useCount       = 0;
        ItemStack pendingRestore = null;
        long      restoreAtTick  = 0;
    }
}