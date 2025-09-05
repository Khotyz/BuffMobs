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

    public static void save() {
        saveConfig();
    }

    private static void loadConfig() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                config = GSON.fromJson(json, ConfigData.class);
                LOGGER.info("Config loaded successfully");
                saveConfig();
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
            String json = GSON.toJson(config);
            Files.writeString(CONFIG_PATH, json);
        } catch (IOException e) {
            LOGGER.error("Failed to save config", e);
        }
    }

    public enum NotificationMode {
        EVERY_DAY,
        SCALING_INCREASE_ONLY
    }

    public static class ConfigData {
        public GeneralSettings general = new GeneralSettings();
        public RangedEquipmentSettings rangedEquipment = new RangedEquipmentSettings();
        public DayScalingSettings dayScaling = new DayScalingSettings();
        public AttributeSettings attributes = new AttributeSettings();
        public EffectSettings effects = new EffectSettings();
        public HarmfulEffectSettings harmfulEffects = new HarmfulEffectSettings();
        public FilterSettings mobFilter = new FilterSettings();
        public FilterSettings modidFilter = new FilterSettings();
        public FilterSettings dimensionFilter = new FilterSettings();
        public DimensionScalingSettings dimensionScaling = new DimensionScalingSettings();

        public static class GeneralSettings {
            public boolean enabled = true;
            public boolean visualEffects = true;
        }

        public static class RangedEquipmentSettings {
            public boolean enabled = true;
            public double triggerDistance = 8.0;
            public double netheriteChance = 0.01;
            public double diamondChance = 0.05;
            public double ironChance = 0.15;
            public boolean enchantmentsEnabled = true;
            public double enchantmentChance = 0.25;
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
            // Removed speedAmplifier - handled by attributes only
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

            public FilterSettings() {
            }
        }

        public static class DimensionScalingSettings {
            public List<DimensionScaling> scalings = Arrays.asList();

            public static class DimensionScaling {
                public String dimensionName = "";
                public double healthMultiplier = 1.0;
                public double damageMultiplier = 1.0;
                public double speedMultiplier = 1.0;
                public double attackSpeedMultiplier = 1.0;
                public double armorAddition = 0.0;
                public double armorToughnessAddition = 0.0;

                public DimensionScaling() {
                }

                public DimensionScaling(String dimensionName) {
                    this.dimensionName = dimensionName;
                }
            }
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

    // Ranged Equipment getters
    public static boolean isRangedEquipmentEnabled() {
        return config.rangedEquipment.enabled;
    }

    public static double getRangedEquipmentDistance() {
        return Math.max(2.0, Math.min(32.0, config.rangedEquipment.triggerDistance));
    }

    public static double getRangedNetheriteChance() {
        return Math.max(0.001, Math.min(0.1, config.rangedEquipment.netheriteChance));
    }

    public static double getRangedDiamondChance() {
        return Math.max(0.01, Math.min(0.2, config.rangedEquipment.diamondChance));
    }

    public static double getRangedIronChance() {
        return Math.max(0.05, Math.min(0.4, config.rangedEquipment.ironChance));
    }

    public static boolean isRangedEnchantmentsEnabled() {
        return config.rangedEquipment.enchantmentsEnabled;
    }

    public static double getRangedEnchantmentChance() {
        return Math.max(0.0, Math.min(1.0, config.rangedEquipment.enchantmentChance));
    }

    public static boolean isMeleeSwitchEnabled() {
        return isRangedEquipmentEnabled();
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

    // Removed getSpeedAmplifier - using attributes only

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

    // Setters for Cloth Config
    public static void setEnabled(boolean enabled) {
        config.general.enabled = enabled;
    }

    public static void setVisualEffects(boolean visualEffects) {
        config.general.visualEffects = visualEffects;
    }

    public static void setRangedEquipmentEnabled(boolean enabled) {
        config.rangedEquipment.enabled = enabled;
    }

    public static void setRangedEquipmentDistance(double distance) {
        config.rangedEquipment.triggerDistance = Math.max(2.0, Math.min(32.0, distance));
    }

    public static void setRangedNetheriteChance(double chance) {
        config.rangedEquipment.netheriteChance = Math.max(0.001, Math.min(0.1, chance));
    }

    public static void setRangedDiamondChance(double chance) {
        config.rangedEquipment.diamondChance = Math.max(0.01, Math.min(0.2, chance));
    }

    public static void setRangedIronChance(double chance) {
        config.rangedEquipment.ironChance = Math.max(0.05, Math.min(0.4, chance));
    }

    public static void setRangedEnchantmentsEnabled(boolean enabled) {
        config.rangedEquipment.enchantmentsEnabled = enabled;
    }

    public static void setRangedEnchantmentChance(double chance) {
        config.rangedEquipment.enchantmentChance = Math.max(0.0, Math.min(1.0, chance));
    }

    public static void setMeleeSwitchEnabled(boolean enabled) {
        setRangedEquipmentEnabled(enabled);
    }

    public static void setDayScalingEnabled(boolean enabled) {
        config.dayScaling.enabled = enabled;
    }

    public static void setDayScalingInterval(int interval) {
        config.dayScaling.interval = Math.max(1, Math.min(365, interval));
    }

    public static void setDayScalingMultiplier(double multiplier) {
        config.dayScaling.multiplier = Math.max(0.01, Math.min(2.0, multiplier));
    }

    public static void setDayScalingMax(double max) {
        config.dayScaling.maxMultiplier = Math.max(1.0, Math.min(10.0, max));
    }

    public static void setShowDayScalingNotifications(boolean show) {
        config.dayScaling.showNotifications = show;
    }

    public static void setDayScalingNotificationMode(NotificationMode mode) {
        config.dayScaling.notificationMode = mode;
    }

    public static void setHealthMultiplier(double multiplier) {
        config.attributes.healthMultiplier = Math.max(1.0, Math.min(10.0, multiplier));
    }

    public static void setDamageMultiplier(double multiplier) {
        config.attributes.damageMultiplier = Math.max(1.0, Math.min(10.0, multiplier));
    }

    public static void setSpeedMultiplier(double multiplier) {
        config.attributes.speedMultiplier = Math.max(1.0, Math.min(5.0, multiplier));
    }

    public static void setAttackSpeedMultiplier(double multiplier) {
        config.attributes.attackSpeedMultiplier = Math.max(1.0, Math.min(10.0, multiplier));
    }

    public static void setOverrideAttackTimers(boolean override) {
        config.attributes.overrideAttackTimers = override;
    }

    public static void setArmorAddition(double armor) {
        config.attributes.armorAddition = Math.max(0.0, Math.min(20.0, armor));
    }

    public static void setArmorToughnessAddition(double toughness) {
        config.attributes.armorToughnessAddition = Math.max(0.0, Math.min(10.0, toughness));
    }

    public static void setEffectDuration(int duration) {
        config.effects.duration = Math.max(-1, Math.min(7200, duration));
    }

    public static void setStrengthAmplifier(int amplifier) {
        config.effects.strengthAmplifier = Math.max(0, Math.min(10, amplifier));
    }

    // Removed setSpeedAmplifier - using attributes only

    public static void setResistanceAmplifier(int amplifier) {
        config.effects.resistanceAmplifier = Math.max(0, Math.min(10, amplifier));
    }

    public static void setRegenerationAmplifier(int amplifier) {
        config.effects.regenerationAmplifier = Math.max(0, Math.min(10, amplifier));
    }

    public static void setApplyHarmfulEffects(boolean apply) {
        config.harmfulEffects.enabled = apply;
    }

    public static void setHarmfulEffectChance(double chance) {
        config.harmfulEffects.chance = Math.max(0.0, Math.min(1.0, chance));
    }

    public static void setPoisonDuration(int duration) {
        config.harmfulEffects.poisonDuration = Math.max(1, Math.min(60, duration));
    }

    public static void setSlownessDuration(int duration) {
        config.harmfulEffects.slownessDuration = Math.max(1, Math.min(60, duration));
    }

    public static void setWitherDuration(int duration) {
        config.harmfulEffects.witherDuration = Math.max(1, Math.min(60, duration));
    }

    public static void setUseMobWhitelist(boolean use) {
        config.mobFilter.useWhitelist = use;
    }

    public static void setMobWhitelist(List<String> whitelist) {
        config.mobFilter.whitelist = whitelist;
    }

    public static void setMobBlacklist(List<String> blacklist) {
        config.mobFilter.blacklist = blacklist;
    }

    public static void setUseModidWhitelist(boolean use) {
        config.modidFilter.useWhitelist = use;
    }

    public static void setModidWhitelist(List<String> whitelist) {
        config.modidFilter.whitelist = whitelist;
    }

    public static void setModidBlacklist(List<String> blacklist) {
        config.modidFilter.blacklist = blacklist;
    }

    public static void setUseDimensionWhitelist(boolean use) {
        config.dimensionFilter.useWhitelist = use;
    }

    public static void setDimensionWhitelist(List<String> whitelist) {
        config.dimensionFilter.whitelist = whitelist;
    }

    public static void setDimensionBlacklist(List<String> blacklist) {
        config.dimensionFilter.blacklist = blacklist;
    }

    // Dimension Scaling getters and setters
    public static List<ConfigData.DimensionScalingSettings.DimensionScaling> getDimensionScalings() {
        return config.dimensionScaling.scalings;
    }

    public static void setDimensionScalings(List<ConfigData.DimensionScalingSettings.DimensionScaling> scalings) {
        config.dimensionScaling.scalings = scalings;
    }

    public static ConfigData.DimensionScalingSettings.DimensionScaling getDimensionScaling(String dimensionName) {
        return config.dimensionScaling.scalings.stream()
                .filter(scaling -> scaling.dimensionName.equals(dimensionName))
                .findFirst()
                .orElse(null);
    }
}