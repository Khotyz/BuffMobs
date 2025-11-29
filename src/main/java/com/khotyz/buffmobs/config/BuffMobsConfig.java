package com.khotyz.buffmobs.config;

import com.khotyz.buffmobs.util.RangedBehaviorMode;
import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.List;

public class BuffMobsConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
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
        BUILDER.comment("BuffMobs Configuration").push("general");
        enabled = BUILDER.comment("Enable or disable the entire mod").define("enabled", true);
        visualEffects = BUILDER.comment("Show visual particle effects for mob buffs").define("visualEffects", true);
        BUILDER.pop();

        BUILDER.comment("Day-based scaling system").push("dayScaling");
        DayScaling.enabled = BUILDER.comment("Enable day-based difficulty scaling").define("enabled", false);
        DayScaling.interval = BUILDER.comment("Days between each scaling increase").defineInRange("interval", 7, 1, 365);
        DayScaling.multiplier = BUILDER.comment("Multiplier increase per interval").defineInRange("multiplier", 0.1, 0.01, 20.0);
        DayScaling.maxMultiplier = BUILDER.comment("Maximum multiplier cap").defineInRange("maxMultiplier", 5.0, 1.0, 10.0);
        DayScaling.showNotifications = BUILDER.comment("Show day scaling notifications").define("showNotifications", true);
        DayScaling.notificationMode = BUILDER.comment("Notification frequency mode").defineEnum("notificationMode", DayScaling.NotificationMode.EVERY_DAY);
        BUILDER.pop();

        BUILDER.comment("Base attribute multipliers").push("attributes");
        Attributes.healthMultiplier = BUILDER.comment("Health multiplier").defineInRange("healthMultiplier", 1.5, 0.1, 10.0);
        Attributes.damageMultiplier = BUILDER.comment("Damage multiplier").defineInRange("damageMultiplier", 1.5, 0.1, 10.0);
        Attributes.speedMultiplier = BUILDER.comment("Speed multiplier").defineInRange("speedMultiplier", 1.0, 0.1, 10.0);
        Attributes.attackSpeedMultiplier = BUILDER.comment("Attack speed multiplier").defineInRange("attackSpeedMultiplier", 1.0, 0.1, 10.0);
        Attributes.armorAddition = BUILDER.comment("Flat armor points to add").defineInRange("armorAddition", 5.0, 0.0, 20.0);
        Attributes.armorToughnessAddition = BUILDER.comment("Flat armor toughness to add").defineInRange("armorToughnessAddition", 0.0, 0.0, 20.0);
        BUILDER.pop();

        BUILDER.comment("Status effects applied to mobs").push("effects");
        Effects.duration = BUILDER.comment("Effect duration in seconds (-1 for infinite)").defineInRange("duration", -1, -1, 7200);
        Effects.strengthAmplifier = BUILDER.comment("Strength effect level (0 to disable)").defineInRange("strengthAmplifier", 0, 0, 10);
        Effects.speedAmplifier = BUILDER.comment("Speed effect level (0 to disable)").defineInRange("speedAmplifier", 0, 0, 10);
        Effects.resistanceAmplifier = BUILDER.comment("Resistance effect level (0 to disable)").defineInRange("resistanceAmplifier", 0, 0, 10);
        Effects.regenerationAmplifier = BUILDER.comment("Regeneration effect level (0 to disable)").defineInRange("regenerationAmplifier", 0, 0, 10);
        BUILDER.pop();

        BUILDER.comment("Harmful effects applied to players").push("harmfulEffects");
        HarmfulEffects.enabled = BUILDER.comment("Enable harmful effects on players").define("enabled", true);
        HarmfulEffects.chance = BUILDER.comment("Chance to apply effect per hit").defineInRange("chance", 0.15, 0.0, 1.0);
        HarmfulEffects.poisonDuration = BUILDER.comment("Poison duration in seconds").defineInRange("poisonDuration", 5, 1, 60);
        HarmfulEffects.slownessDuration = BUILDER.comment("Slowness duration in seconds").defineInRange("slownessDuration", 3, 1, 60);
        HarmfulEffects.witherDuration = BUILDER.comment("Wither duration in seconds").defineInRange("witherDuration", 3, 1, 60);
        BUILDER.pop();

        BUILDER.comment("Dimension-specific scaling (use dimension IDs like 'minecraft:overworld')").push("dimensionScaling");
        DimensionScaling.slot1 = createDimensionSlot(BUILDER, "slot1");
        DimensionScaling.slot2 = createDimensionSlot(BUILDER, "slot2");
        DimensionScaling.slot3 = createDimensionSlot(BUILDER, "slot3");
        DimensionScaling.slot4 = createDimensionSlot(BUILDER, "slot4");
        DimensionScaling.slot5 = createDimensionSlot(BUILDER, "slot5");
        BUILDER.pop();

        BUILDER.comment("Filter which mobs are affected").push("mobFilter");
        MobFilter.useWhitelist = BUILDER.comment("Use whitelist instead of blacklist").define("useWhitelist", false);
        MobFilter.whitelist = BUILDER.comment("Mob whitelist (only these mobs will be buffed if enabled)").defineList("whitelist", List.of(), obj -> obj instanceof String);
        MobFilter.blacklist = BUILDER.comment("Mob blacklist (these mobs will never be buffed)").defineList("blacklist", List.of("minecraft:warden"), obj -> obj instanceof String);
        BUILDER.pop();

        BUILDER.comment("Filter which mod's mobs are affected").push("modidFilter");
        ModIdFilter.useWhitelist = BUILDER.comment("Use whitelist instead of blacklist").define("useWhitelist", false);
        ModIdFilter.whitelist = BUILDER.comment("Mod ID whitelist").defineList("whitelist", List.of(), obj -> obj instanceof String);
        ModIdFilter.blacklist = BUILDER.comment("Mod ID blacklist").defineList("blacklist", List.of(), obj -> obj instanceof String);
        BUILDER.pop();

        BUILDER.comment("Filter which dimensions mobs are affected in").push("dimensionFilter");
        DimensionFilter.useWhitelist = BUILDER.comment("Use whitelist instead of blacklist").define("useWhitelist", false);
        DimensionFilter.whitelist = BUILDER.comment("Dimension whitelist").defineList("whitelist", List.of("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"), obj -> obj instanceof String);
        DimensionFilter.blacklist = BUILDER.comment("Dimension blacklist").defineList("blacklist", List.of(), obj -> obj instanceof String);
        BUILDER.pop();

        BUILDER.comment("Ranged mob melee switching system").push("rangedMeleeSwitching");
        RangedMeleeSwitching.enabled = BUILDER.comment("Enable ranged mobs to switch to melee").define("enabled", true);
        RangedMeleeSwitching.behaviorMode = BUILDER.comment("Behavior mode: MELEE, RETREAT, or RANDOM").defineEnum("behaviorMode", RangedBehaviorMode.MELEE);
        RangedMeleeSwitching.switchDistance = BUILDER.comment("Distance to switch to melee mode").defineInRange("switchDistance", 4.0, 1.0, 16.0);
        RangedMeleeSwitching.meleeSpeedMultiplier = BUILDER.comment("Speed multiplier in melee mode").defineInRange("meleeSpeedMultiplier", 0.9, 0.5, 2.0);
        RangedMeleeSwitching.retreatSpeed = BUILDER.comment("Retreat movement speed").defineInRange("retreatSpeed", 1.5, 0.5, 3.0);
        RangedMeleeSwitching.retreatDuration = BUILDER.comment("Retreat duration in ticks").defineInRange("retreatDuration", 40, 10, 200);
        RangedMeleeSwitching.customRangedMobs = BUILDER.comment("Additional ranged mobs to enable switching for").defineList("customRangedMobs", List.of(), obj -> obj instanceof String);

        RangedMeleeSwitching.stoneSwordUnlockDay = BUILDER.comment("Day stone sword unlocks").defineInRange("stoneSwordUnlockDay", 0, 0, 365);
        RangedMeleeSwitching.ironSwordUnlockDay = BUILDER.comment("Day iron sword unlocks").defineInRange("ironSwordUnlockDay", 7, 0, 365);
        RangedMeleeSwitching.diamondSwordUnlockDay = BUILDER.comment("Day diamond sword unlocks").defineInRange("diamondSwordUnlockDay", 21, 0, 365);
        RangedMeleeSwitching.netheriteSwordUnlockDay = BUILDER.comment("Day netherite sword unlocks").defineInRange("netheriteSwordUnlockDay", 60, 0, 365);
        RangedMeleeSwitching.goldenAxeUnlockDay = BUILDER.comment("Day golden axe unlocks").defineInRange("goldenAxeUnlockDay", 0, 0, 365);
        RangedMeleeSwitching.diamondAxeUnlockDay = BUILDER.comment("Day diamond axe unlocks").defineInRange("diamondAxeUnlockDay", 14, 0, 365);
        RangedMeleeSwitching.netheriteAxeUnlockDay = BUILDER.comment("Day netherite axe unlocks").defineInRange("netheriteAxeUnlockDay", 45, 0, 365);

        RangedMeleeSwitching.enchantmentsEnabled = BUILDER.comment("Enable enchantments on melee weapons").define("enchantmentsEnabled", true);
        RangedMeleeSwitching.maxEnchantmentsPerWeapon = BUILDER.comment("Maximum enchantments per weapon").defineInRange("maxEnchantmentsPerWeapon", 2, 1, 4);
        RangedMeleeSwitching.daysPerEnchantmentLevel = BUILDER.comment("Days required per enchantment level").defineInRange("daysPerEnchantmentLevel", 7, 1, 30);

        RangedMeleeSwitching.sharpnessUnlockDay = BUILDER.comment("Day sharpness unlocks").defineInRange("sharpnessUnlockDay", 0, 0, 365);
        RangedMeleeSwitching.sharpnessMaxLevel = BUILDER.comment("Maximum sharpness level").defineInRange("sharpnessMaxLevel", 5, 1, 5);
        RangedMeleeSwitching.fireAspectUnlockDay = BUILDER.comment("Day fire aspect unlocks").defineInRange("fireAspectUnlockDay", 14, 0, 365);
        RangedMeleeSwitching.fireAspectMaxLevel = BUILDER.comment("Maximum fire aspect level").defineInRange("fireAspectMaxLevel", 2, 1, 2);
        RangedMeleeSwitching.knockbackUnlockDay = BUILDER.comment("Day knockback unlocks").defineInRange("knockbackUnlockDay", 7, 0, 365);
        RangedMeleeSwitching.knockbackMaxLevel = BUILDER.comment("Maximum knockback level").defineInRange("knockbackMaxLevel", 2, 1, 2);
        RangedMeleeSwitching.sweepingEdgeUnlockDay = BUILDER.comment("Day sweeping edge unlocks").defineInRange("sweepingEdgeUnlockDay", 21, 0, 365);
        RangedMeleeSwitching.sweepingEdgeMaxLevel = BUILDER.comment("Maximum sweeping edge level").defineInRange("sweepingEdgeMaxLevel", 3, 1, 3);
        BUILDER.pop();

        BUILDER.comment("Mob preset system - assign custom multipliers to specific mobs").push("mobPresets");
        MobPresets.enabled = BUILDER.comment("Enable mob presets system").defineEnum("enabled", MobPresets.PresetToggle.DISABLED);
        MobPresets.preset1 = createPresetSlot(BUILDER, "preset1", "default", 1.0, 1.0, 1.0, 1.0, 0.0, 0.0);
        MobPresets.preset2 = createPresetSlot(BUILDER, "preset2", "boss", 3.0, 2.5, 1.2, 1.5, 10.0, 5.0);
        MobPresets.preset3 = createPresetSlot(BUILDER, "preset3", "elite", 2.0, 1.8, 1.1, 1.2, 5.0, 2.0);
        MobPresets.preset4 = createPresetSlot(BUILDER, "preset4", "weak", 0.5, 0.5, 0.9, 0.8, 0.0, 0.0);
        MobPresets.preset5 = createPresetSlot(BUILDER, "preset5", "", 1.0, 1.0, 1.0, 1.0, 0.0, 0.0);
        MobPresets.mobMapping = BUILDER.comment("Mob to preset mappings (format: namespace:mob_id:preset_name)").defineList("mobMapping", List.of(), obj -> obj instanceof String);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    private static DimensionScaling.DimensionSlot createDimensionSlot(ModConfigSpec.Builder builder, String name) {
        builder.push(name);
        DimensionScaling.DimensionSlot slot = new DimensionScaling.DimensionSlot();
        slot.dimensionName = builder.comment("Dimension ID (e.g., 'minecraft:overworld')").define("dimensionName", "");
        slot.healthMultiplier = builder.comment("Health multiplier percentage (100 = no change)").defineInRange("healthMultiplier", 100, 10, 1000);
        slot.damageMultiplier = builder.comment("Damage multiplier percentage (100 = no change)").defineInRange("damageMultiplier", 100, 10, 1000);
        slot.speedMultiplier = builder.comment("Speed multiplier percentage (100 = no change)").defineInRange("speedMultiplier", 100, 10, 500);
        slot.attackSpeedMultiplier = builder.comment("Attack speed multiplier percentage (100 = no change)").defineInRange("attackSpeedMultiplier", 100, 10, 1000);
        slot.armorAddition = builder.comment("Flat armor points to add").defineInRange("armorAddition", 0, 0, 20);
        slot.armorToughnessAddition = builder.comment("Flat armor toughness to add").defineInRange("armorToughnessAddition", 0, 0, 10);
        builder.pop();
        return slot;
    }

    private static MobPresets.PresetSlot createPresetSlot(ModConfigSpec.Builder builder, String name, String presetName,
                                                          double health, double damage, double speed, double atkSpeed,
                                                          double armor, double toughness) {
        builder.push(name);
        MobPresets.PresetSlot slot = new MobPresets.PresetSlot();
        slot.presetName = builder.comment("Preset identifier name").define("presetName", presetName);
        slot.healthMultiplier = builder.comment("Health multiplier").defineInRange("healthMultiplier", health, 0.1, 100.0);
        slot.damageMultiplier = builder.comment("Damage multiplier").defineInRange("damageMultiplier", damage, 0.1, 100.0);
        slot.speedMultiplier = builder.comment("Speed multiplier").defineInRange("speedMultiplier", speed, 0.1, 10.0);
        slot.attackSpeedMultiplier = builder.comment("Attack speed multiplier").defineInRange("attackSpeedMultiplier", atkSpeed, 0.1, 10.0);
        slot.armorAddition = builder.comment("Flat armor addition").defineInRange("armorAddition", armor, 0.0, 30.0);
        slot.armorToughnessAddition = builder.comment("Flat armor toughness addition").defineInRange("armorToughnessAddition", toughness, 0.0, 20.0);
        builder.pop();
        return slot;
    }
}