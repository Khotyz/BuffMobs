package com.khotyz.buffmobs.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class BuffMobsWorldData extends SavedData {

    private static final Identifier DATA_NAME = Identifier.fromNamespaceAndPath("buffmobs", "world_data");
    private static final Codec<MobState> MOB_STATE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("eliteEvaluated").orElse(false).forGetter(MobState::eliteEvaluated),
            Codec.BOOL.fieldOf("elite").orElse(false).forGetter(MobState::elite),
            Codec.BOOL.fieldOf("eliteGeneratedName").orElse(false).forGetter(MobState::eliteGeneratedName),
            Codec.BOOL.fieldOf("eliteNotified").orElse(false).forGetter(MobState::eliteNotified),
            Codec.BOOL.fieldOf("traitEvaluated").orElse(false).forGetter(MobState::traitEvaluated),
            Codec.STRING.fieldOf("trait").orElse("").forGetter(MobState::trait),
            Codec.BOOL.fieldOf("armorEvaluated").orElse(false).forGetter(MobState::armorEvaluated),
            Codec.INT.fieldOf("armorSlotsMask").orElse(0).forGetter(MobState::armorSlotsMask),
            Codec.BOOL.fieldOf("summonedMinion").orElse(false).forGetter(MobState::summonedMinion),
            Codec.BOOL.fieldOf("lastStandUsed").orElse(false).forGetter(MobState::lastStandUsed),
            Codec.LONG.fieldOf("lastTeleportTick").orElse(-100000L).forGetter(MobState::lastTeleportTick),
            Codec.LONG.fieldOf("nextSummonTick").orElse(0L).forGetter(MobState::nextSummonTick),
            Codec.STRING.listOf().fieldOf("summonedMinions").orElse(List.of()).forGetter(MobState::summonedMinions),
            Codec.LONG.fieldOf("packLastPullTick").orElse(Long.MIN_VALUE).forGetter(MobState::packLastPullTick),
            Codec.BOOL.fieldOf("bloodMoonGlowing").orElse(false).forGetter(MobState::bloodMoonGlowing),
            Codec.BOOL.fieldOf("passiveAggressionEnraged").orElse(false).forGetter(MobState::passiveAggressionEnraged)
    ).apply(instance, MobState::new));

    public static final Codec<BuffMobsWorldData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("scalingDayOffset").orElse(0L).forGetter(data -> data.scalingDayOffset),
            Codec.LONG.fieldOf("lastBloodMoonDay").orElse(0L).forGetter(data -> data.lastBloodMoonDay),
            Codec.LONG.fieldOf("bloodMoonActiveDay").orElse(-1L).forGetter(data -> data.bloodMoonActiveDay),
            Codec.INT.fieldOf("playerDeaths").orElse(0).forGetter(data -> data.playerDeaths),
            Codec.INT.fieldOf("playerMobKills").orElse(0).forGetter(data -> data.playerMobKills),
            Codec.unboundedMap(Codec.STRING, MOB_STATE_CODEC).fieldOf("mobStates").orElse(Map.of()).forGetter(data -> data.mobStates)
    ).apply(instance, BuffMobsWorldData::new));

    private long scalingDayOffset;
    private long lastBloodMoonDay;
    private long bloodMoonActiveDay = -1L;
    private int playerDeaths;
    private int playerMobKills;
    private final Map<String, MobState> mobStates = new HashMap<>();

    public BuffMobsWorldData() {
    }

    private BuffMobsWorldData(long scalingDayOffset, long lastBloodMoonDay, long bloodMoonActiveDay,
                              int playerDeaths, int playerMobKills, Map<String, MobState> mobStates) {
        this.scalingDayOffset = scalingDayOffset;
        this.lastBloodMoonDay = lastBloodMoonDay;
        this.bloodMoonActiveDay = bloodMoonActiveDay;
        this.playerDeaths = playerDeaths;
        this.playerMobKills = playerMobKills;
        mobStates.forEach((key, value) -> this.mobStates.put(key, value.copy()));
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

    public long getBloodMoonActiveDay() {
        return bloodMoonActiveDay;
    }

    public void setBloodMoonActiveDay(long bloodMoonActiveDay) {
        this.bloodMoonActiveDay = bloodMoonActiveDay;
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

    public MobState getMobState(UUID uuid) {
        MobState state = mobStates.get(uuid.toString());
        return state == null ? new MobState() : state.copy();
    }

    public void updateMobState(UUID uuid, Consumer<MobState> update) {
        MobState state = getMobState(uuid);
        update.accept(state);
        mobStates.put(uuid.toString(), state);
        setDirty();
    }

    public void removeMobState(UUID uuid) {
        if (mobStates.remove(uuid.toString()) != null) setDirty();
    }

    public void resetMobState(UUID uuid) {
        updateMobState(uuid, MobState::resetGameplayState);
    }

    public void clearMobStates() {
        if (mobStates.isEmpty()) return;
        mobStates.clear();
        setDirty();
    }

    public static final class MobState {
        private boolean eliteEvaluated;
        private boolean elite;
        private boolean eliteGeneratedName;
        private boolean eliteNotified;
        private boolean traitEvaluated;
        private String trait;
        private boolean armorEvaluated;
        private int armorSlotsMask;
        private boolean summonedMinion;
        private boolean lastStandUsed;
        private long lastTeleportTick;
        private long nextSummonTick;
        private List<String> summonedMinions;
        private long packLastPullTick;
        private boolean bloodMoonGlowing;
        private boolean passiveAggressionEnraged;

        public MobState() {
            this(false, false, false, false, false, "", false, 0, false, false,
                    -100000L, 0L, List.of(), Long.MIN_VALUE, false, false);
        }

        private MobState(boolean eliteEvaluated, boolean elite, boolean eliteGeneratedName, boolean eliteNotified,
                        boolean traitEvaluated, String trait, boolean armorEvaluated, int armorSlotsMask, boolean summonedMinion,
                        boolean lastStandUsed, long lastTeleportTick, long nextSummonTick,
                        List<String> summonedMinions, long packLastPullTick, boolean bloodMoonGlowing,
                        boolean passiveAggressionEnraged) {
            this.eliteEvaluated = eliteEvaluated;
            this.elite = elite;
            this.eliteGeneratedName = eliteGeneratedName;
            this.eliteNotified = eliteNotified;
            this.traitEvaluated = traitEvaluated;
            this.trait = trait == null ? "" : trait;
            this.armorEvaluated = armorEvaluated;
            this.armorSlotsMask = armorSlotsMask;
            this.summonedMinion = summonedMinion;
            this.lastStandUsed = lastStandUsed;
            this.lastTeleportTick = lastTeleportTick;
            this.nextSummonTick = nextSummonTick;
            this.summonedMinions = new ArrayList<>(summonedMinions == null ? List.of() : summonedMinions);
            this.packLastPullTick = packLastPullTick;
            this.bloodMoonGlowing = bloodMoonGlowing;
            this.passiveAggressionEnraged = passiveAggressionEnraged;
        }

        public boolean eliteEvaluated() { return eliteEvaluated; }
        public boolean elite() { return elite; }
        public boolean eliteGeneratedName() { return eliteGeneratedName; }
        public boolean eliteNotified() { return eliteNotified; }
        public boolean traitEvaluated() { return traitEvaluated; }
        public String trait() { return trait; }
        public boolean armorEvaluated() { return armorEvaluated; }
        public int armorSlotsMask() { return armorSlotsMask; }
        public boolean summonedMinion() { return summonedMinion; }
        public boolean lastStandUsed() { return lastStandUsed; }
        public long lastTeleportTick() { return lastTeleportTick; }
        public long nextSummonTick() { return nextSummonTick; }
        public List<String> summonedMinions() { return List.copyOf(summonedMinions); }
        public long packLastPullTick() { return packLastPullTick; }
        public boolean bloodMoonGlowing() { return bloodMoonGlowing; }
        public boolean passiveAggressionEnraged() { return passiveAggressionEnraged; }

        public void setEliteEvaluated(boolean value) { eliteEvaluated = value; }
        public void setElite(boolean value) { elite = value; }
        public void setEliteGeneratedName(boolean value) { eliteGeneratedName = value; }
        public void setEliteNotified(boolean value) { eliteNotified = value; }
        public void setTraitEvaluated(boolean value) { traitEvaluated = value; }
        public void setTrait(String value) { trait = value == null ? "" : value; }
        public void setArmorEvaluated(boolean value) { armorEvaluated = value; }
        public void setArmorSlotsMask(int value) { armorSlotsMask = value & 15; }
        public void setSummonedMinion(boolean value) { summonedMinion = value; }
        public void setLastStandUsed(boolean value) { lastStandUsed = value; }
        public void setLastTeleportTick(long value) { lastTeleportTick = value; }
        public void setNextSummonTick(long value) { nextSummonTick = value; }
        public void setSummonedMinions(List<String> value) { summonedMinions = new ArrayList<>(value); }
        public void addSummonedMinion(UUID uuid) { summonedMinions.add(uuid.toString()); }
        public void removeSummonedMinion(UUID uuid) { summonedMinions.remove(uuid.toString()); }
        public void setPackLastPullTick(long value) { packLastPullTick = value; }
        public void setBloodMoonGlowing(boolean value) { bloodMoonGlowing = value; }
        public void setPassiveAggressionEnraged(boolean value) { passiveAggressionEnraged = value; }

        private void resetGameplayState() {
            eliteEvaluated = false;
            elite = false;
            eliteGeneratedName = false;
            eliteNotified = false;
            traitEvaluated = false;
            trait = "";
            armorEvaluated = false;
            armorSlotsMask = 0;
            summonedMinion = false;
            lastStandUsed = false;
            lastTeleportTick = -100000L;
            nextSummonTick = 0L;
            summonedMinions.clear();
            packLastPullTick = Long.MIN_VALUE;
            passiveAggressionEnraged = false;
        }

        private MobState copy() {
            return new MobState(eliteEvaluated, elite, eliteGeneratedName, eliteNotified, traitEvaluated, trait,
                    armorEvaluated, armorSlotsMask, summonedMinion, lastStandUsed, lastTeleportTick, nextSummonTick,
                    summonedMinions, packLastPullTick, bloodMoonGlowing, passiveAggressionEnraged);
        }
    }
}
