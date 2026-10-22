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
        general.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.general.enabled"), cfg.enabled)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.general.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.enabled = v).build());
        general.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.general.visualEffects"), cfg.visualEffects)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.general.visualEffects.tooltip"))
                .setSaveConsumer(v -> cfg.visualEffects = v).build());
        general.addEntry(eb.startTextDescription(Component.translatable("buffmobs.config.credits.translation")).build());

        ConfigMenu.Category attributes = builder.getOrCreateCategory(Component.translatable("buffmobs.config.attributes"));
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.healthMultiplier"), cfg.attributes.healthMultiplier)
                .setDefaultValue(1.5).setMin(0.1).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.attributes.healthMultiplier.tooltip"))
                .setSaveConsumer(v -> cfg.attributes.healthMultiplier = v).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.damageMultiplier"), cfg.attributes.damageMultiplier)
                .setDefaultValue(1.5).setMin(0.1).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.attributes.damageMultiplier.tooltip"))
                .setSaveConsumer(v -> cfg.attributes.damageMultiplier = v).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.speedMultiplier"), cfg.attributes.speedMultiplier)
                .setDefaultValue(1.0).setMin(0.1).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.attributes.speedMultiplier.tooltip"))
                .setSaveConsumer(v -> cfg.attributes.speedMultiplier = v).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.attackSpeedMultiplier"), cfg.attributes.attackSpeedMultiplier)
                .setDefaultValue(1.0).setMin(0.1).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.attributes.attackSpeedMultiplier.tooltip"))
                .setSaveConsumer(v -> cfg.attributes.attackSpeedMultiplier = v).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.armorAddition"), cfg.attributes.armorAddition)
                .setDefaultValue(5.0).setMin(0.0).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.attributes.armorAddition.tooltip"))
                .setSaveConsumer(v -> cfg.attributes.armorAddition = v).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.armorToughnessAddition"), cfg.attributes.armorToughnessAddition)
                .setDefaultValue(0.0).setMin(0.0).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.attributes.armorToughnessAddition.tooltip"))
                .setSaveConsumer(v -> cfg.attributes.armorToughnessAddition = v).build());

        ConfigMenu.Category effects = builder.getOrCreateCategory(Component.translatable("buffmobs.config.effects"));
        effects.addEntry(eb.startIntField(Component.translatable("buffmobs.config.effects.duration"), cfg.effects.duration)
                .setDefaultValue(-1).setMin(-1).setMax(7200)
                .setTooltip(tt("buffmobs.config.effects.duration.tooltip"))
                .setSaveConsumer(v -> cfg.effects.duration = v).build());
        effects.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.effects.strengthAmplifier"), cfg.effects.strengthAmplifier, 0, 10)
                .setDefaultValue(0)
                .setTextGetter(v -> v == 0
                        ? Component.translatable("buffmobs.config.effects.slider.disabled")
                        : Component.translatable("buffmobs.config.effects.slider.level", v))
                .setTooltip(tt("buffmobs.config.effects.strengthAmplifier.tooltip"))
                .setSaveConsumer(v -> cfg.effects.strengthAmplifier = v).build());
        effects.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.effects.speedAmplifier"), cfg.effects.speedAmplifier, 0, 10)
                .setDefaultValue(0)
                .setTextGetter(v -> v == 0
                        ? Component.translatable("buffmobs.config.effects.slider.disabled")
                        : Component.translatable("buffmobs.config.effects.slider.level", v))
                .setTooltip(tt("buffmobs.config.effects.speedAmplifier.tooltip"))
                .setSaveConsumer(v -> cfg.effects.speedAmplifier = v).build());
        effects.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.effects.resistanceAmplifier"), cfg.effects.resistanceAmplifier, 0, 10)
                .setDefaultValue(0)
                .setTextGetter(v -> v == 0
                        ? Component.translatable("buffmobs.config.effects.slider.disabled")
                        : Component.translatable("buffmobs.config.effects.slider.level", v))
                .setTooltip(tt("buffmobs.config.effects.resistanceAmplifier.tooltip"))
                .setSaveConsumer(v -> cfg.effects.resistanceAmplifier = v).build());
        effects.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.effects.regenerationAmplifier"), cfg.effects.regenerationAmplifier, 0, 10)
                .setDefaultValue(0)
                .setTextGetter(v -> v == 0
                        ? Component.translatable("buffmobs.config.effects.slider.disabled")
                        : Component.translatable("buffmobs.config.effects.slider.level", v))
                .setTooltip(tt("buffmobs.config.effects.regenerationAmplifier.tooltip"))
                .setSaveConsumer(v -> cfg.effects.regenerationAmplifier = v).build());
        effects.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.effects.absorptionAmplifier"), cfg.effects.absorptionAmplifier, 0, 10)
                .setDefaultValue(0)
                .setTextGetter(v -> v == 0
                        ? Component.translatable("buffmobs.config.effects.slider.disabled")
                        : Component.translatable("buffmobs.config.effects.slider.level", v))
                .setTooltip(tt("buffmobs.config.effects.absorptionAmplifier.tooltip"))
                .setSaveConsumer(v -> cfg.effects.absorptionAmplifier = v).build());

        ConfigMenu.Category harmful = builder.getOrCreateCategory(Component.translatable("buffmobs.config.harmfulEffects"));
        harmful.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.harmfulEffects.enabled"), cfg.harmfulEffects.enabled)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.harmfulEffects.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.harmfulEffects.enabled = v).build());
        harmful.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.harmfulEffects.chance"), cfg.harmfulEffects.chance)
                .setDefaultValue(0.15).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.harmfulEffects.chance.tooltip"))
                .setSaveConsumer(v -> cfg.harmfulEffects.chance = v).build());
        harmful.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.harmfulEffects.poisonEnabled"), cfg.harmfulEffects.poisonEnabled)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.harmfulEffects.poisonEnabled.tooltip"))
                .setSaveConsumer(v -> cfg.harmfulEffects.poisonEnabled = v).build());
        harmful.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.harmfulEffects.slownessEnabled"), cfg.harmfulEffects.slownessEnabled)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.harmfulEffects.slownessEnabled.tooltip"))
                .setSaveConsumer(v -> cfg.harmfulEffects.slownessEnabled = v).build());
        harmful.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.harmfulEffects.witherEnabled"), cfg.harmfulEffects.witherEnabled)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.harmfulEffects.witherEnabled.tooltip"))
                .setSaveConsumer(v -> cfg.harmfulEffects.witherEnabled = v).build());
        harmful.addEntry(eb.startIntField(Component.translatable("buffmobs.config.harmfulEffects.poisonDuration"), cfg.harmfulEffects.poisonDuration)
                .setDefaultValue(5).setMin(1).setMax(60)
                .setTooltip(tt("buffmobs.config.harmfulEffects.poisonDuration.tooltip"))
                .setSaveConsumer(v -> cfg.harmfulEffects.poisonDuration = v).build());
        harmful.addEntry(eb.startIntField(Component.translatable("buffmobs.config.harmfulEffects.slownessDuration"), cfg.harmfulEffects.slownessDuration)
                .setDefaultValue(3).setMin(1).setMax(60)
                .setTooltip(tt("buffmobs.config.harmfulEffects.slownessDuration.tooltip"))
                .setSaveConsumer(v -> cfg.harmfulEffects.slownessDuration = v).build());
        harmful.addEntry(eb.startIntField(Component.translatable("buffmobs.config.harmfulEffects.witherDuration"), cfg.harmfulEffects.witherDuration)
                .setDefaultValue(3).setMin(1).setMax(60)
                .setTooltip(tt("buffmobs.config.harmfulEffects.witherDuration.tooltip"))
                .setSaveConsumer(v -> cfg.harmfulEffects.witherDuration = v).build());

        ConfigMenu.Category dayScaling = builder.getOrCreateCategory(Component.translatable("buffmobs.config.dayScaling"));
        dayScaling.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.dayScaling.enabled"), cfg.dayScaling.enabled)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.dayScaling.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.dayScaling.enabled = v).build());
        dayScaling.addEntry(eb.startIntField(Component.translatable("buffmobs.config.dayScaling.interval"), cfg.dayScaling.interval)
                .setDefaultValue(7).setMin(1).setMax(365)
                .setTooltip(tt("buffmobs.config.dayScaling.interval.tooltip"))
                .setSaveConsumer(v -> cfg.dayScaling.interval = v).build());
        dayScaling.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.dayScaling.multiplier"), cfg.dayScaling.multiplier)
                .setDefaultValue(0.1).setMin(0.01).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.dayScaling.multiplier.tooltip"))
                .setSaveConsumer(v -> cfg.dayScaling.multiplier = v).build());
        dayScaling.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.dayScaling.maxMultiplier"), cfg.dayScaling.maxMultiplier)
                .setDefaultValue(5.0).setMin(0.0).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.dayScaling.maxMultiplier.tooltip"))
                .setSaveConsumer(v -> cfg.dayScaling.maxMultiplier = v).build());
        dayScaling.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.dayScaling.showNotifications"), cfg.dayScaling.showNotifications)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.dayScaling.showNotifications.tooltip"))
                .setSaveConsumer(v -> cfg.dayScaling.showNotifications = v).build());
        dayScaling.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.dayScaling.notificationMode"),
                        BuffMobsConfig.DayScaling.NotificationMode.class,
                        cfg.dayScaling.notificationMode)
                .setDefaultValue(BuffMobsConfig.DayScaling.NotificationMode.EVERY_DAY)
                .setEnumNameProvider(e -> switch ((BuffMobsConfig.DayScaling.NotificationMode) e) {
                    case EVERY_DAY             -> Component.translatable("buffmobs.config.dayScaling.notificationMode.every_day");
                    case SCALING_INCREASE_ONLY -> Component.translatable("buffmobs.config.dayScaling.notificationMode.scaling_increase_only");
                })
                .setTooltip(tt("buffmobs.config.dayScaling.notificationMode.tooltip"))
                .setSaveConsumer(v -> cfg.dayScaling.notificationMode = v).build());

        ConfigMenu.Category dimScaling = builder.getOrCreateCategory(Component.translatable("buffmobs.config.dimensionScaling"));
        dimScaling.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.dimensionScaling.mode"),
                        BuffMobsConfig.DimensionScaling.Mode.class,
                        cfg.dimensionScaling.mode)
                .setDefaultValue(BuffMobsConfig.DimensionScaling.Mode.SCALING)
                .setEnumNameProvider(e -> switch ((BuffMobsConfig.DimensionScaling.Mode) e) {
                    case SCALING  -> Component.translatable("buffmobs.config.dimensionScaling.mode.scaling");
                    case OVERRIDE -> Component.translatable("buffmobs.config.dimensionScaling.mode.override");
                })
                .setTooltip(tt("buffmobs.config.dimensionScaling.mode.tooltip"))
                .setSaveConsumer(v -> cfg.dimensionScaling.mode = v).build());

        BuffMobsConfig.DimensionScaling.DimensionSlot[] dimSlots = {
                cfg.dimensionScaling.slot1, cfg.dimensionScaling.slot2, cfg.dimensionScaling.slot3,
                cfg.dimensionScaling.slot4, cfg.dimensionScaling.slot5
        };
        for (int i = 0; i < dimSlots.length; i++) {
            BuffMobsConfig.DimensionScaling.DimensionSlot slot = dimSlots[i];
            int n = i + 1;
            var sub = eb.startSubCategory(Component.translatable("buffmobs.config.dimensionScaling.slot", n));
            sub.add(eb.startStrField(Component.translatable("buffmobs.config.dimensionScaling.dimensionId"), slot.dimensionName)
                    .setDefaultValue("")
                    .setTooltip(tt("buffmobs.config.dimensionScaling.dimensionId.tooltip"))
                    .setSaveConsumer(v -> slot.dimensionName = v).build());
            sub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.healthPercent"), slot.healthMultiplier)
                    .setDefaultValue(100).setMin(100).setMax(999999)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.healthPercent.tooltip"))
                    .setSaveConsumer(v -> slot.healthMultiplier = v).build());
            sub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.damagePercent"), slot.damageMultiplier)
                    .setDefaultValue(100).setMin(100).setMax(999999)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.damagePercent.tooltip"))
                    .setSaveConsumer(v -> slot.damageMultiplier = v).build());
            sub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.speedPercent"), slot.speedMultiplier)
                    .setDefaultValue(100).setMin(100).setMax(999999)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.speedPercent.tooltip"))
                    .setSaveConsumer(v -> slot.speedMultiplier = v).build());
            sub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.attackSpeedPercent"), slot.attackSpeedMultiplier)
                    .setDefaultValue(100).setMin(100).setMax(999999)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.attackSpeedPercent.tooltip"))
                    .setSaveConsumer(v -> slot.attackSpeedMultiplier = v).build());
            sub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.armorAddition"), slot.armorAddition)
                    .setDefaultValue(0).setMin(0).setMax(999999)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.armorAddition.tooltip"))
                    .setSaveConsumer(v -> slot.armorAddition = v).build());
            sub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.toughnessAddition"), slot.armorToughnessAddition)
                    .setDefaultValue(0).setMin(0).setMax(999999)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.toughnessAddition.tooltip"))
                    .setSaveConsumer(v -> slot.armorToughnessAddition = v).build());
            dimScaling.addEntry(sub.build());
        }

        ConfigMenu.Category mobFilter = builder.getOrCreateCategory(Component.translatable("buffmobs.config.mobFilter"));
        mobFilter.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobFilter.useWhitelist"), cfg.mobFilter.useWhitelist)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.mobFilter.useWhitelist.tooltip"))
                .setSaveConsumer(v -> cfg.mobFilter.useWhitelist = v).build());
        mobFilter.addEntry(eb.startStrList(Component.translatable("buffmobs.config.mobFilter.blacklist"), new ArrayList<>(cfg.mobFilter.blacklist))
                .setDefaultValue(List.of("minecraft:warden"))
                .setTooltip(tt("buffmobs.config.mobFilter.blacklist.tooltip"))
                .setSaveConsumer(v -> cfg.mobFilter.blacklist = v).build());
        mobFilter.addEntry(eb.startStrList(Component.translatable("buffmobs.config.mobFilter.whitelist"), new ArrayList<>(cfg.mobFilter.whitelist))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.mobFilter.whitelist.tooltip"))
                .setSaveConsumer(v -> cfg.mobFilter.whitelist = v).build());

        ConfigMenu.Category modFilter = builder.getOrCreateCategory(Component.translatable("buffmobs.config.modidFilter"));
        modFilter.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.modidFilter.useWhitelist"), cfg.modidFilter.useWhitelist)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.modidFilter.useWhitelist.tooltip"))
                .setSaveConsumer(v -> cfg.modidFilter.useWhitelist = v).build());
        modFilter.addEntry(eb.startStrList(Component.translatable("buffmobs.config.modidFilter.blacklist"), new ArrayList<>(cfg.modidFilter.blacklist))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.modidFilter.blacklist.tooltip"))
                .setSaveConsumer(v -> cfg.modidFilter.blacklist = v).build());
        modFilter.addEntry(eb.startStrList(Component.translatable("buffmobs.config.modidFilter.whitelist"), new ArrayList<>(cfg.modidFilter.whitelist))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.modidFilter.whitelist.tooltip"))
                .setSaveConsumer(v -> cfg.modidFilter.whitelist = v).build());

        ConfigMenu.Category dimFilter = builder.getOrCreateCategory(Component.translatable("buffmobs.config.dimensionFilter"));
        dimFilter.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.dimensionFilter.useWhitelist"), cfg.dimensionFilter.useWhitelist)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.dimensionFilter.useWhitelist.tooltip"))
                .setSaveConsumer(v -> cfg.dimensionFilter.useWhitelist = v).build());
        dimFilter.addEntry(eb.startStrList(Component.translatable("buffmobs.config.dimensionFilter.blacklist"), new ArrayList<>(cfg.dimensionFilter.blacklist))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.dimensionFilter.blacklist.tooltip"))
                .setSaveConsumer(v -> cfg.dimensionFilter.blacklist = v).build());
        dimFilter.addEntry(eb.startStrList(Component.translatable("buffmobs.config.dimensionFilter.whitelist"), new ArrayList<>(cfg.dimensionFilter.whitelist))
                .setDefaultValue(List.of("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"))
                .setTooltip(tt("buffmobs.config.dimensionFilter.whitelist.tooltip"))
                .setSaveConsumer(v -> cfg.dimensionFilter.whitelist = v).build());

        ConfigMenu.Category ranged = builder.getOrCreateCategory(Component.translatable("buffmobs.config.rangedMeleeSwitching"));
        ranged.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.rangedMeleeSwitching.enabled"), cfg.rangedMeleeSwitching.enabled)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.rangedMeleeSwitching.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.rangedMeleeSwitching.enabled = v).build());
        ranged.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.rangedMeleeSwitching.behaviorMode"),
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
        ranged.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.rangedMeleeSwitching.switchDistance"), cfg.rangedMeleeSwitching.switchDistance)
                .setDefaultValue(4.0).setMin(1.0).setMax(16.0)
                .setTooltip(tt("buffmobs.config.rangedMeleeSwitching.switchDistance.tooltip"))
                .setSaveConsumer(v -> cfg.rangedMeleeSwitching.switchDistance = v).build());
        ranged.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.rangedMeleeSwitching.meleeSpeedMultiplier"), cfg.rangedMeleeSwitching.meleeSpeedMultiplier)
                .setDefaultValue(0.9).setMin(0.1).setMax(5.0)
                .setTooltip(tt("buffmobs.config.rangedMeleeSwitching.meleeSpeedMultiplier.tooltip"))
                .setSaveConsumer(v -> cfg.rangedMeleeSwitching.meleeSpeedMultiplier = v).build());
        ranged.addEntry(eb.startStrList(Component.translatable("buffmobs.config.rangedMeleeSwitching.customRangedMobs"), new ArrayList<>(cfg.rangedMeleeSwitching.customRangedMobs))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.rangedMeleeSwitching.customRangedMobs.tooltip"))
                .setSaveConsumer(v -> cfg.rangedMeleeSwitching.customRangedMobs = v).build());

        var weaponSub = eb.startSubCategory(Component.translatable("buffmobs.config.rangedMeleeSwitching.weaponUnlockDays"));
        weaponSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.stoneSwordUnlockDay"), cfg.rangedMeleeSwitching.stoneSwordUnlockDay)
                .setDefaultValue(0).setMin(0).setMax(365).setSaveConsumer(v -> cfg.rangedMeleeSwitching.stoneSwordUnlockDay = v).build());
        weaponSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.ironSwordUnlockDay"), cfg.rangedMeleeSwitching.ironSwordUnlockDay)
                .setDefaultValue(7).setMin(0).setMax(365).setSaveConsumer(v -> cfg.rangedMeleeSwitching.ironSwordUnlockDay = v).build());
        weaponSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.diamondSwordUnlockDay"), cfg.rangedMeleeSwitching.diamondSwordUnlockDay)
                .setDefaultValue(21).setMin(0).setMax(365).setSaveConsumer(v -> cfg.rangedMeleeSwitching.diamondSwordUnlockDay = v).build());
        weaponSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.netheriteSwordUnlockDay"), cfg.rangedMeleeSwitching.netheriteSwordUnlockDay)
                .setDefaultValue(60).setMin(0).setMax(365).setSaveConsumer(v -> cfg.rangedMeleeSwitching.netheriteSwordUnlockDay = v).build());
        weaponSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.goldenAxeUnlockDay"), cfg.rangedMeleeSwitching.goldenAxeUnlockDay)
                .setDefaultValue(0).setMin(0).setMax(365).setSaveConsumer(v -> cfg.rangedMeleeSwitching.goldenAxeUnlockDay = v).build());
        weaponSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.diamondAxeUnlockDay"), cfg.rangedMeleeSwitching.diamondAxeUnlockDay)
                .setDefaultValue(14).setMin(0).setMax(365).setSaveConsumer(v -> cfg.rangedMeleeSwitching.diamondAxeUnlockDay = v).build());
        weaponSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.netheriteAxeUnlockDay"), cfg.rangedMeleeSwitching.netheriteAxeUnlockDay)
                .setDefaultValue(45).setMin(0).setMax(365).setSaveConsumer(v -> cfg.rangedMeleeSwitching.netheriteAxeUnlockDay = v).build());
        ranged.addEntry(weaponSub.build());

        var enchantSub = eb.startSubCategory(Component.translatable("buffmobs.config.rangedMeleeSwitching.enchantments"));
        enchantSub.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.rangedMeleeSwitching.enchantmentsEnabled"), cfg.rangedMeleeSwitching.enchantmentsEnabled)
                .setDefaultValue(true).setSaveConsumer(v -> cfg.rangedMeleeSwitching.enchantmentsEnabled = v).build());
        enchantSub.add(eb.startIntSlider(Component.translatable("buffmobs.config.rangedMeleeSwitching.maxEnchantmentsPerWeapon"), cfg.rangedMeleeSwitching.maxEnchantmentsPerWeapon, 1, 4)
                .setDefaultValue(2).setSaveConsumer(v -> cfg.rangedMeleeSwitching.maxEnchantmentsPerWeapon = v).build());
        enchantSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.daysPerEnchantmentLevel"), cfg.rangedMeleeSwitching.daysPerEnchantmentLevel)
                .setDefaultValue(7).setMin(1).setMax(30).setSaveConsumer(v -> cfg.rangedMeleeSwitching.daysPerEnchantmentLevel = v).build());
        enchantSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.sharpnessUnlockDay"), cfg.rangedMeleeSwitching.sharpnessUnlockDay)
                .setDefaultValue(0).setMin(0).setMax(365).setSaveConsumer(v -> cfg.rangedMeleeSwitching.sharpnessUnlockDay = v).build());
        enchantSub.add(eb.startIntSlider(Component.translatable("buffmobs.config.rangedMeleeSwitching.sharpnessMaxLevel"), cfg.rangedMeleeSwitching.sharpnessMaxLevel, 1, 5)
                .setDefaultValue(5).setSaveConsumer(v -> cfg.rangedMeleeSwitching.sharpnessMaxLevel = v).build());
        enchantSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.fireAspectUnlockDay"), cfg.rangedMeleeSwitching.fireAspectUnlockDay)
                .setDefaultValue(14).setMin(0).setMax(365).setSaveConsumer(v -> cfg.rangedMeleeSwitching.fireAspectUnlockDay = v).build());
        enchantSub.add(eb.startIntSlider(Component.translatable("buffmobs.config.rangedMeleeSwitching.fireAspectMaxLevel"), cfg.rangedMeleeSwitching.fireAspectMaxLevel, 1, 2)
                .setDefaultValue(2).setSaveConsumer(v -> cfg.rangedMeleeSwitching.fireAspectMaxLevel = v).build());
        enchantSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.knockbackUnlockDay"), cfg.rangedMeleeSwitching.knockbackUnlockDay)
                .setDefaultValue(7).setMin(0).setMax(365).setSaveConsumer(v -> cfg.rangedMeleeSwitching.knockbackUnlockDay = v).build());
        enchantSub.add(eb.startIntSlider(Component.translatable("buffmobs.config.rangedMeleeSwitching.knockbackMaxLevel"), cfg.rangedMeleeSwitching.knockbackMaxLevel, 1, 2)
                .setDefaultValue(2).setSaveConsumer(v -> cfg.rangedMeleeSwitching.knockbackMaxLevel = v).build());
        enchantSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.sweepingEdgeUnlockDay"), cfg.rangedMeleeSwitching.sweepingEdgeUnlockDay)
                .setDefaultValue(21).setMin(0).setMax(365).setSaveConsumer(v -> cfg.rangedMeleeSwitching.sweepingEdgeUnlockDay = v).build());
        enchantSub.add(eb.startIntSlider(Component.translatable("buffmobs.config.rangedMeleeSwitching.sweepingEdgeMaxLevel"), cfg.rangedMeleeSwitching.sweepingEdgeMaxLevel, 1, 3)
                .setDefaultValue(3).setSaveConsumer(v -> cfg.rangedMeleeSwitching.sweepingEdgeMaxLevel = v).build());
        ranged.addEntry(enchantSub.build());

        ConfigMenu.Category draft = builder.getOrCreateCategory(Component.translatable("buffmobs.config.combatDraft"));
        draft.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.combatDraft.enabled"), cfg.combatDraft.enabled)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.combatDraft.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.combatDraft.enabled = v).build());
        draft.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.combatDraft.healthThreshold"), cfg.combatDraft.healthThreshold)
                .setDefaultValue(0.20).setMin(0.01).setMax(0.99)
                .setTooltip(tt("buffmobs.config.combatDraft.healthThreshold.tooltip"))
                .setSaveConsumer(v -> cfg.combatDraft.healthThreshold = v).build());
        draft.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.combatDraft.regenAmplifier"), cfg.combatDraft.regenAmplifier, 1, 10)
                .setDefaultValue(4)
                .setTextGetter(v -> Component.translatable("buffmobs.config.effects.slider.level", v))
                .setTooltip(tt("buffmobs.config.combatDraft.regenAmplifier.tooltip"))
                .setSaveConsumer(v -> cfg.combatDraft.regenAmplifier = v).build());
        draft.addEntry(eb.startIntField(Component.translatable("buffmobs.config.combatDraft.regenDuration"), cfg.combatDraft.regenDuration)
                .setDefaultValue(10).setMin(1).setMax(120)
                .setTooltip(tt("buffmobs.config.combatDraft.regenDuration.tooltip"))
                .setSaveConsumer(v -> cfg.combatDraft.regenDuration = v).build());
        draft.addEntry(eb.startIntField(Component.translatable("buffmobs.config.combatDraft.cooldownTicks"), cfg.combatDraft.cooldownTicks)
                .setDefaultValue(600).setMin(20).setMax(72000)
                .setTooltip(tt("buffmobs.config.combatDraft.cooldownTicks.tooltip"))
                .setSaveConsumer(v -> cfg.combatDraft.cooldownTicks = v).build());
        draft.addEntry(eb.startIntField(Component.translatable("buffmobs.config.combatDraft.maxUses"), cfg.combatDraft.maxUses)
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
        draft.addEntry(draftFilterSub.build());

        ConfigMenu.Category presets = builder.getOrCreateCategory(Component.translatable("buffmobs.config.mobPresets"));
        presets.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobPresets.enabled"), cfg.mobPresets.enabled)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.mobPresets.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.mobPresets.enabled = v).build());
        presets.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobPresets.overrideDimensionScaling"), cfg.mobPresets.overrideDimensionScaling)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.mobPresets.overrideDimensionScaling.tooltip"))
                .setSaveConsumer(v -> cfg.mobPresets.overrideDimensionScaling = v).build());

        BuffMobsConfig.MobPresets.PresetSlot[] presetSlots = {
                cfg.mobPresets.preset1, cfg.mobPresets.preset2, cfg.mobPresets.preset3,
                cfg.mobPresets.preset4, cfg.mobPresets.preset5
        };
        BuffMobsConfig.MobPresets presetDefaults = new BuffMobsConfig.MobPresets();
        BuffMobsConfig.MobPresets.PresetSlot[] defaultPresetSlots = {
                presetDefaults.preset1, presetDefaults.preset2, presetDefaults.preset3,
                presetDefaults.preset4, presetDefaults.preset5
        };
        for (int i = 0; i < presetSlots.length; i++) {
            BuffMobsConfig.MobPresets.PresetSlot p = presetSlots[i];
            BuffMobsConfig.MobPresets.PresetSlot defaultPreset = defaultPresetSlots[i];
            int n = i + 1;
            String name = p.presetName;
            String builtInPresetName = n == 1 ? "default" : n == 2 ? "boss" : "";
            var pSub = eb.startSubCategory(() -> {
                String currentName = p.presetName;
                return currentName != null && !currentName.isEmpty() && !currentName.equals(builtInPresetName)
                        ? Component.translatable("buffmobs.config.mobPresets.slot.named", n, currentName)
                        : Component.translatable("buffmobs.config.mobPresets.slot", n);
            });
            pSub.add(eb.startStrField(Component.translatable("buffmobs.config.mobPresets.presetName"), name == null ? "" : name)
                    .setTooltip(tt("buffmobs.config.mobPresets.presetName.tooltip"))
                    .setSaveConsumer(v -> p.presetName = v).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.healthMultiplier"), p.healthMultiplier)
                    .setDefaultValue(defaultPreset.healthMultiplier).setMin(0.01).setMax(999999.0).setSaveConsumer(v -> p.healthMultiplier = v).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.damageMultiplier"), p.damageMultiplier)
                    .setDefaultValue(defaultPreset.damageMultiplier).setMin(0.01).setMax(999999.0).setSaveConsumer(v -> p.damageMultiplier = v).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.speedMultiplier"), p.speedMultiplier)
                    .setDefaultValue(defaultPreset.speedMultiplier).setMin(0.01).setMax(999999.0).setSaveConsumer(v -> p.speedMultiplier = v).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.attackSpeedMultiplier"), p.attackSpeedMultiplier)
                    .setDefaultValue(defaultPreset.attackSpeedMultiplier).setMin(0.01).setMax(999999.0).setSaveConsumer(v -> p.attackSpeedMultiplier = v).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.armorAddition"), p.armorAddition)
                    .setDefaultValue(defaultPreset.armorAddition).setMin(0.0).setMax(999999.0).setSaveConsumer(v -> p.armorAddition = v).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.toughnessAddition"), p.armorToughnessAddition)
                    .setDefaultValue(defaultPreset.armorToughnessAddition).setMin(0.0).setMax(999999.0).setSaveConsumer(v -> p.armorToughnessAddition = v).build());
            presets.addEntry(pSub.build());
        }
        presets.addEntry(eb.startStrList(Component.translatable("buffmobs.config.mobPresets.mobMapping"), new ArrayList<>(cfg.mobPresets.mobMapping))
                .setDefaultValue(List.of("minecraft:zombie:default", "minecraft:skeleton:default",
                        "minecraft:ender_dragon:boss", "minecraft:wither:boss"))
                .setTooltip(tt("buffmobs.config.mobPresets.mobMapping.tooltip"))
                .setSaveConsumer(v -> cfg.mobPresets.mobMapping = v).build());

        ConfigMenu.Category passiveAggression = builder.getOrCreateCategory(Component.translatable("buffmobs.config.passiveMobAggression"));
        passiveAggression.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.passiveMobAggression.mode"),
                        BuffMobsConfig.PassiveMobAggression.Mode.class,
                        cfg.passiveMobAggression.mode)
                .setDefaultValue(BuffMobsConfig.PassiveMobAggression.Mode.OFF)
                .setEnumNameProvider(e -> switch ((BuffMobsConfig.PassiveMobAggression.Mode) e) {
                    case OFF     -> Component.translatable("buffmobs.config.passiveMobAggression.mode.off");
                    case NEUTRAL -> Component.translatable("buffmobs.config.passiveMobAggression.mode.neutral");
                    case HOSTILE -> Component.translatable("buffmobs.config.passiveMobAggression.mode.hostile");
                })
                .setTooltip(tt("buffmobs.config.passiveMobAggression.mode.tooltip"))
                .setSaveConsumer(v -> {
                    cfg.passiveMobAggression.mode = v;
                    com.khotyz.buffmobs.util.PassiveMobAggressionHandler.forceReinit();
                }).build());
        passiveAggression.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.passiveMobAggression.baseDamage"), cfg.passiveMobAggression.baseDamage)
                .setDefaultValue(3.0).setMin(0.5).setMax(100.0)
                .setTooltip(tt("buffmobs.config.passiveMobAggression.baseDamage.tooltip"))
                .setSaveConsumer(v -> cfg.passiveMobAggression.baseDamage = v).build());
        passiveAggression.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.passiveMobAggression.scaleWithHealth"), cfg.passiveMobAggression.scaleWithHealth)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.passiveMobAggression.scaleWithHealth.tooltip"))
                .setSaveConsumer(v -> cfg.passiveMobAggression.scaleWithHealth = v).build());
        passiveAggression.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.passiveMobAggression.healthScaleFactor"), cfg.passiveMobAggression.healthScaleFactor)
                .setDefaultValue(0.1).setMin(0.0).setMax(100.0)
                .setTooltip(tt("buffmobs.config.passiveMobAggression.healthScaleFactor.tooltip"))
                .setSaveConsumer(v -> cfg.passiveMobAggression.healthScaleFactor = v).build());

        var passiveMobFilterSub = eb.startSubCategory(Component.translatable("buffmobs.config.passiveMobAggression.mobFilter"));
        passiveMobFilterSub.add(eb.startStrList(Component.translatable("buffmobs.config.passiveMobAggression.whitelist"), new ArrayList<>(cfg.passiveMobAggression.whitelist))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.passiveMobAggression.whitelist.tooltip"))
                .setSaveConsumer(v -> cfg.passiveMobAggression.whitelist = v).build());
        passiveMobFilterSub.add(eb.startStrList(Component.translatable("buffmobs.config.passiveMobAggression.blacklist"), new ArrayList<>(cfg.passiveMobAggression.blacklist))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.passiveMobAggression.blacklist.tooltip"))
                .setSaveConsumer(v -> cfg.passiveMobAggression.blacklist = v).build());
        passiveAggression.addEntry(passiveMobFilterSub.build());

        ConfigMenu.Category zombieHandling = builder.getOrCreateCategory(Component.translatable("buffmobs.config.zombieHandling"));
        zombieHandling.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.zombieHandling.disableLeaderZombies"), cfg.zombieHandling.disableLeaderZombies)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.zombieHandling.disableLeaderZombies.tooltip"))
                .setSaveConsumer(v -> cfg.zombieHandling.disableLeaderZombies = v).build());
        zombieHandling.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.zombieHandling.excludeLeaderBonusFromMultiplier"), cfg.zombieHandling.excludeLeaderBonusFromMultiplier)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.zombieHandling.excludeLeaderBonusFromMultiplier.tooltip"))
                .setSaveConsumer(v -> cfg.zombieHandling.excludeLeaderBonusFromMultiplier = v).build());

        ConfigMenu.Category healthSync = builder.getOrCreateCategory(Component.translatable("buffmobs.config.healthSync"));
        healthSync.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.healthSync.enabled"), cfg.healthSync.enabled)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.healthSync.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.healthSync.enabled = v).build());
        healthSync.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.healthSync.mode"),
                        BuffMobsConfig.HealthSync.Mode.class,
                        cfg.healthSync.mode)
                .setDefaultValue(BuffMobsConfig.HealthSync.Mode.OVERRIDE)
                .setEnumNameProvider(e -> switch ((BuffMobsConfig.HealthSync.Mode) e) {
                    case OVERRIDE -> Component.translatable("buffmobs.config.healthSync.mode.override");
                    case STACK    -> Component.translatable("buffmobs.config.healthSync.mode.stack");
                })
                .setTooltip(tt("buffmobs.config.healthSync.mode.tooltip"))
                .setSaveConsumer(v -> cfg.healthSync.mode = v).build());

        ConfigMenu.Category dimMaxHealth = builder.getOrCreateCategory(Component.translatable("buffmobs.config.dimensionMaxHealth"));
        dimMaxHealth.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.dimensionMaxHealth.enabled"), cfg.dimensionMaxHealth.enabled)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.dimensionMaxHealth.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.dimensionMaxHealth.enabled = v).build());

        BuffMobsConfig.DimensionMaxHealth.DimensionMaxHealthSlot[] dimHealthSlots = {
                cfg.dimensionMaxHealth.slot1, cfg.dimensionMaxHealth.slot2, cfg.dimensionMaxHealth.slot3,
                cfg.dimensionMaxHealth.slot4, cfg.dimensionMaxHealth.slot5
        };
        for (int i = 0; i < dimHealthSlots.length; i++) {
            BuffMobsConfig.DimensionMaxHealth.DimensionMaxHealthSlot slot = dimHealthSlots[i];
            int n = i + 1;
            var dhSub = eb.startSubCategory(Component.translatable("buffmobs.config.dimensionMaxHealth.slot", n));
            dhSub.add(eb.startStrField(Component.translatable("buffmobs.config.dimensionMaxHealth.dimensionId"), slot.dimensionId)
                    .setDefaultValue("")
                    .setTooltip(tt("buffmobs.config.dimensionMaxHealth.dimensionId.tooltip"))
                    .setSaveConsumer(v -> slot.dimensionId = v).build());
            dhSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.dimensionMaxHealth.maxHealth"), slot.maxHealth)
                    .setDefaultValue(20.0).setMin(0.0).setMax(999999.0)
                    .setTooltip(tt("buffmobs.config.dimensionMaxHealth.maxHealth.tooltip"))
                    .setSaveConsumer(v -> slot.maxHealth = v).build());
            var dhFilterSub = eb.startSubCategory(Component.translatable("buffmobs.config.dimensionMaxHealth.mobFilter"));
            dhFilterSub.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.dimensionMaxHealth.useAllowlist"), slot.useAllowlist)
                    .setDefaultValue(false)
                    .setTooltip(tt("buffmobs.config.dimensionMaxHealth.useAllowlist.tooltip"))
                    .setSaveConsumer(v -> slot.useAllowlist = v).build());
            dhFilterSub.add(eb.startStrList(Component.translatable("buffmobs.config.dimensionMaxHealth.allowlist"), new ArrayList<>(slot.allowlist))
                    .setDefaultValue(new ArrayList<>())
                    .setTooltip(tt("buffmobs.config.dimensionMaxHealth.allowlist.tooltip"))
                    .setSaveConsumer(v -> slot.allowlist = v).build());
            dhFilterSub.add(eb.startStrList(Component.translatable("buffmobs.config.dimensionMaxHealth.denylist"), new ArrayList<>(slot.denylist))
                    .setDefaultValue(new ArrayList<>())
                    .setTooltip(tt("buffmobs.config.dimensionMaxHealth.denylist.tooltip"))
                    .setSaveConsumer(v -> slot.denylist = v).build());
            dhSub.add(dhFilterSub.build());
            dimMaxHealth.addEntry(dhSub.build());
        }

        ConfigMenu.Category mobArmor = builder.getOrCreateCategory(Component.translatable("buffmobs.config.mobArmor"));
        mobArmor.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobArmor.enabled"), cfg.mobArmor.enabled)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.mobArmor.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.enabled = v).build());
        mobArmor.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.equipChance"), cfg.mobArmor.equipChance)
                .setDefaultValue(1.0).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.mobArmor.equipChance.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.equipChance = v).build());
        var mobArmorSetup = eb.startSubCategory(Component.translatable("buffmobs.config.mobArmor.equipmentSetup"));
        mobArmorSetup.add(eb.startEnumSelector(Component.translatable("buffmobs.config.mobArmor.setupMode"),
                        BuffMobsConfig.MobArmor.EquipmentSetupMode.class,
                        cfg.mobArmor.setupMode)
                .setDefaultValue(BuffMobsConfig.MobArmor.EquipmentSetupMode.FULL_SET)
                .setEnumNameProvider(e -> switch ((BuffMobsConfig.MobArmor.EquipmentSetupMode) e) {
                    case FULL_SET -> Component.translatable("buffmobs.config.mobArmor.setupMode.full_set");
                    case RANDOM_PIECES -> Component.translatable("buffmobs.config.mobArmor.setupMode.random_pieces");
                })
                .setTooltip(tt("buffmobs.config.mobArmor.setupMode.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.setupMode = v).build());
        mobArmorSetup.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobArmor.mixedTiersEnabled"), cfg.mobArmor.mixedTiersEnabled)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.mobArmor.mixedTiersEnabled.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.mixedTiersEnabled = v).build());
        mobArmorSetup.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.helmetChance"), cfg.mobArmor.helmetChance)
                .setDefaultValue(1.0).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.mobArmor.helmetChance.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.helmetChance = v).build());
        mobArmorSetup.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.chestplateChance"), cfg.mobArmor.chestplateChance)
                .setDefaultValue(1.0).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.mobArmor.chestplateChance.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.chestplateChance = v).build());
        mobArmorSetup.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.leggingsChance"), cfg.mobArmor.leggingsChance)
                .setDefaultValue(1.0).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.mobArmor.leggingsChance.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.leggingsChance = v).build());
        mobArmorSetup.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.bootsChance"), cfg.mobArmor.bootsChance)
                .setDefaultValue(1.0).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.mobArmor.bootsChance.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.bootsChance = v).build());
        mobArmorSetup.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.minPieces"), cfg.mobArmor.minPieces)
                .setDefaultValue(1).setMin(0).setMax(4)
                .setTooltip(tt("buffmobs.config.mobArmor.minPieces.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.minPieces = v).build());
        mobArmorSetup.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.maxPieces"), cfg.mobArmor.maxPieces)
                .setDefaultValue(4).setMin(0).setMax(4)
                .setTooltip(tt("buffmobs.config.mobArmor.maxPieces.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.maxPieces = v).build());
        mobArmorSetup.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobArmor.dropChanceEnabled"), cfg.mobArmor.dropChanceEnabled)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.mobArmor.dropChanceEnabled.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.dropChanceEnabled = v).build());
        mobArmorSetup.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.dropChance"), cfg.mobArmor.dropChance)
                .setDefaultValue(0.085).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.mobArmor.dropChance.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.dropChance = v).build());
        mobArmor.addEntry(mobArmorSetup.build());
        var mobArmorSub1 = eb.startSubCategory(Component.translatable("buffmobs.config.mobArmor.tierUnlockDays"));
        mobArmorSub1.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.leatherUnlockDay"), cfg.mobArmor.leatherUnlockDay)
                .setDefaultValue(0).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.leatherUnlockDay.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.leatherUnlockDay = v).build());
        mobArmorSub1.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.goldUnlockDay"), cfg.mobArmor.goldUnlockDay)
                .setDefaultValue(7).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.goldUnlockDay.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.goldUnlockDay = v).build());
        mobArmorSub1.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.ironUnlockDay"), cfg.mobArmor.ironUnlockDay)
                .setDefaultValue(14).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.ironUnlockDay.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.ironUnlockDay = v).build());
        mobArmorSub1.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.diamondUnlockDay"), cfg.mobArmor.diamondUnlockDay)
                .setDefaultValue(30).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.diamondUnlockDay.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.diamondUnlockDay = v).build());
        mobArmorSub1.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.netheriteUnlockDay"), cfg.mobArmor.netheriteUnlockDay)
                .setDefaultValue(60).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.netheriteUnlockDay.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.netheriteUnlockDay = v).build());
        mobArmor.addEntry(mobArmorSub1.build());
        var mobArmorSub2 = eb.startSubCategory(Component.translatable("buffmobs.config.mobArmor.tierWeights"));
        mobArmorSub2.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.leatherWeight"), cfg.mobArmor.leatherWeight)
                .setDefaultValue(50.0).setMin(0.0).setMax(1000.0)
                .setTooltip(tt("buffmobs.config.mobArmor.leatherWeight.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.leatherWeight = v).build());
        mobArmorSub2.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.goldWeight"), cfg.mobArmor.goldWeight)
                .setDefaultValue(25.0).setMin(0.0).setMax(1000.0)
                .setTooltip(tt("buffmobs.config.mobArmor.goldWeight.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.goldWeight = v).build());
        mobArmorSub2.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.ironWeight"), cfg.mobArmor.ironWeight)
                .setDefaultValue(15.0).setMin(0.0).setMax(1000.0)
                .setTooltip(tt("buffmobs.config.mobArmor.ironWeight.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.ironWeight = v).build());
        mobArmorSub2.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.diamondWeight"), cfg.mobArmor.diamondWeight)
                .setDefaultValue(4.0).setMin(0.0).setMax(1000.0)
                .setTooltip(tt("buffmobs.config.mobArmor.diamondWeight.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.diamondWeight = v).build());
        mobArmorSub2.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.netheriteWeight"), cfg.mobArmor.netheriteWeight)
                .setDefaultValue(1.0).setMin(0.0).setMax(1000.0)
                .setTooltip(tt("buffmobs.config.mobArmor.netheriteWeight.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.netheriteWeight = v).build());
        mobArmor.addEntry(mobArmorSub2.build());
        var mobArmorSub3 = eb.startSubCategory(Component.translatable("buffmobs.config.mobArmor.enchantments"));
        mobArmorSub3.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobArmor.enchantmentsEnabled"), cfg.mobArmor.enchantmentsEnabled)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.mobArmor.enchantmentsEnabled.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.enchantmentsEnabled = v).build());
        mobArmorSub3.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.maxEnchantmentsPerPiece"), cfg.mobArmor.maxEnchantmentsPerPiece)
                .setDefaultValue(2).setMin(1).setMax(4)
                .setTooltip(tt("buffmobs.config.mobArmor.maxEnchantmentsPerPiece.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.maxEnchantmentsPerPiece = v).build());
        mobArmorSub3.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.daysPerEnchantmentLevel"), cfg.mobArmor.daysPerEnchantmentLevel)
                .setDefaultValue(7).setMin(1).setMax(30)
                .setTooltip(tt("buffmobs.config.mobArmor.daysPerEnchantmentLevel.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.daysPerEnchantmentLevel = v).build());
        mobArmorSub3.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.enchantedPieceChance"), cfg.mobArmor.enchantedPieceChance)
                .setDefaultValue(1.0).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.mobArmor.enchantedPieceChance.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.enchantedPieceChance = v).build());
        mobArmorSub3.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.protectionUnlockDay"), cfg.mobArmor.protectionUnlockDay)
                .setDefaultValue(7).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.protectionUnlockDay.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.protectionUnlockDay = v).build());
        mobArmorSub3.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.protectionMaxLevel"), cfg.mobArmor.protectionMaxLevel)
                .setDefaultValue(4).setMin(1).setMax(4)
                .setTooltip(tt("buffmobs.config.mobArmor.protectionMaxLevel.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.protectionMaxLevel = v).build());
        mobArmorSub3.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.thornsUnlockDay"), cfg.mobArmor.thornsUnlockDay)
                .setDefaultValue(21).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.thornsUnlockDay.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.thornsUnlockDay = v).build());
        mobArmorSub3.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.thornsMaxLevel"), cfg.mobArmor.thornsMaxLevel)
                .setDefaultValue(3).setMin(1).setMax(3)
                .setTooltip(tt("buffmobs.config.mobArmor.thornsMaxLevel.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.thornsMaxLevel = v).build());
        mobArmorSub3.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.featherFallingUnlockDay"), cfg.mobArmor.featherFallingUnlockDay)
                .setDefaultValue(14).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.featherFallingUnlockDay.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.featherFallingUnlockDay = v).build());
        mobArmorSub3.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.featherFallingMaxLevel"), cfg.mobArmor.featherFallingMaxLevel)
                .setDefaultValue(4).setMin(1).setMax(4)
                .setTooltip(tt("buffmobs.config.mobArmor.featherFallingMaxLevel.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.featherFallingMaxLevel = v).build());
        var mobArmorAdditionalEnchantments = eb.startSubCategory(Component.translatable("buffmobs.config.mobArmor.additionalEnchantments"));
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.fireProtectionUnlockDay"), cfg.mobArmor.fireProtectionUnlockDay)
                .setDefaultValue(14).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.fireProtectionUnlockDay.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.fireProtectionUnlockDay = v).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.fireProtectionMaxLevel"), cfg.mobArmor.fireProtectionMaxLevel)
                .setDefaultValue(4).setMin(1).setMax(4)
                .setTooltip(tt("buffmobs.config.mobArmor.fireProtectionMaxLevel.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.fireProtectionMaxLevel = v).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.blastProtectionUnlockDay"), cfg.mobArmor.blastProtectionUnlockDay)
                .setDefaultValue(21).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.blastProtectionUnlockDay.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.blastProtectionUnlockDay = v).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.blastProtectionMaxLevel"), cfg.mobArmor.blastProtectionMaxLevel)
                .setDefaultValue(4).setMin(1).setMax(4)
                .setTooltip(tt("buffmobs.config.mobArmor.blastProtectionMaxLevel.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.blastProtectionMaxLevel = v).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.projectileProtectionUnlockDay"), cfg.mobArmor.projectileProtectionUnlockDay)
                .setDefaultValue(14).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.projectileProtectionUnlockDay.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.projectileProtectionUnlockDay = v).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.projectileProtectionMaxLevel"), cfg.mobArmor.projectileProtectionMaxLevel)
                .setDefaultValue(4).setMin(1).setMax(4)
                .setTooltip(tt("buffmobs.config.mobArmor.projectileProtectionMaxLevel.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.projectileProtectionMaxLevel = v).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.unbreakingUnlockDay"), cfg.mobArmor.unbreakingUnlockDay)
                .setDefaultValue(0).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.unbreakingUnlockDay.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.unbreakingUnlockDay = v).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.unbreakingMaxLevel"), cfg.mobArmor.unbreakingMaxLevel)
                .setDefaultValue(3).setMin(1).setMax(3)
                .setTooltip(tt("buffmobs.config.mobArmor.unbreakingMaxLevel.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.unbreakingMaxLevel = v).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.respirationUnlockDay"), cfg.mobArmor.respirationUnlockDay)
                .setDefaultValue(14).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.respirationUnlockDay.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.respirationUnlockDay = v).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.respirationMaxLevel"), cfg.mobArmor.respirationMaxLevel)
                .setDefaultValue(3).setMin(1).setMax(3)
                .setTooltip(tt("buffmobs.config.mobArmor.respirationMaxLevel.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.respirationMaxLevel = v).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.aquaAffinityUnlockDay"), cfg.mobArmor.aquaAffinityUnlockDay)
                .setDefaultValue(21).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.aquaAffinityUnlockDay.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.aquaAffinityUnlockDay = v).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.aquaAffinityMaxLevel"), cfg.mobArmor.aquaAffinityMaxLevel)
                .setDefaultValue(1).setMin(1).setMax(1)
                .setTooltip(tt("buffmobs.config.mobArmor.aquaAffinityMaxLevel.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.aquaAffinityMaxLevel = v).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.depthStriderUnlockDay"), cfg.mobArmor.depthStriderUnlockDay)
                .setDefaultValue(21).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.depthStriderUnlockDay.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.depthStriderUnlockDay = v).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.depthStriderMaxLevel"), cfg.mobArmor.depthStriderMaxLevel)
                .setDefaultValue(3).setMin(1).setMax(3)
                .setTooltip(tt("buffmobs.config.mobArmor.depthStriderMaxLevel.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.depthStriderMaxLevel = v).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.frostWalkerUnlockDay"), cfg.mobArmor.frostWalkerUnlockDay)
                .setDefaultValue(28).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.frostWalkerUnlockDay.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.frostWalkerUnlockDay = v).build());
        mobArmorAdditionalEnchantments.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.frostWalkerMaxLevel"), cfg.mobArmor.frostWalkerMaxLevel)
                .setDefaultValue(2).setMin(1).setMax(2)
                .setTooltip(tt("buffmobs.config.mobArmor.frostWalkerMaxLevel.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.frostWalkerMaxLevel = v).build());
        mobArmorSub3.add(mobArmorAdditionalEnchantments.build());
        var mobArmorCurses = eb.startSubCategory(Component.translatable("buffmobs.config.mobArmor.curses"));
        mobArmorCurses.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobArmor.cursesEnabled"), cfg.mobArmor.cursesEnabled)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.mobArmor.cursesEnabled.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.cursesEnabled = v).build());
        mobArmorCurses.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobArmor.curseChance"), cfg.mobArmor.curseChance)
                .setDefaultValue(0.1).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.mobArmor.curseChance.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.curseChance = v).build());
        mobArmorCurses.add(eb.startIntField(Component.translatable("buffmobs.config.mobArmor.curseUnlockDay"), cfg.mobArmor.curseUnlockDay)
                .setDefaultValue(30).setMin(0).setMax(365)
                .setTooltip(tt("buffmobs.config.mobArmor.curseUnlockDay.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.curseUnlockDay = v).build());
        mobArmorCurses.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobArmor.bindingCurseEnabled"), cfg.mobArmor.bindingCurseEnabled)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.mobArmor.bindingCurseEnabled.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.bindingCurseEnabled = v).build());
        mobArmorCurses.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobArmor.vanishingCurseEnabled"), cfg.mobArmor.vanishingCurseEnabled)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.mobArmor.vanishingCurseEnabled.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.vanishingCurseEnabled = v).build());
        mobArmorSub3.add(mobArmorCurses.build());
        mobArmor.addEntry(mobArmorSub3.build());
        mobArmor.addEntry(eb.startStrList(Component.translatable("buffmobs.config.mobArmor.additionalArmorMobs"), new ArrayList<>(cfg.mobArmor.additionalArmorMobs))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.mobArmor.additionalArmorMobs.tooltip"))
                .setSaveConsumer(v -> cfg.mobArmor.additionalArmorMobs = v).build());

        ConfigMenu.Category eliteMobs = builder.getOrCreateCategory(Component.translatable("buffmobs.config.eliteMobs"));
        eliteMobs.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.eliteMobs.enabled"), cfg.eliteMobs.enabled)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.eliteMobs.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.eliteMobs.enabled = v).build());
        eliteMobs.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.eliteMobs.baseChance"), cfg.eliteMobs.baseChance)
                .setDefaultValue(0.02).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.eliteMobs.baseChance.tooltip"))
                .setSaveConsumer(v -> cfg.eliteMobs.baseChance = v).build());
        eliteMobs.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.eliteMobs.chanceIncreasePerDay"), cfg.eliteMobs.chanceIncreasePerDay)
                .setDefaultValue(0.001).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.eliteMobs.chanceIncreasePerDay.tooltip"))
                .setSaveConsumer(v -> cfg.eliteMobs.chanceIncreasePerDay = v).build());
        eliteMobs.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.eliteMobs.maxChance"), cfg.eliteMobs.maxChance)
                .setDefaultValue(0.25).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.eliteMobs.maxChance.tooltip"))
                .setSaveConsumer(v -> cfg.eliteMobs.maxChance = v).build());
        eliteMobs.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.eliteMobs.healthMultiplier"), cfg.eliteMobs.healthMultiplier)
                .setDefaultValue(3.0).setMin(1.0).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.eliteMobs.healthMultiplier.tooltip"))
                .setSaveConsumer(v -> cfg.eliteMobs.healthMultiplier = v).build());
        eliteMobs.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.eliteMobs.damageMultiplier"), cfg.eliteMobs.damageMultiplier)
                .setDefaultValue(2.0).setMin(1.0).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.eliteMobs.damageMultiplier.tooltip"))
                .setSaveConsumer(v -> cfg.eliteMobs.damageMultiplier = v).build());
        eliteMobs.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.eliteMobs.nameColor"),
                        BuffMobsConfig.EliteMobs.NameColor.class,
                        cfg.eliteMobs.nameColor)
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
                .setSaveConsumer(v -> cfg.eliteMobs.nameColor = v).build());
        eliteMobs.addEntry(eb.startStrField(Component.translatable("buffmobs.config.eliteMobs.customNameColor"), cfg.eliteMobs.customNameColor)
                .setDefaultValue("#FFAA00")
                .setTooltip(tt("buffmobs.config.eliteMobs.customNameColor.tooltip"))
                .setSaveConsumer(v -> cfg.eliteMobs.customNameColor = v).build());
        eliteMobs.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.eliteMobs.namePrefix"),
                        BuffMobsConfig.EliteMobs.NamePrefix.class,
                        cfg.eliteMobs.namePrefix)
                .setDefaultValue(BuffMobsConfig.EliteMobs.NamePrefix.RANDOM)
                .setEnumNameProvider(e -> switch ((BuffMobsConfig.EliteMobs.NamePrefix) e) {
                    case elite -> Component.translatable("buffmobs.elite.prefix.elite");
                    case CHAMPION -> Component.translatable("buffmobs.elite.prefix.champion");
                    case BRUTE -> Component.translatable("buffmobs.elite.prefix.brute");
                    case DREAD -> Component.translatable("buffmobs.elite.prefix.dread");
                    case RANDOM -> Component.translatable("buffmobs.config.eliteMobs.namePrefix.random");
                })
                .setTooltip(tt("buffmobs.config.eliteMobs.namePrefix.tooltip"))
                .setSaveConsumer(v -> cfg.eliteMobs.namePrefix = v).build());
        eliteMobs.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.eliteMobs.nameMode"),
                        BuffMobsConfig.EliteMobs.NameMode.class,
                        cfg.eliteMobs.nameMode)
                .setDefaultValue(BuffMobsConfig.EliteMobs.NameMode.GENERATED)
                .setEnumNameProvider(e -> switch ((BuffMobsConfig.EliteMobs.NameMode) e) {
                    case GENERATED -> Component.translatable("buffmobs.config.eliteMobs.nameMode.generated");
                    case PREFIX -> Component.translatable("buffmobs.config.eliteMobs.nameMode.prefix");
                })
                .setTooltip(tt("buffmobs.config.eliteMobs.nameMode.tooltip"))
                .setSaveConsumer(v -> cfg.eliteMobs.nameMode = v).build());
        eliteMobs.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.eliteMobs.useGeneratedPrefix"), cfg.eliteMobs.useGeneratedPrefix)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.eliteMobs.useGeneratedPrefix.tooltip"))
                .setSaveConsumer(v -> cfg.eliteMobs.useGeneratedPrefix = v).build());
        eliteMobs.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.eliteMobs.useGeneratedSuffix"), cfg.eliteMobs.useGeneratedSuffix)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.eliteMobs.useGeneratedSuffix.tooltip"))
                .setSaveConsumer(v -> cfg.eliteMobs.useGeneratedSuffix = v).build());
        eliteMobs.addEntry(eb.startStrList(Component.translatable("buffmobs.config.eliteMobs.generatedNamePrefixes"), new ArrayList<>(cfg.eliteMobs.generatedNamePrefixes))
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
                .setSaveConsumer(v -> cfg.eliteMobs.generatedNamePrefixes = v).build());
        eliteMobs.addEntry(eb.startStrList(Component.translatable("buffmobs.config.eliteMobs.generatedNameSuffixes"), new ArrayList<>(cfg.eliteMobs.generatedNameSuffixes))
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
                .setSaveConsumer(v -> cfg.eliteMobs.generatedNameSuffixes = v).build());
        eliteMobs.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.eliteMobs.particle"),
                        BuffMobsConfig.EliteMobs.EliteParticle.class,
                        cfg.eliteMobs.particle)
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
                .setSaveConsumer(v -> cfg.eliteMobs.particle = v).build());
        eliteMobs.addEntry(eb.startIntField(Component.translatable("buffmobs.config.eliteMobs.particleIntervalTicks"), cfg.eliteMobs.particleIntervalTicks)
                .setDefaultValue(10).setMin(1).setMax(200)
                .setTooltip(tt("buffmobs.config.eliteMobs.particleIntervalTicks.tooltip"))
                .setSaveConsumer(v -> cfg.eliteMobs.particleIntervalTicks = v).build());
        eliteMobs.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.eliteMobs.notifyNearbyPlayers"), cfg.eliteMobs.notifyNearbyPlayers)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.eliteMobs.notifyNearbyPlayers.tooltip"))
                .setSaveConsumer(v -> cfg.eliteMobs.notifyNearbyPlayers = v).build());
        eliteMobs.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.eliteMobs.notifyRadius"), cfg.eliteMobs.notifyRadius)
                .setDefaultValue(48.0).setMin(0.0).setMax(256.0)
                .setTooltip(tt("buffmobs.config.eliteMobs.notifyRadius.tooltip"))
                .setSaveConsumer(v -> cfg.eliteMobs.notifyRadius = v).build());
        eliteMobs.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.eliteMobs.bonusRewardsEnabled"), cfg.eliteMobs.bonusRewardsEnabled)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.eliteMobs.bonusRewardsEnabled.tooltip"))
                .setSaveConsumer(v -> cfg.eliteMobs.bonusRewardsEnabled = v).build());
        eliteMobs.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.eliteMobs.rewardXpMultiplier"), cfg.eliteMobs.rewardXpMultiplier)
                .setDefaultValue(5.0).setMin(1.0).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.eliteMobs.rewardXpMultiplier.tooltip"))
                .setSaveConsumer(v -> cfg.eliteMobs.rewardXpMultiplier = v).build());
        eliteMobs.addEntry(eb.startIntField(Component.translatable("buffmobs.config.eliteMobs.rewardExtraLootRolls"), cfg.eliteMobs.rewardExtraLootRolls)
                .setDefaultValue(2).setMin(0).setMax(20)
                .setTooltip(tt("buffmobs.config.eliteMobs.rewardExtraLootRolls.tooltip"))
                .setSaveConsumer(v -> cfg.eliteMobs.rewardExtraLootRolls = v).build());

        ConfigMenu.Category lootXp = builder.getOrCreateCategory(Component.translatable("buffmobs.config.lootXpScaling"));
        lootXp.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.lootXpScaling.enabled"), cfg.lootXpScaling.enabled)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.lootXpScaling.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.lootXpScaling.enabled = v).build());
        lootXp.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.lootXpScaling.xpScalingEnabled"), cfg.lootXpScaling.xpScalingEnabled)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.lootXpScaling.xpScalingEnabled.tooltip"))
                .setSaveConsumer(v -> cfg.lootXpScaling.xpScalingEnabled = v).build());
        lootXp.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.lootXpScaling.maxXpMultiplier"), cfg.lootXpScaling.maxXpMultiplier)
                .setDefaultValue(5.0).setMin(1.0).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.lootXpScaling.maxXpMultiplier.tooltip"))
                .setSaveConsumer(v -> cfg.lootXpScaling.maxXpMultiplier = v).build());
        lootXp.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.lootXpScaling.extraLootRollsEnabled"), cfg.lootXpScaling.extraLootRollsEnabled)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.lootXpScaling.extraLootRollsEnabled.tooltip"))
                .setSaveConsumer(v -> cfg.lootXpScaling.extraLootRollsEnabled = v).build());
        lootXp.addEntry(eb.startIntField(Component.translatable("buffmobs.config.lootXpScaling.maxExtraLootRolls"), cfg.lootXpScaling.maxExtraLootRolls)
                .setDefaultValue(3).setMin(0).setMax(20)
                .setTooltip(tt("buffmobs.config.lootXpScaling.maxExtraLootRolls.tooltip"))
                .setSaveConsumer(v -> cfg.lootXpScaling.maxExtraLootRolls = v).build());

        ConfigMenu.Category traits = builder.getOrCreateCategory(Component.translatable("buffmobs.config.traits"));
        traits.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.traits.enabled"), cfg.traits.enabled)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.traits.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.traits.enabled = v).build());
        traits.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.traits.chance"), cfg.traits.chance)
                .setDefaultValue(0.15).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.traits.chance.tooltip"))
                .setSaveConsumer(v -> cfg.traits.chance = v).build());
        traits.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.traits.onHitEffectsEnabled"), cfg.traits.onHitEffectsEnabled)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.traits.onHitEffectsEnabled.tooltip"))
                .setSaveConsumer(v -> cfg.traits.onHitEffectsEnabled = v).build());
        traits.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.traits.flameParticle"),
                        BuffMobsConfig.EliteMobs.EliteParticle.class, cfg.traits.flameParticle)
                .setDefaultValue(BuffMobsConfig.EliteMobs.EliteParticle.FLAME)
                .setEnumNameProvider(e -> Component.translatable("buffmobs.config.eliteMobs.particle." + e.name().toLowerCase(java.util.Locale.ROOT)))
                .setTooltip(tt("buffmobs.config.traits.flameParticle.tooltip"))
                .setSaveConsumer(v -> cfg.traits.flameParticle = v).build());
        traits.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.traits.frostParticle"),
                        BuffMobsConfig.EliteMobs.EliteParticle.class, cfg.traits.frostParticle)
                .setDefaultValue(BuffMobsConfig.EliteMobs.EliteParticle.SNOWFLAKE)
                .setEnumNameProvider(e -> Component.translatable("buffmobs.config.eliteMobs.particle." + e.name().toLowerCase(java.util.Locale.ROOT)))
                .setTooltip(tt("buffmobs.config.traits.frostParticle.tooltip"))
                .setSaveConsumer(v -> cfg.traits.frostParticle = v).build());
        traits.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.traits.toxicParticle"),
                        BuffMobsConfig.EliteMobs.EliteParticle.class, cfg.traits.toxicParticle)
                .setDefaultValue(BuffMobsConfig.EliteMobs.EliteParticle.HAPPY_VILLAGER)
                .setEnumNameProvider(e -> Component.translatable("buffmobs.config.eliteMobs.particle." + e.name().toLowerCase(java.util.Locale.ROOT)))
                .setTooltip(tt("buffmobs.config.traits.toxicParticle.tooltip"))
                .setSaveConsumer(v -> cfg.traits.toxicParticle = v).build());
        traits.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.traits.voltaicParticle"),
                        BuffMobsConfig.EliteMobs.EliteParticle.class, cfg.traits.voltaicParticle)
                .setDefaultValue(BuffMobsConfig.EliteMobs.EliteParticle.ELECTRIC_SPARK)
                .setEnumNameProvider(e -> Component.translatable("buffmobs.config.eliteMobs.particle." + e.name().toLowerCase(java.util.Locale.ROOT)))
                .setTooltip(tt("buffmobs.config.traits.voltaicParticle.tooltip"))
                .setSaveConsumer(v -> cfg.traits.voltaicParticle = v).build());
        traits.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.traits.vampiricParticle"),
                        BuffMobsConfig.EliteMobs.EliteParticle.class, cfg.traits.vampiricParticle)
                .setDefaultValue(BuffMobsConfig.EliteMobs.EliteParticle.HEART)
                .setEnumNameProvider(e -> Component.translatable("buffmobs.config.eliteMobs.particle." + e.name().toLowerCase(java.util.Locale.ROOT)))
                .setTooltip(tt("buffmobs.config.traits.vampiricParticle.tooltip"))
                .setSaveConsumer(v -> cfg.traits.vampiricParticle = v).build());
        traits.addEntry(eb.startEnumSelector(Component.translatable("buffmobs.config.traits.armoredParticle"),
                        BuffMobsConfig.EliteMobs.EliteParticle.class, cfg.traits.armoredParticle)
                .setDefaultValue(BuffMobsConfig.EliteMobs.EliteParticle.NONE)
                .setEnumNameProvider(e -> Component.translatable("buffmobs.config.eliteMobs.particle." + e.name().toLowerCase(java.util.Locale.ROOT)))
                .setTooltip(tt("buffmobs.config.traits.armoredParticle.tooltip"))
                .setSaveConsumer(v -> cfg.traits.armoredParticle = v).build());
        var traitsSub1 = eb.startSubCategory(Component.translatable("buffmobs.config.traits.weights"));
        traitsSub1.add(eb.startIntField(Component.translatable("buffmobs.config.traits.flameWeight"), cfg.traits.flameWeight)
                .setDefaultValue(20).setMin(0).setMax(1000)
                .setTooltip(tt("buffmobs.config.traits.flameWeight.tooltip"))
                .setSaveConsumer(v -> cfg.traits.flameWeight = v).build());
        traitsSub1.add(eb.startIntField(Component.translatable("buffmobs.config.traits.frostWeight"), cfg.traits.frostWeight)
                .setDefaultValue(20).setMin(0).setMax(1000)
                .setTooltip(tt("buffmobs.config.traits.frostWeight.tooltip"))
                .setSaveConsumer(v -> cfg.traits.frostWeight = v).build());
        traitsSub1.add(eb.startIntField(Component.translatable("buffmobs.config.traits.toxicWeight"), cfg.traits.toxicWeight)
                .setDefaultValue(20).setMin(0).setMax(1000)
                .setTooltip(tt("buffmobs.config.traits.toxicWeight.tooltip"))
                .setSaveConsumer(v -> cfg.traits.toxicWeight = v).build());
        traitsSub1.add(eb.startIntField(Component.translatable("buffmobs.config.traits.voltaicWeight"), cfg.traits.voltaicWeight)
                .setDefaultValue(15).setMin(0).setMax(1000)
                .setTooltip(tt("buffmobs.config.traits.voltaicWeight.tooltip"))
                .setSaveConsumer(v -> cfg.traits.voltaicWeight = v).build());
        traitsSub1.add(eb.startIntField(Component.translatable("buffmobs.config.traits.vampiricWeight"), cfg.traits.vampiricWeight)
                .setDefaultValue(15).setMin(0).setMax(1000)
                .setTooltip(tt("buffmobs.config.traits.vampiricWeight.tooltip"))
                .setSaveConsumer(v -> cfg.traits.vampiricWeight = v).build());
        traitsSub1.add(eb.startIntField(Component.translatable("buffmobs.config.traits.armoredWeight"), cfg.traits.armoredWeight)
                .setDefaultValue(10).setMin(0).setMax(1000)
                .setTooltip(tt("buffmobs.config.traits.armoredWeight.tooltip"))
                .setSaveConsumer(v -> cfg.traits.armoredWeight = v).build());
        traits.addEntry(traitsSub1.build());
        var traitsSub2 = eb.startSubCategory(Component.translatable("buffmobs.config.traits.traitSettings"));
        traitsSub2.add(eb.startIntField(Component.translatable("buffmobs.config.traits.flameFireSeconds"), cfg.traits.flameFireSeconds)
                .setDefaultValue(4).setMin(1).setMax(60)
                .setTooltip(tt("buffmobs.config.traits.flameFireSeconds.tooltip"))
                .setSaveConsumer(v -> cfg.traits.flameFireSeconds = v).build());
        traitsSub2.add(eb.startIntField(Component.translatable("buffmobs.config.traits.frostSlownessSeconds"), cfg.traits.frostSlownessSeconds)
                .setDefaultValue(3).setMin(1).setMax(60)
                .setTooltip(tt("buffmobs.config.traits.frostSlownessSeconds.tooltip"))
                .setSaveConsumer(v -> cfg.traits.frostSlownessSeconds = v).build());
        traitsSub2.add(eb.startIntField(Component.translatable("buffmobs.config.traits.frostSlownessLevel"), cfg.traits.frostSlownessLevel)
                .setDefaultValue(2).setMin(1).setMax(10)
                .setTooltip(tt("buffmobs.config.traits.frostSlownessLevel.tooltip"))
                .setSaveConsumer(v -> cfg.traits.frostSlownessLevel = v).build());
        traitsSub2.add(eb.startIntField(Component.translatable("buffmobs.config.traits.toxicPoisonSeconds"), cfg.traits.toxicPoisonSeconds)
                .setDefaultValue(5).setMin(1).setMax(60)
                .setTooltip(tt("buffmobs.config.traits.toxicPoisonSeconds.tooltip"))
                .setSaveConsumer(v -> cfg.traits.toxicPoisonSeconds = v).build());
        traitsSub2.add(eb.startIntField(Component.translatable("buffmobs.config.traits.toxicPoisonLevel"), cfg.traits.toxicPoisonLevel)
                .setDefaultValue(1).setMin(1).setMax(10)
                .setTooltip(tt("buffmobs.config.traits.toxicPoisonLevel.tooltip"))
                .setSaveConsumer(v -> cfg.traits.toxicPoisonLevel = v).build());
        traitsSub2.add(eb.startDoubleField(Component.translatable("buffmobs.config.traits.voltaicChainChance"), cfg.traits.voltaicChainChance)
                .setDefaultValue(0.35).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.traits.voltaicChainChance.tooltip"))
                .setSaveConsumer(v -> cfg.traits.voltaicChainChance = v).build());
        traitsSub2.add(eb.startDoubleField(Component.translatable("buffmobs.config.traits.voltaicChainDamage"), cfg.traits.voltaicChainDamage)
                .setDefaultValue(4.0).setMin(0.0).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.traits.voltaicChainDamage.tooltip"))
                .setSaveConsumer(v -> cfg.traits.voltaicChainDamage = v).build());
        traitsSub2.add(eb.startDoubleField(Component.translatable("buffmobs.config.traits.voltaicChainRadius"), cfg.traits.voltaicChainRadius)
                .setDefaultValue(4.0).setMin(0.0).setMax(32.0)
                .setTooltip(tt("buffmobs.config.traits.voltaicChainRadius.tooltip"))
                .setSaveConsumer(v -> cfg.traits.voltaicChainRadius = v).build());
        traitsSub2.add(eb.startIntField(Component.translatable("buffmobs.config.traits.voltaicMaxTargets"), cfg.traits.voltaicMaxTargets)
                .setDefaultValue(3).setMin(1).setMax(20)
                .setTooltip(tt("buffmobs.config.traits.voltaicMaxTargets.tooltip"))
                .setSaveConsumer(v -> cfg.traits.voltaicMaxTargets = v).build());
        traitsSub2.add(eb.startDoubleField(Component.translatable("buffmobs.config.traits.vampiricHealPercent"), cfg.traits.vampiricHealPercent)
                .setDefaultValue(0.25).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.traits.vampiricHealPercent.tooltip"))
                .setSaveConsumer(v -> cfg.traits.vampiricHealPercent = v).build());
        traitsSub2.add(eb.startDoubleField(Component.translatable("buffmobs.config.traits.armoredArmorBonus"), cfg.traits.armoredArmorBonus)
                .setDefaultValue(8.0).setMin(0.0).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.traits.armoredArmorBonus.tooltip"))
                .setSaveConsumer(v -> cfg.traits.armoredArmorBonus = v).build());
        traitsSub2.add(eb.startDoubleField(Component.translatable("buffmobs.config.traits.armoredToughnessBonus"), cfg.traits.armoredToughnessBonus)
                .setDefaultValue(4.0).setMin(0.0).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.traits.armoredToughnessBonus.tooltip"))
                .setSaveConsumer(v -> cfg.traits.armoredToughnessBonus = v).build());
        traits.addEntry(traitsSub2.build());

        ConfigMenu.Category packAggression = builder.getOrCreateCategory(Component.translatable("buffmobs.config.packAggression"));
        packAggression.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.packAggression.enabled"), cfg.packAggression.enabled)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.packAggression.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.packAggression.enabled = v).build());
        packAggression.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.packAggression.radius"), cfg.packAggression.radius)
                .setDefaultValue(16.0).setMin(1.0).setMax(64.0)
                .setTooltip(tt("buffmobs.config.packAggression.radius.tooltip"))
                .setSaveConsumer(v -> cfg.packAggression.radius = v).build());
        packAggression.addEntry(eb.startIntField(Component.translatable("buffmobs.config.packAggression.maxPackSize"), cfg.packAggression.maxPackSize)
                .setDefaultValue(5).setMin(1).setMax(50)
                .setTooltip(tt("buffmobs.config.packAggression.maxPackSize.tooltip"))
                .setSaveConsumer(v -> cfg.packAggression.maxPackSize = v).build());
        packAggression.addEntry(eb.startIntField(Component.translatable("buffmobs.config.packAggression.cooldownTicks"), cfg.packAggression.cooldownTicks)
                .setDefaultValue(200).setMin(0).setMax(72000)
                .setTooltip(tt("buffmobs.config.packAggression.cooldownTicks.tooltip"))
                .setSaveConsumer(v -> cfg.packAggression.cooldownTicks = v).build());

        ConfigMenu.Category specialAbilities = builder.getOrCreateCategory(Component.translatable("buffmobs.config.specialAbilities"));
        specialAbilities.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.specialAbilities.enabled"), cfg.specialAbilities.enabled)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.specialAbilities.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.specialAbilities.enabled = v).build());
        specialAbilities.addEntry(eb.startStrList(Component.translatable("buffmobs.config.specialAbilities.mobAbilities"), new ArrayList<>(cfg.specialAbilities.mobAbilities))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("buffmobs.config.specialAbilities.mobAbilities.tooltip"))
                .setSaveConsumer(v -> cfg.specialAbilities.mobAbilities = v).build());
        var specialAbilitiesSub1 = eb.startSubCategory(Component.translatable("buffmobs.config.specialAbilities.deathrattle"));
        specialAbilitiesSub1.add(eb.startEnumSelector(Component.translatable("buffmobs.config.specialAbilities.deathrattle.mode"),
                        BuffMobsConfig.SpecialAbilities.Deathrattle.DeathrattleMode.class,
                        cfg.specialAbilities.deathrattle.mode)
                .setDefaultValue(BuffMobsConfig.SpecialAbilities.Deathrattle.DeathrattleMode.POISON_CLOUD)
                .setEnumNameProvider(e -> switch ((BuffMobsConfig.SpecialAbilities.Deathrattle.DeathrattleMode) e) {
                    case EXPLOSION -> Component.translatable("buffmobs.config.specialAbilities.deathrattle.mode.explosion");
                    case POISON_CLOUD -> Component.translatable("buffmobs.config.specialAbilities.deathrattle.mode.poison_cloud");
                    case Summon_MINIONS -> Component.translatable("buffmobs.config.specialAbilities.deathrattle.mode.summon_minions");
                    case RANDOM -> Component.translatable("buffmobs.config.specialAbilities.deathrattle.mode.random");
                })
                .setTooltip(tt("buffmobs.config.specialAbilities.deathrattle.mode.tooltip"))
                .setSaveConsumer(v -> cfg.specialAbilities.deathrattle.mode = v).build());
        specialAbilitiesSub1.add(eb.startDoubleField(Component.translatable("buffmobs.config.specialAbilities.deathrattle.explosionPower"), cfg.specialAbilities.deathrattle.explosionPower)
                .setDefaultValue(1.5).setMin(0.0).setMax(20.0)
                .setTooltip(tt("buffmobs.config.specialAbilities.deathrattle.explosionPower.tooltip"))
                .setSaveConsumer(v -> cfg.specialAbilities.deathrattle.explosionPower = v).build());
        specialAbilitiesSub1.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.specialAbilities.deathrattle.explosionBreaksBlocks"), cfg.specialAbilities.deathrattle.explosionBreaksBlocks)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.specialAbilities.deathrattle.explosionBreaksBlocks.tooltip"))
                .setSaveConsumer(v -> cfg.specialAbilities.deathrattle.explosionBreaksBlocks = v).build());
        specialAbilitiesSub1.add(eb.startDoubleField(Component.translatable("buffmobs.config.specialAbilities.deathrattle.poisonCloudRadius"), cfg.specialAbilities.deathrattle.poisonCloudRadius)
                .setDefaultValue(2.5).setMin(0.5).setMax(16.0)
                .setTooltip(tt("buffmobs.config.specialAbilities.deathrattle.poisonCloudRadius.tooltip"))
                .setSaveConsumer(v -> cfg.specialAbilities.deathrattle.poisonCloudRadius = v).build());
        specialAbilitiesSub1.add(eb.startIntField(Component.translatable("buffmobs.config.specialAbilities.deathrattle.poisonCloudDurationSeconds"), cfg.specialAbilities.deathrattle.poisonCloudDurationSeconds)
                .setDefaultValue(5).setMin(1).setMax(60)
                .setTooltip(tt("buffmobs.config.specialAbilities.deathrattle.poisonCloudDurationSeconds.tooltip"))
                .setSaveConsumer(v -> cfg.specialAbilities.deathrattle.poisonCloudDurationSeconds = v).build());
        specialAbilitiesSub1.add(eb.startIntField(Component.translatable("buffmobs.config.specialAbilities.deathrattle.poisonCloudLevel"), cfg.specialAbilities.deathrattle.poisonCloudLevel)
                .setDefaultValue(1).setMin(1).setMax(10)
                .setTooltip(tt("buffmobs.config.specialAbilities.deathrattle.poisonCloudLevel.tooltip"))
                .setSaveConsumer(v -> cfg.specialAbilities.deathrattle.poisonCloudLevel = v).build());
        specialAbilitiesSub1.add(eb.startStrField(Component.translatable("buffmobs.config.specialAbilities.deathrattle.minionEntityId"), cfg.specialAbilities.deathrattle.minionEntityId)
                .setDefaultValue("minecraft:slime")
                .setTooltip(tt("buffmobs.config.specialAbilities.deathrattle.minionEntityId.tooltip"))
                .setSaveConsumer(v -> cfg.specialAbilities.deathrattle.minionEntityId = v).build());
        specialAbilitiesSub1.add(eb.startIntField(Component.translatable("buffmobs.config.specialAbilities.deathrattle.minionMinCount"), cfg.specialAbilities.deathrattle.minionMinCount)
                .setDefaultValue(1).setMin(0).setMax(10)
                .setTooltip(tt("buffmobs.config.specialAbilities.deathrattle.minionMinCount.tooltip"))
                .setSaveConsumer(v -> cfg.specialAbilities.deathrattle.minionMinCount = v).build());
        specialAbilitiesSub1.add(eb.startIntField(Component.translatable("buffmobs.config.specialAbilities.deathrattle.minionMaxCount"), cfg.specialAbilities.deathrattle.minionMaxCount)
                .setDefaultValue(2).setMin(0).setMax(10)
                .setTooltip(tt("buffmobs.config.specialAbilities.deathrattle.minionMaxCount.tooltip"))
                .setSaveConsumer(v -> cfg.specialAbilities.deathrattle.minionMaxCount = v).build());
        specialAbilities.addEntry(specialAbilitiesSub1.build());
        var specialAbilitiesSub2 = eb.startSubCategory(Component.translatable("buffmobs.config.specialAbilities.lastStand"));
        specialAbilitiesSub2.add(eb.startDoubleField(Component.translatable("buffmobs.config.specialAbilities.lastStand.healthThreshold"), cfg.specialAbilities.lastStand.healthThreshold)
                .setDefaultValue(0.20).setMin(0.01).setMax(0.99)
                .setTooltip(tt("buffmobs.config.specialAbilities.lastStand.healthThreshold.tooltip"))
                .setSaveConsumer(v -> cfg.specialAbilities.lastStand.healthThreshold = v).build());
        specialAbilitiesSub2.add(eb.startIntField(Component.translatable("buffmobs.config.specialAbilities.lastStand.resistanceLevel"), cfg.specialAbilities.lastStand.resistanceLevel)
                .setDefaultValue(2).setMin(1).setMax(10)
                .setTooltip(tt("buffmobs.config.specialAbilities.lastStand.resistanceLevel.tooltip"))
                .setSaveConsumer(v -> cfg.specialAbilities.lastStand.resistanceLevel = v).build());
        specialAbilitiesSub2.add(eb.startIntField(Component.translatable("buffmobs.config.specialAbilities.lastStand.resistanceDurationSeconds"), cfg.specialAbilities.lastStand.resistanceDurationSeconds)
                .setDefaultValue(5).setMin(1).setMax(60)
                .setTooltip(tt("buffmobs.config.specialAbilities.lastStand.resistanceDurationSeconds.tooltip"))
                .setSaveConsumer(v -> cfg.specialAbilities.lastStand.resistanceDurationSeconds = v).build());
        specialAbilities.addEntry(specialAbilitiesSub2.build());
        var specialAbilitiesSub3 = eb.startSubCategory(Component.translatable("buffmobs.config.specialAbilities.Teleport"));
        specialAbilitiesSub3.add(eb.startDoubleField(Component.translatable("buffmobs.config.specialAbilities.Teleport.chance"), cfg.specialAbilities.Teleport.chance)
                .setDefaultValue(0.25).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.specialAbilities.Teleport.chance.tooltip"))
                .setSaveConsumer(v -> cfg.specialAbilities.Teleport.chance = v).build());
        specialAbilitiesSub3.add(eb.startIntField(Component.translatable("buffmobs.config.specialAbilities.Teleport.cooldownTicks"), cfg.specialAbilities.Teleport.cooldownTicks)
                .setDefaultValue(100).setMin(0).setMax(72000)
                .setTooltip(tt("buffmobs.config.specialAbilities.Teleport.cooldownTicks.tooltip"))
                .setSaveConsumer(v -> cfg.specialAbilities.Teleport.cooldownTicks = v).build());
        specialAbilitiesSub3.add(eb.startDoubleField(Component.translatable("buffmobs.config.specialAbilities.Teleport.distanceBehindAttacker"), cfg.specialAbilities.Teleport.distanceBehindAttacker)
                .setDefaultValue(2.0).setMin(0.5).setMax(16.0)
                .setTooltip(tt("buffmobs.config.specialAbilities.Teleport.distanceBehindAttacker.tooltip"))
                .setSaveConsumer(v -> cfg.specialAbilities.Teleport.distanceBehindAttacker = v).build());
        specialAbilities.addEntry(specialAbilitiesSub3.build());
        var specialAbilitiesSub4 = eb.startSubCategory(Component.translatable("buffmobs.config.specialAbilities.Summon"));
        specialAbilitiesSub4.add(eb.startIntField(Component.translatable("buffmobs.config.specialAbilities.Summon.intervalTicks"), cfg.specialAbilities.Summon.intervalTicks)
                .setDefaultValue(200).setMin(20).setMax(72000)
                .setTooltip(tt("buffmobs.config.specialAbilities.Summon.intervalTicks.tooltip"))
                .setSaveConsumer(v -> cfg.specialAbilities.Summon.intervalTicks = v).build());
        specialAbilitiesSub4.add(eb.startStrField(Component.translatable("buffmobs.config.specialAbilities.Summon.minionEntityId"), cfg.specialAbilities.Summon.minionEntityId)
                .setDefaultValue("minecraft:zombie")
                .setTooltip(tt("buffmobs.config.specialAbilities.Summon.minionEntityId.tooltip"))
                .setSaveConsumer(v -> cfg.specialAbilities.Summon.minionEntityId = v).build());
        specialAbilitiesSub4.add(eb.startIntField(Component.translatable("buffmobs.config.specialAbilities.Summon.maxAliveMinions"), cfg.specialAbilities.Summon.maxAliveMinions)
                .setDefaultValue(3).setMin(0).setMax(20)
                .setTooltip(tt("buffmobs.config.specialAbilities.Summon.maxAliveMinions.tooltip"))
                .setSaveConsumer(v -> cfg.specialAbilities.Summon.maxAliveMinions = v).build());
        specialAbilitiesSub4.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.specialAbilities.Summon.onlyInCombat"), cfg.specialAbilities.Summon.onlyInCombat)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.specialAbilities.Summon.onlyInCombat.tooltip"))
                .setSaveConsumer(v -> cfg.specialAbilities.Summon.onlyInCombat = v).build());
        specialAbilities.addEntry(specialAbilitiesSub4.build());

        ConfigMenu.Category bloodMoon = builder.getOrCreateCategory(Component.translatable("buffmobs.config.bloodMoon"));
        bloodMoon.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.bloodMoon.enabled"), cfg.bloodMoon.enabled)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.bloodMoon.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.bloodMoon.enabled = v).build());
        bloodMoon.addEntry(eb.startIntField(Component.translatable("buffmobs.config.bloodMoon.intervalDays"), cfg.bloodMoon.intervalDays)
                .setDefaultValue(7).setMin(1).setMax(365)
                .setTooltip(tt("buffmobs.config.bloodMoon.intervalDays.tooltip"))
                .setSaveConsumer(v -> cfg.bloodMoon.intervalDays = v).build());
        bloodMoon.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.bloodMoon.extraMultiplier"), cfg.bloodMoon.extraMultiplier)
                .setDefaultValue(1.5).setMin(1.0).setMax(999999.0)
                .setTooltip(tt("buffmobs.config.bloodMoon.extraMultiplier.tooltip"))
                .setSaveConsumer(v -> cfg.bloodMoon.extraMultiplier = v).build());
        bloodMoon.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.bloodMoon.glowingMobs"), cfg.bloodMoon.glowingMobs)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.bloodMoon.glowingMobs.tooltip"))
                .setSaveConsumer(v -> cfg.bloodMoon.glowingMobs = v).build());
        bloodMoon.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.bloodMoon.doubleLootAndXp"), cfg.bloodMoon.doubleLootAndXp)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.bloodMoon.doubleLootAndXp.tooltip"))
                .setSaveConsumer(v -> cfg.bloodMoon.doubleLootAndXp = v).build());
        bloodMoon.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.bloodMoon.notifyPlayers"), cfg.bloodMoon.notifyPlayers)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.bloodMoon.notifyPlayers.tooltip"))
                .setSaveConsumer(v -> cfg.bloodMoon.notifyPlayers = v).build());
        bloodMoon.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.bloodMoon.spawnBoostEnabled"), cfg.bloodMoon.spawnBoostEnabled)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.bloodMoon.spawnBoostEnabled.tooltip"))
                .setSaveConsumer(v -> cfg.bloodMoon.spawnBoostEnabled = v).build());
        bloodMoon.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.bloodMoon.extraSpawnChance"), cfg.bloodMoon.extraSpawnChance)
                .setDefaultValue(0.25).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.bloodMoon.extraSpawnChance.tooltip"))
                .setSaveConsumer(v -> cfg.bloodMoon.extraSpawnChance = v).build());

        ConfigMenu.Category adaptive = builder.getOrCreateCategory(Component.translatable("buffmobs.config.adaptiveDifficulty"));
        adaptive.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.adaptiveDifficulty.enabled"), cfg.adaptiveDifficulty.enabled)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.adaptiveDifficulty.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.adaptiveDifficulty.enabled = v).build());
        adaptive.addEntry(eb.startIntField(Component.translatable("buffmobs.config.adaptiveDifficulty.deathsPerStep"), cfg.adaptiveDifficulty.deathsPerStep)
                .setDefaultValue(3).setMin(1).setMax(100)
                .setTooltip(tt("buffmobs.config.adaptiveDifficulty.deathsPerStep.tooltip"))
                .setSaveConsumer(v -> cfg.adaptiveDifficulty.deathsPerStep = v).build());
        adaptive.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.adaptiveDifficulty.bonusPerStep"), cfg.adaptiveDifficulty.bonusPerStep)
                .setDefaultValue(0.1).setMin(0.0).setMax(10.0)
                .setTooltip(tt("buffmobs.config.adaptiveDifficulty.bonusPerStep.tooltip"))
                .setSaveConsumer(v -> cfg.adaptiveDifficulty.bonusPerStep = v).build());
        adaptive.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.adaptiveDifficulty.maxBonus"), cfg.adaptiveDifficulty.maxBonus)
                .setDefaultValue(2.0).setMin(0.0).setMax(100.0)
                .setTooltip(tt("buffmobs.config.adaptiveDifficulty.maxBonus.tooltip"))
                .setSaveConsumer(v -> cfg.adaptiveDifficulty.maxBonus = v).build());
        adaptive.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.adaptiveDifficulty.killReductionEnabled"), cfg.adaptiveDifficulty.killReductionEnabled)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.adaptiveDifficulty.killReductionEnabled.tooltip"))
                .setSaveConsumer(v -> cfg.adaptiveDifficulty.killReductionEnabled = v).build());
        adaptive.addEntry(eb.startIntField(Component.translatable("buffmobs.config.adaptiveDifficulty.killsPerReductionStep"), cfg.adaptiveDifficulty.killsPerReductionStep)
                .setDefaultValue(25).setMin(1).setMax(1000)
                .setTooltip(tt("buffmobs.config.adaptiveDifficulty.killsPerReductionStep.tooltip"))
                .setSaveConsumer(v -> cfg.adaptiveDifficulty.killsPerReductionStep = v).build());
        adaptive.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.adaptiveDifficulty.reductionPerStep"), cfg.adaptiveDifficulty.reductionPerStep)
                .setDefaultValue(0.05).setMin(0.0).setMax(10.0)
                .setTooltip(tt("buffmobs.config.adaptiveDifficulty.reductionPerStep.tooltip"))
                .setSaveConsumer(v -> cfg.adaptiveDifficulty.reductionPerStep = v).build());

        return builder.build();
    }

}
