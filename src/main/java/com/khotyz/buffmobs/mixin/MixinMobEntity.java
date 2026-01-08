package com.khotyz.buffmobs.mixin;

import com.khotyz.buffmobs.util.RangedMobAIManager;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public class MixinMobEntity {

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void onTickMovementHead(CallbackInfo ci) {
        MobEntity self = (MobEntity) (Object) this;

        if (RangedMobAIManager.shouldCancelRangedAttack(self)) {
            self.stopUsingItem();
            self.clearActiveItem();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTickHead(CallbackInfo ci) {
        MobEntity self = (MobEntity) (Object) this;

        if (RangedMobAIManager.shouldCancelRangedAttack(self)) {
            self.stopUsingItem();
            self.clearActiveItem();
        }
    }
}