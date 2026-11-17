package com.khotyz.buffmobs.config;

import com.khotyz.buffmobs.util.MobPresetUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public final class ConfigSuggestions {

    private static final List<String> ABILITY_IDS = List.of("deathrattle", "laststand", "summon", "teleport");

    private static final SuggestionProvider ENTITIES = new SuggestionProvider() {
        @Override
        public List<String> suggest(String input) {
            return rankMatches(registeredEntityIds(), input);
        }

        @Override
        public Component hint() {
            return Component.translatable("buffmobs.config.menu.entityIdHint");
        }
    };

    private static final SuggestionProvider DIMENSIONS = new SuggestionProvider() {
        @Override
        public List<String> suggest(String input) {
            return rankMatches(registeredDimensionIds(), input);
        }

        @Override
        public Component hint() {
            return Component.translatable("buffmobs.config.menu.dimensionIdHint");
        }
    };

    private static final SuggestionProvider MOD_IDS = new SuggestionProvider() {
        @Override
        public List<String> suggest(String input) {
            return rankMatches(registeredModIds(), input);
        }

        @Override
        public Component hint() {
            return Component.translatable("buffmobs.config.menu.modIdHint");
        }
    };

    private static final SuggestionProvider PRESET_MAPPINGS = new SuggestionProvider() {
        @Override
        public List<String> suggest(String input) {
            return suggestPresetMapping(input);
        }

        @Override
        public Component hint() {
            return Component.translatable("buffmobs.config.menu.presetMappingHint");
        }
    };

    private static final SuggestionProvider MOB_ABILITY_MAPPINGS = new SuggestionProvider() {
        @Override
        public List<String> suggest(String input) {
            return suggestAbilityMapping(input);
        }

        @Override
        public Component hint() {
            return Component.translatable("buffmobs.config.menu.abilityMappingHint");
        }
    };

    private ConfigSuggestions() {
    }

    public static SuggestionProvider entities() {
        return ENTITIES;
    }

    public static SuggestionProvider dimensions() {
        return DIMENSIONS;
    }

    public static SuggestionProvider modIds() {
        return MOD_IDS;
    }

    public static SuggestionProvider presetMappings() {
        return PRESET_MAPPINGS;
    }

    public static SuggestionProvider mobAbilityMappings() {
        return MOB_ABILITY_MAPPINGS;
    }

    public static List<String> registeredEntityIds() {
        return BuiltInRegistries.ENTITY_TYPE.keySet().stream()
                .map(ResourceLocation::toString)
                .sorted()
                .toList();
    }

    public static List<String> registeredModIds() {
        Set<String> modIds = new TreeSet<>();
        for (ResourceLocation key : BuiltInRegistries.ENTITY_TYPE.keySet()) {
            modIds.add(key.getNamespace());
        }
        ModList modList = ModList.get();
        if (modList != null) {
            modList.getMods().forEach(modInfo -> modIds.add(modInfo.getModId()));
        }
        return List.copyOf(modIds);
    }

    public static List<String> registeredDimensionIds() {
        Set<String> ids = new TreeSet<>();
        ids.add(Level.OVERWORLD.location().toString());
        ids.add(Level.NETHER.location().toString());
        ids.add(Level.END.location().toString());

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft != null) {
            if (minecraft.level != null) {
                ids.add(minecraft.level.dimension().location().toString());
            }
            ClientPacketListener connection = minecraft.getConnection();
            if (connection != null) {
                for (ResourceKey<Level> key : connection.levels()) {
                    ids.add(key.location().toString());
                }
            }
            IntegratedServer server = minecraft.getSingleplayerServer();
            if (server != null) {
                for (ResourceKey<Level> key : server.levelKeys()) {
                    ids.add(key.location().toString());
                }
                server.registryAccess().registry(Registries.LEVEL_STEM).ifPresent(registry -> {
                    for (ResourceLocation key : registry.keySet()) {
                        ids.add(key.toString());
                    }
                });
            }
        }

        addConfiguredDimensions(ids);
        return List.copyOf(ids);
    }

    private static void addConfiguredDimensions(Set<String> ids) {
        BuffMobsConfig cfg = BuffMobsConfig.INSTANCE;
        addNonBlankEntries(ids, cfg.dimensionFilter.whitelist.get());
        addNonBlankEntries(ids, cfg.dimensionFilter.blacklist.get());

        BuffMobsConfig.DimensionScaling.DimensionSlot[] scalingSlots = {
                cfg.dimensionScaling.slot1, cfg.dimensionScaling.slot2, cfg.dimensionScaling.slot3,
                cfg.dimensionScaling.slot4, cfg.dimensionScaling.slot5
        };
        for (BuffMobsConfig.DimensionScaling.DimensionSlot slot : scalingSlots) {
            addNonBlank(ids, slot.dimensionName.get());
        }

        BuffMobsConfig.DimensionMaxHealth.DimensionHealthSlot[] healthSlots = {
                cfg.dimensionMaxHealth.slot1, cfg.dimensionMaxHealth.slot2, cfg.dimensionMaxHealth.slot3,
                cfg.dimensionMaxHealth.slot4, cfg.dimensionMaxHealth.slot5
        };
        for (BuffMobsConfig.DimensionMaxHealth.DimensionHealthSlot slot : healthSlots) {
            addNonBlank(ids, slot.dimensionName.get());
        }
    }

    private static void addNonBlankEntries(Set<String> target, List<? extends String> entries) {
        for (String entry : entries) {
            addNonBlank(target, entry);
        }
    }

    private static void addNonBlank(Set<String> target, String value) {
        if (value == null) return;
        String trimmed = value.trim();
        if (!trimmed.isEmpty()) target.add(trimmed);
    }

    private static List<String> suggestPresetMapping(String input) {
        String trimmed = input == null ? "" : input.trim();
        int firstColon = trimmed.indexOf(':');
        int secondColon = firstColon >= 0 ? trimmed.indexOf(':', firstColon + 1) : -1;
        if (secondColon < 0) {
            return rankMatches(registeredEntityIds(), trimmed);
        }

        String entityPart = trimmed.substring(0, secondColon);
        String presetQuery = trimmed.substring(secondColon + 1);
        List<String> presetNames = activePresetNames();
        List<String> matchedPresets = rankMatches(presetNames, presetQuery);
        List<String> results = new ArrayList<>(matchedPresets.size());
        for (String presetName : matchedPresets) {
            results.add(entityPart + ":" + presetName);
        }
        return results;
    }

    private static List<String> activePresetNames() {
        BuffMobsConfig.MobPresets presets = BuffMobsConfig.INSTANCE.mobPresets;
        BuffMobsConfig.MobPresets.PresetSlot[] slots = {
                presets.preset1, presets.preset2, presets.preset3, presets.preset4, presets.preset5
        };
        Set<String> names = new TreeSet<>();
        for (int index = 0; index < slots.length; index++) {
            String effective = MobPresetUtil.effectivePresetName(slots[index], index);
            if (!effective.isEmpty()) names.add(effective);
        }
        return List.copyOf(names);
    }

    private static List<String> suggestAbilityMapping(String input) {
        String trimmed = input == null ? "" : input.trim();
        int firstColon = trimmed.indexOf(':');
        int secondColon = firstColon >= 0 ? trimmed.indexOf(':', firstColon + 1) : -1;
        if (secondColon < 0) {
            return rankMatches(registeredEntityIds(), trimmed);
        }

        String entityPart = trimmed.substring(0, secondColon);
        String abilitySection = trimmed.substring(secondColon + 1);
        int lastComma = abilitySection.lastIndexOf(',');
        String existingSection = lastComma >= 0 ? abilitySection.substring(0, lastComma) : "";
        String abilityQuery = lastComma >= 0 ? abilitySection.substring(lastComma + 1) : abilitySection;

        Set<String> usedAbilities = new HashSet<>();
        if (!existingSection.isEmpty()) {
            for (String token : existingSection.split(",")) {
                String normalized = token.trim().toLowerCase(Locale.ROOT);
                if (!normalized.isEmpty()) usedAbilities.add(normalized);
            }
        }

        List<String> remaining = new ArrayList<>();
        for (String abilityId : ABILITY_IDS) {
            if (!usedAbilities.contains(abilityId)) remaining.add(abilityId);
        }

        String prefix = lastComma >= 0
                ? entityPart + ":" + existingSection + ","
                : entityPart + ":";
        List<String> matched = rankMatches(remaining, abilityQuery);
        List<String> results = new ArrayList<>(matched.size());
        for (String ability : matched) {
            results.add(prefix + ability);
        }
        return results;
    }

    public static List<String> rankMatches(List<String> candidates, String rawQuery) {
        String query = rawQuery == null ? "" : rawQuery.trim().toLowerCase(Locale.ROOT);
        if (query.isEmpty()) {
            return List.copyOf(candidates);
        }

        List<String> exactMatches = new ArrayList<>();
        List<String> prefixMatches = new ArrayList<>();
        List<String> pathPrefixMatches = new ArrayList<>();
        List<String> containsMatches = new ArrayList<>();

        for (String candidate : candidates) {
            String lower = candidate.toLowerCase(Locale.ROOT);
            int colon = lower.indexOf(':');
            String path = colon >= 0 ? lower.substring(colon + 1) : lower;

            if (lower.equals(query) || path.equals(query)) {
                exactMatches.add(candidate);
            } else if (lower.startsWith(query)) {
                prefixMatches.add(candidate);
            } else if (path.startsWith(query)) {
                pathPrefixMatches.add(candidate);
            } else if (lower.contains(query)) {
                containsMatches.add(candidate);
            }
        }

        exactMatches.sort(Comparator.naturalOrder());
        prefixMatches.sort(Comparator.naturalOrder());
        pathPrefixMatches.sort(Comparator.naturalOrder());
        containsMatches.sort(Comparator.naturalOrder());

        List<String> results = new ArrayList<>(
                exactMatches.size() + prefixMatches.size() + pathPrefixMatches.size() + containsMatches.size());
        results.addAll(exactMatches);
        results.addAll(prefixMatches);
        results.addAll(pathPrefixMatches);
        results.addAll(containsMatches);
        return results;
    }
}
