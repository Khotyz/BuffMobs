package com.khotyz.buffmobs.config;

import com.khotyz.buffmobs.BuffMobsMod;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ClothConfigScreen {

    private static Component tt(String text) {
        return Component.literal(text);
    }

    public static Screen create(Screen parent) {
        // Always re-read from disk so the screen reflects the saved state,
        // not whatever was in memory since server init.
        BuffMobsConfig.load();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("buffmobs.config.title"))
                .setSavingRunnable(() -> {
                    BuffMobsConfig.save();
                    BuffMobsMod.LOGGER.info("[BuffMobs] Config saved via Cloth Config");
                });

        ConfigEntryBuilder eb = builder.entryBuilder();
        BuffMobsConfig cfg = BuffMobsConfig.INSTANCE;

        // ── General ──────────────────────────────────────────────────────────
        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("buffmobs.config.general"));
        general.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.general.enabled"), cfg.enabled)
                .setDefaultValue(true)
                .setTooltip(tt("Enable or disable the mod entirely. When disabled, all mobs lose their buffs."))
                .setSaveConsumer(v -> cfg.enabled = v).build());
        general.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.general.visualEffects"), cfg.visualEffects)
                .setDefaultValue(true)
                .setTooltip(tt("Show particle effects on mobs that have status effects applied."))
                .setSaveConsumer(v -> cfg.visualEffects = v).build());

        // ── Attributes ───────────────────────────────────────────────────────
        ConfigCategory attributes = builder.getOrCreateCategory(Component.translatable("buffmobs.config.attributes"));
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.healthMultiplier"), cfg.attributes.healthMultiplier)
                .setDefaultValue(1.5).setMin(0.1).setMax(10.0)
                .setTooltip(tt("Multiplies the base max health of all buffed mobs. 1.5 = 50% more HP."))
                .setSaveConsumer(v -> cfg.attributes.healthMultiplier = v).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.damageMultiplier"), cfg.attributes.damageMultiplier)
                .setDefaultValue(1.5).setMin(0.1).setMax(10.0)
                .setTooltip(tt("Multiplies the base attack damage of all buffed mobs. 1.5 = 50% more damage."))
                .setSaveConsumer(v -> cfg.attributes.damageMultiplier = v).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.speedMultiplier"), cfg.attributes.speedMultiplier)
                .setDefaultValue(1.0).setMin(0.1).setMax(10.0)
                .setTooltip(tt("Multiplies movement speed. Capped internally at 2x to avoid unhittable mobs."))
                .setSaveConsumer(v -> cfg.attributes.speedMultiplier = v).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.attackSpeedMultiplier"), cfg.attributes.attackSpeedMultiplier)
                .setDefaultValue(1.0).setMin(0.1).setMax(10.0)
                .setTooltip(tt("Multiplies the base attack speed of mobs."))
                .setSaveConsumer(v -> cfg.attributes.attackSpeedMultiplier = v).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.armorAddition"), cfg.attributes.armorAddition)
                .setDefaultValue(5.0).setMin(0.0).setMax(20.0)
                .setTooltip(tt("Flat armor points added to mobs. 20 = full diamond armor equivalent."))
                .setSaveConsumer(v -> cfg.attributes.armorAddition = v).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.armorToughnessAddition"), cfg.attributes.armorToughnessAddition)
                .setDefaultValue(0.0).setMin(0.0).setMax(20.0)
                .setTooltip(tt("Flat armor toughness added to mobs. Reduces the effectiveness of high-damage hits."))
                .setSaveConsumer(v -> cfg.attributes.armorToughnessAddition = v).build());

        // ── Status Effects ────────────────────────────────────────────────────
        ConfigCategory effects = builder.getOrCreateCategory(Component.translatable("buffmobs.config.effects"));
        effects.addEntry(eb.startIntField(Component.translatable("buffmobs.config.effects.duration"), cfg.effects.duration)
                .setDefaultValue(-1).setMin(-1).setMax(7200)
                .setTooltip(tt("Duration of status effects in seconds. Use -1 for infinite (refreshed every second)."))
                .setSaveConsumer(v -> cfg.effects.duration = v).build());
        // setTextGetter forces Cloth Config 21.11.x to always render the thumb
        // label even when value == min (0), which otherwise leaves the bar blank.
        effects.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.effects.strengthAmplifier"), cfg.effects.strengthAmplifier, 0, 10)
                .setDefaultValue(0)
                .setTextGetter(v -> Component.literal(v == 0 ? "Disabled" : "Level " + v))
                .setTooltip(tt("Strength effect level granted to mobs. 0 = disabled."))
                .setSaveConsumer(v -> cfg.effects.strengthAmplifier = v).build());
        effects.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.effects.speedAmplifier"), cfg.effects.speedAmplifier, 0, 10)
                .setDefaultValue(0)
                .setTextGetter(v -> Component.literal(v == 0 ? "Disabled" : "Level " + v))
                .setTooltip(tt("Speed effect level granted to mobs. 0 = disabled."))
                .setSaveConsumer(v -> cfg.effects.speedAmplifier = v).build());
        effects.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.effects.resistanceAmplifier"), cfg.effects.resistanceAmplifier, 0, 10)
                .setDefaultValue(0)
                .setTextGetter(v -> Component.literal(v == 0 ? "Disabled" : "Level " + v))
                .setTooltip(tt("Resistance effect level granted to mobs. 0 = disabled."))
                .setSaveConsumer(v -> cfg.effects.resistanceAmplifier = v).build());
        effects.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.effects.regenerationAmplifier"), cfg.effects.regenerationAmplifier, 0, 10)
                .setDefaultValue(0)
                .setTextGetter(v -> Component.literal(v == 0 ? "Disabled" : "Level " + v))
                .setTooltip(tt("Regeneration effect level granted to mobs. 0 = disabled. Skipped for undead mobs."))
                .setSaveConsumer(v -> cfg.effects.regenerationAmplifier = v).build());
        effects.addEntry(eb.startIntSlider(Component.literal("Absorption Level"), cfg.effects.absorptionAmplifier, 0, 10)
                .setDefaultValue(0)
                .setTextGetter(v -> Component.literal(v == 0 ? "Disabled" : "Level " + v))
                .setTooltip(tt("Absorption effect level granted to mobs. 0 = disabled."))
                .setSaveConsumer(v -> cfg.effects.absorptionAmplifier = v).build());

        // ── Harmful Effects ──────────────────────────────────────────────────
        ConfigCategory harmful = builder.getOrCreateCategory(Component.translatable("buffmobs.config.harmfulEffects"));
        harmful.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.harmfulEffects.enabled"), cfg.harmfulEffects.enabled)
                .setDefaultValue(true)
                .setTooltip(tt("When enabled, buffed mobs have a chance to apply a harmful effect to the player on hit."))
                .setSaveConsumer(v -> cfg.harmfulEffects.enabled = v).build());
        harmful.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.harmfulEffects.chance"), cfg.harmfulEffects.chance)
                .setDefaultValue(0.15).setMin(0.0).setMax(1.0)
                .setTooltip(tt("Probability of a harmful effect being applied on each hit. 0.15 = 15% chance."))
                .setSaveConsumer(v -> cfg.harmfulEffects.chance = v).build());
        harmful.addEntry(eb.startIntField(Component.translatable("buffmobs.config.harmfulEffects.poisonDuration"), cfg.harmfulEffects.poisonDuration)
                .setDefaultValue(5).setMin(1).setMax(60)
                .setTooltip(tt("How long the Poison effect lasts on the player, in seconds."))
                .setSaveConsumer(v -> cfg.harmfulEffects.poisonDuration = v).build());
        harmful.addEntry(eb.startIntField(Component.translatable("buffmobs.config.harmfulEffects.slownessDuration"), cfg.harmfulEffects.slownessDuration)
                .setDefaultValue(3).setMin(1).setMax(60)
                .setTooltip(tt("How long the Slowness effect lasts on the player, in seconds."))
                .setSaveConsumer(v -> cfg.harmfulEffects.slownessDuration = v).build());
        harmful.addEntry(eb.startIntField(Component.translatable("buffmobs.config.harmfulEffects.witherDuration"), cfg.harmfulEffects.witherDuration)
                .setDefaultValue(3).setMin(1).setMax(60)
                .setTooltip(tt("How long the Wither effect lasts on the player, in seconds."))
                .setSaveConsumer(v -> cfg.harmfulEffects.witherDuration = v).build());

        // ── Day Scaling ───────────────────────────────────────────────────────
        ConfigCategory dayScaling = builder.getOrCreateCategory(Component.translatable("buffmobs.config.dayScaling"));
        dayScaling.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.dayScaling.enabled"), cfg.dayScaling.enabled)
                .setDefaultValue(false)
                .setTooltip(tt("Progressively increases mob strength as in-game days pass."))
                .setSaveConsumer(v -> cfg.dayScaling.enabled = v).build());
        dayScaling.addEntry(eb.startIntField(Component.translatable("buffmobs.config.dayScaling.interval"), cfg.dayScaling.interval)
                .setDefaultValue(7).setMin(1).setMax(365)
                .setTooltip(tt("Number of in-game days between each difficulty increase."))
                .setSaveConsumer(v -> cfg.dayScaling.interval = v).build());
        dayScaling.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.dayScaling.multiplier"), cfg.dayScaling.multiplier)
                .setDefaultValue(0.1).setMin(0.01).setMax(20.0)
                .setTooltip(tt("How much the multiplier increases per interval. 0.1 = +10% per interval."))
                .setSaveConsumer(v -> cfg.dayScaling.multiplier = v).build());
        dayScaling.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.dayScaling.maxMultiplier"), cfg.dayScaling.maxMultiplier)
                .setDefaultValue(5.0).setMin(1.0).setMax(10.0)
                .setTooltip(tt("The maximum value the day scaling multiplier can reach."))
                .setSaveConsumer(v -> cfg.dayScaling.maxMultiplier = v).build());
        dayScaling.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.dayScaling.showNotifications"), cfg.dayScaling.showNotifications)
                .setDefaultValue(true)
                .setTooltip(tt("Show a chat message to all players when the difficulty increases."))
                .setSaveConsumer(v -> cfg.dayScaling.showNotifications = v).build());

        // ── Dimension Scaling ─────────────────────────────────────────────────
        ConfigCategory dimScaling = builder.getOrCreateCategory(Component.translatable("buffmobs.config.dimensionScaling"));
        BuffMobsConfig.DimensionScaling.DimensionSlot[] dimSlots = {
                cfg.dimensionScaling.slot1, cfg.dimensionScaling.slot2, cfg.dimensionScaling.slot3,
                cfg.dimensionScaling.slot4, cfg.dimensionScaling.slot5
        };
        String[] dimSlotNames = {"Slot 1", "Slot 2", "Slot 3", "Slot 4", "Slot 5"};
        for (int i = 0; i < dimSlots.length; i++) {
            BuffMobsConfig.DimensionScaling.DimensionSlot slot = dimSlots[i];
            String prefix = dimSlotNames[i];
            var sub = eb.startSubCategory(Component.literal(prefix));
            sub.add(eb.startStrField(Component.literal("Dimension ID"), slot.dimensionName)
                    .setDefaultValue("")
                    .setTooltip(tt("The dimension ID to apply scaling to. Example: minecraft:the_nether"))
                    .setSaveConsumer(v -> slot.dimensionName = v).build());
            sub.add(eb.startIntField(Component.literal("Health % (100 = 1x)"), slot.healthMultiplier)
                    .setDefaultValue(100).setMin(100).setMax(1000)
                    .setTooltip(tt("Health multiplier for this dimension as a percentage. 200 = double HP."))
                    .setSaveConsumer(v -> slot.healthMultiplier = v).build());
            sub.add(eb.startIntField(Component.literal("Damage % (100 = 1x)"), slot.damageMultiplier)
                    .setDefaultValue(100).setMin(100).setMax(1000)
                    .setTooltip(tt("Damage multiplier for this dimension as a percentage."))
                    .setSaveConsumer(v -> slot.damageMultiplier = v).build());
            sub.add(eb.startIntField(Component.literal("Speed % (100 = 1x)"), slot.speedMultiplier)
                    .setDefaultValue(100).setMin(100).setMax(500)
                    .setTooltip(tt("Speed multiplier for this dimension as a percentage."))
                    .setSaveConsumer(v -> slot.speedMultiplier = v).build());
            sub.add(eb.startIntField(Component.literal("Atk Speed % (100 = 1x)"), slot.attackSpeedMultiplier)
                    .setDefaultValue(100).setMin(100).setMax(1000)
                    .setTooltip(tt("Attack speed multiplier for this dimension as a percentage."))
                    .setSaveConsumer(v -> slot.attackSpeedMultiplier = v).build());
            sub.add(eb.startIntField(Component.literal("Armor Addition"), slot.armorAddition)
                    .setDefaultValue(0).setMin(0).setMax(20)
                    .setTooltip(tt("Flat armor points added to mobs in this dimension."))
                    .setSaveConsumer(v -> slot.armorAddition = v).build());
            sub.add(eb.startIntField(Component.literal("Toughness Addition"), slot.armorToughnessAddition)
                    .setDefaultValue(0).setMin(0).setMax(10)
                    .setTooltip(tt("Flat armor toughness added to mobs in this dimension."))
                    .setSaveConsumer(v -> slot.armorToughnessAddition = v).build());
            dimScaling.addEntry(sub.build());
        }

        // ── Mob Filter ────────────────────────────────────────────────────────
        ConfigCategory mobFilter = builder.getOrCreateCategory(Component.translatable("buffmobs.config.mobFilter"));
        mobFilter.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobFilter.useWhitelist"), cfg.mobFilter.useWhitelist)
                .setDefaultValue(false)
                .setTooltip(tt("If true, only mobs listed in the Whitelist will be buffed."))
                .setSaveConsumer(v -> cfg.mobFilter.useWhitelist = v).build());
        mobFilter.addEntry(eb.startStrList(Component.translatable("buffmobs.config.mobFilter.blacklist"), new ArrayList<>(cfg.mobFilter.blacklist))
                .setDefaultValue(List.of("minecraft:warden"))
                .setTooltip(tt("Mob IDs that will never be buffed. Format: minecraft:zombie"))
                .setSaveConsumer(v -> cfg.mobFilter.blacklist = v).build());
        mobFilter.addEntry(eb.startStrList(Component.translatable("buffmobs.config.mobFilter.whitelist"), new ArrayList<>(cfg.mobFilter.whitelist))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("Mob IDs that will be buffed when Use Whitelist is enabled."))
                .setSaveConsumer(v -> cfg.mobFilter.whitelist = v).build());

        // ── Mod ID Filter ─────────────────────────────────────────────────────
        ConfigCategory modFilter = builder.getOrCreateCategory(Component.translatable("buffmobs.config.modidFilter"));
        modFilter.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.modidFilter.useWhitelist"), cfg.modidFilter.useWhitelist)
                .setDefaultValue(false)
                .setTooltip(tt("If true, only mobs from mods in the Whitelist will be buffed."))
                .setSaveConsumer(v -> cfg.modidFilter.useWhitelist = v).build());
        modFilter.addEntry(eb.startStrList(Component.translatable("buffmobs.config.modidFilter.blacklist"), new ArrayList<>(cfg.modidFilter.blacklist))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("Mod IDs whose mobs will never be buffed."))
                .setSaveConsumer(v -> cfg.modidFilter.blacklist = v).build());
        modFilter.addEntry(eb.startStrList(Component.translatable("buffmobs.config.modidFilter.whitelist"), new ArrayList<>(cfg.modidFilter.whitelist))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("Mod IDs whose mobs will be buffed when Use Whitelist is enabled."))
                .setSaveConsumer(v -> cfg.modidFilter.whitelist = v).build());

        // ── Dimension Filter ──────────────────────────────────────────────────
        ConfigCategory dimFilter = builder.getOrCreateCategory(Component.translatable("buffmobs.config.dimensionFilter"));
        dimFilter.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.dimensionFilter.useWhitelist"), cfg.dimensionFilter.useWhitelist)
                .setDefaultValue(false)
                .setTooltip(tt("If true, mobs are only buffed in dimensions listed in the Whitelist."))
                .setSaveConsumer(v -> cfg.dimensionFilter.useWhitelist = v).build());
        dimFilter.addEntry(eb.startStrList(Component.translatable("buffmobs.config.dimensionFilter.blacklist"), new ArrayList<>(cfg.dimensionFilter.blacklist))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("Dimensions where mobs will never be buffed. Format: minecraft:the_nether"))
                .setSaveConsumer(v -> cfg.dimensionFilter.blacklist = v).build());
        dimFilter.addEntry(eb.startStrList(Component.translatable("buffmobs.config.dimensionFilter.whitelist"), new ArrayList<>(cfg.dimensionFilter.whitelist))
                .setDefaultValue(List.of("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"))
                .setTooltip(tt("Dimensions where mobs are buffed when Use Whitelist is enabled."))
                .setSaveConsumer(v -> cfg.dimensionFilter.whitelist = v).build());

        // ── Ranged / Melee Switching ──────────────────────────────────────────
        ConfigCategory ranged = builder.getOrCreateCategory(Component.translatable("buffmobs.config.rangedMeleeSwitching"));
        ranged.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.rangedMeleeSwitching.enabled"), cfg.rangedMeleeSwitching.enabled)
                .setDefaultValue(true)
                .setTooltip(tt("Enables ranged mobs to change behavior when the player gets close."))
                .setSaveConsumer(v -> cfg.rangedMeleeSwitching.enabled = v).build());
        ranged.addEntry(eb.startEnumSelector(Component.literal("Behavior Mode"),
                        BuffMobsConfig.RangedMeleeSwitching.BehaviorMode.class,
                        cfg.rangedMeleeSwitching.behaviorMode)
                .setDefaultValue(BuffMobsConfig.RangedMeleeSwitching.BehaviorMode.RANDOM)
                .setTooltip(tt("MELEE: switch to sword when close. KITE: flee to maintain distance. RANDOM: each mob picks one on spawn."))
                .setSaveConsumer(v -> cfg.rangedMeleeSwitching.behaviorMode = v).build());
        ranged.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.rangedMeleeSwitching.switchDistance"), cfg.rangedMeleeSwitching.switchDistance)
                .setDefaultValue(4.0).setMin(1.0).setMax(16.0)
                .setTooltip(tt("Distance (in blocks) at which the behavior switch triggers."))
                .setSaveConsumer(v -> cfg.rangedMeleeSwitching.switchDistance = v).build());
        ranged.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.rangedMeleeSwitching.meleeSpeedMultiplier"), cfg.rangedMeleeSwitching.meleeSpeedMultiplier)
                .setDefaultValue(0.9).setMin(0.1).setMax(5.0)
                .setTooltip(tt("Speed multiplier applied when a ranged mob switches to melee mode."))
                .setSaveConsumer(v -> cfg.rangedMeleeSwitching.meleeSpeedMultiplier = v).build());
        ranged.addEntry(eb.startStrList(Component.literal("Custom Ranged Mobs"), new ArrayList<>(cfg.rangedMeleeSwitching.customRangedMobs))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("Additional mob IDs to treat as ranged. Format: modid:mob_id"))
                .setSaveConsumer(v -> cfg.rangedMeleeSwitching.customRangedMobs = v).build());

        var weaponSub = eb.startSubCategory(Component.literal("Weapon Unlock Days"));
        weaponSub.add(eb.startIntField(Component.literal("Stone Sword Unlock Day"), cfg.rangedMeleeSwitching.stoneSwordUnlockDay)
                .setDefaultValue(0).setMin(0).setMax(365).setSaveConsumer(v -> cfg.rangedMeleeSwitching.stoneSwordUnlockDay = v).build());
        weaponSub.add(eb.startIntField(Component.literal("Iron Sword Unlock Day"), cfg.rangedMeleeSwitching.ironSwordUnlockDay)
                .setDefaultValue(7).setMin(0).setMax(365).setSaveConsumer(v -> cfg.rangedMeleeSwitching.ironSwordUnlockDay = v).build());
        weaponSub.add(eb.startIntField(Component.literal("Diamond Sword Unlock Day"), cfg.rangedMeleeSwitching.diamondSwordUnlockDay)
                .setDefaultValue(21).setMin(0).setMax(365).setSaveConsumer(v -> cfg.rangedMeleeSwitching.diamondSwordUnlockDay = v).build());
        weaponSub.add(eb.startIntField(Component.literal("Netherite Sword Unlock Day"), cfg.rangedMeleeSwitching.netheriteSwordUnlockDay)
                .setDefaultValue(60).setMin(0).setMax(365).setSaveConsumer(v -> cfg.rangedMeleeSwitching.netheriteSwordUnlockDay = v).build());
        weaponSub.add(eb.startIntField(Component.literal("Golden Axe Unlock Day"), cfg.rangedMeleeSwitching.goldenAxeUnlockDay)
                .setDefaultValue(0).setMin(0).setMax(365).setSaveConsumer(v -> cfg.rangedMeleeSwitching.goldenAxeUnlockDay = v).build());
        weaponSub.add(eb.startIntField(Component.literal("Diamond Axe Unlock Day"), cfg.rangedMeleeSwitching.diamondAxeUnlockDay)
                .setDefaultValue(14).setMin(0).setMax(365).setSaveConsumer(v -> cfg.rangedMeleeSwitching.diamondAxeUnlockDay = v).build());
        weaponSub.add(eb.startIntField(Component.literal("Netherite Axe Unlock Day"), cfg.rangedMeleeSwitching.netheriteAxeUnlockDay)
                .setDefaultValue(45).setMin(0).setMax(365).setSaveConsumer(v -> cfg.rangedMeleeSwitching.netheriteAxeUnlockDay = v).build());
        ranged.addEntry(weaponSub.build());

        var enchantSub = eb.startSubCategory(Component.literal("Enchantments"));
        enchantSub.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.rangedMeleeSwitching.enchantmentsEnabled"), cfg.rangedMeleeSwitching.enchantmentsEnabled)
                .setDefaultValue(true).setSaveConsumer(v -> cfg.rangedMeleeSwitching.enchantmentsEnabled = v).build());
        enchantSub.add(eb.startIntSlider(Component.translatable("buffmobs.config.rangedMeleeSwitching.maxEnchantmentsPerWeapon"), cfg.rangedMeleeSwitching.maxEnchantmentsPerWeapon, 1, 4)
                .setDefaultValue(2).setSaveConsumer(v -> cfg.rangedMeleeSwitching.maxEnchantmentsPerWeapon = v).build());
        enchantSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.daysPerEnchantmentLevel"), cfg.rangedMeleeSwitching.daysPerEnchantmentLevel)
                .setDefaultValue(7).setMin(1).setMax(30).setSaveConsumer(v -> cfg.rangedMeleeSwitching.daysPerEnchantmentLevel = v).build());
        enchantSub.add(eb.startIntField(Component.literal("Sharpness Unlock Day"), cfg.rangedMeleeSwitching.sharpnessUnlockDay)
                .setDefaultValue(0).setMin(0).setMax(365).setSaveConsumer(v -> cfg.rangedMeleeSwitching.sharpnessUnlockDay = v).build());
        enchantSub.add(eb.startIntSlider(Component.literal("Sharpness Max Level"), cfg.rangedMeleeSwitching.sharpnessMaxLevel, 1, 5)
                .setDefaultValue(5).setSaveConsumer(v -> cfg.rangedMeleeSwitching.sharpnessMaxLevel = v).build());
        enchantSub.add(eb.startIntField(Component.literal("Fire Aspect Unlock Day"), cfg.rangedMeleeSwitching.fireAspectUnlockDay)
                .setDefaultValue(14).setMin(0).setMax(365).setSaveConsumer(v -> cfg.rangedMeleeSwitching.fireAspectUnlockDay = v).build());
        enchantSub.add(eb.startIntSlider(Component.literal("Fire Aspect Max Level"), cfg.rangedMeleeSwitching.fireAspectMaxLevel, 1, 2)
                .setDefaultValue(2).setSaveConsumer(v -> cfg.rangedMeleeSwitching.fireAspectMaxLevel = v).build());
        enchantSub.add(eb.startIntField(Component.literal("Knockback Unlock Day"), cfg.rangedMeleeSwitching.knockbackUnlockDay)
                .setDefaultValue(7).setMin(0).setMax(365).setSaveConsumer(v -> cfg.rangedMeleeSwitching.knockbackUnlockDay = v).build());
        enchantSub.add(eb.startIntSlider(Component.literal("Knockback Max Level"), cfg.rangedMeleeSwitching.knockbackMaxLevel, 1, 2)
                .setDefaultValue(2).setSaveConsumer(v -> cfg.rangedMeleeSwitching.knockbackMaxLevel = v).build());
        enchantSub.add(eb.startIntField(Component.literal("Sweeping Edge Unlock Day"), cfg.rangedMeleeSwitching.sweepingEdgeUnlockDay)
                .setDefaultValue(21).setMin(0).setMax(365).setSaveConsumer(v -> cfg.rangedMeleeSwitching.sweepingEdgeUnlockDay = v).build());
        enchantSub.add(eb.startIntSlider(Component.literal("Sweeping Edge Max Level"), cfg.rangedMeleeSwitching.sweepingEdgeMaxLevel, 1, 3)
                .setDefaultValue(3).setSaveConsumer(v -> cfg.rangedMeleeSwitching.sweepingEdgeMaxLevel = v).build());
        ranged.addEntry(enchantSub.build());

        // ── CombatDraft ───────────────────────────────────────────────────────
        ConfigCategory draft = builder.getOrCreateCategory(Component.literal("CombatDraft"));
        draft.addEntry(eb.startBooleanToggle(Component.literal("Enabled"), cfg.combatDraft.enabled)
                .setDefaultValue(true)
                .setTooltip(tt("When enabled, hostile and neutral mobs drink a regeneration potion when they fall below the health threshold."))
                .setSaveConsumer(v -> cfg.combatDraft.enabled = v).build());
        draft.addEntry(eb.startDoubleField(Component.literal("Health Threshold (0.0-1.0)"), cfg.combatDraft.healthThreshold)
                .setDefaultValue(0.20).setMin(0.01).setMax(0.99)
                .setTooltip(tt("Fraction of max HP at which the potion is triggered. 0.20 = triggers below 20% HP."))
                .setSaveConsumer(v -> cfg.combatDraft.healthThreshold = v).build());
        draft.addEntry(eb.startIntSlider(Component.literal("Regen Amplifier"), cfg.combatDraft.regenAmplifier, 1, 10)
                .setDefaultValue(4)
                .setTooltip(tt("Level of the Regeneration effect applied. Undead mobs receive Absorption + direct heal instead."))
                .setSaveConsumer(v -> cfg.combatDraft.regenAmplifier = v).build());
        draft.addEntry(eb.startIntField(Component.literal("Regen Duration (s)"), cfg.combatDraft.regenDuration)
                .setDefaultValue(10).setMin(1).setMax(120)
                .setTooltip(tt("How long the regeneration effect lasts in seconds."))
                .setSaveConsumer(v -> cfg.combatDraft.regenDuration = v).build());
        draft.addEntry(eb.startIntField(Component.literal("Cooldown (ticks)"), cfg.combatDraft.cooldownTicks)
                .setDefaultValue(600).setMin(20).setMax(72000)
                .setTooltip(tt("Ticks between uses of the potion per mob. 600 = 30 seconds."))
                .setSaveConsumer(v -> cfg.combatDraft.cooldownTicks = v).build());
        draft.addEntry(eb.startIntField(Component.literal("Max Uses (0 = unlimited)"), cfg.combatDraft.maxUses)
                .setDefaultValue(0).setMin(0).setMax(100)
                .setTooltip(tt("Maximum number of times a mob can use the potion per life. 0 = no limit."))
                .setSaveConsumer(v -> cfg.combatDraft.maxUses = v).build());

        var draftFilterSub = eb.startSubCategory(Component.literal("Mob Filter"));
        draftFilterSub.add(eb.startBooleanToggle(Component.literal("Use Whitelist"), cfg.combatDraft.useWhitelist)
                .setDefaultValue(false).setSaveConsumer(v -> cfg.combatDraft.useWhitelist = v).build());
        draftFilterSub.add(eb.startStrList(Component.literal("Whitelist"), new ArrayList<>(cfg.combatDraft.whitelist))
                .setDefaultValue(new ArrayList<>()).setSaveConsumer(v -> cfg.combatDraft.whitelist = v).build());
        draftFilterSub.add(eb.startStrList(Component.literal("Blacklist"), new ArrayList<>(cfg.combatDraft.blacklist))
                .setDefaultValue(new ArrayList<>()).setSaveConsumer(v -> cfg.combatDraft.blacklist = v).build());
        draft.addEntry(draftFilterSub.build());

        // ── Mob Presets ───────────────────────────────────────────────────────
        ConfigCategory presets = builder.getOrCreateCategory(Component.translatable("buffmobs.config.mobPresets"));
        presets.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobPresets.enabled"), cfg.mobPresets.enabled)
                .setDefaultValue(false)
                .setTooltip(tt("When enabled, individual mobs can be assigned a preset that overrides the global multipliers."))
                .setSaveConsumer(v -> cfg.mobPresets.enabled = v).build());

        BuffMobsConfig.MobPresets.PresetSlot[] presetSlots = {
                cfg.mobPresets.preset1, cfg.mobPresets.preset2, cfg.mobPresets.preset3,
                cfg.mobPresets.preset4, cfg.mobPresets.preset5
        };
        String[] defaultPresetNames = {"default", "boss", "elite", "weak", ""};
        for (int i = 0; i < presetSlots.length; i++) {
            BuffMobsConfig.MobPresets.PresetSlot p = presetSlots[i];
            int n = i + 1;
            String displayName = (p.presetName == null || p.presetName.isEmpty()) ? "" : " (" + p.presetName + ")";
            var pSub = eb.startSubCategory(Component.literal("Preset " + n + displayName));
            pSub.add(eb.startStrField(Component.literal("Preset Name"), p.presetName == null ? "" : p.presetName)
                    .setDefaultValue(defaultPresetNames[i])
                    .setTooltip(tt("Internal name for this preset. Used in the Mob Mapping list below."))
                    .setSaveConsumer(v -> p.presetName = v).build());
            pSub.add(eb.startDoubleField(Component.literal("Health Multiplier"), p.healthMultiplier)
                    .setDefaultValue(1.0).setMin(0.01).setMax(100.0).setSaveConsumer(v -> p.healthMultiplier = v).build());
            pSub.add(eb.startDoubleField(Component.literal("Damage Multiplier"), p.damageMultiplier)
                    .setDefaultValue(1.0).setMin(0.01).setMax(100.0).setSaveConsumer(v -> p.damageMultiplier = v).build());
            pSub.add(eb.startDoubleField(Component.literal("Speed Multiplier"), p.speedMultiplier)
                    .setDefaultValue(1.0).setMin(0.01).setMax(10.0).setSaveConsumer(v -> p.speedMultiplier = v).build());
            pSub.add(eb.startDoubleField(Component.literal("Attack Speed Multiplier"), p.attackSpeedMultiplier)
                    .setDefaultValue(1.0).setMin(0.01).setMax(10.0).setSaveConsumer(v -> p.attackSpeedMultiplier = v).build());
            pSub.add(eb.startDoubleField(Component.literal("Armor Addition"), p.armorAddition)
                    .setDefaultValue(0.0).setMin(0.0).setMax(30.0).setSaveConsumer(v -> p.armorAddition = v).build());
            pSub.add(eb.startDoubleField(Component.literal("Toughness Addition"), p.armorToughnessAddition)
                    .setDefaultValue(0.0).setMin(0.0).setMax(20.0).setSaveConsumer(v -> p.armorToughnessAddition = v).build());
            presets.addEntry(pSub.build());
        }
        presets.addEntry(eb.startStrList(Component.translatable("buffmobs.config.mobPresets.mobMapping"), new ArrayList<>(cfg.mobPresets.mobMapping))
                .setDefaultValue(List.of("minecraft:zombie:default", "minecraft:skeleton:default",
                        "minecraft:ender_dragon:boss", "minecraft:wither:boss"))
                .setTooltip(tt("Maps mob IDs to preset names. Format: modid:mob_id:preset_name"))
                .setSaveConsumer(v -> cfg.mobPresets.mobMapping = v).build());

        return builder.build();
    }
}