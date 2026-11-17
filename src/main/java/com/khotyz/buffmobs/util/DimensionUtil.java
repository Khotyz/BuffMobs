package com.khotyz.buffmobs.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DimensionUtil {

    private static final Pattern ID_PATTERN = Pattern.compile("[a-z0-9_.-]+:[a-z0-9_.-]+");

    private DimensionUtil() {}

    public static String getDimensionId(Level level) {
        return getDimensionId(level.dimension());
    }

    public static String getDimensionId(ResourceKey<Level> key) {
        return parseKeyString(key == null ? null : key.toString());
    }

    public static String parseKeyString(String raw) {
        if (raw == null) return "";
        int separator = raw.indexOf(" / ");
        String candidate = separator >= 0 ? raw.substring(separator + 3) : raw;
        Matcher matcher = ID_PATTERN.matcher(candidate);
        String found = null;
        while (matcher.find()) found = matcher.group();
        return found != null ? found : candidate.replace("]", "").trim();
    }
}
