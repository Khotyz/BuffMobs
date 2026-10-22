package com.khotyz.buffmobs.mixin;

import com.khotyz.buffmobs.event.LootXpScalingHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityLootXpMixin {

    @WrapMethod(method = "dropFromLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;Z)V")
    private void buffmobs$extraLootRolls(ServerLevel level, DamageSource source, boolean playerKilled,
                                         Operation<Void> original) {
        original.call(level, source, playerKilled);
        if (!((Object) this instanceof Mob mob)) return;
        int extraRolls = LootXpScalingHandler.extraLootRolls(mob);
        for (int i = 0; i < extraRolls; i++) {
            original.call(level, source, playerKilled);
        }
    }

    @WrapOperation(method = "dropExperience",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"))
    private void buffmobs$scaleExperience(ServerLevel level, Vec3 position, int amount, Operation<Void> original) {
        int scaled = amount;
        if ((Object) this instanceof Mob mob) scaled = LootXpScalingHandler.scaledExperience(mob, amount);
        original.call(level, position, scaled);
    }
}
