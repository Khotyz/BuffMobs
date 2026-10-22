package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

public final class ModConfigCompat {

    private static final String[] PREFERRED_TYPES = {"LOCAL", "COMMON"};
    private static final String[] BACKUP_TYPES = {"STARTUP", "SYNCED", "SERVER"};
    private static ModConfig.Type cachedType;
    private static boolean lookupPerformed;

    private ModConfigCompat() {
    }

    public static ModConfig.Type configType() {
        if (!lookupPerformed) {
            lookupPerformed = true;
            cachedType = findConfigType();
        }
        return cachedType;
    }

    public static boolean register(ModContainer container, ModConfigSpec spec, String fileName) {
        ModConfig.Type type = configType();
        if (type == null) {
            BuffMobsMod.LOGGER.error("[BuffMobs] No usable config type is available, {} keeps its default values", fileName);
            return false;
        }
        try {
            container.registerConfig(type, spec, fileName);
            BuffMobsMod.LOGGER.info("[BuffMobs] Registered {} with the {} config type", fileName, type.name());
            return true;
        } catch (Throwable throwable) {
            BuffMobsMod.LOGGER.error("[BuffMobs] Unable to register {} with the {} config type", fileName, type.name(), throwable);
            return false;
        }
    }

    private static ModConfig.Type findConfigType() {
        List<ModConfig.Type> available = availableTypes();
        for (String name : PREFERRED_TYPES) {
            for (ModConfig.Type type : available) {
                if (type.name().equalsIgnoreCase(name)) return type;
            }
        }
        for (String name : BACKUP_TYPES) {
            for (ModConfig.Type type : available) {
                if (type.name().equalsIgnoreCase(name)) return type;
            }
        }
        for (ModConfig.Type type : available) {
            if (!"CLIENT".equalsIgnoreCase(type.name())) return type;
        }
        return available.isEmpty() ? null : available.get(0);
    }

    private static List<ModConfig.Type> availableTypes() {
        List<ModConfig.Type> types = new ArrayList<>();
        try {
            for (ModConfig.Type type : ModConfig.Type.values()) {
                types.add(type);
            }
        } catch (Throwable throwable) {
            BuffMobsMod.LOGGER.error("[BuffMobs] Unable to read the available config types", throwable);
        }
        return types;
    }
}
