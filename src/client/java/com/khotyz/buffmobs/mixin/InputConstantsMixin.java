package com.khotyz.buffmobs.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InputConstants.class, remap = false)
public abstract class InputConstantsMixin {

    @Inject(method = "isKeyDown(Lcom/mojang/blaze3d/platform/Window;I)Z", at = @At("HEAD"),
            cancellable = true, require = 0, remap = false)
    private static void buffmobs$ignoreNegativeWindowKey(Window window, int key,
                                                          CallbackInfoReturnable<Boolean> cir) {
        if (key < 0) cir.setReturnValue(false);
    }

    @Inject(method = "isKeyDown(I)Z", at = @At("HEAD"), cancellable = true, require = 0, remap = false)
    private static void buffmobs$ignoreNegativeKeyboardKey(int key, CallbackInfoReturnable<Boolean> cir) {
        if (key < 0) cir.setReturnValue(false);
    }
}
