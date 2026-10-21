package com.khotyz.buffmobs.mixin;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.event.BloodMoonHandler;
import com.khotyz.buffmobs.util.MobBuffUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobMixin {

    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    private void buffmobs$duplicateBloodMoonSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                                  MobSpawnType spawnType, SpawnGroupData spawnData,
                                                  CallbackInfoReturnable<SpawnGroupData> callbackInfo) {
        if (spawnType != MobSpawnType.NATURAL) return;
        if (!(level instanceof ServerLevel serverLevel)) return;
        Mob self = (Mob) (Object) this;
        if (!BloodMoonHandler.shouldDuplicateSpawn(serverLevel)) return;
        if (!MobBuffUtil.isValidMob(self)) return;
        try {
            Entity created = self.getType().create(serverLevel);
            if (!(created instanceof Mob duplicate)) return;
            duplicate.moveTo(self.getX(), self.getY(), self.getZ(),
                    self.getRandom().nextFloat() * 360.0f, 0.0f);
            duplicate.finalizeSpawn(serverLevel, difficulty, MobSpawnType.EVENT, null);
            serverLevel.addFreshEntity(duplicate);
        } catch (Exception ex) {
            BuffMobsMod.LOGGER.warn("[BuffMobs] Blood Moon bonus spawn failed", ex);
        }
    }
}
