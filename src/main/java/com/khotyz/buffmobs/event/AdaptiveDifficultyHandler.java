package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.util.Guard;
import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.data.BuffMobsWorldData;
import com.khotyz.buffmobs.util.ResetManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public class AdaptiveDifficultyHandler {

    public static boolean isEnabled() {
        return BuffMobsConfig.INSTANCE.enabled.get() && BuffMobsConfig.INSTANCE.adaptiveDifficulty.enabled.get();
    }

    public static double getAdaptiveMultiplier(Level level) {
        if (!isEnabled()) return 1.0;
        BuffMobsWorldData data = BuffMobsWorldData.get(level);
        if (data == null) return 1.0;
        return 1.0 + computeBonus(data);
    }

    public static double computeBonus(BuffMobsWorldData data) {
        BuffMobsConfig.AdaptiveDifficulty cfg = BuffMobsConfig.INSTANCE.adaptiveDifficulty;
        long deathSteps = data.getPlayerDeaths() / (long) Math.max(1, cfg.deathsPerStep.get());
        double bonus = deathSteps * cfg.bonusPerStep.get();
        if (cfg.killReductionEnabled.get()) {
            long killSteps = data.getPlayerMobKills() / (long) Math.max(1, cfg.killsPerReductionStep.get());
            bonus -= killSteps * cfg.reductionPerStep.get();
        }
        return Math.max(0.0, Math.min(bonus, cfg.maxBonus.get()));
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        try {
            if (!isEnabled()) return;
            if (!(event.getEntity().level() instanceof ServerLevel serverLevel)) return;
            BuffMobsWorldData data = BuffMobsWorldData.get(serverLevel);
            if (data == null) return;
            MinecraftServer server = serverLevel.getServer();
            if (server == null) return;

            if (event.getEntity() instanceof ServerPlayer) {
                double before = computeBonus(data);
                data.incrementPlayerDeaths();
                double after = computeBonus(data);
                BuffMobsMod.LOGGER.debug("[BuffMobs] Adaptive difficulty: player deaths={}, bonus={}",
                        data.getPlayerDeaths(), after);
                if (after != before) ResetManager.reapplyAllMobs(server);
                return;
            }

            if (event.getEntity() instanceof Mob mob) {
                if (!MobTickHandler.isInitialized(mob.getUUID())) return;
                if (!isPlayerKill(event)) return;
                double before = computeBonus(data);
                data.incrementPlayerMobKills();
                double after = computeBonus(data);
                if (after != before) ResetManager.reapplyAllMobs(server);
            }
        } catch (Throwable throwable) {
            Guard.report("AdaptiveDifficultyHandler.onLivingDeath", throwable);
        }
    }

    private static boolean isPlayerKill(LivingDeathEvent event) {
        Entity attacker = event.getSource().getEntity();
        if (attacker instanceof Player) return true;
        Entity direct = event.getSource().getDirectEntity();
        return direct instanceof Projectile projectile && projectile.getOwner() instanceof Player;
    }
}
