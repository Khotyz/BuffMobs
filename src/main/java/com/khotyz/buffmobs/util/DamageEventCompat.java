package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.lang.reflect.Method;

public final class DamageEventCompat {

    private static final String[] DAMAGE_ACCESSORS = {"getHealthDamage", "getNewDamage", "getInflictedDamage", "getOriginalDamage"};
    private static final Method ACCESSOR = findAccessor();

    private DamageEventCompat() {
    }

    public static float damage(LivingDamageEvent.Post event) {
        if (event == null || ACCESSOR == null) return 0.0f;
        try {
            Object value = ACCESSOR.invoke(event);
            if (value instanceof Number number) return number.floatValue();
        } catch (Throwable throwable) {
            Guard.report("LivingDamageEvent damage accessor", throwable);
        }
        return 0.0f;
    }

    private static Method findAccessor() {
        for (String name : DAMAGE_ACCESSORS) {
            try {
                Method method = LivingDamageEvent.Post.class.getMethod(name);
                if (method.getParameterCount() != 0) continue;
                if (!isNumeric(method.getReturnType())) continue;
                BuffMobsMod.LOGGER.info("[BuffMobs] Reading post damage events through {}", name);
                return method;
            } catch (Throwable ignored) {
            }
        }
        BuffMobsMod.LOGGER.error("[BuffMobs] LivingDamageEvent.Post exposes no damage accessor, on hit trait effects receive zero damage");
        return null;
    }

    private static boolean isNumeric(Class<?> type) {
        return type == float.class || type == double.class || type == int.class || type == long.class
                || Number.class.isAssignableFrom(type);
    }
}
