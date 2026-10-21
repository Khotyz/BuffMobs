package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.util.MobBuffUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;

import java.util.List;
import java.util.Optional;

public class LootXpScalingHandler {
    private static final RandomSource RANDOM = RandomSource.create();

    public static boolean isEnabled() {
        return BuffMobsConfig.INSTANCE.enabled.get() && BuffMobsConfig.INSTANCE.lootXpScaling.enabled.get();
    }

    private static boolean hasEliteBonusRewards(Mob mob) {
        BuffMobsConfig.EliteMobs eliteCfg = BuffMobsConfig.INSTANCE.eliteMobs;
        return BuffMobsConfig.INSTANCE.enabled.get()
                && eliteCfg.enabled.get()
                && eliteCfg.bonusRewardsEnabled.get()
                && EliteMobHandler.isElite(mob);
    }

    @SubscribeEvent
    public void onExperienceDrop(LivingExperienceDropEvent event) {
        if (!BuffMobsConfig.INSTANCE.enabled.get()) return;
        if (!(event.getEntity() instanceof Mob mob)) return;
        if (!MobTickHandler.isInitialized(mob.getUUID())) return;
        double multiplier = 1.0;
        if (isEnabled() && BuffMobsConfig.INSTANCE.lootXpScaling.xpScalingEnabled.get()) {
            multiplier = Math.min(MobBuffUtil.getEffectiveHealthMultiplier(mob),
                    BuffMobsConfig.INSTANCE.lootXpScaling.maxXpMultiplier.get());
            if (BloodMoonHandler.isActiveForLoot(mob.level())) multiplier *= 2.0;
        }
        if (hasEliteBonusRewards(mob)) {
            multiplier *= BuffMobsConfig.INSTANCE.eliteMobs.rewardXpMultiplier.get();
        }
        if (multiplier <= 1.0) return;
        int scaled = (int) Math.round(event.getOriginalExperience() * multiplier);
        event.setDroppedExperience(scaled);
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        if (!BuffMobsConfig.INSTANCE.enabled.get()) return;
        if (!(event.getEntity() instanceof Mob mob)) return;
        if (!MobTickHandler.isInitialized(mob.getUUID())) return;
        if (!(mob.level() instanceof ServerLevel serverLevel)) return;

        int rolls = 0;
        if (isEnabled() && BuffMobsConfig.INSTANCE.lootXpScaling.extraLootRollsEnabled.get()) {
            rolls += computeExtraRolls(mob);
        }
        boolean eliteReward = hasEliteBonusRewards(mob);
        if (eliteReward) {
            rolls += BuffMobsConfig.INSTANCE.eliteMobs.rewardExtraLootRolls.get();
        }
        if (rolls <= 0) return;

        Optional<ResourceKey<LootTable>> lootTableKey = mob.getLootTable();
        if (lootTableKey.isEmpty() || serverLevel.getServer() == null) return;
        LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(lootTableKey.get());

        LootParams params = buildLootParams(serverLevel, mob, event.getSource());
        for (int i = 0; i < rolls; i++) {
            List<ItemStack> extraStacks = lootTable.getRandomItems(params);
            for (ItemStack stack : extraStacks) {
                if (stack.isEmpty()) continue;
                ItemEntity drop = new ItemEntity(serverLevel, mob.getX(), mob.getY(), mob.getZ(), stack);
                drop.setDefaultPickUpDelay();
                event.getDrops().add(drop);
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
        int max = BuffMobsConfig.INSTANCE.lootXpScaling.maxExtraLootRolls.get();
        if (BloodMoonHandler.isActiveForLoot(mob.level())) {
            rolls = Math.min(rolls * 2, max * 2);
        } else {
            rolls = Math.min(rolls, max);
        }
        return rolls;
    }

    private static LootParams buildLootParams(ServerLevel level, Mob mob, DamageSource source) {
        LootParams.Builder builder = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, mob)
                .withParameter(LootContextParams.ORIGIN, mob.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, source)
                .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, source.getEntity())
                .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, source.getDirectEntity());
        if (source.getEntity() instanceof Player player) {
            builder.withOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER, player);
            builder.withLuck((float) player.getAttributeValue(Attributes.LUCK));
        }
        return builder.create(LootContextParamSets.ENTITY);
    }
}
