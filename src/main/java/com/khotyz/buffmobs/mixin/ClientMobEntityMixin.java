package com.khotyz.buffmobs.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = MobEntity.class, priority = 900)
public class ClientMobEntityMixin {

    @Inject(method = "tick()V", at = @At("HEAD"), require = 0)
    private void buffmobs$onClientTick(CallbackInfo ci) {
        // Client-side visual handling is automatic via vanilla particles
    }
}