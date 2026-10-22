package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import net.minecraft.world.entity.Entity;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

public final class EntityCompat {

    private static final String[] MOTION_SYNC_FIELDS = {"hurtMarked", "syncVelocity"};
    private static final Field MOTION_SYNC_FIELD = findMotionSyncField();
    private static final AtomicBoolean FAILURE_LOGGED = new AtomicBoolean();

    private EntityCompat() {
    }

    public static void markHurt(Entity entity) {
        if (entity == null) return;
        if (MOTION_SYNC_FIELD == null) {
            logOnce("no motion sync field is available");
            return;
        }
        try {
            MOTION_SYNC_FIELD.setBoolean(entity, true);
        } catch (ReflectiveOperationException | RuntimeException exception) {
            logOnce(String.valueOf(exception.getMessage()));
        }
    }

    private static void logOnce(String detail) {
        if (FAILURE_LOGGED.compareAndSet(false, true)) {
            BuffMobsMod.LOGGER.warn("[BuffMobs] Motion synchronization unavailable: {}", detail);
        }
    }

    private static Field findMotionSyncField() {
        for (Class<?> type = Entity.class; type != null; type = type.getSuperclass()) {
            for (String name : MOTION_SYNC_FIELDS) {
                try {
                    Field field = type.getDeclaredField(name);
                    if (field.getType() != boolean.class) continue;
                    field.setAccessible(true);
                    return field;
                } catch (ReflectiveOperationException | RuntimeException ignored) {
                }
            }
        }
        return null;
    }
}
