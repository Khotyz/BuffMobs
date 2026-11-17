package com.khotyz.buffmobs.config;

import com.khotyz.buffmobs.util.DimensionUtil;
import com.khotyz.buffmobs.util.MobPresetUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class AutoComplete {

    private static final int MAX_SUGGESTIONS = 64;
    private static final List<String> BUILT_IN_DIMENSIONS = List.of(
            "minecraft:overworld",
            "minecraft:the_nether",
            "minecraft:the_end");
    private static final List<String> REGISTRY_LOOKUP_METHODS = List.of("lookupOrThrow", "registryOrThrow");
    private static final List<String> ABILITY_IDS = List.of("deathrattle", "laststand", "Teleport", "Summon");

    private static List<String> cachedEntityIds;
    private static List<String> cachedModIds;

    private AutoComplete() {
    }

    public static List<String> entitySuggestions(String currentValue) {
        return filter(entityIds(), currentValue);
    }

    public static List<String> modIdSuggestions(String currentValue) {
        return filter(modIds(), currentValue);
    }

    public static List<String> dimensionSuggestions(String currentValue) {
        return filter(dimensionIds(), currentValue);
    }

    public static List<String> presetMappingSuggestions(String currentValue) {
        return mappingSuggestions(currentValue, presetNames());
    }

    public static List<String> abilityMappingSuggestions(String currentValue) {
        return mappingSuggestions(currentValue, ABILITY_IDS);
    }

    private static List<String> mappingSuggestions(String currentValue, List<String> suffixes) {
        String value = currentValue == null ? "" : currentValue.trim();
        if (entityIds().contains(value)) {
            List<String> candidates = new ArrayList<>();
            for (String suffix : suffixes) candidates.add(value + ":" + suffix);
            return filter(candidates, value);
        }
        int lastSeparator = value.lastIndexOf(':');
        if (lastSeparator < 0) return entitySuggestions(value);

        String head = value.substring(0, lastSeparator);
        boolean mappingSection = countSeparators(value) >= 2 || entityIds().contains(head);
        if (!mappingSection) return entitySuggestions(value);

        List<String> candidates = new ArrayList<>();
        for (String suffix : suffixes) candidates.add(head + ":" + suffix);
        return filter(candidates, value);
    }

    public static List<String> entityIds() {
        if (cachedEntityIds == null) cachedEntityIds = collectEntityIds();
        return cachedEntityIds;
    }

    public static List<String> modIds() {
        if (cachedModIds == null) cachedModIds = collectModIds();
        return cachedModIds;
    }

    public static List<String> dimensionIds() {
        Set<String> ids = new LinkedHashSet<>(BUILT_IN_DIMENSIONS);
        addConfiguredDimensionIds(ids);
        addClientLevelDimensionId(ids);
        addConnectionDimensionIds(ids);
        addRegistryDimensionIds(ids);
        List<String> result = new ArrayList<>(ids);
        result.removeIf(String::isEmpty);
        result.sort(String::compareTo);
        return List.copyOf(result);
    }

    private static List<String> collectEntityIds() {
        List<String> ids = new ArrayList<>();
        for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
            Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
            if (id != null) ids.add(id.toString());
        }
        ids.sort(String::compareTo);
        return List.copyOf(ids);
    }

    private static List<String> collectModIds() {
        Set<String> ids = new LinkedHashSet<>();
        for (String entityId : entityIds()) {
            int separator = entityId.indexOf(':');
            if (separator > 0) ids.add(entityId.substring(0, separator));
        }
        List<String> result = new ArrayList<>(ids);
        result.sort(String::compareTo);
        return List.copyOf(result);
    }

    private static List<String> presetNames() {
        BuffMobsConfig.MobPresets presets = BuffMobsConfig.INSTANCE.mobPresets;
        BuffMobsConfig.MobPresets.PresetSlot[] slots = {
                presets.preset1, presets.preset2, presets.preset3, presets.preset4, presets.preset5
        };
        Set<String> names = new LinkedHashSet<>();
        for (int index = 0; index < slots.length; index++) {
            String name = MobPresetUtil.effectivePresetName(slots[index], index);
            if (name != null && !name.isEmpty()) names.add(name);
        }
        List<String> result = new ArrayList<>(names);
        result.sort(String::compareTo);
        return result;
    }

    private static List<String> filter(List<String> candidates, String currentValue) {
        String query = currentValue == null ? "" : currentValue.trim().toLowerCase(Locale.ROOT);
        if (query.isEmpty()) return candidates.size() > MAX_SUGGESTIONS
                ? List.copyOf(candidates.subList(0, MAX_SUGGESTIONS))
                : List.copyOf(candidates);

        List<String> prefixMatches = new ArrayList<>();
        List<String> partialMatches = new ArrayList<>();
        for (String candidate : candidates) {
            String lowerCase = candidate.toLowerCase(Locale.ROOT);
            if (lowerCase.startsWith(query)) {
                prefixMatches.add(candidate);
                if (prefixMatches.size() >= MAX_SUGGESTIONS) break;
            } else if (lowerCase.contains(query) && partialMatches.size() < MAX_SUGGESTIONS) {
                partialMatches.add(candidate);
            }
        }
        prefixMatches.addAll(partialMatches);
        if (prefixMatches.size() > MAX_SUGGESTIONS) return List.copyOf(prefixMatches.subList(0, MAX_SUGGESTIONS));
        return List.copyOf(prefixMatches);
    }

    private static int countSeparators(String value) {
        int total = 0;
        for (int index = 0; index < value.length(); index++) {
            if (value.charAt(index) == ':') total++;
        }
        return total;
    }

    private static void addConfiguredDimensionIds(Set<String> ids) {
        BuffMobsConfig config = BuffMobsConfig.INSTANCE;
        ids.addAll(config.dimensionFilter.whitelist);
        ids.addAll(config.dimensionFilter.blacklist);

        BuffMobsConfig.DimensionScaling.DimensionSlot[] scalingSlots = {
                config.dimensionScaling.slot1, config.dimensionScaling.slot2, config.dimensionScaling.slot3,
                config.dimensionScaling.slot4, config.dimensionScaling.slot5
        };
        for (BuffMobsConfig.DimensionScaling.DimensionSlot slot : scalingSlots) {
            if (slot.dimensionName != null) ids.add(slot.dimensionName.trim());
        }

        BuffMobsConfig.DimensionMaxHealth.DimensionMaxHealthSlot[] healthSlots = {
                config.dimensionMaxHealth.slot1, config.dimensionMaxHealth.slot2,
                config.dimensionMaxHealth.slot3, config.dimensionMaxHealth.slot4,
                config.dimensionMaxHealth.slot5
        };
        for (BuffMobsConfig.DimensionMaxHealth.DimensionMaxHealthSlot slot : healthSlots) {
            if (slot.dimensionId != null) ids.add(slot.dimensionId.trim());
        }
    }

    private static void addClientLevelDimensionId(Set<String> ids) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) return;
        Level level = minecraft.level;
        if (level != null) ids.add(DimensionUtil.getDimensionId(level));
    }

    private static void addConnectionDimensionIds(Set<String> ids) {
        Object connection = invokeNoArgs(Minecraft.getInstance(), "getConnection");
        addKeys(ids, invokeNoArgs(connection, "levels"));
    }

    private static void addRegistryDimensionIds(Set<String> ids) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.level == null) return;
        Object registryAccess = invokeNoArgs(minecraft.level, "registryAccess");
        if (registryAccess == null) return;
        Object registry = lookupDimensionRegistry(registryAccess);
        if (registry == null) return;
        addKeys(ids, invokeNoArgs(registry, "keySet"));
    }

    private static Object lookupDimensionRegistry(Object registryAccess) {
        for (String methodName : REGISTRY_LOOKUP_METHODS) {
            Object registry = invokeRegistryLookup(registryAccess, methodName);
            if (registry != null) return registry;
        }
        return null;
    }

    private static Object invokeRegistryLookup(Object registryAccess, String methodName) {
        try {
            Method method = registryAccess.getClass().getMethod(methodName, ResourceKey.class);
            method.setAccessible(true);
            return method.invoke(registryAccess, Registries.DIMENSION);
        } catch (ReflectiveOperationException | RuntimeException exception) {
            return null;
        }
    }

    private static Object invokeNoArgs(Object target, String methodName) {
        if (target == null) return null;
        try {
            Method method = target.getClass().getMethod(methodName);
            method.setAccessible(true);
            return method.invoke(target);
        } catch (ReflectiveOperationException | RuntimeException exception) {
            return null;
        }
    }

    private static void addKeys(Set<String> ids, Object keys) {
        if (!(keys instanceof Iterable<?> iterable)) return;
        for (Object key : iterable) {
            if (key == null) continue;
            String id = DimensionUtil.parseKeyString(key.toString());
            if (!id.isEmpty()) ids.add(id);
        }
    }
}
