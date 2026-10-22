package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class Guard {

    private static final Set<String> REPORTED = ConcurrentHashMap.newKeySet();

    private Guard() {
    }

    public static void report(String feature, Throwable throwable) {
        if (REPORTED.add(feature)) {
            BuffMobsMod.LOGGER.error("[BuffMobs] {} was skipped to keep the game running. Later failures of this feature stay silent.", feature, throwable);
        }
    }
}
