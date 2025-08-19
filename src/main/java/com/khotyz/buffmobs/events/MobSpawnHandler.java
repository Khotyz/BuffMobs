package com.khotyz.buffmobs.events;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.util.MobBuffUtil;
import com.khotyz.buffmobs.util.FilterUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class MobSpawnHandler {

    public void onEntityLoad(Entity entity, ServerWorld world) {
        if (!BuffMobsConfig.isEnabled() || world.isClient() || !(entity instanceof MobEntity mob)) {
            return;
        }

        if (!FilterUtil.isValidMob(mob)) {
            return;
        }

        // Schedule buff application for next tick to ensure mob is fully loaded
        world.getServer().execute(() -> {
            try {
                if (mob.isAlive() && !mob.isRemoved() && FilterUtil.isValidMob(mob)) {
                    MobBuffUtil.applyBuffs(mob);
                }
            } catch (Exception e) {
                BuffMobsMod.LOGGER.error("Error applying buffs to mob: {}",
                        mob.getType().getUntranslatedName(), e);
            }
        });
    }
}