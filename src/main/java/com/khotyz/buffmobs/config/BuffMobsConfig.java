package com.khotyz.buffmobs.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.List;

@Config(name = "buffmobs")
public class BuffMobsConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip(count = 2)
    @ConfigEntry.Category("general")
    public boolean enabled = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    @ConfigEntry.Category("general")
    public boolean visualEffects = true;

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("scaling")
    public DayScaling dayScaling = new DayScaling();

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("attributes")
    public Attributes attributes = new Attributes();

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("attributes")
    public Effects effects = new Effects();

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("attributes")
    public HarmfulEffects harmfulEffects = new HarmfulEffects();

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("scaling")
    public DimensionScaling dimensionScaling = new DimensionScaling();

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("filters")
    public MobFilter mobFilter = new MobFilter();

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("filters")
    public ModIdFilter modidFilter = new ModIdFilter();

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("filters")
    public DimensionFilter dimensionFilter = new DimensionFilter();

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("combat")
    public RangedMeleeSwitching rangedMeleeSwitching = new RangedMeleeSwitching();

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("presets")
    public MobPresets mobPresets = new MobPresets();

    public static class DayScaling {
        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean enabled = false;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 365)
        @ConfigEntry.Gui.Tooltip(count = 3)
        public int interval = 7;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 20)
        @ConfigEntry.Gui.Tooltip(count = 2)
        public double multiplier = 0.1;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 10)
        @ConfigEntry.Gui.Tooltip(count = 2)
        public double maxMultiplier = 5.0;

        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean showNotifications = true;

        @ConfigEntry.Gui.Tooltip(count = 2)
        public NotificationMode notificationMode = NotificationMode.EVERY_DAY;

        public enum NotificationMode {
            EVERY_DAY, SCALING_INCREASE_ONLY
        }
    }

    public static class Attributes {
        @ConfigEntry.BoundedDiscrete(min = 1, max = 10)
        @ConfigEntry.Gui.Tooltip(count = 2)
        public double healthMultiplier = 1.5;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 10)
        @ConfigEntry.Gui.Tooltip(count = 2)
        public double damageMultiplier = 1.5;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 10)
        @ConfigEntry.Gui.Tooltip(count = 2)
        public double speedMultiplier = 1.0;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 10)
        @ConfigEntry.Gui.Tooltip(count = 2)
        public double attackSpeedMultiplier = 1.0;

        @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
        @ConfigEntry.Gui.Tooltip(count = 2)
        public double armorAddition = 5.0;

        @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
        @ConfigEntry.Gui.Tooltip(count = 2)
        public double armorToughnessAddition = 0.0;
    }

    public static class Effects {
        @ConfigEntry.BoundedDiscrete(min = -1, max = 7200)
        @ConfigEntry.Gui.Tooltip(count = 3)
        public int duration = -1;

        @ConfigEntry.BoundedDiscrete(min = 0, max = 10)
        @ConfigEntry.Gui.Tooltip(count = 2)
        public int strengthAmplifier = 0;

        @ConfigEntry.BoundedDiscrete(min = 0, max = 10)
        @ConfigEntry.Gui.Tooltip(count = 2)
        public int speedAmplifier = 0;

        @ConfigEntry.BoundedDiscrete(min = 0, max = 10)
        @ConfigEntry.Gui.Tooltip(count = 2)
        public int resistanceAmplifier = 0;

        @ConfigEntry.BoundedDiscrete(min = 0, max = 10)
        @ConfigEntry.Gui.Tooltip(count = 2)
        public int regenerationAmplifier = 0;
    }

    public static class HarmfulEffects {
        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean enabled = true;

        @ConfigEntry.BoundedDiscrete(min = 0, max = 10)
        @ConfigEntry.Gui.Tooltip(count = 2)
        public double chance = 0.15;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 60)
        @ConfigEntry.Gui.Tooltip(count = 2)
        public int poisonDuration = 5;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 60)
        @ConfigEntry.Gui.Tooltip(count = 2)
        public int slownessDuration = 3;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 60)
        @ConfigEntry.Gui.Tooltip(count = 2)
        public int witherDuration = 3;
    }

    public static class DimensionScaling {
        @ConfigEntry.Gui.CollapsibleObject
        public DimensionSlot slot1 = new DimensionSlot();

        @ConfigEntry.Gui.CollapsibleObject
        public DimensionSlot slot2 = new DimensionSlot();

        @ConfigEntry.Gui.CollapsibleObject
        public DimensionSlot slot3 = new DimensionSlot();

        @ConfigEntry.Gui.CollapsibleObject
        public DimensionSlot slot4 = new DimensionSlot();

        @ConfigEntry.Gui.CollapsibleObject
        public DimensionSlot slot5 = new DimensionSlot();

        public static class DimensionSlot {
            @ConfigEntry.Gui.Tooltip(count = 3)
            public String dimensionName = "";

            @ConfigEntry.BoundedDiscrete(min = 100, max = 1000)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int healthMultiplier = 100;

            @ConfigEntry.BoundedDiscrete(min = 100, max = 1000)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int damageMultiplier = 100;

            @ConfigEntry.BoundedDiscrete(min = 100, max = 500)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int speedMultiplier = 100;

            @ConfigEntry.BoundedDiscrete(min = 100, max = 1000)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int attackSpeedMultiplier = 100;

            @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int armorAddition = 0;

            @ConfigEntry.BoundedDiscrete(min = 0, max = 10)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int armorToughnessAddition = 0;
        }
    }

    public static class MobFilter {
        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean useWhitelist = false;

        @ConfigEntry.Gui.Tooltip(count = 3)
        public List<String> whitelist = new ArrayList<>();

        @ConfigEntry.Gui.Tooltip(count = 3)
        public List<String> blacklist = List.of("minecraft:warden");
    }

    public static class ModIdFilter {
        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean useWhitelist = false;

        @ConfigEntry.Gui.Tooltip(count = 3)
        public List<String> whitelist = new ArrayList<>();

        @ConfigEntry.Gui.Tooltip(count = 3)
        public List<String> blacklist = new ArrayList<>();
    }

    public static class DimensionFilter {
        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean useWhitelist = false;

        @ConfigEntry.Gui.Tooltip(count = 4)
        public List<String> whitelist = List.of(
                "minecraft:overworld", "minecraft:the_nether", "minecraft:the_end");

        @ConfigEntry.Gui.Tooltip(count = 3)
        public List<String> blacklist = new ArrayList<>();
    }

    public static class RangedMeleeSwitching {
        @ConfigEntry.Gui.Tooltip(count = 3)
        public boolean enabled = true;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
        @ConfigEntry.Gui.Tooltip(count = 2)
        public double switchDistance = 4.0;

        @ConfigEntry.Gui.Tooltip(count = 4)
        public BehaviorMode behaviorMode = BehaviorMode.ADAPTIVE;

        @ConfigEntry.Gui.Tooltip(count = 3)
        public List<String> customRangedMobs = new ArrayList<>();

        @ConfigEntry.Gui.CollapsibleObject
        public TacticalMovement tacticalMovement = new TacticalMovement();

        @ConfigEntry.Gui.CollapsibleObject
        public WeaponProgression weaponProgression = new WeaponProgression();

        @ConfigEntry.Gui.CollapsibleObject
        public EnchantmentProgression enchantmentProgression = new EnchantmentProgression();

        public enum BehaviorMode {
            ADAPTIVE,
            MELEE_ONLY,
            RANGED_ONLY,
            RANDOM
        }

        public static class TacticalMovement {
            @ConfigEntry.Gui.Tooltip(count = 3)
            public boolean enabled = true;

            @ConfigEntry.BoundedDiscrete(min = 5, max = 20)
            @ConfigEntry.Gui.Tooltip(count = 3)
            public double maintainDistance = 10.0;

            @ConfigEntry.BoundedDiscrete(min = 10, max = 40)
            @ConfigEntry.Gui.Tooltip(count = 3)
            public double retreatSpeed = 1.6;

            @ConfigEntry.BoundedDiscrete(min = 3, max = 15)
            @ConfigEntry.Gui.Tooltip(count = 3)
            public double panicDistance = 5.0;

            @ConfigEntry.BoundedDiscrete(min = 15, max = 50)
            @ConfigEntry.Gui.Tooltip(count = 3)
            public double panicSpeed = 2.2;

            @ConfigEntry.Gui.Tooltip(count = 3)
            public boolean enableStrafing = true;

            @ConfigEntry.BoundedDiscrete(min = 20, max = 100)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int strafeChance = 40;

            @ConfigEntry.BoundedDiscrete(min = 20, max = 200)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int strafeInterval = 60;

            @ConfigEntry.Gui.Tooltip(count = 3)
            public boolean allowBackwardsMovement = true;

            @ConfigEntry.BoundedDiscrete(min = 5, max = 40)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int pathRecalculationInterval = 15;

            @ConfigEntry.Gui.Tooltip(count = 3)
            public MovementStrategy movementStrategy = MovementStrategy.INTELLIGENT;

            public enum MovementStrategy {
                SIMPLE,
                INTELLIGENT,
                EVASIVE
            }
        }

        public static class WeaponProgression {
            @ConfigEntry.BoundedDiscrete(min = 0, max = 365)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int stoneSwordUnlockDay = 0;

            @ConfigEntry.BoundedDiscrete(min = 0, max = 365)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int ironSwordUnlockDay = 7;

            @ConfigEntry.BoundedDiscrete(min = 0, max = 365)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int diamondSwordUnlockDay = 21;

            @ConfigEntry.BoundedDiscrete(min = 0, max = 365)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int netheriteSwordUnlockDay = 60;

            @ConfigEntry.BoundedDiscrete(min = 0, max = 365)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int goldenAxeUnlockDay = 0;

            @ConfigEntry.BoundedDiscrete(min = 0, max = 365)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int diamondAxeUnlockDay = 14;

            @ConfigEntry.BoundedDiscrete(min = 0, max = 365)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int netheriteAxeUnlockDay = 45;
        }

        public static class EnchantmentProgression {
            @ConfigEntry.Gui.Tooltip(count = 2)
            public boolean enabled = true;

            @ConfigEntry.BoundedDiscrete(min = 1, max = 4)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int maxEnchantmentsPerWeapon = 2;

            @ConfigEntry.BoundedDiscrete(min = 1, max = 30)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int daysPerEnchantmentLevel = 7;

            @ConfigEntry.BoundedDiscrete(min = 0, max = 365)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int sharpnessUnlockDay = 0;

            @ConfigEntry.BoundedDiscrete(min = 1, max = 5)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int sharpnessMaxLevel = 5;

            @ConfigEntry.BoundedDiscrete(min = 0, max = 365)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int fireAspectUnlockDay = 14;

            @ConfigEntry.BoundedDiscrete(min = 1, max = 2)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int fireAspectMaxLevel = 2;

            @ConfigEntry.BoundedDiscrete(min = 0, max = 365)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int knockbackUnlockDay = 7;

            @ConfigEntry.BoundedDiscrete(min = 1, max = 2)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int knockbackMaxLevel = 2;

            @ConfigEntry.BoundedDiscrete(min = 0, max = 365)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int sweepingEdgeUnlockDay = 21;

            @ConfigEntry.BoundedDiscrete(min = 1, max = 3)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public int sweepingEdgeMaxLevel = 3;
        }
    }

    public static class MobPresets {
        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean enabled = false;

        @ConfigEntry.Gui.CollapsibleObject
        public PresetSlot preset1 = new PresetSlot("default", 1.0, 1.0, 1.0, 1.0, 0.0, 0.0);

        @ConfigEntry.Gui.CollapsibleObject
        public PresetSlot preset2 = new PresetSlot("boss", 3.0, 2.5, 1.2, 1.5, 10.0, 5.0);

        @ConfigEntry.Gui.CollapsibleObject
        public PresetSlot preset3 = new PresetSlot("elite", 2.0, 1.8, 1.1, 1.2, 5.0, 2.0);

        @ConfigEntry.Gui.CollapsibleObject
        public PresetSlot preset4 = new PresetSlot("weak", 0.5, 0.5, 0.9, 0.8, 0.0, 0.0);

        @ConfigEntry.Gui.CollapsibleObject
        public PresetSlot preset5 = new PresetSlot("", 1.0, 1.0, 1.0, 1.0, 0.0, 0.0);

        @ConfigEntry.Gui.Tooltip(count = 4)
        public List<String> mobMapping = List.of(
                "minecraft:zombie:default",
                "minecraft:skeleton:default",
                "minecraft:ender_dragon:boss",
                "minecraft:wither:boss"
        );

        public static class PresetSlot {
            @ConfigEntry.Gui.Tooltip(count = 2)
            public String presetName;

            @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public double healthMultiplier;

            @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public double damageMultiplier;

            @ConfigEntry.BoundedDiscrete(min = 1, max = 10)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public double speedMultiplier;

            @ConfigEntry.BoundedDiscrete(min = 1, max = 10)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public double attackSpeedMultiplier;

            @ConfigEntry.BoundedDiscrete(min = 0, max = 30)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public double armorAddition;

            @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
            @ConfigEntry.Gui.Tooltip(count = 2)
            public double armorToughnessAddition;

            public PresetSlot() {
                this("", 1.0, 1.0, 1.0, 1.0, 0.0, 0.0);
            }

            public PresetSlot(String name, double health, double damage, double speed,
                              double attackSpeed, double armor, double toughness) {
                this.presetName = name;
                this.healthMultiplier = health;
                this.damageMultiplier = damage;
                this.speedMultiplier = speed;
                this.attackSpeedMultiplier = attackSpeed;
                this.armorAddition = armor;
                this.armorToughnessAddition = toughness;
            }
        }
    }
}