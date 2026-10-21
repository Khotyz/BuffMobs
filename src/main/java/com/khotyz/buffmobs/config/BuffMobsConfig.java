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

    public boolean enabled = true;
    public boolean visualEffects = true;

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
    public PassiveMobAggression passiveMobAggression = new PassiveMobAggression();
    public ZombieHandling zombieHandling = new ZombieHandling();
    public HealthSync healthSync = new HealthSync();
    public DimensionMaxHealth dimensionMaxHealth = new DimensionMaxHealth();
    public MobArmor mobArmor = new MobArmor();
    public EliteMobs eliteMobs = new EliteMobs();
    public LootXpScaling lootXpScaling = new LootXpScaling();
    public Traits traits = new Traits();
    public PackAggression packAggression = new PackAggression();
    public SpecialAbilities specialAbilities = new SpecialAbilities();
    public BloodMoon bloodMoon = new BloodMoon();
    public AdaptiveDifficulty adaptiveDifficulty = new AdaptiveDifficulty();

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
        if (src.passiveMobAggression != null) INSTANCE.passiveMobAggression = src.passiveMobAggression;
        if (src.zombieHandling != null) INSTANCE.zombieHandling = src.zombieHandling;
        if (src.healthSync != null) INSTANCE.healthSync = src.healthSync;
        if (src.dimensionMaxHealth != null) INSTANCE.dimensionMaxHealth = src.dimensionMaxHealth;
        if (src.mobArmor != null) INSTANCE.mobArmor = src.mobArmor;
        if (src.eliteMobs != null) INSTANCE.eliteMobs = src.eliteMobs;
        if (src.lootXpScaling != null) INSTANCE.lootXpScaling = src.lootXpScaling;
        if (src.traits != null) INSTANCE.traits = src.traits;
        if (src.packAggression != null) INSTANCE.packAggression = src.packAggression;
        if (src.specialAbilities != null) INSTANCE.specialAbilities = src.specialAbilities;
        if (src.bloodMoon != null) INSTANCE.bloodMoon = src.bloodMoon;
        if (src.adaptiveDifficulty != null) INSTANCE.adaptiveDifficulty = src.adaptiveDifficulty;
    }

    public static class DayScaling {
        public boolean enabled = false;
        public int interval = 7;
        public double multiplier = 0.1;
        public boolean showNotifications = true;
        public NotificationMode notificationMode = NotificationMode.EVERY_DAY;
        public double maxMultiplier = 5.0;

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
        public boolean poisonEnabled = true;
        public boolean slownessEnabled = true;
        public boolean witherEnabled = true;
    }

    public static class DimensionScaling {
        public DimensionSlot slot1 = new DimensionSlot();
        public DimensionSlot slot2 = new DimensionSlot();
        public DimensionSlot slot3 = new DimensionSlot();
        public DimensionSlot slot4 = new DimensionSlot();
        public DimensionSlot slot5 = new DimensionSlot();
        public Mode mode = Mode.SCALING;

        public enum Mode { SCALING, OVERRIDE }

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
        public boolean overrideDimensionScaling = false;
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

    public static class PassiveMobAggression {
        public Mode mode = Mode.NEUTRAL;
        public double baseDamage = 3.0;
        public boolean scaleWithHealth = false;
        public double healthScaleFactor = 0.1;
        public List<String> whitelist = new ArrayList<>();
        public List<String> blacklist = new ArrayList<>();

        public enum Mode { OFF, NEUTRAL, HOSTILE }
    }

    public static class ZombieHandling {
        public boolean disableLeaderZombies = false;
        public boolean excludeLeaderBonusFromMultiplier = false;
    }

    public static class HealthSync {
        public boolean enabled = true;
        public Mode mode = Mode.OVERRIDE;

        public enum Mode { OVERRIDE, STACK }
    }

    public static class DimensionMaxHealth {
        public boolean enabled = false;
        public Slot slot1 = new Slot();
        public Slot slot2 = new Slot();
        public Slot slot3 = new Slot();
        public Slot slot4 = new Slot();
        public Slot slot5 = new Slot();

        public static class Slot {
            public String dimensionId = "";
            public double maxHealth = 20.0;
            public boolean useAllowlist = false;
            public List<String> allowlist = new ArrayList<>();
            public List<String> denylist = new ArrayList<>();
        }
    }

    public static class MobArmor {
        public boolean enabled = false;
        public double equipChance = 1.0;
        public int leatherUnlockDay = 0;
        public int goldUnlockDay = 7;
        public int ironUnlockDay = 14;
        public int diamondUnlockDay = 30;
        public int netheriteUnlockDay = 60;
        public double leatherWeight = 50.0;
        public double goldWeight = 25.0;
        public double ironWeight = 15.0;
        public double diamondWeight = 4.0;
        public double netheriteWeight = 1.0;
        public boolean enchantmentsEnabled = true;
        public int maxEnchantmentsPerPiece = 2;
        public int daysPerEnchantmentLevel = 7;
        public int protectionUnlockDay = 7;
        public int protectionMaxLevel = 4;
        public int fireProtectionUnlockDay = 14;
        public int fireProtectionMaxLevel = 4;
        public int blastProtectionUnlockDay = 21;
        public int blastProtectionMaxLevel = 4;
        public int projectileProtectionUnlockDay = 14;
        public int projectileProtectionMaxLevel = 4;
        public int thornsUnlockDay = 21;
        public int thornsMaxLevel = 3;
        public int unbreakingUnlockDay = 0;
        public int unbreakingMaxLevel = 3;
        public int respirationUnlockDay = 14;
        public int respirationMaxLevel = 3;
        public int aquaAffinityUnlockDay = 21;
        public int aquaAffinityMaxLevel = 1;
        public int featherFallingUnlockDay = 14;
        public int featherFallingMaxLevel = 4;
        public int depthStriderUnlockDay = 21;
        public int depthStriderMaxLevel = 3;
        public int frostWalkerUnlockDay = 28;
        public int frostWalkerMaxLevel = 2;
        public boolean cursesEnabled = false;
        public double curseChance = 0.1;
        public int curseUnlockDay = 30;
        public boolean bindingCurseEnabled = true;
        public boolean vanishingCurseEnabled = true;
        public EquipmentSetupMode setupMode = EquipmentSetupMode.FULL_SET;
        public boolean mixedTiersEnabled = false;
        public double helmetChance = 1.0;
        public double chestplateChance = 1.0;
        public double leggingsChance = 1.0;
        public double bootsChance = 1.0;
        public int minPieces = 1;
        public int maxPieces = 4;
        public boolean dropChanceEnabled = false;
        public double dropChance = 0.085;
        public double enchantedPieceChance = 1.0;
        public List<String> additionalArmorMobs = new ArrayList<>();

        public enum EquipmentSetupMode { FULL_SET, RANDOM_PIECES }
    }

    public static class EliteMobs {
        public boolean enabled = false;
        public double baseChance = 0.02;
        public double chanceIncreasePerDay = 0.001;
        public double maxChance = 0.25;
        public double healthMultiplier = 3.0;
        public double damageMultiplier = 2.0;
        public NameColor nameColor = NameColor.GOLD;
        public String customNameColor = "#FFAA00";
        public NamePrefix namePrefix = NamePrefix.RANDOM;
        public NameMode nameMode = NameMode.GENERATED;
        public boolean useGeneratedPrefix = true;
        public boolean useGeneratedSuffix = true;
        public List<String> generatedNamePrefixes = new ArrayList<>(List.of(
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
        ));
        public List<String> generatedNameSuffixes = new ArrayList<>(List.of(
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
        ));
        public EliteParticle particle = EliteParticle.ENCHANT;
        public int particleIntervalTicks = 10;
        public boolean notifyNearbyPlayers = true;
        public double notifyRadius = 48.0;
        public boolean bonusRewardsEnabled = true;
        public double rewardXpMultiplier = 5.0;
        public int rewardExtraLootRolls = 2;

        public enum NameColor { GOLD, RED, DARK_RED, LIGHT_PURPLE, AQUA, YELLOW, RANDOM, CUSTOM }

        public enum NamePrefix { elite, CHAMPION, BRUTE, DREAD, RANDOM }

        public enum NameMode { GENERATED, PREFIX }

        public enum EliteParticle { ENCHANT, FLAME, SOUL_FIRE_FLAME, PORTAL, END_ROD, HAPPY_VILLAGER, HEART, SNOWFLAKE, ELECTRIC_SPARK, RANDOM, NONE }
    }

    public static class LootXpScaling {
        public boolean enabled = false;
        public boolean xpScalingEnabled = true;
        public double maxXpMultiplier = 5.0;
        public boolean extraLootRollsEnabled = true;
        public int maxExtraLootRolls = 3;
    }

    public static class Traits {
        public boolean enabled = false;
        public double chance = 0.15;
        public int flameWeight = 20;
        public int frostWeight = 20;
        public int toxicWeight = 20;
        public int voltaicWeight = 15;
        public int vampiricWeight = 15;
        public int armoredWeight = 10;
        public int flameFireSeconds = 4;
        public int frostSlownessSeconds = 3;
        public int frostSlownessLevel = 2;
        public int toxicPoisonSeconds = 5;
        public int toxicPoisonLevel = 1;
        public double voltaicChainChance = 0.35;
        public double voltaicChainDamage = 4.0;
        public double voltaicChainRadius = 4.0;
        public int voltaicMaxTargets = 3;
        public double vampiricHealPercent = 0.25;
        public double armoredArmorBonus = 8.0;
        public double armoredToughnessBonus = 4.0;
        public boolean onHitEffectsEnabled = true;
        public EliteMobs.EliteParticle flameParticle = EliteMobs.EliteParticle.FLAME;
        public EliteMobs.EliteParticle frostParticle = EliteMobs.EliteParticle.SNOWFLAKE;
        public EliteMobs.EliteParticle toxicParticle = EliteMobs.EliteParticle.HAPPY_VILLAGER;
        public EliteMobs.EliteParticle voltaicParticle = EliteMobs.EliteParticle.ELECTRIC_SPARK;
        public EliteMobs.EliteParticle vampiricParticle = EliteMobs.EliteParticle.HEART;
        public EliteMobs.EliteParticle armoredParticle = EliteMobs.EliteParticle.NONE;
    }

    public static class PackAggression {
        public boolean enabled = false;
        public double radius = 16.0;
        public int maxPackSize = 5;
        public int cooldownTicks = 200;
    }

    public static class SpecialAbilities {
        public boolean enabled = false;
        public List<String> mobAbilities = new ArrayList<>();
        public Deathrattle deathrattle = new Deathrattle();
        public LastStand lastStand = new LastStand();
        public Teleport Teleport = new Teleport();
        public Summon Summon = new Summon();

        public static class Deathrattle {
            public DeathrattleMode mode = DeathrattleMode.POISON_CLOUD;
            public double explosionPower = 1.5;
            public boolean explosionBreaksBlocks = false;
            public double poisonCloudRadius = 2.5;
            public int poisonCloudDurationSeconds = 5;
            public int poisonCloudLevel = 1;
            public String minionEntityId = "minecraft:slime";
            public int minionMinCount = 1;
            public int minionMaxCount = 2;

            public enum DeathrattleMode { EXPLOSION, POISON_CLOUD, Summon_MINIONS, RANDOM }
        }

        public static class LastStand {
            public double healthThreshold = 0.20;
            public int resistanceLevel = 2;
            public int resistanceDurationSeconds = 5;
        }

        public static class Teleport {
            public double chance = 0.25;
            public int cooldownTicks = 100;
            public double distanceBehindAttacker = 2.0;
        }

        public static class Summon {
            public int intervalTicks = 200;
            public String minionEntityId = "minecraft:zombie";
            public int maxAliveMinions = 3;
            public boolean onlyInCombat = true;
        }
    }

    public static class BloodMoon {
        public boolean enabled = false;
        public int intervalDays = 7;
        public double extraMultiplier = 1.5;
        public boolean glowingMobs = false;
        public boolean doubleLootAndXp = true;
        public boolean notifyPlayers = true;
        public boolean spawnBoostEnabled = false;
        public double extraSpawnChance = 0.25;
    }

    public static class AdaptiveDifficulty {
        public boolean enabled = false;
        public int deathsPerStep = 3;
        public double bonusPerStep = 0.1;
        public double maxBonus = 2.0;
        public boolean killReductionEnabled = true;
        public int killsPerReductionStep = 25;
        public double reductionPerStep = 0.05;
    }
}