package com.khotyz.buffmobs.config;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public final class Suggestions {

    private static final int MAX_SUGGESTIONS = 8;

    private Suggestions() {
    }

    public static SuggestionProvider entityIds() {
        return Suggestions::matchingEntityIds;
    }

    public static SuggestionProvider dimensionIds() {
        return Suggestions::matchingDimensionIds;
    }

    public static SuggestionProvider modIds() {
        return Suggestions::matchingModIds;
    }

    public static List<String> matchingEntityIds(String query) {
        String needle = query.toLowerCase(Locale.ROOT);
        List<String> matches = new ArrayList<>();
        for (ResourceKey<?> key : BuiltInRegistries.ENTITY_TYPE.registryKeySet()) {
            String id = key.identifier().toString();
            if (id.toLowerCase(Locale.ROOT).contains(needle)) {
                matches.add(id);
                if (matches.size() >= MAX_SUGGESTIONS) break;
            }
        }
        Collections.sort(matches);
        return matches;
    }

    public static List<String> matchingModIds(String query) {
        String needle = query.toLowerCase(Locale.ROOT);
        List<String> matches = new ArrayList<>();
        for (String id : knownModIds()) {
            if (id.toLowerCase(Locale.ROOT).contains(needle)) {
                matches.add(id);
                if (matches.size() >= MAX_SUGGESTIONS) break;
            }
        }
        return matches;
    }

    public static List<String> matchingDimensionIds(String query) {
        String needle = query.toLowerCase(Locale.ROOT);
        List<String> matches = new ArrayList<>();
        for (String id : knownDimensionIds()) {
            if (id.toLowerCase(Locale.ROOT).contains(needle)) {
                matches.add(id);
                if (matches.size() >= MAX_SUGGESTIONS) break;
            }
        }
        return matches;
    }

    private static Set<String> knownModIds() {
        Set<String> ids = new TreeSet<>();
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            ids.add(mod.getMetadata().getId());
        }
        for (ResourceKey<?> key : BuiltInRegistries.ENTITY_TYPE.registryKeySet()) {
            ids.add(key.identifier().getNamespace());
        }
        return ids;
    }

    private static Set<String> knownDimensionIds() {
        Set<String> ids = new LinkedHashSet<>();
        ids.add(Level.OVERWORLD.identifier().toString());
        ids.add(Level.NETHER.identifier().toString());
        ids.add(Level.END.identifier().toString());

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            ids.add(minecraft.level.dimension().identifier().toString());
        }
        if (minecraft.getConnection() != null) {
            for (Object key : minecraft.getConnection().levels()) {
                ids.add(((ResourceKey<?>) key).identifier().toString());
            }
        }
        if (minecraft.getSingleplayerServer() != null) {
            for (ServerLevel level : minecraft.getSingleplayerServer().getAllLevels()) {
                ids.add(level.dimension().identifier().toString());
            }
        }
        return ids;
    }
}
