package com.khotyz.buffmobs.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class EnhancedVitalityEffect extends StatusEffect {
    public EnhancedVitalityEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        // Visual effect only - actual buffs handled by attributes
        return false;
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        super.onApplied(entity, amplifier);
    }

    @Override
    public void onRemoved(AttributeContainer attributeContainer) {
        super.onRemoved(attributeContainer);
    }

    @Override
    public boolean isInstant() {
        return false;
    }
}