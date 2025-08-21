package com.khotyz.buffmobs.effects;

import com.khotyz.buffmobs.BuffMobsMod;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BuffMobEffects {

    public static final StatusEffect ENHANCED_VITALITY;
    public static final StatusEffect COMBAT_FURY;
    public static final StatusEffect DESTRUCTIVE_POWER;

    static {
        ENHANCED_VITALITY = Registry.register(
                Registries.STATUS_EFFECT,
                Identifier.of(BuffMobsMod.MOD_ID, "enhanced_vitality"),
                new EnhancedVitalityEffect(StatusEffectCategory.BENEFICIAL, 0xFF4444)
        );

        COMBAT_FURY = Registry.register(
                Registries.STATUS_EFFECT,
                Identifier.of(BuffMobsMod.MOD_ID, "combat_fury"),
                new CombatFuryEffect(StatusEffectCategory.BENEFICIAL, 0xFF8800)
        );

        DESTRUCTIVE_POWER = Registry.register(
                Registries.STATUS_EFFECT,
                Identifier.of(BuffMobsMod.MOD_ID, "destructive_power"),
                new DestructivePowerEffect(StatusEffectCategory.BENEFICIAL, 0xCC0000)
        );
    }

    public static void initialize() {
        BuffMobsMod.LOGGER.info("Registered custom status effects");
    }
}