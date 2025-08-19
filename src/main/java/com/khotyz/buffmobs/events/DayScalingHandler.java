package com.khotyz.buffmobs.events;

import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.config.BuffMobsConfig.NotificationMode;
import com.khotyz.buffmobs.util.MobBuffUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.WeakHashMap;
import java.util.Map;

public class DayScalingHandler {
    private static final Map<ServerWorld, Long> LAST_DAY_CHECK = new WeakHashMap<>();

    public void onWorldTick(ServerWorld world) {
        if (!BuffMobsConfig.isEnabled() ||
                !BuffMobsConfig.isDayScalingEnabled() ||
                !BuffMobsConfig.showDayScalingNotifications() ||
                world.getPlayers().isEmpty()) {
            return;
        }

        long currentTime = world.getTimeOfDay();
        long currentDay = currentTime / 24000L;

        Long lastCheckedDay = LAST_DAY_CHECK.get(world);

        // Check if a new day has started
        if (lastCheckedDay == null || currentDay > lastCheckedDay) {
            LAST_DAY_CHECK.put(world, currentDay);

            // Skip notification on the very first day
            if (lastCheckedDay != null && currentDay > 0) {
                boolean shouldNotify = false;

                switch (BuffMobsConfig.getDayScalingNotificationMode()) {
                    case EVERY_DAY -> shouldNotify = true;
                    case SCALING_INCREASE_ONLY -> {
                        int scalingInterval = BuffMobsConfig.getDayScalingInterval();
                        shouldNotify = currentDay % scalingInterval == 0;
                    }
                }

                if (shouldNotify) {
                    sendDayScalingNotification(world, currentDay);
                }
            }
        }
    }

    private void sendDayScalingNotification(ServerWorld world, long currentDay) {
        int scalingInterval = BuffMobsConfig.getDayScalingInterval();
        double currentMultiplier = MobBuffUtil.getDayMultiplier(world.getTimeOfDay());
        double maxMultiplier = BuffMobsConfig.getDayScalingMax();

        // Calculate days until next scaling increase
        long daysUntilNextScaling = scalingInterval - (currentDay % scalingInterval);
        if (daysUntilNextScaling == scalingInterval) daysUntilNextScaling = 0;

        // Check if we hit max scaling
        boolean isMaxed = currentMultiplier >= maxMultiplier;

        // Create the message
        MutableText message;
        if (isMaxed) {
            message = Text.literal("☀ ")
                    .append(Text.literal("Day " + currentDay + " - Mob Scaling: ")
                            .formatted(Formatting.GOLD, Formatting.BOLD))
                    .append(Text.literal(String.format("%.1fx", currentMultiplier))
                            .formatted(Formatting.RED, Formatting.BOLD))
                    .append(Text.literal(" (MAXIMUM)")
                            .formatted(Formatting.DARK_RED, Formatting.BOLD));
        } else {
            message = Text.literal("☀ ")
                    .append(Text.literal("Day " + currentDay + " - Mob Scaling: ")
                            .formatted(Formatting.GOLD))
                    .append(Text.literal(String.format("%.1fx", currentMultiplier))
                            .formatted(Formatting.RED, Formatting.BOLD));

            if (daysUntilNextScaling > 0) {
                message.append(Text.literal(" | Next increase in " + daysUntilNextScaling +
                                " day" + (daysUntilNextScaling != 1 ? "s" : ""))
                        .formatted(Formatting.YELLOW));
            }
        }

        // Send to all players in the world
        for (ServerPlayerEntity player : world.getPlayers()) {
            player.sendMessage(message, false);
        }
    }
}