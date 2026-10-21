package com.khotyz.buffmobs.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class BuffMobsWorldData extends SavedData {

    private static final String DATA_NAME = "buffmobs_world_data";

    public static final Codec<BuffMobsWorldData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("scalingDayOffset").orElse(0L).forGetter(data -> data.scalingDayOffset),
            Codec.LONG.fieldOf("lastBloodMoonDay").orElse(0L).forGetter(data -> data.lastBloodMoonDay),
            Codec.INT.fieldOf("playerDeaths").orElse(0).forGetter(data -> data.playerDeaths),
            Codec.INT.fieldOf("playerMobKills").orElse(0).forGetter(data -> data.playerMobKills)
    ).apply(instance, BuffMobsWorldData::new));

    private long scalingDayOffset;
    private long lastBloodMoonDay;
    private int playerDeaths;
    private int playerMobKills;

    public BuffMobsWorldData() {
    }

    private BuffMobsWorldData(long scalingDayOffset, long lastBloodMoonDay, int playerDeaths, int playerMobKills) {
        this.scalingDayOffset = scalingDayOffset;
        this.lastBloodMoonDay = lastBloodMoonDay;
        this.playerDeaths = playerDeaths;
        this.playerMobKills = playerMobKills;
    }

    public static BuffMobsWorldData get(MinecraftServer server) {
        if (server == null) return null;
        ServerLevel overworld = server.overworld();
        if (overworld == null) return null;
        return overworld.getDataStorage().computeIfAbsent(
                new SavedDataType<>(DATA_NAME, BuffMobsWorldData::new, CODEC, null));
    }

    public static BuffMobsWorldData get(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) return null;
        return get(serverLevel.getServer());
    }

    public long getScalingDayOffset() {
        return scalingDayOffset;
    }

    public void setScalingDayOffset(long scalingDayOffset) {
        this.scalingDayOffset = scalingDayOffset;
        setDirty();
    }

    public long getLastBloodMoonDay() {
        return lastBloodMoonDay;
    }

    public void setLastBloodMoonDay(long lastBloodMoonDay) {
        this.lastBloodMoonDay = lastBloodMoonDay;
        setDirty();
    }

    public int getPlayerDeaths() {
        return playerDeaths;
    }

    public int getPlayerMobKills() {
        return playerMobKills;
    }

    public void incrementPlayerDeaths() {
        playerDeaths++;
        setDirty();
    }

    public void incrementPlayerMobKills() {
        playerMobKills++;
        setDirty();
    }

    public void resetAdaptiveCounters() {
        playerDeaths = 0;
        playerMobKills = 0;
        setDirty();
    }
}
