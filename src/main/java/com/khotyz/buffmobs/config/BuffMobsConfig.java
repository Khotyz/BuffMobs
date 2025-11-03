package com.khotyz.buffmobs.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.List;

public class BuffMobsConfig {
    public static final ModConfigSpec SPEC;

    // General
    public static ModConfigSpec.BooleanValue enabled;
    public static ModConfigSpec.BooleanValue visualEffects;

    // Day Scaling
    public static class DayScaling {
        public static ModConfigSpec.BooleanValue enabled;
        public static ModConfigSpec.IntValue interval;
        public static ModConfigSpec.DoubleValue multiplier;
        public static ModConfigSpec.DoubleValue maxMultiplier;
        public static ModConfigSpec.BooleanValue showNotifications;
        public static ModConfigSpec.EnumValue<NotificationMode> notificationMode;

        public enum NotificationMode {
            EVERY_DAY, SCALING_INCREASE_ONLY
        }
    }

    // Attributes
    public static class Attributes {
        public static ModConfigSpec.DoubleValue healthMultiplier;
        public static ModConfigSpec.DoubleValue damageMultiplier;
        public static ModConfigSpec.DoubleValue speedMultiplier;
        public static ModConfigSpec.DoubleValue attackSpeedMultiplier;
        public static ModConfigSpec.DoubleValue armorAddition;
        public static ModConfigSpec.DoubleValue armorToughnessAddition;
    }

    // Effects
    public static class Effects {
        public static ModConfigSpec.IntValue duration;
        public static ModConfigSpec.IntValue strengthAmplifier;
        public static ModConfigSpec.IntValue speedAmplifier;
        public static ModConfigSpec.IntValue resistanceAmplifier;
        public static ModConfigSpec.IntValue regenerationAmplifier;
    }

    // Harmful Effects
    public static class HarmfulEffects {
        public static ModConfigSpec.BooleanValue enabled;
        public static ModConfigSpec.DoubleValue chance;
        public static ModConfigSpec.IntValue poisonDuration;
        public static ModConfigSpec.IntValue slownessDuration;
        public static ModConfigSpec.IntValue witherDuration;
    }

    // Dimension Scaling
    public static class DimensionScaling {
        public static DimensionSlot slot1;
        public static DimensionSlot slot2;
        public static DimensionSlot slot3;
        public static DimensionSlot slot4;
        public static DimensionSlot slot5;

        public static class DimensionSlot {
            public ModConfigSpec.ConfigValue<String> dimensionName;
            public ModConfigSpec.IntValue healthMultiplier;
            public ModConfigSpec.IntValue damageMultiplier;
            public ModConfigSpec.IntValue speedMultiplier;
            public ModConfigSpec.IntValue attackSpeedMultiplier;
            public ModConfigSpec.IntValue armorAddition;
            public ModConfigSpec.IntValue armorToughnessAddition;
        }
    }

    // Filters
    public static class MobFilter {
        public static ModConfigSpec.BooleanValue useWhitelist;
        public static ModConfigSpec.ConfigValue<List<? extends String>> whitelist;
        public static ModConfigSpec.ConfigValue<List<? extends String>> blacklist;
    }

    public static class ModIdFilter {
        public static ModConfigSpec.BooleanValue useWhitelist;
        public static ModConfigSpec.ConfigValue<List<? extends String>> whitelist;
        public static ModConfigSpec.ConfigValue<List<? extends String>> blacklist;
    }

    public static class DimensionFilter {
        public static ModConfigSpec.BooleanValue useWhitelist;
        public static ModConfigSpec.ConfigValue<List<? extends String>> whitelist;
        public static ModConfigSpec.ConfigValue<List<? extends String>> blacklist;
    }

    // Ranged Melee Switching
    public static class RangedMeleeSwitching {
        public static ModConfigSpec.BooleanValue enabled;
        public static ModConfigSpec.ConfigValue<String> behaviorMode;
        public static ModConfigSpec.DoubleValue switchDistance;
        public static ModConfigSpec.DoubleValue meleeSpeedMultiplier;
        public static ModConfigSpec.DoubleValue retreatSpeed;
        public static ModConfigSpec.IntValue retreatDuration;
        public static ModConfigSpec.ConfigValue<List<? extends String>> customRangedMobs;

