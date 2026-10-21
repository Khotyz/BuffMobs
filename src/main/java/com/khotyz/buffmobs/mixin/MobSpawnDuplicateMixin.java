package com.khotyz.buffmobs.mixin;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.event.BloodMoonHandler;
import com.khotyz.buffmobs.util.MobBuffUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobSpawnDuplicateMixin {

    @Unique
    private static final RandomSource buffmobs$RANDOM = RandomSource.create();

    @Unique
    private boolean buffmobs$duplicatingSpawn = false;

    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    private void buffmobs$duplicateBloodMoonSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                                  EntitySpawnReason spawnReason, SpawnGroupData groupData,
                                                  CallbackInfoReturnable<SpawnGroupData> cir) {
        if (buffmobs$duplicatingSpawn) return;
        if (spawnReason != EntitySpawnReason.NATURAL) return;
        BuffMobsConfig.BloodMoon cfg = BuffMobsConfig.INSTANCE.bloodMoon;
        if (!BuffMobsConfig.INSTANCE.enabled || !cfg.enabled || !cfg.spawnBoostEnabled) return;
        if (!((Object) this instanceof Mob mob)) return;
        if (!(mob.level() instanceof ServerLevel serverLevel)) return;
        if (!BloodMoonHandler.isActive(serverLevel)) return;
        if (!MobBuffUtil.isValidMob(mob)) return;
        if (buffmobs$RANDOM.nextDouble() >= cfg.extraSpawnChance) return;

        Entity created = mob.getType().create(serverLevel, EntitySpawnReason.EVENT);
        if (!(created instanceof Mob duplicate)) return;
        buffmobs$duplicatingSpawn = true;
        try {
            duplicate.setPos(mob.getX(), mob.getY(), mob.getZ());
            duplicate.finalizeSpawn(level, difficulty, EntitySpawnReason.EVENT, null);
            serverLevel.addFreshEntity(duplicate);
        } catch (Exception ex) {
            BuffMobsMod.LOGGER.warn("[BuffMobs] Blood Moon bonus spawn failed", ex);
        } finally {
            buffmobs$duplicatingSpawn = false;
        }
    }
}
