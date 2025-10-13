package com.khotyz.buffmobs.mixin;

import com.khotyz.buffmobs.util.RangedMobAIManager;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MobEntity.class, priority = 1200)
public class RangedMobEntityMixin {

    @Unique
    private boolean buffmobs$rangedInitialized = false;

    @Inject(
            method = "tick",
            at = @At("TAIL"),
            require = 0
    )
    private void buffmobs$onRangedTick(CallbackInfo ci) {
        MobEntity self = (MobEntity)(Object)this;

        if (self.getWorld().isClient()) return;
        if (!(self instanceof PathAwareEntity)) return;
        if (!RangedMobAIManager.isRangedMob(self)) return;

        if (!this.buffmobs$rangedInitialized) {
            RangedMobAIManager.initializeMob(self);
            this.buffmobs$rangedInitialized = true;
        }

        if (self.age % 20 == 0) {
            try {
                RangedMobAIManager.updateMobBehavior(self);
            } catch (Exception e) {
                // Fail silently
            }
        }
    }
}