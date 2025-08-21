package com.khotyz.buffmobs;

import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.effects.BuffMobEffects;
import com.khotyz.buffmobs.events.DamageHandler;
import com.khotyz.buffmobs.events.DayScalingHandler;
import com.khotyz.buffmobs.events.MobSpawnHandler;
import com.khotyz.buffmobs.events.TickHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuffMobsMod implements ModInitializer {
    public static final String MOD_ID = "buffmobs";
    public static final Logger LOGGER = LoggerFactory.getLogger("BuffMobs");

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing BuffMobs...");

        BuffMobsConfig.initialize();
        BuffMobEffects.initialize();

        DamageHandler damageHandler = new DamageHandler();
        DayScalingHandler dayScalingHandler = new DayScalingHandler();
        MobSpawnHandler mobSpawnHandler = new MobSpawnHandler();
        TickHandler tickHandler = new TickHandler();

        ServerLivingEntityEvents.ALLOW_DAMAGE.register(damageHandler::onLivingDamage);
        ServerTickEvents.START_WORLD_TICK.register(dayScalingHandler::onWorldTick);
        ServerTickEvents.START_SERVER_TICK.register(tickHandler::onServerTick);
        ServerEntityEvents.ENTITY_LOAD.register(mobSpawnHandler::onEntityLoad);

        LOGGER.info("BuffMobs initialized successfully");
    }
}