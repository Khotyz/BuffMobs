package com.khotyz.buffmobs.util;

import com.khotyz.buffmobs.config.BuffMobsConfig;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

public class FilterUtil {

    public static boolean isValidMob(MobEntity mob) {
        if (mob == null || !mob.isAlive()) {
            return false;
        }

        // Check if mob is hostile or potentially hostile
        if (!isHostileOrNeutralMob(mob)) {
            return false;
        }

        Identifier mobId = Registries.ENTITY_TYPE.getId(mob.getType());
        if (mobId == null) {
            return false;
        }

        String mobName = mobId.toString();
        String modId = mobId.getNamespace();

        try {
            String dimensionName = mob.getWorld().getRegistryKey().getValue().toString();

            // Check filters in order: dimension, mod, mob
            if (!isValidDimension(dimensionName)) {
                return false;
            }

            if (!isValidModId(modId)) {
                return false;
            }

            if (!isValidMobName(mobName)) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isHostileOrNeutralMob(MobEntity mob) {
        // Always hostile mobs
        if (mob instanceof Monster) {
            return true;
        }

        // Angerable mobs (can become hostile)
        if (mob instanceof Angerable) {
            return true;
        }

        // Specific neutral/conditionally hostile mobs
        if (mob instanceof EndermanEntity ||
                mob instanceof SpiderEntity ||
                mob instanceof ZombifiedPiglinEntity ||
                mob instanceof AbstractPiglinEntity) {
            return true;
        }

        // Animals that can be hostile
        if (mob instanceof WolfEntity ||
                mob instanceof BeeEntity ||
                mob instanceof PolarBearEntity ||
                mob instanceof LlamaEntity ||
                mob instanceof PandaEntity ||
                mob instanceof DolphinEntity) {
            return true;
        }

        // Golems (can be hostile to certain entities)
        if (mob instanceof GolemEntity || mob instanceof IronGolemEntity) {
            return true;
        }

        // Check by entity type for modded mobs that might not implement proper interfaces
        EntityType<?> type = mob.getType();
        String typeName = Registries.ENTITY_TYPE.getId(type).toString().toLowerCase();

        // Common naming patterns for hostile/neutral mobs
        return typeName.contains("zombie") ||
                typeName.contains("skeleton") ||
                typeName.contains("spider") ||
                typeName.contains("creeper") ||
                typeName.contains("witch") ||
                typeName.contains("enderman") ||
                typeName.contains("blaze") ||
                typeName.contains("ghast") ||
                typeName.contains("slime") ||
                typeName.contains("magma") ||
                typeName.contains("phantom") ||
                typeName.contains("husk") ||
                typeName.contains("stray") ||
                typeName.contains("drowned") ||
                typeName.contains("pillager") ||
                typeName.contains("vindicator") ||
                typeName.contains("evoker") ||
                typeName.contains("ravager") ||
                typeName.contains("vex") ||
                typeName.contains("guardian") ||
                typeName.contains("elder") ||
                typeName.contains("shulker") ||
                typeName.contains("piglin") ||
                typeName.contains("hoglin") ||
                typeName.contains("zoglin") ||
                typeName.contains("warden") ||
                typeName.contains("wither") ||
                typeName.contains("boss") ||
                typeName.contains("golem") ||
                typeName.contains("hostile") ||
                typeName.contains("monster") ||
                typeName.contains("demon") ||
                typeName.contains("devil") ||
                typeName.contains("undead") ||
                typeName.contains("warrior") ||
                typeName.contains("soldier") ||
                typeName.contains("guard") ||
                typeName.contains("bandit") ||
                typeName.contains("raider") ||
                typeName.contains("orc") ||
                typeName.contains("goblin") ||
                typeName.contains("troll");
    }

    private static boolean isValidDimension(String dimensionName) {
        if (BuffMobsConfig.getDimensionBlacklist().contains(dimensionName)) {
            return false;
        }

        if (BuffMobsConfig.useDimensionWhitelist()) {
            return BuffMobsConfig.getDimensionWhitelist().contains(dimensionName);
        }

        return true;
    }

    private static boolean isValidModId(String modId) {
        if (BuffMobsConfig.getModidBlacklist().contains(modId)) {
            return false;
        }

        if (BuffMobsConfig.useModidWhitelist()) {
            return BuffMobsConfig.getModidWhitelist().contains(modId);
        }

        return true;
    }

    private static boolean isValidMobName(String mobName) {
        if (BuffMobsConfig.getMobBlacklist().contains(mobName)) {
            return false;
        }

        if (BuffMobsConfig.useMobWhitelist()) {
            return BuffMobsConfig.getMobWhitelist().contains(mobName);
        }

        return true;
    }
}