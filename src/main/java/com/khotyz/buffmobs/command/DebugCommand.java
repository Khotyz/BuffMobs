package com.khotyz.buffmobs.command;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.event.MobTickHandler;
import com.khotyz.buffmobs.util.MobBuffUtil;
import com.khotyz.buffmobs.util.MobPresetUtil;
import com.khotyz.buffmobs.util.RangedMobAIManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class DebugCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext registryAccess,
                                Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("buffmobs")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("debug")
                        .executes(DebugCommand::debugNearestMob))
                .then(Commands.literal("reload")
                        .executes(DebugCommand::reloadMobs))
                .then(Commands.literal("info")
                        .executes(DebugCommand::showInfo))
                .then(Commands.literal("presets")
                        .executes(DebugCommand::showPresets))
                .then(Commands.literal("testpreset")
                        .executes(DebugCommand::testPresetSystem)));
    }

    private static int debugNearestMob(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (source.getPlayer() == null) {
            source.sendFailure(Component.literal("Must be executed by a player!"));
            return 0;
        }

        AABB searchBox = source.getPlayer().getBoundingBox().inflate(5.0);
        List<Mob> nearbyMobs = source.getLevel().getEntitiesOfClass(
                Mob.class, searchBox, mob -> true);

        if (nearbyMobs.isEmpty()) {
            source.sendFailure(Component.literal("No mobs nearby! Get closer."));
            return 0;
        }

        Mob closestMob = nearbyMobs.get(0);
        double closestDist = source.getPlayer().distanceToSqr(closestMob);

        for (Mob mob : nearbyMobs) {
            double dist = source.getPlayer().distanceToSqr(mob);
            if (dist < closestDist) {
                closestDist = dist;
                closestMob = mob;
            }
        }

        Mob mob = closestMob;
        String mobId = mob.getType().toString();

        net.minecraft.resources.ResourceLocation mobRL = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
        String cleanMobId = mobRL.toString();

        source.sendSuccess(() -> Component.literal("§6=== BuffMobs Debug ==="), false);
        source.sendSuccess(() -> Component.literal("§eMob: §f" + mob.getType().toString()), false);
        source.sendSuccess(() -> Component.literal("§eMob ID: §f" + cleanMobId), false);
        source.sendSuccess(() -> Component.literal("§eDimension: §f" +
                mob.level().dimension().location()), false);

        boolean isValid = MobBuffUtil.isValidMob(mob);
        source.sendSuccess(() -> Component.literal("§eValid for buffs: §f" +
                (isValid ? "§aYES" : "§cNO")), false);

        if (!isValid) {
            source.sendSuccess(() -> Component.literal("§cMob is filtered out!"), false);
        }

        source.sendSuccess(() -> Component.literal(""), false);
        source.sendSuccess(() -> Component.literal("§6=== Ranged/Melee System ==="), false);
        source.sendSuccess(() -> Component.literal("§eSystem Enabled: §f" +
                (BuffMobsConfig.RangedMeleeSwitching.enabled.get() ? "§aYES" : "§cNO")), false);

        if (RangedMobAIManager.isRangedMob(mob)) {
            source.sendSuccess(() -> Component.literal("§eIs Ranged Mob: §aYES"), false);
            source.sendSuccess(() -> Component.literal("§eBehavior Mode (Config): §f" +
                    BuffMobsConfig.RangedMeleeSwitching.behaviorMode.get()), false);
            source.sendSuccess(() -> Component.literal("§eIn Melee Mode: §f" +
                    (RangedMobAIManager.isInMeleeMode(mob) ? "§aYES" : "§cNO")), false);
        } else {
            source.sendSuccess(() -> Component.literal("§eIs Ranged Mob: §cNO"), false);
        }

        source.sendSuccess(() -> Component.literal(""), false);
        source.sendSuccess(() -> Component.literal("§6=== Preset System ==="), false);
        source.sendSuccess(() -> Component.literal("§ePresets Enabled: §f" +
                (BuffMobsConfig.MobPresets.enabled.get() == BuffMobsConfig.MobPresets.PresetToggle.ENABLED ? "§aYES" : "§cNO")), false);

        if (BuffMobsConfig.MobPresets.enabled.get() == BuffMobsConfig.MobPresets.PresetToggle.ENABLED) {
            MobPresetUtil.PresetMultipliers preset = MobPresetUtil.getPresetForMob(mob);

            if (preset != null) {
                source.sendSuccess(() -> Component.literal("§ePreset Found: §aYES"), false);
                source.sendSuccess(() -> Component.literal("§ePreset Health: §f" + preset.health + "x"), false);
                source.sendSuccess(() -> Component.literal("§ePreset Damage: §f" + preset.damage + "x"), false);
                source.sendSuccess(() -> Component.literal("§ePreset Speed: §f" + preset.speed + "x"), false);
                source.sendSuccess(() -> Component.literal("§ePreset Attack Speed: §f" + preset.attackSpeed + "x"), false);
                source.sendSuccess(() -> Component.literal("§ePreset Armor: §f+" + preset.armor), false);
                source.sendSuccess(() -> Component.literal("§ePreset Toughness: §f+" + preset.armorToughness), false);
            } else {
                source.sendSuccess(() -> Component.literal("§ePreset Found: §cNO"), false);
                source.sendSuccess(() -> Component.literal("§7Using dimension/default scaling"), false);

                source.sendSuccess(() -> Component.literal(""), false);
                source.sendSuccess(() -> Component.literal("§7Available mappings:"), false);
                for (String mapping : BuffMobsConfig.MobPresets.mobMapping.get()) {
                    source.sendSuccess(() -> Component.literal("§7  - " + mapping), false);
                }

                source.sendSuccess(() -> Component.literal(""), false);
                source.sendSuccess(() -> Component.literal("§7To add this mob, use format:"), false);
                source.sendSuccess(() -> Component.literal("§7  " + mobId + ":preset_name"), false);
                source.sendSuccess(() -> Component.literal("§7Example: " + mobId + ":weak"), false);
            }
        }

        source.sendSuccess(() -> Component.literal(""), false);
        source.sendSuccess(() -> Component.literal("§6=== Scaling ==="), false);

        double dayMult = MobBuffUtil.getDayMultiplier(mob.level().getDayTime());
        source.sendSuccess(() -> Component.literal("§eDay Multiplier: §f" +
                String.format("%.2f", dayMult)), false);

        var dimMult = MobBuffUtil.getDimensionMultipliers(mob);
        source.sendSuccess(() -> Component.literal("§eDim Health Mult: §f" + dimMult.health), false);
        source.sendSuccess(() -> Component.literal("§eDim Damage Mult: §f" + dimMult.damage), false);

        source.sendSuccess(() -> Component.literal(""), false);
        source.sendSuccess(() -> Component.literal("§6=== Current Stats ==="), false);

        AttributeInstance health = mob.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) {
            source.sendSuccess(() -> Component.literal("§eHealth: §f" +
                    String.format("%.1f / %.1f", mob.getHealth(), health.getValue())), false);
            source.sendSuccess(() -> Component.literal("§eBase Health: §f" +
                    String.format("%.1f", health.getBaseValue())), false);
        }

        AttributeInstance damage = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage != null) {
            source.sendSuccess(() -> Component.literal("§eAttack Damage: §f" +
                    String.format("%.1f (base: %.1f)", damage.getValue(), damage.getBaseValue())), false);
        }

        source.sendSuccess(() -> Component.literal("§eActive Effects: §f" +
                mob.getActiveEffects().size()), false);

        mob.getActiveEffects().forEach(effect -> {
            source.sendSuccess(() -> Component.literal("  §7- " +
                    effect.getEffect().value().getDisplayName().getString() +
                    " " + (effect.getAmplifier() + 1)), false);
        });

        return 1;
    }

    private static int showPresets(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        source.sendSuccess(() -> Component.literal("§6=== BuffMobs Presets ==="), false);
        source.sendSuccess(() -> Component.literal("§eEnabled: §f" +
                (BuffMobsConfig.MobPresets.enabled.get() == BuffMobsConfig.MobPresets.PresetToggle.ENABLED ? "§aYES" : "§cNO")), false);

        if (BuffMobsConfig.MobPresets.enabled.get() != BuffMobsConfig.MobPresets.PresetToggle.ENABLED) {
            source.sendSuccess(() -> Component.literal("§cPresets are disabled in config!"), false);
            source.sendSuccess(() -> Component.literal("§7Enable in config/buffmobs-common.toml"), false);
            return 1;
        }

        source.sendSuccess(() -> Component.literal(""), false);
        source.sendSuccess(() -> Component.literal("§6=== Available Presets ==="), false);

        BuffMobsConfig.MobPresets.PresetSlot[] presets = {
                BuffMobsConfig.MobPresets.preset1,
                BuffMobsConfig.MobPresets.preset2,
                BuffMobsConfig.MobPresets.preset3,
                BuffMobsConfig.MobPresets.preset4,
                BuffMobsConfig.MobPresets.preset5
        };

        int presetNum = 1;
        int validPresets = 0;
        for (BuffMobsConfig.MobPresets.PresetSlot preset : presets) {
            String name = preset.presetName.get();
            if (!name.isEmpty()) {
                int num = presetNum;
                source.sendSuccess(() -> Component.literal(String.format(
                        "§e%d. §f%s §7(HP: %.1fx, DMG: %.1fx, SPD: %.1fx, ASPD: %.1fx, ARM: +%.0f, TOUGH: +%.0f)",
                        num, preset.presetName.get(), preset.healthMultiplier.get(), preset.damageMultiplier.get(),
                        preset.speedMultiplier.get(), preset.attackSpeedMultiplier.get(),
                        preset.armorAddition.get(), preset.armorToughnessAddition.get())), false);
                validPresets++;
            } else {
                int num = presetNum;
                source.sendSuccess(() -> Component.literal(String.format(
                        "§e%d. §7(empty slot)", num)), false);
            }
            presetNum++;
        }

        if (validPresets == 0) {
            source.sendSuccess(() -> Component.literal("§cNo presets configured! All slots are empty."), false);
        }

        source.sendSuccess(() -> Component.literal(""), false);
        source.sendSuccess(() -> Component.literal("§6=== Mob Mappings ==="), false);
        source.sendSuccess(() -> Component.literal("§7Total mappings: §f" +
                BuffMobsConfig.MobPresets.mobMapping.get().size()), false);

        if (BuffMobsConfig.MobPresets.mobMapping.get().isEmpty()) {
            source.sendSuccess(() -> Component.literal("§cNo mappings configured!"), false);
            source.sendSuccess(() -> Component.literal("§7Add mappings in config/buffmobs-common.toml"), false);
        } else {
            int count = 0;
            for (String mapping : BuffMobsConfig.MobPresets.mobMapping.get()) {
                count++;
                final int num = count;
                String cleanMapping = mapping.startsWith("entity.") ? mapping.substring(7) : mapping;
                String[] parts = cleanMapping.split(":");
                if (parts.length >= 3) {
                    String mobId = parts[0] + ":" + parts[1];
                    String presetName = parts[2];
                    source.sendSuccess(() -> Component.literal(String.format(
                            "§7%d. §f%s §7→ §e%s", num, mobId, presetName)), false);
                } else {
                    source.sendSuccess(() -> Component.literal(String.format(
                            "§7%d. §c[INVALID] %s", num, mapping)), false);
                }
            }
        }

        source.sendSuccess(() -> Component.literal(""), false);
        source.sendSuccess(() -> Component.literal("§7Use §e/buffmobs debug §7near a mob to check its preset"), false);

        return 1;
    }

    private static int reloadMobs(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        source.sendSuccess(() -> Component.literal("§6Reapplying buffs to all mobs..."), false);

        int count = 0;
        for (Entity entity : source.getLevel().getAllEntities()) {
            if (entity instanceof Mob mob) {
                try {
                    MobBuffUtil.applyBuffs(mob);
                    count++;
                } catch (Exception e) {
                    BuffMobsMod.LOGGER.error("Failed to buff mob", e);
                }
            }
        }

        final int finalCount = count;
        source.sendSuccess(() -> Component.literal("§aBuffed " + finalCount + " mobs!"), false);

        return 1;
    }

    private static int showInfo(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        source.sendSuccess(() -> Component.literal("§6=== BuffMobs Info ==="), false);
        source.sendSuccess(() -> Component.literal("§eEnabled: §f" +
                BuffMobsConfig.enabled.get()), false);
        source.sendSuccess(() -> Component.literal("§eInitialized Mobs: §f" +
                MobTickHandler.getInitializedCount()), false);
        source.sendSuccess(() -> Component.literal("§eHealth Mult: §f" +
                BuffMobsConfig.Attributes.healthMultiplier.get()), false);
        source.sendSuccess(() -> Component.literal("§eDamage Mult: §f" +
                BuffMobsConfig.Attributes.damageMultiplier.get()), false);
        source.sendSuccess(() -> Component.literal("§eDay Scaling: §f" +
                BuffMobsConfig.DayScaling.enabled.get()), false);
        source.sendSuccess(() -> Component.literal("§ePresets: §f" +
                BuffMobsConfig.MobPresets.enabled.get()), false);
        source.sendSuccess(() -> Component.literal("§eRanged/Melee: §f" +
                BuffMobsConfig.RangedMeleeSwitching.enabled.get()), false);
        source.sendSuccess(() -> Component.literal("§eBehavior Mode: §f" +
                BuffMobsConfig.RangedMeleeSwitching.behaviorMode.get()), false);

        source.sendSuccess(() -> Component.literal("§eUse whitelist (mobs): §f" +
                BuffMobsConfig.MobFilter.useWhitelist.get()), false);
        source.sendSuccess(() -> Component.literal("§eUse whitelist (mods): §f" +
                BuffMobsConfig.ModIdFilter.useWhitelist.get()), false);
        source.sendSuccess(() -> Component.literal("§eUse whitelist (dims): §f" +
                BuffMobsConfig.DimensionFilter.useWhitelist.get()), false);

        return 1;
    }

    private static int testPresetSystem(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        source.sendSuccess(() -> Component.literal("§6=== Testing Preset System ==="), false);

        source.sendSuccess(() -> Component.literal("§ePresets Enabled: §f" +
                (BuffMobsConfig.MobPresets.enabled.get() == BuffMobsConfig.MobPresets.PresetToggle.ENABLED ? "§aYES" : "§cNO")), false);

        source.sendSuccess(() -> Component.literal(""), false);
        source.sendSuccess(() -> Component.literal("§eChecking all preset slots:"), false);

        testPresetSlot(source, "preset1", BuffMobsConfig.MobPresets.preset1);
        testPresetSlot(source, "preset2", BuffMobsConfig.MobPresets.preset2);
        testPresetSlot(source, "preset3", BuffMobsConfig.MobPresets.preset3);
        testPresetSlot(source, "preset4", BuffMobsConfig.MobPresets.preset4);
        testPresetSlot(source, "preset5", BuffMobsConfig.MobPresets.preset5);

        source.sendSuccess(() -> Component.literal(""), false);
        source.sendSuccess(() -> Component.literal("§eMob Mappings (" +
                BuffMobsConfig.MobPresets.mobMapping.get().size() + " total):"), false);

        if (BuffMobsConfig.MobPresets.mobMapping.get().isEmpty()) {
            source.sendSuccess(() -> Component.literal("  §c(none configured)"), false);
        } else {
            for (String mapping : BuffMobsConfig.MobPresets.mobMapping.get()) {
                source.sendSuccess(() -> Component.literal("  §7" + mapping), false);
            }
        }

        source.sendSuccess(() -> Component.literal(""), false);
        source.sendSuccess(() -> Component.literal("§eTest getPresetByName:"), false);

        for (String mapping : BuffMobsConfig.MobPresets.mobMapping.get()) {
            String[] parts = mapping.split(":");
            if (parts.length >= 3) {
                String mobId = parts[0] + ":" + parts[1];
                String presetName = parts[2];

                var preset = MobPresetUtil.getPresetByName(presetName);
                if (preset != null) {
                    source.sendSuccess(() -> Component.literal(String.format(
                            "  §a✓ §f%s §7-> §e%s §7(HP: %.1fx)",
                            mobId, presetName, preset.health)), false);
                } else {
                    source.sendSuccess(() -> Component.literal(String.format(
                            "  §c✗ §f%s §7-> §e%s §c(NOT FOUND)",
                            mobId, presetName)), false);
                }
            }
        }

        return 1;
    }

    private static void testPresetSlot(CommandSourceStack source, String slotName,
                                       BuffMobsConfig.MobPresets.PresetSlot slot) {
        String name = slot.presetName.get();
        if (name != null && !name.isEmpty()) {
            source.sendSuccess(() -> Component.literal(String.format(
                    "  §a✓ §e%s: §f'%s' §7(HP: %.1fx, DMG: %.1fx)",
                    slotName, name,
                    slot.healthMultiplier.get(),
                    slot.damageMultiplier.get())), false);
        } else {
            source.sendSuccess(() -> Component.literal(String.format(
                    "  §7○ §e%s: §7(empty)", slotName)), false);
        }
    }
}