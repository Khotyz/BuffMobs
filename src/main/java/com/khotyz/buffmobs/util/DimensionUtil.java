package com.khotyz.buffmobs.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public final class DimensionUtil {

    private DimensionUtil() {}

    public static String getDimensionId(Level level) {
        ResourceKey<Level> key = level.dimension();
        String raw = key.toString();
        int sep = raw.indexOf(" / ");
        if (sep >= 0) {
            String id = raw.substring(sep + 3);
            if (id.endsWith("]")) id = id.substring(0, id.length() - 1);
            return id;
        }
        return raw;
    }
}
