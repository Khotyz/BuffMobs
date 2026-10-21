package com.khotyz.buffmobs.command;

import com.khotyz.buffmobs.util.ResetManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class ResetCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("buffmobs")
                .then(Commands.literal("reset")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.literal("all")
                                .executes(ResetCommand::requireConfirmation)
                                .then(Commands.literal("confirm").executes(ResetCommand::resetAll)))
                        .then(Commands.literal("scaling")
                                .executes(ResetCommand::requireConfirmation)
                                .then(Commands.literal("confirm").executes(ResetCommand::resetScaling)))
                        .then(Commands.literal("adaptive")
                                .executes(ResetCommand::requireConfirmation)
                                .then(Commands.literal("confirm").executes(ResetCommand::resetAdaptive)))
                        .then(Commands.literal("bloodmoon")
                                .executes(ResetCommand::requireConfirmation)
                                .then(Commands.literal("confirm").executes(ResetCommand::resetBloodMoon)))
                        .then(Commands.literal("mobs").executes(ResetCommand::resetMobs))));
    }

    private static int requireConfirmation(CommandContext<CommandSourceStack> context) {
        context.getSource().sendFailure(Component.translatable("buffmobs.command.reset.confirm_required"));
        return 0;
    }

    private static int resetAll(CommandContext<CommandSourceStack> context) {
        MinecraftServer server = context.getSource().getServer();
        int count = ResetManager.resetAll(server);
        context.getSource().sendSuccess(
                () -> Component.translatable("buffmobs.command.reset.all.done", count), true);
        return 1;
    }

    private static int resetScaling(CommandContext<CommandSourceStack> context) {
        MinecraftServer server = context.getSource().getServer();
        int count = ResetManager.resetScaling(server);
        context.getSource().sendSuccess(
                () -> Component.translatable("buffmobs.command.reset.scaling.done", count), true);
        return 1;
    }

    private static int resetAdaptive(CommandContext<CommandSourceStack> context) {
        MinecraftServer server = context.getSource().getServer();
        int count = ResetManager.resetAdaptive(server);
        context.getSource().sendSuccess(
                () -> Component.translatable("buffmobs.command.reset.adaptive.done", count), true);
        return 1;
    }

    private static int resetBloodMoon(CommandContext<CommandSourceStack> context) {
        MinecraftServer server = context.getSource().getServer();
        int count = ResetManager.resetBloodMoon(server);
        context.getSource().sendSuccess(
                () -> Component.translatable("buffmobs.command.reset.bloodmoon.done", count), true);
        return 1;
    }

    private static int resetMobs(CommandContext<CommandSourceStack> context) {
        MinecraftServer server = context.getSource().getServer();
        int count = ResetManager.resetMobs(server);
        context.getSource().sendSuccess(
                () -> Component.translatable("buffmobs.command.reset.mobs.done", count), true);
        return 1;
    }
}
