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

    // General
    public final ModConfigSpec.BooleanValue enabled;
    public final ModConfigSpec.BooleanValue visualEffects;

    // Day Scaling
    public final DayScaling dayScaling;
    // Attributes
    public final Attributes attributes;
    // Effects
    public final Effects effects;
    // Harmful Effects
    public final HarmfulEffects harmfulEffects;
    // Dimension Scaling
    public final DimensionScaling dimensionScaling;
    // Filters
    public final MobFilter mobFilter;
    public final ModIdFilter modidFilter;
    public final DimensionFilter dimensionFilter;
    // Combat
    public final RangedMeleeSwitching rangedMeleeSwitching;
    // CombatDraft
    public final CombatDraft combatDraft;
    // Presets
    public final MobPresets mobPresets;
    // Passive Mob Aggression
    public final PassiveMobAggression passiveMobAggression;
    // Zombie Handling
    public final ZombieHandling zombieHandling;
    // Health Sync
    public final HealthSync healthSync;
    // Dimension Max Health
    public final DimensionMaxHealth dimensionMaxHealth;

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

        HarmfulEffects(ModConfigSpec.Builder builder) {
            builder.push("harmfulEffects");
            enabled = builder.comment("Enable harmful effects applied to players on hit").define("enabled", true);
            chance = builder.comment("Chance of applying a harmful effect per hit (0.0-1.0)").defineInRange("chance", 0.15, 0.0, 1.0);
            poisonDuration = builder.comment("Poison duration in seconds").defineInRange("poisonDuration", 5, 1, 60);
            slownessDuration = builder.comment("Slowness duration in seconds").defineInRange("slownessDuration", 3, 1, 60);
            witherDuration = builder.comment("Wither duration in seconds").defineInRange("witherDuration", 3, 1, 60);
            builder.pop();
        }
    }

    public static class DimensionScaling {
        public final DimensionSlot slot1, slot2, slot3, slot4, slot5;
        public final ModConfigSpec.EnumValue<Mode> mode;

        public enum Mode { SCALING, OVERRIDE }

        DimensionScaling(ModConfigSpec.Builder builder) {
            builder.push("dimensionScaling");
            slot1 = new DimensionSlot(builder, "slot1");
            slot2 = new DimensionSlot(builder, "slot2");
            slot3 = new DimensionSlot(builder, "slot3");
            slot4 = new DimensionSlot(builder, "slot4");
            slot5 = new DimensionSlot(builder, "slot5");
            mode = builder.comment("Dimension scaling mode: SCALING applies as multiplier, OVERRIDE replaces attributes with dimension values.")
                    .defineEnum("mode", Mode.SCALING);
            builder.pop();
        }

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
            overrideDimensionScaling = builder.comment("If true, mobPresets values override dimensionScaling multipliers instead of stacking with them")
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
        public final ModConfigSpec.BooleanValue enabled;
        public final ModConfigSpec.EnumValue<Mode> mode;
        public final ModConfigSpec.DoubleValue baseDamage;
        public final ModConfigSpec.BooleanValue scaleWithHealth;
        public final ModConfigSpec.DoubleValue healthScaleFactor;
        public final ModConfigSpec.ConfigValue<List<? extends String>> whitelist;
        public final ModConfigSpec.ConfigValue<List<? extends String>> blacklist;

        public enum Mode { OFF, NEUTRAL, HOSTILE }

        PassiveMobAggression(ModConfigSpec.Builder builder) {
            builder.push("passiveMobAggression");
            enabled = builder.comment("Enable Passive Mob Aggression: passive mobs retaliate when hit").define("enabled", false);
            mode = builder.comment("Passive mob aggression mode: OFF disables, NEUTRAL retaliates when hit, HOSTILE attacks players on sight.")
                    .defineEnum("mode", Mode.NEUTRAL);
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
                            "If true, removes vanilla's leader zombie bonus health entirely.")
                    .define("disableLeaderZombies", false);
            excludeLeaderBonusFromMultiplier = builder.comment(
                            "If true, the leader zombie bonus health is not multiplied by BuffMobs' health scaling.")
                    .define("excludeLeaderBonusFromMultiplier", true);
            builder.pop();
        }
    }

    public static class HealthSync {
        public final ModConfigSpec.BooleanValue enabled;
        public final ModConfigSpec.EnumValue<Mode> mode;

        public enum Mode { OVERRIDE, STACK }

        HealthSync(ModConfigSpec.Builder builder) {
            builder.push("healthSync");
            enabled = builder.comment(
                            "If true, a mob's current health is synced to its new max health after BuffMobs applies its buffs.")
                    .define("enabled", true);
            mode = builder.comment(
                            "OVERRIDE: current health is always set to the new max health.\n" +
                                    "STACK: current health increases by the same amount the max health increased, preserving prior damage taken.")
                    .defineEnum("mode", Mode.OVERRIDE);
            builder.pop();
        }
    }

    public static class DimensionMaxHealth {
        public final ModConfigSpec.BooleanValue enabled;
        public final Slot slot1, slot2, slot3, slot4, slot5;

        DimensionMaxHealth(ModConfigSpec.Builder builder) {
            builder.push("dimensionMaxHealth");
            enabled = builder.comment("Enable per-dimension flat max health overrides for mobs").define("enabled", false);
            slot1 = new Slot(builder, "slot1");
            slot2 = new Slot(builder, "slot2");
            slot3 = new Slot(builder, "slot3");
            slot4 = new Slot(builder, "slot4");
            slot5 = new Slot(builder, "slot5");
            builder.pop();
        }

        public static class Slot {
            public final ModConfigSpec.ConfigValue<String> dimensionId;
            public final ModConfigSpec.DoubleValue maxHealth;
            public final ModConfigSpec.BooleanValue useAllowlist;
            public final ModConfigSpec.ConfigValue<List<? extends String>> allowlist;
            public final ModConfigSpec.ConfigValue<List<? extends String>> denylist;

            Slot(ModConfigSpec.Builder builder, String key) {
                builder.push(key);
                dimensionId = builder.comment("Dimension ID this slot applies to, e.g. minecraft:the_end").define("dimensionId", "");
                maxHealth = builder.comment("Flat max health value to apply to matching mobs in this dimension").defineInRange("maxHealth", 20.0, 1.0, 999999.0);
                useAllowlist = builder.comment("If true, only mobs in 'allowlist' are affected; otherwise all mobs except those in 'denylist' are affected")
                        .define("useAllowlist", false);
                allowlist = builder.comment("Mob IDs affected (only used when useAllowlist = true)")
                        .defineListAllowEmpty("allowlist", new ArrayList<>(), o -> o instanceof String);
                denylist = builder.comment("Mob IDs never affected (used when useAllowlist = false)")
                        .defineListAllowEmpty("denylist", new ArrayList<>(), o -> o instanceof String);
                builder.pop();
            }
        }
    }
}