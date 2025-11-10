package com.khotyz.buffmobs.config;

import com.khotyz.buffmobs.util.RangedBehaviorMode;
import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.Arrays;
import java.util.List;

public class BuffMobsConfig {
    public static final ModConfigSpec SPEC;

    public static ModConfigSpec.BooleanValue enabled;
    public static ModConfigSpec.BooleanValue visualEffects;

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

    public static class Attributes {
        public static ModConfigSpec.DoubleValue healthMultiplier;
        public static ModConfigSpec.DoubleValue damageMultiplier;
        public static ModConfigSpec.DoubleValue speedMultiplier;
        public static ModConfigSpec.DoubleValue attackSpeedMultiplier;
        public static ModConfigSpec.DoubleValue armorAddition;
        public static ModConfigSpec.DoubleValue armorToughnessAddition;
    }

    public static class Effects {
        public static ModConfigSpec.IntValue duration;
        public static ModConfigSpec.IntValue strengthAmplifier;
        public static ModConfigSpec.IntValue speedAmplifier;
        public static ModConfigSpec.IntValue resistanceAmplifier;
        public static ModConfigSpec.IntValue regenerationAmplifier;
    }

    public static class HarmfulEffects {
        public static ModConfigSpec.BooleanValue enabled;
        public static ModConfigSpec.DoubleValue chance;
        public static ModConfigSpec.IntValue poisonDuration;
        public static ModConfigSpec.IntValue slownessDuration;
        public static ModConfigSpec.IntValue witherDuration;
    }

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

    public static class RangedMeleeSwitching {
        public static ModConfigSpec.BooleanValue enabled;
        public static ModConfigSpec.EnumValue<RangedBehaviorMode> behaviorMode;
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

    public static class MobPresets {
        public static ModConfigSpec.EnumValue<PresetToggle> enabled;
        public static PresetSlot preset1;
        public static PresetSlot preset2;
        public static PresetSlot preset3;
        public static PresetSlot preset4;
        public static PresetSlot preset5;
        public static ModConfigSpec.ConfigValue<List<? extends String>> mobMapping;

        public enum PresetToggle {
            ENABLED, DISABLED
        }

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

        builder.push("general");
        enabled = builder.define("enabled", true);
        visualEffects = builder.define("visualEffects", true);
        builder.pop();

        builder.push("dayScaling");
        DayScaling.enabled = builder.define("enabled", false);
        DayScaling.interval = builder.defineInRange("interval", 7, 1, 365);
        DayScaling.multiplier = builder.defineInRange("multiplier", 0.1, 0.01, 20.0);
        DayScaling.maxMultiplier = builder.defineInRange("maxMultiplier", 5.0, 1.0, 10.0);
        DayScaling.showNotifications = builder.define("showNotifications", true);
        DayScaling.notificationMode = builder.defineEnum("notificationMode", DayScaling.NotificationMode.EVERY_DAY);
        builder.pop();

        builder.push("attributes");
        Attributes.healthMultiplier = builder.defineInRange("healthMultiplier", 1.5, 0.1, 10.0);
        Attributes.damageMultiplier = builder.defineInRange("damageMultiplier", 1.5, 0.1, 10.0);
        Attributes.speedMultiplier = builder.defineInRange("speedMultiplier", 1.0, 0.1, 10.0);
        Attributes.attackSpeedMultiplier = builder.defineInRange("attackSpeedMultiplier", 1.0, 0.1, 10.0);
        Attributes.armorAddition = builder.defineInRange("armorAddition", 5.0, 0.0, 20.0);
        Attributes.armorToughnessAddition = builder.defineInRange("armorToughnessAddition", 0.0, 0.0, 20.0);
        builder.pop();

        builder.push("effects");
        Effects.duration = builder.defineInRange("duration", -1, -1, 7200);
        Effects.strengthAmplifier = builder.defineInRange("strengthAmplifier", 0, 0, 10);
        Effects.speedAmplifier = builder.defineInRange("speedAmplifier", 0, 0, 10);
        Effects.resistanceAmplifier = builder.defineInRange("resistanceAmplifier", 0, 0, 10);
        Effects.regenerationAmplifier = builder.defineInRange("regenerationAmplifier", 0, 0, 10);
        builder.pop();

