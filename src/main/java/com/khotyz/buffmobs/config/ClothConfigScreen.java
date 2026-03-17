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
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("buffmobs.config.title"))
                .setSavingRunnable(() -> {
                    BuffMobsConfig.SPEC.save();
                    BuffMobsMod.LOGGER.info("[BuffMobs] Config saved via Cloth Config");
                });

        ConfigEntryBuilder eb = builder.entryBuilder();
        BuffMobsConfig cfg = BuffMobsConfig.INSTANCE;

        // ── General ──────────────────────────────────────────────────────────
        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("buffmobs.config.general"));
        general.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.general.enabled"), cfg.enabled.get())
                .setDefaultValue(true)
                .setTooltip(tt("Enable or disable the mod entirely. When disabled, all mobs lose their buffs."))
                .setSaveConsumer(cfg.enabled::set).build());
        general.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.general.visualEffects"), cfg.visualEffects.get())
                .setDefaultValue(true)
                .setTooltip(tt("Show particle effects on mobs that have status effects applied."))
                .setSaveConsumer(cfg.visualEffects::set).build());

        // ── Attributes ───────────────────────────────────────────────────────
        ConfigCategory attributes = builder.getOrCreateCategory(Component.translatable("buffmobs.config.attributes"));
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.healthMultiplier"), cfg.attributes.healthMultiplier.get())
                .setDefaultValue(1.5).setMin(0.1).setMax(10.0)
                .setTooltip(tt("Multiplies the base max health of all buffed mobs. 1.5 = 50% more HP."))
                .setSaveConsumer(cfg.attributes.healthMultiplier::set).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.damageMultiplier"), cfg.attributes.damageMultiplier.get())
                .setDefaultValue(1.5).setMin(0.1).setMax(10.0)
                .setTooltip(tt("Multiplies the base attack damage of all buffed mobs. 1.5 = 50% more damage."))
                .setSaveConsumer(cfg.attributes.damageMultiplier::set).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.speedMultiplier"), cfg.attributes.speedMultiplier.get())
                .setDefaultValue(1.0).setMin(0.1).setMax(10.0)
                .setTooltip(tt("Multiplies movement speed. Capped internally at 2x to avoid unhittable mobs."))
                .setSaveConsumer(cfg.attributes.speedMultiplier::set).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.attackSpeedMultiplier"), cfg.attributes.attackSpeedMultiplier.get())
                .setDefaultValue(1.0).setMin(0.1).setMax(10.0)
                .setTooltip(tt("Multiplies the base attack speed of mobs."))
                .setSaveConsumer(cfg.attributes.attackSpeedMultiplier::set).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.armorAddition"), cfg.attributes.armorAddition.get())
                .setDefaultValue(5.0).setMin(0.0).setMax(20.0)
                .setTooltip(tt("Flat armor points added to mobs. 20 = full diamond armor equivalent."))
                .setSaveConsumer(cfg.attributes.armorAddition::set).build());
        attributes.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.attributes.armorToughnessAddition"), cfg.attributes.armorToughnessAddition.get())
                .setDefaultValue(0.0).setMin(0.0).setMax(20.0)
                .setTooltip(tt("Flat armor toughness added to mobs. Reduces the effectiveness of high-damage hits."))
                .setSaveConsumer(cfg.attributes.armorToughnessAddition::set).build());

        // ── Status Effects ────────────────────────────────────────────────────
        ConfigCategory effects = builder.getOrCreateCategory(Component.translatable("buffmobs.config.effects"));
        effects.addEntry(eb.startIntField(Component.translatable("buffmobs.config.effects.duration"), cfg.effects.duration.get())
                .setDefaultValue(-1).setMin(-1).setMax(7200)
                .setTooltip(tt("Duration of status effects in seconds. Use -1 for infinite (refreshed every second)."))
                .setSaveConsumer(cfg.effects.duration::set).build());
        effects.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.effects.strengthAmplifier"), cfg.effects.strengthAmplifier.get(), 0, 10)
                .setDefaultValue(0)
                .setTooltip(tt("Strength effect level granted to mobs. 0 = disabled. 1 = Strength I, 2 = Strength II, etc."))
                .setSaveConsumer(cfg.effects.strengthAmplifier::set).build());
        effects.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.effects.speedAmplifier"), cfg.effects.speedAmplifier.get(), 0, 10)
                .setDefaultValue(0)
                .setTooltip(tt("Speed effect level granted to mobs. 0 = disabled. Stacks with the speed attribute multiplier."))
                .setSaveConsumer(cfg.effects.speedAmplifier::set).build());
        effects.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.effects.resistanceAmplifier"), cfg.effects.resistanceAmplifier.get(), 0, 10)
                .setDefaultValue(0)
                .setTooltip(tt("Resistance effect level granted to mobs. 0 = disabled. Reduces all incoming damage."))
                .setSaveConsumer(cfg.effects.resistanceAmplifier::set).build());
        effects.addEntry(eb.startIntSlider(Component.translatable("buffmobs.config.effects.regenerationAmplifier"), cfg.effects.regenerationAmplifier.get(), 0, 10)
                .setDefaultValue(0)
                .setTooltip(tt("Regeneration effect level granted to mobs. 0 = disabled. Skipped for undead mobs."))
                .setSaveConsumer(cfg.effects.regenerationAmplifier::set).build());
        effects.addEntry(eb.startIntSlider(Component.literal("Absorption Level"), cfg.effects.absorptionAmplifier.get(), 0, 10)
                .setDefaultValue(0)
                .setTooltip(tt("Absorption effect level granted to mobs. 0 = disabled. Adds extra health buffer."))
                .setSaveConsumer(cfg.effects.absorptionAmplifier::set).build());

        // ── Harmful Effects ──────────────────────────────────────────────────
        ConfigCategory harmful = builder.getOrCreateCategory(Component.translatable("buffmobs.config.harmfulEffects"));
        harmful.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.harmfulEffects.enabled"), cfg.harmfulEffects.enabled.get())
                .setDefaultValue(true)
                .setTooltip(tt("When enabled, buffed mobs have a chance to apply a harmful effect to the player on hit."))
                .setSaveConsumer(cfg.harmfulEffects.enabled::set).build());
        harmful.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.harmfulEffects.chance"), cfg.harmfulEffects.chance.get())
                .setDefaultValue(0.15).setMin(0.0).setMax(1.0)
                .setTooltip(tt("Probability of a harmful effect being applied on each hit. 0.15 = 15% chance."))
                .setSaveConsumer(cfg.harmfulEffects.chance::set).build());
        harmful.addEntry(eb.startIntField(Component.translatable("buffmobs.config.harmfulEffects.poisonDuration"), cfg.harmfulEffects.poisonDuration.get())
                .setDefaultValue(5).setMin(1).setMax(60)
                .setTooltip(tt("How long the Poison effect lasts on the player, in seconds."))
                .setSaveConsumer(cfg.harmfulEffects.poisonDuration::set).build());
        harmful.addEntry(eb.startIntField(Component.translatable("buffmobs.config.harmfulEffects.slownessDuration"), cfg.harmfulEffects.slownessDuration.get())
                .setDefaultValue(3).setMin(1).setMax(60)
                .setTooltip(tt("How long the Slowness effect lasts on the player, in seconds."))
                .setSaveConsumer(cfg.harmfulEffects.slownessDuration::set).build());
        harmful.addEntry(eb.startIntField(Component.translatable("buffmobs.config.harmfulEffects.witherDuration"), cfg.harmfulEffects.witherDuration.get())
                .setDefaultValue(3).setMin(1).setMax(60)
                .setTooltip(tt("How long the Wither effect lasts on the player, in seconds."))
                .setSaveConsumer(cfg.harmfulEffects.witherDuration::set).build());

        // ── Day Scaling ───────────────────────────────────────────────────────
        ConfigCategory dayScaling = builder.getOrCreateCategory(Component.translatable("buffmobs.config.dayScaling"));
        dayScaling.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.dayScaling.enabled"), cfg.dayScaling.enabled.get())
                .setDefaultValue(false)
                .setTooltip(tt("Progressively increases mob strength as in-game days pass."))
                .setSaveConsumer(cfg.dayScaling.enabled::set).build());
        dayScaling.addEntry(eb.startIntField(Component.translatable("buffmobs.config.dayScaling.interval"), cfg.dayScaling.interval.get())
                .setDefaultValue(7).setMin(1).setMax(365)
                .setTooltip(tt("Number of in-game days between each difficulty increase."))
                .setSaveConsumer(cfg.dayScaling.interval::set).build());
        dayScaling.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.dayScaling.multiplier"), cfg.dayScaling.multiplier.get())
                .setDefaultValue(0.1).setMin(0.01).setMax(20.0)
                .setTooltip(tt("How much the multiplier increases per interval. 0.1 = +10% per interval."))
                .setSaveConsumer(cfg.dayScaling.multiplier::set).build());
        dayScaling.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.dayScaling.maxMultiplier"), cfg.dayScaling.maxMultiplier.get())
                .setDefaultValue(5.0).setMin(1.0).setMax(10.0)
                .setTooltip(tt("The maximum value the day scaling multiplier can reach."))
                .setSaveConsumer(cfg.dayScaling.maxMultiplier::set).build());
        dayScaling.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.dayScaling.showNotifications"), cfg.dayScaling.showNotifications.get())
                .setDefaultValue(true)
                .setTooltip(tt("Show a chat message to all players when the difficulty increases."))
                .setSaveConsumer(cfg.dayScaling.showNotifications::set).build());

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
            sub.add(eb.startStrField(Component.literal("Dimension ID"), slot.dimensionName.get())
                    .setDefaultValue("")
                    .setTooltip(tt("The dimension ID to apply scaling to. Example: minecraft:the_nether"))
                    .setSaveConsumer(slot.dimensionName::set).build());
            sub.add(eb.startIntField(Component.literal("Health % (100 = 1x)"), slot.healthMultiplier.get())
                    .setDefaultValue(100).setMin(100).setMax(1000)
                    .setTooltip(tt("Health multiplier for this dimension as a percentage. 200 = double HP."))
                    .setSaveConsumer(slot.healthMultiplier::set).build());
            sub.add(eb.startIntField(Component.literal("Damage % (100 = 1x)"), slot.damageMultiplier.get())
                    .setDefaultValue(100).setMin(100).setMax(1000)
                    .setTooltip(tt("Damage multiplier for this dimension as a percentage. 150 = 50% more damage."))
                    .setSaveConsumer(slot.damageMultiplier::set).build());
            sub.add(eb.startIntField(Component.literal("Speed % (100 = 1x)"), slot.speedMultiplier.get())
                    .setDefaultValue(100).setMin(100).setMax(500)
                    .setTooltip(tt("Speed multiplier for this dimension as a percentage."))
                    .setSaveConsumer(slot.speedMultiplier::set).build());
            sub.add(eb.startIntField(Component.literal("Atk Speed % (100 = 1x)"), slot.attackSpeedMultiplier.get())
                    .setDefaultValue(100).setMin(100).setMax(1000)
                    .setTooltip(tt("Attack speed multiplier for this dimension as a percentage."))
                    .setSaveConsumer(slot.attackSpeedMultiplier::set).build());
            sub.add(eb.startIntField(Component.literal("Armor Addition"), slot.armorAddition.get())
                    .setDefaultValue(0).setMin(0).setMax(20)
                    .setTooltip(tt("Flat armor points added to mobs in this dimension."))
                    .setSaveConsumer(slot.armorAddition::set).build());
            sub.add(eb.startIntField(Component.literal("Toughness Addition"), slot.armorToughnessAddition.get())
                    .setDefaultValue(0).setMin(0).setMax(10)
                    .setTooltip(tt("Flat armor toughness added to mobs in this dimension."))
                    .setSaveConsumer(slot.armorToughnessAddition::set).build());
            dimScaling.addEntry(sub.build());
        }

        // ── Mob Filter ────────────────────────────────────────────────────────
        ConfigCategory mobFilter = builder.getOrCreateCategory(Component.translatable("buffmobs.config.mobFilter"));
        mobFilter.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobFilter.useWhitelist"), cfg.mobFilter.useWhitelist.get())
                .setDefaultValue(false)
                .setTooltip(tt("If true, only mobs listed in the Whitelist will be buffed. If false, all mobs except the Blacklist are buffed."))
                .setSaveConsumer(cfg.mobFilter.useWhitelist::set).build());
        mobFilter.addEntry(eb.startStrList(Component.translatable("buffmobs.config.mobFilter.blacklist"), new ArrayList<>(cfg.mobFilter.blacklist.get()))
                .setDefaultValue(List.of("minecraft:warden"))
                .setTooltip(tt("Mob IDs that will never be buffed. Format: minecraft:zombie"))
                .setSaveConsumer(v -> cfg.mobFilter.blacklist.set(v)).build());
        mobFilter.addEntry(eb.startStrList(Component.translatable("buffmobs.config.mobFilter.whitelist"), new ArrayList<>(cfg.mobFilter.whitelist.get()))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("Mob IDs that will be buffed when Use Whitelist is enabled. Format: minecraft:zombie"))
                .setSaveConsumer(v -> cfg.mobFilter.whitelist.set(v)).build());

        // ── Mod ID Filter ─────────────────────────────────────────────────────
        ConfigCategory modFilter = builder.getOrCreateCategory(Component.translatable("buffmobs.config.modidFilter"));
        modFilter.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.modidFilter.useWhitelist"), cfg.modidFilter.useWhitelist.get())
                .setDefaultValue(false)
                .setTooltip(tt("If true, only mobs from mods in the Whitelist will be buffed."))
                .setSaveConsumer(cfg.modidFilter.useWhitelist::set).build());
        modFilter.addEntry(eb.startStrList(Component.translatable("buffmobs.config.modidFilter.blacklist"), new ArrayList<>(cfg.modidFilter.blacklist.get()))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("Mod IDs whose mobs will never be buffed. Format: minecraft"))
                .setSaveConsumer(v -> cfg.modidFilter.blacklist.set(v)).build());
        modFilter.addEntry(eb.startStrList(Component.translatable("buffmobs.config.modidFilter.whitelist"), new ArrayList<>(cfg.modidFilter.whitelist.get()))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("Mod IDs whose mobs will be buffed when Use Whitelist is enabled. Format: minecraft"))
                .setSaveConsumer(v -> cfg.modidFilter.whitelist.set(v)).build());

        // ── Dimension Filter ──────────────────────────────────────────────────
        ConfigCategory dimFilter = builder.getOrCreateCategory(Component.translatable("buffmobs.config.dimensionFilter"));
        dimFilter.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.dimensionFilter.useWhitelist"), cfg.dimensionFilter.useWhitelist.get())
                .setDefaultValue(false)
                .setTooltip(tt("If true, mobs are only buffed in dimensions listed in the Whitelist."))
                .setSaveConsumer(cfg.dimensionFilter.useWhitelist::set).build());
        dimFilter.addEntry(eb.startStrList(Component.translatable("buffmobs.config.dimensionFilter.blacklist"), new ArrayList<>(cfg.dimensionFilter.blacklist.get()))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("Dimensions where mobs will never be buffed. Format: minecraft:the_nether"))
                .setSaveConsumer(v -> cfg.dimensionFilter.blacklist.set(v)).build());
        dimFilter.addEntry(eb.startStrList(Component.translatable("buffmobs.config.dimensionFilter.whitelist"), new ArrayList<>(cfg.dimensionFilter.whitelist.get()))
                .setDefaultValue(List.of("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"))
                .setTooltip(tt("Dimensions where mobs are buffed when Use Whitelist is enabled."))
                .setSaveConsumer(v -> cfg.dimensionFilter.whitelist.set(v)).build());

        // ── Ranged / Melee Switching ──────────────────────────────────────────
        ConfigCategory ranged = builder.getOrCreateCategory(Component.translatable("buffmobs.config.rangedMeleeSwitching"));
        ranged.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.rangedMeleeSwitching.enabled"), cfg.rangedMeleeSwitching.enabled.get())
                .setDefaultValue(true)
                .setTooltip(tt("Enables ranged mobs (skeletons, pillagers, etc.) to change behavior when the player gets close."))
                .setSaveConsumer(cfg.rangedMeleeSwitching.enabled::set).build());
        ranged.addEntry(eb.startEnumSelector(Component.literal("Behavior Mode"),
                        BuffMobsConfig.RangedMeleeSwitching.BehaviorMode.class,
                        cfg.rangedMeleeSwitching.behaviorMode.get())
                .setDefaultValue(BuffMobsConfig.RangedMeleeSwitching.BehaviorMode.RANDOM)
                .setTooltip(tt("MELEE: switch to sword when player is close. KITE: flee to maintain distance. RANDOM: each mob picks one randomly on spawn."))
                .setSaveConsumer(cfg.rangedMeleeSwitching.behaviorMode::set).build());
        ranged.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.rangedMeleeSwitching.switchDistance"), cfg.rangedMeleeSwitching.switchDistance.get())
                .setDefaultValue(4.0).setMin(1.0).setMax(16.0)
                .setTooltip(tt("Distance (in blocks) at which the behavior switch triggers."))
                .setSaveConsumer(cfg.rangedMeleeSwitching.switchDistance::set).build());
        ranged.addEntry(eb.startDoubleField(Component.translatable("buffmobs.config.rangedMeleeSwitching.meleeSpeedMultiplier"), cfg.rangedMeleeSwitching.meleeSpeedMultiplier.get())
                .setDefaultValue(0.9).setMin(0.1).setMax(5.0)
                .setTooltip(tt("Speed multiplier applied when a ranged mob switches to melee mode."))
                .setSaveConsumer(cfg.rangedMeleeSwitching.meleeSpeedMultiplier::set).build());
        ranged.addEntry(eb.startStrList(Component.literal("Custom Ranged Mobs"), new ArrayList<>(cfg.rangedMeleeSwitching.customRangedMobs.get()))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("Additional mob IDs to treat as ranged (beyond skeletons, pillagers, piglins). Format: modid:mob_id"))
                .setSaveConsumer(v -> cfg.rangedMeleeSwitching.customRangedMobs.set(v)).build());

        // Weapon tiers subcategory
        var weaponSub = eb.startSubCategory(Component.literal("Weapon Unlock Days"));
        weaponSub.add(eb.startIntField(Component.literal("Stone Sword Unlock Day"), cfg.rangedMeleeSwitching.stoneSwordUnlockDay.get())
                .setDefaultValue(0).setMin(0).setMax(365)
                .setTooltip(tt("Day from which stone swords can appear (Overworld/End)."))
                .setSaveConsumer(cfg.rangedMeleeSwitching.stoneSwordUnlockDay::set).build());
        weaponSub.add(eb.startIntField(Component.literal("Iron Sword Unlock Day"), cfg.rangedMeleeSwitching.ironSwordUnlockDay.get())
                .setDefaultValue(7).setMin(0).setMax(365)
                .setTooltip(tt("Day from which iron swords can appear (Overworld/End)."))
                .setSaveConsumer(cfg.rangedMeleeSwitching.ironSwordUnlockDay::set).build());
        weaponSub.add(eb.startIntField(Component.literal("Diamond Sword Unlock Day"), cfg.rangedMeleeSwitching.diamondSwordUnlockDay.get())
                .setDefaultValue(21).setMin(0).setMax(365)
                .setTooltip(tt("Day from which diamond swords can appear (Overworld/End)."))
                .setSaveConsumer(cfg.rangedMeleeSwitching.diamondSwordUnlockDay::set).build());
        weaponSub.add(eb.startIntField(Component.literal("Netherite Sword Unlock Day"), cfg.rangedMeleeSwitching.netheriteSwordUnlockDay.get())
                .setDefaultValue(60).setMin(0).setMax(365)
                .setTooltip(tt("Day from which netherite swords can appear (End only)."))
                .setSaveConsumer(cfg.rangedMeleeSwitching.netheriteSwordUnlockDay::set).build());
        weaponSub.add(eb.startIntField(Component.literal("Golden Axe Unlock Day"), cfg.rangedMeleeSwitching.goldenAxeUnlockDay.get())
                .setDefaultValue(0).setMin(0).setMax(365)
                .setTooltip(tt("Day from which golden axes can appear (Nether only)."))
                .setSaveConsumer(cfg.rangedMeleeSwitching.goldenAxeUnlockDay::set).build());
        weaponSub.add(eb.startIntField(Component.literal("Diamond Axe Unlock Day"), cfg.rangedMeleeSwitching.diamondAxeUnlockDay.get())
                .setDefaultValue(14).setMin(0).setMax(365)
                .setTooltip(tt("Day from which diamond axes can appear (Nether only)."))
                .setSaveConsumer(cfg.rangedMeleeSwitching.diamondAxeUnlockDay::set).build());
        weaponSub.add(eb.startIntField(Component.literal("Netherite Axe Unlock Day"), cfg.rangedMeleeSwitching.netheriteAxeUnlockDay.get())
                .setDefaultValue(45).setMin(0).setMax(365)
                .setTooltip(tt("Day from which netherite axes can appear (Nether only)."))
                .setSaveConsumer(cfg.rangedMeleeSwitching.netheriteAxeUnlockDay::set).build());
        ranged.addEntry(weaponSub.build());

        // Enchantments subcategory
        var enchantSub = eb.startSubCategory(Component.literal("Enchantments"));
        enchantSub.add(eb.startBooleanToggle(Component.translatable("buffmobs.config.rangedMeleeSwitching.enchantmentsEnabled"), cfg.rangedMeleeSwitching.enchantmentsEnabled.get())
                .setDefaultValue(true)
                .setTooltip(tt("Allow generated melee weapons to receive enchantments."))
                .setSaveConsumer(cfg.rangedMeleeSwitching.enchantmentsEnabled::set).build());
        enchantSub.add(eb.startIntSlider(Component.translatable("buffmobs.config.rangedMeleeSwitching.maxEnchantmentsPerWeapon"), cfg.rangedMeleeSwitching.maxEnchantmentsPerWeapon.get(), 1, 4)
                .setDefaultValue(2)
                .setTooltip(tt("Maximum number of different enchantments a generated weapon can have."))
                .setSaveConsumer(cfg.rangedMeleeSwitching.maxEnchantmentsPerWeapon::set).build());
        enchantSub.add(eb.startIntField(Component.translatable("buffmobs.config.rangedMeleeSwitching.daysPerEnchantmentLevel"), cfg.rangedMeleeSwitching.daysPerEnchantmentLevel.get())
                .setDefaultValue(7).setMin(1).setMax(30)
                .setTooltip(tt("How many days must pass after unlock for each enchantment level to increase."))
                .setSaveConsumer(cfg.rangedMeleeSwitching.daysPerEnchantmentLevel::set).build());
        enchantSub.add(eb.startIntField(Component.literal("Sharpness Unlock Day"), cfg.rangedMeleeSwitching.sharpnessUnlockDay.get())
                .setDefaultValue(0).setMin(0).setMax(365)
                .setTooltip(tt("Day from which Sharpness can appear on generated weapons."))
                .setSaveConsumer(cfg.rangedMeleeSwitching.sharpnessUnlockDay::set).build());
        enchantSub.add(eb.startIntSlider(Component.literal("Sharpness Max Level"), cfg.rangedMeleeSwitching.sharpnessMaxLevel.get(), 1, 5)
                .setDefaultValue(5)
                .setTooltip(tt("Maximum level Sharpness can reach on generated weapons."))
                .setSaveConsumer(cfg.rangedMeleeSwitching.sharpnessMaxLevel::set).build());
        enchantSub.add(eb.startIntField(Component.literal("Fire Aspect Unlock Day"), cfg.rangedMeleeSwitching.fireAspectUnlockDay.get())
                .setDefaultValue(14).setMin(0).setMax(365)
                .setTooltip(tt("Day from which Fire Aspect can appear on generated weapons."))
                .setSaveConsumer(cfg.rangedMeleeSwitching.fireAspectUnlockDay::set).build());
        enchantSub.add(eb.startIntSlider(Component.literal("Fire Aspect Max Level"), cfg.rangedMeleeSwitching.fireAspectMaxLevel.get(), 1, 2)
                .setDefaultValue(2)
                .setTooltip(tt("Maximum level Fire Aspect can reach on generated weapons."))
                .setSaveConsumer(cfg.rangedMeleeSwitching.fireAspectMaxLevel::set).build());
        enchantSub.add(eb.startIntField(Component.literal("Knockback Unlock Day"), cfg.rangedMeleeSwitching.knockbackUnlockDay.get())
                .setDefaultValue(7).setMin(0).setMax(365)
                .setTooltip(tt("Day from which Knockback can appear on generated weapons."))
                .setSaveConsumer(cfg.rangedMeleeSwitching.knockbackUnlockDay::set).build());
        enchantSub.add(eb.startIntSlider(Component.literal("Knockback Max Level"), cfg.rangedMeleeSwitching.knockbackMaxLevel.get(), 1, 2)
                .setDefaultValue(2)
                .setTooltip(tt("Maximum level Knockback can reach on generated weapons."))
                .setSaveConsumer(cfg.rangedMeleeSwitching.knockbackMaxLevel::set).build());
        enchantSub.add(eb.startIntField(Component.literal("Sweeping Edge Unlock Day"), cfg.rangedMeleeSwitching.sweepingEdgeUnlockDay.get())
                .setDefaultValue(21).setMin(0).setMax(365)
                .setTooltip(tt("Day from which Sweeping Edge can appear on generated weapons."))
                .setSaveConsumer(cfg.rangedMeleeSwitching.sweepingEdgeUnlockDay::set).build());
        enchantSub.add(eb.startIntSlider(Component.literal("Sweeping Edge Max Level"), cfg.rangedMeleeSwitching.sweepingEdgeMaxLevel.get(), 1, 3)
                .setDefaultValue(3)
                .setTooltip(tt("Maximum level Sweeping Edge can reach on generated weapons."))
                .setSaveConsumer(cfg.rangedMeleeSwitching.sweepingEdgeMaxLevel::set).build());
        ranged.addEntry(enchantSub.build());

        // ── CombatDraft ───────────────────────────────────────────────────────
        ConfigCategory draft = builder.getOrCreateCategory(Component.literal("CombatDraft"));
        draft.addEntry(eb.startBooleanToggle(Component.literal("Enabled"), cfg.combatDraft.enabled.get())
                .setDefaultValue(true)
                .setTooltip(tt("When enabled, hostile and neutral mobs drink a regeneration potion when they fall below the health threshold."))
                .setSaveConsumer(cfg.combatDraft.enabled::set).build());
        draft.addEntry(eb.startDoubleField(Component.literal("Health Threshold (0.0-1.0)"), cfg.combatDraft.healthThreshold.get())
                .setDefaultValue(0.20).setMin(0.01).setMax(0.99)
                .setTooltip(tt("Fraction of max HP at which the potion is triggered. 0.20 = triggers below 20% HP."))
                .setSaveConsumer(cfg.combatDraft.healthThreshold::set).build());
        draft.addEntry(eb.startIntSlider(Component.literal("Regen Amplifier"), cfg.combatDraft.regenAmplifier.get(), 1, 10)
                .setDefaultValue(4)
                .setTooltip(tt("Level of the Regeneration effect applied. 1 = Regen I, 4 = Regen IV. Undead mobs receive Absorption + direct heal instead."))
                .setSaveConsumer(cfg.combatDraft.regenAmplifier::set).build());
        draft.addEntry(eb.startIntField(Component.literal("Regen Duration (s)"), cfg.combatDraft.regenDuration.get())
                .setDefaultValue(10).setMin(1).setMax(120)
                .setTooltip(tt("How long the regeneration effect lasts in seconds."))
                .setSaveConsumer(cfg.combatDraft.regenDuration::set).build());
        draft.addEntry(eb.startIntField(Component.literal("Cooldown (ticks)"), cfg.combatDraft.cooldownTicks.get())
                .setDefaultValue(600).setMin(20).setMax(72000)
                .setTooltip(tt("Ticks between uses of the potion per mob. 600 = 30 seconds."))
                .setSaveConsumer(cfg.combatDraft.cooldownTicks::set).build());
        draft.addEntry(eb.startIntField(Component.literal("Max Uses (0 = unlimited)"), cfg.combatDraft.maxUses.get())
                .setDefaultValue(0).setMin(0).setMax(100)
                .setTooltip(tt("Maximum number of times a mob can use the potion per life. 0 = no limit."))
                .setSaveConsumer(cfg.combatDraft.maxUses::set).build());

        var draftFilterSub = eb.startSubCategory(Component.literal("Mob Filter"));
        draftFilterSub.add(eb.startBooleanToggle(Component.literal("Use Whitelist"), cfg.combatDraft.useWhitelist.get())
                .setDefaultValue(false)
                .setTooltip(tt("If true, only mobs in the Whitelist can use CombatDraft."))
                .setSaveConsumer(cfg.combatDraft.useWhitelist::set).build());
        draftFilterSub.add(eb.startStrList(Component.literal("Whitelist"), new ArrayList<>(cfg.combatDraft.whitelist.get()))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("Mob IDs allowed to use CombatDraft (only active when Use Whitelist is true)."))
                .setSaveConsumer(v -> cfg.combatDraft.whitelist.set(v)).build());
        draftFilterSub.add(eb.startStrList(Component.literal("Blacklist"), new ArrayList<>(cfg.combatDraft.blacklist.get()))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tt("Mob IDs that can never use CombatDraft. Slime, magma_cube and ghast are always blacklisted."))
                .setSaveConsumer(v -> cfg.combatDraft.blacklist.set(v)).build());
        draft.addEntry(draftFilterSub.build());

        // ── Mob Presets ───────────────────────────────────────────────────────
        ConfigCategory presets = builder.getOrCreateCategory(Component.translatable("buffmobs.config.mobPresets"));
        presets.addEntry(eb.startBooleanToggle(Component.translatable("buffmobs.config.mobPresets.enabled"), cfg.mobPresets.enabled.get())
                .setDefaultValue(false)
                .setTooltip(tt("When enabled, individual mobs can be assigned a preset that overrides the global multipliers."))
                .setSaveConsumer(cfg.mobPresets.enabled::set).build());

        BuffMobsConfig.MobPresets.PresetSlot[] presetSlots = {
                cfg.mobPresets.preset1, cfg.mobPresets.preset2, cfg.mobPresets.preset3,
                cfg.mobPresets.preset4, cfg.mobPresets.preset5
        };
        String[] defaultPresetNames = {"default", "boss", "elite", "weak", ""};
        for (int i = 0; i < presetSlots.length; i++) {
            BuffMobsConfig.MobPresets.PresetSlot p = presetSlots[i];
            int n = i + 1;
            var pSub = eb.startSubCategory(Component.literal("Preset " + n
                    + (p.presetName.get().isEmpty() ? "" : " (" + p.presetName.get() + ")")));
            pSub.add(eb.startStrField(Component.literal("Preset Name"), p.presetName.get())
                    .setDefaultValue(defaultPresetNames[i])
                    .setTooltip(tt("Internal name for this preset. Used in the Mob Mapping list below."))
                    .setSaveConsumer(p.presetName::set).build());
            pSub.add(eb.startDoubleField(Component.literal("Health Multiplier"), p.healthMultiplier.get())
                    .setDefaultValue(1.0).setMin(0.01).setMax(100.0)
                    .setTooltip(tt("Health multiplier for mobs assigned to this preset. Stacks on top of global multipliers."))
                    .setSaveConsumer(p.healthMultiplier::set).build());
            pSub.add(eb.startDoubleField(Component.literal("Damage Multiplier"), p.damageMultiplier.get())
                    .setDefaultValue(1.0).setMin(0.01).setMax(100.0)
                    .setTooltip(tt("Damage multiplier for mobs assigned to this preset. Stacks on top of global multipliers."))
                    .setSaveConsumer(p.damageMultiplier::set).build());
            pSub.add(eb.startDoubleField(Component.literal("Speed Multiplier"), p.speedMultiplier.get())
                    .setDefaultValue(1.0).setMin(0.01).setMax(10.0)
                    .setTooltip(tt("Speed multiplier for mobs assigned to this preset. Stacks on top of global multipliers."))
                    .setSaveConsumer(p.speedMultiplier::set).build());
            pSub.add(eb.startDoubleField(Component.literal("Attack Speed Multiplier"), p.attackSpeedMultiplier.get())
                    .setDefaultValue(1.0).setMin(0.01).setMax(10.0)
                    .setTooltip(tt("Attack speed multiplier for mobs assigned to this preset."))
                    .setSaveConsumer(p.attackSpeedMultiplier::set).build());
            pSub.add(eb.startDoubleField(Component.literal("Armor Addition"), p.armorAddition.get())
                    .setDefaultValue(0.0).setMin(0.0).setMax(30.0)
                    .setTooltip(tt("Flat armor points added on top of global armor addition for this preset."))
                    .setSaveConsumer(p.armorAddition::set).build());
            pSub.add(eb.startDoubleField(Component.literal("Toughness Addition"), p.armorToughnessAddition.get())
                    .setDefaultValue(0.0).setMin(0.0).setMax(20.0)
                    .setTooltip(tt("Flat armor toughness added on top of global toughness addition for this preset."))
                    .setSaveConsumer(p.armorToughnessAddition::set).build());
            presets.addEntry(pSub.build());
        }
        presets.addEntry(eb.startStrList(Component.translatable("buffmobs.config.mobPresets.mobMapping"), new ArrayList<>(cfg.mobPresets.mobMapping.get()))
                .setDefaultValue(List.of("minecraft:zombie:default", "minecraft:skeleton:default",
                        "minecraft:ender_dragon:boss", "minecraft:wither:boss"))
                .setTooltip(tt("Maps mob IDs to preset names. Format: modid:mob_id:preset_name — e.g. minecraft:zombie:boss"))
                .setSaveConsumer(v -> cfg.mobPresets.mobMapping.set(v)).build());

        return builder.build();
    }
}