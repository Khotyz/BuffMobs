package com.khotyz.buffmobs.config;

import com.khotyz.buffmobs.util.Guard;
import net.minecraft.client.gui.screens.Screen;

public final class ConfigScreenFactory {

    private ConfigScreenFactory() {
    }

    public static boolean isAvailable() {
        try {
            Class.forName("com.khotyz.buffmobs.config.BuffMobsConfigScreen", false, ConfigScreenFactory.class.getClassLoader());
            return true;
        } catch (Throwable throwable) {
            Guard.report("BuffMobs config screen availability", throwable);
            return false;
        }
    }

    public static Screen create(Screen parent) {
        try {
            return new BuffMobsConfigScreen(parent);
        } catch (Throwable throwable) {
            Guard.report("BuffMobs config screen", throwable);
            return parent;
        }
    }
}
