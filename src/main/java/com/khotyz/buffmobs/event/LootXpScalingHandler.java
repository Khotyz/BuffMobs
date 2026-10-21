package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.util.MobBuffUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;

public class LootXpScalingHandler {

    private static final RandomSource RANDOM = RandomSource.create();

    public static boolean isEnabled() {
        return BuffMobsConfig.INSTANCE.enabled && BuffMobsConfig.INSTANCE.lootXpScaling.enabled;
    }

    public static boolean hasEliteBonusRewards(Mob mob) {
        BuffMobsConfig.EliteMobs eliteCfg = BuffMobsConfig.INSTANCE.eliteMobs;
        return BuffMobsConfig.INSTANCE.enabled
                && eliteCfg.enabled
                && eliteCfg.bonusRewardsEnabled
                && EliteMobHandler.isElite(mob);
    }

    public static int scaledExperience(Mob mob, int originalExperience) {
        double multiplier = experienceMultiplier(mob);
        if (multiplier <= 1.0) return originalExperience;
        return (int) Math.round(originalExperience * multiplier);
    }

    public static double experienceMultiplier(Mob mob) {
        if (!BuffMobsConfig.INSTANCE.enabled) return 1.0;
        if (!MobTickHandler.isInitialized(mob.getUUID())) return 1.0;
        double multiplier = 1.0;
        if (isEnabled() && BuffMobsConfig.INSTANCE.lootXpScaling.xpScalingEnabled) {
            multiplier = Math.min(MobBuffUtil.getEffectiveHealthMultiplier(mob),
                    BuffMobsConfig.INSTANCE.lootXpScaling.maxXpMultiplier);
            if (BloodMoonHandler.isActiveForLoot(mob.level())) multiplier *= 2.0;
        }
        if (hasEliteBonusRewards(mob)) {
            multiplier *= BuffMobsConfig.INSTANCE.eliteMobs.rewardXpMultiplier;
        }
        return multiplier;
    }

    public static int extraLootRolls(Mob mob) {
        if (!BuffMobsConfig.INSTANCE.enabled) return 0;
        if (!MobTickHandler.isInitialized(mob.getUUID())) return 0;

        int rolls = 0;
        if (isEnabled() && BuffMobsConfig.INSTANCE.lootXpScaling.extraLootRollsEnabled) {
            rolls += computeExtraRolls(mob);
        }
        boolean eliteReward = hasEliteBonusRewards(mob);
        if (eliteReward) {
            rolls += BuffMobsConfig.INSTANCE.eliteMobs.rewardExtraLootRolls;
        }
        if (rolls > 0) {
            BuffMobsMod.LOGGER.debug("[BuffMobs] Loot scaling: {} extra rolls for {} (elite={})",
                    rolls, mob.getType().getDescriptionId(), eliteReward);
        }
        return rolls;
    }

    private static int computeExtraRolls(Mob mob) {
        double effectiveMultiplier = MobBuffUtil.getEffectiveHealthMultiplier(mob);
        double extra = effectiveMultiplier - 1.0;
        if (extra <= 0.0) return 0;
        int rolls = (int) Math.floor(extra);
        double fraction = extra - rolls;
        if (fraction > 0.0 && RANDOM.nextDouble() < fraction) rolls++;
        int max = BuffMobsConfig.INSTANCE.lootXpScaling.maxExtraLootRolls;
        if (BloodMoonHandler.isActiveForLoot(mob.level())) {
            rolls = Math.min(rolls * 2, max * 2);
        } else {
            rolls = Math.min(rolls, max);
        }
        return rolls;
    }
}
