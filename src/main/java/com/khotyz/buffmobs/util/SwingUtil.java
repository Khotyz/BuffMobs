package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class SwingUtil {

    private static final List<Method> SWING_METHODS = findSwingMethods();
    private static final AtomicBoolean FAILURE_LOGGED = new AtomicBoolean();

    private SwingUtil() {
    }

    public static void swing(LivingEntity entity, InteractionHand hand) {
        Throwable failure = null;
        for (Method method : SWING_METHODS) {
            try {
                if (method.getParameterCount() == 1) {
                    method.invoke(entity, hand);
                } else {
                    method.invoke(entity, hand, true);
                }
                return;
            } catch (ReflectiveOperationException | RuntimeException exception) {
                failure = exception;
            }
        }
        if (FAILURE_LOGGED.compareAndSet(false, true)) {
            String detail = failure == null ? "No compatible swing method found" : failure.getMessage();
            BuffMobsMod.LOGGER.warn("[BuffMobs] Swing animation unavailable: {}", detail);
        }
    }

    private static List<Method> findSwingMethods() {
        List<Method> methods = new ArrayList<>();
        for (Class<?> type = LivingEntity.class; type != null; type = type.getSuperclass()) {
            for (Method method : type.getDeclaredMethods()) {
                if (!method.getName().equals("swing")) continue;
                Class<?>[] parameters = method.getParameterTypes();
                if (!((parameters.length == 1 && parameters[0] == InteractionHand.class)
                        || (parameters.length == 2 && parameters[0] == InteractionHand.class
                        && parameters[1] == boolean.class))) continue;
                try {
                    method.setAccessible(true);
                    methods.add(method);
                } catch (RuntimeException ignored) {
                }
            }
        }
        methods.sort(Comparator.comparingInt(Method::getParameterCount));
        return List.copyOf(methods);
    }
}
