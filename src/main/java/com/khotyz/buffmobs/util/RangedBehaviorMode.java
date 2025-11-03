package com.khotyz.buffmobs.util;

public enum RangedBehaviorMode {
    MELEE("melee"),
    RETREAT("retreat"),
    RANDOM("random");

    private final String name;

    RangedBehaviorMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static RangedBehaviorMode fromString(String name) {
        for (RangedBehaviorMode mode : values()) {
            if (mode.name.equalsIgnoreCase(name)) {
                return mode;
            }
        }
        return MELEE; // Default fallback
    }
}