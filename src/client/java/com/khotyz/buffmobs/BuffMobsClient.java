package com.khotyz.buffmobs;

import com.khotyz.buffmobs.config.BuffMobsConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class BuffMobsClient implements ClientModInitializer {

    private static KeyMapping openConfigKey;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "config"));
        openConfigKey = KeyBindingHelper.registerKeyBinding(
                new KeyMapping("buffmobs.key.open_config", GLFW.GLFW_KEY_UNKNOWN, category));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openConfigKey.consumeClick()) {
                client.setScreen(new BuffMobsConfigScreen(client.screen));
            }
        });

        BuffMobsMod.LOGGER.info("[BuffMobs] Config screen registered (Mod Menu button or the BuffMobs key binding)");
    }
}
