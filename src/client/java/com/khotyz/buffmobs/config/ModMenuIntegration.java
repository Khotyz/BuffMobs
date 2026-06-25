package com.khotyz.buffmobs.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (com.khotyz.buffmobs.config.ConfigScreenFactory.isAvailable()) {
            return com.khotyz.buffmobs.config.ConfigScreenFactory::create;
        }
        return parent -> null;
    }
}
