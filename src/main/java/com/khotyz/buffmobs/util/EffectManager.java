package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import net.minecraft.entity.mob.MobEntity;
import java.util.WeakHashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class EffectManager {
    private static final Map<MobEntity, Set<String>> APPLIED_EFFECTS = new WeakHashMap<>();

    public static boolean hasAppliedEffect(MobEntity mob, String effectKey) {
        Set<String> mobEffects = APPLIED_EFFECTS.get(mob);
        return mobEffects != null && mobEffects.contains(effectKey);
    }

    public static void markEffectApplied(MobEntity mob, String effectKey) {
        Set<String> mobEffects = APPLIED_EFFECTS.computeIfAbsent(mob, k -> new HashSet<>());
        mobEffects.add(effectKey);
    }

    public static void clearMobEffects(MobEntity mob) {
        APPLIED_EFFECTS.remove(mob);
    }

    public static void cleanup() {
        APPLIED_EFFECTS.clear();
    }
}