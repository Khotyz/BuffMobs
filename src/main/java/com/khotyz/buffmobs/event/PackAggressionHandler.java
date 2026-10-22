package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.data.BuffMobsWorldData;
import com.khotyz.buffmobs.util.MobBuffUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;

import java.util.List;

public class PackAggressionHandler {

    public static boolean isEnabled() {
        return BuffMobsConfig.INSTANCE.enabled.get() && BuffMobsConfig.INSTANCE.packAggression.enabled.get();
    }

    public static void onMobRemoved(Mob mob) {
    }

    public static void clearCaches() {
    }

    public static void onMobDamaged(Mob victim, DamageSource source) {
        if (!isEnabled()) return;
        if (!(victim.level() instanceof ServerLevel serverLevel)) return;
        if (!MobBuffUtil.isValidMob(victim)) return;
        Player player = resolveAttackingPlayer(source);
        if (player == null || player.isSpectator() || player.isCreative()) return;

        BuffMobsConfig.PackAggression cfg = BuffMobsConfig.INSTANCE.packAggression;
        long now = serverLevel.getGameTime();
        BuffMobsWorldData data = BuffMobsWorldData.get(serverLevel);
        if (data == null) return;
        List<Mob> candidates = serverLevel.getEntitiesOfClass(Mob.class,
                victim.getBoundingBox().inflate(cfg.radius.get()),
                candidate -> isPackCandidate(victim, candidate, now, cfg, data));

        int pulled = 0;
        for (Mob candidate : candidates) {
            if (pulled >= cfg.maxPackSize.get()) break;
            pullMob(candidate, player);
            data.updateMobState(candidate.getUUID(), state -> state.setPackLastPullTick(now));
            pulled++;
        }
        if (pulled > 0) {
            BuffMobsMod.LOGGER.debug("[BuffMobs] Pack aggression: pulled {} mobs of type {}",
                    pulled, victim.getType().getDescriptionId());
        }
    }

    private static boolean isPackCandidate(Mob victim, Mob candidate, long now,
                                           BuffMobsConfig.PackAggression cfg, BuffMobsWorldData data) {
        if (candidate == victim) return false;
        if (candidate.getType() != victim.getType()) return false;
        if (!MobBuffUtil.isValidMob(candidate)) return false;
        long lastPull = data.getMobState(candidate.getUUID()).packLastPullTick();
        return lastPull == Long.MIN_VALUE || now - lastPull >= cfg.cooldownTicks.get();
    }

    private static void pullMob(Mob mob, Player player) {
        mob.setTarget(player);
        if (mob instanceof NeutralMob neutralMob) {
            neutralMob.setPersistentAngerTarget(EntityReference.of(player));
            neutralMob.startPersistentAngerTimer();
        }
    }

    private static Player resolveAttackingPlayer(DamageSource source) {
        Entity attacker = source.getEntity();
        if (attacker instanceof Player player) return player;
        Entity direct = source.getDirectEntity();
        if (direct instanceof Projectile projectile && projectile.getOwner() instanceof Player player) return player;
        return null;
    }
}
