package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.effects.BuffMobEffects;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import java.util.WeakHashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class EffectManager {
    private static final Map<MobEntity, Set<String>> APPLIED_EFFECTS = new WeakHashMap<>();

    public static void applyCustomEffect(MobEntity mob, String effectType, int duration, int amplifier, boolean showParticles) {
        if (mob == null) return;

        try {
            Set<String> mobEffects = APPLIED_EFFECTS.computeIfAbsent(mob, k -> new HashSet<>());

            if (!mobEffects.contains(effectType)) {
                StatusEffectInstance effect = switch (effectType) {
                    case "health" -> new StatusEffectInstance(
                            Registries.STATUS_EFFECT.getEntry(BuffMobEffects.ENHANCED_VITALITY),
                            duration, amplifier, false, showParticles, showParticles
                    );
                    case "attack_speed" -> new StatusEffectInstance(
                            Registries.STATUS_EFFECT.getEntry(BuffMobEffects.COMBAT_FURY),
                            duration, amplifier, false, showParticles, showParticles
                    );
                    case "damage" -> new StatusEffectInstance(
                            Registries.STATUS_EFFECT.getEntry(BuffMobEffects.DESTRUCTIVE_POWER),
                            duration, amplifier, false, showParticles, showParticles
                    );
                    default -> null;
                };

                if (effect != null) {
                    mob.addStatusEffect(effect);
                    mobEffects.add(effectType);
                }
            }
        } catch (Exception e) {
            BuffMobsMod.LOGGER.error("Failed to apply custom effect: " + effectType, e);
        }
    }

    public static void applyVanillaEffect(MobEntity mob, String effectType, int duration, int amplifier, boolean showParticles) {
        if (mob == null) return;

        try {
            StatusEffectInstance effect = switch (effectType) {
                case "strength" -> new StatusEffectInstance(
                        StatusEffects.STRENGTH, duration, amplifier, false, showParticles, showParticles
                );
                case "speed" -> new StatusEffectInstance(
                        StatusEffects.SPEED, duration, amplifier, false, showParticles, showParticles
                );
                case "resistance" -> new StatusEffectInstance(
                        StatusEffects.RESISTANCE, duration, amplifier, false, showParticles, showParticles
                );
                case "regeneration" -> new StatusEffectInstance(
                        StatusEffects.REGENERATION, duration, amplifier, false, showParticles, showParticles
                );
                default -> null;
            };

            if (effect != null) {
                mob.addStatusEffect(effect);
            }
        } catch (Exception e) {
            BuffMobsMod.LOGGER.error("Failed to apply vanilla effect: " + effectType, e);
        }
    }

    public static void clearMobEffects(MobEntity mob) {
        if (mob == null) return;

        try {
            mob.removeStatusEffect(Registries.STATUS_EFFECT.getEntry(BuffMobEffects.ENHANCED_VITALITY));
            mob.removeStatusEffect(Registries.STATUS_EFFECT.getEntry(BuffMobEffects.COMBAT_FURY));
            mob.removeStatusEffect(Registries.STATUS_EFFECT.getEntry(BuffMobEffects.DESTRUCTIVE_POWER));
            APPLIED_EFFECTS.remove(mob);
        } catch (Exception e) {
            BuffMobsMod.LOGGER.error("Failed to clear mob effects", e);
        }
    }

    public static boolean hasAppliedEffect(MobEntity mob, String effectKey) {
        Set<String> mobEffects = APPLIED_EFFECTS.get(mob);
        return mobEffects != null && mobEffects.contains(effectKey);
    }

    public static void cleanup() {
        APPLIED_EFFECTS.clear();
    }
}