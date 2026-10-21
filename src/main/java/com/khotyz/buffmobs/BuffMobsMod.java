package com.khotyz.buffmobs;

import com.khotyz.buffmobs.command.DebugCommand;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.event.AdaptiveDifficultyHandler;
import com.khotyz.buffmobs.event.BloodMoonHandler;
import com.khotyz.buffmobs.event.LootXpScalingHandler;
import com.khotyz.buffmobs.event.MobEventHandler;
import com.khotyz.buffmobs.event.MobTickHandler;
import com.khotyz.buffmobs.event.PackAggressionHandler;
import com.khotyz.buffmobs.event.SpecialAbilityHandler;
import com.khotyz.buffmobs.event.TraitsHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuffMobsMod implements ModInitializer {
    public static final String MOD_ID = "buffmobs";
    public static final Logger LOGGER = LoggerFactory.getLogger("BuffMobs");

    @Override
    public void onInitialize() {
        BuffMobsConfig.load();

        MobEventHandler.register();
        MobTickHandler.register();
        TraitsHandler.register();
        PackAggressionHandler.register();
        SpecialAbilityHandler.register();
        BloodMoonHandler.register();
        AdaptiveDifficultyHandler.register();
        LootXpScalingHandler.register();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                DebugCommand.register(dispatcher, registryAccess));

        LOGGER.info("BuffMobs initialized successfully");
    }
}
