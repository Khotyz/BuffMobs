package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.SwingAnimation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public final class SwingUtil {

    private static final Map<Class<?>, List<Method>> SWING_METHODS = new ConcurrentHashMap<>();
    private static final AtomicBoolean FAILURE_LOGGED = new AtomicBoolean();

    private SwingUtil() {
    }

    public static void swing(LivingEntity entity, InteractionHand hand) {
        if (entity == null || hand == null) return;
        List<Method> candidates = SWING_METHODS.computeIfAbsent(entity.getClass(), SwingUtil::findSwingMethods);
        Throwable failure = null;
        for (Method method : candidates) {
            try {
                method.invoke(entity, argumentsFor(method, hand));
                return;
            } catch (ReflectiveOperationException | RuntimeException exception) {
                failure = exception;
            }
        }
        if (FAILURE_LOGGED.compareAndSet(false, true)) {
            String detail = failure == null ? "No compatible swing method found" : String.valueOf(failure.getMessage());
            BuffMobsMod.LOGGER.warn("[BuffMobs] Swing animation unavailable: {}", detail);
        }
    }

    private static Object[] argumentsFor(Method method, InteractionHand hand) {
        Class<?>[] parameters = method.getParameterTypes();
        Object[] arguments = new Object[parameters.length];
        arguments[0] = hand;
        for (int index = 1; index < parameters.length; index++) {
            arguments[index] = parameters[index] == boolean.class ? Boolean.FALSE : SwingAnimation.DEFAULT;
        }
        return arguments;
    }

    private static List<Method> findSwingMethods(Class<?> entityType) {
        List<Method> plain = new ArrayList<>();
        List<Method> flagged = new ArrayList<>();
        List<Method> animated = new ArrayList<>();
        List<Method> animatedFlagged = new ArrayList<>();
        for (Class<?> type = entityType; type != null && LivingEntity.class.isAssignableFrom(type); type = type.getSuperclass()) {
            for (Method method : type.getDeclaredMethods()) {
                if (!method.getName().equals("swing")) continue;
                Class<?>[] parameters = method.getParameterTypes();
                if (parameters.length < 1 || parameters.length > 3 || parameters[0] != InteractionHand.class) continue;
                if (!isSupportedTail(parameters)) continue;
                try {
                    method.setAccessible(true);
                } catch (RuntimeException ignored) {
                }
                if (parameters.length == 1) {
                    plain.add(method);
                } else if (parameters.length == 2 && parameters[1] == boolean.class) {
                    flagged.add(method);
                } else if (parameters.length == 2) {
                    animated.add(method);
                } else {
                    animatedFlagged.add(method);
                }
            }
        }
        List<Method> ordered = new ArrayList<>(plain);
        ordered.addAll(flagged);
        ordered.addAll(animated);
        ordered.addAll(animatedFlagged);
        return List.copyOf(ordered);
    }

    private static boolean isSupportedTail(Class<?>[] parameters) {
        for (int index = 1; index < parameters.length; index++) {
            if (parameters[index] != boolean.class && parameters[index] != SwingAnimation.class) return false;
        }
        return true;
    }
}
