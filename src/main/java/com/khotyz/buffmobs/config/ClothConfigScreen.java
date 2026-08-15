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
                    BuffMobsConfig.SPEC.save();
                    BuffMobsMod.LOGGER.info("[BuffMobs] Config saved via Cloth Config");
                });

        ConfigEntryBuilder eb = builder.entryBuilder();
        BuffMobsConfig cfg = BuffMobsConfig.INSTANCE;

        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("buffmobs.config.general"));
        general.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.general.enabled"), cfg.enabled.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.general.enabled.tooltip"))
                .setSaveConsumer(cfg.enabled::set).build());
        general.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.general.visualEffects"), cfg.visualEffects.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.general.visualEffects.tooltip"))
                .setSaveConsumer(cfg.visualEffects::set).build());
        general.addEntry(eb.startTextDescription(Component.translatable("buffmobs.config.credits.translation")).build());

        ConfigCategory attributes = builder.getOrCreateCategory(Component.translatable("buffmobs.config.attributes"));
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.healthMultiplier"), cfg.attributes.healthMultiplier.get())
                .setDefaultValue(1.5).setMin(0.1).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.attributes.healthMultiplier.tooltip"))
                .setSaveConsumer(cfg.attributes.healthMultiplier::set).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.damageMultiplier"), cfg.attributes.damageMultiplier.get())
                .setDefaultValue(1.5).setMin(0.1).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.attributes.damageMultiplier.tooltip"))
                .setSaveConsumer(cfg.attributes.damageMultiplier::set).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.speedMultiplier"), cfg.attributes.speedMultiplier.get())
                .setDefaultValue(1.0).setMin(0.1).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.attributes.speedMultiplier.tooltip"))
                .setSaveConsumer(cfg.attributes.speedMultiplier::set).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.attackSpeedMultiplier"), cfg.attributes.attackSpeedMultiplier.get())
                .setDefaultValue(1.0).setMin(0.1).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.attributes.attackSpeedMultiplier.tooltip"))
                .setSaveConsumer(cfg.attributes.attackSpeedMultiplier::set).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.armorAddition"), cfg.attributes.armorAddition.get())
                .setDefaultValue(5.0).setMin(0.0).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.attributes.armorAddition.tooltip"))
                .setSaveConsumer(cfg.attributes.armorAddition::set).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.armorToughnessAddition"), cfg.attributes.armorToughnessAddition.get())
                .setDefaultValue(0.0).setMin(0.0).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.attributes.armorToughnessAddition.tooltip"))
                .setSaveConsumer(cfg.attributes.armorToughnessAddition::set).build());

        ConfigCategory effects = builder.getOrCreateCategory(Component.translatable("buffmobs.config.effects"));
        effects.addEntry(eb.startIntField(Component.translatable("buffmobs.config.effects.duration"), cfg.effects.duration.get())
                .setDefaultValue(-1).setMin(-1).setMax(7200)
                .setTooltip(tt("buffmobs.config.effects.duration.tooltip"))
                .setSaveConsumer(cfg.effects.duration::set).build());
        effects.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.effects.strengthAmplifier"), cfg.effects.strengthAmplifier.get(), 0, 10)
                .setDefaultValue(0)
                .setTextGetter(v -> v == 0
                        ? Component.translatable("buffmobs.config.effects.slider.disabled")
                        : Component.translatable("buffmobs.config.effects.slider.level", v))
                .setTooltip(tt("buffmobs.config.effects.strengthAmplifier.tooltip"))
                .setSaveConsumer(cfg.effects.strengthAmplifier::set).build());
        effects.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.effects.speedAmplifier"), cfg.effects.speedAmplifier.get(), 0, 10)
                .setDefaultValue(0)
                .setTextGetter(v -> v == 0
                        ? Component.translatable("buffmobs.config.effects.slider.disabled")
                        : Component.translatable("buffmobs.config.effects.slider.level", v))
                .setTooltip(tt("buffmobs.config.effects.speedAmplifier.tooltip"))
                .setSaveConsumer(cfg.effects.speedAmplifier::set).build());
        effects.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.effects.resistanceAmplifier"), cfg.effects.resistanceAmplifier.get(), 0, 10)
                .setDefaultValue(0)
                .setTextGetter(v -> v == 0
                        ? Component.translatable("buffmobs.config.effects.slider.disabled")
                        : Component.translatable("buffmobs.config.effects.slider.level", v))
                .setTooltip(tt("buffmobs.config.effects.resistanceAmplifier.tooltip"))
                .setSaveConsumer(cfg.effects.resistanceAmplifier::set).build());
        effects.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.effects.regenerationAmplifier"), cfg.effects.regenerationAmplifier.get(), 0, 10)
                .setDefaultValue(0)
                .setTextGetter(v -> v == 0
                        ? Component.translatable("buffmobs.config.effects.slider.disabled")
                        : Component.translatable("buffmobs.config.effects.slider.level", v))
                .setTooltip(tt("buffmobs.config.effects.regenerationAmplifier.tooltip"))
                .setSaveConsumer(cfg.effects.regenerationAmplifier::set).build());
        effects.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.effects.absorptionAmplifier"), cfg.effects.absorptionAmplifier.get(), 0, 10)
                .setDefaultValue(0)
                .setTextGetter(v -> v == 0
                        ? Component.translatable("buffmobs.config.effects.slider.disabled")
                        : Component.translatable("buffmobs.config.effects.slider.level", v))
                .setTooltip(tt("buffmobs.config.effects.absorptionAmplifier.tooltip"))
                .setSaveConsumer(cfg.effects.absorptionAmplifier::set).build());

        ConfigCategory harmful = builder.getOrCreateCategory(Component.translatable("buffmobs.config.harmfulEffects"));
        harmful.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.harmfulEffects.enabled"), cfg.harmfulEffects.enabled.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.harmfulEffects.enabled.tooltip"))
                .setSaveConsumer(cfg.harmfulEffects.enabled::set).build());
        harmful.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.harmfulEffects.chance"), cfg.harmfulEffects.chance.get())
                .setDefaultValue(0.15).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.harmfulEffects.chance.tooltip"))
                .setSaveConsumer(cfg.harmfulEffects.chance::set).build());
        harmful.addEntry(eb.startIntField(Component.translatable("buffmobs.config.harmfulEffects.poisonDuration"), cfg.harmfulEffects.poisonDuration.get())
                .setDefaultValue(5).setMin(1).setMax(60)
                .setTooltip(tt("buffmobs.config.harmfulEffects.poisonDuration.tooltip"))
                .setSaveConsumer(cfg.harmfulEffects.poisonDuration::set).build());
        harmful.addEntry(eb.startIntField(Component.translatable("buffmobs.config.harmfulEffects.slownessDuration"), cfg.harmfulEffects.slownessDuration.get())
                .setDefaultValue(3).setMin(1).setMax(60)
                .setTooltip(tt("buffmobs.config.harmfulEffects.slownessDuration.tooltip"))
                .setSaveConsumer(cfg.harmfulEffects.slownessDuration::set).build());
        harmful.addEntry(eb.startIntField(Component.translatable("buffmobs.config.harmfulEffects.witherDuration"), cfg.harmfulEffects.witherDuration.get())
                .setDefaultValue(3).setMin(1).setMax(60)
                .setTooltip(tt("buffmobs.config.harmfulEffects.witherDuration.tooltip"))
                .setSaveConsumer(cfg.harmfulEffects.witherDuration::set).build());

        ConfigCategory dayScaling = builder.getOrCreateCategory(Component.translatable("buffmobs.config.dayScaling"));
        dayScaling.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.dayScaling.enabled"), cfg.dayScaling.enabled.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.dayScaling.enabled.tooltip"))
                .setSaveConsumer(cfg.dayScaling.enabled::set).build());
        dayScaling.addEntry(eb.startIntField(Component.translatable("buffmobs.config.dayScaling.interval"), cfg.dayScaling.interval.get())
                .setDefaultValue(7).setMin(1).setMax(365)
                .setTooltip(tt("buffmobs.config.dayScaling.interval.tooltip"))
                .setSaveConsumer(cfg.dayScaling.interval::set).build());
        dayScaling.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.dayScaling.multiplier"), cfg.dayScaling.multiplier.get())
                .setDefaultValue(0.1).setMin(0.01).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.dayScaling.multiplier.tooltip"))
                .setSaveConsumer(cfg.dayScaling.multiplier::set).build());
        dayScaling.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.dayScaling.maxMultiplier"), cfg.dayScaling.maxMultiplier.get())
                .setDefaultValue(5.0).setMin(0.0).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.dayScaling.maxMultiplier.tooltip"))
                .setSaveConsumer(cfg.dayScaling.maxMultiplier::set).build());
        dayScaling.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.dayScaling.showNotifications"), cfg.dayScaling.showNotifications.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.dayScaling.showNotifications.tooltip"))
                .setSaveConsumer(cfg.dayScaling.showNotifications::set).build());
        dayScaling.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.dayScaling.notificationMode"),
                        BuffMobsConfig.DayScaling.NotificationMode.class,
                        cfg.dayScaling.notificationMode.get())
                .setDefaultValue(BuffMobsConfig.DayScaling.NotificationMode.EVERY_DAY)
                .setEnumNameProvider(e -> switch ((BuffMobsConfig.DayScaling.NotificationMode) e) {
                    case EVERY_DAY             -> Component.translatable("buffmobs.config.dayScaling.notificationMode.every_day");
                    case SCALING_INCREASE_ONLY -> Component.translatable("buffmobs.config.dayScaling.notificationMode.scaling_increase_only");
                })
                .setTooltip(tt("buffmobs.config.dayScaling.notificationMode.tooltip"))
                .setSaveConsumer(cfg.dayScaling.notificationMode::set).build());

        ConfigCategory dimScaling = builder.getOrCreateCategory(Component.translatable("buffmobs.config.dimensionScaling"));
        BuffMobsConfig.DimensionScaling.DimensionSlot[] dimSlots = {
                cfg.dimensionScaling.slot1, cfg.dimensionScaling.slot2, cfg.dimensionScaling.slot3,
                cfg.dimensionScaling.slot4, cfg.dimensionScaling.slot5
        };
        for (int i = 0; i < dimSlots.length; i++) {
            BuffMobsConfig.DimensionScaling.DimensionSlot slot = dimSlots[i];
            int n = i + 1;
            var sub = eb.startSubCategory(Component.translatable("buffmobs.config.dimensionScaling.slot", n));
            sub.add(eb.startStrField(Component.translatable("buffmobs.config.dimensionScaling.dimensionId"), slot.dimensionName.get())
                    .setDefaultValue("")
                    .setTooltip(tt("buffmobs.config.dimensionScaling.dimensionId.tooltip"))
                    .setSaveConsumer(slot.dimensionName::set).build());
            sub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.healthPercent"), slot.healthMultiplier.get())
                    .setDefaultValue(100).setMin(100).setMax(999999)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.healthPercent.tooltip"))
                    .setSaveConsumer(slot.healthMultiplier::set).build());
            sub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.damagePercent"), slot.damageMultiplier.get())
                    .setDefaultValue(100).setMin(100).setMax(999999)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.damagePercent.tooltip"))
                    .setSaveConsumer(slot.damageMultiplier::set).build());
            sub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.speedPercent"), slot.speedMultiplier.get())
                    .setDefaultValue(100).setMin(100).setMax(999999)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.speedPercent.tooltip"))
                    .setSaveConsumer(slot.speedMultiplier::set).build());
            sub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.attackSpeedPercent"), slot.attackSpeedMultiplier.get())
                    .setDefaultValue(100).setMin(100).setMax(999999)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.attackSpeedPercent.tooltip"))
                    .setSaveConsumer(slot.attackSpeedMultiplier::set).build());
            sub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.armorAddition"), slot.armorAddition.get())
                    .setDefaultValue(0).setMin(0).setMax(999999)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.armorAddition.tooltip"))
                    .setSaveConsumer(slot.armorAddition::set).build());
            sub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.toughnessAddition"), slot.armorToughnessAddition.get())
                    .setDefaultValue(0).setMin(0).setMax(999999)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.toughnessAddition.tooltip"))
                    .setSaveConsumer(slot.armorToughnessAddition::set).build());
            dimScaling.addEntry(sub.build());
        }

        ConfigCategory mobFilter = builder.getOrCreateCategory(Component.translatable("buffmobs.config.mobFilter"));
        mobFilter.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobFilter.useWhitelist"), cfg.mobFilter.useWhitelist.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.mobFilter.useWhitelist.tooltip"))
                .setSaveConsumer(cfg.mobFilter.useWhitelist::set).build());
        mobFilter.addEntry(eb.startStrList(Component.translatable("buffmobs.config.mobFilter.blacklist"), new ArrayList<>(cfg.mobFilter.blacklist.get()))
                .setDefaultValue(List.of("minecraft:warden"))
                .setTooltip(tt("buffmobs.config.mobFilter.blacklist.tooltip"))
                .setSaveConsumer(v -> cfg.mobFilter.blacklist.set(v)).build());
        mobFilter.addEntry(eb.startStrList(Component.translatable("buffmobs.config.mobFilter.whitelist"), new ArrayList<>(cfg.mobFilter.whitelist.get()))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.mobFilter.whitelist.tooltip"))
                .setSaveConsumer(v -> cfg.mobFilter.whitelist.set(v)).build());

        ConfigCategory modFilter = builder.getOrCreateCategory(Component.translatable("buffmobs.config.modidFilter"));
        modFilter.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.modidFilter.useWhitelist"), cfg.modidFilter.useWhitelist.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.modidFilter.useWhitelist.tooltip"))
                .setSaveConsumer(cfg.modidFilter.useWhitelist::set).build());
        modFilter.addEntry(eb.startStrList(Component.translatable("buffmobs.config.modidFilter.blacklist"), new ArrayList<>(cfg.modidFilter.blacklist.get()))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.modidFilter.blacklist.tooltip"))
                .setSaveConsumer(v -> cfg.modidFilter.blacklist.set(v)).build());
        modFilter.addEntry(eb.startStrList(Component.translatable("buffmobs.config.modidFilter.whitelist"), new ArrayList<>(cfg.modidFilter.whitelist.get()))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.modidFilter.whitelist.tooltip"))
                .setSaveConsumer(v -> cfg.modidFilter.whitelist.set(v)).build());

        ConfigCategory dimFilter = builder.getOrCreateCategory(Component.translatable("buffmobs.config.dimensionFilter"));
        dimFilter.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.dimensionFilter.useWhitelist"), cfg.dimensionFilter.useWhitelist.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.dimensionFilter.useWhitelist.tooltip"))
                .setSaveConsumer(cfg.dimensionFilter.useWhitelist::set).build());
        dimFilter.addEntry(eb.startStrList(Component.translatable("buffmobs.config.dimensionFilter.blacklist"), new ArrayList<>(cfg.dimensionFilter.blacklist.get()))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.dimensionFilter.blacklist.tooltip"))
                .setSaveConsumer(v -> cfg.dimensionFilter.blacklist.set(v)).build());
        dimFilter.addEntry(eb.startStrList(Component.translatable("buffmobs.config.dimensionFilter.whitelist"), new ArrayList<>(cfg.dimensionFilter.whitelist.get()))
                .setDefaultValue(List.of("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"))
                .setTooltip(tt("buffmobs.config.dimensionFilter.whitelist.tooltip"))
                .setSaveConsumer(v -> cfg.dimensionFilter.whitelist.set(v)).build());

        ConfigCategory ranged = builder.getOrCreateCategory(Component.translatable("buffmobs.config.rangedMeleeSwitching"));
        ranged.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.rangedMeleeSwitching.enabled"), cfg.rangedMeleeSwitching.enabled.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.rangedMeleeSwitching.enabled.tooltip"))
                .setSaveConsumer(cfg.rangedMeleeSwitching.enabled::set).build());
        ranged.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.rangedMeleeSwitching.behaviorMode"),
                        BuffMobsConfig.RangedMeleeSwitching.BehaviorMode.class,
                        cfg.rangedMeleeSwitching.behaviorMode.get())
                .setDefaultValue(BuffMobsConfig.RangedMeleeSwitching.BehaviorMode.RANDOM)
                .setEnumNameProvider(e -> switch ((BuffMobsConfig.RangedMeleeSwitching.BehaviorMode) e) {
                    case MELEE  -> Component.translatable("buffmobs.config.rangedMeleeSwitching.behaviorMode.melee");
                    case KITE   -> Component.translatable("buffmobs.config.rangedMeleeSwitching.behaviorMode.kite");
                    case RANDOM -> Component.translatable("buffmobs.config.rangedMeleeSwitching.behaviorMode.random");
                })
                .setTooltip(tt("buffmobs.config.rangedMeleeSwitching.behaviorMode.tooltip"))
                .setSaveConsumer(cfg.rangedMeleeSwitching.behaviorMode::set).build());
        ranged.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.rangedMeleeSwitching.switchDistance"), cfg.rangedMeleeSwitching.switchDistance.get())
                .setDefaultValue(4.0).setMin(1.0).setMax(16.0)
                .setTooltip(tt("buffmobs.config.rangedMeleeSwitching.switchDistance.tooltip"))
                .setSaveConsumer(cfg.rangedMeleeSwitching.switchDistance::set).build());
        ranged.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.rangedMeleeSwitching.meleeSpeedMultiplier"), cfg.rangedMeleeSwitching.meleeSpeedMultiplier.get())
                .setDefaultValue(0.9).setMin(0.1).setMax(5.0)
                .setTooltip(tt("buffmobs.config.rangedMeleeSwitching.meleeSpeedMultiplier.tooltip"))
                .setSaveConsumer(cfg.rangedMeleeSwitching.meleeSpeedMultiplier::set).build());
        ranged.addEntry(eb.startStrList(Component.translatable("buffmobs.config.rangedMeleeSwitching.customRangedMobs"), new ArrayList<>(cfg.rangedMeleeSwitching.customRangedMobs.get()))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.rangedMeleeSwitching.customRangedMobs.tooltip"))
                .setSaveConsumer(v -> cfg.rangedMeleeSwitching.customRangedMobs.set(v)).build());

        var weaponSub = eb.startSubCategory(Component.translatable("buffmobs.config.rangedMeleeSwitching.weaponUnlockDays"));
        weaponSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.stoneSwordUnlockDay"), cfg.rangedMeleeSwitching.stoneSwordUnlockDay.get())
                .setDefaultValue(0).setMin(0).setMax(365).setSaveConsumer(cfg.rangedMeleeSwitching.stoneSwordUnlockDay::set).build());
        weaponSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.ironSwordUnlockDay"), cfg.rangedMeleeSwitching.ironSwordUnlockDay.get())
                .setDefaultValue(7).setMin(0).setMax(365).setSaveConsumer(cfg.rangedMeleeSwitching.ironSwordUnlockDay::set).build());
        weaponSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.diamondSwordUnlockDay"), cfg.rangedMeleeSwitching.diamondSwordUnlockDay.get())
                .setDefaultValue(21).setMin(0).setMax(365).setSaveConsumer(cfg.rangedMeleeSwitching.diamondSwordUnlockDay::set).build());
        weaponSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.netheriteSwordUnlockDay"), cfg.rangedMeleeSwitching.netheriteSwordUnlockDay.get())
                .setDefaultValue(60).setMin(0).setMax(365).setSaveConsumer(cfg.rangedMeleeSwitching.netheriteSwordUnlockDay::set).build());
        weaponSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.goldenAxeUnlockDay"), cfg.rangedMeleeSwitching.goldenAxeUnlockDay.get())
                .setDefaultValue(0).setMin(0).setMax(365).setSaveConsumer(cfg.rangedMeleeSwitching.goldenAxeUnlockDay::set).build());
        weaponSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.diamondAxeUnlockDay"), cfg.rangedMeleeSwitching.diamondAxeUnlockDay.get())
                .setDefaultValue(14).setMin(0).setMax(365).setSaveConsumer(cfg.rangedMeleeSwitching.diamondAxeUnlockDay::set).build());
        weaponSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.netheriteAxeUnlockDay"), cfg.rangedMeleeSwitching.netheriteAxeUnlockDay.get())
                .setDefaultValue(45).setMin(0).setMax(365).setSaveConsumer(cfg.rangedMeleeSwitching.netheriteAxeUnlockDay::set).build());
        ranged.addEntry(weaponSub.build());

        var enchantSub = eb.startSubCategory(Component.translatable("buffmobs.config.rangedMeleeSwitching.enchantments"));
        enchantSub.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.rangedMeleeSwitching.enchantmentsEnabled"), cfg.rangedMeleeSwitching.enchantmentsEnabled.get())
                .setDefaultValue(true).setSaveConsumer(cfg.rangedMeleeSwitching.enchantmentsEnabled::set).build());
        enchantSub.add(eb.startIntSlider(Component.translatable("buffmobs.config.rangedMeleeSwitching.maxEnchantmentsPerWeapon"), cfg.rangedMeleeSwitching.maxEnchantmentsPerWeapon.get(), 1, 4)
                .setDefaultValue(2).setSaveConsumer(cfg.rangedMeleeSwitching.maxEnchantmentsPerWeapon::set).build());
        enchantSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.daysPerEnchantmentLevel"), cfg.rangedMeleeSwitching.daysPerEnchantmentLevel.get())
                .setDefaultValue(7).setMin(1).setMax(30).setSaveConsumer(cfg.rangedMeleeSwitching.daysPerEnchantmentLevel::set).build());
        enchantSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.sharpnessUnlockDay"), cfg.rangedMeleeSwitching.sharpnessUnlockDay.get())
                .setDefaultValue(0).setMin(0).setMax(365).setSaveConsumer(cfg.rangedMeleeSwitching.sharpnessUnlockDay::set).build());
        enchantSub.add(eb.startIntSlider(Component.translatable("buffmobs.config.rangedMeleeSwitching.sharpnessMaxLevel"), cfg.rangedMeleeSwitching.sharpnessMaxLevel.get(), 1, 5)
                .setDefaultValue(5).setSaveConsumer(cfg.rangedMeleeSwitching.sharpnessMaxLevel::set).build());
        enchantSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.fireAspectUnlockDay"), cfg.rangedMeleeSwitching.fireAspectUnlockDay.get())
                .setDefaultValue(14).setMin(0).setMax(365).setSaveConsumer(cfg.rangedMeleeSwitching.fireAspectUnlockDay::set).build());
        enchantSub.add(eb.startIntSlider(Component.translatable("buffmobs.config.rangedMeleeSwitching.fireAspectMaxLevel"), cfg.rangedMeleeSwitching.fireAspectMaxLevel.get(), 1, 2)
                .setDefaultValue(2).setSaveConsumer(cfg.rangedMeleeSwitching.fireAspectMaxLevel::set).build());
        enchantSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.knockbackUnlockDay"), cfg.rangedMeleeSwitching.knockbackUnlockDay.get())
                .setDefaultValue(7).setMin(0).setMax(365).setSaveConsumer(cfg.rangedMeleeSwitching.knockbackUnlockDay::set).build());
        enchantSub.add(eb.startIntSlider(Component.translatable("buffmobs.config.rangedMeleeSwitching.knockbackMaxLevel"), cfg.rangedMeleeSwitching.knockbackMaxLevel.get(), 1, 2)
                .setDefaultValue(2).setSaveConsumer(cfg.rangedMeleeSwitching.knockbackMaxLevel::set).build());
        enchantSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.sweepingEdgeUnlockDay"), cfg.rangedMeleeSwitching.sweepingEdgeUnlockDay.get())
                .setDefaultValue(21).setMin(0).setMax(365).setSaveConsumer(cfg.rangedMeleeSwitching.sweepingEdgeUnlockDay::set).build());
        enchantSub.add(eb.startIntSlider(Component.translatable("buffmobs.config.rangedMeleeSwitching.sweepingEdgeMaxLevel"), cfg.rangedMeleeSwitching.sweepingEdgeMaxLevel.get(), 1, 3)
                .setDefaultValue(3).setSaveConsumer(cfg.rangedMeleeSwitching.sweepingEdgeMaxLevel::set).build());
        ranged.addEntry(enchantSub.build());

        ConfigCategory draft = builder.getOrCreateCategory(Component.translatable("buffmobs.config.combatDraft"));
        draft.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.combatDraft.enabled"), cfg.combatDraft.enabled.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.combatDraft.enabled.tooltip"))
                .setSaveConsumer(cfg.combatDraft.enabled::set).build());
        draft.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.combatDraft.healthThreshold"), cfg.combatDraft.healthThreshold.get())
                .setDefaultValue(0.20).setMin(0.01).setMax(0.99)
                .setTooltip(tt("buffmobs.config.combatDraft.healthThreshold.tooltip"))
                .setSaveConsumer(cfg.combatDraft.healthThreshold::set).build());
        draft.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.combatDraft.regenAmplifier"), cfg.combatDraft.regenAmplifier.get(), 1, 10)
                .setDefaultValue(4)
                .setTextGetter(v -> Component.translatable("buffmobs.config.effects.slider.level", v))
                .setTooltip(tt("buffmobs.config.combatDraft.regenAmplifier.tooltip"))
                .setSaveConsumer(cfg.combatDraft.regenAmplifier::set).build());
        draft.addEntry(eb.startIntField(Component.translatable("buffmobs.config.combatDraft.regenDuration"), cfg.combatDraft.regenDuration.get())
                .setDefaultValue(10).setMin(1).setMax(120)
                .setTooltip(tt("buffmobs.config.combatDraft.regenDuration.tooltip"))
                .setSaveConsumer(cfg.combatDraft.regenDuration::set).build());
        draft.addEntry(eb.startIntField(Component.translatable("buffmobs.config.combatDraft.cooldownTicks"), cfg.combatDraft.cooldownTicks.get())
                .setDefaultValue(600).setMin(20).setMax(72000)
                .setTooltip(tt("buffmobs.config.combatDraft.cooldownTicks.tooltip"))
                .setSaveConsumer(cfg.combatDraft.cooldownTicks::set).build());
        draft.addEntry(eb.startIntField(Component.translatable("buffmobs.config.combatDraft.maxUses"), cfg.combatDraft.maxUses.get())
                .setDefaultValue(0).setMin(0).setMax(100)
                .setTooltip(tt("buffmobs.config.combatDraft.maxUses.tooltip"))
                .setSaveConsumer(cfg.combatDraft.maxUses::set).build());

        var draftFilterSub = eb.startSubCategory(Component.translatable("buffmobs.config.combatDraft.mobFilter"));
        draftFilterSub.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.combatDraft.mobFilter.useWhitelist"), cfg.combatDraft.useWhitelist.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.filter.useWhitelist.tooltip"))
                .setSaveConsumer(cfg.combatDraft.useWhitelist::set).build());
        draftFilterSub.add(eb.startStrList(Component.translatable("buffmobs.config.combatDraft.mobFilter.whitelist"), new ArrayList<>(cfg.combatDraft.whitelist.get()))
                .setDefaultValue(new ArrayList<>()).setSaveConsumer(v -> cfg.combatDraft.whitelist.set(v)).build());
        draftFilterSub.add(eb.startStrList(Component.translatable("buffmobs.config.combatDraft.mobFilter.blacklist"), new ArrayList<>(cfg.combatDraft.blacklist.get()))
                .setDefaultValue(new ArrayList<>()).setSaveConsumer(v -> cfg.combatDraft.blacklist.set(v)).build());
        draft.addEntry(draftFilterSub.build());

        ConfigCategory presets = builder.getOrCreateCategory(Component.translatable("buffmobs.config.mobPresets"));
        presets.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobPresets.enabled"), cfg.mobPresets.enabled.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.mobPresets.enabled.tooltip"))
                .setSaveConsumer(cfg.mobPresets.enabled::set).build());
        presets.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobPresets.overrideDimensionScaling"), cfg.mobPresets.overrideDimensionScaling.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.mobPresets.overrideDimensionScaling.tooltip"))
                .setSaveConsumer(cfg.mobPresets.overrideDimensionScaling::set).build());

        BuffMobsConfig.MobPresets.PresetSlot[] presetSlots = {
                cfg.mobPresets.preset1, cfg.mobPresets.preset2, cfg.mobPresets.preset3,
                cfg.mobPresets.preset4, cfg.mobPresets.preset5
        };
        for (int i = 0; i < presetSlots.length; i++) {
            BuffMobsConfig.MobPresets.PresetSlot p = presetSlots[i];
            int n = i + 1;
            String name = p.presetName.get();
            Component slotLabel = (name != null && !name.isEmpty())
                    ? Component.translatable("buffmobs.config.mobPresets.slot.named", n, name)
                    : Component.translatable("buffmobs.config.mobPresets.slot", n);
            var pSub = eb.startSubCategory(slotLabel);
            pSub.add(eb.startStrField(Component.translatable("buffmobs.config.mobPresets.presetName"), name == null ? "" : name)
                    .setTooltip(tt("buffmobs.config.mobPresets.presetName.tooltip"))
                    .setSaveConsumer(p.presetName::set).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.healthMultiplier"), p.healthMultiplier.get())
                    .setDefaultValue(1.0).setMin(0.01).setMax(999999.0).setSaveConsumer(p.healthMultiplier::set).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.damageMultiplier"), p.damageMultiplier.get())
                    .setDefaultValue(1.0).setMin(0.01).setMax(999999.0).setSaveConsumer(p.damageMultiplier::set).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.speedMultiplier"), p.speedMultiplier.get())
                    .setDefaultValue(1.0).setMin(0.01).setMax(999999.0).setSaveConsumer(p.speedMultiplier::set).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.attackSpeedMultiplier"), p.attackSpeedMultiplier.get())
                    .setDefaultValue(1.0).setMin(0.01).setMax(999999.0).setSaveConsumer(p.attackSpeedMultiplier::set).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.armorAddition"), p.armorAddition.get())
                    .setDefaultValue(0.0).setMin(0.0).setMax(999999.0).setSaveConsumer(p.armorAddition::set).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.toughnessAddition"), p.armorToughnessAddition.get())
                    .setDefaultValue(0.0).setMin(0.0).setMax(999999.0).setSaveConsumer(p.armorToughnessAddition::set).build());
            presets.addEntry(pSub.build());
        }
        presets.addEntry(eb.startStrList(Component.translatable("buffmobs.config.mobPresets.mobMapping"), new ArrayList<>(cfg.mobPresets.mobMapping.get()))
                .setDefaultValue(List.of("minecraft:zombie:default", "minecraft:skeleton:default",
                        "minecraft:ender_dragon:boss", "minecraft:wither:boss"))
                .setTooltip(tt("buffmobs.config.mobPresets.mobMapping.tooltip"))
                .setSaveConsumer(v -> cfg.mobPresets.mobMapping.set(v)).build());

        ConfigCategory passiveAggression = builder.getOrCreateCategory(Component.translatable("buffmobs.config.passiveMobAggression"));
        passiveAggression.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.passiveMobAggression.enabled"), cfg.passiveMobAggression.enabled.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.passiveMobAggression.enabled.tooltip"))
                .setSaveConsumer(v -> {
                    cfg.passiveMobAggression.enabled.set(v);
                    com.khotyz.buffmobs.event.PassiveMobAggressionHandler.forceReinit();
                }).build());
        passiveAggression.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.passiveMobAggression.baseDamage"), cfg.passiveMobAggression.baseDamage.get())
                .setDefaultValue(3.0).setMin(0.5).setMax(100.0)
                .setTooltip(tt("buffmobs.config.passiveMobAggression.baseDamage.tooltip"))
                .setSaveConsumer(cfg.passiveMobAggression.baseDamage::set).build());
        passiveAggression.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.passiveMobAggression.scaleWithHealth"), cfg.passiveMobAggression.scaleWithHealth.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.passiveMobAggression.scaleWithHealth.tooltip"))
                .setSaveConsumer(cfg.passiveMobAggression.scaleWithHealth::set).build());
        passiveAggression.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.passiveMobAggression.healthScaleFactor"), cfg.passiveMobAggression.healthScaleFactor.get())
                .setDefaultValue(0.1).setMin(0.0).setMax(100.0)
                .setTooltip(tt("buffmobs.config.passiveMobAggression.healthScaleFactor.tooltip"))
                .setSaveConsumer(cfg.passiveMobAggression.healthScaleFactor::set).build());

        var passiveMobFilterSub = eb.startSubCategory(Component.translatable("buffmobs.config.passiveMobAggression.mobFilter"));
        passiveMobFilterSub.add(eb.startStrList(Component.translatable("buffmobs.config.passiveMobAggression.whitelist"), new ArrayList<>(cfg.passiveMobAggression.whitelist.get()))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.passiveMobAggression.whitelist.tooltip"))
                .setSaveConsumer(v -> cfg.passiveMobAggression.whitelist.set(v)).build());
        passiveMobFilterSub.add(eb.startStrList(Component.translatable("buffmobs.config.passiveMobAggression.blacklist"), new ArrayList<>(cfg.passiveMobAggression.blacklist.get()))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.passiveMobAggression.blacklist.tooltip"))
                .setSaveConsumer(v -> cfg.passiveMobAggression.blacklist.set(v)).build());
        passiveAggression.addEntry(passiveMobFilterSub.build());

        ConfigCategory zombieHandling = builder.getOrCreateCategory(Component.translatable("buffmobs.config.zombieHandling"));
        zombieHandling.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.zombieHandling.disableLeaderZombies"), cfg.zombieHandling.disableLeaderZombies.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.zombieHandling.disableLeaderZombies.tooltip"))
                .setSaveConsumer(cfg.zombieHandling.disableLeaderZombies::set).build());
        zombieHandling.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.zombieHandling.excludeLeaderBonusFromMultiplier"), cfg.zombieHandling.excludeLeaderBonusFromMultiplier.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.zombieHandling.excludeLeaderBonusFromMultiplier.tooltip"))
                .setSaveConsumer(cfg.zombieHandling.excludeLeaderBonusFromMultiplier::set).build());

        ConfigCategory healthSync = builder.getOrCreateCategory(Component.translatable("buffmobs.config.healthSync"));
        healthSync.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.healthSync.enabled"), cfg.healthSync.enabled.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.healthSync.enabled.tooltip"))
                .setSaveConsumer(cfg.healthSync.enabled::set).build());
        healthSync.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.healthSync.mode"),
                        BuffMobsConfig.HealthSync.HealthSyncMode.class,
                        cfg.healthSync.mode.get())
                .setDefaultValue(BuffMobsConfig.HealthSync.HealthSyncMode.OVERRIDE)
                .setEnumNameProvider(e -> switch ((BuffMobsConfig.HealthSync.HealthSyncMode) e) {
                    case OVERRIDE -> Component.translatable("buffmobs.config.healthSync.mode.override");
                    case STACK    -> Component.translatable("buffmobs.config.healthSync.mode.stack");
                })
                .setTooltip(tt("buffmobs.config.healthSync.mode.tooltip"))
                .setSaveConsumer(cfg.healthSync.mode::set).build());

        ConfigCategory dimMaxHealth = builder.getOrCreateCategory(Component.translatable("buffmobs.config.dimensionMaxHealth"));
        dimMaxHealth.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.dimensionMaxHealth.enabled"), cfg.dimensionMaxHealth.enabled.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.dimensionMaxHealth.enabled.tooltip"))
                .setSaveConsumer(cfg.dimensionMaxHealth.enabled::set).build());

        BuffMobsConfig.DimensionMaxHealth.DimensionHealthSlot[] dimHealthSlots = {
                cfg.dimensionMaxHealth.slot1, cfg.dimensionMaxHealth.slot2, cfg.dimensionMaxHealth.slot3,
                cfg.dimensionMaxHealth.slot4, cfg.dimensionMaxHealth.slot5
        };
        for (int i = 0; i < dimHealthSlots.length; i++) {
            BuffMobsConfig.DimensionMaxHealth.DimensionHealthSlot slot = dimHealthSlots[i];
            int n = i + 1;
            var dhSub = eb.startSubCategory(Component.translatable("buffmobs.config.dimensionMaxHealth.slot", n));
            dhSub.add(eb.startStrField(Component.translatable("buffmobs.config.dimensionMaxHealth.dimensionId"), slot.dimensionName.get())
                    .setDefaultValue("")
                    .setTooltip(tt("buffmobs.config.dimensionMaxHealth.dimensionId.tooltip"))
                    .setSaveConsumer(slot.dimensionName::set).build());
            dhSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.dimensionMaxHealth.maxHealth"), slot.maxHealth.get())
                    .setDefaultValue(0.0).setMin(0.0).setMax(999999.0)
                    .setTooltip(tt("buffmobs.config.dimensionMaxHealth.maxHealth.tooltip"))
                    .setSaveConsumer(slot.maxHealth::set).build());
            dimMaxHealth.addEntry(dhSub.build());
        }

        var dimHealthFilterSub = eb.startSubCategory(Component.translatable("buffmobs.config.dimensionMaxHealth.mobFilter"));
        dimHealthFilterSub.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.dimensionMaxHealth.useAllowlist"), cfg.dimensionMaxHealth.useAllowlist.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.dimensionMaxHealth.useAllowlist.tooltip"))
                .setSaveConsumer(cfg.dimensionMaxHealth.useAllowlist::set).build());
        dimHealthFilterSub.add(eb.startStrList(Component.translatable("buffmobs.config.dimensionMaxHealth.allowlist"), new ArrayList<>(cfg.dimensionMaxHealth.allowlist.get()))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.dimensionMaxHealth.allowlist.tooltip"))
                .setSaveConsumer(v -> cfg.dimensionMaxHealth.allowlist.set(v)).build());
        dimHealthFilterSub.add(eb.startStrList(Component.translatable("buffmobs.config.dimensionMaxHealth.denylist"), new ArrayList<>(cfg.dimensionMaxHealth.denylist.get()))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.dimensionMaxHealth.denylist.tooltip"))
                .setSaveConsumer(v -> cfg.dimensionMaxHealth.denylist.set(v)).build());
        dimMaxHealth.addEntry(dimHealthFilterSub.build());

        return builder.build();
    }

}