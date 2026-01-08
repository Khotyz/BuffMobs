package com.khotyz.buffmobs.command;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.event.MobTickHandler;
import com.khotyz.buffmobs.util.MobBuffUtil;
import com.khotyz.buffmobs.util.MobPresetUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;

import java.util.List;

public class DebugCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("buffmobs")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("debug")
                        .executes(DebugCommand::debugNearestMob))
                .then(CommandManager.literal("reload")
                        .executes(DebugCommand::reloadMobs))
                .then(CommandManager.literal("info")
                        .executes(DebugCommand::showInfo))
                .then(CommandManager.literal("presets")
                        .executes(DebugCommand::showPresets)));
    }

    private static int debugNearestMob(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        if (source.getPlayer() == null) {
            source.sendError(Text.literal("Must be executed by a player!"));
            return 0;
        }

        Box searchBox = source.getPlayer().getBoundingBox().expand(5.0);
        List<MobEntity> nearbyMobs = source.getWorld().getEntitiesByClass(
                MobEntity.class, searchBox, mob -> true);

        if (nearbyMobs.isEmpty()) {
            source.sendError(Text.literal("No mobs nearby! Get closer."));
            return 0;
        }

        MobEntity closestMob = nearbyMobs.get(0);
        double closestDist = source.getPlayer().squaredDistanceTo(closestMob);

        for (MobEntity mob : nearbyMobs) {
            double dist = source.getPlayer().squaredDistanceTo(mob);
            if (dist < closestDist) {
                closestDist = dist;
                closestMob = mob;
            }
        }

        MobEntity mob = closestMob;
        String mobId = Registries.ENTITY_TYPE.getId(mob.getType()).toString();

        source.sendFeedback(() -> Text.literal("§6=== BuffMobs Debug ==="), false);
        source.sendFeedback(() -> Text.literal("§eMob: §f" + mob.getType().toString()), false);
        source.sendFeedback(() -> Text.literal("§eMob ID: §f" + mobId), false);
        source.sendFeedback(() -> Text.literal("§eMod ID: §f" +
                Registries.ENTITY_TYPE.getId(mob.getType()).getNamespace()), false);
        source.sendFeedback(() -> Text.literal("§eDimension: §f" +
                mob.getWorld().getRegistryKey().getValue()), false);

        boolean isValid = MobBuffUtil.isValidMob(mob);
        source.sendFeedback(() -> Text.literal("§eValid for buffs: §f" +
                (isValid ? "§aYES" : "§cNO")), false);

        if (!isValid) {
            source.sendFeedback(() -> Text.literal("§cMob is filtered out!"), false);
        }

        source.sendFeedback(() -> Text.literal(""), false);
        source.sendFeedback(() -> Text.literal("§6=== Preset System ==="), false);
        source.sendFeedback(() -> Text.literal("§ePresets Enabled: §f" +
                (BuffMobsMod.CONFIG.mobPresets.enabled ? "§aYES" : "§cNO")), false);

        if (BuffMobsMod.CONFIG.mobPresets.enabled) {
            MobPresetUtil.PresetMultipliers preset = MobPresetUtil.getPresetForMob(mob);

            if (preset != null) {
                source.sendFeedback(() -> Text.literal("§ePreset Found: §aYES"), false);
                source.sendFeedback(() -> Text.literal("§ePreset Health: §f" + preset.health + "x"), false);
                source.sendFeedback(() -> Text.literal("§ePreset Damage: §f" + preset.damage + "x"), false);
                source.sendFeedback(() -> Text.literal("§ePreset Speed: §f" + preset.speed + "x"), false);
                source.sendFeedback(() -> Text.literal("§ePreset Attack Speed: §f" + preset.attackSpeed + "x"), false);
                source.sendFeedback(() -> Text.literal("§ePreset Armor: §f+" + preset.armor), false);
                source.sendFeedback(() -> Text.literal("§ePreset Toughness: §f+" + preset.armorToughness), false);
            } else {
                source.sendFeedback(() -> Text.literal("§ePreset Found: §cNO"), false);
                source.sendFeedback(() -> Text.literal("§7Using dimension/default scaling"), false);

                source.sendFeedback(() -> Text.literal(""), false);
                source.sendFeedback(() -> Text.literal("§7Available mappings:"), false);
                for (String mapping : BuffMobsMod.CONFIG.mobPresets.mobMapping) {
                    source.sendFeedback(() -> Text.literal("§7  - " + mapping), false);
                }

                source.sendFeedback(() -> Text.literal(""), false);
                source.sendFeedback(() -> Text.literal("§7To add this mob, use format:"), false);
                String finalMobId = mobId;
                source.sendFeedback(() -> Text.literal("§7  " + finalMobId + ":preset_name"), false);
            }
        }

        source.sendFeedback(() -> Text.literal(""), false);
        source.sendFeedback(() -> Text.literal("§6=== Scaling ==="), false);

        double dayMult = MobBuffUtil.getDayMultiplier(mob.getWorld().getTimeOfDay());
        source.sendFeedback(() -> Text.literal("§eDay Multiplier: §f" +
                String.format("%.2f", dayMult)), false);

        var dimMult = MobBuffUtil.getDimensionMultipliers(mob);
        source.sendFeedback(() -> Text.literal("§eDim Health Mult: §f" + dimMult.health), false);
        source.sendFeedback(() -> Text.literal("§eDim Damage Mult: §f" + dimMult.damage), false);

        source.sendFeedback(() -> Text.literal(""), false);
        source.sendFeedback(() -> Text.literal("§6=== Current Stats ==="), false);

        EntityAttributeInstance health = mob.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        if (health != null) {
            source.sendFeedback(() -> Text.literal("§eHealth: §f" +
                    String.format("%.1f / %.1f", mob.getHealth(), health.getValue())), false);
            source.sendFeedback(() -> Text.literal("§eBase Health: §f" +
                    String.format("%.1f", health.getBaseValue())), false);
        }

        EntityAttributeInstance damage = mob.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE);
        if (damage != null) {
            source.sendFeedback(() -> Text.literal("§eAttack Damage: §f" +
                    String.format("%.1f (base: %.1f)", damage.getValue(), damage.getBaseValue())), false);
        }

        source.sendFeedback(() -> Text.literal("§eActive Effects: §f" +
                mob.getStatusEffects().size()), false);

        mob.getStatusEffects().forEach(effect -> {
            source.sendFeedback(() -> Text.literal("  §7- " +
                    effect.getEffectType().value().getName().getString() +
                    " " + (effect.getAmplifier() + 1)), false);
        });

        return 1;
    }

    private static int showPresets(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        source.sendFeedback(() -> Text.literal("§6=== BuffMobs Presets ==="), false);
        source.sendFeedback(() -> Text.literal("§eEnabled: §f" +
                (BuffMobsMod.CONFIG.mobPresets.enabled ? "§aYES" : "§cNO")), false);

        if (!BuffMobsMod.CONFIG.mobPresets.enabled) {
            source.sendFeedback(() -> Text.literal("§cPresets are disabled in config!"), false);
            return 1;
        }

        source.sendFeedback(() -> Text.literal(""), false);
        source.sendFeedback(() -> Text.literal("§6=== Available Presets ==="), false);

        BuffMobsConfig.MobPresets.PresetSlot[] presets = {
                BuffMobsMod.CONFIG.mobPresets.preset1,
                BuffMobsMod.CONFIG.mobPresets.preset2,
                BuffMobsMod.CONFIG.mobPresets.preset3,
                BuffMobsMod.CONFIG.mobPresets.preset4,
                BuffMobsMod.CONFIG.mobPresets.preset5
        };

        int presetNum = 1;
        for (BuffMobsConfig.MobPresets.PresetSlot preset : presets) {
            if (!preset.presetName.isEmpty()) {
                int num = presetNum;
                source.sendFeedback(() -> Text.literal(String.format(
                        "§e%d. §f%s §7(HP: %.1fx, DMG: %.1fx, SPD: %.1fx, ASPD: %.1fx, ARM: +%.0f, TOUGH: +%.0f)",
                        num, preset.presetName, preset.healthMultiplier, preset.damageMultiplier,
                        preset.speedMultiplier, preset.attackSpeedMultiplier,
                        preset.armorAddition, preset.armorToughnessAddition)), false);
            }
            presetNum++;
        }

        source.sendFeedback(() -> Text.literal(""), false);
        source.sendFeedback(() -> Text.literal("§6=== Mob Mappings ==="), false);

        if (BuffMobsMod.CONFIG.mobPresets.mobMapping.isEmpty()) {
            source.sendFeedback(() -> Text.literal("§cNo mappings configured!"), false);
        } else {
            for (String mapping : BuffMobsMod.CONFIG.mobPresets.mobMapping) {
                String[] parts = mapping.split(":");
                if (parts.length >= 3) {
                    String mobId = parts[0] + ":" + parts[1];
                    String presetName = parts[2];
                    source.sendFeedback(() -> Text.literal("§7- §f" + mobId + " §7→ §e" + presetName), false);
                } else {
                    source.sendFeedback(() -> Text.literal("§c- Invalid: " + mapping), false);
                }
            }
        }

        return 1;
    }

    private static int reloadMobs(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        source.sendFeedback(() -> Text.literal("§6Reapplying buffs to all mobs..."), false);

        int count = 0;
        for (Entity entity : source.getWorld().iterateEntities()) {
            if (entity instanceof MobEntity mob) {
                try {
                    MobBuffUtil.applyBuffs(mob);
                    count++;
                } catch (Exception e) {
                    BuffMobsMod.LOGGER.error("Failed to buff mob", e);
                }
            }
        }

        final int finalCount = count;
        source.sendFeedback(() -> Text.literal("§aBuffed " + finalCount + " mobs!"), false);

        return 1;
    }

    private static int showInfo(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        source.sendFeedback(() -> Text.literal("§6=== BuffMobs Info ==="), false);
        source.sendFeedback(() -> Text.literal("§eEnabled: §f" +
                BuffMobsMod.CONFIG.enabled), false);
        source.sendFeedback(() -> Text.literal("§eInitialized Mobs: §f" +
                MobTickHandler.getInitializedCount()), false);
        source.sendFeedback(() -> Text.literal("§eHealth Mult: §f" +
                BuffMobsMod.CONFIG.attributes.healthMultiplier), false);
        source.sendFeedback(() -> Text.literal("§eDamage Mult: §f" +
                BuffMobsMod.CONFIG.attributes.damageMultiplier), false);
        source.sendFeedback(() -> Text.literal("§eDay Scaling: §f" +
                BuffMobsMod.CONFIG.dayScaling.enabled), false);
        source.sendFeedback(() -> Text.literal("§ePresets: §f" +
                BuffMobsMod.CONFIG.mobPresets.enabled), false);

        source.sendFeedback(() -> Text.literal("§eUse whitelist (mobs): §f" +
                BuffMobsMod.CONFIG.mobFilter.useWhitelist), false);
        source.sendFeedback(() -> Text.literal("§eUse whitelist (mods): §f" +
                BuffMobsMod.CONFIG.modidFilter.useWhitelist), false);
        source.sendFeedback(() -> Text.literal("§eUse whitelist (dims): §f" +
                BuffMobsMod.CONFIG.dimensionFilter.useWhitelist), false);

        return 1;
    }
}