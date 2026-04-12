package com.khotyz.buffmobs.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.khotyz.buffmobs.BuffMobsMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BuffMobsConfig {

    public static final BuffMobsConfig INSTANCE = new BuffMobsConfig();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("buffmobs.json");

    // ── General ──────────────────────────────────────────────────────────────
    public boolean enabled = true;
    public boolean visualEffects = true;

    // ── Sections ─────────────────────────────────────────────────────────────
    public DayScaling dayScaling = new DayScaling();
    public Attributes attributes = new Attributes();
    public Effects effects = new Effects();
    public HarmfulEffects harmfulEffects = new HarmfulEffects();
    public DimensionScaling dimensionScaling = new DimensionScaling();
    public MobFilter mobFilter = new MobFilter();
    public ModIdFilter modidFilter = new ModIdFilter();
    public DimensionFilter dimensionFilter = new DimensionFilter();
    public RangedMeleeSwitching rangedMeleeSwitching = new RangedMeleeSwitching();
    public CombatDraft combatDraft = new CombatDraft();
    public MobPresets mobPresets = new MobPresets();

    // ── Load / Save ───────────────────────────────────────────────────────────
    public static void load() {
        File file = CONFIG_PATH.toFile();
        if (file.exists()) {
            try (Reader r = new FileReader(file)) {
                BuffMobsConfig loaded = GSON.fromJson(r, BuffMobsConfig.class);
                if (loaded != null) copyFrom(loaded);
            } catch (Exception e) {
                BuffMobsMod.LOGGER.error("[BuffMobs] Failed to load config, using defaults", e);
            }
        }
        save();
    }

    public static void save() {
        try (Writer w = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(INSTANCE, w);
        } catch (Exception e) {
            BuffMobsMod.LOGGER.error("[BuffMobs] Failed to save config", e);
        }
    }

    private static void copyFrom(BuffMobsConfig src) {
        INSTANCE.enabled = src.enabled;
        INSTANCE.visualEffects = src.visualEffects;
        if (src.dayScaling != null) INSTANCE.dayScaling = src.dayScaling;
        if (src.attributes != null) INSTANCE.attributes = src.attributes;
        if (src.effects != null) INSTANCE.effects = src.effects;
        if (src.harmfulEffects != null) INSTANCE.harmfulEffects = src.harmfulEffects;
        if (src.dimensionScaling != null) INSTANCE.dimensionScaling = src.dimensionScaling;
        if (src.mobFilter != null) INSTANCE.mobFilter = src.mobFilter;
        if (src.modidFilter != null) INSTANCE.modidFilter = src.modidFilter;
        if (src.dimensionFilter != null) INSTANCE.dimensionFilter = src.dimensionFilter;
        if (src.rangedMeleeSwitching != null) INSTANCE.rangedMeleeSwitching = src.rangedMeleeSwitching;
        if (src.combatDraft != null) INSTANCE.combatDraft = src.combatDraft;
        if (src.mobPresets != null) INSTANCE.mobPresets = src.mobPresets;
    }

    // ── Inner classes ─────────────────────────────────────────────────────────

    public static class DayScaling {
        public boolean enabled = false;
        public int interval = 7;
        public double multiplier = 0.1;
        public double maxMultiplier = 5.0;
        public boolean showNotifications = true;
        public NotificationMode notificationMode = NotificationMode.EVERY_DAY;

        public enum NotificationMode { EVERY_DAY, SCALING_INCREASE_ONLY }
    }

    public static class Attributes {
        public double healthMultiplier = 1.5;
        public double damageMultiplier = 1.5;
        public double speedMultiplier = 1.0;
        public double attackSpeedMultiplier = 1.0;
        public double armorAddition = 5.0;
        public double armorToughnessAddition = 0.0;
    }

    public static class Effects {
        public int duration = -1;
        public int strengthAmplifier = 0;
        public int speedAmplifier = 0;
        public int resistanceAmplifier = 0;
        public int regenerationAmplifier = 0;
        public int absorptionAmplifier = 0;
    }

    public static class HarmfulEffects {
        public boolean enabled = true;
        public double chance = 0.15;
        public int poisonDuration = 5;
        public int slownessDuration = 3;
        public int witherDuration = 3;
    }

    public static class DimensionScaling {
        public DimensionSlot slot1 = new DimensionSlot();
        public DimensionSlot slot2 = new DimensionSlot();
        public DimensionSlot slot3 = new DimensionSlot();
        public DimensionSlot slot4 = new DimensionSlot();
        public DimensionSlot slot5 = new DimensionSlot();

        public static class DimensionSlot {
            public String dimensionName = "";
            public int healthMultiplier = 100;
            public int damageMultiplier = 100;
            public int speedMultiplier = 100;
            public int attackSpeedMultiplier = 100;
            public int armorAddition = 0;
            public int armorToughnessAddition = 0;
        }
    }

    public static class MobFilter {
        public boolean useWhitelist = false;
        public List<String> whitelist = new ArrayList<>();
        public List<String> blacklist = new ArrayList<>(List.of("minecraft:warden"));
    }

    public static class ModIdFilter {
        public boolean useWhitelist = false;
        public List<String> whitelist = new ArrayList<>();
        public List<String> blacklist = new ArrayList<>();
    }

    public static class DimensionFilter {
        public boolean useWhitelist = false;
        public List<String> whitelist = new ArrayList<>(
                List.of("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"));
        public List<String> blacklist = new ArrayList<>();
    }

    public static class RangedMeleeSwitching {
        public boolean enabled = true;
        public BehaviorMode behaviorMode = BehaviorMode.RANDOM;
        public double switchDistance = 4.0;
        public double meleeSpeedMultiplier = 0.9;
        public List<String> customRangedMobs = new ArrayList<>();

        public int stoneSwordUnlockDay = 0;
        public int ironSwordUnlockDay = 7;
        public int diamondSwordUnlockDay = 21;
        public int netheriteSwordUnlockDay = 60;
        public int goldenAxeUnlockDay = 0;
        public int diamondAxeUnlockDay = 14;
        public int netheriteAxeUnlockDay = 45;

        public boolean enchantmentsEnabled = true;
        public int maxEnchantmentsPerWeapon = 2;
        public int daysPerEnchantmentLevel = 7;
        public int sharpnessUnlockDay = 0;
        public int sharpnessMaxLevel = 5;
        public int fireAspectUnlockDay = 14;
        public int fireAspectMaxLevel = 2;
        public int knockbackUnlockDay = 7;
        public int knockbackMaxLevel = 2;
        public int sweepingEdgeUnlockDay = 21;
        public int sweepingEdgeMaxLevel = 3;

        public enum BehaviorMode { MELEE, KITE, RANDOM }
    }

    public static class CombatDraft {
        public boolean enabled = true;
        public double healthThreshold = 0.20;
        public int regenAmplifier = 4;
        public int regenDuration = 10;
        public int cooldownTicks = 600;
        public int maxUses = 0;
        public boolean useWhitelist = false;
        public List<String> whitelist = new ArrayList<>();
        public List<String> blacklist = new ArrayList<>();
    }

    public static class MobPresets {
        public boolean enabled = false;
        public PresetSlot preset1 = new PresetSlot("", 1.0, 1.0, 1.0, 1.0, 0.0, 0.0);
        public PresetSlot preset2 = new PresetSlot("",    3.0, 2.5, 1.2, 1.5, 10.0, 5.0);
        public PresetSlot preset3 = new PresetSlot("",   2.0, 1.8, 1.1, 1.2, 5.0,  2.0);
        public PresetSlot preset4 = new PresetSlot("",    0.5, 0.5, 0.9, 0.8, 0.0,  0.0);
        public PresetSlot preset5 = new PresetSlot("",        1.0, 1.0, 1.0, 1.0, 0.0,  0.0);
        public List<String> mobMapping = new ArrayList<>(List.of(
                "minecraft:zombie:default",
                "minecraft:skeleton:default",
                "minecraft:ender_dragon:boss",
                "minecraft:wither:boss"
        ));

        public static class PresetSlot {
            public String presetName;
            public double healthMultiplier;
            public double damageMultiplier;
            public double speedMultiplier;
            public double attackSpeedMultiplier;
            public double armorAddition;
            public double armorToughnessAddition;

            public PresetSlot() {}

            public PresetSlot(String name, double hp, double dmg, double spd,
                              double aspd, double arm, double tough) {
                this.presetName = name;
                this.healthMultiplier = hp;
                this.damageMultiplier = dmg;
                this.speedMultiplier = spd;
                this.attackSpeedMultiplier = aspd;
                this.armorAddition = arm;
                this.armorToughnessAddition = tough;
            }
        }
    }
}
