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

    private static Component tt(String key) {
        return Component.translatable(key);
    }

    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("buffmobs.config.title"))
                .setSavingRunnable(() -> {
                    BuffMobsConfig.save();
                    BuffMobsMod.LOGGER.info("[BuffMobs] Config saved via Cloth Config");
                });

        ConfigEntryBuilder eb = builder.entryBuilder();
        BuffMobsConfig cfg = BuffMobsConfig.INSTANCE;

        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("buffmobs.config.general"));
        general.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.general.enabled"), cfg.enabled)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.general.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.enabled = v).build());
        general.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.general.visualEffects"), cfg.visualEffects)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.general.visualEffects.tooltip"))
                .setSaveConsumer(v -> cfg.visualEffects = v).build());
        general.addEntry(eb.startTextDescription(tt("buffmobs.config.credits.translation")).build());

        buildAttributesCategory(builder, eb, cfg);
        buildZombieHandlingCategory(builder, eb, cfg);
        buildEffectsCategory(builder, eb, cfg);
        buildHarmfulEffectsCategory(builder, eb, cfg);
        buildHealthSyncCategory(builder, eb, cfg);
        buildDayScalingCategory(builder, eb, cfg);
        buildDimensionScalingCategory(builder, eb, cfg);
        buildDimensionMaxHealthCategory(builder, eb, cfg);
        buildMobFilterCategory(builder, eb, cfg);
        buildModIdFilterCategory(builder, eb, cfg);
        buildDimensionFilterCategory(builder, eb, cfg);
        buildRangedMeleeSwitchingCategory(builder, eb, cfg);
        buildCombatDraftCategory(builder, eb, cfg);
        buildMobPresetsCategory(builder, eb, cfg);
        buildPassiveMobAggressionCategory(builder, eb, cfg);

        return builder.build();
    }

    private static void buildAttributesCategory(ConfigBuilder builder, ConfigEntryBuilder eb, BuffMobsConfig cfg) {
        ConfigCategory cat = builder.getOrCreateCategory(Component.translatable("buffmobs.config.attributes"));
        cat.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.healthMultiplier"), cfg.attributes.healthMultiplier)
                .setDefaultValue(1.5).setMin(0.1)
                .setTooltip(tt("buffmobs.config.attributes.healthMultiplier.tooltip"))
                .setSaveConsumer(v -> cfg.attributes.healthMultiplier = v).build());
        cat.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.damageMultiplier"), cfg.attributes.damageMultiplier)
                .setDefaultValue(1.5).setMin(0.1)
                .setTooltip(tt("buffmobs.config.attributes.damageMultiplier.tooltip"))
                .setSaveConsumer(v -> cfg.attributes.damageMultiplier = v).build());
        cat.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.speedMultiplier"), cfg.attributes.speedMultiplier)
                .setDefaultValue(1.0).setMin(0.1)
                .setTooltip(tt("buffmobs.config.attributes.speedMultiplier.tooltip"))
                .setSaveConsumer(v -> cfg.attributes.speedMultiplier = v).build());
        cat.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.attackSpeedMultiplier"), cfg.attributes.attackSpeedMultiplier)
                .setDefaultValue(1.0).setMin(0.1)
                .setTooltip(tt("buffmobs.config.attributes.attackSpeedMultiplier.tooltip"))
                .setSaveConsumer(v -> cfg.attributes.attackSpeedMultiplier = v).build());
        cat.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.armorAddition"), cfg.attributes.armorAddition)
                .setDefaultValue(5.0).setMin(0.0)
                .setTooltip(tt("buffmobs.config.attributes.armorAddition.tooltip"))
                .setSaveConsumer(v -> cfg.attributes.armorAddition = v).build());
        cat.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.armorToughnessAddition"), cfg.attributes.armorToughnessAddition)
                .setDefaultValue(0.0).setMin(0.0)
                .setTooltip(tt("buffmobs.config.attributes.armorToughnessAddition.tooltip"))
                .setSaveConsumer(v -> cfg.attributes.armorToughnessAddition = v).build());
    }

    private static void buildZombieHandlingCategory(ConfigBuilder builder, ConfigEntryBuilder eb, BuffMobsConfig cfg) {
        ConfigCategory cat = builder.getOrCreateCategory(Component.translatable("buffmobs.config.zombieHandling"));
        cat.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.zombieHandling.disableLeaderZombies"), cfg.zombieHandling.disableLeaderZombies)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.zombieHandling.disableLeaderZombies.tooltip"))
                .setSaveConsumer(v -> cfg.zombieHandling.disableLeaderZombies = v).build());
        cat.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.zombieHandling.excludeLeaderBonusFromMultiplier"), cfg.zombieHandling.excludeLeaderBonusFromMultiplier)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.zombieHandling.excludeLeaderBonusFromMultiplier.tooltip"))
                .setSaveConsumer(v -> cfg.zombieHandling.excludeLeaderBonusFromMultiplier = v).build());
    }

    private static void buildEffectsCategory(ConfigBuilder builder, ConfigEntryBuilder eb, BuffMobsConfig cfg) {
        ConfigCategory cat = builder.getOrCreateCategory(Component.translatable("buffmobs.config.effects"));
        cat.addEntry(eb.startIntField(Component.translatable("buffmobs.config.effects.duration"), cfg.effects.duration)
                .setDefaultValue(-1).setMin(-1).setMax(7200)
                .setTooltip(tt("buffmobs.config.effects.duration.tooltip"))
                .setSaveConsumer(v -> cfg.effects.duration = v).build());
        cat.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.effects.strengthAmplifier"), cfg.effects.strengthAmplifier, 0, 10)
                .setDefaultValue(0)
                .setTextGetter(v -> v == 0
                        ? Component.translatable("buffmobs.config.effects.slider.disabled")
                        : Component.translatable("buffmobs.config.effects.slider.level", v))
                .setTooltip(tt("buffmobs.config.effects.strengthAmplifier.tooltip"))
                .setSaveConsumer(v -> cfg.effects.strengthAmplifier = v).build());
        cat.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.effects.speedAmplifier"), cfg.effects.speedAmplifier, 0, 10)
                .setDefaultValue(0)
                .setTextGetter(v -> v == 0
                        ? Component.translatable("buffmobs.config.effects.slider.disabled")
                        : Component.translatable("buffmobs.config.effects.slider.level", v))
                .setTooltip(tt("buffmobs.config.effects.speedAmplifier.tooltip"))
                .setSaveConsumer(v -> cfg.effects.speedAmplifier = v).build());
        cat.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.effects.resistanceAmplifier"), cfg.effects.resistanceAmplifier, 0, 10)
                .setDefaultValue(0)
                .setTextGetter(v -> v == 0
                        ? Component.translatable("buffmobs.config.effects.slider.disabled")
                        : Component.translatable("buffmobs.config.effects.slider.level", v))
                .setTooltip(tt("buffmobs.config.effects.resistanceAmplifier.tooltip"))
                .setSaveConsumer(v -> cfg.effects.resistanceAmplifier = v).build());
        cat.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.effects.regenerationAmplifier"), cfg.effects.regenerationAmplifier, 0, 10)
                .setDefaultValue(0)
                .setTextGetter(v -> v == 0
                        ? Component.translatable("buffmobs.config.effects.slider.disabled")
                        : Component.translatable("buffmobs.config.effects.slider.level", v))
                .setTooltip(tt("buffmobs.config.effects.regenerationAmplifier.tooltip"))
                .setSaveConsumer(v -> cfg.effects.regenerationAmplifier = v).build());
        cat.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.effects.absorptionAmplifier"), cfg.effects.absorptionAmplifier, 0, 10)
                .setDefaultValue(0)
                .setTextGetter(v -> v == 0
                        ? Component.translatable("buffmobs.config.effects.slider.disabled")
                        : Component.translatable("buffmobs.config.effects.slider.level", v))
                .setTooltip(tt("buffmobs.config.effects.absorptionAmplifier.tooltip"))
                .setSaveConsumer(v -> cfg.effects.absorptionAmplifier = v).build());
    }

    private static void buildHarmfulEffectsCategory(ConfigBuilder builder, ConfigEntryBuilder eb, BuffMobsConfig cfg) {
        ConfigCategory cat = builder.getOrCreateCategory(Component.translatable("buffmobs.config.harmfulEffects"));
        cat.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.harmfulEffects.enabled"), cfg.harmfulEffects.enabled)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.harmfulEffects.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.harmfulEffects.enabled = v).build());
        cat.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.harmfulEffects.chance"), cfg.harmfulEffects.chance)
                .setDefaultValue(0.15).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.harmfulEffects.chance.tooltip"))
                .setSaveConsumer(v -> cfg.harmfulEffects.chance = v).build());
        cat.addEntry(eb.startIntField(Component.translatable("buffmobs.config.harmfulEffects.poisonDuration"), cfg.harmfulEffects.poisonDuration)
                .setDefaultValue(5).setMin(1).setMax(60)
                .setTooltip(tt("buffmobs.config.harmfulEffects.poisonDuration.tooltip"))
                .setSaveConsumer(v -> cfg.harmfulEffects.poisonDuration = v).build());
        cat.addEntry(eb.startIntField(Component.translatable("buffmobs.config.harmfulEffects.slownessDuration"), cfg.harmfulEffects.slownessDuration)
                .setDefaultValue(3).setMin(1).setMax(60)
                .setTooltip(tt("buffmobs.config.harmfulEffects.slownessDuration.tooltip"))
                .setSaveConsumer(v -> cfg.harmfulEffects.slownessDuration = v).build());
        cat.addEntry(eb.startIntField(Component.translatable("buffmobs.config.harmfulEffects.witherDuration"), cfg.harmfulEffects.witherDuration)
                .setDefaultValue(3).setMin(1).setMax(60)
                .setTooltip(tt("buffmobs.config.harmfulEffects.witherDuration.tooltip"))
                .setSaveConsumer(v -> cfg.harmfulEffects.witherDuration = v).build());
    }

    private static void buildHealthSyncCategory(ConfigBuilder builder, ConfigEntryBuilder eb, BuffMobsConfig cfg) {
        ConfigCategory cat = builder.getOrCreateCategory(Component.translatable("buffmobs.config.healthSync"));
        cat.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.healthSync.enabled"), cfg.healthSync.enabled)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.healthSync.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.healthSync.enabled = v).build());
        cat.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.healthSync.mode"),
                        BuffMobsConfig.HealthSync.Mode.class,
                        cfg.healthSync.mode)
                .setDefaultValue(BuffMobsConfig.HealthSync.Mode.OVERRIDE)
                .setEnumNameProvider(e -> switch ((BuffMobsConfig.HealthSync.Mode) e) {
                    case OVERRIDE -> Component.translatable("buffmobs.config.healthSync.mode.override");
                    case STACK    -> Component.translatable("buffmobs.config.healthSync.mode.stack");
                })
                .setTooltip(tt("buffmobs.config.healthSync.mode.tooltip"))
                .setSaveConsumer(v -> cfg.healthSync.mode = v).build());
    }

    private static void buildDayScalingCategory(ConfigBuilder builder, ConfigEntryBuilder eb, BuffMobsConfig cfg) {
        ConfigCategory cat = builder.getOrCreateCategory(Component.translatable("buffmobs.config.dayScaling"));
        cat.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.dayScaling.enabled"), cfg.dayScaling.enabled)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.dayScaling.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.dayScaling.enabled = v).build());
        cat.addEntry(eb.startIntField(Component.translatable("buffmobs.config.dayScaling.interval"), cfg.dayScaling.interval)
                .setDefaultValue(7).setMin(1)
                .setTooltip(tt("buffmobs.config.dayScaling.interval.tooltip"))
                .setSaveConsumer(v -> cfg.dayScaling.interval = v).build());
        cat.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.dayScaling.multiplier"), cfg.dayScaling.multiplier)
                .setDefaultValue(0.1).setMin(0.001)
                .setTooltip(tt("buffmobs.config.dayScaling.multiplier.tooltip"))
                .setSaveConsumer(v -> cfg.dayScaling.multiplier = v).build());
        cat.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.dayScaling.maxMultiplier"), cfg.dayScaling.maxMultiplier)
                .setDefaultValue(5.0).setMin(0.0)
                .setTooltip(tt("buffmobs.config.dayScaling.maxMultiplier.tooltip"))
                .setSaveConsumer(v -> cfg.dayScaling.maxMultiplier = v).build());
        cat.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.dayScaling.showNotifications"), cfg.dayScaling.showNotifications)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.dayScaling.showNotifications.tooltip"))
                .setSaveConsumer(v -> cfg.dayScaling.showNotifications = v).build());
        cat.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.dayScaling.notificationMode"),
                        BuffMobsConfig.DayScaling.NotificationMode.class,
                        cfg.dayScaling.notificationMode)
                .setDefaultValue(BuffMobsConfig.DayScaling.NotificationMode.EVERY_DAY)
                .setTooltip(tt("buffmobs.config.dayScaling.notificationMode.tooltip"))
                .setSaveConsumer(v -> cfg.dayScaling.notificationMode = v).build());
    }

    private static void buildDimensionScalingCategory(ConfigBuilder builder, ConfigEntryBuilder eb, BuffMobsConfig cfg) {
        ConfigCategory cat = builder.getOrCreateCategory(Component.translatable("buffmobs.config.dimensionScaling"));
        BuffMobsConfig.DimensionScaling.DimensionSlot[] dimSlots = {
                cfg.dimensionScaling.slot1, cfg.dimensionScaling.slot2, cfg.dimensionScaling.slot3,
                cfg.dimensionScaling.slot4, cfg.dimensionScaling.slot5
        };
        for (int i = 0; i < dimSlots.length; i++) {
            BuffMobsConfig.DimensionScaling.DimensionSlot s = dimSlots[i];
            int n = i + 1;
            var slotSub = eb.startSubCategory(Component.translatable("buffmobs.config.dimensionScaling.slot", n));
            slotSub.add(eb.startStrField(Component.translatable("buffmobs.config.dimensionScaling.dimensionId"), s.dimensionName == null ? "" : s.dimensionName)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.dimensionId.tooltip"))
                    .setSaveConsumer(v -> s.dimensionName = v).build());
            slotSub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.healthPercent"), s.healthMultiplier)
                    .setDefaultValue(100).setMin(1)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.healthPercent.tooltip"))
                    .setSaveConsumer(v -> s.healthMultiplier = v).build());
            slotSub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.damagePercent"), s.damageMultiplier)
                    .setDefaultValue(100).setMin(1)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.damagePercent.tooltip"))
                    .setSaveConsumer(v -> s.damageMultiplier = v).build());
            slotSub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.speedPercent"), s.speedMultiplier)
                    .setDefaultValue(100).setMin(1)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.speedPercent.tooltip"))
                    .setSaveConsumer(v -> s.speedMultiplier = v).build());
            slotSub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.attackSpeedPercent"), s.attackSpeedMultiplier)
                    .setDefaultValue(100).setMin(1)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.attackSpeedPercent.tooltip"))
                    .setSaveConsumer(v -> s.attackSpeedMultiplier = v).build());
            slotSub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.armorAddition"), s.armorAddition)
                    .setDefaultValue(0).setMin(0)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.armorAddition.tooltip"))
                    .setSaveConsumer(v -> s.armorAddition = v).build());
            slotSub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.toughnessAddition"), s.armorToughnessAddition)
                    .setDefaultValue(0).setMin(0)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.toughnessAddition.tooltip"))
                    .setSaveConsumer(v -> s.armorToughnessAddition = v).build());
            cat.addEntry(slotSub.build());
        }
    }

    private static void buildDimensionMaxHealthCategory(ConfigBuilder builder, ConfigEntryBuilder eb, BuffMobsConfig cfg) {
        ConfigCategory cat = builder.getOrCreateCategory(Component.translatable("buffmobs.config.dimensionMaxHealth"));
        cat.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.dimensionMaxHealth.enabled"), cfg.dimensionMaxHealth.enabled)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.dimensionMaxHealth.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.dimensionMaxHealth.enabled = v).build());

        BuffMobsConfig.DimensionMaxHealth.Slot[] slots = {
                cfg.dimensionMaxHealth.slot1, cfg.dimensionMaxHealth.slot2, cfg.dimensionMaxHealth.slot3,
                cfg.dimensionMaxHealth.slot4, cfg.dimensionMaxHealth.slot5
        };
        for (int i = 0; i < slots.length; i++) {
            BuffMobsConfig.DimensionMaxHealth.Slot s = slots[i];
            int n = i + 1;
            var slotSub = eb.startSubCategory(Component.translatable("buffmobs.config.dimensionMaxHealth.slot", n));
            slotSub.add(eb.startStrField(Component.translatable("buffmobs.config.dimensionMaxHealth.dimensionId"), s.dimensionId == null ? "" : s.dimensionId)
                    .setTooltip(tt("buffmobs.config.dimensionMaxHealth.dimensionId.tooltip"))
                    .setSaveConsumer(v -> s.dimensionId = v).build());
            slotSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.dimensionMaxHealth.maxHealth"), s.maxHealth)
                    .setDefaultValue(20.0).setMin(1.0)
                    .setTooltip(tt("buffmobs.config.dimensionMaxHealth.maxHealth.tooltip"))
                    .setSaveConsumer(v -> s.maxHealth = v).build());

            var filterSub = eb.startSubCategory(Component.translatable("buffmobs.config.dimensionMaxHealth.mobFilter"));
            filterSub.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.dimensionMaxHealth.useAllowlist"), s.useAllowlist)
                    .setDefaultValue(false)
                    .setTooltip(tt("buffmobs.config.dimensionMaxHealth.useAllowlist.tooltip"))
                    .setSaveConsumer(v -> s.useAllowlist = v).build());
            filterSub.add(eb.startStrList(Component.translatable("buffmobs.config.dimensionMaxHealth.allowlist"), new ArrayList<>(s.allowlist))
                    .setDefaultValue(new ArrayList<>())
                    .setTooltip(tt("buffmobs.config.dimensionMaxHealth.allowlist.tooltip"))
                    .setSaveConsumer(v -> s.allowlist = v).build());
            filterSub.add(eb.startStrList(Component.translatable("buffmobs.config.dimensionMaxHealth.denylist"), new ArrayList<>(s.denylist))
                    .setDefaultValue(new ArrayList<>())
                    .setTooltip(tt("buffmobs.config.dimensionMaxHealth.denylist.tooltip"))
                    .setSaveConsumer(v -> s.denylist = v).build());
            slotSub.add(filterSub.build());

            cat.addEntry(slotSub.build());
        }
    }

    private static void buildMobFilterCategory(ConfigBuilder builder, ConfigEntryBuilder eb, BuffMobsConfig cfg) {
        ConfigCategory cat = builder.getOrCreateCategory(Component.translatable("buffmobs.config.mobFilter"));
        cat.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobFilter.useWhitelist"), cfg.mobFilter.useWhitelist)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.mobFilter.useWhitelist.tooltip"))
                .setSaveConsumer(v -> cfg.mobFilter.useWhitelist = v).build());
        cat.addEntry(eb.startStrList(Component.translatable("buffmobs.config.mobFilter.whitelist"), new ArrayList<>(cfg.mobFilter.whitelist))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.mobFilter.whitelist.tooltip"))
                .setSaveConsumer(v -> cfg.mobFilter.whitelist = v).build());
        cat.addEntry(eb.startStrList(Component.translatable("buffmobs.config.mobFilter.blacklist"), new ArrayList<>(cfg.mobFilter.blacklist))
                .setDefaultValue(new ArrayList<>(List.of("minecraft:warden")))
                .setTooltip(tt("buffmobs.config.mobFilter.blacklist.tooltip"))
                .setSaveConsumer(v -> cfg.mobFilter.blacklist = v).build());
    }

    private static void buildModIdFilterCategory(ConfigBuilder builder, ConfigEntryBuilder eb, BuffMobsConfig cfg) {
        ConfigCategory cat = builder.getOrCreateCategory(Component.translatable("buffmobs.config.modidFilter"));
        cat.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.modidFilter.useWhitelist"), cfg.modidFilter.useWhitelist)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.modidFilter.useWhitelist.tooltip"))
                .setSaveConsumer(v -> cfg.modidFilter.useWhitelist = v).build());
        cat.addEntry(eb.startStrList(Component.translatable("buffmobs.config.modidFilter.whitelist"), new ArrayList<>(cfg.modidFilter.whitelist))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.modidFilter.whitelist.tooltip"))
                .setSaveConsumer(v -> cfg.modidFilter.whitelist = v).build());
        cat.addEntry(eb.startStrList(Component.translatable("buffmobs.config.modidFilter.blacklist"), new ArrayList<>(cfg.modidFilter.blacklist))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.modidFilter.blacklist.tooltip"))
                .setSaveConsumer(v -> cfg.modidFilter.blacklist = v).build());
    }

    private static void buildDimensionFilterCategory(ConfigBuilder builder, ConfigEntryBuilder eb, BuffMobsConfig cfg) {
        ConfigCategory cat = builder.getOrCreateCategory(Component.translatable("buffmobs.config.dimensionFilter"));
        cat.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.dimensionFilter.useWhitelist"), cfg.dimensionFilter.useWhitelist)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.dimensionFilter.useWhitelist.tooltip"))
                .setSaveConsumer(v -> cfg.dimensionFilter.useWhitelist = v).build());
        cat.addEntry(eb.startStrList(Component.translatable("buffmobs.config.dimensionFilter.whitelist"), new ArrayList<>(cfg.dimensionFilter.whitelist))
                .setDefaultValue(new ArrayList<>(List.of("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end")))
                .setTooltip(tt("buffmobs.config.dimensionFilter.whitelist.tooltip"))
                .setSaveConsumer(v -> cfg.dimensionFilter.whitelist = v).build());
        cat.addEntry(eb.startStrList(Component.translatable("buffmobs.config.dimensionFilter.blacklist"), new ArrayList<>(cfg.dimensionFilter.blacklist))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.dimensionFilter.blacklist.tooltip"))
                .setSaveConsumer(v -> cfg.dimensionFilter.blacklist = v).build());
    }

    private static void buildRangedMeleeSwitchingCategory(ConfigBuilder builder, ConfigEntryBuilder eb, BuffMobsConfig cfg) {
        ConfigCategory cat = builder.getOrCreateCategory(Component.translatable("buffmobs.config.rangedMeleeSwitching"));
        cat.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.rangedMeleeSwitching.enabled"), cfg.rangedMeleeSwitching.enabled)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.rangedMeleeSwitching.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.rangedMeleeSwitching.enabled = v).build());
        cat.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.rangedMeleeSwitching.behaviorMode"),
                        BuffMobsConfig.RangedMeleeSwitching.BehaviorMode.class,
                        cfg.rangedMeleeSwitching.behaviorMode)
                .setDefaultValue(BuffMobsConfig.RangedMeleeSwitching.BehaviorMode.RANDOM)
                .setEnumNameProvider(e -> switch ((BuffMobsConfig.RangedMeleeSwitching.BehaviorMode) e) {
                    case MELEE  -> Component.translatable("buffmobs.config.rangedMeleeSwitching.behaviorMode.melee");
                    case KITE   -> Component.translatable("buffmobs.config.rangedMeleeSwitching.behaviorMode.kite");
                    case RANDOM -> Component.translatable("buffmobs.config.rangedMeleeSwitching.behaviorMode.random");
                })
                .setTooltip(tt("buffmobs.config.rangedMeleeSwitching.behaviorMode.tooltip"))
                .setSaveConsumer(v -> cfg.rangedMeleeSwitching.behaviorMode = v).build());
        cat.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.rangedMeleeSwitching.switchDistance"), cfg.rangedMeleeSwitching.switchDistance)
                .setDefaultValue(4.0).setMin(1.0)
                .setTooltip(tt("buffmobs.config.rangedMeleeSwitching.switchDistance.tooltip"))
                .setSaveConsumer(v -> cfg.rangedMeleeSwitching.switchDistance = v).build());
        cat.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.rangedMeleeSwitching.meleeSpeedMultiplier"), cfg.rangedMeleeSwitching.meleeSpeedMultiplier)
                .setDefaultValue(0.9).setMin(0.1)
                .setTooltip(tt("buffmobs.config.rangedMeleeSwitching.meleeSpeedMultiplier.tooltip"))
                .setSaveConsumer(v -> cfg.rangedMeleeSwitching.meleeSpeedMultiplier = v).build());
        cat.addEntry(eb.startStrList(Component.translatable("buffmobs.config.rangedMeleeSwitching.customRangedMobs"), new ArrayList<>(cfg.rangedMeleeSwitching.customRangedMobs))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.rangedMeleeSwitching.customRangedMobs.tooltip"))
                .setSaveConsumer(v -> cfg.rangedMeleeSwitching.customRangedMobs = v).build());

        var weaponSub = eb.startSubCategory(Component.translatable("buffmobs.config.rangedMeleeSwitching.weaponUnlockDays"));
        weaponSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.stoneSwordUnlockDay"), cfg.rangedMeleeSwitching.stoneSwordUnlockDay)
                .setDefaultValue(0).setMin(0).setSaveConsumer(v -> cfg.rangedMeleeSwitching.stoneSwordUnlockDay = v).build());
        weaponSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.ironSwordUnlockDay"), cfg.rangedMeleeSwitching.ironSwordUnlockDay)
                .setDefaultValue(7).setMin(0).setSaveConsumer(v -> cfg.rangedMeleeSwitching.ironSwordUnlockDay = v).build());
        weaponSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.diamondSwordUnlockDay"), cfg.rangedMeleeSwitching.diamondSwordUnlockDay)
                .setDefaultValue(21).setMin(0).setSaveConsumer(v -> cfg.rangedMeleeSwitching.diamondSwordUnlockDay = v).build());
        weaponSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.netheriteSwordUnlockDay"), cfg.rangedMeleeSwitching.netheriteSwordUnlockDay)
                .setDefaultValue(60).setMin(0).setSaveConsumer(v -> cfg.rangedMeleeSwitching.netheriteSwordUnlockDay = v).build());
        weaponSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.goldenAxeUnlockDay"), cfg.rangedMeleeSwitching.goldenAxeUnlockDay)
                .setDefaultValue(0).setMin(0).setSaveConsumer(v -> cfg.rangedMeleeSwitching.goldenAxeUnlockDay = v).build());
        weaponSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.diamondAxeUnlockDay"), cfg.rangedMeleeSwitching.diamondAxeUnlockDay)
                .setDefaultValue(14).setMin(0).setSaveConsumer(v -> cfg.rangedMeleeSwitching.diamondAxeUnlockDay = v).build());
        weaponSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.netheriteAxeUnlockDay"), cfg.rangedMeleeSwitching.netheriteAxeUnlockDay)
                .setDefaultValue(45).setMin(0).setSaveConsumer(v -> cfg.rangedMeleeSwitching.netheriteAxeUnlockDay = v).build());
        cat.addEntry(weaponSub.build());

        var enchantSub = eb.startSubCategory(Component.translatable("buffmobs.config.rangedMeleeSwitching.enchantments"));
        enchantSub.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.rangedMeleeSwitching.enchantmentsEnabled"), cfg.rangedMeleeSwitching.enchantmentsEnabled)
                .setDefaultValue(true).setSaveConsumer(v -> cfg.rangedMeleeSwitching.enchantmentsEnabled = v).build());
        enchantSub.add(eb.startIntSlider(Component.translatable("buffmobs.config.rangedMeleeSwitching.maxEnchantmentsPerWeapon"), cfg.rangedMeleeSwitching.maxEnchantmentsPerWeapon, 1, 4)
                .setDefaultValue(2).setSaveConsumer(v -> cfg.rangedMeleeSwitching.maxEnchantmentsPerWeapon = v).build());
        enchantSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.daysPerEnchantmentLevel"), cfg.rangedMeleeSwitching.daysPerEnchantmentLevel)
                .setDefaultValue(7).setMin(1).setSaveConsumer(v -> cfg.rangedMeleeSwitching.daysPerEnchantmentLevel = v).build());
        enchantSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.sharpnessUnlockDay"), cfg.rangedMeleeSwitching.sharpnessUnlockDay)
                .setDefaultValue(0).setMin(0).setSaveConsumer(v -> cfg.rangedMeleeSwitching.sharpnessUnlockDay = v).build());
        enchantSub.add(eb.startIntSlider(Component.translatable("buffmobs.config.rangedMeleeSwitching.sharpnessMaxLevel"), cfg.rangedMeleeSwitching.sharpnessMaxLevel, 1, 5)
                .setDefaultValue(5).setSaveConsumer(v -> cfg.rangedMeleeSwitching.sharpnessMaxLevel = v).build());
        enchantSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.fireAspectUnlockDay"), cfg.rangedMeleeSwitching.fireAspectUnlockDay)
                .setDefaultValue(14).setMin(0).setSaveConsumer(v -> cfg.rangedMeleeSwitching.fireAspectUnlockDay = v).build());
        enchantSub.add(eb.startIntSlider(Component.translatable("buffmobs.config.rangedMeleeSwitching.fireAspectMaxLevel"), cfg.rangedMeleeSwitching.fireAspectMaxLevel, 1, 2)
                .setDefaultValue(2).setSaveConsumer(v -> cfg.rangedMeleeSwitching.fireAspectMaxLevel = v).build());
        enchantSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.knockbackUnlockDay"), cfg.rangedMeleeSwitching.knockbackUnlockDay)
                .setDefaultValue(7).setMin(0).setSaveConsumer(v -> cfg.rangedMeleeSwitching.knockbackUnlockDay = v).build());
        enchantSub.add(eb.startIntSlider(Component.translatable("buffmobs.config.rangedMeleeSwitching.knockbackMaxLevel"), cfg.rangedMeleeSwitching.knockbackMaxLevel, 1, 2)
                .setDefaultValue(2).setSaveConsumer(v -> cfg.rangedMeleeSwitching.knockbackMaxLevel = v).build());
        enchantSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.sweepingEdgeUnlockDay"), cfg.rangedMeleeSwitching.sweepingEdgeUnlockDay)
                .setDefaultValue(21).setMin(0).setSaveConsumer(v -> cfg.rangedMeleeSwitching.sweepingEdgeUnlockDay = v).build());
        enchantSub.add(eb.startIntSlider(Component.translatable("buffmobs.config.rangedMeleeSwitching.sweepingEdgeMaxLevel"), cfg.rangedMeleeSwitching.sweepingEdgeMaxLevel, 1, 3)
                .setDefaultValue(3).setSaveConsumer(v -> cfg.rangedMeleeSwitching.sweepingEdgeMaxLevel = v).build());
        cat.addEntry(enchantSub.build());
    }

    private static void buildCombatDraftCategory(ConfigBuilder builder, ConfigEntryBuilder eb, BuffMobsConfig cfg) {
        ConfigCategory cat = builder.getOrCreateCategory(Component.translatable("buffmobs.config.combatDraft"));
        cat.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.combatDraft.enabled"), cfg.combatDraft.enabled)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.combatDraft.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.combatDraft.enabled = v).build());
        cat.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.combatDraft.healthThreshold"), cfg.combatDraft.healthThreshold)
                .setDefaultValue(0.20).setMin(0.01).setMax(0.99)
                .setTooltip(tt("buffmobs.config.combatDraft.healthThreshold.tooltip"))
                .setSaveConsumer(v -> cfg.combatDraft.healthThreshold = v).build());
        cat.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.combatDraft.regenAmplifier"), cfg.combatDraft.regenAmplifier, 1, 10)
                .setDefaultValue(4)
                .setTextGetter(v -> Component.translatable("buffmobs.config.effects.slider.level", v))
                .setTooltip(tt("buffmobs.config.combatDraft.regenAmplifier.tooltip"))
                .setSaveConsumer(v -> cfg.combatDraft.regenAmplifier = v).build());
        cat.addEntry(eb.startIntField(Component.translatable("buffmobs.config.combatDraft.regenDuration"), cfg.combatDraft.regenDuration)
                .setDefaultValue(10).setMin(1).setMax(120)
                .setTooltip(tt("buffmobs.config.combatDraft.regenDuration.tooltip"))
                .setSaveConsumer(v -> cfg.combatDraft.regenDuration = v).build());
        cat.addEntry(eb.startIntField(Component.translatable("buffmobs.config.combatDraft.cooldownTicks"), cfg.combatDraft.cooldownTicks)
                .setDefaultValue(600).setMin(20).setMax(72000)
                .setTooltip(tt("buffmobs.config.combatDraft.cooldownTicks.tooltip"))
                .setSaveConsumer(v -> cfg.combatDraft.cooldownTicks = v).build());
        cat.addEntry(eb.startIntField(Component.translatable("buffmobs.config.combatDraft.maxUses"), cfg.combatDraft.maxUses)
                .setDefaultValue(0).setMin(0).setMax(100)
                .setTooltip(tt("buffmobs.config.combatDraft.maxUses.tooltip"))
                .setSaveConsumer(v -> cfg.combatDraft.maxUses = v).build());

        var draftFilterSub = eb.startSubCategory(Component.translatable("buffmobs.config.combatDraft.mobFilter"));
        draftFilterSub.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.combatDraft.mobFilter.useWhitelist"), cfg.combatDraft.useWhitelist)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.filter.useWhitelist.tooltip"))
                .setSaveConsumer(v -> cfg.combatDraft.useWhitelist = v).build());
        draftFilterSub.add(eb.startStrList(Component.translatable("buffmobs.config.combatDraft.mobFilter.whitelist"), new ArrayList<>(cfg.combatDraft.whitelist))
                .setDefaultValue(new ArrayList<>()).setSaveConsumer(v -> cfg.combatDraft.whitelist = v).build());
        draftFilterSub.add(eb.startStrList(Component.translatable("buffmobs.config.combatDraft.mobFilter.blacklist"), new ArrayList<>(cfg.combatDraft.blacklist))
                .setDefaultValue(new ArrayList<>()).setSaveConsumer(v -> cfg.combatDraft.blacklist = v).build());
        cat.addEntry(draftFilterSub.build());
    }

    private static void buildMobPresetsCategory(ConfigBuilder builder, ConfigEntryBuilder eb, BuffMobsConfig cfg) {
        ConfigCategory cat = builder.getOrCreateCategory(Component.translatable("buffmobs.config.mobPresets"));
        cat.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobPresets.enabled"), cfg.mobPresets.enabled)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.mobPresets.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.mobPresets.enabled = v).build());
        cat.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobPresets.overrideDimensionScaling"), cfg.mobPresets.overrideDimensionScaling)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.mobPresets.overrideDimensionScaling.tooltip"))
                .setSaveConsumer(v -> cfg.mobPresets.overrideDimensionScaling = v).build());

        BuffMobsConfig.MobPresets.PresetSlot[] presetSlots = {
                cfg.mobPresets.preset1, cfg.mobPresets.preset2, cfg.mobPresets.preset3,
                cfg.mobPresets.preset4, cfg.mobPresets.preset5
        };
        for (int i = 0; i < presetSlots.length; i++) {
            BuffMobsConfig.MobPresets.PresetSlot p = presetSlots[i];
            int n = i + 1;
            Component slotLabel = (p.presetName != null && !p.presetName.isEmpty())
                    ? Component.translatable("buffmobs.config.mobPresets.slot.named", n, p.presetName)
                    : Component.translatable("buffmobs.config.mobPresets.slot", n);
            var pSub = eb.startSubCategory(slotLabel);
            pSub.add(eb.startStrField(Component.translatable("buffmobs.config.mobPresets.presetName"), p.presetName == null ? "" : p.presetName)
                    .setTooltip(tt("buffmobs.config.mobPresets.presetName.tooltip"))
                    .setSaveConsumer(v -> p.presetName = v).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.healthMultiplier"), p.healthMultiplier)
                    .setDefaultValue(1.0).setMin(0.01).setSaveConsumer(v -> p.healthMultiplier = v).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.damageMultiplier"), p.damageMultiplier)
                    .setDefaultValue(1.0).setMin(0.01).setSaveConsumer(v -> p.damageMultiplier = v).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.speedMultiplier"), p.speedMultiplier)
                    .setDefaultValue(1.0).setMin(0.01).setSaveConsumer(v -> p.speedMultiplier = v).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.attackSpeedMultiplier"), p.attackSpeedMultiplier)
                    .setDefaultValue(1.0).setMin(0.01).setSaveConsumer(v -> p.attackSpeedMultiplier = v).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.armorAddition"), p.armorAddition)
                    .setDefaultValue(0.0).setMin(0.0).setSaveConsumer(v -> p.armorAddition = v).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.toughnessAddition"), p.armorToughnessAddition)
                    .setDefaultValue(0.0).setMin(0.0).setSaveConsumer(v -> p.armorToughnessAddition = v).build());
            cat.addEntry(pSub.build());
        }
        cat.addEntry(eb.startStrList(Component.translatable("buffmobs.config.mobPresets.mobMapping"), new ArrayList<>(cfg.mobPresets.mobMapping))
                .setDefaultValue(List.of("minecraft:zombie:default", "minecraft:skeleton:default",
                        "minecraft:ender_dragon:boss", "minecraft:wither:boss"))
                .setTooltip(tt("buffmobs.config.mobPresets.mobMapping.tooltip"))
                .setSaveConsumer(v -> cfg.mobPresets.mobMapping = v).build());
    }

    private static void buildPassiveMobAggressionCategory(ConfigBuilder builder, ConfigEntryBuilder eb, BuffMobsConfig cfg) {
        ConfigCategory cat = builder.getOrCreateCategory(Component.translatable("buffmobs.config.passiveMobAggression"));
        cat.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.passiveMobAggression.enabled"), cfg.passiveMobAggression.enabled)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.passiveMobAggression.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.passiveMobAggression.enabled = v).build());
        cat.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.passiveMobAggression.baseDamage"), cfg.passiveMobAggression.baseDamage)
                .setDefaultValue(3.0).setMin(0.5)
                .setTooltip(tt("buffmobs.config.passiveMobAggression.baseDamage.tooltip"))
                .setSaveConsumer(v -> cfg.passiveMobAggression.baseDamage = v).build());
        cat.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.passiveMobAggression.scaleWithHealth"), cfg.passiveMobAggression.scaleWithHealth)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.passiveMobAggression.scaleWithHealth.tooltip"))
                .setSaveConsumer(v -> cfg.passiveMobAggression.scaleWithHealth = v).build());
        cat.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.passiveMobAggression.healthScaleFactor"), cfg.passiveMobAggression.healthScaleFactor)
                .setDefaultValue(0.1).setMin(0.0)
                .setTooltip(tt("buffmobs.config.passiveMobAggression.healthScaleFactor.tooltip"))
                .setSaveConsumer(v -> cfg.passiveMobAggression.healthScaleFactor = v).build());
        cat.addEntry(eb.startStrList(Component.translatable("buffmobs.config.passiveMobAggression.whitelist"), new ArrayList<>(cfg.passiveMobAggression.whitelist))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.passiveMobAggression.whitelist.tooltip"))
                .setSaveConsumer(v -> cfg.passiveMobAggression.whitelist = v).build());
        cat.addEntry(eb.startStrList(Component.translatable("buffmobs.config.passiveMobAggression.blacklist"), new ArrayList<>(cfg.passiveMobAggression.blacklist))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.passiveMobAggression.blacklist.tooltip"))
                .setSaveConsumer(v -> cfg.passiveMobAggression.blacklist = v).build());
    }
}