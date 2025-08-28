package com.khotyz.buffmobs.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class ConfigScreen {

    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("BuffMobs Configuration"))
                .setSavingRunnable(BuffMobsConfig::save);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // General Category
        ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));

        general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enabled"), BuffMobsConfig.isEnabled())
                .setDefaultValue(true)
                .setTooltip(Text.literal("Enable/disable the entire mod"))
                .setSaveConsumer(BuffMobsConfig::setEnabled)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Visual Effects"), BuffMobsConfig.showVisualEffects())
                .setDefaultValue(true)
                .setTooltip(Text.literal("Show visual potion effects on buffed mobs"))
                .setSaveConsumer(BuffMobsConfig::setVisualEffects)
                .build());

        // Ranged Equipment Category
        ConfigCategory rangedEquipment = builder.getOrCreateCategory(Text.literal("Ranged Equipment"));

        rangedEquipment.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enabled"), BuffMobsConfig.isRangedEquipmentEnabled())
                .setDefaultValue(true)
                .setTooltip(Text.literal("Enable ranged mobs to equip melee weapons when players get close"))
                .setSaveConsumer(BuffMobsConfig::setRangedEquipmentEnabled)
                .build());

        rangedEquipment.addEntry(entryBuilder.startDoubleField(Text.literal("Trigger Distance"), BuffMobsConfig.getRangedEquipmentDistance())
                .setDefaultValue(8.0)
                .setMin(2.0).setMax(32.0)
                .setTooltip(Text.literal("Distance at which ranged mobs will equip melee weapons"))
                .setSaveConsumer(BuffMobsConfig::setRangedEquipmentDistance)
                .build());

        rangedEquipment.addEntry(entryBuilder.startDoubleField(Text.literal("Netherite Chance"), BuffMobsConfig.getRangedNetheriteChance())
                .setDefaultValue(0.01)
                .setMin(0.001).setMax(0.1)
                .setTooltip(Text.literal("Base chance multiplier for netherite weapons (ultra rare)"))
                .setSaveConsumer(BuffMobsConfig::setRangedNetheriteChance)
                .build());

        rangedEquipment.addEntry(entryBuilder.startDoubleField(Text.literal("Diamond Chance"), BuffMobsConfig.getRangedDiamondChance())
                .setDefaultValue(0.05)
                .setMin(0.01).setMax(0.2)
                .setTooltip(Text.literal("Base chance multiplier for diamond weapons (Overworld only)"))
                .setSaveConsumer(BuffMobsConfig::setRangedDiamondChance)
                .build());

        rangedEquipment.addEntry(entryBuilder.startDoubleField(Text.literal("Iron Chance"), BuffMobsConfig.getRangedIronChance())
                .setDefaultValue(0.15)
                .setMin(0.05).setMax(0.4)
                .setTooltip(Text.literal("Base chance multiplier for iron weapons (Overworld only)"))
                .setSaveConsumer(BuffMobsConfig::setRangedIronChance)
                .build());

        rangedEquipment.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enchantments Enabled"), BuffMobsConfig.isRangedEnchantmentsEnabled())
                .setDefaultValue(true)
                .setTooltip(Text.literal("Enable enchantments on melee weapons for ranged mobs"))
                .setSaveConsumer(BuffMobsConfig::setRangedEnchantmentsEnabled)
                .build());

        rangedEquipment.addEntry(entryBuilder.startDoubleField(Text.literal("Enchantment Chance"), BuffMobsConfig.getRangedEnchantmentChance())
                .setDefaultValue(0.25)
                .setMin(0.0).setMax(1.0)
                .setTooltip(Text.literal("Base chance for weapons to be enchanted"))
                .setSaveConsumer(BuffMobsConfig::setRangedEnchantmentChance)
                .build());

        // Day Scaling Category
        ConfigCategory dayScaling = builder.getOrCreateCategory(Text.literal("Day Scaling"));

        dayScaling.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enabled"), BuffMobsConfig.isDayScalingEnabled())
                .setDefaultValue(false)
                .setTooltip(Text.literal("Enable scaling based on world days"))
                .setSaveConsumer(BuffMobsConfig::setDayScalingEnabled)
                .build());

        dayScaling.addEntry(entryBuilder.startIntField(Text.literal("Interval"), BuffMobsConfig.getDayScalingInterval())
                .setDefaultValue(7)
                .setMin(1).setMax(365)
                .setTooltip(Text.literal("Days interval for scaling increase"))
                .setSaveConsumer(BuffMobsConfig::setDayScalingInterval)
                .build());

        dayScaling.addEntry(entryBuilder.startDoubleField(Text.literal("Multiplier"), BuffMobsConfig.getDayScalingMultiplier())
                .setDefaultValue(0.1)
                .setMin(0.01).setMax(2.0)
                .setTooltip(Text.literal("Multiplier increase per interval"))
                .setSaveConsumer(BuffMobsConfig::setDayScalingMultiplier)
                .build());

        dayScaling.addEntry(entryBuilder.startDoubleField(Text.literal("Max Multiplier"), BuffMobsConfig.getDayScalingMax())
                .setDefaultValue(5.0)
                .setMin(1.0).setMax(10.0)
                .setTooltip(Text.literal("Maximum scaling multiplier"))
                .setSaveConsumer(BuffMobsConfig::setDayScalingMax)
                .build());

        dayScaling.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show Notifications"), BuffMobsConfig.showDayScalingNotifications())
                .setDefaultValue(true)
                .setTooltip(Text.literal("Show day scaling notifications in chat"))
                .setSaveConsumer(BuffMobsConfig::setShowDayScalingNotifications)
                .build());

        dayScaling.addEntry(entryBuilder.startEnumSelector(Text.literal("Notification Mode"),
                        BuffMobsConfig.NotificationMode.class, BuffMobsConfig.getDayScalingNotificationMode())
                .setDefaultValue(BuffMobsConfig.NotificationMode.EVERY_DAY)
                .setTooltip(Text.literal("When to show day scaling notifications"))
                .setSaveConsumer(BuffMobsConfig::setDayScalingNotificationMode)
                .build());

        // Attributes Category
        ConfigCategory attributes = builder.getOrCreateCategory(Text.literal("Attributes"));

        attributes.addEntry(entryBuilder.startDoubleField(Text.literal("Health Multiplier"), BuffMobsConfig.getHealthMultiplier())
                .setDefaultValue(1.0)
                .setMin(1.0).setMax(10.0)
                .setTooltip(Text.literal("Health multiplier (1.0 = default/disabled)"))
                .setSaveConsumer(BuffMobsConfig::setHealthMultiplier)
                .build());

        attributes.addEntry(entryBuilder.startDoubleField(Text.literal("Damage Multiplier"), BuffMobsConfig.getDamageMultiplier())
                .setDefaultValue(1.0)
                .setMin(1.0).setMax(10.0)
                .setTooltip(Text.literal("Attack damage multiplier (1.0 = default/disabled)"))
                .setSaveConsumer(BuffMobsConfig::setDamageMultiplier)
                .build());

        attributes.addEntry(entryBuilder.startDoubleField(Text.literal("Speed Multiplier"), BuffMobsConfig.getSpeedMultiplier())
                .setDefaultValue(1.0)
                .setMin(1.0).setMax(5.0)
                .setTooltip(Text.literal("Movement speed multiplier (1.0 = default/disabled)"))
                .setSaveConsumer(BuffMobsConfig::setSpeedMultiplier)
                .build());

        attributes.addEntry(entryBuilder.startDoubleField(Text.literal("Attack Speed Multiplier"), BuffMobsConfig.getAttackSpeedMultiplier())
                .setDefaultValue(1.0)
                .setMin(1.0).setMax(10.0)
                .setTooltip(Text.literal("Attack speed multiplier (1.0 = default/disabled)"))
                .setSaveConsumer(BuffMobsConfig::setAttackSpeedMultiplier)
                .build());

        attributes.addEntry(entryBuilder.startBooleanToggle(Text.literal("Override Attack Timers"), BuffMobsConfig.shouldOverrideAttackTimers())
                .setDefaultValue(false)
                .setTooltip(Text.literal("Override internal attack timers to allow true attack speed scaling"))
                .setSaveConsumer(BuffMobsConfig::setOverrideAttackTimers)
                .build());

        attributes.addEntry(entryBuilder.startDoubleField(Text.literal("Armor Addition"), BuffMobsConfig.getArmorAddition())
                .setDefaultValue(0.0)
                .setMin(0.0).setMax(20.0)
                .setTooltip(Text.literal("Additional armor points (0 = disabled)"))
                .setSaveConsumer(BuffMobsConfig::setArmorAddition)
                .build());

        attributes.addEntry(entryBuilder.startDoubleField(Text.literal("Armor Toughness Addition"), BuffMobsConfig.getArmorToughnessAddition())
                .setDefaultValue(0.0)
                .setMin(0.0).setMax(10.0)
                .setTooltip(Text.literal("Additional armor toughness (0 = disabled)"))
                .setSaveConsumer(BuffMobsConfig::setArmorToughnessAddition)
                .build());

        // Effects Category
        ConfigCategory effects = builder.getOrCreateCategory(Text.literal("Effects"));

        effects.addEntry(entryBuilder.startIntField(Text.literal("Duration"), BuffMobsConfig.getEffectDuration())
                .setDefaultValue(-1)
                .setMin(-1).setMax(7200)
                .setTooltip(Text.literal("Effect duration in seconds (-1 for infinite)"))
                .setSaveConsumer(BuffMobsConfig::setEffectDuration)
                .build());

        effects.addEntry(entryBuilder.startIntField(Text.literal("Strength Amplifier"), BuffMobsConfig.getStrengthAmplifier())
                .setDefaultValue(0)
                .setMin(0).setMax(10)
                .setTooltip(Text.literal("Strength effect amplifier (0 = disabled)"))
                .setSaveConsumer(BuffMobsConfig::setStrengthAmplifier)
                .build());

        effects.addEntry(entryBuilder.startIntField(Text.literal("Speed Amplifier"), BuffMobsConfig.getSpeedAmplifier())
                .setDefaultValue(0)
                .setMin(0).setMax(10)
                .setTooltip(Text.literal("Speed effect amplifier (0 = disabled)"))
                .setSaveConsumer(BuffMobsConfig::setSpeedAmplifier)
                .build());

        effects.addEntry(entryBuilder.startIntField(Text.literal("Resistance Amplifier"), BuffMobsConfig.getResistanceAmplifier())
                .setDefaultValue(0)
                .setMin(0).setMax(10)
                .setTooltip(Text.literal("Resistance effect amplifier (0 = disabled)"))
                .setSaveConsumer(BuffMobsConfig::setResistanceAmplifier)
                .build());

        effects.addEntry(entryBuilder.startIntField(Text.literal("Regeneration Amplifier"), BuffMobsConfig.getRegenerationAmplifier())
                .setDefaultValue(0)
                .setMin(0).setMax(10)
                .setTooltip(Text.literal("Regeneration effect amplifier (0 = disabled)"))
                .setSaveConsumer(BuffMobsConfig::setRegenerationAmplifier)
                .build());

        // Harmful Effects Category
        ConfigCategory harmfulEffects = builder.getOrCreateCategory(Text.literal("Harmful Effects"));

        harmfulEffects.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enabled"), BuffMobsConfig.applyHarmfulEffects())
                .setDefaultValue(true)
                .setTooltip(Text.literal("Mobs apply harmful effects to players on attack"))
                .setSaveConsumer(BuffMobsConfig::setApplyHarmfulEffects)
                .build());

        harmfulEffects.addEntry(entryBuilder.startDoubleField(Text.literal("Chance"), BuffMobsConfig.getHarmfulEffectChance())
                .setDefaultValue(0.15)
                .setMin(0.0).setMax(1.0)
                .setTooltip(Text.literal("Chance to apply harmful effects (0.0-1.0)"))
                .setSaveConsumer(BuffMobsConfig::setHarmfulEffectChance)
                .build());

        harmfulEffects.addEntry(entryBuilder.startIntField(Text.literal("Poison Duration"), BuffMobsConfig.getPoisonDuration())
                .setDefaultValue(5)
                .setMin(1).setMax(60)
                .setTooltip(Text.literal("Poison effect duration in seconds"))
                .setSaveConsumer(BuffMobsConfig::setPoisonDuration)
                .build());

        harmfulEffects.addEntry(entryBuilder.startIntField(Text.literal("Slowness Duration"), BuffMobsConfig.getSlownessDuration())
                .setDefaultValue(3)
                .setMin(1).setMax(60)
                .setTooltip(Text.literal("Slowness effect duration in seconds"))
                .setSaveConsumer(BuffMobsConfig::setSlownessDuration)
                .build());

        harmfulEffects.addEntry(entryBuilder.startIntField(Text.literal("Wither Duration"), BuffMobsConfig.getWitherDuration())
                .setDefaultValue(3)
                .setMin(1).setMax(60)
                .setTooltip(Text.literal("Wither effect duration in seconds"))
                .setSaveConsumer(BuffMobsConfig::setWitherDuration)
                .build());

        // Mob Filter Category
        ConfigCategory mobFilter = builder.getOrCreateCategory(Text.literal("Mob Filter"));

        mobFilter.addEntry(entryBuilder.startBooleanToggle(Text.literal("Use Whitelist"), BuffMobsConfig.useMobWhitelist())
                .setDefaultValue(false)
                .setTooltip(Text.literal("Use whitelist mode (only listed mobs affected)"))
                .setSaveConsumer(BuffMobsConfig::setUseMobWhitelist)
                .build());

        mobFilter.addEntry(entryBuilder.startStrList(Text.literal("Whitelist"), BuffMobsConfig.getMobWhitelist())
                .setDefaultValue(Arrays.asList())
                .setTooltip(Text.literal("Mob whitelist (mob registry names)"))
                .setSaveConsumer(BuffMobsConfig::setMobWhitelist)
                .build());

        mobFilter.addEntry(entryBuilder.startStrList(Text.literal("Blacklist"), BuffMobsConfig.getMobBlacklist())
                .setDefaultValue(Arrays.asList("minecraft:warden"))
                .setTooltip(Text.literal("Mob blacklist (mob registry names)"))
                .setSaveConsumer(BuffMobsConfig::setMobBlacklist)
                .build());

        // Mod Filter Category
        ConfigCategory modFilter = builder.getOrCreateCategory(Text.literal("Mod Filter"));

        modFilter.addEntry(entryBuilder.startBooleanToggle(Text.literal("Use Whitelist"), BuffMobsConfig.useModidWhitelist())
                .setDefaultValue(false)
                .setTooltip(Text.literal("Use modid whitelist mode (only mobs from listed mods affected)"))
                .setSaveConsumer(BuffMobsConfig::setUseModidWhitelist)
                .build());

        modFilter.addEntry(entryBuilder.startStrList(Text.literal("Whitelist"), BuffMobsConfig.getModidWhitelist())
                .setDefaultValue(Arrays.asList("minecraft"))
                .setTooltip(Text.literal("ModID whitelist (only mobs from these mods will be affected)"))
                .setSaveConsumer(BuffMobsConfig::setModidWhitelist)
                .build());

        modFilter.addEntry(entryBuilder.startStrList(Text.literal("Blacklist"), BuffMobsConfig.getModidBlacklist())
                .setDefaultValue(Arrays.asList())
                .setTooltip(Text.literal("ModID blacklist (mobs from these mods will not be affected)"))
                .setSaveConsumer(BuffMobsConfig::setModidBlacklist)
                .build());

        // Dimension Filter Category
        ConfigCategory dimensionFilter = builder.getOrCreateCategory(Text.literal("Dimension Filter"));

        dimensionFilter.addEntry(entryBuilder.startBooleanToggle(Text.literal("Use Whitelist"), BuffMobsConfig.useDimensionWhitelist())
                .setDefaultValue(false)
                .setTooltip(Text.literal("Use dimension whitelist mode (only listed dimensions affected)"))
                .setSaveConsumer(BuffMobsConfig::setUseDimensionWhitelist)
                .build());

        dimensionFilter.addEntry(entryBuilder.startStrList(Text.literal("Whitelist"), BuffMobsConfig.getDimensionWhitelist())
                .setDefaultValue(Arrays.asList("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"))
                .setTooltip(Text.literal("Dimension whitelist (only these dimensions will be affected)"))
                .setSaveConsumer(BuffMobsConfig::setDimensionWhitelist)
                .build());

        dimensionFilter.addEntry(entryBuilder.startStrList(Text.literal("Blacklist"), BuffMobsConfig.getDimensionBlacklist())
                .setDefaultValue(Arrays.asList())
                .setTooltip(Text.literal("Dimension blacklist (these dimensions will not be affected)"))
                .setSaveConsumer(BuffMobsConfig::setDimensionBlacklist)
                .build());

        // Dimension Scaling Category
        ConfigCategory dimensionScaling = builder.getOrCreateCategory(Text.literal("Dimension Scaling"));

        List<BuffMobsConfig.ConfigData.DimensionScalingSettings.DimensionScaling> currentScalings = BuffMobsConfig.getDimensionScalings();

        for (int i = 0; i < 5; i++) {
            final int index = i;
            String slotName = "Slot " + (i + 1);

            BuffMobsConfig.ConfigData.DimensionScalingSettings.DimensionScaling scaling =
                    index < currentScalings.size() ? currentScalings.get(index) :
                            new BuffMobsConfig.ConfigData.DimensionScalingSettings.DimensionScaling();

            dimensionScaling.addEntry(entryBuilder.startTextField(Text.literal(slotName + " - Dimension Name"), scaling.dimensionName)
                    .setDefaultValue("")
                    .setTooltip(Text.literal("Dimension name (e.g., minecraft:the_nether)"))
                    .setSaveConsumer(value -> {
                        List<BuffMobsConfig.ConfigData.DimensionScalingSettings.DimensionScaling> scalings =
                                new ArrayList<>(BuffMobsConfig.getDimensionScalings());
                        while (scalings.size() <= index) {
                            scalings.add(new BuffMobsConfig.ConfigData.DimensionScalingSettings.DimensionScaling());
                        }
                        scalings.get(index).dimensionName = value;
                        BuffMobsConfig.setDimensionScalings(scalings);
                    })
                    .build());

            dimensionScaling.addEntry(entryBuilder.startDoubleField(Text.literal(slotName + " - Health Multiplier"), scaling.healthMultiplier)
                    .setDefaultValue(1.0)
                    .setMin(1.0).setMax(10.0)
                    .setTooltip(Text.literal("Health multiplier for this dimension"))
                    .setSaveConsumer(value -> {
                        List<BuffMobsConfig.ConfigData.DimensionScalingSettings.DimensionScaling> scalings =
                                new ArrayList<>(BuffMobsConfig.getDimensionScalings());
                        while (scalings.size() <= index) {
                            scalings.add(new BuffMobsConfig.ConfigData.DimensionScalingSettings.DimensionScaling());
                        }
                        scalings.get(index).healthMultiplier = value;
                        BuffMobsConfig.setDimensionScalings(scalings);
                    })
                    .build());

            dimensionScaling.addEntry(entryBuilder.startDoubleField(Text.literal(slotName + " - Damage Multiplier"), scaling.damageMultiplier)
                    .setDefaultValue(1.0)
                    .setMin(1.0).setMax(10.0)
                    .setTooltip(Text.literal("Damage multiplier for this dimension"))
                    .setSaveConsumer(value -> {
                        List<BuffMobsConfig.ConfigData.DimensionScalingSettings.DimensionScaling> scalings =
                                new ArrayList<>(BuffMobsConfig.getDimensionScalings());
                        while (scalings.size() <= index) {
                            scalings.add(new BuffMobsConfig.ConfigData.DimensionScalingSettings.DimensionScaling());
                        }
                        scalings.get(index).damageMultiplier = value;
                        BuffMobsConfig.setDimensionScalings(scalings);
                    })
                    .build());

            dimensionScaling.addEntry(entryBuilder.startDoubleField(Text.literal(slotName + " - Speed Multiplier"), scaling.speedMultiplier)
                    .setDefaultValue(1.0)
                    .setMin(1.0).setMax(5.0)
                    .setTooltip(Text.literal("Speed multiplier for this dimension"))
                    .setSaveConsumer(value -> {
                        List<BuffMobsConfig.ConfigData.DimensionScalingSettings.DimensionScaling> scalings =
                                new ArrayList<>(BuffMobsConfig.getDimensionScalings());
                        while (scalings.size() <= index) {
                            scalings.add(new BuffMobsConfig.ConfigData.DimensionScalingSettings.DimensionScaling());
                        }
                        scalings.get(index).speedMultiplier = value;
                        BuffMobsConfig.setDimensionScalings(scalings);
                    })
                    .build());

            dimensionScaling.addEntry(entryBuilder.startDoubleField(Text.literal(slotName + " - Attack Speed Multiplier"), scaling.attackSpeedMultiplier)
                    .setDefaultValue(1.0)
                    .setMin(1.0).setMax(10.0)
                    .setTooltip(Text.literal("Attack speed multiplier for this dimension"))
                    .setSaveConsumer(value -> {
                        List<BuffMobsConfig.ConfigData.DimensionScalingSettings.DimensionScaling> scalings =
                                new ArrayList<>(BuffMobsConfig.getDimensionScalings());
                        while (scalings.size() <= index) {
                            scalings.add(new BuffMobsConfig.ConfigData.DimensionScalingSettings.DimensionScaling());
                        }
                        scalings.get(index).attackSpeedMultiplier = value;
                        BuffMobsConfig.setDimensionScalings(scalings);
                    })
                    .build());

            dimensionScaling.addEntry(entryBuilder.startDoubleField(Text.literal(slotName + " - Armor Addition"), scaling.armorAddition)
                    .setDefaultValue(0.0)
                    .setMin(0.0).setMax(20.0)
                    .setTooltip(Text.literal("Armor addition for this dimension"))
                    .setSaveConsumer(value -> {
                        List<BuffMobsConfig.ConfigData.DimensionScalingSettings.DimensionScaling> scalings =
                                new ArrayList<>(BuffMobsConfig.getDimensionScalings());
                        while (scalings.size() <= index) {
                            scalings.add(new BuffMobsConfig.ConfigData.DimensionScalingSettings.DimensionScaling());
                        }
                        scalings.get(index).armorAddition = value;
                        BuffMobsConfig.setDimensionScalings(scalings);
                    })
                    .build());

            dimensionScaling.addEntry(entryBuilder.startDoubleField(Text.literal(slotName + " - Armor Toughness Addition"), scaling.armorToughnessAddition)
                    .setDefaultValue(0.0)
                    .setMin(0.0).setMax(10.0)
                    .setTooltip(Text.literal("Armor toughness addition for this dimension"))
                    .setSaveConsumer(value -> {
                        List<BuffMobsConfig.ConfigData.DimensionScalingSettings.DimensionScaling> scalings =
                                new ArrayList<>(BuffMobsConfig.getDimensionScalings());
                        while (scalings.size() <= index) {
                            scalings.add(new BuffMobsConfig.ConfigData.DimensionScalingSettings.DimensionScaling());
                        }
                        scalings.get(index).armorToughnessAddition = value;
                        BuffMobsConfig.setDimensionScalings(scalings);
                    })
                    .build());
        }

        return builder.build();
    }
}