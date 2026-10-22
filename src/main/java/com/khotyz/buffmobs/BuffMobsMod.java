package com.khotyz.buffmobs;

import com.khotyz.buffmobs.command.DebugCommand;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.event.AdaptiveDifficultyHandler;
import com.khotyz.buffmobs.event.BloodMoonHandler;
import com.khotyz.buffmobs.event.LootXpScalingHandler;
import com.khotyz.buffmobs.event.MobEventHandler;
import com.khotyz.buffmobs.event.MobTickHandler;
import com.khotyz.buffmobs.event.PassiveMobAggressionHandler;
import com.khotyz.buffmobs.util.Guard;
import com.khotyz.buffmobs.util.ModConfigCompat;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(BuffMobsMod.MOD_ID)
public class BuffMobsMod {
    public static final String MOD_ID = "buffmobs";
    public static final Logger LOGGER = LoggerFactory.getLogger("BuffMobs");

    public BuffMobsMod(IEventBus modBus, ModContainer container) {
        ModConfigCompat.register(container, BuffMobsConfig.SPEC, "buffmobs.toml");

        safely(() -> NeoForge.EVENT_BUS.register(new MobEventHandler()), "mob combat events");
        safely(() -> NeoForge.EVENT_BUS.register(new MobTickHandler()), "mob lifecycle events");
        safely(() -> NeoForge.EVENT_BUS.register(new BloodMoonHandler()), "blood moon events");
        safely(() -> NeoForge.EVENT_BUS.register(new AdaptiveDifficultyHandler()), "adaptive difficulty events");
        safely(() -> NeoForge.EVENT_BUS.register(new LootXpScalingHandler()), "loot and xp scaling events");
        safely(() -> NeoForge.EVENT_BUS.register(DebugCommand.class), "the debug command listener");
        safely(PassiveMobAggressionHandler::register, "passive mob aggression");

        LOGGER.info("BuffMobs initialized successfully");
    }

    private static void safely(Runnable registration, String feature) {
        try {
            registration.run();
        } catch (Throwable throwable) {
            Guard.report("Registering " + feature, throwable);
        }
    }
}