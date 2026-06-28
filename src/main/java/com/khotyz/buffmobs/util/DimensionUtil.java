package com.khotyz.buffmobs.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public final class DimensionUtil {

    private DimensionUtil() {}

    /**
     * Returns the dimension ID as a string like "minecraft:overworld".
     * ResourceKey.toString() returns "ResourceKey[minecraft:dimension / minecraft:overworld]"
     * so we extract the part after " / " and strip the trailing "]".
     */
    public static String getDimensionId(Level level) {
        ResourceKey<Level> key = level.dimension();
        String raw = key.toString(); // "ResourceKey[minecraft:dimension / minecraft:overworld]"
        int sep = raw.indexOf(" / ");
        if (sep >= 0) {
            String id = raw.substring(sep + 3);
            if (id.endsWith("]")) id = id.substring(0, id.length() - 1);
            return id;
        }
        // Fallback: use the full toString (should never happen)
        return raw;
    }
}
