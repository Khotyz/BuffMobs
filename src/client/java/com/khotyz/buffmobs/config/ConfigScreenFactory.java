package com.khotyz.buffmobs.config;

import com.khotyz.buffmobs.BuffMobsMod;
import net.minecraft.client.gui.screens.Screen;

public class ConfigScreenFactory {

    public static boolean isAvailable() {
        return true;
    }

    public static Screen create(Screen parent) {
        try {
            return new BuffMobsConfigScreen(parent);
        } catch (Exception e) {
            BuffMobsMod.LOGGER.error("[BuffMobs] Failed to create config screen", e);
            return null;
        }
    }
}
