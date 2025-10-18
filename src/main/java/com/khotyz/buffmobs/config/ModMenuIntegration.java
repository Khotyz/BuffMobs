package com.khotyz.buffmobs.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
        return parent -> {
            try {
                // Check if AutoConfig classes are available
                Class<?> autoConfigClass = Class.forName("me.shedaniel.autoconfig.AutoConfig");
                Class<?> configHolderClass = Class.forName("me.shedaniel.autoconfig.ConfigHolder");

                // Try to get the config holder first
                Object configHolder = autoConfigClass.getMethod("getConfigHolder", Class.class)
                        .invoke(null, BuffMobsConfig.class);

                // Check if we can get the config screen
                java.lang.reflect.Method getConfigScreenMethod = autoConfigClass
                        .getMethod("getConfigScreen", Class.class, Screen.class);

                Object screenSupplier = getConfigScreenMethod.invoke(null, BuffMobsConfig.class, parent);

                if (screenSupplier instanceof java.util.function.Supplier<?>) {
                    @SuppressWarnings("unchecked")
                    java.util.function.Supplier<Screen> supplier =
                            (java.util.function.Supplier<Screen>) screenSupplier;
                    Screen screen = supplier.get();

                    // Additional validation - make sure the screen isn't null
                    if (screen != null) {
                        return screen;
                    }
                }

                return createFallbackScreen(parent, "Config screen creation failed");

            } catch (ClassNotFoundException e) {
                return createErrorScreen(parent,
                        "Cloth Config Missing",
                        "Cloth Config API v19.0.147+ required for configuration screen.\n\n" +
                                "Download from:\n" +
                                "- CurseForge: Cloth Config API\n" +
                                "- Modrinth: Cloth Config API\n\n" +
                                "Manual config: config/buffmobs.json5");
            } catch (NoSuchMethodException | IllegalAccessException e) {
                return createErrorScreen(parent,
                        "Incompatible Version",
                        "Incompatible Cloth Config version detected.\n\n" +
                                "Required: v19.0.147+fabric or later\n" +
                                "For Minecraft 1.21.6-1.21.8\n\n" +
                                "Manual config: config/buffmobs.json5");
            } catch (java.lang.reflect.InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof NoSuchMethodError) {
                    return createErrorScreen(parent,
                            "Method Compatibility Issue",
                            "Cloth Config method signature mismatch.\n\n" +
                                    "This usually means version incompatibility.\n" +
                                    "Try Cloth Config v19.0.147+fabric\n\n" +
                                    "Manual config: config/buffmobs.json5");
                }

                com.khotyz.buffmobs.BuffMobsMod.LOGGER.error("Config screen invocation failed", e);
                return createFallbackScreen(parent, "Screen creation error: " + cause.getMessage());
            } catch (Exception e) {
                com.khotyz.buffmobs.BuffMobsMod.LOGGER.error("Unexpected config screen error", e);
                return createFallbackScreen(parent, "Unexpected error: " + e.getClass().getSimpleName());
            }
        };
    }

    private Screen createFallbackScreen(Screen parent, String reason) {
        return new NoticeScreen(
                () -> MinecraftClient.getInstance().setScreen(parent),
                Text.literal("BuffMobs Configuration"),
                Text.literal("Config GUI temporarily unavailable.\n\n" +
                        "Reason: " + reason + "\n\n" +
                        "Manual configuration:\n" +
                        "Edit config/buffmobs.json5\n" +
                        "Restart game after changes.\n\n" +
                        "For GUI support, install:\n" +
                        "Cloth Config API v19.0.147+fabric")
        );
    }

    private Screen createErrorScreen(Screen parent, String title, String message) {
        return new NoticeScreen(
                () -> MinecraftClient.getInstance().setScreen(parent),
                Text.literal("BuffMobs - " + title),
                Text.literal(message)
        );
    }
}