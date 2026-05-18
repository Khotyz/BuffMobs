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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;

import java.util.List;

import static com.khotyz.buffmobs.util.DimensionUtil.getDimensionId;

public class DebugCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext context) {
        dispatcher.register(Commands.literal("buffmobs")
                .then(Commands.literal("debug")  .executes(DebugCommand::debugNearestMob))
                .then(Commands.literal("reload")  .executes(DebugCommand::reloadMobs))
                .then(Commands.literal("info")    .executes(DebugCommand::showInfo))
                .then(Commands.literal("presets") .executes(DebugCommand::showPresets)));
    }

    private static int debugNearestMob(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        if (src.getPlayer() == null) {
            src.sendFailure(Component.translatable("buffmobs.command.debug.error.player_only"));
            return 0;
        }

        AABB box = src.getPlayer().getBoundingBox().inflate(5.0);
        List<Mob> nearby = src.getLevel().getEntitiesOfClass(Mob.class, box, m -> true);
        if (nearby.isEmpty()) {
            src.sendFailure(Component.translatable("buffmobs.command.debug.error.no_mobs"));
            return 0;
        }

        Mob closest = nearby.get(0);
        double cd = src.getPlayer().distanceToSqr(closest);
        for (Mob m : nearby) { double d = src.getPlayer().distanceToSqr(m); if (d < cd) { cd = d; closest = m; } }

        final Mob mob = closest;
        final String mobId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        final String modNs = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).getNamespace();
        final String dimId = getDimensionId(mob.level());

        src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.header"), false);
        src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.mob_id", mobId), false);
        src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.mod_id", modNs), false);
        src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.dimension", dimId), false);

        boolean isValid = MobBuffUtil.isValidMob(mob);
        src.sendSuccess(() -> Component.translatable(
                isValid ? "buffmobs.command.debug.valid.yes" : "buffmobs.command.debug.valid.no"), false);

        src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.preset_header"), false);
        boolean presetsOn = BuffMobsConfig.INSTANCE.mobPresets.enabled;
        src.sendSuccess(() -> Component.translatable(
                presetsOn ? "buffmobs.command.debug.presets_enabled.yes" : "buffmobs.command.debug.presets_enabled.no"), false);

        if (presetsOn) {
            MobPresetUtil.PresetMultipliers preset = MobPresetUtil.getPresetForMob(mob);
            if (preset != null) {
                src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.preset_found.yes"), false);
                src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.preset_hp",        String.format("%.2f", preset.health)),        false);
                src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.preset_dmg",       String.format("%.2f", preset.damage)),        false);
                src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.preset_spd",       String.format("%.2f", preset.speed)),         false);
                src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.preset_aspd",      String.format("%.2f", preset.attackSpeed)),   false);
                src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.preset_armor",     String.format("%.1f", preset.armor)),         false);
                src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.preset_toughness", String.format("%.1f", preset.armorToughness)), false);
            } else {
                src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.preset_found.no"), false);
                for (String mapping : BuffMobsConfig.INSTANCE.mobPresets.mobMapping) {
                    src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.preset_mapping_entry", mapping), false);
                }
                src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.preset_mapping_hint", mobId), false);
            }
        }

        src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.scaling_header"), false);
        final String dayMultStr = String.format("%.2f", MobBuffUtil.getDayMultiplier(mob.level().getOverworldClockTime()));
        src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.day_mult", dayMultStr), false);
        final MobBuffUtil.DimensionMultipliers dm = MobBuffUtil.getDimensionMultipliers(mob);
        final String dimHp  = String.format("%.2f", dm.health);
        final String dimDmg = String.format("%.2f", dm.damage);
        src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.dim_hp_mult",  dimHp),  false);
        src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.dim_dmg_mult", dimDmg), false);

        src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.stats_header"), false);
        AttributeInstance hp = mob.getAttribute(Attributes.MAX_HEALTH);
        if (hp != null) {
            final String hpCur  = String.format("%.1f", mob.getHealth());
            final String hpMax  = String.format("%.1f", hp.getValue());
            final String hpBase = String.format("%.1f", hp.getBaseValue());
            src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.health", hpCur, hpMax, hpBase), false);
        }
        AttributeInstance dmg = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmg != null) {
            final String dmgVal  = String.format("%.1f", dmg.getValue());
            final String dmgBase = String.format("%.1f", dmg.getBaseValue());
            src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.damage", dmgVal, dmgBase), false);
        }
        src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.effects_count",
                String.valueOf(mob.getActiveEffects().size())), false);
        mob.getActiveEffects().forEach(eff ->
                src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.effect_entry",
                        Component.translatable(eff.getEffect().value().getDescriptionId()).getString(),
                        String.valueOf(eff.getAmplifier() + 1)), false));

        return 1;
    }

    private static int showPresets(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        src.sendSuccess(() -> Component.translatable("buffmobs.command.presets.header"), false);
        boolean on = BuffMobsConfig.INSTANCE.mobPresets.enabled;
        src.sendSuccess(() -> Component.translatable(
                on ? "buffmobs.command.presets.enabled.yes" : "buffmobs.command.presets.enabled.no"), false);
        if (!on) return 1;

        BuffMobsConfig.MobPresets.PresetSlot[] presets = {
                BuffMobsConfig.INSTANCE.mobPresets.preset1, BuffMobsConfig.INSTANCE.mobPresets.preset2,
                BuffMobsConfig.INSTANCE.mobPresets.preset3, BuffMobsConfig.INSTANCE.mobPresets.preset4,
                BuffMobsConfig.INSTANCE.mobPresets.preset5
        };
        int[] n = {1};
        for (BuffMobsConfig.MobPresets.PresetSlot p : presets) {
            if (p.presetName != null && !p.presetName.isEmpty()) {
                int num = n[0];
                final String entry = String.format("%d. %s (HP:%.1fx DMG:%.1fx SPD:%.1fx ASPD:%.1fx ARM:+%.0f TOUGH:+%.0f)",
                        num, p.presetName,
                        p.healthMultiplier, p.damageMultiplier,
                        p.speedMultiplier, p.attackSpeedMultiplier,
                        p.armorAddition, p.armorToughnessAddition);
                src.sendSuccess(() -> Component.literal(entry), false);
            }
            n[0]++;
        }

        src.sendSuccess(() -> Component.translatable("buffmobs.command.presets.mappings_header"), false);
        for (String mapping : BuffMobsConfig.INSTANCE.mobPresets.mobMapping) {
            String[] parts = mapping.split(":");
            if (parts.length >= 3) {
                final String mid = parts[0] + ":" + parts[1];
                final String pn  = parts[2];
                src.sendSuccess(() -> Component.translatable("buffmobs.command.presets.mapping_entry", mid, pn), false);
            } else {
                src.sendSuccess(() -> Component.translatable("buffmobs.command.presets.mapping_invalid", mapping), false);
            }
        }
        return 1;
    }

    private static int reloadMobs(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        BuffMobsConfig.load();
        src.sendSuccess(() -> Component.translatable("buffmobs.command.reload.start"), false);
        int count = 0;
        MobTickHandler.forceReinitAll();
        for (Entity e : src.getLevel().getAllEntities()) {
            if (e instanceof Mob mob) {
                try {
                    MobBuffUtil.removeAllModifiers(mob);
                    MobBuffUtil.removeAllBuffEffects(mob);
                    MobBuffUtil.applyBuffs(mob);
                    count++;
                } catch (Exception ex) {
                    BuffMobsMod.LOGGER.error("Failed to buff mob", ex);
                }
            }
        }
        final int fc = count;
        src.sendSuccess(() -> Component.translatable("buffmobs.command.reload.done", String.valueOf(fc)), false);
        return 1;
    }

    private static int showInfo(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        src.sendSuccess(() -> Component.translatable("buffmobs.command.info.header"), false);
        src.sendSuccess(() -> Component.translatable("buffmobs.command.info.enabled",     String.valueOf(BuffMobsConfig.INSTANCE.enabled)),                              false);
        src.sendSuccess(() -> Component.translatable("buffmobs.command.info.initialized", String.valueOf(MobTickHandler.getInitializedCount())),                         false);
        src.sendSuccess(() -> Component.translatable("buffmobs.command.info.health_mult", String.valueOf(BuffMobsConfig.INSTANCE.attributes.healthMultiplier)),          false);
        src.sendSuccess(() -> Component.translatable("buffmobs.command.info.damage_mult", String.valueOf(BuffMobsConfig.INSTANCE.attributes.damageMultiplier)),          false);
        src.sendSuccess(() -> Component.translatable("buffmobs.command.info.day_scaling", String.valueOf(BuffMobsConfig.INSTANCE.dayScaling.enabled)),                   false);
        src.sendSuccess(() -> Component.translatable("buffmobs.command.info.presets",     String.valueOf(BuffMobsConfig.INSTANCE.mobPresets.enabled)),                   false);
        return 1;
    }
}