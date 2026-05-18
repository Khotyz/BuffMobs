package com.khotyz.buffmobs.command;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.event.MobTickHandler;
import com.khotyz.buffmobs.util.MobBuffUtil;
import com.khotyz.buffmobs.util.MobPresetUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import static com.khotyz.buffmobs.util.DimensionUtil.getDimensionId;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.List;

public class DebugCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext context, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal("buffmobs")
                .then(Commands.literal("debug")  .executes(DebugCommand::debugNearestMob))
                .then(Commands.literal("reload")  .executes(DebugCommand::reloadMobs))
                .then(Commands.literal("info")    .executes(DebugCommand::showInfo))
                .then(Commands.literal("presets") .executes(DebugCommand::showPresets)));
    }

    private static Component lit(String key) {
        return Component.translatable(key);
    }

    private static Component lit(String key, Object... args) {
        return Component.translatable(key, args);
    }

    private static int debugNearestMob(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        if (src.getPlayer() == null) {
            src.sendFailure(lit("buffmobs.command.debug.error.player_only"));
            return 0;
        }

        AABB box = src.getPlayer().getBoundingBox().inflate(5.0);
        List<Mob> nearby = src.getLevel().getEntitiesOfClass(Mob.class, box, m -> true);
        if (nearby.isEmpty()) {
            src.sendFailure(lit("buffmobs.command.debug.error.no_mobs"));
            return 0;
        }

        Mob closest = nearby.get(0);
        double cd = src.getPlayer().distanceToSqr(closest);
        for (Mob m : nearby) { double d = src.getPlayer().distanceToSqr(m); if (d < cd) { cd = d; closest = m; } }

        final Mob mob = closest;
        final String mobId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        final String modNs = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).getNamespace();
        final String dimId = getDimensionId(mob.level());

        src.sendSuccess(() -> lit("buffmobs.command.debug.header"), false);
        src.sendSuccess(() -> lit("buffmobs.command.debug.mob_id", mobId), false);
        src.sendSuccess(() -> lit("buffmobs.command.debug.mod_id", modNs), false);
        src.sendSuccess(() -> lit("buffmobs.command.debug.dimension", dimId), false);

        boolean isValid = MobBuffUtil.isValidMob(mob);
        src.sendSuccess(() -> lit(isValid
                ? "buffmobs.command.debug.valid.yes"
                : "buffmobs.command.debug.valid.no"), false);

        src.sendSuccess(() -> lit("buffmobs.command.debug.preset_header"), false);
        boolean presetsOn = BuffMobsConfig.INSTANCE.mobPresets.enabled.get();
        src.sendSuccess(() -> lit(presetsOn
                ? "buffmobs.command.debug.presets_enabled.yes"
                : "buffmobs.command.debug.presets_enabled.no"), false);

        if (presetsOn) {
            MobPresetUtil.PresetMultipliers preset = MobPresetUtil.getPresetForMob(mob);
            if (preset != null) {
                src.sendSuccess(() -> lit("buffmobs.command.debug.preset_found.yes"), false);
                src.sendSuccess(() -> lit("buffmobs.command.debug.preset_hp",     String.valueOf(preset.health)),        false);
                src.sendSuccess(() -> lit("buffmobs.command.debug.preset_dmg",    String.valueOf(preset.damage)),        false);
                src.sendSuccess(() -> lit("buffmobs.command.debug.preset_spd",    String.valueOf(preset.speed)),         false);
                src.sendSuccess(() -> lit("buffmobs.command.debug.preset_aspd",   String.valueOf(preset.attackSpeed)),   false);
                src.sendSuccess(() -> lit("buffmobs.command.debug.preset_armor",  String.valueOf(preset.armor)),         false);
                src.sendSuccess(() -> lit("buffmobs.command.debug.preset_toughness", String.valueOf(preset.armorToughness)), false);
            } else {
                src.sendSuccess(() -> lit("buffmobs.command.debug.preset_found.no"), false);
                for (String mapping : BuffMobsConfig.INSTANCE.mobPresets.mobMapping.get()) {
                    src.sendSuccess(() -> lit("buffmobs.command.debug.preset_mapping_entry", mapping), false);
                }
                src.sendSuccess(() -> lit("buffmobs.command.debug.preset_mapping_hint", mobId), false);
            }
        }

        src.sendSuccess(() -> lit("buffmobs.command.debug.scaling_header"), false);
        final double dayMult = MobBuffUtil.getDayMultiplier(MobBuffUtil.getOverworldDayTime(mob.level()));
        src.sendSuccess(() -> lit("buffmobs.command.debug.day_mult", dayMult), false);
        final MobBuffUtil.DimensionMultipliers dm = MobBuffUtil.getDimensionMultipliers(mob);
        src.sendSuccess(() -> lit("buffmobs.command.debug.dim_hp_mult",  String.valueOf(dm.health)), false);
        src.sendSuccess(() -> lit("buffmobs.command.debug.dim_dmg_mult", String.valueOf(dm.damage)), false);

        src.sendSuccess(() -> lit("buffmobs.command.debug.stats_header"), false);
        AttributeInstance hp = mob.getAttribute(Attributes.MAX_HEALTH);
        if (hp != null) {
            src.sendSuccess(() -> lit("buffmobs.command.debug.health",
                    mob.getHealth(), hp.getValue(), hp.getBaseValue()), false);
        }
        AttributeInstance dmg = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmg != null) {
            src.sendSuccess(() -> lit("buffmobs.command.debug.damage",
                    dmg.getValue(), dmg.getBaseValue()), false);
        }
        src.sendSuccess(() -> lit("buffmobs.command.debug.effects_count", mob.getActiveEffects().size()), false);
        mob.getActiveEffects().forEach(eff -> {
            Component effectName = eff.getEffect().value().getDisplayName();
            int level = eff.getAmplifier() + 1;
            src.sendSuccess(() -> lit("buffmobs.command.debug.effect_entry",
                    effectName, level), false);
        });

        return 1;
    }

    private static int showPresets(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        src.sendSuccess(() -> lit("buffmobs.command.presets.header"), false);
        boolean on = BuffMobsConfig.INSTANCE.mobPresets.enabled.get();
        src.sendSuccess(() -> lit(on
                ? "buffmobs.command.presets.enabled.yes"
                : "buffmobs.command.presets.enabled.no"), false);
        if (!on) return 1;

        BuffMobsConfig.MobPresets.PresetSlot[] presets = {
                BuffMobsConfig.INSTANCE.mobPresets.preset1, BuffMobsConfig.INSTANCE.mobPresets.preset2,
                BuffMobsConfig.INSTANCE.mobPresets.preset3, BuffMobsConfig.INSTANCE.mobPresets.preset4,
                BuffMobsConfig.INSTANCE.mobPresets.preset5
        };
        int[] n = {1};
        for (BuffMobsConfig.MobPresets.PresetSlot p : presets) {
            if (!p.presetName.get().isEmpty()) {
                int num = n[0];
                src.sendSuccess(() -> lit("buffmobs.command.presets.entry",
                        num, p.presetName.get(),
                        p.healthMultiplier.get(), p.damageMultiplier.get(),
                        p.speedMultiplier.get(), p.attackSpeedMultiplier.get(),
                        p.armorAddition.get(), p.armorToughnessAddition.get()), false);
            }
            n[0]++;
        }

        src.sendSuccess(() -> lit("buffmobs.command.presets.mappings_header"), false);
        for (String mapping : BuffMobsConfig.INSTANCE.mobPresets.mobMapping.get()) {
            String[] parts = mapping.split(":");
            if (parts.length >= 3) {
                final String mid = parts[0] + ":" + parts[1];
                final String pn  = parts[2];
                src.sendSuccess(() -> lit("buffmobs.command.presets.mapping_entry", mid, pn), false);
            } else {
                src.sendSuccess(() -> lit("buffmobs.command.presets.mapping_invalid", mapping), false);
            }
        }
        return 1;
    }

    private static int reloadMobs(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        src.sendSuccess(() -> lit("buffmobs.command.reload.start"), false);
        int count = 0;
        for (Entity e : src.getLevel().getAllEntities()) {
            if (e instanceof Mob mob) {
                try { MobBuffUtil.applyBuffs(mob); count++; }
                catch (Exception ex) { BuffMobsMod.LOGGER.error("Failed to buff mob", ex); }
            }
        }
        final int fc = count;
        src.sendSuccess(() -> lit("buffmobs.command.reload.done", fc), false);
        return 1;
    }

    private static int showInfo(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        src.sendSuccess(() -> lit("buffmobs.command.info.header"), false);
        src.sendSuccess(() -> lit("buffmobs.command.info.enabled",      BuffMobsConfig.INSTANCE.enabled.get()), false);
        src.sendSuccess(() -> lit("buffmobs.command.info.initialized",  MobTickHandler.getInitializedCount()), false);
        src.sendSuccess(() -> lit("buffmobs.command.info.health_mult",  BuffMobsConfig.INSTANCE.attributes.healthMultiplier.get()), false);
        src.sendSuccess(() -> lit("buffmobs.command.info.damage_mult",  BuffMobsConfig.INSTANCE.attributes.damageMultiplier.get()), false);
        src.sendSuccess(() -> lit("buffmobs.command.info.day_scaling",  BuffMobsConfig.INSTANCE.dayScaling.enabled.get()), false);
        src.sendSuccess(() -> lit("buffmobs.command.info.presets",      BuffMobsConfig.INSTANCE.mobPresets.enabled.get()), false);
        return 1;
    }
}