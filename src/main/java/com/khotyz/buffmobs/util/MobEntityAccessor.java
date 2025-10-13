package com.khotyz.buffmobs.util;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.mob.MobEntity;

import java.lang.reflect.Field;
import java.util.Set;

public class MobEntityAccessor {
    private static Field goalSelectorField;
    private static Field goalsField;

    static {
        try {
            goalSelectorField = MobEntity.class.getDeclaredField("goalSelector");
            goalSelectorField.setAccessible(true);

            goalsField = GoalSelector.class.getDeclaredField("goals");
            goalsField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to access MobEntity fields", e);
        }
    }

    public static void addGoal(MobEntity mob, int priority, Goal goal) {
        try {
            GoalSelector selector = (GoalSelector) goalSelectorField.get(mob);
            selector.add(priority, goal);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to add goal", e);
        }
    }

    public static void removeGoal(MobEntity mob, Goal goal) {
        try {
            GoalSelector selector = (GoalSelector) goalSelectorField.get(mob);
            selector.remove(goal);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to remove goal", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Set<PrioritizedGoal> getGoals(MobEntity mob) {
        try {
            GoalSelector selector = (GoalSelector) goalSelectorField.get(mob);
            return (Set<PrioritizedGoal>) goalsField.get(selector);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to get goals", e);
        }
    }
}