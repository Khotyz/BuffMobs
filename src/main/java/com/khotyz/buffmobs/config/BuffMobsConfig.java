package com.khotyz.buffmobs.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.List;

@Config(name = "buffmobs")
public class BuffMobsConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean enabled = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean visualEffects = true;

    @ConfigEntry.Gui.CollapsibleObject
    public DayScaling dayScaling = new DayScaling();

    @ConfigEntry.Gui.CollapsibleObject
    public Attributes attributes = new Attributes();

    @ConfigEntry.Gui.CollapsibleObject
    public Effects effects = new Effects();

    @ConfigEntry.Gui.CollapsibleObject
    public HarmfulEffects harmfulEffects = new HarmfulEffects();

    @ConfigEntry.Gui.CollapsibleObject
    public VisualBuffEffects visualBuffEffects = new VisualBuffEffects();

    @ConfigEntry.Gui.CollapsibleObject
    public DimensionScaling dimensionScaling = new DimensionScaling();

    @ConfigEntry.Gui.CollapsibleObject
    public MobFilter mobFilter = new MobFilter();

    @ConfigEntry.Gui.CollapsibleObject
    public ModIdFilter modidFilter = new ModIdFilter();

    @ConfigEntry.Gui.CollapsibleObject
    public DimensionFilter dimensionFilter = new DimensionFilter();

    @ConfigEntry.Gui.CollapsibleObject
    public RangedMeleeSwitching rangedMeleeSwitching = new RangedMeleeSwitching();

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

    public static class VisualBuffEffects {
        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean healthEffect = true;

        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean damageEffect = true;

        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean attackSpeedEffect = true;
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
        public List<String> whitelist = List.of("minecraft");

        @ConfigEntry.Gui.Tooltip(count = 3)
        public List<String> blacklist = new ArrayList<>();
    }

    public static class DimensionFilter {
        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean useWhitelist = false;

        @ConfigEntry.Gui.Tooltip(count = 4)
        public List<String> whitelist = List.of("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end");

        @ConfigEntry.Gui.Tooltip(count = 3)
        public List<String> blacklist = new ArrayList<>();
    }

    public static class RangedMeleeSwitching {
        @ConfigEntry.Gui.Tooltip(count = 3)
        public boolean enabled = true;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
        @ConfigEntry.Gui.Tooltip(count = 2)
        public double switchDistance = 4.0;

        @ConfigEntry.BoundedDiscrete(min = 5, max = 20)
        @ConfigEntry.Gui.Tooltip(count = 2)
        public double meleeSpeedMultiplier = 0.9;

        @ConfigEntry.Gui.Tooltip(count = 3)
        public List<String> customRangedMobs = new ArrayList<>();

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

        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean enchantmentsEnabled = true;

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