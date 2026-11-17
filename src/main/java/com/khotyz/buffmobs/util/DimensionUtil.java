package com.khotyz.buffmobs.util;

import net.minecraft.world.level.Level;

public final class DimensionUtil {

    private DimensionUtil() {
    }

    public static String getDimensionId(Level level) {
        return level.dimension().location().toString();
    }
}