        builder.push("harmfulEffects");
        HarmfulEffects.enabled = builder.define("enabled", true);
        HarmfulEffects.chance = builder.defineInRange("chance", 0.15, 0.0, 1.0);
        HarmfulEffects.poisonDuration = builder.defineInRange("poisonDuration", 5, 1, 60);
        HarmfulEffects.slownessDuration = builder.defineInRange("slownessDuration", 3, 1, 60);
        HarmfulEffects.witherDuration = builder.defineInRange("witherDuration", 3, 1, 60);
        builder.pop();

        builder.push("dimensionScaling");
        builder.comment("Configure dimension-specific scaling. Use dimension IDs like 'minecraft:overworld', 'minecraft:the_nether', 'minecraft:the_end'");
        DimensionScaling.slot1 = createDimensionSlot(builder, "slot1");
        DimensionScaling.slot2 = createDimensionSlot(builder, "slot2");
        DimensionScaling.slot3 = createDimensionSlot(builder, "slot3");
        DimensionScaling.slot4 = createDimensionSlot(builder, "slot4");
        DimensionScaling.slot5 = createDimensionSlot(builder, "slot5");
        builder.pop();

        builder.push("mobFilter");
        MobFilter.useWhitelist = builder.define("useWhitelist", false);
        MobFilter.whitelist = builder.defineList("whitelist", Arrays.asList(), o -> o instanceof String);
        MobFilter.blacklist = builder.defineList("blacklist", Arrays.asList("minecraft:warden"), o -> o instanceof String);
        builder.pop();

        builder.push("modidFilter");
        ModIdFilter.useWhitelist = builder.define("useWhitelist", false);
        ModIdFilter.whitelist = builder.defineList("whitelist", Arrays.asList(), o -> o instanceof String);
        ModIdFilter.blacklist = builder.defineList("blacklist", Arrays.asList(), o -> o instanceof String);
        builder.pop();

        builder.push("dimensionFilter");
        DimensionFilter.useWhitelist = builder.define("useWhitelist", false);
        DimensionFilter.whitelist = builder.defineList("whitelist",
                Arrays.asList("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"), o -> o instanceof String);
        DimensionFilter.blacklist = builder.defineList("blacklist", Arrays.asList(), o -> o instanceof String);
        builder.pop();

        builder.push("rangedMeleeSwitching");
        RangedMeleeSwitching.enabled = builder.define("enabled", true);
        RangedMeleeSwitching.behaviorMode = builder.defineEnum("behaviorMode", RangedBehaviorMode.MELEE);
        RangedMeleeSwitching.switchDistance = builder.defineInRange("switchDistance", 4.0, 1.0, 16.0);
        RangedMeleeSwitching.meleeSpeedMultiplier = builder.defineInRange("meleeSpeedMultiplier", 0.9, 0.5, 2.0);
        RangedMeleeSwitching.retreatSpeed = builder.defineInRange("retreatSpeed", 1.5, 0.5, 3.0);
        RangedMeleeSwitching.retreatDuration = builder.defineInRange("retreatDuration", 40, 10, 200);
        RangedMeleeSwitching.customRangedMobs = builder.defineList("customRangedMobs", Arrays.asList(), o -> o instanceof String);

        RangedMeleeSwitching.stoneSwordUnlockDay = builder.defineInRange("stoneSwordUnlockDay", 0, 0, 365);
        RangedMeleeSwitching.ironSwordUnlockDay = builder.defineInRange("ironSwordUnlockDay", 7, 0, 365);
        RangedMeleeSwitching.diamondSwordUnlockDay = builder.defineInRange("diamondSwordUnlockDay", 21, 0, 365);
        RangedMeleeSwitching.netheriteSwordUnlockDay = builder.defineInRange("netheriteSwordUnlockDay", 60, 0, 365);
        RangedMeleeSwitching.goldenAxeUnlockDay = builder.defineInRange("goldenAxeUnlockDay", 0, 0, 365);
        RangedMeleeSwitching.diamondAxeUnlockDay = builder.defineInRange("diamondAxeUnlockDay", 14, 0, 365);
        RangedMeleeSwitching.netheriteAxeUnlockDay = builder.defineInRange("netheriteAxeUnlockDay", 45, 0, 365);

