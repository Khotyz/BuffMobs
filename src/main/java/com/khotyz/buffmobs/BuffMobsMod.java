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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(BuffMobsMod.MOD_ID)
public class BuffMobsMod {
    public static final String MOD_ID = "buffmobs";
    public static final Logger LOGGER = LoggerFactory.getLogger("BuffMobs");

    public BuffMobsMod(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, BuffMobsConfig.SPEC, "buffmobs.toml");

        NeoForge.EVENT_BUS.register(new MobEventHandler());
        NeoForge.EVENT_BUS.register(new MobTickHandler());
        NeoForge.EVENT_BUS.register(DebugCommand.class);

        LOGGER.info("BuffMobs initialized successfully");
    }
}
