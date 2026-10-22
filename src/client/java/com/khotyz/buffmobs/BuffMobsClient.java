package com.khotyz.buffmobs;

import com.khotyz.buffmobs.config.BuffMobsConfigScreen;
import com.khotyz.buffmobs.config.MinecraftScreenCompat;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class BuffMobsClient implements ClientModInitializer {

    private static KeyMapping openConfigKey;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "config"));
        openConfigKey = KeyMappingHelper.registerKeyMapping(
                new KeyMapping("buffmobs.key.open_config", InputConstants.UNKNOWN.getValue(), category));
        openConfigKey.setKey(InputConstants.UNKNOWN);
        KeyMapping.resetMapping();

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            if (openConfigKey.isDefault()) {
                openConfigKey.setKey(InputConstants.UNKNOWN);
                KeyMapping.resetMapping();
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openConfigKey.consumeClick()) {
                BuffMobsConfigScreen screen = new BuffMobsConfigScreen(MinecraftScreenCompat.current(client));
                MinecraftScreenCompat.set(client, screen);
            }
        });

        BuffMobsMod.LOGGER.info("[BuffMobs] Config screen registered (Mod Menu button or the BuffMobs key binding)");
    }
}
