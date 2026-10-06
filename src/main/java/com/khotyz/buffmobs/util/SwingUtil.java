package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Method;

public final class SwingUtil {

    private static Method swingHandOnly = null;
    private static Method swingHandAndUpdate = null;

    static {
        for (Method m : LivingEntity.class.getMethods()) {
            if (!m.getName().equals("swing")) continue;
            Class<?>[] params = m.getParameterTypes();
            if (params.length == 1 && params[0] == InteractionHand.class) {
                m.setAccessible(true);
                swingHandOnly = m;
            } else if (params.length == 2 && params[0] == InteractionHand.class && params[1] == boolean.class) {
                m.setAccessible(true);
                swingHandAndUpdate = m;
            }
        }
        if (swingHandOnly == null && swingHandAndUpdate == null) {
            BuffMobsMod.LOGGER.warn("[BuffMobs] Could not find a swing method, swing animation disabled");
        }
    }

    private SwingUtil() {}

    public static void swing(LivingEntity entity, InteractionHand hand) {
        try {
            if (swingHandOnly != null) {
                swingHandOnly.invoke(entity, hand);
            } else if (swingHandAndUpdate != null) {
                swingHandAndUpdate.invoke(entity, hand, false);
            }
        } catch (Exception e) {
            BuffMobsMod.LOGGER.debug("[BuffMobs] Swing failed: {}", e.getMessage());
        }
    }
}