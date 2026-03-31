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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ClothConfigScreen {

    private static Component tt(String key) {
        return Component.translatable(key);
    }

    private static AbstractConfigListEntry<?> buildPulsingCredits() {
        return new AbstractConfigListEntry<>(Component.translatable("buffmobs.config.credits.translation"), false) {
            @Override public Object getValue() { return null; }
            @Override public Optional<Object> getDefaultValue() { return Optional.empty(); }
            @Override public void save() {}

            @Override
            public java.util.List<? extends net.minecraft.client.gui.components.events.GuiEventListener> children() {
                return Collections.emptyList();
            }

            @Override
            public java.util.List<? extends net.minecraft.client.gui.narration.NarratableEntry> narratables() {
                return Collections.emptyList();
            }

            @Override
            public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight,
                               int mouseX, int mouseY, boolean isHovered, float delta) {
                long time = System.currentTimeMillis();
                float pulse = (float)(Math.sin(time / 1000.0) * 0.5 + 0.5);
                int alpha = (int)(40 + pulse * 215);
                int color = (alpha << 24) | 0xFFFFFF;
                Component text = Component.translatable("buffmobs.config.credits.translation");
                int textWidth = Minecraft.getInstance().font.width(text);
                graphics.drawString(Minecraft.getInstance().font, text,
                        x + entryWidth / 2 - textWidth / 2, y + entryHeight / 2 - 4, color, false);
            }
        };
    }

    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("buffmobs.config.title"))
                .setSavingRunnable(() -> BuffMobsMod.LOGGER.info("[BuffMobs] Config saved via Cloth Config"));

        ConfigEntryBuilder eb = builder.entryBuilder();
        BuffMobsConfig cfg = BuffMobsConfig.INSTANCE;

        // ── General ──────────────────────────────────────────────────────────
        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("buffmobs.config.general"));
        general.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.general.enabled"), cfg.enabled.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.general.enabled.tooltip"))
                .setSaveConsumer(cfg.enabled::set).build());
        general.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.general.visualEffects"), cfg.visualEffects.get())
                .setDefaultValue(true)
                .setTooltip(tt("buffmobs.config.general.visualEffects.tooltip"))
                .setSaveConsumer(cfg.visualEffects::set).build());
        general.addEntry(buildPulsingCredits());

        // ── Attributes ───────────────────────────────────────────────────────
        ConfigCategory attributes = builder.getOrCreateCategory(Component.translatable("buffmobs.config.attributes"));
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.healthMultiplier"), cfg.attributes.healthMultiplier.get())
                .setDefaultValue(1.5).setMin(0.1).setMax(10.0)
                .setTooltip(tt("buffmobs.config.attributes.healthMultiplier.tooltip"))
                .setSaveConsumer(cfg.attributes.healthMultiplier::set).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.damageMultiplier"), cfg.attributes.damageMultiplier.get())
                .setDefaultValue(1.5).setMin(0.1).setMax(10.0)
                .setTooltip(tt("buffmobs.config.attributes.damageMultiplier.tooltip"))
                .setSaveConsumer(cfg.attributes.damageMultiplier::set).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.speedMultiplier"), cfg.attributes.speedMultiplier.get())
                .setDefaultValue(1.0).setMin(0.1).setMax(10.0)
                .setTooltip(tt("buffmobs.config.attributes.speedMultiplier.tooltip"))
                .setSaveConsumer(cfg.attributes.speedMultiplier::set).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.attackSpeedMultiplier"), cfg.attributes.attackSpeedMultiplier.get())
                .setDefaultValue(1.0).setMin(0.1).setMax(10.0)
                .setTooltip(tt("buffmobs.config.attributes.attackSpeedMultiplier.tooltip"))
                .setSaveConsumer(cfg.attributes.attackSpeedMultiplier::set).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.armorAddition"), cfg.attributes.armorAddition.get())
                .setDefaultValue(5.0).setMin(0.0).setMax(20.0)
                .setTooltip(tt("buffmobs.config.attributes.armorAddition.tooltip"))
                .setSaveConsumer(cfg.attributes.armorAddition::set).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.armorToughnessAddition"), cfg.attributes.armorToughnessAddition.get())
                .setDefaultValue(0.0).setMin(0.0).setMax(20.0)
                .setTooltip(tt("buffmobs.config.attributes.armorToughnessAddition.tooltip"))
                .setSaveConsumer(cfg.attributes.armorToughnessAddition::set).build());

        // ── Status Effects ────────────────────────────────────────────────────
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

        // ── Harmful Effects ──────────────────────────────────────────────────
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

        // ── Day Scaling ───────────────────────────────────────────────────────
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
                .setDefaultValue(0.1).setMin(0.01).setMax(20.0)
                .setTooltip(tt("buffmobs.config.dayScaling.multiplier.tooltip"))
                .setSaveConsumer(cfg.dayScaling.multiplier::set).build());
        dayScaling.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.dayScaling.maxMultiplier"), cfg.dayScaling.maxMultiplier.get())
                .setDefaultValue(5.0).setMin(1.0).setMax(10.0)
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
            sub.add(eb.startStrField(Component.translatable("buffmobs.config.dimensionScaling.dimensionId"), slot.dimensionName.get())
                    .setDefaultValue("")
                    .setTooltip(tt("buffmobs.config.dimensionScaling.dimensionId.tooltip"))
                    .setSaveConsumer(slot.dimensionName::set).build());
            sub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.healthPercent"), slot.healthMultiplier.get())
                    .setDefaultValue(100).setMin(100).setMax(1000)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.healthPercent.tooltip"))
                    .setSaveConsumer(slot.healthMultiplier::set).build());
            sub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.damagePercent"), slot.damageMultiplier.get())
                    .setDefaultValue(100).setMin(100).setMax(1000)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.damagePercent.tooltip"))
                    .setSaveConsumer(slot.damageMultiplier::set).build());
            sub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.speedPercent"), slot.speedMultiplier.get())
                    .setDefaultValue(100).setMin(100).setMax(500)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.speedPercent.tooltip"))
                    .setSaveConsumer(slot.speedMultiplier::set).build());
            sub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.attackSpeedPercent"), slot.attackSpeedMultiplier.get())
                    .setDefaultValue(100).setMin(100).setMax(1000)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.attackSpeedPercent.tooltip"))
                    .setSaveConsumer(slot.attackSpeedMultiplier::set).build());
            sub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.armorAddition"), slot.armorAddition.get())
                    .setDefaultValue(0).setMin(0).setMax(20)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.armorAddition.tooltip"))
                    .setSaveConsumer(slot.armorAddition::set).build());
            sub.add(eb.startIntField(Component.translatable("buffmobs.config.dimensionScaling.toughnessAddition"), slot.armorToughnessAddition.get())
                    .setDefaultValue(0).setMin(0).setMax(10)
                    .setTooltip(tt("buffmobs.config.dimensionScaling.toughnessAddition.tooltip"))
                    .setSaveConsumer(slot.armorToughnessAddition::set).build());
            dimScaling.addEntry(sub.build());
        }

        // ── Mob Filter ────────────────────────────────────────────────────────
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

        // ── Mod ID Filter ─────────────────────────────────────────────────────
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

        // ── Dimension Filter ──────────────────────────────────────────────────
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

        // ── Ranged / Melee Switching ──────────────────────────────────────────
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

        // ── CombatDraft ───────────────────────────────────────────────────────
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

        // ── Mob Presets ───────────────────────────────────────────────────────
        ConfigCategory presets = builder.getOrCreateCategory(Component.translatable("buffmobs.config.mobPresets"));
        presets.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobPresets.enabled"), cfg.mobPresets.enabled.get())
                .setDefaultValue(false)
                .setTooltip(tt("buffmobs.config.mobPresets.enabled.tooltip"))
                .setSaveConsumer(cfg.mobPresets.enabled::set).build());

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
                    .setDefaultValue(1.0).setMin(0.01).setMax(100.0).setSaveConsumer(p.healthMultiplier::set).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.damageMultiplier"), p.damageMultiplier.get())
                    .setDefaultValue(1.0).setMin(0.01).setMax(100.0).setSaveConsumer(p.damageMultiplier::set).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.speedMultiplier"), p.speedMultiplier.get())
                    .setDefaultValue(1.0).setMin(0.01).setMax(10.0).setSaveConsumer(p.speedMultiplier::set).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.attackSpeedMultiplier"), p.attackSpeedMultiplier.get())
                    .setDefaultValue(1.0).setMin(0.01).setMax(10.0).setSaveConsumer(p.attackSpeedMultiplier::set).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.armorAddition"), p.armorAddition.get())
                    .setDefaultValue(0.0).setMin(0.0).setMax(30.0).setSaveConsumer(p.armorAddition::set).build());
            pSub.add(eb.startDoubleField(Component.translatable("buffmobs.config.mobPresets.toughnessAddition"), p.armorToughnessAddition.get())
                    .setDefaultValue(0.0).setMin(0.0).setMax(20.0).setSaveConsumer(p.armorToughnessAddition::set).build());
            presets.addEntry(pSub.build());
        }
        presets.addEntry(eb.startStrList(Component.translatable("buffmobs.config.mobPresets.mobMapping"), new ArrayList<>(cfg.mobPresets.mobMapping.get()))
                .setDefaultValue(List.of("minecraft:zombie:default", "minecraft:skeleton:default",
                        "minecraft:ender_dragon:boss", "minecraft:wither:boss"))
                .setTooltip(tt("buffmobs.config.mobPresets.mobMapping.tooltip"))
                .setSaveConsumer(v -> cfg.mobPresets.mobMapping.set(v)).build());

        return builder.build();
    }

}