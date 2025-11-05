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

    @Override
    public String toString() {
        return name;
    }
}