package com.khotyz.buffmobs;

import com.khotyz.buffmobs.config.ConfigScreenFactory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = BuffMobsMod.MOD_ID, dist = Dist.CLIENT)
public class BuffMobsClient {

    public BuffMobsClient(IEventBus modBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class,
                (modContainer, parent) -> ConfigScreenFactory.create(parent));
    }
}
