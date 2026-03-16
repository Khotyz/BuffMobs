package com.khotyz.buffmobs.config;

import com.khotyz.buffmobs.BuffMobsMod;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class ClothConfigScreen {

    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("BuffMobs Configuration"))
                .setSavingRunnable(() -> BuffMobsMod.LOGGER.info("[BuffMobs] Config saved via Cloth Config"));

        ConfigEntryBuilder eb = builder.entryBuilder();
        BuffMobsConfig cfg = BuffMobsConfig.INSTANCE;

        // ── General ──────────────────────────────────────────────────────────
        ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));
        general.addEntry(eb.startBooleanToggle(Component.literal("Enabled"), cfg.enabled.get())
                .setDefaultValue(true)
                .setSaveConsumer(cfg.enabled::set)
                .build());
        general.addEntry(eb.startBooleanToggle(Component.literal("Visual Effects"), cfg.visualEffects.get())
                .setDefaultValue(true)
                .setSaveConsumer(cfg.visualEffects::set)
                .build());

        // ── Attributes ───────────────────────────────────────────────────────
        ConfigCategory attributes = builder.getOrCreateCategory(Component.literal("Attributes"));
        attributes.addEntry(eb.startDoubleField(Component.literal("Health Multiplier"), cfg.attributes.healthMultiplier.get())
                .setDefaultValue(1.5).setMin(0.1).setMax(10.0)
                .setSaveConsumer(cfg.attributes.healthMultiplier::set).build());
        attributes.addEntry(eb.startDoubleField(Component.literal("Damage Multiplier"), cfg.attributes.damageMultiplier.get())
                .setDefaultValue(1.5).setMin(0.1).setMax(10.0)
                .setSaveConsumer(cfg.attributes.damageMultiplier::set).build());
        attributes.addEntry(eb.startDoubleField(Component.literal("Speed Multiplier"), cfg.attributes.speedMultiplier.get())
                .setDefaultValue(1.0).setMin(0.1).setMax(10.0)
                .setSaveConsumer(cfg.attributes.speedMultiplier::set).build());
        attributes.addEntry(eb.startDoubleField(Component.literal("Attack Speed Multiplier"), cfg.attributes.attackSpeedMultiplier.get())
                .setDefaultValue(1.0).setMin(0.1).setMax(10.0)
                .setSaveConsumer(cfg.attributes.attackSpeedMultiplier::set).build());
        attributes.addEntry(eb.startDoubleField(Component.literal("Armor Addition"), cfg.attributes.armorAddition.get())
                .setDefaultValue(5.0).setMin(0.0).setMax(20.0)
                .setSaveConsumer(cfg.attributes.armorAddition::set).build());
        attributes.addEntry(eb.startDoubleField(Component.literal("Armor Toughness Addition"), cfg.attributes.armorToughnessAddition.get())
                .setDefaultValue(0.0).setMin(0.0).setMax(20.0)
                .setSaveConsumer(cfg.attributes.armorToughnessAddition::set).build());

        // ── Effects ──────────────────────────────────────────────────────────
        ConfigCategory effects = builder.getOrCreateCategory(Component.literal("Status Effects"));
        effects.addEntry(eb.startIntField(Component.literal("Duration (s, -1 = infinite)"), cfg.effects.duration.get())
                .setDefaultValue(-1).setMin(-1).setMax(7200)
                .setSaveConsumer(cfg.effects.duration::set).build());
        effects.addEntry(eb.startIntSlider(Component.literal("Strength Level"), cfg.effects.strengthAmplifier.get(), 0, 10)
                .setDefaultValue(0).setSaveConsumer(cfg.effects.strengthAmplifier::set).build());
        effects.addEntry(eb.startIntSlider(Component.literal("Speed Level"), cfg.effects.speedAmplifier.get(), 0, 10)
                .setDefaultValue(0).setSaveConsumer(cfg.effects.speedAmplifier::set).build());
        effects.addEntry(eb.startIntSlider(Component.literal("Resistance Level"), cfg.effects.resistanceAmplifier.get(), 0, 10)
                .setDefaultValue(0).setSaveConsumer(cfg.effects.resistanceAmplifier::set).build());
        effects.addEntry(eb.startIntSlider(Component.literal("Regeneration Level"), cfg.effects.regenerationAmplifier.get(), 0, 10)
                .setDefaultValue(0).setSaveConsumer(cfg.effects.regenerationAmplifier::set).build());
        effects.addEntry(eb.startIntSlider(Component.literal("Absorption Level"), cfg.effects.absorptionAmplifier.get(), 0, 10)
                .setDefaultValue(0).setSaveConsumer(cfg.effects.absorptionAmplifier::set).build());

        // ── Harmful Effects ──────────────────────────────────────────────────
        ConfigCategory harmful = builder.getOrCreateCategory(Component.literal("Harmful Effects"));
        harmful.addEntry(eb.startBooleanToggle(Component.literal("Enabled"), cfg.harmfulEffects.enabled.get())
                .setDefaultValue(true).setSaveConsumer(cfg.harmfulEffects.enabled::set).build());
        harmful.addEntry(eb.startDoubleField(Component.literal("Chance (0.0-1.0)"), cfg.harmfulEffects.chance.get())
                .setDefaultValue(0.15).setMin(0.0).setMax(1.0)
                .setSaveConsumer(cfg.harmfulEffects.chance::set).build());
        harmful.addEntry(eb.startIntField(Component.literal("Poison Duration (s)"), cfg.harmfulEffects.poisonDuration.get())
                .setDefaultValue(5).setMin(1).setMax(60)
                .setSaveConsumer(cfg.harmfulEffects.poisonDuration::set).build());
        harmful.addEntry(eb.startIntField(Component.literal("Slowness Duration (s)"), cfg.harmfulEffects.slownessDuration.get())
                .setDefaultValue(3).setMin(1).setMax(60)
                .setSaveConsumer(cfg.harmfulEffects.slownessDuration::set).build());
        harmful.addEntry(eb.startIntField(Component.literal("Wither Duration (s)"), cfg.harmfulEffects.witherDuration.get())
                .setDefaultValue(3).setMin(1).setMax(60)
                .setSaveConsumer(cfg.harmfulEffects.witherDuration::set).build());

        // ── Day Scaling ───────────────────────────────────────────────────────
        ConfigCategory dayScaling = builder.getOrCreateCategory(Component.literal("Day Scaling"));
        dayScaling.addEntry(eb.startBooleanToggle(Component.literal("Enabled"), cfg.dayScaling.enabled.get())
                .setDefaultValue(false).setSaveConsumer(cfg.dayScaling.enabled::set).build());
        dayScaling.addEntry(eb.startIntField(Component.literal("Interval (days)"), cfg.dayScaling.interval.get())
                .setDefaultValue(7).setMin(1).setMax(365)
                .setSaveConsumer(cfg.dayScaling.interval::set).build());
        dayScaling.addEntry(eb.startDoubleField(Component.literal("Multiplier per Interval"), cfg.dayScaling.multiplier.get())
                .setDefaultValue(0.1).setMin(0.01).setMax(20.0)
                .setSaveConsumer(cfg.dayScaling.multiplier::set).build());
        dayScaling.addEntry(eb.startDoubleField(Component.literal("Max Multiplier"), cfg.dayScaling.maxMultiplier.get())
                .setDefaultValue(5.0).setMin(1.0).setMax(10.0)
                .setSaveConsumer(cfg.dayScaling.maxMultiplier::set).build());
        dayScaling.addEntry(eb.startBooleanToggle(Component.literal("Show Notifications"), cfg.dayScaling.showNotifications.get())
                .setDefaultValue(true).setSaveConsumer(cfg.dayScaling.showNotifications::set).build());

        // ── Filters ───────────────────────────────────────────────────────────
        ConfigCategory filters = builder.getOrCreateCategory(Component.literal("Filters"));
        filters.addEntry(eb.startBooleanToggle(Component.literal("Mob Filter: Use Whitelist"), cfg.mobFilter.useWhitelist.get())
                .setDefaultValue(false).setSaveConsumer(cfg.mobFilter.useWhitelist::set).build());
        filters.addEntry(eb.startStrList(Component.literal("Mob Blacklist"), new ArrayList<>(cfg.mobFilter.blacklist.get()))
                .setDefaultValue(List.of("minecraft:warden"))
                .setSaveConsumer(v -> cfg.mobFilter.blacklist.set(v)).build());
        filters.addEntry(eb.startStrList(Component.literal("Mob Whitelist"), new ArrayList<>(cfg.mobFilter.whitelist.get()))
                .setDefaultValue(new ArrayList<>())
                .setSaveConsumer(v -> cfg.mobFilter.whitelist.set(v)).build());
        filters.addEntry(eb.startBooleanToggle(Component.literal("Mod Filter: Use Whitelist"), cfg.modidFilter.useWhitelist.get())
                .setDefaultValue(false).setSaveConsumer(cfg.modidFilter.useWhitelist::set).build());
        filters.addEntry(eb.startBooleanToggle(Component.literal("Dimension Filter: Use Whitelist"), cfg.dimensionFilter.useWhitelist.get())
                .setDefaultValue(false).setSaveConsumer(cfg.dimensionFilter.useWhitelist::set).build());

        // ── Ranged Melee Switching ────────────────────────────────────────────
        ConfigCategory ranged = builder.getOrCreateCategory(Component.literal("Ranged Behavior"));
        ranged.addEntry(eb.startBooleanToggle(Component.literal("Enabled"), cfg.rangedMeleeSwitching.enabled.get())
                .setDefaultValue(true).setSaveConsumer(cfg.rangedMeleeSwitching.enabled::set).build());
        ranged.addEntry(eb.startEnumSelector(Component.literal("Behavior Mode"),
                        BuffMobsConfig.RangedMeleeSwitching.BehaviorMode.class,
                        cfg.rangedMeleeSwitching.behaviorMode.get())
                .setDefaultValue(BuffMobsConfig.RangedMeleeSwitching.BehaviorMode.RANDOM)
                .setSaveConsumer(cfg.rangedMeleeSwitching.behaviorMode::set).build());
        ranged.addEntry(eb.startDoubleField(Component.literal("Switch Distance"), cfg.rangedMeleeSwitching.switchDistance.get())
                .setDefaultValue(4.0).setMin(1.0).setMax(16.0)
                .setSaveConsumer(cfg.rangedMeleeSwitching.switchDistance::set).build());
        ranged.addEntry(eb.startBooleanToggle(Component.literal("Enchantments Enabled"), cfg.rangedMeleeSwitching.enchantmentsEnabled.get())
                .setDefaultValue(true).setSaveConsumer(cfg.rangedMeleeSwitching.enchantmentsEnabled::set).build());
        ranged.addEntry(eb.startIntSlider(Component.literal("Max Enchantments Per Weapon"), cfg.rangedMeleeSwitching.maxEnchantmentsPerWeapon.get(), 1, 4)
                .setDefaultValue(2).setSaveConsumer(cfg.rangedMeleeSwitching.maxEnchantmentsPerWeapon::set).build());

        // ── CombatDraft ───────────────────────────────────────────────────────
        ConfigCategory draft = builder.getOrCreateCategory(Component.literal("CombatDraft"));
        draft.addEntry(eb.startBooleanToggle(Component.literal("Enabled"), cfg.combatDraft.enabled.get())
                .setDefaultValue(true).setSaveConsumer(cfg.combatDraft.enabled::set).build());
        draft.addEntry(eb.startDoubleField(Component.literal("Health Threshold (0.0-1.0)"), cfg.combatDraft.healthThreshold.get())
                .setDefaultValue(0.20).setMin(0.01).setMax(0.99)
                .setSaveConsumer(cfg.combatDraft.healthThreshold::set).build());
        draft.addEntry(eb.startIntSlider(Component.literal("Regen Amplifier"), cfg.combatDraft.regenAmplifier.get(), 1, 10)
                .setDefaultValue(4).setSaveConsumer(cfg.combatDraft.regenAmplifier::set).build());
        draft.addEntry(eb.startIntField(Component.literal("Regen Duration (s)"), cfg.combatDraft.regenDuration.get())
                .setDefaultValue(10).setMin(1).setMax(120)
                .setSaveConsumer(cfg.combatDraft.regenDuration::set).build());
        draft.addEntry(eb.startIntField(Component.literal("Cooldown (ticks)"), cfg.combatDraft.cooldownTicks.get())
                .setDefaultValue(600).setMin(20).setMax(72000)
                .setSaveConsumer(cfg.combatDraft.cooldownTicks::set).build());
        draft.addEntry(eb.startIntField(Component.literal("Max Uses (0 = unlimited)"), cfg.combatDraft.maxUses.get())
                .setDefaultValue(0).setMin(0).setMax(100)
                .setSaveConsumer(cfg.combatDraft.maxUses::set).build());
        draft.addEntry(eb.startBooleanToggle(Component.literal("Use Whitelist"), cfg.combatDraft.useWhitelist.get())
                .setDefaultValue(false).setSaveConsumer(cfg.combatDraft.useWhitelist::set).build());
        draft.addEntry(eb.startStrList(Component.literal("Whitelist"), new ArrayList<>(cfg.combatDraft.whitelist.get()))
                .setDefaultValue(new ArrayList<>())
                .setSaveConsumer(v -> cfg.combatDraft.whitelist.set(v)).build());
        draft.addEntry(eb.startStrList(Component.literal("Blacklist"), new ArrayList<>(cfg.combatDraft.blacklist.get()))
                .setDefaultValue(new ArrayList<>())
                .setSaveConsumer(v -> cfg.combatDraft.blacklist.set(v)).build());

        return builder.build();
    }
}
