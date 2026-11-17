package com.khotyz.buffmobs.command;

import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.event.EliteMobHandler;
import com.khotyz.buffmobs.event.MobTickHandler;
import com.khotyz.buffmobs.event.SpecialAbilityHandler;
import com.khotyz.buffmobs.event.TraitsHandler;
import com.khotyz.buffmobs.util.MobBuffUtil;
import com.khotyz.buffmobs.util.MobPresetUtil;
import com.khotyz.buffmobs.util.ResetManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
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
                .then(Commands.literal("presets") .executes(DebugCommand::showPresets))
                .then(ResetCommand.node()));
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
        final String dayMultStr = String.format("%.2f", MobBuffUtil.getDayMultiplier(mob.level()));
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
        src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.elite",
                yesNo(EliteMobHandler.isElite(mob))), false);
        TraitsHandler.Trait trait = TraitsHandler.getTrait(mob);
        src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.trait",
                trait != null ? Component.translatable(trait.translationKey())
                        : Component.translatable("buffmobs.command.debug.none")), false);
        Component abilityNames = SpecialAbilityHandler.getAbilities(mob).stream()
                .map(ability -> (Component) Component.translatable(ability.translationKey()))
                .reduce((first, second) -> first.copy().append(", ").append(second))
                .orElse(Component.translatable("buffmobs.command.debug.none"));
        src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.abilities", abilityNames), false);
        src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.effects_count",
                String.valueOf(mob.getActiveEffects().size())), false);
        mob.getActiveEffects().forEach(eff ->
                src.sendSuccess(() -> Component.translatable("buffmobs.command.debug.effect_entry",
                        Component.translatable(eff.getEffect().value().getDescriptionId()),
                        String.valueOf(eff.getAmplifier() + 1)), false));

        return 1;
    }

    private static Component yesNo(boolean value) {
        return Component.translatable(value ? "buffmobs.command.debug.yes" : "buffmobs.command.debug.no");
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
        for (int index = 0; index < presets.length; index++) {
            BuffMobsConfig.MobPresets.PresetSlot p = presets[index];
            String effectiveName = MobPresetUtil.effectivePresetName(p, index);
            if (!effectiveName.isEmpty()) {
                final int slotNumber = index + 1;
                src.sendSuccess(() -> Component.translatable("buffmobs.command.presets.entry",
                        slotNumber, effectiveName,
                        String.format("%.1f", p.healthMultiplier),
                        String.format("%.1f", p.damageMultiplier),
                        String.format("%.1f", p.speedMultiplier),
                        String.format("%.1f", p.attackSpeedMultiplier),
                        String.format("%.0f", p.armorAddition),
                        String.format("%.0f", p.armorToughnessAddition)), false);
            }
        }

        src.sendSuccess(() -> Component.translatable("buffmobs.command.presets.mappings_header"), false);
        for (String mapping : BuffMobsConfig.INSTANCE.mobPresets.mobMapping) {
            if (mapping == null) continue;
            String trimmed = mapping.trim();
            if (trimmed.isEmpty()) continue;
            String[] parts = trimmed.split(":");
            if (parts.length >= 3) {
                final String mid = parts[0].trim() + ":" + parts[1].trim();
                final String pn  = parts[2].trim();
                src.sendSuccess(() -> Component.translatable("buffmobs.command.presets.mapping_entry", mid, pn), false);
            } else {
                src.sendSuccess(() -> Component.translatable("buffmobs.command.presets.mapping_invalid", trimmed), false);
            }
        }
        return 1;
    }

    private static int reloadMobs(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        BuffMobsConfig.load();
        src.sendSuccess(() -> Component.translatable("buffmobs.command.reload.start"), false);
        int count = ResetManager.resetMobs(src.getServer());
        final int fc = count;
        src.sendSuccess(() -> Component.translatable("buffmobs.command.reload.done", String.valueOf(fc)), false);
        return 1;
    }

    private static int showInfo(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        src.sendSuccess(() -> Component.translatable("buffmobs.command.info.header"), false);
        src.sendSuccess(() -> Component.translatable("buffmobs.command.info.enabled",           yesNo(BuffMobsConfig.INSTANCE.enabled)),                     false);
        src.sendSuccess(() -> Component.translatable("buffmobs.command.info.initialized",       String.valueOf(MobTickHandler.getInitializedCount())),       false);
        src.sendSuccess(() -> Component.translatable("buffmobs.command.info.health_mult",       String.valueOf(BuffMobsConfig.INSTANCE.attributes.healthMultiplier)), false);
        src.sendSuccess(() -> Component.translatable("buffmobs.command.info.damage_mult",       String.valueOf(BuffMobsConfig.INSTANCE.attributes.damageMultiplier)), false);
        src.sendSuccess(() -> Component.translatable("buffmobs.command.info.day_scaling",       yesNo(BuffMobsConfig.INSTANCE.dayScaling.enabled)),          false);
        src.sendSuccess(() -> Component.translatable("buffmobs.command.info.presets",           yesNo(BuffMobsConfig.INSTANCE.mobPresets.enabled)),          false);
        src.sendSuccess(() -> Component.translatable("buffmobs.command.info.buff_neutral_mobs", yesNo(BuffMobsConfig.INSTANCE.mobFilter.buffNeutralMobs)),   false);
        return 1;
    }
}