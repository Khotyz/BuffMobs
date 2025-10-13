package com.khotyz.buffmobs.mixin;

import com.khotyz.buffmobs.util.MobBuffUtil;
import com.khotyz.buffmobs.util.RangedMobAIManager;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MobEntity.class, priority = 900)
public class MobEntityMixin {

    @Unique
    private boolean buffmobs$initialized = false;

    @Inject(method = "tick", at = @At("HEAD"))
    private void buffmobs$onTick(CallbackInfo ci) {
        MobEntity self = (MobEntity)(Object)this;

        if (self.getWorld().isClient()) return;

        if (!this.buffmobs$initialized) {
            this.buffmobs$initialized = true;

            try {
                MobBuffUtil.applyBuffs(self);
                RangedMobAIManager.initializeMob(self);
            } catch (Throwable e) {
                // Silent fail
            }
        }

        if (self.age % 20 == 0) {
            try {
                RangedMobAIManager.updateMobBehavior(self);
            } catch (Throwable e) {
                // Silent fail
            }
        }
    }
}