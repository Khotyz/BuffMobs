package com.khotyz.buffmobs.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class BuffMobsConfig {
    public static final ModConfigSpec SPEC;
    public static final BuffMobsConfig INSTANCE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        INSTANCE = new BuffMobsConfig(builder);
        SPEC = builder.build();
    }

    public final ModConfigSpec.BooleanValue enabled;
    public final ModConfigSpec.BooleanValue visualEffects;

    public final DayScaling dayScaling;
    public final Attributes attributes;
    public final Effects effects;
    public final HarmfulEffects harmfulEffects;
    public final DimensionScaling dimensionScaling;
    public final MobFilter mobFilter;
    public final ModIdFilter modidFilter;
    public final DimensionFilter dimensionFilter;
    public final RangedMeleeSwitching rangedMeleeSwitching;
    public final CombatDraft combatDraft;
    public final MobPresets mobPresets;
    public final PassiveMobAggression passiveMobAggression;
    public final ZombieHandling zombieHandling;
    public final HealthSync healthSync;
    public final DimensionMaxHealth dimensionMaxHealth;
    public final MobArmor mobArmor;
    public final EliteMobs eliteMobs;
    public final LootXpScaling lootXpScaling;
    public final Traits traits;
    public final PackAggression packAggression;
    public final SpecialAbilities specialAbilities;
    public final BloodMoon bloodMoon;
    public final AdaptiveDifficulty adaptiveDifficulty;

    private BuffMobsConfig(ModConfigSpec.Builder builder) {
        builder.push("general");
        enabled = builder.comment("Enable or disable the mod entirely").define("enabled", true);
        visualEffects = builder.comment("Show visual particles for status effects").define("visualEffects", true);
        builder.pop();

        dayScaling = new DayScaling(builder);
        attributes = new Attributes(builder);
        effects = new Effects(builder);
        harmfulEffects = new HarmfulEffects(builder);
        dimensionScaling = new DimensionScaling(builder);
        mobFilter = new MobFilter(builder);
        modidFilter = new ModIdFilter(builder);
        dimensionFilter = new DimensionFilter(builder);
        rangedMeleeSwitching = new RangedMeleeSwitching(builder);
        combatDraft = new CombatDraft(builder);
        mobPresets = new MobPresets(builder);
        passiveMobAggression = new PassiveMobAggression(builder);
        zombieHandling = new ZombieHandling(builder);
        healthSync = new HealthSync(builder);
        dimensionMaxHealth = new DimensionMaxHealth(builder);
        mobArmor = new MobArmor(builder);
        eliteMobs = new EliteMobs(builder);
        lootXpScaling = new LootXpScaling(builder);
        traits = new Traits(builder);
        packAggression = new PackAggression(builder);
        specialAbilities = new SpecialAbilities(builder);
        bloodMoon = new BloodMoon(builder);
        adaptiveDifficulty = new AdaptiveDifficulty(builder);
    }

    public static class DayScaling {
        public final ModConfigSpec.BooleanValue enabled;
        public final ModConfigSpec.IntValue interval;
        public final ModConfigSpec.DoubleValue multiplier;
        public final ModConfigSpec.DoubleValue maxMultiplier;
        public final ModConfigSpec.BooleanValue showNotifications;
        public final ModConfigSpec.EnumValue<NotificationMode> notificationMode;

        DayScaling(ModConfigSpec.Builder builder) {
            builder.push("dayScaling");
            enabled = builder.comment("Enable day-based difficulty scaling").define("enabled", false);
            interval = builder.comment("Days between each scaling increase").defineInRange("interval", 7, 1, 365);
            multiplier = builder.comment("Multiplier added per interval").defineInRange("multiplier", 0.1, 0.01, 999999.0);
            maxMultiplier = builder.comment("Maximum scaling multiplier").defineInRange("maxMultiplier", 5.0, 0.0, 999999.0);
            showNotifications = builder.comment("Show day scaling notifications to players").define("showNotifications", true);
            notificationMode = builder.comment("When to send notifications: EVERY_DAY or SCALING_INCREASE_ONLY")
                    .defineEnum("notificationMode", NotificationMode.EVERY_DAY);
            builder.pop();
        }

        public enum NotificationMode { EVERY_DAY, SCALING_INCREASE_ONLY }
    }

    public static class Attributes {
        public final ModConfigSpec.DoubleValue healthMultiplier;
        public final ModConfigSpec.DoubleValue damageMultiplier;
        public final ModConfigSpec.DoubleValue speedMultiplier;
        public final ModConfigSpec.DoubleValue attackSpeedMultiplier;
        public final ModConfigSpec.DoubleValue armorAddition;
        public final ModConfigSpec.DoubleValue armorToughnessAddition;

        Attributes(ModConfigSpec.Builder builder) {
            builder.push("attributes");
            healthMultiplier = builder.comment("Health multiplier for mobs").defineInRange("healthMultiplier", 1.5, 0.1, 999999.0);
            damageMultiplier = builder.comment("Damage multiplier for mobs").defineInRange("damageMultiplier", 1.5, 0.1, 999999.0);
            speedMultiplier = builder.comment("Speed multiplier for mobs").defineInRange("speedMultiplier", 1.0, 0.1, 999999.0);
            attackSpeedMultiplier = builder.comment("Attack speed multiplier for mobs").defineInRange("attackSpeedMultiplier", 1.0, 0.1, 999999.0);
            armorAddition = builder.comment("Armor points added to mobs").defineInRange("armorAddition", 5.0, 0.0, 999999.0);
            armorToughnessAddition = builder.comment("Armor toughness added to mobs").defineInRange("armorToughnessAddition", 0.0, 0.0, 999999.0);
            builder.pop();
        }
    }

    public static class Effects {
        public final ModConfigSpec.IntValue duration;
        public final ModConfigSpec.IntValue strengthAmplifier;
        public final ModConfigSpec.IntValue speedAmplifier;
        public final ModConfigSpec.IntValue resistanceAmplifier;
        public final ModConfigSpec.IntValue regenerationAmplifier;
        public final ModConfigSpec.IntValue absorptionAmplifier;

        Effects(ModConfigSpec.Builder builder) {
            builder.push("effects");
            duration = builder.comment("Effect duration in seconds, -1 for infinite").defineInRange("duration", -1, -1, 7200);
            strengthAmplifier = builder.comment("Strength effect level (0 = disabled)").defineInRange("strengthAmplifier", 0, 0, 10);
            speedAmplifier = builder.comment("Speed effect level (0 = disabled)").defineInRange("speedAmplifier", 0, 0, 10);
            resistanceAmplifier = builder.comment("Resistance effect level (0 = disabled)").defineInRange("resistanceAmplifier", 0, 0, 10);
            regenerationAmplifier = builder.comment("Regeneration effect level (0 = disabled, skipped for undead)").defineInRange("regenerationAmplifier", 0, 0, 10);
            absorptionAmplifier = builder.comment("Absorption effect level (0 = disabled)").defineInRange("absorptionAmplifier", 0, 0, 10);
            builder.pop();
        }
    }

    public static class HarmfulEffects {
        public final ModConfigSpec.BooleanValue enabled;
        public final ModConfigSpec.DoubleValue chance;
        public final ModConfigSpec.IntValue poisonDuration;
        public final ModConfigSpec.IntValue slownessDuration;
        public final ModConfigSpec.IntValue witherDuration;
        public final ModConfigSpec.BooleanValue poisonEnabled;
        public final ModConfigSpec.BooleanValue slownessEnabled;
        public final ModConfigSpec.BooleanValue witherEnabled;

        HarmfulEffects(ModConfigSpec.Builder builder) {
            builder.push("harmfulEffects");
            enabled = builder.comment("Enable harmful effects applied to players on hit").define("enabled", true);
            chance = builder.comment("Chance of applying a harmful effect per hit (0.0-1.0)").defineInRange("chance", 0.15, 0.0, 1.0);
            poisonDuration = builder.comment("Poison duration in seconds").defineInRange("poisonDuration", 5, 1, 60);
            slownessDuration = builder.comment("Slowness duration in seconds").defineInRange("slownessDuration", 3, 1, 60);
            witherDuration = builder.comment("Wither duration in seconds").defineInRange("witherDuration", 3, 1, 60);
            poisonEnabled = builder.comment("Allow Poison to be picked as the random harmful effect (each hit randomly picks one of the enabled effects)").define("poisonEnabled", true);
            slownessEnabled = builder.comment("Allow Slowness to be picked as the random harmful effect").define("slownessEnabled", true);
            witherEnabled = builder.comment("Allow Wither to be picked as the random harmful effect").define("witherEnabled", true);
            builder.pop();
        }
    }

    public static class DimensionScaling {
        public final ModConfigSpec.EnumValue<DimensionScalingMode> mode;
        public final DimensionSlot slot1, slot2, slot3, slot4, slot5;

        DimensionScaling(ModConfigSpec.Builder builder) {
            builder.push("dimensionScaling");
            mode = builder.comment("Select scaling mode: SCALING (multiply base attributes) or OVERRIDE (replace with fixed multipliers)")
                    .defineEnum("mode", DimensionScalingMode.SCALING);
            slot1 = new DimensionSlot(builder, "slot1");
            slot2 = new DimensionSlot(builder, "slot2");
            slot3 = new DimensionSlot(builder, "slot3");
            slot4 = new DimensionSlot(builder, "slot4");
            slot5 = new DimensionSlot(builder, "slot5");
            builder.pop();
        }

        public enum DimensionScalingMode { SCALING, OVERRIDE }

        public static class DimensionSlot {
            public final ModConfigSpec.ConfigValue<String> dimensionName;
            public final ModConfigSpec.IntValue healthMultiplier;
            public final ModConfigSpec.IntValue damageMultiplier;
            public final ModConfigSpec.IntValue speedMultiplier;
            public final ModConfigSpec.IntValue attackSpeedMultiplier;
            public final ModConfigSpec.IntValue armorAddition;
            public final ModConfigSpec.IntValue armorToughnessAddition;

            DimensionSlot(ModConfigSpec.Builder builder, String name) {
                builder.push(name);
                dimensionName = builder.comment("Dimension ID, e.g. minecraft:overworld").define("dimensionName", "");
                healthMultiplier = builder.comment("Health % multiplier (100 = 1x)").defineInRange("healthMultiplier", 100, 100, 999999);
                damageMultiplier = builder.comment("Damage % multiplier (100 = 1x)").defineInRange("damageMultiplier", 100, 100, 999999);
                speedMultiplier = builder.comment("Speed % multiplier (100 = 1x)").defineInRange("speedMultiplier", 100, 100, 999999);
                attackSpeedMultiplier = builder.comment("Attack speed % multiplier (100 = 1x)").defineInRange("attackSpeedMultiplier", 100, 100, 999999);
                armorAddition = builder.comment("Armor points added").defineInRange("armorAddition", 0, 0, 999999);
                armorToughnessAddition = builder.comment("Armor toughness points added").defineInRange("armorToughnessAddition", 0, 0, 999999);
                builder.pop();
            }
        }
    }

    public static class MobFilter {
        public final ModConfigSpec.BooleanValue useWhitelist;
        public final ModConfigSpec.ConfigValue<List<? extends String>> whitelist;
        public final ModConfigSpec.ConfigValue<List<? extends String>> blacklist;

        MobFilter(ModConfigSpec.Builder builder) {
            builder.push("mobFilter");
            useWhitelist = builder.comment("If true, only mobs in the whitelist are buffed").define("useWhitelist", false);
            whitelist = builder.comment("Mob IDs to buff (used when useWhitelist = true)").defineListAllowEmpty("whitelist", new ArrayList<>(), o -> o instanceof String);
            blacklist = builder.comment("Mob IDs to never buff").defineListAllowEmpty("blacklist", List.of("minecraft:warden"), o -> o instanceof String);
            builder.pop();
        }
    }

    public static class ModIdFilter {
        public final ModConfigSpec.BooleanValue useWhitelist;
        public final ModConfigSpec.ConfigValue<List<? extends String>> whitelist;
        public final ModConfigSpec.ConfigValue<List<? extends String>> blacklist;

        ModIdFilter(ModConfigSpec.Builder builder) {
            builder.push("modidFilter");
            useWhitelist = builder.comment("If true, only mobs from whitelisted mods are buffed").define("useWhitelist", false);
            whitelist = builder.comment("Mod IDs to buff").defineListAllowEmpty("whitelist", new ArrayList<>(), o -> o instanceof String);
            blacklist = builder.comment("Mod IDs to never buff").defineListAllowEmpty("blacklist", new ArrayList<>(), o -> o instanceof String);
            builder.pop();
        }
    }

    public static class DimensionFilter {
        public final ModConfigSpec.BooleanValue useWhitelist;
        public final ModConfigSpec.ConfigValue<List<? extends String>> whitelist;
        public final ModConfigSpec.ConfigValue<List<? extends String>> blacklist;

        DimensionFilter(ModConfigSpec.Builder builder) {
            builder.push("dimensionFilter");
            useWhitelist = builder.comment("If true, only buff mobs in whitelisted dimensions").define("useWhitelist", false);
            whitelist = builder.comment("Dimensions where mobs are buffed (useWhitelist = true)")
                    .defineListAllowEmpty("whitelist", List.of("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"), o -> o instanceof String);
            blacklist = builder.comment("Dimensions where mobs are never buffed").defineListAllowEmpty("blacklist", new ArrayList<>(), o -> o instanceof String);
            builder.pop();
        }
    }

    public static class RangedMeleeSwitching {
        public final ModConfigSpec.BooleanValue enabled;
        public final ModConfigSpec.EnumValue<BehaviorMode> behaviorMode;
        public final ModConfigSpec.DoubleValue switchDistance;
        public final ModConfigSpec.DoubleValue meleeSpeedMultiplier;
        public final ModConfigSpec.ConfigValue<List<? extends String>> customRangedMobs;
        public final ModConfigSpec.IntValue stoneSwordUnlockDay;
        public final ModConfigSpec.IntValue ironSwordUnlockDay;
        public final ModConfigSpec.IntValue diamondSwordUnlockDay;
        public final ModConfigSpec.IntValue netheriteSwordUnlockDay;
        public final ModConfigSpec.IntValue goldenAxeUnlockDay;
        public final ModConfigSpec.IntValue diamondAxeUnlockDay;
        public final ModConfigSpec.IntValue netheriteAxeUnlockDay;
        public final ModConfigSpec.BooleanValue enchantmentsEnabled;
        public final ModConfigSpec.IntValue maxEnchantmentsPerWeapon;
        public final ModConfigSpec.IntValue daysPerEnchantmentLevel;
        public final ModConfigSpec.IntValue sharpnessUnlockDay;
        public final ModConfigSpec.IntValue sharpnessMaxLevel;
        public final ModConfigSpec.IntValue fireAspectUnlockDay;
        public final ModConfigSpec.IntValue fireAspectMaxLevel;
        public final ModConfigSpec.IntValue knockbackUnlockDay;
        public final ModConfigSpec.IntValue knockbackMaxLevel;
        public final ModConfigSpec.IntValue sweepingEdgeUnlockDay;
        public final ModConfigSpec.IntValue sweepingEdgeMaxLevel;

        public enum BehaviorMode { MELEE, KITE, RANDOM }

        RangedMeleeSwitching(ModConfigSpec.Builder builder) {
            builder.push("rangedMeleeSwitching");
            enabled = builder.comment("Enable ranged mob close-range behavior").define("enabled", true);
            behaviorMode = builder.comment(
                            "MELEE: ranged mobs switch to sword when player is close.\n" +
                                    "KITE:  ranged mobs run away to maintain distance.\n" +
                                    "RANDOM: each mob randomly picks MELEE or KITE on spawn.")
                    .defineEnum("behaviorMode", BehaviorMode.RANDOM);
            switchDistance = builder.comment("Distance threshold that triggers the behavior").defineInRange("switchDistance", 4.0, 1.0, 16.0);
            meleeSpeedMultiplier = builder.comment("Speed multiplier when in melee mode").defineInRange("meleeSpeedMultiplier", 0.9, 0.1, 5.0);
            customRangedMobs = builder.comment("Additional mob IDs treated as ranged").defineListAllowEmpty("customRangedMobs", new ArrayList<>(), o -> o instanceof String);

            stoneSwordUnlockDay = builder.defineInRange("stoneSwordUnlockDay", 0, 0, 365);
            ironSwordUnlockDay = builder.defineInRange("ironSwordUnlockDay", 7, 0, 365);
            diamondSwordUnlockDay = builder.defineInRange("diamondSwordUnlockDay", 21, 0, 365);
            netheriteSwordUnlockDay = builder.defineInRange("netheriteSwordUnlockDay", 60, 0, 365);
            goldenAxeUnlockDay = builder.defineInRange("goldenAxeUnlockDay", 0, 0, 365);
            diamondAxeUnlockDay = builder.defineInRange("diamondAxeUnlockDay", 14, 0, 365);
            netheriteAxeUnlockDay = builder.defineInRange("netheriteAxeUnlockDay", 45, 0, 365);

            enchantmentsEnabled = builder.comment("Enable enchantments on generated melee weapons").define("enchantmentsEnabled", true);
            maxEnchantmentsPerWeapon = builder.defineInRange("maxEnchantmentsPerWeapon", 2, 1, 4);
            daysPerEnchantmentLevel = builder.defineInRange("daysPerEnchantmentLevel", 7, 1, 30);
            sharpnessUnlockDay = builder.defineInRange("sharpnessUnlockDay", 0, 0, 365);
            sharpnessMaxLevel = builder.defineInRange("sharpnessMaxLevel", 5, 1, 5);
            fireAspectUnlockDay = builder.defineInRange("fireAspectUnlockDay", 14, 0, 365);
            fireAspectMaxLevel = builder.defineInRange("fireAspectMaxLevel", 2, 1, 2);
            knockbackUnlockDay = builder.defineInRange("knockbackUnlockDay", 7, 0, 365);
            knockbackMaxLevel = builder.defineInRange("knockbackMaxLevel", 2, 1, 2);
            sweepingEdgeUnlockDay = builder.defineInRange("sweepingEdgeUnlockDay", 21, 0, 365);
            sweepingEdgeMaxLevel = builder.defineInRange("sweepingEdgeMaxLevel", 3, 1, 3);
            builder.pop();
        }
    }

    public static class CombatDraft {
        public final ModConfigSpec.BooleanValue enabled;
        public final ModConfigSpec.DoubleValue healthThreshold;
        public final ModConfigSpec.IntValue regenAmplifier;
        public final ModConfigSpec.IntValue regenDuration;
        public final ModConfigSpec.IntValue cooldownTicks;
        public final ModConfigSpec.IntValue maxUses;
        public final ModConfigSpec.BooleanValue useWhitelist;
        public final ModConfigSpec.ConfigValue<List<? extends String>> whitelist;
        public final ModConfigSpec.ConfigValue<List<? extends String>> blacklist;

        CombatDraft(ModConfigSpec.Builder builder) {
            builder.push("combatDraft");
            enabled = builder.comment(
                            "Enable CombatDraft: hostile/neutral mobs drink a regeneration potion when low on health.")
                    .define("enabled", true);
            healthThreshold = builder.comment(
                            "HP fraction that triggers the potion (0.20 = 20% of max HP).")
                    .defineInRange("healthThreshold", 0.20, 0.01, 0.99);
            regenAmplifier = builder.comment(
                            "Regeneration effect level (1 = Regen I, 4 = Regen IV). Default 4.")
                    .defineInRange("regenAmplifier", 4, 1, 10);
            regenDuration = builder.comment(
                            "Regeneration duration in seconds.")
                    .defineInRange("regenDuration", 10, 1, 120);
            cooldownTicks = builder.comment(
                            "Ticks before the mob can use CombatDraft again. Default 600 (30 s).")
                    .defineInRange("cooldownTicks", 600, 20, 72000);
            maxUses = builder.comment(
                            "Max times a mob can use CombatDraft per life. 0 = unlimited.")
                    .defineInRange("maxUses", 0, 0, 100);
            useWhitelist = builder.comment(
                            "If true, only mobs in 'whitelist' can use CombatDraft.")
                    .define("useWhitelist", false);
            whitelist = builder.comment(
                            "Mob IDs allowed to use CombatDraft (only when useWhitelist = true).")
                    .defineListAllowEmpty("whitelist", new ArrayList<>(), o -> o instanceof String);
            blacklist = builder.comment(
                            "Mob IDs that cannot use CombatDraft (always applied). Slime, magma_cube and ghast are hardcoded.")
                    .defineListAllowEmpty("blacklist", new ArrayList<>(), o -> o instanceof String);
            builder.pop();
        }
    }

    public static class MobPresets {
        public final ModConfigSpec.BooleanValue enabled;
        public final ModConfigSpec.BooleanValue overrideDimensionScaling;
        public final PresetSlot preset1, preset2, preset3, preset4, preset5;
        public final ModConfigSpec.ConfigValue<List<? extends String>> mobMapping;

        MobPresets(ModConfigSpec.Builder builder) {
            builder.push("mobPresets");
            enabled = builder.comment("Enable per-mob stat presets").define("enabled", false);
            overrideDimensionScaling = builder.comment(
                            "If true, mobs that have a matching preset ignore dimensionScaling multipliers and only use the preset values.")
                    .define("overrideDimensionScaling", false);
            preset1 = new PresetSlot(builder, "preset1", "", 1.0, 1.0, 1.0, 1.0, 0.0, 0.0);
            preset2 = new PresetSlot(builder, "preset2", "", 3.0, 2.5, 1.2, 1.5, 10.0, 5.0);
            preset3 = new PresetSlot(builder, "preset3", "", 2.0, 1.8, 1.1, 1.2, 5.0, 2.0);
            preset4 = new PresetSlot(builder, "preset4", "", 0.5, 0.5, 0.9, 0.8, 0.0, 0.0);
            preset5 = new PresetSlot(builder, "preset5", "", 1.0, 1.0, 1.0, 1.0, 0.0, 0.0);
            mobMapping = builder.comment("Mob-to-preset mapping, format: modid:mob_id:preset_name")
                    .defineListAllowEmpty("mobMapping", List.of(
                            "minecraft:zombie:default",
                            "minecraft:skeleton:default",
                            "minecraft:ender_dragon:boss",
                            "minecraft:wither:boss"
                    ), o -> o instanceof String);
            builder.pop();
        }

        public static class PresetSlot {
            public final ModConfigSpec.ConfigValue<String> presetName;
            public final ModConfigSpec.DoubleValue healthMultiplier;
            public final ModConfigSpec.DoubleValue damageMultiplier;
            public final ModConfigSpec.DoubleValue speedMultiplier;
            public final ModConfigSpec.DoubleValue attackSpeedMultiplier;
            public final ModConfigSpec.DoubleValue armorAddition;
            public final ModConfigSpec.DoubleValue armorToughnessAddition;

            PresetSlot(ModConfigSpec.Builder builder, String key, String name,
                       double hp, double dmg, double spd, double aspd, double arm, double tough) {
                builder.push(key);
                presetName = builder.define("presetName", name);
                healthMultiplier = builder.defineInRange("healthMultiplier", hp, 0.01, 999999.0);
                damageMultiplier = builder.defineInRange("damageMultiplier", dmg, 0.01, 999999.0);
                speedMultiplier = builder.defineInRange("speedMultiplier", spd, 0.01, 999999.0);
                attackSpeedMultiplier = builder.defineInRange("attackSpeedMultiplier", aspd, 0.01, 999999.0);
                armorAddition = builder.defineInRange("armorAddition", arm, 0.0, 999999.0);
                armorToughnessAddition = builder.defineInRange("armorToughnessAddition", tough, 0.0, 999999.0);
                builder.pop();
            }
        }
    }

    public static class PassiveMobAggression {
        public final ModConfigSpec.EnumValue<PassiveMode> mode;
        public final ModConfigSpec.DoubleValue baseDamage;
        public final ModConfigSpec.BooleanValue scaleWithHealth;
        public final ModConfigSpec.DoubleValue healthScaleFactor;
        public final ModConfigSpec.ConfigValue<List<? extends String>> whitelist;
        public final ModConfigSpec.ConfigValue<List<? extends String>> blacklist;

        public enum PassiveMode { OFF, NEUTRAL, HOSTILE }

        PassiveMobAggression(ModConfigSpec.Builder builder) {
            builder.push("passiveMobAggression");
            mode = builder.comment("Passive mob aggression mode: OFF, NEUTRAL (retaliate), HOSTILE (actively attack)")
                    .defineEnum("mode", PassiveMode.OFF);
            baseDamage = builder.comment("Base attack damage given to affected passive mobs").defineInRange("baseDamage", 3.0, 0.5, 100.0);
            scaleWithHealth = builder.comment("Add bonus damage based on the mob's max health").define("scaleWithHealth", false);
            healthScaleFactor = builder.comment("Scaling factor: totalDamage = baseDamage + (maxHealth * healthScaleFactor)").defineInRange("healthScaleFactor", 0.1, 0.0, 100.0);
            whitelist = builder.comment("Mob IDs affected (if non-empty, only these mobs are affected)").defineListAllowEmpty("whitelist", new ArrayList<>(), o -> o instanceof String);
            blacklist = builder.comment("Mob IDs that are never affected").defineListAllowEmpty("blacklist", new ArrayList<>(), o -> o instanceof String);
            builder.pop();
        }
    }

    public static class ZombieHandling {
        public final ModConfigSpec.BooleanValue disableLeaderZombies;
        public final ModConfigSpec.BooleanValue excludeLeaderBonusFromMultiplier;

        ZombieHandling(ModConfigSpec.Builder builder) {
            builder.push("zombieHandling");
            disableLeaderZombies = builder.comment(
                            "Strip the vanilla leader zombie health bonus modifier from zombies entirely.")
                    .define("disableLeaderZombies", false);
            excludeLeaderBonusFromMultiplier = builder.comment(
                            "Exclude the vanilla leader zombie health bonus from BuffMobs health scaling so leader zombies do not become overpowered. Ignored if disableLeaderZombies is true.")
                    .define("excludeLeaderBonusFromMultiplier", true);
            builder.pop();
        }
    }

    public static class HealthSync {
        public final ModConfigSpec.BooleanValue enabled;
        public final ModConfigSpec.EnumValue<HealthSyncMode> mode;

        HealthSync(ModConfigSpec.Builder builder) {
            builder.push("healthSync");
            enabled = builder.comment(
                            "Synchronize a mob's current health with its new max health after BuffMobs modifies it, fixing structure mob health desync.")
                    .define("enabled", true);
            mode = builder.comment(
                            "OVERRIDE: fully heal the mob to its new max health.\n" +
                                    "STACK: preserve the mob's existing damage taken and add the health delta on top.")
                    .defineEnum("mode", HealthSyncMode.OVERRIDE);
            builder.pop();
        }

        public enum HealthSyncMode { OVERRIDE, STACK }
    }

    public static class DimensionMaxHealth {
        public final ModConfigSpec.BooleanValue enabled;
        public final DimensionHealthSlot slot1, slot2, slot3, slot4, slot5;
        public final ModConfigSpec.BooleanValue useAllowlist;
        public final ModConfigSpec.ConfigValue<List<? extends String>> allowlist;
        public final ModConfigSpec.ConfigValue<List<? extends String>> denylist;

        DimensionMaxHealth(ModConfigSpec.Builder builder) {
            builder.push("dimensionMaxHealth");
            enabled = builder.comment("Enable fixed max health overrides per dimension").define("enabled", false);
            slot1 = new DimensionHealthSlot(builder, "slot1");
            slot2 = new DimensionHealthSlot(builder, "slot2");
            slot3 = new DimensionHealthSlot(builder, "slot3");
            slot4 = new DimensionHealthSlot(builder, "slot4");
            slot5 = new DimensionHealthSlot(builder, "slot5");
            useAllowlist = builder.comment("If true, only mobs in 'allowlist' receive dimension max health overrides").define("useAllowlist", false);
            allowlist = builder.comment("Mob IDs eligible for dimension max health overrides (used when useAllowlist = true)")
                    .defineListAllowEmpty("allowlist", new ArrayList<>(), o -> o instanceof String);
            denylist = builder.comment("Mob IDs that never receive dimension max health overrides")
                    .defineListAllowEmpty("denylist", new ArrayList<>(), o -> o instanceof String);
            builder.pop();
        }

        public static class DimensionHealthSlot {
            public final ModConfigSpec.ConfigValue<String> dimensionName;
            public final ModConfigSpec.DoubleValue maxHealth;

            DimensionHealthSlot(ModConfigSpec.Builder builder, String name) {
                builder.push(name);
                dimensionName = builder.comment("Dimension ID, e.g. minecraft:the_nether").define("dimensionName", "");
                maxHealth = builder.comment("Fixed max health applied to mobs in this dimension (0 = disabled)").defineInRange("maxHealth", 0.0, 0.0, 999999.0);
                builder.pop();
            }
        }
    }

    public static class MobArmor {
        public final ModConfigSpec.BooleanValue enabled;
        public final ModConfigSpec.DoubleValue equipChance;
        public final ModConfigSpec.IntValue leatherUnlockDay;
        public final ModConfigSpec.IntValue goldUnlockDay;
        public final ModConfigSpec.IntValue ironUnlockDay;
        public final ModConfigSpec.IntValue diamondUnlockDay;
        public final ModConfigSpec.IntValue netheriteUnlockDay;
        public final ModConfigSpec.DoubleValue leatherWeight;
        public final ModConfigSpec.DoubleValue goldWeight;
        public final ModConfigSpec.DoubleValue ironWeight;
        public final ModConfigSpec.DoubleValue diamondWeight;
        public final ModConfigSpec.DoubleValue netheriteWeight;
        public final ModConfigSpec.BooleanValue enchantmentsEnabled;
        public final ModConfigSpec.IntValue maxEnchantmentsPerPiece;
        public final ModConfigSpec.IntValue daysPerEnchantmentLevel;
        public final ModConfigSpec.IntValue protectionUnlockDay;
        public final ModConfigSpec.IntValue protectionMaxLevel;
        public final ModConfigSpec.IntValue thornsUnlockDay;
        public final ModConfigSpec.IntValue thornsMaxLevel;
        public final ModConfigSpec.IntValue featherFallingUnlockDay;
        public final ModConfigSpec.IntValue featherFallingMaxLevel;
        public final ModConfigSpec.EnumValue<EquipmentSetupMode> setupMode;
        public final ModConfigSpec.BooleanValue mixedTiersEnabled;
        public final ModConfigSpec.DoubleValue helmetChance;
        public final ModConfigSpec.DoubleValue chestplateChance;
        public final ModConfigSpec.DoubleValue leggingsChance;
        public final ModConfigSpec.DoubleValue bootsChance;
        public final ModConfigSpec.IntValue minPieces;
        public final ModConfigSpec.IntValue maxPieces;
        public final ModConfigSpec.BooleanValue dropChanceEnabled;
        public final ModConfigSpec.DoubleValue dropChance;
        public final ModConfigSpec.DoubleValue enchantedPieceChance;
        public final ModConfigSpec.IntValue fireProtectionUnlockDay;
        public final ModConfigSpec.IntValue fireProtectionMaxLevel;
        public final ModConfigSpec.IntValue blastProtectionUnlockDay;
        public final ModConfigSpec.IntValue blastProtectionMaxLevel;
        public final ModConfigSpec.IntValue projectileProtectionUnlockDay;
        public final ModConfigSpec.IntValue projectileProtectionMaxLevel;
        public final ModConfigSpec.IntValue unbreakingUnlockDay;
        public final ModConfigSpec.IntValue unbreakingMaxLevel;
        public final ModConfigSpec.IntValue respirationUnlockDay;
        public final ModConfigSpec.IntValue respirationMaxLevel;
        public final ModConfigSpec.IntValue aquaAffinityUnlockDay;
        public final ModConfigSpec.IntValue aquaAffinityMaxLevel;
        public final ModConfigSpec.IntValue depthStriderUnlockDay;
        public final ModConfigSpec.IntValue depthStriderMaxLevel;
        public final ModConfigSpec.IntValue frostWalkerUnlockDay;
        public final ModConfigSpec.IntValue frostWalkerMaxLevel;
        public final ModConfigSpec.BooleanValue cursesEnabled;
        public final ModConfigSpec.DoubleValue curseChance;
        public final ModConfigSpec.IntValue curseUnlockDay;
        public final ModConfigSpec.BooleanValue bindingCurseEnabled;
        public final ModConfigSpec.BooleanValue vanishingCurseEnabled;
        public final ModConfigSpec.ConfigValue<List<? extends String>> additionalArmorMobs;

        public enum EquipmentSetupMode { FULL_SET, RANDOM_PIECES }

        MobArmor(ModConfigSpec.Builder builder) {
            builder.push("mobArmor");
            enabled = builder.comment("Enable scaled armor sets on buffed mobs with valid armor slots").define("enabled", false);
            equipChance = builder.comment("Chance that an eligible buffed mob receives a generated armor setup (0.0-1.0)").defineInRange("equipChance", 1.0, 0.0, 1.0);
            leatherUnlockDay = builder.defineInRange("leatherUnlockDay", 0, 0, 365);
            goldUnlockDay = builder.defineInRange("goldUnlockDay", 7, 0, 365);
            ironUnlockDay = builder.defineInRange("ironUnlockDay", 14, 0, 365);
            diamondUnlockDay = builder.defineInRange("diamondUnlockDay", 30, 0, 365);
            netheriteUnlockDay = builder.defineInRange("netheriteUnlockDay", 60, 0, 365);
            leatherWeight = builder.defineInRange("leatherWeight", 50.0, 0.0, 1000.0);
            goldWeight = builder.defineInRange("goldWeight", 25.0, 0.0, 1000.0);
            ironWeight = builder.defineInRange("ironWeight", 15.0, 0.0, 1000.0);
            diamondWeight = builder.defineInRange("diamondWeight", 4.0, 0.0, 1000.0);
            netheriteWeight = builder.defineInRange("netheriteWeight", 1.0, 0.0, 1000.0);
            enchantmentsEnabled = builder.comment("Enable enchantments on generated armor pieces").define("enchantmentsEnabled", true);
            maxEnchantmentsPerPiece = builder.defineInRange("maxEnchantmentsPerPiece", 2, 1, 4);
            daysPerEnchantmentLevel = builder.defineInRange("daysPerEnchantmentLevel", 7, 1, 30);
            protectionUnlockDay = builder.defineInRange("protectionUnlockDay", 7, 0, 365);
            protectionMaxLevel = builder.defineInRange("protectionMaxLevel", 4, 1, 4);
            thornsUnlockDay = builder.defineInRange("thornsUnlockDay", 21, 0, 365);
            thornsMaxLevel = builder.defineInRange("thornsMaxLevel", 3, 1, 3);
            featherFallingUnlockDay = builder.defineInRange("featherFallingUnlockDay", 14, 0, 365);
            featherFallingMaxLevel = builder.defineInRange("featherFallingMaxLevel", 4, 1, 4);
            additionalArmorMobs = builder.comment("Additional mob IDs (e.g. from other mods) treated as having valid armor slots").defineListAllowEmpty("additionalArmorMobs", new ArrayList<>(), o -> o instanceof String);
            setupMode = builder.comment("FULL_SET equips every armor slot, RANDOM_PIECES rolls each slot with the per-slot chances below").defineEnum("setupMode", EquipmentSetupMode.FULL_SET);
            mixedTiersEnabled = builder.comment("Roll the armor tier separately for each equipped piece instead of using one tier for the whole setup").define("mixedTiersEnabled", false);
            helmetChance = builder.comment("Chance to include a helmet in RANDOM_PIECES mode (0.0-1.0)").defineInRange("helmetChance", 1.0, 0.0, 1.0);
            chestplateChance = builder.comment("Chance to include a chestplate in RANDOM_PIECES mode (0.0-1.0)").defineInRange("chestplateChance", 1.0, 0.0, 1.0);
            leggingsChance = builder.comment("Chance to include leggings in RANDOM_PIECES mode (0.0-1.0)").defineInRange("leggingsChance", 1.0, 0.0, 1.0);
            bootsChance = builder.comment("Chance to include boots in RANDOM_PIECES mode (0.0-1.0)").defineInRange("bootsChance", 1.0, 0.0, 1.0);
            minPieces = builder.comment("Minimum armor pieces guaranteed in RANDOM_PIECES mode").defineInRange("minPieces", 1, 0, 4);
            maxPieces = builder.comment("Maximum armor pieces allowed in RANDOM_PIECES mode").defineInRange("maxPieces", 4, 0, 4);
            dropChanceEnabled = builder.comment("Allow generated armor pieces to drop when the mob dies").define("dropChanceEnabled", false);
            dropChance = builder.comment("Drop chance for each generated armor piece when dropChanceEnabled is on (0.0-1.0, vanilla equipment uses about 0.085)").defineInRange("dropChance", 0.085, 0.0, 1.0);
            enchantedPieceChance = builder.comment("Chance that an individual generated armor piece receives enchantments (0.0-1.0)").defineInRange("enchantedPieceChance", 1.0, 0.0, 1.0);
            fireProtectionUnlockDay = builder.defineInRange("fireProtectionUnlockDay", 14, 0, 365);
            fireProtectionMaxLevel = builder.defineInRange("fireProtectionMaxLevel", 4, 1, 4);
            blastProtectionUnlockDay = builder.defineInRange("blastProtectionUnlockDay", 21, 0, 365);
            blastProtectionMaxLevel = builder.defineInRange("blastProtectionMaxLevel", 4, 1, 4);
            projectileProtectionUnlockDay = builder.defineInRange("projectileProtectionUnlockDay", 14, 0, 365);
            projectileProtectionMaxLevel = builder.defineInRange("projectileProtectionMaxLevel", 4, 1, 4);
            unbreakingUnlockDay = builder.defineInRange("unbreakingUnlockDay", 7, 0, 365);
            unbreakingMaxLevel = builder.defineInRange("unbreakingMaxLevel", 3, 1, 3);
            respirationUnlockDay = builder.defineInRange("respirationUnlockDay", 21, 0, 365);
            respirationMaxLevel = builder.defineInRange("respirationMaxLevel", 3, 1, 3);
            aquaAffinityUnlockDay = builder.defineInRange("aquaAffinityUnlockDay", 21, 0, 365);
            aquaAffinityMaxLevel = builder.defineInRange("aquaAffinityMaxLevel", 1, 1, 1);
            depthStriderUnlockDay = builder.defineInRange("depthStriderUnlockDay", 21, 0, 365);
            depthStriderMaxLevel = builder.defineInRange("depthStriderMaxLevel", 3, 1, 3);
            frostWalkerUnlockDay = builder.defineInRange("frostWalkerUnlockDay", 28, 0, 365);
            frostWalkerMaxLevel = builder.defineInRange("frostWalkerMaxLevel", 2, 1, 2);
            cursesEnabled = builder.comment("Allow curse enchantments on generated armor pieces").define("cursesEnabled", false);
            curseChance = builder.comment("Chance for each enabled curse to be applied to a generated armor piece (0.0-1.0)").defineInRange("curseChance", 0.1, 0.0, 1.0);
            curseUnlockDay = builder.comment("Day from which curses can appear on generated armor").defineInRange("curseUnlockDay", 30, 0, 365);
            bindingCurseEnabled = builder.comment("Allow Curse of Binding on generated armor pieces").define("bindingCurseEnabled", true);
            vanishingCurseEnabled = builder.comment("Allow Curse of Vanishing on generated armor pieces").define("vanishingCurseEnabled", true);
            builder.pop();
        }
    }

    public static class EliteMobs {
        public final ModConfigSpec.BooleanValue enabled;
        public final ModConfigSpec.DoubleValue baseChance;
        public final ModConfigSpec.DoubleValue chanceIncreasePerDay;
        public final ModConfigSpec.DoubleValue maxChance;
        public final ModConfigSpec.DoubleValue healthMultiplier;
        public final ModConfigSpec.DoubleValue damageMultiplier;
        public final ModConfigSpec.EnumValue<NameColor> nameColor;
        public final ModConfigSpec.ConfigValue<String> customNameColor;
        public final ModConfigSpec.EnumValue<NamePrefix> namePrefix;
        public final ModConfigSpec.EnumValue<NameMode> nameMode;
        public final ModConfigSpec.BooleanValue useGeneratedPrefix;
        public final ModConfigSpec.BooleanValue useGeneratedSuffix;
        public final ModConfigSpec.ConfigValue<List<? extends String>> generatedNamePrefixes;
        public final ModConfigSpec.ConfigValue<List<? extends String>> generatedNameSuffixes;
        public final ModConfigSpec.EnumValue<EliteParticle> particle;
        public final ModConfigSpec.IntValue particleIntervalTicks;
        public final ModConfigSpec.BooleanValue notifyNearbyPlayers;
        public final ModConfigSpec.DoubleValue notifyRadius;
        public final ModConfigSpec.BooleanValue bonusRewardsEnabled;
        public final ModConfigSpec.DoubleValue rewardXpMultiplier;
        public final ModConfigSpec.IntValue rewardExtraLootRolls;

        public enum NameColor { GOLD, RED, DARK_RED, LIGHT_PURPLE, AQUA, YELLOW, RANDOM, CUSTOM }

        public enum NamePrefix { elite, CHAMPION, BRUTE, DREAD, RANDOM }

        public enum NameMode { GENERATED, PREFIX }

        public enum EliteParticle { ENCHANT, FLAME, SOUL_FIRE_FLAME, PORTAL, END_ROD, HAPPY_VILLAGER, HEART, SNOWFLAKE, ELECTRIC_SPARK, RANDOM, NONE }

        EliteMobs(ModConfigSpec.Builder builder) {
            builder.push("eliteMobs");
            enabled = builder.comment("Enable Elite mob variants").define("enabled", false);
            baseChance = builder.comment("Base chance for a buffed mob to become Elite on spawn (0.0-1.0)").defineInRange("baseChance", 0.02, 0.0, 1.0);
            chanceIncreasePerDay = builder.comment("Additional Elite chance gained per in-game day").defineInRange("chanceIncreasePerDay", 0.001, 0.0, 1.0);
            maxChance = builder.comment("Maximum Elite chance (0.0-1.0)").defineInRange("maxChance", 0.25, 0.0, 1.0);
            healthMultiplier = builder.comment("Health multiplier applied to Elite mobs on top of normal buffs").defineInRange("healthMultiplier", 3.0, 1.0, 999999.0);
            damageMultiplier = builder.comment("Damage multiplier applied to Elite mobs on top of normal buffs").defineInRange("damageMultiplier", 2.0, 1.0, 999999.0);
            nameColor = builder.comment("Color of the Elite custom name, RANDOM picks one preset color per mob, CUSTOM uses customNameColor").defineEnum("nameColor", NameColor.GOLD);
            customNameColor = builder.comment("Custom Elite name color used when nameColor is CUSTOM, as a hex RGB value such as #FF55FF, FF55FF or 0xFF55FF").define("customNameColor", "#FFAA00");
            namePrefix = builder.comment("Translatable name prefix for Elite mobs, RANDOM picks one per mob").defineEnum("namePrefix", NamePrefix.RANDOM);
            nameMode = builder.comment("GENERATED builds a roguelike name from the generated prefix/suffix lists around the mob type name, PREFIX uses the translatable namePrefix option").defineEnum("nameMode", NameMode.GENERATED);
            useGeneratedPrefix = builder.comment("Add a random word from generatedNamePrefixes before the mob name in GENERATED mode").define("useGeneratedPrefix", true);
            useGeneratedSuffix = builder.comment("Add a random title from generatedNameSuffixes after the mob name in GENERATED mode").define("useGeneratedSuffix", true);
            generatedNamePrefixes = builder.comment("Words randomly placed before the mob name in GENERATED mode. Entries without spaces that contain a dot are treated as translation keys and shown in each player's language, anything else is shown literally.")
                    .defineListAllowEmpty("generatedNamePrefixes", List.of(
                            "buffmobs.elite.generated_prefix.grim", "buffmobs.elite.generated_prefix.dread",
                            "buffmobs.elite.generated_prefix.savage", "buffmobs.elite.generated_prefix.ancient",
                            "buffmobs.elite.generated_prefix.cursed", "buffmobs.elite.generated_prefix.bloodthirsty",
                            "buffmobs.elite.generated_prefix.feral", "buffmobs.elite.generated_prefix.howling",
                            "buffmobs.elite.generated_prefix.iron", "buffmobs.elite.generated_prefix.nightmare",
                            "buffmobs.elite.generated_prefix.rotting", "buffmobs.elite.generated_prefix.shadow",
                            "buffmobs.elite.generated_prefix.silent", "buffmobs.elite.generated_prefix.storm",
                            "buffmobs.elite.generated_prefix.thorn", "buffmobs.elite.generated_prefix.venomous",
                            "buffmobs.elite.generated_prefix.wretched", "buffmobs.elite.generated_prefix.ember",
                            "buffmobs.elite.generated_prefix.frostbitten", "buffmobs.elite.generated_prefix.grave",
                            "buffmobs.elite.generated_prefix.hollow", "buffmobs.elite.generated_prefix.onyx",
                            "buffmobs.elite.generated_prefix.plague", "buffmobs.elite.generated_prefix.ravenous",
                            "buffmobs.elite.generated_prefix.scarred", "buffmobs.elite.generated_prefix.tomb",
                            "buffmobs.elite.generated_prefix.umbral", "buffmobs.elite.generated_prefix.wild"
                    ), o -> o instanceof String);
            generatedNameSuffixes = builder.comment("Titles randomly placed after the mob name in GENERATED mode. Entries without spaces that contain a dot are treated as translation keys and shown in each player's language, anything else is shown literally.")
                    .defineListAllowEmpty("generatedNameSuffixes", List.of(
                            "buffmobs.elite.generated_suffix.the_undying", "buffmobs.elite.generated_suffix.the_merciless",
                            "buffmobs.elite.generated_suffix.the_devourer", "buffmobs.elite.generated_suffix.the_bonecrusher",
                            "buffmobs.elite.generated_suffix.the_soulreaper", "buffmobs.elite.generated_suffix.the_graveborn",
                            "buffmobs.elite.generated_suffix.the_blooddrinker", "buffmobs.elite.generated_suffix.the_plaguebearer",
                            "buffmobs.elite.generated_suffix.the_stormbringer", "buffmobs.elite.generated_suffix.the_nightmare",
                            "buffmobs.elite.generated_suffix.the_silent_death", "buffmobs.elite.generated_suffix.the_mauler",
                            "buffmobs.elite.generated_suffix.the_heartripper", "buffmobs.elite.generated_suffix.the_doomcaller",
                            "buffmobs.elite.generated_suffix.the_shadowstalker", "buffmobs.elite.generated_suffix.the_ironhide",
                            "buffmobs.elite.generated_suffix.the_fang", "buffmobs.elite.generated_suffix.the_carrion_king",
                            "buffmobs.elite.generated_suffix.the_dreadlord", "buffmobs.elite.generated_suffix.the_widowmaker",
                            "buffmobs.elite.generated_suffix.the_howling_terror", "buffmobs.elite.generated_suffix.the_corpsecaller",
                            "buffmobs.elite.generated_suffix.the_voidtouched", "buffmobs.elite.generated_suffix.the_throatripper",
                            "buffmobs.elite.generated_suffix.the_ashen_one", "buffmobs.elite.generated_suffix.the_mooncursed",
                            "buffmobs.elite.generated_suffix.the_skullsplitter", "buffmobs.elite.generated_suffix.the_harbinger"
                    ), o -> o instanceof String);
            particle = builder.comment("Continuous vanilla particle shown around Elite mobs, RANDOM picks one particle per mob").defineEnum("particle", EliteParticle.ENCHANT);
            particleIntervalTicks = builder.comment("Ticks between Elite particle bursts").defineInRange("particleIntervalTicks", 10, 1, 200);
            notifyNearbyPlayers = builder.comment("Notify players within notifyRadius when an Elite mob spawns").define("notifyNearbyPlayers", true);
            notifyRadius = builder.comment("Radius in blocks for Elite spawn notifications, also used to notify when a player first approaches an Elite").defineInRange("notifyRadius", 48.0, 0.0, 256.0);
            bonusRewardsEnabled = builder.comment("Enable bonus rewards for killing Elite mobs, works even when lootXpScaling is disabled").define("bonusRewardsEnabled", true);
            rewardXpMultiplier = builder.comment("XP multiplier applied when an Elite mob dies (5.0 = five times the normal XP), stacked on top of any lootXpScaling bonus").defineInRange("rewardXpMultiplier", 5.0, 1.0, 999999.0);
            rewardExtraLootRolls = builder.comment("Extra loot table rolls granted when an Elite mob dies, added on top of any lootXpScaling rolls").defineInRange("rewardExtraLootRolls", 2, 0, 20);
            builder.pop();
        }
    }

    public static class LootXpScaling {
        public final ModConfigSpec.BooleanValue enabled;
        public final ModConfigSpec.BooleanValue xpScalingEnabled;
        public final ModConfigSpec.DoubleValue maxXpMultiplier;
        public final ModConfigSpec.BooleanValue extraLootRollsEnabled;
        public final ModConfigSpec.IntValue maxExtraLootRolls;

        LootXpScaling(ModConfigSpec.Builder builder) {
            builder.push("lootXpScaling");
            enabled = builder.comment("Enable loot and XP scaling for buffed mobs").define("enabled", false);
            xpScalingEnabled = builder.comment("Scale dropped XP with the mob's effective buff multiplier (base attributes, dimension, preset, day and adaptive scaling)").define("xpScalingEnabled", true);
            maxXpMultiplier = builder.comment("Maximum XP multiplier from effective buff scaling").defineInRange("maxXpMultiplier", 5.0, 1.0, 999999.0);
            extraLootRollsEnabled = builder.comment("Roll the mob loot table additional times based on the mob's effective buff multiplier").define("extraLootRollsEnabled", true);
            maxExtraLootRolls = builder.comment("Maximum number of extra loot rolls per mob (doubled cap during a Blood Moon)").defineInRange("maxExtraLootRolls", 3, 0, 20);
            builder.pop();
        }
    }

    public static class Traits {
        public final ModConfigSpec.BooleanValue enabled;
        public final ModConfigSpec.DoubleValue chance;
        public final ModConfigSpec.IntValue flameWeight;
        public final ModConfigSpec.IntValue frostWeight;
        public final ModConfigSpec.IntValue toxicWeight;
        public final ModConfigSpec.IntValue voltaicWeight;
        public final ModConfigSpec.IntValue vampiricWeight;
        public final ModConfigSpec.IntValue armoredWeight;
        public final ModConfigSpec.IntValue flameFireSeconds;
        public final ModConfigSpec.IntValue frostSlownessSeconds;
        public final ModConfigSpec.IntValue frostSlownessLevel;
        public final ModConfigSpec.IntValue toxicPoisonSeconds;
        public final ModConfigSpec.IntValue toxicPoisonLevel;
        public final ModConfigSpec.DoubleValue voltaicChainChance;
        public final ModConfigSpec.DoubleValue voltaicChainDamage;
        public final ModConfigSpec.DoubleValue voltaicChainRadius;
        public final ModConfigSpec.IntValue voltaicMaxTargets;
        public final ModConfigSpec.DoubleValue vampiricHealPercent;
        public final ModConfigSpec.DoubleValue armoredArmorBonus;
        public final ModConfigSpec.DoubleValue armoredToughnessBonus;
        public final ModConfigSpec.BooleanValue onHitEffectsEnabled;
        public final ModConfigSpec.EnumValue<EliteMobs.EliteParticle> flameParticle;
        public final ModConfigSpec.EnumValue<EliteMobs.EliteParticle> frostParticle;
        public final ModConfigSpec.EnumValue<EliteMobs.EliteParticle> toxicParticle;
        public final ModConfigSpec.EnumValue<EliteMobs.EliteParticle> voltaicParticle;
        public final ModConfigSpec.EnumValue<EliteMobs.EliteParticle> vampiricParticle;
        public final ModConfigSpec.EnumValue<EliteMobs.EliteParticle> armoredParticle;

        Traits(ModConfigSpec.Builder builder) {
            builder.push("traits");
            enabled = builder.comment("Enable elemental traits on buffed mobs (at most one trait per mob)").define("enabled", false);
            chance = builder.comment("Chance for a buffed mob to receive exactly one trait on spawn (0.0-1.0)").defineInRange("chance", 0.15, 0.0, 1.0);
            flameWeight = builder.defineInRange("flameWeight", 20, 0, 1000);
            frostWeight = builder.defineInRange("frostWeight", 20, 0, 1000);
            toxicWeight = builder.defineInRange("toxicWeight", 20, 0, 1000);
            voltaicWeight = builder.defineInRange("voltaicWeight", 15, 0, 1000);
            vampiricWeight = builder.defineInRange("vampiricWeight", 15, 0, 1000);
            armoredWeight = builder.defineInRange("armoredWeight", 10, 0, 1000);
            flameFireSeconds = builder.comment("Seconds the FLAME trait sets its target on fire").defineInRange("flameFireSeconds", 4, 1, 60);
            frostSlownessSeconds = builder.comment("Seconds of Slowness applied by the FROST trait").defineInRange("frostSlownessSeconds", 3, 1, 60);
            frostSlownessLevel = builder.comment("Slowness level applied by the FROST trait (2 = Slowness II)").defineInRange("frostSlownessLevel", 2, 1, 10);
            toxicPoisonSeconds = builder.comment("Seconds of Poison applied by the TOXIC trait").defineInRange("toxicPoisonSeconds", 5, 1, 60);
            toxicPoisonLevel = builder.comment("Poison level applied by the TOXIC trait").defineInRange("toxicPoisonLevel", 1, 1, 10);
            voltaicChainChance = builder.comment("Chance for the VOLTAIC trait to chain extra damage to nearby enemies on hit (0.0-1.0)").defineInRange("voltaicChainChance", 0.35, 0.0, 1.0);
            voltaicChainDamage = builder.comment("Extra damage dealt to each chained enemy by the VOLTAIC trait").defineInRange("voltaicChainDamage", 4.0, 0.0, 999999.0);
            voltaicChainRadius = builder.comment("Radius in blocks around the hit target searched for VOLTAIC chain targets").defineInRange("voltaicChainRadius", 4.0, 0.0, 32.0);
            voltaicMaxTargets = builder.comment("Maximum number of chained enemies per VOLTAIC hit").defineInRange("voltaicMaxTargets", 3, 1, 20);
            vampiricHealPercent = builder.comment("Fraction of dealt damage healed by the VAMPIRIC trait (0.25 = 25%)").defineInRange("vampiricHealPercent", 0.25, 0.0, 1.0);
            armoredArmorBonus = builder.comment("Permanent armor points granted by the ARMORED trait").defineInRange("armoredArmorBonus", 8.0, 0.0, 999999.0);
            armoredToughnessBonus = builder.comment("Permanent armor toughness granted by the ARMORED trait").defineInRange("armoredToughnessBonus", 4.0, 0.0, 999999.0);
            onHitEffectsEnabled = builder.comment("Enable trait on-hit effects (Flame ignites, Frost slows, Toxic poisons, Voltaic chains, Vampiric heals on hit). Separate from the HarmfulEffects category, which applies a random effect from any buffed mob.").define("onHitEffectsEnabled", true);
            flameParticle = builder.comment("Continuous particle shown by mobs with the FLAME trait, NONE disables it and RANDOM picks one particle per mob").defineEnum("flameParticle", EliteMobs.EliteParticle.FLAME);
            frostParticle = builder.comment("Continuous particle shown by mobs with the FROST trait, NONE disables it and RANDOM picks one particle per mob").defineEnum("frostParticle", EliteMobs.EliteParticle.SNOWFLAKE);
            toxicParticle = builder.comment("Continuous particle shown by mobs with the TOXIC trait, NONE disables it and RANDOM picks one particle per mob").defineEnum("toxicParticle", EliteMobs.EliteParticle.HAPPY_VILLAGER);
            voltaicParticle = builder.comment("Continuous particle shown by mobs with the VOLTAIC trait, NONE disables it and RANDOM picks one particle per mob").defineEnum("voltaicParticle", EliteMobs.EliteParticle.ELECTRIC_SPARK);
            vampiricParticle = builder.comment("Continuous particle shown by mobs with the VAMPIRIC trait, NONE disables it and RANDOM picks one particle per mob").defineEnum("vampiricParticle", EliteMobs.EliteParticle.HEART);
            armoredParticle = builder.comment("Continuous particle shown by mobs with the ARMORED trait, NONE disables it and RANDOM picks one particle per mob").defineEnum("armoredParticle", EliteMobs.EliteParticle.NONE);
            builder.pop();
        }
    }

    public static class PackAggression {
        public final ModConfigSpec.BooleanValue enabled;
        public final ModConfigSpec.DoubleValue radius;
        public final ModConfigSpec.IntValue maxPackSize;
        public final ModConfigSpec.IntValue cooldownTicks;

        PackAggression(ModConfigSpec.Builder builder) {
            builder.push("packAggression");
            enabled = builder.comment("Enable pack aggression: nearby mobs of the same type join the fight when a buffed mob is attacked by a player").define("enabled", false);
            radius = builder.comment("Radius in blocks searched for pack members").defineInRange("radius", 16.0, 1.0, 64.0);
            maxPackSize = builder.comment("Maximum number of additional mobs pulled per attack").defineInRange("maxPackSize", 5, 1, 50);
            cooldownTicks = builder.comment("Per-mob cooldown in ticks before it can be pulled by another pack attack").defineInRange("cooldownTicks", 200, 0, 72000);
            builder.pop();
        }
    }

    public static class SpecialAbilities {
        public final ModConfigSpec.BooleanValue enabled;
        public final ModConfigSpec.ConfigValue<List<? extends String>> mobAbilities;
        public final Deathrattle deathrattle;
        public final LastStand lastStand;
        public final Teleport Teleport;
        public final Summon Summon;

        SpecialAbilities(ModConfigSpec.Builder builder) {
            builder.push("specialAbilities");
            enabled = builder.comment("Enable per-mob special abilities").define("enabled", false);
            mobAbilities = builder.comment("Mob-to-ability mapping, format: modid:mob_id:ability_id (deathrattle, laststand, Teleport, Summon)")
                    .defineListAllowEmpty("mobAbilities", new ArrayList<>(), o -> o instanceof String);
            deathrattle = new Deathrattle(builder);
            lastStand = new LastStand(builder);
            Teleport = new Teleport(builder);
            Summon = new Summon(builder);
            builder.pop();
        }

        public static class Deathrattle {
            public final ModConfigSpec.EnumValue<DeathrattleMode> mode;
            public final ModConfigSpec.DoubleValue explosionPower;
            public final ModConfigSpec.BooleanValue explosionBreaksBlocks;
            public final ModConfigSpec.DoubleValue poisonCloudRadius;
            public final ModConfigSpec.IntValue poisonCloudDurationSeconds;
            public final ModConfigSpec.IntValue poisonCloudLevel;
            public final ModConfigSpec.ConfigValue<String> minionEntityId;
            public final ModConfigSpec.IntValue minionMinCount;
            public final ModConfigSpec.IntValue minionMaxCount;

            public enum DeathrattleMode { EXPLOSION, POISON_CLOUD, Summon_MINIONS, RANDOM }

            Deathrattle(ModConfigSpec.Builder builder) {
                builder.push("deathrattle");
                mode = builder.comment("Effect triggered on death: EXPLOSION, POISON_CLOUD, Summon_MINIONS or RANDOM (one random effect per death)").defineEnum("mode", DeathrattleMode.POISON_CLOUD);
                explosionPower = builder.comment("Explosion power used by the EXPLOSION mode").defineInRange("explosionPower", 1.5, 0.0, 20.0);
                explosionBreaksBlocks = builder.comment("Whether the EXPLOSION mode destroys blocks").define("explosionBreaksBlocks", false);
                poisonCloudRadius = builder.comment("Radius of the poison area effect cloud").defineInRange("poisonCloudRadius", 2.5, 0.5, 16.0);
                poisonCloudDurationSeconds = builder.comment("Duration in seconds of the poison area effect cloud").defineInRange("poisonCloudDurationSeconds", 5, 1, 60);
                poisonCloudLevel = builder.comment("Poison level applied by the cloud").defineInRange("poisonCloudLevel", 1, 1, 10);
                minionEntityId = builder.comment("Entity ID summoned by the Summon_MINIONS mode, e.g. minecraft:slime").define("minionEntityId", "minecraft:slime");
                minionMinCount = builder.comment("Minimum minions summoned by the Summon_MINIONS mode").defineInRange("minionMinCount", 1, 0, 10);
                minionMaxCount = builder.comment("Maximum minions summoned by the Summon_MINIONS mode").defineInRange("minionMaxCount", 2, 0, 10);
                builder.pop();
            }
        }

        public static class LastStand {
            public final ModConfigSpec.DoubleValue healthThreshold;
            public final ModConfigSpec.IntValue resistanceLevel;
            public final ModConfigSpec.IntValue resistanceDurationSeconds;

            LastStand(ModConfigSpec.Builder builder) {
                builder.push("lastStand");
                healthThreshold = builder.comment("HP fraction below which Last Stand triggers once per life (0.20 = 20%)").defineInRange("healthThreshold", 0.20, 0.01, 0.99);
                resistanceLevel = builder.comment("Resistance level granted by Last Stand").defineInRange("resistanceLevel", 2, 1, 10);
                resistanceDurationSeconds = builder.comment("Resistance duration in seconds granted by Last Stand").defineInRange("resistanceDurationSeconds", 5, 1, 60);
                builder.pop();
            }
        }

        public static class Teleport {
            public final ModConfigSpec.DoubleValue chance;
            public final ModConfigSpec.IntValue cooldownTicks;
            public final ModConfigSpec.DoubleValue distanceBehindAttacker;

            Teleport(ModConfigSpec.Builder builder) {
                builder.push("Teleport");
                chance = builder.comment("Chance to Teleport behind the attacker when damaged (0.0-1.0)").defineInRange("chance", 0.25, 0.0, 1.0);
                cooldownTicks = builder.comment("Ticks before the mob can Teleport again").defineInRange("cooldownTicks", 100, 0, 72000);
                distanceBehindAttacker = builder.comment("Distance in blocks behind the attacker used as the Teleport destination").defineInRange("distanceBehindAttacker", 2.0, 0.5, 16.0);
                builder.pop();
            }
        }

        public static class Summon {
            public final ModConfigSpec.IntValue intervalTicks;
            public final ModConfigSpec.ConfigValue<String> minionEntityId;
            public final ModConfigSpec.IntValue maxAliveMinions;
            public final ModConfigSpec.BooleanValue onlyInCombat;

            Summon(ModConfigSpec.Builder builder) {
                builder.push("Summon");
                intervalTicks = builder.comment("Ticks between minion summons while the ability is active").defineInRange("intervalTicks", 200, 20, 72000);
                minionEntityId = builder.comment("Entity ID summoned as a minion, e.g. minecraft:zombie").define("minionEntityId", "minecraft:zombie");
                maxAliveMinions = builder.comment("Maximum number of simultaneously alive minions per summoner (0 = no summons)").defineInRange("maxAliveMinions", 3, 0, 20);
                onlyInCombat = builder.comment("Only Summon minions while the mob has an attack target").define("onlyInCombat", true);
                builder.pop();
            }
        }
    }

    public static class BloodMoon {
        public final ModConfigSpec.BooleanValue enabled;
        public final ModConfigSpec.IntValue intervalDays;
        public final ModConfigSpec.DoubleValue extraMultiplier;
        public final ModConfigSpec.BooleanValue glowingMobs;
        public final ModConfigSpec.BooleanValue doubleLootAndXp;
        public final ModConfigSpec.BooleanValue notifyPlayers;
        public final ModConfigSpec.BooleanValue spawnBoostEnabled;
        public final ModConfigSpec.DoubleValue extraSpawnChance;

        BloodMoon(ModConfigSpec.Builder builder) {
            builder.push("bloodMoon");
            enabled = builder.comment("Enable the Blood Moon event (logical only, world time is never changed)").define("enabled", false);
            intervalDays = builder.comment("In-game days between Blood Moon nights").defineInRange("intervalDays", 7, 1, 365);
            extraMultiplier = builder.comment("Extra multiplier applied on top of normal scaling during a Blood Moon").defineInRange("extraMultiplier", 1.5, 1.0, 999999.0);
            glowingMobs = builder.comment("Apply a temporary Glowing effect to buffed mobs during a Blood Moon").define("glowingMobs", false);
            doubleLootAndXp = builder.comment("Double scaled XP and extra loot rolls during a Blood Moon (requires lootXpScaling)").define("doubleLootAndXp", true);
            notifyPlayers = builder.comment("Send a global chat notification to all players when a Blood Moon starts and ends").define("notifyPlayers", true);
            spawnBoostEnabled = builder.comment("Approximate a higher spawn rate during a Blood Moon by occasionally duplicating natural spawns (true spawn-rate changes require a Mixin and are not supported)").define("spawnBoostEnabled", false);
            extraSpawnChance = builder.comment("Chance that a natural spawn during a Blood Moon is duplicated once (0.0-1.0)").defineInRange("extraSpawnChance", 0.25, 0.0, 1.0);
            builder.pop();
        }
    }

    public static class AdaptiveDifficulty {
        public final ModConfigSpec.BooleanValue enabled;
        public final ModConfigSpec.IntValue deathsPerStep;
        public final ModConfigSpec.DoubleValue bonusPerStep;
        public final ModConfigSpec.DoubleValue maxBonus;
        public final ModConfigSpec.BooleanValue killReductionEnabled;
        public final ModConfigSpec.IntValue killsPerReductionStep;
        public final ModConfigSpec.DoubleValue reductionPerStep;

        AdaptiveDifficulty(ModConfigSpec.Builder builder) {
            builder.push("adaptiveDifficulty");
            enabled = builder.comment("Enable adaptive difficulty based on player performance, multiplied on top of day scaling").define("enabled", false);
            deathsPerStep = builder.comment("Player deaths required for each adaptive bonus step").defineInRange("deathsPerStep", 3, 1, 100);
            bonusPerStep = builder.comment("Multiplier bonus added per death step (0.1 = +10%)").defineInRange("bonusPerStep", 0.1, 0.0, 10.0);
            maxBonus = builder.comment("Maximum accumulated adaptive bonus").defineInRange("maxBonus", 2.0, 0.0, 100.0);
            killReductionEnabled = builder.comment("Reduce the adaptive bonus as players kill buffed mobs").define("killReductionEnabled", true);
            killsPerReductionStep = builder.comment("Buffed mob kills required for each reduction step").defineInRange("killsPerReductionStep", 25, 1, 1000);
            reductionPerStep = builder.comment("Multiplier bonus removed per kill step").defineInRange("reductionPerStep", 0.05, 0.0, 10.0);
            builder.pop();
        }
    }
}