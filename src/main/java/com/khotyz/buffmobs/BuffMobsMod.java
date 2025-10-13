package com.khotyz.buffmobs;

import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.event.MobEventHandler;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuffMobsMod implements ModInitializer {
    public static final String MOD_ID = "buffmobs";
    public static final Logger LOGGER = LoggerFactory.getLogger("BuffMobs");

    public static BuffMobsConfig CONFIG;

    @Override
    public void onInitialize() {
        AutoConfig.register(BuffMobsConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(BuffMobsConfig.class).getConfig();

        MobEventHandler handler = new MobEventHandler();

        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamage, damageTaken, blocked) ->
                handler.onLivingDamage(entity, source, damageTaken));
        ServerTickEvents.END_WORLD_TICK.register(handler::onWorldTick);
        AttackEntityCallback.EVENT.register(handler::onPlayerAttack);

        LOGGER.info("BuffMobs initialized successfully");
    }
}