        public static ModConfigSpec.IntValue stoneSwordUnlockDay;
        public static ModConfigSpec.IntValue ironSwordUnlockDay;
        public static ModConfigSpec.IntValue diamondSwordUnlockDay;
        public static ModConfigSpec.IntValue netheriteSwordUnlockDay;
        public static ModConfigSpec.IntValue goldenAxeUnlockDay;
        public static ModConfigSpec.IntValue diamondAxeUnlockDay;
        public static ModConfigSpec.IntValue netheriteAxeUnlockDay;

        public static ModConfigSpec.BooleanValue enchantmentsEnabled;
        public static ModConfigSpec.IntValue maxEnchantmentsPerWeapon;
        public static ModConfigSpec.IntValue daysPerEnchantmentLevel;

        public static ModConfigSpec.IntValue sharpnessUnlockDay;
        public static ModConfigSpec.IntValue sharpnessMaxLevel;
        public static ModConfigSpec.IntValue fireAspectUnlockDay;
        public static ModConfigSpec.IntValue fireAspectMaxLevel;
        public static ModConfigSpec.IntValue knockbackUnlockDay;
        public static ModConfigSpec.IntValue knockbackMaxLevel;
        public static ModConfigSpec.IntValue sweepingEdgeUnlockDay;
        public static ModConfigSpec.IntValue sweepingEdgeMaxLevel;
    }

    // Mob Presets
    public static class MobPresets {
        public static ModConfigSpec.BooleanValue enabled;
        public static PresetSlot preset1;
        public static PresetSlot preset2;
        public static PresetSlot preset3;
        public static PresetSlot preset4;
        public static PresetSlot preset5;
        public static ModConfigSpec.ConfigValue<List<? extends String>> mobMapping;

        public static class PresetSlot {
            public ModConfigSpec.ConfigValue<String> presetName;
            public ModConfigSpec.DoubleValue healthMultiplier;
            public ModConfigSpec.DoubleValue damageMultiplier;
            public ModConfigSpec.DoubleValue speedMultiplier;
            public ModConfigSpec.DoubleValue attackSpeedMultiplier;
            public ModConfigSpec.DoubleValue armorAddition;
            public ModConfigSpec.DoubleValue armorToughnessAddition;
        }
    }

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("General Settings").push("general");
        enabled = builder.comment("Enable the mod").define("enabled", true);
        visualEffects = builder.comment("Show particle effects on buffed mobs").define("visualEffects", true);
        builder.pop();

        builder.comment("Day Scaling Configuration").push("dayScaling");
        DayScaling.enabled = builder.comment("Enable day-based difficulty scaling").define("enabled", false);
        DayScaling.interval = builder.comment("Days between each scaling increase").defineInRange("interval", 7, 1, 365);
        DayScaling.multiplier = builder.comment("Multiplier added per interval").defineInRange("multiplier", 0.1, 0.01, 20.0);
        DayScaling.maxMultiplier = builder.comment("Maximum multiplier cap").defineInRange("maxMultiplier", 5.0, 1.0, 10.0);
        DayScaling.showNotifications = builder.comment("Show scaling notifications").define("showNotifications", true);
        DayScaling.notificationMode = builder.comment("When to show notifications").defineEnum("notificationMode", DayScaling.NotificationMode.EVERY_DAY);
        builder.pop();

