package com.khotyz.buffmobs.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;

@Environment(EnvType.CLIENT)
public class ConfigScreenFactory {

    public static boolean isAvailable() {
        return true;
    }

    public static Screen create(Screen parent) {
        return new BuffMobsConfigScreen(parent);
    }
}