        RangedMeleeSwitching.enchantmentsEnabled = builder.define("enchantmentsEnabled", true);
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

        builder.push("mobPresets");
        builder.comment("Mob presets allow custom multipliers per mob type. Use format 'namespace:mob_id:preset_name' in mobMapping");
        MobPresets.enabled = builder.defineEnum("enabled", MobPresets.PresetToggle.DISABLED);
        MobPresets.preset1 = createPresetSlot(builder, "preset1", "default", 1.0, 1.0, 1.0, 1.0, 0.0, 0.0);
        MobPresets.preset2 = createPresetSlot(builder, "preset2", "boss", 3.0, 2.5, 1.2, 1.5, 10.0, 5.0);
        MobPresets.preset3 = createPresetSlot(builder, "preset3", "elite", 2.0, 1.8, 1.1, 1.2, 5.0, 2.0);
        MobPresets.preset4 = createPresetSlot(builder, "preset4", "weak", 0.5, 0.5, 0.9, 0.8, 0.0, 0.0);
        MobPresets.preset5 = createPresetSlot(builder, "preset5", "", 1.0, 1.0, 1.0, 1.0, 0.0, 0.0);
        MobPresets.mobMapping = builder.defineList("mobMapping",
                Arrays.asList("minecraft:zombie:default", "minecraft:skeleton:default",
                        "minecraft:ender_dragon:boss", "minecraft:wither:boss"), o -> o instanceof String);
        builder.pop();

        SPEC = builder.build();
    }

    private static DimensionScaling.DimensionSlot createDimensionSlot(ModConfigSpec.Builder builder, String name) {
        builder.push(name);
        DimensionScaling.DimensionSlot slot = new DimensionScaling.DimensionSlot();
        slot.dimensionName = builder
                .comment("Dimension ID (e.g., 'minecraft:overworld', 'minecraft:the_nether')")
                .define("dimensionName", "", o -> o instanceof String);
        slot.healthMultiplier = builder
                .comment("Health multiplier as percentage (100 = no change, 200 = double)")
                .defineInRange("healthMultiplier", 100, 10, 1000);
        slot.damageMultiplier = builder
                .comment("Damage multiplier as percentage (100 = no change, 200 = double)")
                .defineInRange("damageMultiplier", 100, 10, 1000);
        slot.speedMultiplier = builder
                .comment("Speed multiplier as percentage (100 = no change, 150 = 1.5x)")
                .defineInRange("speedMultiplier", 100, 10, 500);
        slot.attackSpeedMultiplier = builder
                .comment("Attack speed multiplier as percentage (100 = no change)")
                .defineInRange("attackSpeedMultiplier", 100, 10, 1000);
        slot.armorAddition = builder
                .comment("Flat armor points to add")
                .defineInRange("armorAddition", 0, 0, 20);
        slot.armorToughnessAddition = builder
                .comment("Flat armor toughness to add")
                .defineInRange("armorToughnessAddition", 0, 0, 10);
        builder.pop();
        return slot;
    }

    private static MobPresets.PresetSlot createPresetSlot(ModConfigSpec.Builder builder, String name, String presetName,
                                                          double health, double damage, double speed, double atkSpeed,
                                                          double armor, double toughness) {
        builder.push(name);
        MobPresets.PresetSlot slot = new MobPresets.PresetSlot();
        slot.presetName = builder
                .comment("Preset identifier name")
                .define("presetName", presetName, o -> o instanceof String);
        slot.healthMultiplier = builder.defineInRange("healthMultiplier", health, 0.1, 100.0);
        slot.damageMultiplier = builder.defineInRange("damageMultiplier", damage, 0.1, 100.0);
        slot.speedMultiplier = builder.defineInRange("speedMultiplier", speed, 0.1, 10.0);
        slot.attackSpeedMultiplier = builder.defineInRange("attackSpeedMultiplier", atkSpeed, 0.1, 10.0);
        slot.armorAddition = builder.defineInRange("armorAddition", armor, 0.0, 30.0);
        slot.armorToughnessAddition = builder.defineInRange("armorToughnessAddition", toughness, 0.0, 20.0);
        builder.pop();
        return slot;
    }
}