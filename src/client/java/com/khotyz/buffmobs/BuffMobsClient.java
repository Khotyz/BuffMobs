package com.khotyz.buffmobs;

import com.khotyz.buffmobs.config.ConfigScreenFactory;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BuffMobsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        if (ConfigScreenFactory.isAvailable()) {
            BuffMobsMod.LOGGER.info("[BuffMobs] Config screen registered via Cloth Config");
        }
    }
}
