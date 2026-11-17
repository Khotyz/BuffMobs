package com.khotyz.buffmobs.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public final class DimensionUtil {

    private DimensionUtil() {
    }

    public static String getDimensionId(Level level) {
        return getDimensionId(level.dimension());
    }

    public static String getDimensionId(ResourceKey<Level> dimensionKey) {
        return dimensionKey.identifier().toString();
    }

    public static String toIdString(ResourceKey<?> key) {
        return key.identifier().toString();
    }
}
