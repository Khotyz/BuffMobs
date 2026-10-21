package com.khotyz.buffmobs.config;

import net.minecraft.client.gui.screens.Screen;

public final class ConfigScreenFactory {

    private ConfigScreenFactory() {
    }

    public static Screen create(Screen parent) {
        return new BuffMobsConfigScreen(parent);
    }
}
