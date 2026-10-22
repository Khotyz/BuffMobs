package com.khotyz.buffmobs.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class MinecraftScreenCompat {

    private MinecraftScreenCompat() {
    }

    public static Screen current(Minecraft minecraft) {
        try {
            Object gui = Minecraft.class.getField("gui").get(minecraft);
            Method screenMethod = gui.getClass().getMethod("screen");
            return (Screen) screenMethod.invoke(gui);
        } catch (NoSuchFieldException | NoSuchMethodException exception) {
            try {
                return (Screen) Minecraft.class.getField("screen").get(minecraft);
            } catch (ReflectiveOperationException fallbackException) {
                throw new IllegalStateException(fallbackException);
            }
        } catch (InvocationTargetException exception) {
            throw propagate(exception.getCause());
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }
    }

    public static void set(Minecraft minecraft, Screen screen) {
        try {
            Object gui = Minecraft.class.getField("gui").get(minecraft);
            Method setScreenMethod = gui.getClass().getMethod("setScreen", Screen.class);
            setScreenMethod.invoke(gui, screen);
        } catch (NoSuchFieldException | NoSuchMethodException exception) {
            try {
                Minecraft.class.getMethod("setScreen", Screen.class).invoke(minecraft, screen);
            } catch (InvocationTargetException invocationException) {
                throw propagate(invocationException.getCause());
            } catch (ReflectiveOperationException fallbackException) {
                throw new IllegalStateException(fallbackException);
            }
        } catch (InvocationTargetException exception) {
            throw propagate(exception.getCause());
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static RuntimeException propagate(Throwable throwable) {
        if (throwable instanceof RuntimeException runtimeException) return runtimeException;
        if (throwable instanceof Error error) throw error;
        return new IllegalStateException(throwable);
    }
}
