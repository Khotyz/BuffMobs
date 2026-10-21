package com.khotyz.buffmobs;

import com.khotyz.buffmobs.command.DebugCommand;
import com.khotyz.buffmobs.command.ResetCommand;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.event.AdaptiveDifficultyHandler;
import com.khotyz.buffmobs.event.BloodMoonHandler;
import com.khotyz.buffmobs.event.EliteMobHandler;
import com.khotyz.buffmobs.event.LootXpScalingHandler;
import com.khotyz.buffmobs.event.MobEventHandler;
import com.khotyz.buffmobs.event.MobTickHandler;
import com.khotyz.buffmobs.event.PackAggressionHandler;
import com.khotyz.buffmobs.event.PassiveMobAggressionHandler;
import com.khotyz.buffmobs.event.SpecialAbilityHandler;
import com.khotyz.buffmobs.event.TraitsHandler;
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
        NeoForge.EVENT_BUS.register(new EliteMobHandler());
        NeoForge.EVENT_BUS.register(new TraitsHandler());
        NeoForge.EVENT_BUS.register(new LootXpScalingHandler());
        NeoForge.EVENT_BUS.register(new PackAggressionHandler());
        NeoForge.EVENT_BUS.register(new SpecialAbilityHandler());
        NeoForge.EVENT_BUS.register(new BloodMoonHandler());
        NeoForge.EVENT_BUS.register(new AdaptiveDifficultyHandler());
        NeoForge.EVENT_BUS.register(DebugCommand.class);
        NeoForge.EVENT_BUS.register(ResetCommand.class);
        PassiveMobAggressionHandler.register();

        LOGGER.info("BuffMobs initialized successfully");
    }
}