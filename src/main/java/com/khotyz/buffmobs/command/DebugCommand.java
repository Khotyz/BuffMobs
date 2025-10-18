package com.khotyz.buffmobs.command;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.event.MobTickHandler;
import com.khotyz.buffmobs.util.MobBuffUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
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
                        .executes(DebugCommand::showInfo)));
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

        source.sendFeedback(() -> Text.literal("§6=== BuffMobs Debug ==="), false);
        source.sendFeedback(() -> Text.literal("§eMob: §f" + mob.getType().toString()), false);
        source.sendFeedback(() -> Text.literal("§eMob ID: §f" +
                net.minecraft.registry.Registries.ENTITY_TYPE.getId(mob.getType())), false);
        source.sendFeedback(() -> Text.literal("§eMod ID: §f" +
                net.minecraft.registry.Registries.ENTITY_TYPE.getId(mob.getType()).getNamespace()), false);
        source.sendFeedback(() -> Text.literal("§eDimension: §f" +
                mob.getEntityWorld().getRegistryKey().getValue()), false);

        boolean isValid = MobBuffUtil.isValidMob(mob);
        source.sendFeedback(() -> Text.literal("§eValid for buffs: §f" +
                (isValid ? "§aYES" : "§cNO")), false);

        if (!isValid) {
            source.sendFeedback(() -> Text.literal("§cMob is filtered out!"), false);
        }

        double dayMult = MobBuffUtil.getDayMultiplier(mob.getEntityWorld().getTimeOfDay());
        source.sendFeedback(() -> Text.literal("§eDay Multiplier: §f" +
                String.format("%.2f", dayMult)), false);

        var dimMult = MobBuffUtil.getDimensionMultipliers(mob);
        source.sendFeedback(() -> Text.literal("§eDim Health Mult: §f" + dimMult.health), false);
        source.sendFeedback(() -> Text.literal("§eDim Damage Mult: §f" + dimMult.damage), false);

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

        source.sendFeedback(() -> Text.literal("§eUse whitelist (mobs): §f" +
                BuffMobsMod.CONFIG.mobFilter.useWhitelist), false);
        source.sendFeedback(() -> Text.literal("§eUse whitelist (mods): §f" +
                BuffMobsMod.CONFIG.modidFilter.useWhitelist), false);
        source.sendFeedback(() -> Text.literal("§eUse whitelist (dims): §f" +
                BuffMobsMod.CONFIG.dimensionFilter.useWhitelist), false);

        return 1;
    }
}