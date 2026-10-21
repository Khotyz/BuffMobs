package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.util.MobBuffUtil;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;

public class LootXpScalingHandler {
    private static final RandomSource RANDOM = RandomSource.create();

    public static boolean isEnabled() {
        return BuffMobsConfig.INSTANCE.enabled && BuffMobsConfig.INSTANCE.lootXpScaling.enabled;
    }

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register(LootXpScalingHandler::onAfterDeath);
    }

    private static boolean hasEliteBonusRewards(Mob mob) {
        BuffMobsConfig.EliteMobs eliteCfg = BuffMobsConfig.INSTANCE.eliteMobs;
        return BuffMobsConfig.INSTANCE.enabled
                && eliteCfg.enabled
                && eliteCfg.bonusRewardsEnabled
                && EliteMobHandler.isElite(mob);
    }

    public static double getXpMultiplier(Mob mob) {
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

    private static void onAfterDeath(LivingEntity entity, DamageSource source) {
        if (!BuffMobsConfig.INSTANCE.enabled) return;
        if (!(entity instanceof Mob mob)) return;
        if (!MobTickHandler.isInitialized(mob.getUUID())) return;
        if (!(mob.level() instanceof ServerLevel serverLevel)) return;
        if (!serverLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) return;
        if (mob.isBaby()) return;
        Player killer = resolveKiller(source);
        if (killer == null) return;

        awardBonusExperience(mob, serverLevel, killer);
        dropExtraLoot(mob, serverLevel, source, killer);
    }

    private static void awardBonusExperience(Mob mob, ServerLevel serverLevel, Player killer) {
        double multiplier = getXpMultiplier(mob);
        if (multiplier <= 1.0) return;
        if (mob.wasExperienceConsumed()) return;
        int baseExperience = mob.getExperienceReward(serverLevel, killer);
        if (baseExperience <= 0) return;
        int bonus = (int) Math.round(baseExperience * (multiplier - 1.0));
        if (bonus <= 0) return;
        ExperienceOrb.award(serverLevel, mob.position(), bonus);
        BuffMobsMod.LOGGER.debug("[BuffMobs] XP scaling: {} bonus XP for {} (multiplier={})",
                bonus, mob.getType().getDescriptionId(), multiplier);
    }

    private static void dropExtraLoot(Mob mob, ServerLevel serverLevel, DamageSource source, Player killer) {
        int rolls = 0;
        if (isEnabled() && BuffMobsConfig.INSTANCE.lootXpScaling.extraLootRollsEnabled) {
            rolls += computeExtraRolls(mob);
        }
        boolean eliteReward = hasEliteBonusRewards(mob);
        if (eliteReward) {
            rolls += BuffMobsConfig.INSTANCE.eliteMobs.rewardExtraLootRolls;
        }
        if (rolls <= 0) return;

        ResourceKey<LootTable> lootTableKey = mob.getLootTable();
        if (lootTableKey == null || serverLevel.getServer() == null) return;
        LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(lootTableKey);

        LootParams params = buildLootParams(serverLevel, mob, source, killer);
        for (int i = 0; i < rolls; i++) {
            List<ItemStack> extraStacks = lootTable.getRandomItems(params);
            for (ItemStack stack : extraStacks) {
                if (stack.isEmpty()) continue;
                ItemEntity drop = new ItemEntity(serverLevel, mob.getX(), mob.getY(), mob.getZ(), stack);
                drop.setDefaultPickUpDelay();
                serverLevel.addFreshEntity(drop);
            }
        }
        BuffMobsMod.LOGGER.debug("[BuffMobs] Loot scaling: {} extra rolls for {} (elite={})",
                rolls, mob.getType().getDescriptionId(), eliteReward);
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

    private static Player resolveKiller(DamageSource source) {
        if (source.getEntity() instanceof Player player) return player;
        return null;
    }

    private static LootParams buildLootParams(ServerLevel level, Mob mob, DamageSource source, Player killer) {
        return new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, mob)
                .withParameter(LootContextParams.ORIGIN, mob.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, source)
                .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, source.getEntity())
                .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, source.getDirectEntity())
                .withOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER, killer)
                .withLuck((float) killer.getAttributeValue(Attributes.LUCK))
                .create(LootContextParamSets.ENTITY);
    }
}
