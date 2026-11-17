package com.khotyz.buffmobs.config;

import com.khotyz.buffmobs.util.DimensionUtil;
import com.khotyz.buffmobs.util.Guard;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public final class IdSuggestions {

    private static final int MAX_MATCHES = 12;
    private static final long DIMENSION_CACHE_MILLIS = 5000L;
    private static final String[] VANILLA_DIMENSIONS = {
            "minecraft:overworld",
            "minecraft:the_nether",
            "minecraft:the_end"
    };

    private static List<String> entityIds;
    private static List<String> modIds;
    private static List<String> cachedDimensions = List.of();
    private static long dimensionCacheTime;

    private IdSuggestions() {
    }

    public static List<String> matches(SuggestionType type, String query) {
        if (type == null || type == SuggestionType.NONE) return List.of();
        String needle = entityPart(type, query).trim().toLowerCase(Locale.ROOT);
        List<String> pool = pool(type);
        List<String> found = new ArrayList<>();
        for (String candidate : pool) {
            if (candidate.toLowerCase(Locale.ROOT).startsWith(needle)) add(found, candidate);
            if (found.size() >= MAX_MATCHES) return found;
        }
        for (String candidate : pool) {
            if (found.contains(candidate)) continue;
            if (candidate.toLowerCase(Locale.ROOT).contains(needle)) add(found, candidate);
            if (found.size() >= MAX_MATCHES) return found;
        }
        return found;
    }

    public static String completion(SuggestionType type, String suggestion, String currentValue) {
        if (type != SuggestionType.ENTITY_PRESET && type != SuggestionType.ENTITY_ABILITY) return suggestion;
        String value = currentValue == null ? "" : currentValue;
        int firstSeparator = value.indexOf(':');
        int secondSeparator = firstSeparator < 0 ? -1 : value.indexOf(':', firstSeparator + 1);
        String suffix = secondSeparator < 0 ? ":" : value.substring(secondSeparator);
        return suggestion + suffix;
    }

    public static String hintKey(SuggestionType type) {
        if (type == null) return null;
        return switch (type) {
            case ENTITY -> "buffmobs.config.menu.entityIdHint";
            case ENTITY_PRESET -> "buffmobs.config.menu.presetMappingHint";
            case ENTITY_ABILITY -> "buffmobs.config.menu.abilityMappingHint";
            case DIMENSION -> "buffmobs.config.menu.dimensionIdHint";
            case MOD -> "buffmobs.config.menu.modIdHint";
            case NONE -> null;
        };
    }

    private static void add(List<String> found, String candidate) {
        if (found.size() < MAX_MATCHES) found.add(candidate);
    }

    private static String entityPart(SuggestionType type, String query) {
        String value = query == null ? "" : query;
        if (type != SuggestionType.ENTITY_PRESET && type != SuggestionType.ENTITY_ABILITY) return value;
        int firstSeparator = value.indexOf(':');
        if (firstSeparator < 0) return value;
        int secondSeparator = value.indexOf(':', firstSeparator + 1);
        return secondSeparator < 0 ? value : value.substring(0, secondSeparator);
    }

    private static List<String> pool(SuggestionType type) {
        return switch (type) {
            case ENTITY, ENTITY_PRESET, ENTITY_ABILITY -> entityIds();
            case DIMENSION -> dimensionIds();
            case MOD -> modIds();
            case NONE -> List.of();
        };
    }

    private static List<String> entityIds() {
        if (entityIds != null) return entityIds;
        Set<String> ids = new TreeSet<>();
        try {
            for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
                Identifier key = BuiltInRegistries.ENTITY_TYPE.getKey(type);
                if (key != null) ids.add(key.toString());
            }
        } catch (Throwable throwable) {
            Guard.report("BuffMobs entity suggestions", throwable);
        }
        entityIds = List.copyOf(ids);
        return entityIds;
    }

    private static List<String> modIds() {
        if (modIds != null) return modIds;
        Set<String> ids = new TreeSet<>();
        for (String entityId : entityIds()) {
            int separator = entityId.indexOf(':');
            if (separator > 0) ids.add(entityId.substring(0, separator));
        }
        modIds = List.copyOf(ids);
        return modIds;
    }

    private static List<String> dimensionIds() {
        long now = System.currentTimeMillis();
        if (!cachedDimensions.isEmpty() && now - dimensionCacheTime < DIMENSION_CACHE_MILLIS) return cachedDimensions;
        dimensionCacheTime = now;
        Set<String> ids = new TreeSet<>(List.of(VANILLA_DIMENSIONS));
        try {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft != null) {
                Object currentLevel = readMember(minecraft, "getLevel", "level");
                if (currentLevel instanceof Level level) addLevel(level, ids);
                collectConnectedLevels(minecraft, ids);
                collectServerLevels(minecraft, ids);
            }
        } catch (Throwable throwable) {
            Guard.report("BuffMobs dimension suggestions", throwable);
        }
        cachedDimensions = List.copyOf(ids);
        return cachedDimensions;
    }

    private static void collectConnectedLevels(Minecraft minecraft, Set<String> ids) {
        Object connection = readMember(minecraft, "getConnection", "connection");
        if (connection == null) return;
        for (Method method : connection.getClass().getMethods()) {
            if (method.getParameterCount() != 0) continue;
            if (method.getDeclaringClass() == Object.class) continue;
            Class<?> returnType = method.getReturnType();
            boolean mapType = Map.class.isAssignableFrom(returnType);
            boolean collectionType = Collection.class.isAssignableFrom(returnType);
            if (!mapType && !collectionType) continue;
            try {
                Object value = method.invoke(connection);
                if (value instanceof Map<?, ?> map) {
                    for (Object element : map.values()) addLevel(element, ids);
                } else if (value instanceof Collection<?> collection) {
                    for (Object element : collection) addLevel(element, ids);
                }
            } catch (ReflectiveOperationException | RuntimeException ignored) {
            }
        }
    }

    private static void collectServerLevels(Minecraft minecraft, Set<String> ids) {
        Object server = readMember(minecraft, "getSingleplayerServer", "singleplayerServer");
        if (server == null) return;
        for (Method method : server.getClass().getMethods()) {
            if (method.getParameterCount() != 0) continue;
            if (!method.getName().equals("getAllLevels")) continue;
            try {
                Object value = method.invoke(server);
                if (value instanceof Iterable<?> levels) {
                    for (Object element : levels) addLevel(element, ids);
                }
            } catch (ReflectiveOperationException | RuntimeException ignored) {
            }
        }
    }

    private static void addLevel(Object element, Set<String> ids) {
        if (!(element instanceof Level level)) return;
        try {
            ids.add(DimensionUtil.getDimensionId(level));
        } catch (RuntimeException ignored) {
        }
    }

    private static Object readMember(Object target, String getterName, String fieldName) {
        try {
            Method getter = target.getClass().getMethod(getterName);
            Object value = getter.invoke(target);
            if (value != null) return value;
        } catch (ReflectiveOperationException | RuntimeException ignored) {
        }
        try {
            Field field = target.getClass().getField(fieldName);
            return field.get(target);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
        }
        for (Class<?> type = target.getClass(); type != null; type = type.getSuperclass()) {
            try {
                Field field = type.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(target);
            } catch (ReflectiveOperationException | RuntimeException ignored) {
            }
        }
        return null;
    }
}
