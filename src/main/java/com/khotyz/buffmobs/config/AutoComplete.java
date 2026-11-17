package com.khotyz.buffmobs.config;

import com.khotyz.buffmobs.util.DimensionUtil;
import com.khotyz.buffmobs.util.MobPresetUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class AutoComplete {

    private static final List<String> ABILITY_IDS = List.of("deathrattle", "laststand", "teleport", "summon");
    private static final List<String> DEFAULT_DIMENSIONS = List.of("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end");

    private static List<String> cachedEntityIds;
    private static List<String> cachedModIds;

    private AutoComplete() {
    }

    public static List<String> suggestions(SuggestionKind kind, String text, int limit) {
        if (kind == null || !kind.isEnabled()) return List.of();
        String input = text == null ? "" : text.trim();
        return switch (kind) {
            case ENTITY_ID -> complete(input, entityIds(), limit, true);
            case MOD_ID -> complete(input, modIds(), limit, true);
            case DIMENSION_ID -> complete(input, dimensionIds(), limit, true);
            case ENTITY_PRESET_MAPPING -> completeMapping(input, presetNames(), limit);
            case ENTITY_ABILITY_MAPPING -> completeMapping(input, ABILITY_IDS, limit);
            case NONE -> List.of();
        };
    }

    private static List<String> complete(String input, List<String> candidates, int limit, boolean suggestWhenEmpty) {
        if (limit <= 0 || (input.isEmpty() && !suggestWhenEmpty)) return List.of();
        String query = input.toLowerCase(Locale.ROOT);
        List<String> matches = new ArrayList<>();
        for (String candidate : candidates) {
            if (matches.size() >= limit) return matches;
            if (candidate.equalsIgnoreCase(input)) continue;
            if (candidate.toLowerCase(Locale.ROOT).startsWith(query)) matches.add(candidate);
        }
        for (String candidate : candidates) {
            if (matches.size() >= limit) break;
            if (candidate.equalsIgnoreCase(input) || matches.contains(candidate)) continue;
            if (candidate.toLowerCase(Locale.ROOT).contains(query)) matches.add(candidate);
        }
        return matches;
    }

    private static List<String> completeMapping(String input, List<String> suffixes, int limit) {
        if (input.isEmpty()) return List.of();
        String[] parts = input.split(":", -1);
        String entityPart = parts.length >= 2 ? parts[0] + ":" + parts[1] : parts[0];
        String suffixPart = parts.length >= 3 ? parts[2] : "";
        if (parts.length >= 3 || isKnownEntity(entityPart)) {
            List<String> matches = new ArrayList<>();
            for (String suffix : complete(suffixPart, suffixes, limit, true)) matches.add(entityPart + ":" + suffix);
            if (!matches.isEmpty()) return matches;
        }
        List<String> matches = new ArrayList<>();
        for (String entityId : complete(entityPart, entityIds(), limit, false)) matches.add(entityId + ":" + suffixPart);
        return matches;
    }

    private static boolean isKnownEntity(String entityId) {
        Identifier identifier = Identifier.tryParse(entityId);
        return identifier != null && BuiltInRegistries.ENTITY_TYPE.getOptional(identifier).isPresent();
    }

    private static List<String> entityIds() {
        List<String> ids = cachedEntityIds;
        if (ids == null) {
            ids = sortedNames(BuiltInRegistries.ENTITY_TYPE.keySet());
            cachedEntityIds = ids;
        }
        return ids;
    }

    private static List<String> modIds() {
        List<String> ids = cachedModIds;
        if (ids == null) {
            Set<String> namespaces = new LinkedHashSet<>();
            for (Identifier identifier : BuiltInRegistries.ENTITY_TYPE.keySet()) namespaces.add(identifier.getNamespace());
            ids = new ArrayList<>(namespaces);
            Collections.sort(ids);
            cachedModIds = ids;
        }
        return ids;
    }

    private static List<String> dimensionIds() {
        Set<String> ids = new LinkedHashSet<>(DEFAULT_DIMENSIONS);
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level != null) ids.add(DimensionUtil.getDimensionId(level));
        ClientPacketListener connection = minecraft.getConnection();
        if (connection != null) collectRegistryKeys(connection.registryAccess(), Registries.LEVEL_STEM, ids);
        List<String> result = new ArrayList<>(ids);
        Collections.sort(result);
        return result;
    }

    private static <T> void collectRegistryKeys(RegistryAccess registryAccess, ResourceKey<Registry<T>> registryKey, Set<String> target) {
        registryAccess.lookup(registryKey).ifPresent(lookup ->
                lookup.listElementIds().forEach(key -> target.add(DimensionUtil.toIdString(key))));
    }

    private static List<String> presetNames() {
        BuffMobsConfig.MobPresets.PresetSlot[] slots = {
                BuffMobsConfig.INSTANCE.mobPresets.preset1,
                BuffMobsConfig.INSTANCE.mobPresets.preset2,
                BuffMobsConfig.INSTANCE.mobPresets.preset3,
                BuffMobsConfig.INSTANCE.mobPresets.preset4,
                BuffMobsConfig.INSTANCE.mobPresets.preset5
        };
        List<String> names = new ArrayList<>();
        for (int index = 0; index < slots.length; index++) {
            String name = MobPresetUtil.effectivePresetName(slots[index], index);
            if (!name.isEmpty() && !names.contains(name)) names.add(name);
        }
        Collections.sort(names);
        return names;
    }

    private static List<String> sortedNames(Set<Identifier> identifiers) {
        List<String> names = new ArrayList<>(identifiers.size());
        for (Identifier identifier : identifiers) names.add(identifier.toString());
        Collections.sort(names);
        return names;
    }
}
