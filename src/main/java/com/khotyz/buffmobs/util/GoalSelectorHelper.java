package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.BuffMobsMod;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.ai.goal.CrossbowAttackGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.MobEntity;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class GoalSelectorHelper {
    private static Field goalSelectorField;
    private static Field goalSetField;
    private static Field goalField;
    private static boolean initialized = false;

    private static void initialize() {
        if (initialized) return;

        try {
            Class<MobEntity> mobClass = MobEntity.class;
            Field[] fields = mobClass.getDeclaredFields();

            for (Field field : fields) {
                if (field.getType() == GoalSelector.class) {
                    goalSelectorField = field;
                    goalSelectorField.setAccessible(true);
                    break;
                }
            }

            if (goalSelectorField == null) {
                String[] possibleNames = {"goalSelector", "field_6200", "f_21346_"};
                for (String name : possibleNames) {
                    try {
                        goalSelectorField = mobClass.getDeclaredField(name);
                        goalSelectorField.setAccessible(true);
                        break;
                    } catch (NoSuchFieldException ignored) {
                    }
                }
            }

            Class<GoalSelector> goalSelectorClass = GoalSelector.class;
            Field[] gsFields = goalSelectorClass.getDeclaredFields();

            for (Field field : gsFields) {
                if (field.getType() == Set.class) {
                    goalSetField = field;
                    goalSetField.setAccessible(true);
                    break;
                }
            }

            if (goalSetField == null) {
                String[] goalSetNames = {"goals", "field_1373", "f_25370_"};
                for (String name : goalSetNames) {
                    try {
                        goalSetField = goalSelectorClass.getDeclaredField(name);
                        goalSetField.setAccessible(true);
                        break;
                    } catch (NoSuchFieldException ignored) {
                    }
                }
            }

            String[] goalFieldNames = {"goal", "field_1370", "f_26030_"};
            for (String name : goalFieldNames) {
                try {
                    goalField = Class.forName("net.minecraft.entity.ai.goal.GoalSelector$PrioritizedGoal")
                            .getDeclaredField(name);
                    goalField.setAccessible(true);
                    break;
                } catch (ClassNotFoundException | NoSuchFieldException ignored) {
                }
            }

            initialized = true;
            BuffMobsMod.LOGGER.info("GoalSelectorHelper initialized successfully");
        } catch (Exception e) {
            BuffMobsMod.LOGGER.error("Failed to initialize GoalSelectorHelper", e);
        }
    }

    public static void addGoal(MobEntity mob, int priority, Goal goal) {
        initialize();
        try {
            if (goalSelectorField != null) {
                GoalSelector goalSelector = (GoalSelector) goalSelectorField.get(mob);
                goalSelector.add(priority, goal);
            }
        } catch (Exception e) {
            BuffMobsMod.LOGGER.error("Failed to add goal", e);
        }
    }

    public static void removeGoal(MobEntity mob, Goal goal) {
        initialize();
        try {
            if (goalSelectorField != null) {
                GoalSelector goalSelector = (GoalSelector) goalSelectorField.get(mob);
                goalSelector.remove(goal);
            }
        } catch (Exception e) {
            BuffMobsMod.LOGGER.error("Failed to remove goal", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static void removeAttackGoals(MobEntity mob) {
        initialize();
        try {
            if (goalSelectorField != null && goalSetField != null) {
                GoalSelector goalSelector = (GoalSelector) goalSelectorField.get(mob);
                Set<Object> goals = (Set<Object>) goalSetField.get(goalSelector);

                List<Goal> goalsToRemove = new ArrayList<>();

                for (Object prioritizedGoal : goals) {
                    try {
                        Goal currentGoal = null;

                        if (goalField != null) {
                            currentGoal = (Goal) goalField.get(prioritizedGoal);
                        } else {
                            Field[] fields = prioritizedGoal.getClass().getDeclaredFields();
                            for (Field field : fields) {
                                if (field.getType().getSuperclass() == Goal.class ||
                                        field.getType() == Goal.class ||
                                        Goal.class.isAssignableFrom(field.getType())) {
                                    field.setAccessible(true);
                                    currentGoal = (Goal) field.get(prioritizedGoal);
                                    break;
                                }
                            }
                        }

                        if (currentGoal != null && isAttackGoal(currentGoal)) {
                            goalsToRemove.add(currentGoal);
                        }
                    } catch (Exception e) {
                        BuffMobsMod.LOGGER.debug("Error accessing goal field", e);
                    }
                }

                for (Goal goal : goalsToRemove) {
                    goalSelector.remove(goal);
                }
            }
        } catch (Exception e) {
            BuffMobsMod.LOGGER.error("Failed to remove attack goals", e);
        }
    }

    private static boolean isAttackGoal(Goal goal) {
        return goal instanceof BowAttackGoal ||
                goal instanceof CrossbowAttackGoal ||
                goal instanceof MeleeAttackGoal ||
                goal.getClass().getSimpleName().contains("MeleeAttackGoal") ||
                goal.getClass().getSimpleName().contains("AttackGoal");
    }

    public static boolean isInitialized() {
        return initialized && goalSelectorField != null;
    }
}