        builder.comment("Attribute Multipliers").push("attributes");
        Attributes.healthMultiplier = builder.comment("Health multiplier").defineInRange("healthMultiplier", 1.5, 1.0, 10.0);
        Attributes.damageMultiplier = builder.comment("Damage multiplier").defineInRange("damageMultiplier", 1.5, 1.0, 10.0);
        Attributes.speedMultiplier = builder.comment("Speed multiplier").defineInRange("speedMultiplier", 1.0, 1.0, 10.0);
        Attributes.attackSpeedMultiplier = builder.comment("Attack speed multiplier").defineInRange("attackSpeedMultiplier", 1.0, 1.0, 10.0);
        Attributes.armorAddition = builder.comment("Flat armor addition").defineInRange("armorAddition", 5.0, 0.0, 20.0);
        Attributes.armorToughnessAddition = builder.comment("Flat armor toughness addition").defineInRange("armorToughnessAddition", 0.0, 0.0, 20.0);
        builder.pop();

        builder.comment("Status Effects").push("effects");
        Effects.duration = builder.comment("Effect duration in seconds (-1 for infinite)").defineInRange("duration", -1, -1, 7200);
        Effects.strengthAmplifier = builder.comment("Strength level (0 = disabled)").defineInRange("strengthAmplifier", 0, 0, 10);
        Effects.speedAmplifier = builder.comment("Speed level (0 = disabled)").defineInRange("speedAmplifier", 0, 0, 10);
        Effects.resistanceAmplifier = builder.comment("Resistance level (0 = disabled)").defineInRange("resistanceAmplifier", 0, 0, 10);
        Effects.regenerationAmplifier = builder.comment("Regeneration level (0 = disabled)").defineInRange("regenerationAmplifier", 0, 0, 10);
        builder.pop();

        builder.comment("Harmful Effects on Players").push("harmfulEffects");
        HarmfulEffects.enabled = builder.comment("Enable harmful effects").define("enabled", true);
        HarmfulEffects.chance = builder.comment("Chance per hit").defineInRange("chance", 0.15, 0.0, 1.0);
        HarmfulEffects.poisonDuration = builder.comment("Poison duration (seconds)").defineInRange("poisonDuration", 5, 1, 60);
        HarmfulEffects.slownessDuration = builder.comment("Slowness duration (seconds)").defineInRange("slownessDuration", 3, 1, 60);
        HarmfulEffects.witherDuration = builder.comment("Wither duration (seconds)").defineInRange("witherDuration", 3, 1, 60);
        builder.pop();

        builder.comment("Dimension-Specific Scaling").push("dimensionScaling");
        DimensionScaling.slot1 = createDimensionSlot(builder, "slot1");
        DimensionScaling.slot2 = createDimensionSlot(builder, "slot2");
        DimensionScaling.slot3 = createDimensionSlot(builder, "slot3");
        DimensionScaling.slot4 = createDimensionSlot(builder, "slot4");
        DimensionScaling.slot5 = createDimensionSlot(builder, "slot5");
        builder.pop();

        builder.comment("Mob Filtering").push("mobFilter");
        MobFilter.useWhitelist = builder.comment("Use whitelist mode").define("useWhitelist", false);
        MobFilter.whitelist = builder.comment("Mob whitelist").defineList("whitelist", List.of(), obj -> obj instanceof String);
        MobFilter.blacklist = builder.comment("Mob blacklist").defineList("blacklist", List.of("minecraft:warden"), obj -> obj instanceof String);
        builder.pop();

        builder.comment("Mod ID Filtering").push("modidFilter");
        ModIdFilter.useWhitelist = builder.comment("Use whitelist mode").define("useWhitelist", false);
        ModIdFilter.whitelist = builder.comment("Mod ID whitelist").defineList("whitelist", List.of(), obj -> obj instanceof String);
        ModIdFilter.blacklist = builder.comment("Mod ID blacklist").defineList("blacklist", List.of(), obj -> obj instanceof String);
        builder.pop();

        builder.comment("Dimension Filtering").push("dimensionFilter");
        DimensionFilter.useWhitelist = builder.comment("Use whitelist mode").define("useWhitelist", false);
        DimensionFilter.whitelist = builder.comment("Dimension whitelist").defineList("whitelist",
                List.of("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"), obj -> obj instanceof String);
        DimensionFilter.blacklist = builder.comment("Dimension blacklist").defineList("blacklist", List.of(), obj -> obj instanceof String);
        builder.pop();

