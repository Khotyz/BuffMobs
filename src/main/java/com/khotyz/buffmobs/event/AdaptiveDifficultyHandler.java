package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.data.BuffMobsWorldData;
import com.khotyz.buffmobs.util.ResetManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class AdaptiveDifficultyHandler {

    public static boolean isEnabled() {
        return BuffMobsConfig.INSTANCE.enabled && BuffMobsConfig.INSTANCE.adaptiveDifficulty.enabled;
    }

    public static double getAdaptiveMultiplier(Level level) {
        if (!isEnabled()) return 1.0;
        BuffMobsWorldData data = BuffMobsWorldData.get(level);
        if (data == null) return 1.0;
        return 1.0 + computeBonus(data);
    }

    public static double computeBonus(BuffMobsWorldData data) {
        BuffMobsConfig.AdaptiveDifficulty cfg = BuffMobsConfig.INSTANCE.adaptiveDifficulty;
        long deathSteps = data.getPlayerDeaths() / (long) Math.max(1, cfg.deathsPerStep);
        double bonus = deathSteps * cfg.bonusPerStep;
        if (cfg.killReductionEnabled) {
            long killSteps = data.getPlayerMobKills() / (long) Math.max(1, cfg.killsPerReductionStep);
            bonus -= killSteps * cfg.reductionPerStep;
        }
        return Math.max(0.0, Math.min(bonus, cfg.maxBonus));
    }

    public static void onPlayerDeath(Player player) {
        if (!isEnabled()) return;
        if (!(player.level() instanceof ServerLevel serverLevel)) return;
        MinecraftServer server = serverLevel.getServer();
        if (server == null) return;
        BuffMobsWorldData data = BuffMobsWorldData.get(serverLevel);
        if (data == null) return;

        double before = computeBonus(data);
        data.incrementPlayerDeaths();
        double after = computeBonus(data);
        BuffMobsMod.LOGGER.debug("[BuffMobs] Adaptive difficulty: player deaths={}, bonus={}",
                data.getPlayerDeaths(), after);
        if (after != before) ResetManager.reapplyAllMobs(server);
    }

    public static void onMobDeath(Mob mob, DamageSource source) {
        if (!isEnabled()) return;
        if (!(mob.level() instanceof ServerLevel serverLevel)) return;
        if (!MobTickHandler.isInitialized(mob.getUUID())) return;
        if (!isPlayerKill(source)) return;
        MinecraftServer server = serverLevel.getServer();
        if (server == null) return;
        BuffMobsWorldData data = BuffMobsWorldData.get(serverLevel);
        if (data == null) return;

        double before = computeBonus(data);
        data.incrementPlayerMobKills();
        double after = computeBonus(data);
        BuffMobsMod.LOGGER.debug("[BuffMobs] Adaptive difficulty: buffed mob kills={}, bonus={}",
                data.getPlayerMobKills(), after);
        if (after != before) ResetManager.reapplyAllMobs(server);
    }

    private static boolean isPlayerKill(DamageSource source) {
        Entity attacker = source.getEntity();
        if (attacker instanceof Player) return true;
        Entity direct = source.getDirectEntity();
        return direct instanceof Projectile projectile && projectile.getOwner() instanceof Player;
    }
}
