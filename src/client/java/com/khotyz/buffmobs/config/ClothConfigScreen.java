package com.khotyz.buffmobs.config;

import com.khotyz.buffmobs.BuffMobsMod;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClothConfigScreen {

    // Returns a translatable tooltip component for the given key.
    private static Component tt(String key) {
        return Component.translatable(key);
    }

    // Animated text entry that pulses its alpha using a sine wave.
    private static AbstractConfigListEntry<?> buildPulsingCredits() {
        return new AbstractConfigListEntry<>(Component.translatable("buffmobs.config.credits.translation"), false) {
            @Override
            public Object getValue() { return null; }
            @Override
            public Optional<Object> getDefaultValue() { return Optional.empty(); }
            @Override
            public void save() {}

            @Override
            public java.util.List<? extends net.minecraft.client.gui.components.events.GuiEventListener> children() {
                return java.util.Collections.emptyList();
            }

            @Override
            public java.util.List<? extends net.minecraft.client.gui.narration.NarratableEntry> narratables() {
                return java.util.Collections.emptyList();
            }

            @Override
            public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
                // Pulse alpha between ~40 and 255 using a sine wave over time
                long time = System.currentTimeMillis();
                float pulse = (float)(Math.sin(time / 1000.0) * 0.5 + 0.5); // 0.0 – 1.0
                int alpha = (int)(40 + pulse * 215);
                int color = (alpha << 24) | 0xFFFFFF;
                Component text = Component.translatable("buffmobs.config.credits.translation");
                int textWidth = Minecraft.getInstance().font.width(text);
                graphics.drawString(Minecraft.getInstance().font, text, x + entryWidth / 2 - textWidth / 2, y + entryHeight / 2 - 4, color, false);
            }
        };
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

        // ── General ──────────────────────────────────────────────────────────
        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("buffmobs.config.general"));
        general.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.general.enabled"), cfg.enabled)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.general.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.enabled = v).build());
        general.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.general.visualEffects"), cfg.visualEffects)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.general.visualEffects.tooltip"))
                .setSaveConsumer(v -> cfg.visualEffects = v).build());
        general.addEntry(buildPulsingCredits());

        // ── Attributes ───────────────────────────────────────────────────────
        ConfigCategory attributes = builder.getOrCreateCategory(Component.translatable("buffmobs.config.attributes"));
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.healthMultiplier"), cfg.attributes.healthMultiplier)
                .setDefaultValue(1.5).setMin(0.1).setMax(10.0)
                .setTooltip(tt("buffmobs.config.attributes.healthMultiplier.tooltip"))
                .setSaveConsumer(v -> cfg.attributes.healthMultiplier = v).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.damageMultiplier"), cfg.attributes.damageMultiplier)
                .setDefaultValue(1.5).setMin(0.1).setMax(10.0)
                .setTooltip(tt("buffmobs.config.attributes.damageMultiplier.tooltip"))
                .setSaveConsumer(v -> cfg.attributes.damageMultiplier = v).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.speedMultiplier"), cfg.attributes.speedMultiplier)
                .setDefaultValue(1.0).setMin(0.1).setMax(10.0)
                .setTooltip(tt("buffmobs.config.attributes.speedMultiplier.tooltip"))
                .setSaveConsumer(v -> cfg.attributes.speedMultiplier = v).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.attackSpeedMultiplier"), cfg.attributes.attackSpeedMultiplier)
                .setDefaultValue(1.0).setMin(0.1).setMax(10.0)
                .setTooltip(tt("buffmobs.config.attributes.attackSpeedMultiplier.tooltip"))
                .setSaveConsumer(v -> cfg.attributes.attackSpeedMultiplier = v).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.armorAddition"), cfg.attributes.armorAddition)
                .setDefaultValue(5.0).setMin(0.0).setMax(20.0)
                .setTooltip(tt("buffmobs.config.attributes.armorAddition.tooltip"))
                .setSaveConsumer(v -> cfg.attributes.armorAddition = v).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.armorToughnessAddition"), cfg.attributes.armorToughnessAddition)
                .setDefaultValue(0.0).setMin(0.0).setMax(20.0)
                .setTooltip(tt("buffmobs.config.attributes.armorToughnessAddition.tooltip"))
                .setSaveConsumer(v -> cfg.attributes.armorToughnessAddition = v).build());

        // ── Status Effects ────────────────────────────────────────────────────
        ConfigCategory effects = builder.getOrCreateCategory(Component.translatable("buffmobs.config.effects"));
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

        // ── Harmful Effects ──────────────────────────────────────────────────
        ConfigCategory harmful = builder.getOrCreateCategory(Component.translatable("buffmobs.config.harmfulEffects"));
        harmful.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.harmfulEffects.enabled"), cfg.harmfulEffects.enabled)
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.harmfulEffects.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.harmfulEffects.enabled = v).build());
        harmful.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.harmfulEffects.chance"), cfg.harmfulEffects.chance)
                .setDefaultValue(0.15).setMin(0.0).setMax(1.0)
                .setTooltip(tt("buffmobs.config.harmfulEffects.chance.tooltip"))
                .setSaveConsumer(v -> cfg.harmfulEffects.chance = v).build());
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

        // ── Day Scaling ───────────────────────────────────────────────────────
        ConfigCategory dayScaling = builder.getOrCreateCategory(Component.translatable("buffmobs.config.dayScaling"));
        dayScaling.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.dayScaling.enabled"), cfg.dayScaling.enabled)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.dayScaling.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.dayScaling.enabled = v).build());
        dayScaling.addEntry(eb.startIntField(Component.translatable("buffmobs.config.dayScaling.interval"), cfg.dayScaling.interval)
                .setDefaultValue(7).setMin(1).setMax(365)
                .setTooltip(tt("buffmobs.config.dayScaling.interval.tooltip"))
                .setSaveConsumer(v -> cfg.dayScaling.interval = v).build());
        dayScaling.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.dayScaling.multiplier"), cfg.dayScaling.multiplier)
                .setDefaultValue(0.1).setMin(0.01).setMax(20.0)
                .setTooltip(tt("buffmobs.config.dayScaling.multiplier.tooltip"))
                .setSaveConsumer(v -> cfg.dayScaling.multiplier = v).build());
        dayScaling.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.dayScaling.maxMultiplier"), cfg.dayScaling.maxMultiplier)
                .setDefaultValue(5.0).setMin(1.0).setMax(10.0)
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

        // ── Dimension Scaling ─────────────────────────────────────────────────
        ConfigCategory dimScaling = builder.getOrCreateCategory(Component.translatable("buffmobs.config.dimensionScaling"));
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
                    .setDefaultValue(100).setMin(100).setMax(1000)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.healthPercent.tooltip"))
                    .setSaveConsumer(v -> slot.healthMultiplier = v).build());
            sub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.damagePercent"), slot.damageMultiplier)
                    .setDefaultValue(100).setMin(100).setMax(1000)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.damagePercent.tooltip"))
                    .setSaveConsumer(v -> slot.damageMultiplier = v).build());
            sub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.speedPercent"), slot.speedMultiplier)
                    .setDefaultValue(100).setMin(100).setMax(500)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.speedPercent.tooltip"))
                    .setSaveConsumer(v -> slot.speedMultiplier = v).build());
            sub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.attackSpeedPercent"), slot.attackSpeedMultiplier)
                    .setDefaultValue(100).setMin(100).setMax(1000)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.attackSpeedPercent.tooltip"))
                    .setSaveConsumer(v -> slot.attackSpeedMultiplier = v).build());
            sub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.armorAddition"), slot.armorAddition)
                    .setDefaultValue(0).setMin(0).setMax(20)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.armorAddition.tooltip"))
                    .setSaveConsumer(v -> slot.armorAddition = v).build());
            sub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.toughnessAddition"), slot.armorToughnessAddition)
                    .setDefaultValue(0).setMin(0).setMax(10)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.toughnessAddition.tooltip"))
                    .setSaveConsumer(v -> slot.armorToughnessAddition = v).build());
            dimScaling.addEntry(sub.build());
        }

        // ── Mob Filter ────────────────────────────────────────────────────────
        ConfigCategory mobFilter = builder.getOrCreateCategory(Component.translatable("buffmobs.config.mobFilter"));
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

        // ── Mod ID Filter ─────────────────────────────────────────────────────
        ConfigCategory modFilter = builder.getOrCreateCategory(Component.translatable("buffmobs.config.modidFilter"));
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

        // ── Dimension Filter ──────────────────────────────────────────────────
        ConfigCategory dimFilter = builder.getOrCreateCategory(Component.translatable("buffmobs.config.dimensionFilter"));
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

        // ── Ranged / Melee Switching ──────────────────────────────────────────
        ConfigCategory ranged = builder.getOrCreateCategory(Component.translatable("buffmobs.config.rangedMeleeSwitching"));
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

        // ── CombatDraft ───────────────────────────────────────────────────────
        ConfigCategory draft = builder.getOrCreateCategory(Component.translatable("buffmobs.config.combatDraft"));
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

        // ── Mob Presets ───────────────────────────────────────────────────────
        ConfigCategory presets = builder.getOrCreateCategory(Component.translatable("buffmobs.config.mobPresets"));
        presets.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobPresets.enabled"), cfg.mobPresets.enabled)
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.mobPresets.enabled.tooltip"))
                .setSaveConsumer(v -> cfg.mobPresets.enabled = v).build());

        BuffMobsConfig.MobPresets.PresetSlot[] presetSlots = {
                cfg.mobPresets.preset1, cfg.mobPresets.preset2, cfg.mobPresets.preset3,
                cfg.mobPresets.preset4, cfg.mobPresets.preset5
        };
        String[] defaultPresetNames = {"default", "boss", "elite", "weak", ""};
        for (int i = 0; i < presetSlots.length; i++) {
            BuffMobsConfig.MobPresets.PresetSlot p = presetSlots[i];
            int n = i + 1;
            // Use named key when the slot has a name, plain slot key otherwise.
            Component slotLabel = (p.presetName != null && !p.presetName.isEmpty())
                    ? Component.translatable("buffmobs.config.mobPresets.slot.named", n, p.presetName)
                    : Component.translatable("buffmobs.config.mobPresets.slot", n);
            var pSub = eb.startSubCategory(slotLabel);
            pSub.add(eb.startStrField(Component.translatable("buffmobs.config.mobPresets.presetName"), p.presetName == null ? "" : p.presetName)
                    .setDefaultValue(defaultPresetNames[i])
                    .setTooltip(tt("buffmobs.config.mobPresets.presetName.tooltip"))
                    .setSaveConsumer(v -> p.presetName = v).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.healthMultiplier"), p.healthMultiplier)
                    .setDefaultValue(1.0).setMin(0.01).setMax(100.0).setSaveConsumer(v -> p.healthMultiplier = v).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.damageMultiplier"), p.damageMultiplier)
                    .setDefaultValue(1.0).setMin(0.01).setMax(100.0).setSaveConsumer(v -> p.damageMultiplier = v).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.speedMultiplier"), p.speedMultiplier)
                    .setDefaultValue(1.0).setMin(0.01).setMax(10.0).setSaveConsumer(v -> p.speedMultiplier = v).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.attackSpeedMultiplier"), p.attackSpeedMultiplier)
                    .setDefaultValue(1.0).setMin(0.01).setMax(10.0).setSaveConsumer(v -> p.attackSpeedMultiplier = v).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.armorAddition"), p.armorAddition)
                    .setDefaultValue(0.0).setMin(0.0).setMax(30.0).setSaveConsumer(v -> p.armorAddition = v).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.toughnessAddition"), p.armorToughnessAddition)
                    .setDefaultValue(0.0).setMin(0.0).setMax(20.0).setSaveConsumer(v -> p.armorToughnessAddition = v).build());
            presets.addEntry(pSub.build());
        }
        presets.addEntry(eb.startStrList(Component.translatable("buffmobs.config.mobPresets.mobMapping"), new ArrayList<>(cfg.mobPresets.mobMapping))
                .setDefaultValue(List.of("minecraft:zombie:default", "minecraft:skeleton:default",
                        "minecraft:ender_dragon:boss", "minecraft:wither:boss"))
                .setTooltip(tt("buffmobs.config.mobPresets.mobMapping.tooltip"))
                .setSaveConsumer(v -> cfg.mobPresets.mobMapping = v).build());

        return builder.build();
    }
}