        builder.comment("Ranged/Melee Switching System").push("rangedMeleeSwitching");
        RangedMeleeSwitching.enabled = builder.comment("Enable ranged/melee switching").define("enabled", true);
        RangedMeleeSwitching.behaviorMode = builder
                .comment("Behavior mode: 'melee' (switch to melee), 'retreat' (back away), 'random' (random choice)")
                .define("behaviorMode", "melee", obj -> {
                    if (!(obj instanceof String)) return false;
                    String str = (String) obj;
                    return str.equalsIgnoreCase("melee") || str.equalsIgnoreCase("retreat") || str.equalsIgnoreCase("random");
                });
        RangedMeleeSwitching.switchDistance = builder.comment("Switch distance").defineInRange("switchDistance", 4.0, 1.0, 16.0);
        RangedMeleeSwitching.meleeSpeedMultiplier = builder.comment("Melee speed multiplier").defineInRange("meleeSpeedMultiplier", 0.9, 0.5, 2.0);
        RangedMeleeSwitching.retreatSpeed = builder.comment("Retreat speed multiplier").defineInRange("retreatSpeed", 1.2, 0.5, 2.0);
        RangedMeleeSwitching.retreatDuration = builder.comment("Retreat duration in ticks (20 ticks = 1 second)").defineInRange("retreatDuration", 60, 20, 200);
        RangedMeleeSwitching.customRangedMobs = builder.comment("Custom ranged mobs").defineList("customRangedMobs", List.of(), obj -> obj instanceof String);

        RangedMeleeSwitching.stoneSwordUnlockDay = builder.defineInRange("stoneSwordUnlockDay", 0, 0, 365);
        RangedMeleeSwitching.ironSwordUnlockDay = builder.defineInRange("ironSwordUnlockDay", 7, 0, 365);
        RangedMeleeSwitching.diamondSwordUnlockDay = builder.defineInRange("diamondSwordUnlockDay", 21, 0, 365);
        RangedMeleeSwitching.netheriteSwordUnlockDay = builder.defineInRange("netheriteSwordUnlockDay", 60, 0, 365);
        RangedMeleeSwitching.goldenAxeUnlockDay = builder.defineInRange("goldenAxeUnlockDay", 0, 0, 365);
        RangedMeleeSwitching.diamondAxeUnlockDay = builder.defineInRange("diamondAxeUnlockDay", 14, 0, 365);
        RangedMeleeSwitching.netheriteAxeUnlockDay = builder.defineInRange("netheriteAxeUnlockDay", 45, 0, 365);

        RangedMeleeSwitching.enchantmentsEnabled = builder.comment("Enable weapon enchantments").define("enchantmentsEnabled", true);
        RangedMeleeSwitching.maxEnchantmentsPerWeapon = builder.defineInRange("maxEnchantmentsPerWeapon", 2, 1, 4);
        RangedMeleeSwitching.daysPerEnchantmentLevel = builder.defineInRange("daysPerEnchantmentLevel", 7, 1, 30);

        RangedMeleeSwitching.sharpnessUnlockDay = builder.defineInRange("sharpnessUnlockDay", 0, 0, 365);
        RangedMeleeSwitching.sharpnessMaxLevel = builder.defineInRange("sharpnessMaxLevel", 5, 1, 5);
        RangedMeleeSwitching.fireAspectUnlockDay = builder.defineInRange("fireAspectUnlockDay", 14, 0, 365);
        RangedMeleeSwitching.fireAspectMaxLevel = builder.defineInRange("fireAspectMaxLevel", 2, 1, 2);
        RangedMeleeSwitching.knockbackUnlockDay = builder.defineInRange("knockbackUnlockDay", 7, 0, 365);
        RangedMeleeSwitching.knockbackMaxLevel = builder.defineInRange("knockbackMaxLevel", 2, 1, 2);
        RangedMeleeSwitching.sweepingEdgeUnlockDay = builder.defineInRange("sweepingEdgeUnlockDay", 21, 0, 365);
        RangedMeleeSwitching.sweepingEdgeMaxLevel = builder.defineInRange("sweepingEdgeMaxLevel", 3, 1, 3);
        builder.pop();

