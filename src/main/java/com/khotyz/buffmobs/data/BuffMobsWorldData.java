package com.khotyz.buffmobs.data;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class BuffMobsWorldData extends SavedData {
    private static final String KEY_SCALING_DAY_OFFSET = "scalingDayOffset";
    private static final String KEY_LAST_BLOOD_MOON_DAY = "lastBloodMoonDay";
    private static final String KEY_PLAYER_DEATHS = "playerDeaths";
    private static final String KEY_PLAYER_MOB_KILLS = "playerMobKills";

    private static final Codec<BuffMobsWorldData> CODEC =
            CompoundTag.CODEC.xmap(BuffMobsWorldData::load, BuffMobsWorldData::save);

    private static final SavedDataType<BuffMobsWorldData> TYPE =
            new SavedDataType<>("buffmobs_world_data", BuffMobsWorldData::new, CODEC);

    private long scalingDayOffset;
    private long lastBloodMoonDay;
    private int playerDeaths;
    private int playerMobKills;

    public BuffMobsWorldData() {
    }

    private static BuffMobsWorldData load(CompoundTag tag) {
        BuffMobsWorldData data = new BuffMobsWorldData();
        data.scalingDayOffset = tag.getLongOr(KEY_SCALING_DAY_OFFSET, 0L);
        data.lastBloodMoonDay = tag.getLongOr(KEY_LAST_BLOOD_MOON_DAY, 0L);
        data.playerDeaths = tag.getIntOr(KEY_PLAYER_DEATHS, 0);
        data.playerMobKills = tag.getIntOr(KEY_PLAYER_MOB_KILLS, 0);
        return data;
    }

    private CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putLong(KEY_SCALING_DAY_OFFSET, scalingDayOffset);
        tag.putLong(KEY_LAST_BLOOD_MOON_DAY, lastBloodMoonDay);
        tag.putInt(KEY_PLAYER_DEATHS, playerDeaths);
        tag.putInt(KEY_PLAYER_MOB_KILLS, playerMobKills);
        return tag;
    }

    public static BuffMobsWorldData get(MinecraftServer server) {
        if (server == null) return null;
        ServerLevel overworld = server.overworld();
        if (overworld == null) return null;
        return overworld.getDataStorage().computeIfAbsent(TYPE);
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
