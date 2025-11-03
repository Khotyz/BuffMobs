package com.khotyz.buffmobs;

import com.khotyz.buffmobs.command.DebugCommand;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.event.MobEventHandler;
import com.khotyz.buffmobs.event.MobTickHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(BuffMobsMod.MOD_ID)
public class BuffMobsMod {
    public static final String MOD_ID = "buffmobs";
    public static final Logger LOGGER = LoggerFactory.getLogger("BuffMobs");

    public static BuffMobsConfig CONFIG;

    public BuffMobsMod(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, BuffMobsConfig.SPEC);

        MobEventHandler handler = new MobEventHandler();
        MobTickHandler.register();

        NeoForge.EVENT_BUS.addListener((LivingDamageEvent.Post event) -> {
            handler.onLivingDamage(event.getEntity(), event.getSource(), event.getNewDamage());
        });

        NeoForge.EVENT_BUS.addListener((ServerTickEvent.Post event) -> {
            event.getServer().getAllLevels().forEach(handler::onWorldTick);
        });

        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> {
            DebugCommand.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
        });

        LOGGER.info("BuffMobs initialized successfully");
    }
}