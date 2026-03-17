package com.khotyz.buffmobs;

import com.khotyz.buffmobs.config.ConfigScreenFactory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = BuffMobsMod.MOD_ID, dist = Dist.CLIENT)
public class BuffMobsClient {

    public BuffMobsClient(IEventBus modBus, ModContainer container) {
        if (ConfigScreenFactory.isAvailable()) {
            container.registerExtensionPoint(
                    net.neoforged.neoforge.client.gui.IConfigScreenFactory.class,
                    (mc, parent) -> ConfigScreenFactory.create(parent)
            );
            BuffMobsMod.LOGGER.info("[BuffMobs] Config screen registered via Cloth Config");
        }
    }
}
