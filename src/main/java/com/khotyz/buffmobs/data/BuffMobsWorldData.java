package com.khotyz.buffmobs.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

public class BuffMobsWorldData extends SavedData {
    private static final String DATA_NAME = "buffmobs_world_data";
    private static final String KEY_SCALING_DAY_OFFSET = "scalingDayOffset";
    private static final String KEY_LAST_BLOOD_MOON_DAY = "lastBloodMoonDay";
    private static final String KEY_PLAYER_DEATHS = "playerDeaths";
    private static final String KEY_PLAYER_MOB_KILLS = "playerMobKills";

    private long scalingDayOffset;
    private long lastBloodMoonDay;
    private int playerDeaths;
    private int playerMobKills;

    public BuffMobsWorldData() {
    }

    public static BuffMobsWorldData load(CompoundTag tag, HolderLookup.Provider provider) {
        BuffMobsWorldData data = new BuffMobsWorldData();
        data.scalingDayOffset = tag.getLong(KEY_SCALING_DAY_OFFSET);
        data.lastBloodMoonDay = tag.getLong(KEY_LAST_BLOOD_MOON_DAY);
        data.playerDeaths = tag.getInt(KEY_PLAYER_DEATHS);
        data.playerMobKills = tag.getInt(KEY_PLAYER_MOB_KILLS);
        return data;
    }

    public static BuffMobsWorldData get(MinecraftServer server) {
        if (server == null) return null;
        ServerLevel overworld = server.overworld();
        if (overworld == null) return null;
        return overworld.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(BuffMobsWorldData::new, BuffMobsWorldData::load, null),
                DATA_NAME);
    }

    public static BuffMobsWorldData get(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) return null;
        return get(serverLevel.getServer());
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        tag.putLong(KEY_SCALING_DAY_OFFSET, scalingDayOffset);
        tag.putLong(KEY_LAST_BLOOD_MOON_DAY, lastBloodMoonDay);
        tag.putInt(KEY_PLAYER_DEATHS, playerDeaths);
        tag.putInt(KEY_PLAYER_MOB_KILLS, playerMobKills);
        return tag;
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