        builder.comment("Mob Presets System").push("mobPresets");
        MobPresets.enabled = builder.comment("Enable mob presets").define("enabled", false);
        MobPresets.preset1 = createPresetSlot(builder, "preset1", "default", 1.0, 1.0, 1.0, 1.0, 0.0, 0.0);
        MobPresets.preset2 = createPresetSlot(builder, "preset2", "boss", 3.0, 2.5, 1.2, 1.5, 10.0, 5.0);
        MobPresets.preset3 = createPresetSlot(builder, "preset3", "elite", 2.0, 1.8, 1.1, 1.2, 5.0, 2.0);
        MobPresets.preset4 = createPresetSlot(builder, "preset4", "weak", 0.5, 0.5, 0.9, 0.8, 0.0, 0.0);
        MobPresets.preset5 = createPresetSlot(builder, "preset5", "", 1.0, 1.0, 1.0, 1.0, 0.0, 0.0);
        MobPresets.mobMapping = builder.comment("Mob to preset mapping (format: minecraft:zombie:default)")
                .defineList("mobMapping", List.of(
                        "minecraft:zombie:default",
                        "minecraft:skeleton:default",
                        "minecraft:ender_dragon:boss",
                        "minecraft:wither:boss"
                ), obj -> obj instanceof String);
        builder.pop();

        SPEC = builder.build();
    }

    private static DimensionScaling.DimensionSlot createDimensionSlot(ModConfigSpec.Builder builder, String name) {
        builder.push(name);
        DimensionScaling.DimensionSlot slot = new DimensionScaling.DimensionSlot();
        slot.dimensionName = builder.comment("Dimension ID (e.g., minecraft:the_nether)").define("dimensionName", "");
        slot.healthMultiplier = builder.comment("Health multiplier %").defineInRange("healthMultiplier", 100, 100, 1000);
        slot.damageMultiplier = builder.comment("Damage multiplier %").defineInRange("damageMultiplier", 100, 100, 1000);
        slot.speedMultiplier = builder.comment("Speed multiplier %").defineInRange("speedMultiplier", 100, 100, 500);
        slot.attackSpeedMultiplier = builder.comment("Attack speed multiplier %").defineInRange("attackSpeedMultiplier", 100, 100, 1000);
        slot.armorAddition = builder.comment("Armor addition").defineInRange("armorAddition", 0, 0, 20);
        slot.armorToughnessAddition = builder.comment("Armor toughness addition").defineInRange("armorToughnessAddition", 0, 0, 10);
        builder.pop();
        return slot;
    }

    private static MobPresets.PresetSlot createPresetSlot(ModConfigSpec.Builder builder, String name, String presetName,
                                                          double health, double damage, double speed, double atkSpeed,
                                                          double armor, double toughness) {
        builder.push(name);
        MobPresets.PresetSlot slot = new MobPresets.PresetSlot();
        slot.presetName = builder.comment("Preset name").define("presetName", presetName);
        slot.healthMultiplier = builder.comment("Health multiplier").defineInRange("healthMultiplier", health, 0.1, 100.0);
        slot.damageMultiplier = builder.comment("Damage multiplier").defineInRange("damageMultiplier", damage, 0.1, 100.0);
        slot.speedMultiplier = builder.comment("Speed multiplier").defineInRange("speedMultiplier", speed, 0.1, 10.0);
        slot.attackSpeedMultiplier = builder.comment("Attack speed multiplier").defineInRange("attackSpeedMultiplier", atkSpeed, 0.1, 10.0);
        slot.armorAddition = builder.comment("Armor addition").defineInRange("armorAddition", armor, 0.0, 30.0);
        slot.armorToughnessAddition = builder.comment("Armor toughness addition").defineInRange("armorToughnessAddition", toughness, 0.0, 20.0);
        builder.pop();
        return slot;
    }
}