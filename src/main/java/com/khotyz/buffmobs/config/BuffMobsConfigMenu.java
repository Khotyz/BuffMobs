package com.khotyz.buffmobs.config;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class BuffMobsConfigMenu {

    private static Component tt(String key) {
        return Component.translatable(key);
    }

    public static List<ConfigMenu.Node> create() {
        ConfigMenu.Builder builder = ConfigMenu.Builder.create();

        ConfigMenu.EntryBuilder eb = builder.entryBuilder();
        BuffMobsConfig cfg = BuffMobsConfig.INSTANCE;

        ConfigMenu.Category general = builder.getOrCreateCategory(Component.translatable("buffmobs.config.general"));
        general.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.general.enabled"), cfg.enabled.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.general.enabled.tooltip"))
                .setSaveConsumer(cfg.enabled::set).build());
        general.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.general.visualEffects"), cfg.visualEffects.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.general.visualEffects.tooltip"))
                .setSaveConsumer(cfg.visualEffects::set).build());
        general.addEntry(eb.startTextDescription(Component.translatable("buffmobs.config.credits.translation")).build());

        ConfigMenu.Category attributes = builder.getOrCreateCategory(Component.translatable("buffmobs.config.attributes"));
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

        ConfigMenu.Category effects = builder.getOrCreateCategory(Component.translatable("buffmobs.config.effects"));
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

        ConfigMenu.Category harmful = builder.getOrCreateCategory(Component.translatable("buffmobs.config.harmfulEffects"));
        harmful.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.harmfulEffects.enabled"), cfg.harmfulEffects.enabled.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.harmfulEffects.enabled.tooltip"))
                .setSaveConsumer(cfg.harmfulEffects.enabled::set).build());
        harmful.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.harmfulEffects.chance"), cfg.harmfulEffects.chance.get())
                .setDefaultValue(0.15).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.harmfulEffects.chance.tooltip"))
                .setSaveConsumer(cfg.harmfulEffects.chance::set).build());
        harmful.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.harmfulEffects.poisonEnabled"), cfg.harmfulEffects.poisonEnabled.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.harmfulEffects.poisonEnabled.tooltip"))
                .setSaveConsumer(cfg.harmfulEffects.poisonEnabled::set).build());
        harmful.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.harmfulEffects.slownessEnabled"), cfg.harmfulEffects.slownessEnabled.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.harmfulEffects.slownessEnabled.tooltip"))
                .setSaveConsumer(cfg.harmfulEffects.slownessEnabled::set).build());
        harmful.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.harmfulEffects.witherEnabled"), cfg.harmfulEffects.witherEnabled.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.harmfulEffects.witherEnabled.tooltip"))
                .setSaveConsumer(cfg.harmfulEffects.witherEnabled::set).build());
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

        ConfigMenu.Category dayScaling = builder.getOrCreateCategory(Component.translatable("buffmobs.config.dayScaling"));
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

        ConfigMenu.Category dimScaling = builder.getOrCreateCategory(Component.translatable("buffmobs.config.dimensionScaling"));
        dimScaling.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.dimensionScaling.mode"),
                        BuffMobsConfig.DimensionScaling.DimensionScalingMode.class,
                        cfg.dimensionScaling.mode.get())
                .setDefaultValue(BuffMobsConfig.DimensionScaling.DimensionScalingMode.SCALING)
                .setEnumNameProvider(e -> switch ((BuffMobsConfig.DimensionScaling.DimensionScalingMode) e) {
                    case SCALING  -> Component.translatable("buffmobs.config.dimensionScaling.mode.scaling");
                    case OVERRIDE -> Component.translatable("buffmobs.config.dimensionScaling.mode.override");
                })
                .setTooltip(tt("buffmobs.config.dimensionScaling.mode.tooltip"))
                .setSaveConsumer(cfg.dimensionScaling.mode::set).build());

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

        ConfigMenu.Category mobFilter = builder.getOrCreateCategory(Component.translatable("buffmobs.config.mobFilter"));
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

        ConfigMenu.Category modFilter = builder.getOrCreateCategory(Component.translatable("buffmobs.config.modidFilter"));
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

        ConfigMenu.Category dimFilter = builder.getOrCreateCategory(Component.translatable("buffmobs.config.dimensionFilter"));
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

        ConfigMenu.Category ranged = builder.getOrCreateCategory(Component.translatable("buffmobs.config.rangedMeleeSwitching"));
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

        ConfigMenu.Category draft = builder.getOrCreateCategory(Component.translatable("buffmobs.config.combatDraft"));
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

        ConfigMenu.Category presets = builder.getOrCreateCategory(Component.translatable("buffmobs.config.mobPresets"));
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
        double[][] presetDefaults = {
                {1.0, 1.0, 1.0, 1.0, 0.0, 0.0},
                {3.0, 2.5, 1.2, 1.5, 10.0, 5.0},
                {2.0, 1.8, 1.1, 1.2, 5.0, 2.0},
                {0.5, 0.5, 0.9, 0.8, 0.0, 0.0},
                {1.0, 1.0, 1.0, 1.0, 0.0, 0.0}
        };
        for (int i = 0; i < presetSlots.length; i++) {
            BuffMobsConfig.MobPresets.PresetSlot p = presetSlots[i];
            double[] presetDefault = presetDefaults[i];
            int n = i + 1;
            String name = p.presetName.get();
            String builtInPresetName = n == 1 ? "default" : n == 2 ? "boss" : "";
            var pSub = eb.startSubCategory(() -> {
                String currentName = p.presetName.get();
                return currentName != null && !currentName.isEmpty() && !currentName.equals(builtInPresetName)
                        ? Component.translatable("buffmobs.config.mobPresets.slot.named", n, currentName)
                        : Component.translatable("buffmobs.config.mobPresets.slot", n);
            });
            pSub.add(eb.startStrField(Component.translatable("buffmobs.config.mobPresets.presetName"), name == null ? "" : name)
                    .setTooltip(tt("buffmobs.config.mobPresets.presetName.tooltip"))
                    .setSaveConsumer(p.presetName::set).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.healthMultiplier"), p.healthMultiplier.get())
                    .setDefaultValue(presetDefault[0]).setMin(0.01).setMax(999999.0).setSaveConsumer(p.healthMultiplier::set).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.damageMultiplier"), p.damageMultiplier.get())
                    .setDefaultValue(presetDefault[1]).setMin(0.01).setMax(999999.0).setSaveConsumer(p.damageMultiplier::set).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.speedMultiplier"), p.speedMultiplier.get())
                    .setDefaultValue(presetDefault[2]).setMin(0.01).setMax(999999.0).setSaveConsumer(p.speedMultiplier::set).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.attackSpeedMultiplier"), p.attackSpeedMultiplier.get())
                    .setDefaultValue(presetDefault[3]).setMin(0.01).setMax(999999.0).setSaveConsumer(p.attackSpeedMultiplier::set).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.armorAddition"), p.armorAddition.get())
                    .setDefaultValue(presetDefault[4]).setMin(0.0).setMax(999999.0).setSaveConsumer(p.armorAddition::set).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.toughnessAddition"), p.armorToughnessAddition.get())
                    .setDefaultValue(presetDefault[5]).setMin(0.0).setMax(999999.0).setSaveConsumer(p.armorToughnessAddition::set).build());
            presets.addEntry(pSub.build());
        }
        presets.addEntry(eb.startStrList(Component.translatable("buffmobs.config.mobPresets.mobMapping"), new ArrayList<>(cfg.mobPresets.mobMapping.get()))
                .setDefaultValue(List.of("minecraft:zombie:default", "minecraft:skeleton:default",
                        "minecraft:ender_dragon:boss", "minecraft:wither:boss"))
                .setTooltip(tt("buffmobs.config.mobPresets.mobMapping.tooltip"))
                .setSaveConsumer(v -> cfg.mobPresets.mobMapping.set(v)).build());

        ConfigMenu.Category passiveAggression = builder.getOrCreateCategory(Component.translatable("buffmobs.config.passiveMobAggression"));
        passiveAggression.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.passiveMobAggression.mode"),
                        BuffMobsConfig.PassiveMobAggression.PassiveMobMode.class,
                        cfg.passiveMobAggression.mode.get())
                .setDefaultValue(BuffMobsConfig.PassiveMobAggression.PassiveMobMode.OFF)
                .setEnumNameProvider(e -> switch ((BuffMobsConfig.PassiveMobAggression.PassiveMobMode) e) {
                    case OFF     -> Component.translatable("buffmobs.config.passiveMobAggression.mode.off");
                    case NEUTRAL -> Component.translatable("buffmobs.config.passiveMobAggression.mode.neutral");
                    case HOSTILE -> Component.translatable("buffmobs.config.passiveMobAggression.mode.hostile");
                })
                .setTooltip(tt("buffmobs.config.passiveMobAggression.mode.tooltip"))
                .setSaveConsumer(v -> {
                    cfg.passiveMobAggression.mode.set(v);
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

        ConfigMenu.Category zombieHandling = builder.getOrCreateCategory(Component.translatable("buffmobs.config.zombieHandling"));
        zombieHandling.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.zombieHandling.disableLeaderZombies"), cfg.zombieHandling.disableLeaderZombies.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.zombieHandling.disableLeaderZombies.tooltip"))
                .setSaveConsumer(cfg.zombieHandling.disableLeaderZombies::set).build());
        zombieHandling.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.zombieHandling.excludeLeaderBonusFromMultiplier"), cfg.zombieHandling.excludeLeaderBonusFromMultiplier.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.zombieHandling.excludeLeaderBonusFromMultiplier.tooltip"))
                .setSaveConsumer(cfg.zombieHandling.excludeLeaderBonusFromMultiplier::set).build());

        ConfigMenu.Category healthSync = builder.getOrCreateCategory(Component.translatable("buffmobs.config.healthSync"));
        healthSync.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.healthSync.enabled"), cfg.healthSync.enabled.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.healthSync.enabled.tooltip"))
                .setSaveConsumer(cfg.healthSync.enabled::set).build());
        healthSync.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.healthSync.mode"),
                        BuffMobsConfig.HealthSync.Mode.class,
                        cfg.healthSync.mode.get())
                .setDefaultValue(BuffMobsConfig.HealthSync.Mode.OVERRIDE)
                .setEnumNameProvider(e -> switch ((BuffMobsConfig.HealthSync.Mode) e) {
                    case OVERRIDE -> Component.translatable("buffmobs.config.healthSync.mode.override");
                    case STACK    -> Component.translatable("buffmobs.config.healthSync.mode.stack");
                })
                .setTooltip(tt("buffmobs.config.healthSync.mode.tooltip"))
                .setSaveConsumer(cfg.healthSync.mode::set).build());

        ConfigMenu.Category dimMaxHealth = builder.getOrCreateCategory(Component.translatable("buffmobs.config.dimensionMaxHealth"));
        dimMaxHealth.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.dimensionMaxHealth.enabled"), cfg.dimensionMaxHealth.enabled.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.dimensionMaxHealth.enabled.tooltip"))
                .setSaveConsumer(cfg.dimensionMaxHealth.enabled::set).build());

        BuffMobsConfig.DimensionMaxHealth.Slot[] dimHealthSlots = {
                cfg.dimensionMaxHealth.slot1, cfg.dimensionMaxHealth.slot2, cfg.dimensionMaxHealth.slot3,
                cfg.dimensionMaxHealth.slot4, cfg.dimensionMaxHealth.slot5
        };
        for (int i = 0; i < dimHealthSlots.length; i++) {
            BuffMobsConfig.DimensionMaxHealth.Slot slot = dimHealthSlots[i];
            int n = i + 1;
            var dhSub = eb.startSubCategory(Component.translatable("buffmobs.config.dimensionMaxHealth.slot", n));
            dhSub.add(eb.startStrField(Component.translatable("buffmobs.config.dimensionMaxHealth.dimensionId"), slot.dimensionId.get())
                    .setDefaultValue("")
                    .setTooltip(tt("buffmobs.config.dimensionMaxHealth.dimensionId.tooltip"))
                    .setSaveConsumer(slot.dimensionId::set).build());
            dhSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.dimensionMaxHealth.maxHealth"), slot.maxHealth.get())
                    .setDefaultValue(20.0).setMin(1.0).setMax(999999.0)
                    .setTooltip(tt("buffmobs.config.dimensionMaxHealth.maxHealth.tooltip"))
                    .setSaveConsumer(slot.maxHealth::set).build());
            var dhFilterSub = eb.startSubCategory(Component.translatable("buffmobs.config.dimensionMaxHealth.mobFilter"));
            dhFilterSub.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.dimensionMaxHealth.useAllowlist"), slot.useAllowlist.get())
                    .setDefaultValue(false)
                    .setTooltip(tt("buffmobs.config.dimensionMaxHealth.useAllowlist.tooltip"))
                    .setSaveConsumer(slot.useAllowlist::set).build());
            dhFilterSub.add(eb.startStrList(Component.translatable("buffmobs.config.dimensionMaxHealth.allowlist"), new ArrayList<>(slot.allowlist.get()))
                    .setDefaultValue(new ArrayList<>())
                    .setTooltip(tt("buffmobs.config.dimensionMaxHealth.allowlist.tooltip"))
                    .setSaveConsumer(v -> slot.allowlist.set(v)).build());
            dhFilterSub.add(eb.startStrList(Component.translatable("buffmobs.config.dimensionMaxHealth.denylist"), new ArrayList<>(slot.denylist.get()))
                    .setDefaultValue(new ArrayList<>())
                    .setTooltip(tt("buffmobs.config.dimensionMaxHealth.denylist.tooltip"))
                    .setSaveConsumer(v -> slot.denylist.set(v)).build());
            dhSub.add(dhFilterSub.build());
            dimMaxHealth.addEntry(dhSub.build());
        }

        ConfigMenu.Category mobArmor = builder.getOrCreateCategory(Component.translatable("buffmobs.config.mobArmor"));
        mobArmor.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobArmor.enabled"), cfg.mobArmor.enabled.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.mobArmor.enabled.tooltip"))
                .setSaveConsumer(cfg.mobArmor.enabled::set).build());
        mobArmor.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.equipChance"), cfg.mobArmor.equipChance.get())
                .setDefaultValue(1.0).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.mobArmor.equipChance.tooltip"))
                .setSaveConsumer(cfg.mobArmor.equipChance::set).build());
        var mobArmorSetup = eb.startSubCategory(Component.translatable("buffmobs.config.mobArmor.equipmentSetup"));
        mobArmorSetup.add(eb.startEnumSelector(Component.translatable("buffmobs.config.mobArmor.setupMode"),
                        BuffMobsConfig.MobArmor.EquipmentSetupMode.class,
                        cfg.mobArmor.setupMode.get())
                .setDefaultValue(BuffMobsConfig.MobArmor.EquipmentSetupMode.FULL_SET)
                .setEnumNameProvider(e -> switch ((BuffMobsConfig.MobArmor.EquipmentSetupMode) e) {
                    case FULL_SET -> Component.translatable("buffmobs.config.mobArmor.setupMode.full_set");
                    case RANDOM_PIECES -> Component.translatable("buffmobs.config.mobArmor.setupMode.random_pieces");
                })
                .setTooltip(tt("buffmobs.config.mobArmor.setupMode.tooltip"))
                .setSaveConsumer(cfg.mobArmor.setupMode::set).build());
        mobArmorSetup.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobArmor.mixedTiersEnabled"), cfg.mobArmor.mixedTiersEnabled.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.mobArmor.mixedTiersEnabled.tooltip"))
                .setSaveConsumer(cfg.mobArmor.mixedTiersEnabled::set).build());
        mobArmorSetup.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.helmetChance"), cfg.mobArmor.helmetChance.get())
                .setDefaultValue(1.0).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.mobArmor.helmetChance.tooltip"))
                .setSaveConsumer(cfg.mobArmor.helmetChance::set).build());
        mobArmorSetup.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.chestplateChance"), cfg.mobArmor.chestplateChance.get())
                .setDefaultValue(1.0).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.mobArmor.chestplateChance.tooltip"))
                .setSaveConsumer(cfg.mobArmor.chestplateChance::set).build());
        mobArmorSetup.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.leggingsChance"), cfg.mobArmor.leggingsChance.get())
                .setDefaultValue(1.0).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.mobArmor.leggingsChance.tooltip"))
                .setSaveConsumer(cfg.mobArmor.leggingsChance::set).build());
        mobArmorSetup.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.bootsChance"), cfg.mobArmor.bootsChance.get())
                .setDefaultValue(1.0).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.mobArmor.bootsChance.tooltip"))
                .setSaveConsumer(cfg.mobArmor.bootsChance::set).build());
        mobArmorSetup.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.minPieces"), cfg.mobArmor.minPieces.get())
                .setDefaultValue(1).setMin(0).setMax(4)
                .setTooltip(tt("buffmobs.config.mobArmor.minPieces.tooltip"))
                .setSaveConsumer(cfg.mobArmor.minPieces::set).build());
        mobArmorSetup.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.maxPieces"), cfg.mobArmor.maxPieces.get())
                .setDefaultValue(4).setMin(0).setMax(4)
                .setTooltip(tt("buffmobs.config.mobArmor.maxPieces.tooltip"))
                .setSaveConsumer(cfg.mobArmor.maxPieces::set).build());
        mobArmorSetup.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobArmor.dropChanceEnabled"), cfg.mobArmor.dropChanceEnabled.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.mobArmor.dropChanceEnabled.tooltip"))
                .setSaveConsumer(cfg.mobArmor.dropChanceEnabled::set).build());
        mobArmorSetup.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.dropChance"), cfg.mobArmor.dropChance.get())
                .setDefaultValue(0.085).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.mobArmor.dropChance.tooltip"))
                .setSaveConsumer(cfg.mobArmor.dropChance::set).build());
        mobArmor.addEntry(mobArmorSetup.build());
        var mobArmorSub1 = eb.startSubCategory(Component.translatable("buffmobs.config.mobArmor.tierUnlockDays"));
        mobArmorSub1.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.leatherUnlockDay"), cfg.mobArmor.leatherUnlockDay.get())
                .setDefaultValue(0).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.leatherUnlockDay.tooltip"))
                .setSaveConsumer(cfg.mobArmor.leatherUnlockDay::set).build());
        mobArmorSub1.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.goldUnlockDay"), cfg.mobArmor.goldUnlockDay.get())
                .setDefaultValue(7).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.goldUnlockDay.tooltip"))
                .setSaveConsumer(cfg.mobArmor.goldUnlockDay::set).build());
        mobArmorSub1.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.ironUnlockDay"), cfg.mobArmor.ironUnlockDay.get())
                .setDefaultValue(14).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.ironUnlockDay.tooltip"))
                .setSaveConsumer(cfg.mobArmor.ironUnlockDay::set).build());
        mobArmorSub1.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.diamondUnlockDay"), cfg.mobArmor.diamondUnlockDay.get())
                .setDefaultValue(30).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.diamondUnlockDay.tooltip"))
                .setSaveConsumer(cfg.mobArmor.diamondUnlockDay::set).build());
        mobArmorSub1.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.netheriteUnlockDay"), cfg.mobArmor.netheriteUnlockDay.get())
                .setDefaultValue(60).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.netheriteUnlockDay.tooltip"))
                .setSaveConsumer(cfg.mobArmor.netheriteUnlockDay::set).build());
        mobArmor.addEntry(mobArmorSub1.build());
        var mobArmorSub2 = eb.startSubCategory(Component.translatable("buffmobs.config.mobArmor.tierWeights"));
        mobArmorSub2.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.leatherWeight"), cfg.mobArmor.leatherWeight.get())
                .setDefaultValue(50.0).setMin(0.0).setMax(1000.0)
                .setTooltip(tt("buffmobs.config.mobArmor.leatherWeight.tooltip"))
                .setSaveConsumer(cfg.mobArmor.leatherWeight::set).build());
        mobArmorSub2.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.goldWeight"), cfg.mobArmor.goldWeight.get())
                .setDefaultValue(25.0).setMin(0.0).setMax(1000.0)
                .setTooltip(tt("buffmobs.config.mobArmor.goldWeight.tooltip"))
                .setSaveConsumer(cfg.mobArmor.goldWeight::set).build());
        mobArmorSub2.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.ironWeight"), cfg.mobArmor.ironWeight.get())
                .setDefaultValue(15.0).setMin(0.0).setMax(1000.0)
                .setTooltip(tt("buffmobs.config.mobArmor.ironWeight.tooltip"))
                .setSaveConsumer(cfg.mobArmor.ironWeight::set).build());
        mobArmorSub2.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.diamondWeight"), cfg.mobArmor.diamondWeight.get())
                .setDefaultValue(4.0).setMin(0.0).setMax(1000.0)
                .setTooltip(tt("buffmobs.config.mobArmor.diamondWeight.tooltip"))
                .setSaveConsumer(cfg.mobArmor.diamondWeight::set).build());
        mobArmorSub2.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.netheriteWeight"), cfg.mobArmor.netheriteWeight.get())
                .setDefaultValue(1.0).setMin(0.0).setMax(1000.0)
                .setTooltip(tt("buffmobs.config.mobArmor.netheriteWeight.tooltip"))
                .setSaveConsumer(cfg.mobArmor.netheriteWeight::set).build());
        mobArmor.addEntry(mobArmorSub2.build());
        var mobArmorSub3 = eb.startSubCategory(Component.translatable("buffmobs.config.mobArmor.enchantments"));
        mobArmorSub3.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobArmor.enchantmentsEnabled"), cfg.mobArmor.enchantmentsEnabled.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.mobArmor.enchantmentsEnabled.tooltip"))
                .setSaveConsumer(cfg.mobArmor.enchantmentsEnabled::set).build());
        mobArmorSub3.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.maxEnchantmentsPerPiece"), cfg.mobArmor.maxEnchantmentsPerPiece.get())
                .setDefaultValue(2).setMin(1).setMax(4)
                .setTooltip(tt("buffmobs.config.mobArmor.maxEnchantmentsPerPiece.tooltip"))
                .setSaveConsumer(cfg.mobArmor.maxEnchantmentsPerPiece::set).build());
        mobArmorSub3.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.daysPerEnchantmentLevel"), cfg.mobArmor.daysPerEnchantmentLevel.get())
                .setDefaultValue(7).setMin(1).setMax(30)
                .setTooltip(tt("buffmobs.config.mobArmor.daysPerEnchantmentLevel.tooltip"))
                .setSaveConsumer(cfg.mobArmor.daysPerEnchantmentLevel::set).build());
        mobArmorSub3.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.enchantedPieceChance"), cfg.mobArmor.enchantedPieceChance.get())
                .setDefaultValue(1.0).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.mobArmor.enchantedPieceChance.tooltip"))
                .setSaveConsumer(cfg.mobArmor.enchantedPieceChance::set).build());
        mobArmorSub3.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.protectionUnlockDay"), cfg.mobArmor.protectionUnlockDay.get())
                .setDefaultValue(7).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.protectionUnlockDay.tooltip"))
                .setSaveConsumer(cfg.mobArmor.protectionUnlockDay::set).build());
        mobArmorSub3.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.protectionMaxLevel"), cfg.mobArmor.protectionMaxLevel.get())
                .setDefaultValue(4).setMin(1).setMax(4)
                .setTooltip(tt("buffmobs.config.mobArmor.protectionMaxLevel.tooltip"))
                .setSaveConsumer(cfg.mobArmor.protectionMaxLevel::set).build());
        mobArmorSub3.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.thornsUnlockDay"), cfg.mobArmor.thornsUnlockDay.get())
                .setDefaultValue(21).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.thornsUnlockDay.tooltip"))
                .setSaveConsumer(cfg.mobArmor.thornsUnlockDay::set).build());
        mobArmorSub3.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.thornsMaxLevel"), cfg.mobArmor.thornsMaxLevel.get())
                .setDefaultValue(3).setMin(1).setMax(3)
                .setTooltip(tt("buffmobs.config.mobArmor.thornsMaxLevel.tooltip"))
                .setSaveConsumer(cfg.mobArmor.thornsMaxLevel::set).build());
        mobArmorSub3.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.featherFallingUnlockDay"), cfg.mobArmor.featherFallingUnlockDay.get())
                .setDefaultValue(14).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.featherFallingUnlockDay.tooltip"))
                .setSaveConsumer(cfg.mobArmor.featherFallingUnlockDay::set).build());
        mobArmorSub3.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.featherFallingMaxLevel"), cfg.mobArmor.featherFallingMaxLevel.get())
                .setDefaultValue(4).setMin(1).setMax(4)
                .setTooltip(tt("buffmobs.config.mobArmor.featherFallingMaxLevel.tooltip"))
                .setSaveConsumer(cfg.mobArmor.featherFallingMaxLevel::set).build());
        var mobArmorAdditionalEnchantments = eb.startSubCategory(Component.translatable("buffmobs.config.mobArmor.additionalEnchantments"));
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.fireProtectionUnlockDay"), cfg.mobArmor.fireProtectionUnlockDay.get())
                .setDefaultValue(14).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.fireProtectionUnlockDay.tooltip"))
                .setSaveConsumer(cfg.mobArmor.fireProtectionUnlockDay::set).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.fireProtectionMaxLevel"), cfg.mobArmor.fireProtectionMaxLevel.get())
                .setDefaultValue(4).setMin(1).setMax(4)
                .setTooltip(tt("buffmobs.config.mobArmor.fireProtectionMaxLevel.tooltip"))
                .setSaveConsumer(cfg.mobArmor.fireProtectionMaxLevel::set).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.blastProtectionUnlockDay"), cfg.mobArmor.blastProtectionUnlockDay.get())
                .setDefaultValue(21).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.blastProtectionUnlockDay.tooltip"))
                .setSaveConsumer(cfg.mobArmor.blastProtectionUnlockDay::set).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.blastProtectionMaxLevel"), cfg.mobArmor.blastProtectionMaxLevel.get())
                .setDefaultValue(4).setMin(1).setMax(4)
                .setTooltip(tt("buffmobs.config.mobArmor.blastProtectionMaxLevel.tooltip"))
                .setSaveConsumer(cfg.mobArmor.blastProtectionMaxLevel::set).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.projectileProtectionUnlockDay"), cfg.mobArmor.projectileProtectionUnlockDay.get())
                .setDefaultValue(14).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.projectileProtectionUnlockDay.tooltip"))
                .setSaveConsumer(cfg.mobArmor.projectileProtectionUnlockDay::set).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.projectileProtectionMaxLevel"), cfg.mobArmor.projectileProtectionMaxLevel.get())
                .setDefaultValue(4).setMin(1).setMax(4)
                .setTooltip(tt("buffmobs.config.mobArmor.projectileProtectionMaxLevel.tooltip"))
                .setSaveConsumer(cfg.mobArmor.projectileProtectionMaxLevel::set).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.unbreakingUnlockDay"), cfg.mobArmor.unbreakingUnlockDay.get())
                .setDefaultValue(0).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.unbreakingUnlockDay.tooltip"))
                .setSaveConsumer(cfg.mobArmor.unbreakingUnlockDay::set).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.unbreakingMaxLevel"), cfg.mobArmor.unbreakingMaxLevel.get())
                .setDefaultValue(3).setMin(1).setMax(3)
                .setTooltip(tt("buffmobs.config.mobArmor.unbreakingMaxLevel.tooltip"))
                .setSaveConsumer(cfg.mobArmor.unbreakingMaxLevel::set).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.respirationUnlockDay"), cfg.mobArmor.respirationUnlockDay.get())
                .setDefaultValue(14).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.respirationUnlockDay.tooltip"))
                .setSaveConsumer(cfg.mobArmor.respirationUnlockDay::set).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.respirationMaxLevel"), cfg.mobArmor.respirationMaxLevel.get())
                .setDefaultValue(3).setMin(1).setMax(3)
                .setTooltip(tt("buffmobs.config.mobArmor.respirationMaxLevel.tooltip"))
                .setSaveConsumer(cfg.mobArmor.respirationMaxLevel::set).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.aquaAffinityUnlockDay"), cfg.mobArmor.aquaAffinityUnlockDay.get())
                .setDefaultValue(21).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.aquaAffinityUnlockDay.tooltip"))
                .setSaveConsumer(cfg.mobArmor.aquaAffinityUnlockDay::set).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.aquaAffinityMaxLevel"), cfg.mobArmor.aquaAffinityMaxLevel.get())
                .setDefaultValue(1).setMin(1).setMax(1)
                .setTooltip(tt("buffmobs.config.mobArmor.aquaAffinityMaxLevel.tooltip"))
                .setSaveConsumer(cfg.mobArmor.aquaAffinityMaxLevel::set).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.depthStriderUnlockDay"), cfg.mobArmor.depthStriderUnlockDay.get())
                .setDefaultValue(21).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.depthStriderUnlockDay.tooltip"))
                .setSaveConsumer(cfg.mobArmor.depthStriderUnlockDay::set).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.depthStriderMaxLevel"), cfg.mobArmor.depthStriderMaxLevel.get())
                .setDefaultValue(3).setMin(1).setMax(3)
                .setTooltip(tt("buffmobs.config.mobArmor.depthStriderMaxLevel.tooltip"))
                .setSaveConsumer(cfg.mobArmor.depthStriderMaxLevel::set).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.frostWalkerUnlockDay"), cfg.mobArmor.frostWalkerUnlockDay.get())
                .setDefaultValue(28).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.frostWalkerUnlockDay.tooltip"))
                .setSaveConsumer(cfg.mobArmor.frostWalkerUnlockDay::set).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.frostWalkerMaxLevel"), cfg.mobArmor.frostWalkerMaxLevel.get())
                .setDefaultValue(2).setMin(1).setMax(2)
                .setTooltip(tt("buffmobs.config.mobArmor.frostWalkerMaxLevel.tooltip"))
                .setSaveConsumer(cfg.mobArmor.frostWalkerMaxLevel::set).build());
        mobArmorSub3.add(mobArmorAdditionalEnchantments.build());
        var mobArmorCurses = eb.startSubCategory(Component.translatable("buffmobs.config.mobArmor.curses"));
        mobArmorCurses.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobArmor.cursesEnabled"), cfg.mobArmor.cursesEnabled.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.mobArmor.cursesEnabled.tooltip"))
                .setSaveConsumer(cfg.mobArmor.cursesEnabled::set).build());
        mobArmorCurses.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.curseChance"), cfg.mobArmor.curseChance.get())
                .setDefaultValue(0.1).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.mobArmor.curseChance.tooltip"))
                .setSaveConsumer(cfg.mobArmor.curseChance::set).build());
        mobArmorCurses.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.curseUnlockDay"), cfg.mobArmor.curseUnlockDay.get())
                .setDefaultValue(30).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.curseUnlockDay.tooltip"))
                .setSaveConsumer(cfg.mobArmor.curseUnlockDay::set).build());
        mobArmorCurses.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobArmor.bindingCurseEnabled"), cfg.mobArmor.bindingCurseEnabled.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.mobArmor.bindingCurseEnabled.tooltip"))
                .setSaveConsumer(cfg.mobArmor.bindingCurseEnabled::set).build());
        mobArmorCurses.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobArmor.vanishingCurseEnabled"), cfg.mobArmor.vanishingCurseEnabled.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.mobArmor.vanishingCurseEnabled.tooltip"))
                .setSaveConsumer(cfg.mobArmor.vanishingCurseEnabled::set).build());
        mobArmorSub3.add(mobArmorCurses.build());
        mobArmor.addEntry(mobArmorSub3.build());
        mobArmor.addEntry(eb.startStrList(Component.translatable("buffmobs.config.mobArmor.additionalArmorMobs"), new ArrayList<>(cfg.mobArmor.additionalArmorMobs.get()))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.mobArmor.additionalArmorMobs.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.additionalArmorMobs.set(v)).build());

        ConfigMenu.Category eliteMobs = builder.getOrCreateCategory(Component.translatable("buffmobs.config.eliteMobs"));
        eliteMobs.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.eliteMobs.enabled"), cfg.eliteMobs.enabled.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.eliteMobs.enabled.tooltip"))
                .setSaveConsumer(cfg.eliteMobs.enabled::set).build());
        eliteMobs.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.eliteMobs.baseChance"), cfg.eliteMobs.baseChance.get())
                .setDefaultValue(0.02).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.eliteMobs.baseChance.tooltip"))
                .setSaveConsumer(cfg.eliteMobs.baseChance::set).build());
        eliteMobs.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.eliteMobs.chanceIncreasePerDay"), cfg.eliteMobs.chanceIncreasePerDay.get())
                .setDefaultValue(0.001).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.eliteMobs.chanceIncreasePerDay.tooltip"))
                .setSaveConsumer(cfg.eliteMobs.chanceIncreasePerDay::set).build());
        eliteMobs.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.eliteMobs.maxChance"), cfg.eliteMobs.maxChance.get())
                .setDefaultValue(0.25).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.eliteMobs.maxChance.tooltip"))
                .setSaveConsumer(cfg.eliteMobs.maxChance::set).build());
        eliteMobs.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.eliteMobs.healthMultiplier"), cfg.eliteMobs.healthMultiplier.get())
                .setDefaultValue(3.0).setMin(1.0).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.eliteMobs.healthMultiplier.tooltip"))
                .setSaveConsumer(cfg.eliteMobs.healthMultiplier::set).build());
        eliteMobs.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.eliteMobs.damageMultiplier"), cfg.eliteMobs.damageMultiplier.get())
                .setDefaultValue(2.0).setMin(1.0).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.eliteMobs.damageMultiplier.tooltip"))
                .setSaveConsumer(cfg.eliteMobs.damageMultiplier::set).build());
        eliteMobs.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.eliteMobs.nameColor"),
                        BuffMobsConfig.EliteMobs.NameColor.class,
                        cfg.eliteMobs.nameColor.get())
                .setDefaultValue(BuffMobsConfig.EliteMobs.NameColor.GOLD)
                .setEnumNameProvider(e -> switch ((BuffMobsConfig.EliteMobs.NameColor) e) {
                    case GOLD -> Component.translatable("buffmobs.config.eliteMobs.nameColor.gold");
                    case RED -> Component.translatable("buffmobs.config.eliteMobs.nameColor.red");
                    case DARK_RED -> Component.translatable("buffmobs.config.eliteMobs.nameColor.dark_red");
                    case LIGHT_PURPLE -> Component.translatable("buffmobs.config.eliteMobs.nameColor.light_purple");
                    case AQUA -> Component.translatable("buffmobs.config.eliteMobs.nameColor.aqua");
                    case YELLOW -> Component.translatable("buffmobs.config.eliteMobs.nameColor.yellow");
                    case RANDOM -> Component.translatable("buffmobs.config.eliteMobs.nameColor.random");
                    case CUSTOM -> Component.translatable("buffmobs.config.eliteMobs.nameColor.custom");
                })
                .setTooltip(tt("buffmobs.config.eliteMobs.nameColor.tooltip"))
                .setSaveConsumer(cfg.eliteMobs.nameColor::set).build());
        eliteMobs.addEntry(eb.startStrField(Component.translatable("buffmobs.config.eliteMobs.customNameColor"), cfg.eliteMobs.customNameColor.get())
                .setDefaultValue("#FFAA00")
                .setTooltip(tt("buffmobs.config.eliteMobs.customNameColor.tooltip"))
                .setSaveConsumer(cfg.eliteMobs.customNameColor::set).build());
        eliteMobs.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.eliteMobs.namePrefix"),
                        BuffMobsConfig.EliteMobs.NamePrefix.class,
                        cfg.eliteMobs.namePrefix.get())
                .setDefaultValue(BuffMobsConfig.EliteMobs.NamePrefix.RANDOM)
                .setEnumNameProvider(e -> switch ((BuffMobsConfig.EliteMobs.NamePrefix) e) {
                    case elite -> Component.translatable("buffmobs.elite.prefix.elite");
                    case CHAMPION -> Component.translatable("buffmobs.elite.prefix.champion");
                    case BRUTE -> Component.translatable("buffmobs.elite.prefix.brute");
                    case DREAD -> Component.translatable("buffmobs.elite.prefix.dread");
                    case RANDOM -> Component.translatable("buffmobs.config.eliteMobs.namePrefix.random");
                })
                .setTooltip(tt("buffmobs.config.eliteMobs.namePrefix.tooltip"))
                .setSaveConsumer(cfg.eliteMobs.namePrefix::set).build());
        eliteMobs.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.eliteMobs.nameMode"),
                        BuffMobsConfig.EliteMobs.NameMode.class,
                        cfg.eliteMobs.nameMode.get())
                .setDefaultValue(BuffMobsConfig.EliteMobs.NameMode.GENERATED)
                .setEnumNameProvider(e -> switch ((BuffMobsConfig.EliteMobs.NameMode) e) {
                    case GENERATED -> Component.translatable("buffmobs.config.eliteMobs.nameMode.generated");
                    case PREFIX -> Component.translatable("buffmobs.config.eliteMobs.nameMode.prefix");
                })
                .setTooltip(tt("buffmobs.config.eliteMobs.nameMode.tooltip"))
                .setSaveConsumer(cfg.eliteMobs.nameMode::set).build());
        eliteMobs.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.eliteMobs.useGeneratedPrefix"), cfg.eliteMobs.useGeneratedPrefix.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.eliteMobs.useGeneratedPrefix.tooltip"))
                .setSaveConsumer(cfg.eliteMobs.useGeneratedPrefix::set).build());
        eliteMobs.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.eliteMobs.useGeneratedSuffix"), cfg.eliteMobs.useGeneratedSuffix.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.eliteMobs.useGeneratedSuffix.tooltip"))
                .setSaveConsumer(cfg.eliteMobs.useGeneratedSuffix::set).build());
        eliteMobs.addEntry(eb.startStrList(Component.translatable("buffmobs.config.eliteMobs.generatedNamePrefixes"), new ArrayList<>(cfg.eliteMobs.generatedNamePrefixes.get()))
                .setDefaultValue(List.of(
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
                        "buffmobs.elite.generated_prefix.umbral", "buffmobs.elite.generated_prefix.wild"))
                .setTooltip(tt("buffmobs.config.eliteMobs.generatedNamePrefixes.tooltip"))
                .setSaveConsumer(v -> cfg.eliteMobs.generatedNamePrefixes.set(v)).build());
        eliteMobs.addEntry(eb.startStrList(Component.translatable("buffmobs.config.eliteMobs.generatedNameSuffixes"), new ArrayList<>(cfg.eliteMobs.generatedNameSuffixes.get()))
                .setDefaultValue(List.of(
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
                        "buffmobs.elite.generated_suffix.the_skullsplitter", "buffmobs.elite.generated_suffix.the_harbinger"))
                .setTooltip(tt("buffmobs.config.eliteMobs.generatedNameSuffixes.tooltip"))
                .setSaveConsumer(v -> cfg.eliteMobs.generatedNameSuffixes.set(v)).build());
        eliteMobs.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.eliteMobs.particle"),
                        BuffMobsConfig.EliteMobs.EliteParticle.class,
                        cfg.eliteMobs.particle.get())
                .setDefaultValue(BuffMobsConfig.EliteMobs.EliteParticle.ENCHANT)
                .setEnumNameProvider(e -> switch ((BuffMobsConfig.EliteMobs.EliteParticle) e) {
                    case ENCHANT -> Component.translatable("buffmobs.config.eliteMobs.particle.enchant");
                    case FLAME -> Component.translatable("buffmobs.config.eliteMobs.particle.flame");
                    case SOUL_FIRE_FLAME -> Component.translatable("buffmobs.config.eliteMobs.particle.soul_fire_flame");
                    case PORTAL -> Component.translatable("buffmobs.config.eliteMobs.particle.portal");
                    case END_ROD -> Component.translatable("buffmobs.config.eliteMobs.particle.end_rod");
                    case HAPPY_VILLAGER -> Component.translatable("buffmobs.config.eliteMobs.particle.happy_villager");
                    case HEART -> Component.translatable("buffmobs.config.eliteMobs.particle.heart");
                    case SNOWFLAKE -> Component.translatable("buffmobs.config.eliteMobs.particle.snowflake");
                    case ELECTRIC_SPARK -> Component.translatable("buffmobs.config.eliteMobs.particle.electric_spark");
                    case RANDOM -> Component.translatable("buffmobs.config.eliteMobs.particle.random");
                    case NONE -> Component.translatable("buffmobs.config.eliteMobs.particle.none");
                })
                .setTooltip(tt("buffmobs.config.eliteMobs.particle.tooltip"))
                .setSaveConsumer(cfg.eliteMobs.particle::set).build());
        eliteMobs.addEntry(eb.startIntField(Component.translatable("buffmobs.config.eliteMobs.particleIntervalTicks"), cfg.eliteMobs.particleIntervalTicks.get())
                .setDefaultValue(10).setMin(1).setMax(200)
                .setTooltip(tt("buffmobs.config.eliteMobs.particleIntervalTicks.tooltip"))
                .setSaveConsumer(cfg.eliteMobs.particleIntervalTicks::set).build());
        eliteMobs.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.eliteMobs.notifyNearbyPlayers"), cfg.eliteMobs.notifyNearbyPlayers.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.eliteMobs.notifyNearbyPlayers.tooltip"))
                .setSaveConsumer(cfg.eliteMobs.notifyNearbyPlayers::set).build());
        eliteMobs.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.eliteMobs.notifyRadius"), cfg.eliteMobs.notifyRadius.get())
                .setDefaultValue(48.0).setMin(0.0).setMax(256.0)
                .setTooltip(tt("buffmobs.config.eliteMobs.notifyRadius.tooltip"))
                .setSaveConsumer(cfg.eliteMobs.notifyRadius::set).build());
        eliteMobs.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.eliteMobs.bonusRewardsEnabled"), cfg.eliteMobs.bonusRewardsEnabled.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.eliteMobs.bonusRewardsEnabled.tooltip"))
                .setSaveConsumer(cfg.eliteMobs.bonusRewardsEnabled::set).build());
        eliteMobs.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.eliteMobs.rewardXpMultiplier"), cfg.eliteMobs.rewardXpMultiplier.get())
                .setDefaultValue(5.0).setMin(1.0).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.eliteMobs.rewardXpMultiplier.tooltip"))
                .setSaveConsumer(cfg.eliteMobs.rewardXpMultiplier::set).build());
        eliteMobs.addEntry(eb.startIntField(Component.translatable("buffmobs.config.eliteMobs.rewardExtraLootRolls"), cfg.eliteMobs.rewardExtraLootRolls.get())
                .setDefaultValue(2).setMin(0).setMax(20)
                .setTooltip(tt("buffmobs.config.eliteMobs.rewardExtraLootRolls.tooltip"))
                .setSaveConsumer(cfg.eliteMobs.rewardExtraLootRolls::set).build());

        ConfigMenu.Category lootXp = builder.getOrCreateCategory(Component.translatable("buffmobs.config.lootXpScaling"));
        lootXp.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.lootXpScaling.enabled"), cfg.lootXpScaling.enabled.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.lootXpScaling.enabled.tooltip"))
                .setSaveConsumer(cfg.lootXpScaling.enabled::set).build());
        lootXp.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.lootXpScaling.xpScalingEnabled"), cfg.lootXpScaling.xpScalingEnabled.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.lootXpScaling.xpScalingEnabled.tooltip"))
                .setSaveConsumer(cfg.lootXpScaling.xpScalingEnabled::set).build());
        lootXp.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.lootXpScaling.maxXpMultiplier"), cfg.lootXpScaling.maxXpMultiplier.get())
                .setDefaultValue(5.0).setMin(1.0).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.lootXpScaling.maxXpMultiplier.tooltip"))
                .setSaveConsumer(cfg.lootXpScaling.maxXpMultiplier::set).build());
        lootXp.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.lootXpScaling.extraLootRollsEnabled"), cfg.lootXpScaling.extraLootRollsEnabled.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.lootXpScaling.extraLootRollsEnabled.tooltip"))
                .setSaveConsumer(cfg.lootXpScaling.extraLootRollsEnabled::set).build());
        lootXp.addEntry(eb.startIntField(Component.translatable("buffmobs.config.lootXpScaling.maxExtraLootRolls"), cfg.lootXpScaling.maxExtraLootRolls.get())
                .setDefaultValue(3).setMin(0).setMax(20)
                .setTooltip(tt("buffmobs.config.lootXpScaling.maxExtraLootRolls.tooltip"))
                .setSaveConsumer(cfg.lootXpScaling.maxExtraLootRolls::set).build());

        ConfigMenu.Category traits = builder.getOrCreateCategory(Component.translatable("buffmobs.config.traits"));
        traits.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.traits.enabled"), cfg.traits.enabled.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.traits.enabled.tooltip"))
                .setSaveConsumer(cfg.traits.enabled::set).build());
        traits.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.traits.chance"), cfg.traits.chance.get())
                .setDefaultValue(0.15).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.traits.chance.tooltip"))
                .setSaveConsumer(cfg.traits.chance::set).build());
        traits.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.traits.onHitEffectsEnabled"), cfg.traits.onHitEffectsEnabled.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.traits.onHitEffectsEnabled.tooltip"))
                .setSaveConsumer(cfg.traits.onHitEffectsEnabled::set).build());
        traits.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.traits.flameParticle"),
                        BuffMobsConfig.EliteMobs.EliteParticle.class, cfg.traits.flameParticle.get())
                .setDefaultValue(BuffMobsConfig.EliteMobs.EliteParticle.FLAME)
                .setEnumNameProvider(e -> Component.translatable("buffmobs.config.eliteMobs.particle." + e.name().toLowerCase(java.util.Locale.ROOT)))
                .setTooltip(tt("buffmobs.config.traits.flameParticle.tooltip"))
                .setSaveConsumer(cfg.traits.flameParticle::set).build());
        traits.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.traits.frostParticle"),
                        BuffMobsConfig.EliteMobs.EliteParticle.class, cfg.traits.frostParticle.get())
                .setDefaultValue(BuffMobsConfig.EliteMobs.EliteParticle.SNOWFLAKE)
                .setEnumNameProvider(e -> Component.translatable("buffmobs.config.eliteMobs.particle." + e.name().toLowerCase(java.util.Locale.ROOT)))
                .setTooltip(tt("buffmobs.config.traits.frostParticle.tooltip"))
                .setSaveConsumer(cfg.traits.frostParticle::set).build());
        traits.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.traits.toxicParticle"),
                        BuffMobsConfig.EliteMobs.EliteParticle.class, cfg.traits.toxicParticle.get())
                .setDefaultValue(BuffMobsConfig.EliteMobs.EliteParticle.HAPPY_VILLAGER)
                .setEnumNameProvider(e -> Component.translatable("buffmobs.config.eliteMobs.particle." + e.name().toLowerCase(java.util.Locale.ROOT)))
                .setTooltip(tt("buffmobs.config.traits.toxicParticle.tooltip"))
                .setSaveConsumer(cfg.traits.toxicParticle::set).build());
        traits.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.traits.voltaicParticle"),
                        BuffMobsConfig.EliteMobs.EliteParticle.class, cfg.traits.voltaicParticle.get())
                .setDefaultValue(BuffMobsConfig.EliteMobs.EliteParticle.ELECTRIC_SPARK)
                .setEnumNameProvider(e -> Component.translatable("buffmobs.config.eliteMobs.particle." + e.name().toLowerCase(java.util.Locale.ROOT)))
                .setTooltip(tt("buffmobs.config.traits.voltaicParticle.tooltip"))
                .setSaveConsumer(cfg.traits.voltaicParticle::set).build());
        traits.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.traits.vampiricParticle"),
                        BuffMobsConfig.EliteMobs.EliteParticle.class, cfg.traits.vampiricParticle.get())
                .setDefaultValue(BuffMobsConfig.EliteMobs.EliteParticle.HEART)
                .setEnumNameProvider(e -> Component.translatable("buffmobs.config.eliteMobs.particle." + e.name().toLowerCase(java.util.Locale.ROOT)))
                .setTooltip(tt("buffmobs.config.traits.vampiricParticle.tooltip"))
                .setSaveConsumer(cfg.traits.vampiricParticle::set).build());
        traits.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.traits.armoredParticle"),
                        BuffMobsConfig.EliteMobs.EliteParticle.class, cfg.traits.armoredParticle.get())
                .setDefaultValue(BuffMobsConfig.EliteMobs.EliteParticle.NONE)
                .setEnumNameProvider(e -> Component.translatable("buffmobs.config.eliteMobs.particle." + e.name().toLowerCase(java.util.Locale.ROOT)))
                .setTooltip(tt("buffmobs.config.traits.armoredParticle.tooltip"))
                .setSaveConsumer(cfg.traits.armoredParticle::set).build());
        var traitsSub1 = eb.startSubCategory(Component.translatable("buffmobs.config.traits.weights"));
        traitsSub1.add(eb.startIntField(Component.translatable("buffmobs.config.traits.flameWeight"), cfg.traits.flameWeight.get())
                .setDefaultValue(20).setMin(0).setMax(1000)
                .setTooltip(tt("buffmobs.config.traits.flameWeight.tooltip"))
                .setSaveConsumer(cfg.traits.flameWeight::set).build());
        traitsSub1.add(eb.startIntField(Component.translatable("buffmobs.config.traits.frostWeight"), cfg.traits.frostWeight.get())
                .setDefaultValue(20).setMin(0).setMax(1000)
                .setTooltip(tt("buffmobs.config.traits.frostWeight.tooltip"))
                .setSaveConsumer(cfg.traits.frostWeight::set).build());
        traitsSub1.add(eb.startIntField(Component.translatable("buffmobs.config.traits.toxicWeight"), cfg.traits.toxicWeight.get())
                .setDefaultValue(20).setMin(0).setMax(1000)
                .setTooltip(tt("buffmobs.config.traits.toxicWeight.tooltip"))
                .setSaveConsumer(cfg.traits.toxicWeight::set).build());
        traitsSub1.add(eb.startIntField(Component.translatable("buffmobs.config.traits.voltaicWeight"), cfg.traits.voltaicWeight.get())
                .setDefaultValue(15).setMin(0).setMax(1000)
                .setTooltip(tt("buffmobs.config.traits.voltaicWeight.tooltip"))
                .setSaveConsumer(cfg.traits.voltaicWeight::set).build());
        traitsSub1.add(eb.startIntField(Component.translatable("buffmobs.config.traits.vampiricWeight"), cfg.traits.vampiricWeight.get())
                .setDefaultValue(15).setMin(0).setMax(1000)
                .setTooltip(tt("buffmobs.config.traits.vampiricWeight.tooltip"))
                .setSaveConsumer(cfg.traits.vampiricWeight::set).build());
        traitsSub1.add(eb.startIntField(Component.translatable("buffmobs.config.traits.armoredWeight"), cfg.traits.armoredWeight.get())
                .setDefaultValue(10).setMin(0).setMax(1000)
                .setTooltip(tt("buffmobs.config.traits.armoredWeight.tooltip"))
                .setSaveConsumer(cfg.traits.armoredWeight::set).build());
        traits.addEntry(traitsSub1.build());
        var traitsSub2 = eb.startSubCategory(Component.translatable("buffmobs.config.traits.traitSettings"));
        traitsSub2.add(eb.startIntField(Component.translatable("buffmobs.config.traits.flameFireSeconds"), cfg.traits.flameFireSeconds.get())
                .setDefaultValue(4).setMin(1).setMax(60)
                .setTooltip(tt("buffmobs.config.traits.flameFireSeconds.tooltip"))
                .setSaveConsumer(cfg.traits.flameFireSeconds::set).build());
        traitsSub2.add(eb.startIntField(Component.translatable("buffmobs.config.traits.frostSlownessSeconds"), cfg.traits.frostSlownessSeconds.get())
                .setDefaultValue(3).setMin(1).setMax(60)
                .setTooltip(tt("buffmobs.config.traits.frostSlownessSeconds.tooltip"))
                .setSaveConsumer(cfg.traits.frostSlownessSeconds::set).build());
        traitsSub2.add(eb.startIntField(Component.translatable("buffmobs.config.traits.frostSlownessLevel"), cfg.traits.frostSlownessLevel.get())
                .setDefaultValue(2).setMin(1).setMax(10)
                .setTooltip(tt("buffmobs.config.traits.frostSlownessLevel.tooltip"))
                .setSaveConsumer(cfg.traits.frostSlownessLevel::set).build());
        traitsSub2.add(eb.startIntField(Component.translatable("buffmobs.config.traits.toxicPoisonSeconds"), cfg.traits.toxicPoisonSeconds.get())
                .setDefaultValue(5).setMin(1).setMax(60)
                .setTooltip(tt("buffmobs.config.traits.toxicPoisonSeconds.tooltip"))
                .setSaveConsumer(cfg.traits.toxicPoisonSeconds::set).build());
        traitsSub2.add(eb.startIntField(Component.translatable("buffmobs.config.traits.toxicPoisonLevel"), cfg.traits.toxicPoisonLevel.get())
                .setDefaultValue(1).setMin(1).setMax(10)
                .setTooltip(tt("buffmobs.config.traits.toxicPoisonLevel.tooltip"))
                .setSaveConsumer(cfg.traits.toxicPoisonLevel::set).build());
        traitsSub2.add(eb.startDoubleField(Component.translatable("buffmobs.config.traits.voltaicChainChance"), cfg.traits.voltaicChainChance.get())
                .setDefaultValue(0.35).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.traits.voltaicChainChance.tooltip"))
                .setSaveConsumer(cfg.traits.voltaicChainChance::set).build());
        traitsSub2.add(eb.startDoubleField(Component.translatable("buffmobs.config.traits.voltaicChainDamage"), cfg.traits.voltaicChainDamage.get())
                .setDefaultValue(4.0).setMin(0.0).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.traits.voltaicChainDamage.tooltip"))
                .setSaveConsumer(cfg.traits.voltaicChainDamage::set).build());
        traitsSub2.add(eb.startDoubleField(Component.translatable("buffmobs.config.traits.voltaicChainRadius"), cfg.traits.voltaicChainRadius.get())
                .setDefaultValue(4.0).setMin(0.0).setMax(32.0)
                .setTooltip(tt("buffmobs.config.traits.voltaicChainRadius.tooltip"))
                .setSaveConsumer(cfg.traits.voltaicChainRadius::set).build());
        traitsSub2.add(eb.startIntField(Component.translatable("buffmobs.config.traits.voltaicMaxTargets"), cfg.traits.voltaicMaxTargets.get())
                .setDefaultValue(3).setMin(1).setMax(20)
                .setTooltip(tt("buffmobs.config.traits.voltaicMaxTargets.tooltip"))
                .setSaveConsumer(cfg.traits.voltaicMaxTargets::set).build());
        traitsSub2.add(eb.startDoubleField(Component.translatable("buffmobs.config.traits.vampiricHealPercent"), cfg.traits.vampiricHealPercent.get())
                .setDefaultValue(0.25).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.traits.vampiricHealPercent.tooltip"))
                .setSaveConsumer(cfg.traits.vampiricHealPercent::set).build());
        traitsSub2.add(eb.startDoubleField(Component.translatable("buffmobs.config.traits.armoredArmorBonus"), cfg.traits.armoredArmorBonus.get())
                .setDefaultValue(8.0).setMin(0.0).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.traits.armoredArmorBonus.tooltip"))
                .setSaveConsumer(cfg.traits.armoredArmorBonus::set).build());
        traitsSub2.add(eb.startDoubleField(Component.translatable("buffmobs.config.traits.armoredToughnessBonus"), cfg.traits.armoredToughnessBonus.get())
                .setDefaultValue(4.0).setMin(0.0).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.traits.armoredToughnessBonus.tooltip"))
                .setSaveConsumer(cfg.traits.armoredToughnessBonus::set).build());
        traits.addEntry(traitsSub2.build());

        ConfigMenu.Category packAggression = builder.getOrCreateCategory(Component.translatable("buffmobs.config.packAggression"));
        packAggression.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.packAggression.enabled"), cfg.packAggression.enabled.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.packAggression.enabled.tooltip"))
                .setSaveConsumer(cfg.packAggression.enabled::set).build());
        packAggression.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.packAggression.radius"), cfg.packAggression.radius.get())
                .setDefaultValue(16.0).setMin(1.0).setMax(64.0)
                .setTooltip(tt("buffmobs.config.packAggression.radius.tooltip"))
                .setSaveConsumer(cfg.packAggression.radius::set).build());
        packAggression.addEntry(eb.startIntField(Component.translatable("buffmobs.config.packAggression.maxPackSize"), cfg.packAggression.maxPackSize.get())
                .setDefaultValue(5).setMin(1).setMax(50)
                .setTooltip(tt("buffmobs.config.packAggression.maxPackSize.tooltip"))
                .setSaveConsumer(cfg.packAggression.maxPackSize::set).build());
        packAggression.addEntry(eb.startIntField(Component.translatable("buffmobs.config.packAggression.cooldownTicks"), cfg.packAggression.cooldownTicks.get())
                .setDefaultValue(200).setMin(0).setMax(72000)
                .setTooltip(tt("buffmobs.config.packAggression.cooldownTicks.tooltip"))
                .setSaveConsumer(cfg.packAggression.cooldownTicks::set).build());

        ConfigMenu.Category specialAbilities = builder.getOrCreateCategory(Component.translatable("buffmobs.config.specialAbilities"));
        specialAbilities.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.specialAbilities.enabled"), cfg.specialAbilities.enabled.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.specialAbilities.enabled.tooltip"))
                .setSaveConsumer(cfg.specialAbilities.enabled::set).build());
        specialAbilities.addEntry(eb.startStrList(Component.translatable("buffmobs.config.specialAbilities.mobAbilities"), new ArrayList<>(cfg.specialAbilities.mobAbilities.get()))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.specialAbilities.mobAbilities.tooltip"))
                .setSaveConsumer(v -> cfg.specialAbilities.mobAbilities.set(v)).build());
        var specialAbilitiesSub1 = eb.startSubCategory(Component.translatable("buffmobs.config.specialAbilities.deathrattle"));
        specialAbilitiesSub1.add(eb.startEnumSelector(Component.translatable("buffmobs.config.specialAbilities.deathrattle.mode"),
                        BuffMobsConfig.SpecialAbilities.Deathrattle.DeathrattleMode.class,
                        cfg.specialAbilities.deathrattle.mode.get())
                .setDefaultValue(BuffMobsConfig.SpecialAbilities.Deathrattle.DeathrattleMode.POISON_CLOUD)
                .setEnumNameProvider(e -> switch ((BuffMobsConfig.SpecialAbilities.Deathrattle.DeathrattleMode) e) {
                    case EXPLOSION -> Component.translatable("buffmobs.config.specialAbilities.deathrattle.mode.explosion");
                    case POISON_CLOUD -> Component.translatable("buffmobs.config.specialAbilities.deathrattle.mode.poison_cloud");
                    case Summon_MINIONS -> Component.translatable("buffmobs.config.specialAbilities.deathrattle.mode.summon_minions");
                    case RANDOM -> Component.translatable("buffmobs.config.specialAbilities.deathrattle.mode.random");
                })
                .setTooltip(tt("buffmobs.config.specialAbilities.deathrattle.mode.tooltip"))
                .setSaveConsumer(cfg.specialAbilities.deathrattle.mode::set).build());
        specialAbilitiesSub1.add(eb.startDoubleField(Component.translatable("buffmobs.config.specialAbilities.deathrattle.explosionPower"), cfg.specialAbilities.deathrattle.explosionPower.get())
                .setDefaultValue(1.5).setMin(0.0).setMax(20.0)
                .setTooltip(tt("buffmobs.config.specialAbilities.deathrattle.explosionPower.tooltip"))
                .setSaveConsumer(cfg.specialAbilities.deathrattle.explosionPower::set).build());
        specialAbilitiesSub1.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.specialAbilities.deathrattle.explosionBreaksBlocks"), cfg.specialAbilities.deathrattle.explosionBreaksBlocks.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.specialAbilities.deathrattle.explosionBreaksBlocks.tooltip"))
                .setSaveConsumer(cfg.specialAbilities.deathrattle.explosionBreaksBlocks::set).build());
        specialAbilitiesSub1.add(eb.startDoubleField(Component.translatable("buffmobs.config.specialAbilities.deathrattle.poisonCloudRadius"), cfg.specialAbilities.deathrattle.poisonCloudRadius.get())
                .setDefaultValue(2.5).setMin(0.5).setMax(16.0)
                .setTooltip(tt("buffmobs.config.specialAbilities.deathrattle.poisonCloudRadius.tooltip"))
                .setSaveConsumer(cfg.specialAbilities.deathrattle.poisonCloudRadius::set).build());
        specialAbilitiesSub1.add(eb.startIntField(Component.translatable("buffmobs.config.specialAbilities.deathrattle.poisonCloudDurationSeconds"), cfg.specialAbilities.deathrattle.poisonCloudDurationSeconds.get())
                .setDefaultValue(5).setMin(1).setMax(60)
                .setTooltip(tt("buffmobs.config.specialAbilities.deathrattle.poisonCloudDurationSeconds.tooltip"))
                .setSaveConsumer(cfg.specialAbilities.deathrattle.poisonCloudDurationSeconds::set).build());
        specialAbilitiesSub1.add(eb.startIntField(Component.translatable("buffmobs.config.specialAbilities.deathrattle.poisonCloudLevel"), cfg.specialAbilities.deathrattle.poisonCloudLevel.get())
                .setDefaultValue(1).setMin(1).setMax(10)
                .setTooltip(tt("buffmobs.config.specialAbilities.deathrattle.poisonCloudLevel.tooltip"))
                .setSaveConsumer(cfg.specialAbilities.deathrattle.poisonCloudLevel::set).build());
        specialAbilitiesSub1.add(eb.startStrField(Component.translatable("buffmobs.config.specialAbilities.deathrattle.minionEntityId"), cfg.specialAbilities.deathrattle.minionEntityId.get())
                .setDefaultValue("minecraft:slime")
                .setTooltip(tt("buffmobs.config.specialAbilities.deathrattle.minionEntityId.tooltip"))
                .setSaveConsumer(cfg.specialAbilities.deathrattle.minionEntityId::set).build());
        specialAbilitiesSub1.add(eb.startIntField(Component.translatable("buffmobs.config.specialAbilities.deathrattle.minionMinCount"), cfg.specialAbilities.deathrattle.minionMinCount.get())
                .setDefaultValue(1).setMin(0).setMax(10)
                .setTooltip(tt("buffmobs.config.specialAbilities.deathrattle.minionMinCount.tooltip"))
                .setSaveConsumer(cfg.specialAbilities.deathrattle.minionMinCount::set).build());
        specialAbilitiesSub1.add(eb.startIntField(Component.translatable("buffmobs.config.specialAbilities.deathrattle.minionMaxCount"), cfg.specialAbilities.deathrattle.minionMaxCount.get())
                .setDefaultValue(2).setMin(0).setMax(10)
                .setTooltip(tt("buffmobs.config.specialAbilities.deathrattle.minionMaxCount.tooltip"))
                .setSaveConsumer(cfg.specialAbilities.deathrattle.minionMaxCount::set).build());
        specialAbilities.addEntry(specialAbilitiesSub1.build());
        var specialAbilitiesSub2 = eb.startSubCategory(Component.translatable("buffmobs.config.specialAbilities.lastStand"));
        specialAbilitiesSub2.add(eb.startDoubleField(Component.translatable("buffmobs.config.specialAbilities.lastStand.healthThreshold"), cfg.specialAbilities.lastStand.healthThreshold.get())
                .setDefaultValue(0.20).setMin(0.01).setMax(0.99)
                .setTooltip(tt("buffmobs.config.specialAbilities.lastStand.healthThreshold.tooltip"))
                .setSaveConsumer(cfg.specialAbilities.lastStand.healthThreshold::set).build());
        specialAbilitiesSub2.add(eb.startIntField(Component.translatable("buffmobs.config.specialAbilities.lastStand.resistanceLevel"), cfg.specialAbilities.lastStand.resistanceLevel.get())
                .setDefaultValue(2).setMin(1).setMax(10)
                .setTooltip(tt("buffmobs.config.specialAbilities.lastStand.resistanceLevel.tooltip"))
                .setSaveConsumer(cfg.specialAbilities.lastStand.resistanceLevel::set).build());
        specialAbilitiesSub2.add(eb.startIntField(Component.translatable("buffmobs.config.specialAbilities.lastStand.resistanceDurationSeconds"), cfg.specialAbilities.lastStand.resistanceDurationSeconds.get())
                .setDefaultValue(5).setMin(1).setMax(60)
                .setTooltip(tt("buffmobs.config.specialAbilities.lastStand.resistanceDurationSeconds.tooltip"))
                .setSaveConsumer(cfg.specialAbilities.lastStand.resistanceDurationSeconds::set).build());
        specialAbilities.addEntry(specialAbilitiesSub2.build());
        var specialAbilitiesSub3 = eb.startSubCategory(Component.translatable("buffmobs.config.specialAbilities.Teleport"));
        specialAbilitiesSub3.add(eb.startDoubleField(Component.translatable("buffmobs.config.specialAbilities.Teleport.chance"), cfg.specialAbilities.Teleport.chance.get())
                .setDefaultValue(0.25).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.specialAbilities.Teleport.chance.tooltip"))
                .setSaveConsumer(cfg.specialAbilities.Teleport.chance::set).build());
        specialAbilitiesSub3.add(eb.startIntField(Component.translatable("buffmobs.config.specialAbilities.Teleport.cooldownTicks"), cfg.specialAbilities.Teleport.cooldownTicks.get())
                .setDefaultValue(100).setMin(0).setMax(72000)
                .setTooltip(tt("buffmobs.config.specialAbilities.Teleport.cooldownTicks.tooltip"))
                .setSaveConsumer(cfg.specialAbilities.Teleport.cooldownTicks::set).build());
        specialAbilitiesSub3.add(eb.startDoubleField(Component.translatable("buffmobs.config.specialAbilities.Teleport.distanceBehindAttacker"), cfg.specialAbilities.Teleport.distanceBehindAttacker.get())
                .setDefaultValue(2.0).setMin(0.5).setMax(16.0)
                .setTooltip(tt("buffmobs.config.specialAbilities.Teleport.distanceBehindAttacker.tooltip"))
                .setSaveConsumer(cfg.specialAbilities.Teleport.distanceBehindAttacker::set).build());
        specialAbilities.addEntry(specialAbilitiesSub3.build());
        var specialAbilitiesSub4 = eb.startSubCategory(Component.translatable("buffmobs.config.specialAbilities.Summon"));
        specialAbilitiesSub4.add(eb.startIntField(Component.translatable("buffmobs.config.specialAbilities.Summon.intervalTicks"), cfg.specialAbilities.Summon.intervalTicks.get())
                .setDefaultValue(200).setMin(20).setMax(72000)
                .setTooltip(tt("buffmobs.config.specialAbilities.Summon.intervalTicks.tooltip"))
                .setSaveConsumer(cfg.specialAbilities.Summon.intervalTicks::set).build());
        specialAbilitiesSub4.add(eb.startStrField(Component.translatable("buffmobs.config.specialAbilities.Summon.minionEntityId"), cfg.specialAbilities.Summon.minionEntityId.get())
                .setDefaultValue("minecraft:zombie")
                .setTooltip(tt("buffmobs.config.specialAbilities.Summon.minionEntityId.tooltip"))
                .setSaveConsumer(cfg.specialAbilities.Summon.minionEntityId::set).build());
        specialAbilitiesSub4.add(eb.startIntField(Component.translatable("buffmobs.config.specialAbilities.Summon.maxAliveMinions"), cfg.specialAbilities.Summon.maxAliveMinions.get())
                .setDefaultValue(3).setMin(0).setMax(20)
                .setTooltip(tt("buffmobs.config.specialAbilities.Summon.maxAliveMinions.tooltip"))
                .setSaveConsumer(cfg.specialAbilities.Summon.maxAliveMinions::set).build());
        specialAbilitiesSub4.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.specialAbilities.Summon.onlyInCombat"), cfg.specialAbilities.Summon.onlyInCombat.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.specialAbilities.Summon.onlyInCombat.tooltip"))
                .setSaveConsumer(cfg.specialAbilities.Summon.onlyInCombat::set).build());
        specialAbilities.addEntry(specialAbilitiesSub4.build());

        ConfigMenu.Category bloodMoon = builder.getOrCreateCategory(Component.translatable("buffmobs.config.bloodMoon"));
        bloodMoon.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.bloodMoon.enabled"), cfg.bloodMoon.enabled.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.bloodMoon.enabled.tooltip"))
                .setSaveConsumer(cfg.bloodMoon.enabled::set).build());
        bloodMoon.addEntry(eb.startIntField(Component.translatable("buffmobs.config.bloodMoon.intervalDays"), cfg.bloodMoon.intervalDays.get())
                .setDefaultValue(7).setMin(1).setMax(365)
                .setTooltip(tt("buffmobs.config.bloodMoon.intervalDays.tooltip"))
                .setSaveConsumer(cfg.bloodMoon.intervalDays::set).build());
        bloodMoon.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.bloodMoon.extraMultiplier"), cfg.bloodMoon.extraMultiplier.get())
                .setDefaultValue(1.5).setMin(1.0).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.bloodMoon.extraMultiplier.tooltip"))
                .setSaveConsumer(cfg.bloodMoon.extraMultiplier::set).build());
        bloodMoon.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.bloodMoon.glowingMobs"), cfg.bloodMoon.glowingMobs.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.bloodMoon.glowingMobs.tooltip"))
                .setSaveConsumer(cfg.bloodMoon.glowingMobs::set).build());
        bloodMoon.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.bloodMoon.doubleLootAndXp"), cfg.bloodMoon.doubleLootAndXp.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.bloodMoon.doubleLootAndXp.tooltip"))
                .setSaveConsumer(cfg.bloodMoon.doubleLootAndXp::set).build());
        bloodMoon.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.bloodMoon.notifyPlayers"), cfg.bloodMoon.notifyPlayers.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.bloodMoon.notifyPlayers.tooltip"))
                .setSaveConsumer(cfg.bloodMoon.notifyPlayers::set).build());
        bloodMoon.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.bloodMoon.spawnBoostEnabled"), cfg.bloodMoon.spawnBoostEnabled.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.bloodMoon.spawnBoostEnabled.tooltip"))
                .setSaveConsumer(cfg.bloodMoon.spawnBoostEnabled::set).build());
        bloodMoon.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.bloodMoon.extraSpawnChance"), cfg.bloodMoon.extraSpawnChance.get())
                .setDefaultValue(0.25).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.bloodMoon.extraSpawnChance.tooltip"))
                .setSaveConsumer(cfg.bloodMoon.extraSpawnChance::set).build());

        ConfigMenu.Category adaptive = builder.getOrCreateCategory(Component.translatable("buffmobs.config.adaptiveDifficulty"));
        adaptive.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.adaptiveDifficulty.enabled"), cfg.adaptiveDifficulty.enabled.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.adaptiveDifficulty.enabled.tooltip"))
                .setSaveConsumer(cfg.adaptiveDifficulty.enabled::set).build());
        adaptive.addEntry(eb.startIntField(Component.translatable("buffmobs.config.adaptiveDifficulty.deathsPerStep"), cfg.adaptiveDifficulty.deathsPerStep.get())
                .setDefaultValue(3).setMin(1).setMax(100)
                .setTooltip(tt("buffmobs.config.adaptiveDifficulty.deathsPerStep.tooltip"))
                .setSaveConsumer(cfg.adaptiveDifficulty.deathsPerStep::set).build());
        adaptive.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.adaptiveDifficulty.bonusPerStep"), cfg.adaptiveDifficulty.bonusPerStep.get())
                .setDefaultValue(0.1).setMin(0.0).setMax(10.0)
                .setTooltip(tt("buffmobs.config.adaptiveDifficulty.bonusPerStep.tooltip"))
                .setSaveConsumer(cfg.adaptiveDifficulty.bonusPerStep::set).build());
        adaptive.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.adaptiveDifficulty.maxBonus"), cfg.adaptiveDifficulty.maxBonus.get())
                .setDefaultValue(2.0).setMin(0.0).setMax(100.0)
                .setTooltip(tt("buffmobs.config.adaptiveDifficulty.maxBonus.tooltip"))
                .setSaveConsumer(cfg.adaptiveDifficulty.maxBonus::set).build());
        adaptive.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.adaptiveDifficulty.killReductionEnabled"), cfg.adaptiveDifficulty.killReductionEnabled.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.adaptiveDifficulty.killReductionEnabled.tooltip"))
                .setSaveConsumer(cfg.adaptiveDifficulty.killReductionEnabled::set).build());
        adaptive.addEntry(eb.startIntField(Component.translatable("buffmobs.config.adaptiveDifficulty.killsPerReductionStep"), cfg.adaptiveDifficulty.killsPerReductionStep.get())
                .setDefaultValue(25).setMin(1).setMax(1000)
                .setTooltip(tt("buffmobs.config.adaptiveDifficulty.killsPerReductionStep.tooltip"))
                .setSaveConsumer(cfg.adaptiveDifficulty.killsPerReductionStep::set).build());
        adaptive.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.adaptiveDifficulty.reductionPerStep"), cfg.adaptiveDifficulty.reductionPerStep.get())
                .setDefaultValue(0.05).setMin(0.0).setMax(10.0)
                .setTooltip(tt("buffmobs.config.adaptiveDifficulty.reductionPerStep.tooltip"))
                .setSaveConsumer(cfg.adaptiveDifficulty.reductionPerStep::set).build());

        return builder.build();
    }

}
