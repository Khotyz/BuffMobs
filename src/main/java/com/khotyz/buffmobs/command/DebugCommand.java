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
import net.minecraft.resources.Identifier;
import static com.khotyz.buffmobs.util.DimensionUtil.getDimensionId;
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

    private static int debugNearestMob(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        if (src.getPlayer() == null) { src.sendFailure(Component.literal("Must be a player!")); return 0; }

        AABB box = src.getPlayer().getBoundingBox().inflate(5.0);
        List<Mob> nearby = src.getLevel().getEntitiesOfClass(Mob.class, box, m -> true);
        if (nearby.isEmpty()) { src.sendFailure(Component.literal("No mobs nearby!")); return 0; }

        Mob closest = nearby.get(0);
        double cd = src.getPlayer().distanceToSqr(closest);
        for (Mob m : nearby) { double d = src.getPlayer().distanceToSqr(m); if (d < cd) { cd = d; closest = m; } }

        final Mob mob  = closest;
        // Extract strings before lambdas — must be effectively final
        final String mobId  = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        final String modNs  = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).getNamespace();
        final String dimId  = getDimensionId(mob.level());

        src.sendSuccess(() -> Component.literal("§6=== BuffMobs Debug ==="), false);
        src.sendSuccess(() -> Component.literal("§eMob ID: §f"    + mobId),  false);
        src.sendSuccess(() -> Component.literal("§eMod ID: §f"    + modNs),  false);
        src.sendSuccess(() -> Component.literal("§eDimension: §f" + dimId),  false);

        boolean isValid = MobBuffUtil.isValidMob(mob);
        src.sendSuccess(() -> Component.literal("§eValid: §f" + (isValid ? "§aYES" : "§cNO")), false);

        src.sendSuccess(() -> Component.literal("§6=== Preset System ==="), false);
        boolean presetsOn = BuffMobsConfig.INSTANCE.mobPresets.enabled.get();
        src.sendSuccess(() -> Component.literal("§ePresets Enabled: §f" + (presetsOn ? "§aYES" : "§cNO")), false);

        if (presetsOn) {
            MobPresetUtil.PresetMultipliers preset = MobPresetUtil.getPresetForMob(mob);
            if (preset != null) {
                src.sendSuccess(() -> Component.literal("§ePreset Found: §aYES"),                                   false);
                src.sendSuccess(() -> Component.literal("§eHP: §f"   + preset.health      + "x"),                  false);
                src.sendSuccess(() -> Component.literal("§eDMG: §f"  + preset.damage      + "x"),                  false);
                src.sendSuccess(() -> Component.literal("§eSPD: §f"  + preset.speed       + "x"),                  false);
                src.sendSuccess(() -> Component.literal("§eASPD: §f" + preset.attackSpeed + "x"),                  false);
                src.sendSuccess(() -> Component.literal("§eArmor: §f+"    + preset.armor),                         false);
                src.sendSuccess(() -> Component.literal("§eToughness: §f+" + preset.armorToughness),               false);
            } else {
                src.sendSuccess(() -> Component.literal("§ePreset Found: §cNO — using dimension/default"), false);
                for (String mapping : BuffMobsConfig.INSTANCE.mobPresets.mobMapping.get()) {
                    src.sendSuccess(() -> Component.literal("§7  - " + mapping), false);
                }
                src.sendSuccess(() -> Component.literal("§7To add: " + mobId + ":preset_name"), false);
            }
        }

        src.sendSuccess(() -> Component.literal("§6=== Scaling ==="), false);
        final double dayMult = MobBuffUtil.getDayMultiplier(mob.level().getDayTime());
        src.sendSuccess(() -> Component.literal("§eDay Mult: §f" + String.format("%.2f", dayMult)), false);
        final MobBuffUtil.DimensionMultipliers dm = MobBuffUtil.getDimensionMultipliers(mob);
        src.sendSuccess(() -> Component.literal("§eDim HP Mult: §f"  + dm.health), false);
        src.sendSuccess(() -> Component.literal("§eDim DMG Mult: §f" + dm.damage), false);

        src.sendSuccess(() -> Component.literal("§6=== Current Stats ==="), false);
        AttributeInstance hp = mob.getAttribute(Attributes.MAX_HEALTH);
        if (hp != null) {
            src.sendSuccess(() -> Component.literal("§eHealth: §f" +
                    String.format("%.1f / %.1f (base %.1f)", mob.getHealth(), hp.getValue(), hp.getBaseValue())), false);
        }
        AttributeInstance dmg = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmg != null) {
            src.sendSuccess(() -> Component.literal("§eDamage: §f" +
                    String.format("%.1f (base %.1f)", dmg.getValue(), dmg.getBaseValue())), false);
        }
        src.sendSuccess(() -> Component.literal("§eEffects: §f" + mob.getActiveEffects().size()), false);
        mob.getActiveEffects().forEach(eff ->
                src.sendSuccess(() -> Component.literal("  §7- " +
                        eff.getEffect().value().getDescriptionId() + " " + (eff.getAmplifier() + 1)), false));

        return 1;
    }

    private static int showPresets(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        src.sendSuccess(() -> Component.literal("§6=== BuffMobs Presets ==="), false);
        boolean on = BuffMobsConfig.INSTANCE.mobPresets.enabled.get();
        src.sendSuccess(() -> Component.literal("§eEnabled: §f" + (on ? "§aYES" : "§cNO")), false);
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
                src.sendSuccess(() -> Component.literal(String.format(
                        "§e%d. §f%s §7(HP:%.1fx DMG:%.1fx SPD:%.1fx ASPD:%.1fx ARM:+%.0f TOUGH:+%.0f)",
                        num, p.presetName.get(), p.healthMultiplier.get(), p.damageMultiplier.get(),
                        p.speedMultiplier.get(), p.attackSpeedMultiplier.get(),
                        p.armorAddition.get(), p.armorToughnessAddition.get())), false);
            }
            n[0]++;
        }

        src.sendSuccess(() -> Component.literal("§6=== Mob Mappings ==="), false);
        for (String mapping : BuffMobsConfig.INSTANCE.mobPresets.mobMapping.get()) {
            String[] parts = mapping.split(":");
            if (parts.length >= 3) {
                final String mid = parts[0] + ":" + parts[1];
                final String pn  = parts[2];
                src.sendSuccess(() -> Component.literal("§7- §f" + mid + " §7-> §e" + pn), false);
            } else {
                src.sendSuccess(() -> Component.literal("§c- Invalid: " + mapping), false);
            }
        }
        return 1;
    }

    private static int reloadMobs(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        src.sendSuccess(() -> Component.literal("§6Reapplying buffs to all mobs..."), false);
        int count = 0;
        for (Entity e : src.getLevel().getAllEntities()) {
            if (e instanceof Mob mob) {
                try { MobBuffUtil.applyBuffs(mob); count++; }
                catch (Exception ex) { BuffMobsMod.LOGGER.error("Failed to buff mob", ex); }
            }
        }
        final int fc = count;
        src.sendSuccess(() -> Component.literal("§aBuffed " + fc + " mobs!"), false);
        return 1;
    }

    private static int showInfo(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        src.sendSuccess(() -> Component.literal("§6=== BuffMobs Info ==="), false);
        src.sendSuccess(() -> Component.literal("§eEnabled: §f"          + BuffMobsConfig.INSTANCE.enabled.get()), false);
        src.sendSuccess(() -> Component.literal("§eInitialized Mobs: §f" + MobTickHandler.getInitializedCount()), false);
        src.sendSuccess(() -> Component.literal("§eHealth Mult: §f"      + BuffMobsConfig.INSTANCE.attributes.healthMultiplier.get()), false);
        src.sendSuccess(() -> Component.literal("§eDamage Mult: §f"      + BuffMobsConfig.INSTANCE.attributes.damageMultiplier.get()), false);
        src.sendSuccess(() -> Component.literal("§eDay Scaling: §f"      + BuffMobsConfig.INSTANCE.dayScaling.enabled.get()), false);
        src.sendSuccess(() -> Component.literal("§ePresets: §f"          + BuffMobsConfig.INSTANCE.mobPresets.enabled.get()), false);
        return 1;
    }
}