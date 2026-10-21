package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.util.MobBuffUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PackAggressionHandler {
    private static final Map<UUID, Long> LAST_PULL_TICK = new HashMap<>();

    public static boolean isEnabled() {
        return BuffMobsConfig.INSTANCE.enabled.get() && BuffMobsConfig.INSTANCE.packAggression.enabled.get();
    }

    public static void onMobRemoved(Mob mob) {
        LAST_PULL_TICK.remove(mob.getUUID());
    }

    public static void clearCaches() {
        LAST_PULL_TICK.clear();
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent.Post event) {
        if (!isEnabled()) return;
        if (!(event.getEntity() instanceof Mob victim)) return;
        if (!(victim.level() instanceof ServerLevel serverLevel)) return;
        if (!MobBuffUtil.isValidMob(victim)) return;
        Player player = resolveAttackingPlayer(event);
        if (player == null || player.isSpectator() || player.isCreative()) return;

        BuffMobsConfig.PackAggression cfg = BuffMobsConfig.INSTANCE.packAggression;
        long now = serverLevel.getGameTime();
        List<Mob> candidates = serverLevel.getEntitiesOfClass(Mob.class,
                victim.getBoundingBox().inflate(cfg.radius.get()),
                candidate -> isPackCandidate(victim, candidate, now, cfg));

        int pulled = 0;
        for (Mob candidate : candidates) {
            if (pulled >= cfg.maxPackSize.get()) break;
            pullMob(candidate, player);
            LAST_PULL_TICK.put(candidate.getUUID(), now);
            pulled++;
        }
        if (pulled > 0) {
            BuffMobsMod.LOGGER.debug("[BuffMobs] Pack aggression: pulled {} mobs of type {}",
                    pulled, victim.getType().getDescriptionId());
        }
    }

    private static boolean isPackCandidate(Mob victim, Mob candidate, long now, BuffMobsConfig.PackAggression cfg) {
        if (candidate == victim) return false;
        if (candidate.getType() != victim.getType()) return false;
        if (!MobBuffUtil.isValidMob(candidate)) return false;
        Long lastPull = LAST_PULL_TICK.get(candidate.getUUID());
        return lastPull == null || now - lastPull >= cfg.cooldownTicks.get();
    }

    private static void pullMob(Mob mob, Player player) {
        mob.setTarget(player);
        if (mob instanceof NeutralMob neutralMob) {
            neutralMob.setPersistentAngerTarget(player.getUUID());
            neutralMob.startPersistentAngerTimer();
        }
    }

    private static Player resolveAttackingPlayer(LivingDamageEvent.Post event) {
        Entity attacker = event.getSource().getEntity();
        if (attacker instanceof Player player) return player;
        Entity direct = event.getSource().getDirectEntity();
        if (direct instanceof Projectile projectile && projectile.getOwner() instanceof Player player) return player;
        return null;
    }
}
