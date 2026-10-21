package com.khotyz.buffmobs;

import com.khotyz.buffmobs.config.ConfigScreenFactory;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class BuffMobsClient implements ClientModInitializer {

    private static KeyMapping openConfigKey;

    @Override
    public void onInitializeClient() {
        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.buffmobs.open_config",
                GLFW.GLFW_KEY_F9,
                "key.categories.buffmobs"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openConfigKey.consumeClick()) {
                if (client.screen == null) {
                    client.setScreen(ConfigScreenFactory.create(null));
                }
            }
        });

        BuffMobsMod.LOGGER.info("[BuffMobs] Config screen registered");
    }
}
