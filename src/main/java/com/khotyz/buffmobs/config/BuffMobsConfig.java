package com.khotyz.buffmobs.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class BuffMobsConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("BuffMobsConfig");
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("buffmobs.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static ConfigData config = new ConfigData();
    private static boolean initialized = false;

    public static void initialize() {
        loadConfig();
        initialized = true;
    }

    public static void reload() {
        if (initialized) {
            LOGGER.info("Reloading BuffMobs configuration...");
            loadConfig();
        }
    }

    private static void loadConfig() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                config = GSON.fromJson(json, ConfigData.class);
                LOGGER.info("Config loaded successfully");
                saveConfig(); // Ensure any new fields are added
            } catch (IOException | JsonSyntaxException e) {
                LOGGER.error("Failed to load config, using defaults", e);
                config = new ConfigData();
                saveConfig();
            }
        } else {
            LOGGER.info("Config file not found, creating default config");
            config = new ConfigData();
            saveConfig();
        }
    }

    private static void saveConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            String json = generateCommentedJson(config);
            Files.writeString(CONFIG_PATH, json);
        } catch (IOException e) {
            LOGGER.error("Failed to save config", e);
        }
    }

    public enum NotificationMode {
        EVERY_DAY,
        SCALING_INCREASE_ONLY
    }

    private static String generateCommentedJson(ConfigData data) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  // Enable/disable the entire mod\n");
        json.append("  \"enabled\": ").append(data.general.enabled).append(",\n");
        json.append("  // Show visual potion effects on buffed mobs\n");
        json.append("  \"visualEffects\": ").append(data.general.visualEffects).append(",\n\n");

        json.append("  \"dayScaling\": {\n");
        json.append("    // Enable scaling based on world days\n");
        json.append("    \"enabled\": ").append(data.dayScaling.enabled).append(",\n");
        json.append("    // Days interval for scaling increase\n");
        json.append("    // Default: 7, Range: 1 ~ 365\n");
        json.append("    \"interval\": ").append(data.dayScaling.interval).append(",\n");
        json.append("    // Multiplier increase per interval\n");
        json.append("    // Default: 0.1, Range: 0.01 ~ 2.0\n");
        json.append("    \"multiplier\": ").append(data.dayScaling.multiplier).append(",\n");
        json.append("    // Maximum scaling multiplier\n");
        json.append("    // Default: 5.0, Range: 1.0 ~ 10.0\n");
        json.append("    \"maxMultiplier\": ").append(data.dayScaling.maxMultiplier).append(",\n");
        json.append("    // Show day scaling notifications in chat\n");
        json.append("    \"showNotifications\": ").append(data.dayScaling.showNotifications).append(",\n");
        json.append("    // When to show day scaling notifications: EVERY_DAY, SCALING_INCREASE_ONLY\n");
        json.append("    \"notificationMode\": \"").append(data.dayScaling.notificationMode).append("\"\n");
        json.append("  },\n\n");

        json.append("  \"attributes\": {\n");
        json.append("    // Health multiplier (1.0 = default/disabled)\n");
        json.append("    // Default: 1.0, Range: 1.0 ~ 10.0\n");
        json.append("    \"healthMultiplier\": ").append(data.attributes.healthMultiplier).append(",\n");
        json.append("    // Attack damage multiplier (1.0 = default/disabled)\n");
        json.append("    // Default: 1.0, Range: 1.0 ~ 10.0\n");
        json.append("    \"damageMultiplier\": ").append(data.attributes.damageMultiplier).append(",\n");
        json.append("    // Movement speed multiplier (1.0 = default/disabled)\n");
        json.append("    // Default: 1.0, Range: 1.0 ~ 5.0\n");
        json.append("    \"speedMultiplier\": ").append(data.attributes.speedMultiplier).append(",\n");
        json.append("    // Attack speed multiplier (1.0 = default/disabled)\n");
        json.append("    // Default: 1.0, Range: 1.0 ~ 10.0\n");
        json.append("    \"attackSpeedMultiplier\": ").append(data.attributes.attackSpeedMultiplier).append(",\n");
        json.append("    // Override internal attack timers to allow true attack speed scaling\n");
        json.append("    \"overrideAttackTimers\": ").append(data.attributes.overrideAttackTimers).append(",\n");
        json.append("    // Additional armor points (0 = disabled)\n");
        json.append("    // Default: 0.0, Range: 0.0 ~ 20.0\n");
        json.append("    \"armorAddition\": ").append(data.attributes.armorAddition).append(",\n");
        json.append("    // Additional armor toughness (0 = disabled)\n");
        json.append("    // Default: 0.0, Range: 0.0 ~ 10.0\n");
        json.append("    \"armorToughnessAddition\": ").append(data.attributes.armorToughnessAddition).append("\n");
        json.append("  },\n\n");

        json.append("  \"effects\": {\n");
        json.append("    // Effect duration in seconds (-1 for infinite)\n");
        json.append("    // Default: -1, Range: -1 ~ 7200\n");
        json.append("    \"duration\": ").append(data.effects.duration).append(",\n");
        json.append("    // Strength effect amplifier (0 = disabled)\n");
        json.append("    // Default: 0, Range: 0 ~ 10\n");
        json.append("    \"strengthAmplifier\": ").append(data.effects.strengthAmplifier).append(",\n");
        json.append("    // Speed effect amplifier (0 = disabled)\n");
        json.append("    // Default: 0, Range: 0 ~ 10\n");
        json.append("    \"speedAmplifier\": ").append(data.effects.speedAmplifier).append(",\n");
        json.append("    // Resistance effect amplifier (0 = disabled)\n");
        json.append("    // Default: 0, Range: 0 ~ 10\n");
        json.append("    \"resistanceAmplifier\": ").append(data.effects.resistanceAmplifier).append(",\n");
        json.append("    // Regeneration effect amplifier (0 = disabled)\n");
        json.append("    // Default: 0, Range: 0 ~ 10\n");
        json.append("    \"regenerationAmplifier\": ").append(data.effects.regenerationAmplifier).append("\n");
        json.append("  },\n\n");

        json.append("  \"harmfulEffects\": {\n");
        json.append("    // Mobs apply harmful effects to players on attack\n");
        json.append("    \"enabled\": ").append(data.harmfulEffects.enabled).append(",\n");
        json.append("    // Chance to apply harmful effects (0.0-1.0)\n");
        json.append("    // Default: 0.15, Range: 0.0 ~ 1.0\n");
        json.append("    \"chance\": ").append(data.harmfulEffects.chance).append(",\n");
        json.append("    // Poison effect duration in seconds\n");
        json.append("    // Default: 5, Range: 1 ~ 60\n");
        json.append("    \"poisonDuration\": ").append(data.harmfulEffects.poisonDuration).append(",\n");
        json.append("    // Slowness effect duration in seconds\n");
        json.append("    // Default: 3, Range: 1 ~ 60\n");
        json.append("    \"slownessDuration\": ").append(data.harmfulEffects.slownessDuration).append(",\n");
        json.append("    // Wither effect duration in seconds\n");
        json.append("    // Default: 3, Range: 1 ~ 60\n");
        json.append("    \"witherDuration\": ").append(data.harmfulEffects.witherDuration).append("\n");
        json.append("  },\n\n");

        json.append("  \"mobFilter\": {\n");
        json.append("    // Use whitelist mode (only listed mobs affected)\n");
        json.append("    \"useWhitelist\": ").append(data.mobFilter.useWhitelist).append(",\n");
        json.append("    // Mob whitelist (mob registry names)\n");
        json.append("    \"whitelist\": ").append(GSON.toJson(data.mobFilter.whitelist)).append(",\n");
        json.append("    // Mob blacklist (mob registry names)\n");
        json.append("    \"blacklist\": ").append(GSON.toJson(data.mobFilter.blacklist)).append("\n");
        json.append("  },\n\n");

        json.append("  \"modidFilter\": {\n");
        json.append("    // Use modid whitelist mode (only mobs from listed mods affected)\n");
        json.append("    \"useWhitelist\": ").append(data.modidFilter.useWhitelist).append(",\n");
        json.append("    // ModID whitelist (only mobs from these mods will be affected)\n");
        json.append("    \"whitelist\": ").append(GSON.toJson(data.modidFilter.whitelist)).append(",\n");
        json.append("    // ModID blacklist (mobs from these mods will not be affected)\n");
        json.append("    \"blacklist\": ").append(GSON.toJson(data.modidFilter.blacklist)).append("\n");
        json.append("  },\n\n");

        json.append("  \"dimensionFilter\": {\n");
        json.append("    // Use dimension whitelist mode (only listed dimensions affected)\n");
        json.append("    \"useWhitelist\": ").append(data.dimensionFilter.useWhitelist).append(",\n");
        json.append("    // Dimension whitelist (only these dimensions will be affected)\n");
        json.append("    \"whitelist\": ").append(GSON.toJson(data.dimensionFilter.whitelist)).append(",\n");
        json.append("    // Dimension blacklist (these dimensions will not be affected)\n");
        json.append("    \"blacklist\": ").append(GSON.toJson(data.dimensionFilter.blacklist)).append("\n");
        json.append("  }\n");
        json.append("}");

        return json.toString();
    }

    public static class ConfigData {
        public GeneralSettings general = new GeneralSettings();
        public DayScalingSettings dayScaling = new DayScalingSettings();
        public AttributeSettings attributes = new AttributeSettings();
        public EffectSettings effects = new EffectSettings();
        public HarmfulEffectSettings harmfulEffects = new HarmfulEffectSettings();
        public FilterSettings mobFilter = new FilterSettings();
        public FilterSettings modidFilter = new FilterSettings();
        public FilterSettings dimensionFilter = new FilterSettings();

        public static class GeneralSettings {
            public boolean enabled = true;
            public boolean visualEffects = true;
        }

        public static class DayScalingSettings {
            public boolean enabled = false;
            public int interval = 7;
            public double multiplier = 0.1;
            public double maxMultiplier = 5.0;
            public boolean showNotifications = true;
            public NotificationMode notificationMode = NotificationMode.EVERY_DAY;
        }

        public static class AttributeSettings {
            public double healthMultiplier = 1.0;
            public double damageMultiplier = 1.0;
            public double speedMultiplier = 1.0;
            public double attackSpeedMultiplier = 1.0;
            public boolean overrideAttackTimers = false;
            public double armorAddition = 0.0;
            public double armorToughnessAddition = 0.0;
        }

        public static class EffectSettings {
            public int duration = -1;
            public int strengthAmplifier = 0;
            public int speedAmplifier = 0;
            public int resistanceAmplifier = 0;
            public int regenerationAmplifier = 0;
        }

        public static class HarmfulEffectSettings {
            public boolean enabled = true;
            public double chance = 0.15;
            public int poisonDuration = 5;
            public int slownessDuration = 3;
            public int witherDuration = 3;
        }

        public static class FilterSettings {
            public boolean useWhitelist = false;
            public List<String> whitelist = Arrays.asList();
            public List<String> blacklist = Arrays.asList();

            public FilterSettings() {}
        }

        public ConfigData() {
            modidFilter.whitelist = Arrays.asList("minecraft");
            modidFilter.blacklist = Arrays.asList();

            dimensionFilter.whitelist = Arrays.asList("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end");
            dimensionFilter.blacklist = Arrays.asList();

            mobFilter.blacklist = Arrays.asList("minecraft:warden");
        }
    }

    // Getters with validation
    public static boolean isEnabled() {
        return config.general.enabled;
    }

    public static boolean showVisualEffects() {
        return config.general.visualEffects;
    }

    public static boolean isDayScalingEnabled() {
        return config.dayScaling.enabled;
    }

    public static int getDayScalingInterval() {
        return Math.max(1, config.dayScaling.interval);
    }

    public static double getDayScalingMultiplier() {
        return Math.max(0.0, config.dayScaling.multiplier);
    }

    public static double getDayScalingMax() {
        return Math.max(1.0, config.dayScaling.maxMultiplier);
    }

    public static boolean showDayScalingNotifications() {
        return config.dayScaling.showNotifications;
    }

    public static NotificationMode getDayScalingNotificationMode() {
        return config.dayScaling.notificationMode;
    }

    public static double getHealthMultiplier() {
        return Math.max(0.1, config.attributes.healthMultiplier);
    }

    public static double getDamageMultiplier() {
        return Math.max(0.1, config.attributes.damageMultiplier);
    }

    public static double getSpeedMultiplier() {
        return Math.max(0.1, config.attributes.speedMultiplier);
    }

    public static double getAttackSpeedMultiplier() {
        return Math.max(0.1, config.attributes.attackSpeedMultiplier);
    }

    public static boolean shouldOverrideAttackTimers() {
        return config.attributes.overrideAttackTimers;
    }

    public static double getArmorAddition() {
        return Math.max(0.0, config.attributes.armorAddition);
    }

    public static double getArmorToughnessAddition() {
        return Math.max(0.0, config.attributes.armorToughnessAddition);
    }

    public static int getEffectDuration() {
        return config.effects.duration;
    }

    public static int getStrengthAmplifier() {
        return Math.max(0, config.effects.strengthAmplifier);
    }

    public static int getSpeedAmplifier() {
        return Math.max(0, config.effects.speedAmplifier);
    }

    public static int getResistanceAmplifier() {
        return Math.max(0, config.effects.resistanceAmplifier);
    }

    public static int getRegenerationAmplifier() {
        return Math.max(0, config.effects.regenerationAmplifier);
    }

    public static boolean applyHarmfulEffects() {
        return config.harmfulEffects.enabled;
    }

    public static double getHarmfulEffectChance() {
        return Math.max(0.0, Math.min(1.0, config.harmfulEffects.chance));
    }

    public static int getPoisonDuration() {
        return Math.max(1, config.harmfulEffects.poisonDuration);
    }

    public static int getSlownessDuration() {
        return Math.max(1, config.harmfulEffects.slownessDuration);
    }

    public static int getWitherDuration() {
        return Math.max(1, config.harmfulEffects.witherDuration);
    }

    public static boolean useMobWhitelist() {
        return config.mobFilter.useWhitelist;
    }

    public static List<String> getMobWhitelist() {
        return config.mobFilter.whitelist;
    }

    public static List<String> getMobBlacklist() {
        return config.mobFilter.blacklist;
    }

    public static boolean useModidWhitelist() {
        return config.modidFilter.useWhitelist;
    }

    public static List<String> getModidWhitelist() {
        return config.modidFilter.whitelist;
    }

    public static List<String> getModidBlacklist() {
        return config.modidFilter.blacklist;
    }

    public static boolean useDimensionWhitelist() {
        return config.dimensionFilter.useWhitelist;
    }

    public static List<String> getDimensionWhitelist() {
        return config.dimensionFilter.whitelist;
    }

    public static List<String> getDimensionBlacklist() {
        return config.dimensionFilter.blacklist;
    }
}