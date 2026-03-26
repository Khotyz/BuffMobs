package com.khotyz.buffmobs.config;

import com.khotyz.buffmobs.BuffMobsMod;
import net.minecraft.client.gui.screens.Screen;

public class ConfigScreenFactory {

    private static final boolean CLOTH_AVAILABLE = detectCloth();

    private static boolean detectCloth() {
        try {
            Class.forName("me.shedaniel.clothconfig2.api.ConfigBuilder");
            BuffMobsMod.LOGGER.info("[BuffMobs] Cloth Config detected — GUI enabled");
            return true;
        } catch (ClassNotFoundException e) {
            BuffMobsMod.LOGGER.info("[BuffMobs] Cloth Config not found — using fallback notice");
            return false;
        }
    }

    public static boolean isAvailable() {
        return CLOTH_AVAILABLE;
    }

    public static Screen create(Screen parent) {
        if (!CLOTH_AVAILABLE) return null;
        try {
            return ClothConfigScreen.create(parent);
        } catch (Exception e) {
            BuffMobsMod.LOGGER.error("[BuffMobs] Failed to create Cloth Config screen", e);
            return null;
        }
    }
}
