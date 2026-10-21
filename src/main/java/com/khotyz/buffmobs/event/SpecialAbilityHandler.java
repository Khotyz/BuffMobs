package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.util.MobBuffUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class SpecialAbilityHandler {

    public enum Ability {
        DEATHRATTLE("deathrattle"),
        LASTSTAND("laststand"),
        Teleport("teleport"),
        Summon("summon");

        private final String id;

        Ability(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }

        public String translationKey() {
            return "buffmobs.ability." + id;
        }

        public static Ability fromId(String id) {
            for (Ability ability : values()) {
                if (ability.id.equalsIgnoreCase(id)) return ability;
            }
            return null;
        }
    }

    private static final RandomSource RANDOM = RandomSource.create();
    private static final BuffMobsConfig.SpecialAbilities.Deathrattle.DeathrattleMode[] RANDOM_DEATHRATTLE_MODES = {
            BuffMobsConfig.SpecialAbilities.Deathrattle.DeathrattleMode.EXPLOSION,
            BuffMobsConfig.SpecialAbilities.Deathrattle.DeathrattleMode.POISON_CLOUD,
            BuffMobsConfig.SpecialAbilities.Deathrattle.DeathrattleMode.Summon_MINIONS
    };
    private static final Map<UUID, AbilityState> STATES = new HashMap<>();
    private static final Set<UUID> SUMMONED_MINIONS = new HashSet<>();

    public static boolean isEnabled() {
        return BuffMobsConfig.INSTANCE.enabled.get() && BuffMobsConfig.INSTANCE.specialAbilities.enabled.get();
    }

    public static EnumSet<Ability> getAbilities(Mob mob) {
        AbilityState state = STATES.get(mob.getUUID());
        return state != null ? state.abilities : EnumSet.noneOf(Ability.class);
    }

    public static void onMobInitialized(Mob mob) {
        if (!isEnabled()) return;
        if (!MobBuffUtil.isValidMob(mob)) return;
        if (SUMMONED_MINIONS.contains(mob.getUUID())) return;
        String mobId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        EnumSet<Ability> abilities = parseAbilitiesFor(mobId);
        if (abilities.isEmpty()) return;
        STATES.putIfAbsent(mob.getUUID(), new AbilityState(abilities));
    }

    public static void onMobRemoved(Mob mob) {
        STATES.remove(mob.getUUID());
        SUMMONED_MINIONS.remove(mob.getUUID());
    }

    public static void clearCaches() {
        STATES.clear();
        SUMMONED_MINIONS.clear();
    }

    @SubscribeEvent
    public void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Mob mob)) return;
        AbilityState state = STATES.get(mob.getUUID());
        if (state == null) return;
        if (state.abilities.contains(Ability.Summon)) {
            tickSummon(mob, state);
        }
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent.Post event) {
        if (!isEnabled()) return;
        if (!(event.getEntity() instanceof Mob mob)) return;
        AbilityState state = STATES.get(mob.getUUID());
        if (state == null) return;
        if (state.abilities.contains(Ability.LASTSTAND)) {
            tryLastStand(mob, state);
        }
        if (state.abilities.contains(Ability.Teleport)) {
            LivingEntity attacker = resolveAttacker(event);
            if (attacker != null) tryTeleport(mob, attacker, state);
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Mob mob)) return;
        SUMMONED_MINIONS.remove(mob.getUUID());
        AbilityState state = STATES.remove(mob.getUUID());
        if (!isEnabled() || state == null) return;
        if (!state.abilities.contains(Ability.DEATHRATTLE)) return;
        if (!(mob.level() instanceof ServerLevel serverLevel)) return;
        triggerDeathrattle(mob, serverLevel, event);
    }

    private static EnumSet<Ability> parseAbilitiesFor(String mobId) {
        EnumSet<Ability> abilities = EnumSet.noneOf(Ability.class);
        for (String entry : BuffMobsConfig.INSTANCE.specialAbilities.mobAbilities.get()) {
            String[] parts = entry.split(":");
            if (parts.length < 3) {
                BuffMobsMod.LOGGER.warn("[BuffMobs] Invalid ability mapping format: '{}' (expected modid:mob_id:ability_id)", entry);
                continue;
            }
            String mappedMobId = parts[0] + ":" + parts[1];
            if (!mappedMobId.equals(mobId)) continue;
            Ability ability = Ability.fromId(parts[2].trim());
            if (ability != null) {
                abilities.add(ability);
            } else {
                BuffMobsMod.LOGGER.warn("[BuffMobs] Unknown ability id: '{}' in mapping '{}'", parts[2], entry);
            }
        }
        return abilities;
    }

    private static void tickSummon(Mob mob, AbilityState state) {
        BuffMobsConfig.SpecialAbilities.Summon cfg = BuffMobsConfig.INSTANCE.specialAbilities.Summon;
        if (cfg.maxAliveMinions.get() <= 0) return;
        if (!(mob.level() instanceof ServerLevel serverLevel)) return;
        if (cfg.onlyInCombat.get() && mob.getTarget() == null) return;
        long now = serverLevel.getGameTime();
        if (state.nextSummonTick == 0L) {
            state.nextSummonTick = now + cfg.intervalTicks.get();
            return;
        }
        if (now < state.nextSummonTick) return;
        state.nextSummonTick = now + cfg.intervalTicks.get();
        if (countAliveMinions(serverLevel, state) >= cfg.maxAliveMinions.get()) return;
        Mob minion = spawnMinion(serverLevel, mob.position(), cfg.minionEntityId.get());
        if (minion != null) {
            state.minions.add(minion.getUUID());
            if (mob.getTarget() != null) minion.setTarget(mob.getTarget());
        }
    }

    private static int countAliveMinions(ServerLevel level, AbilityState state) {
        state.minions.removeIf(uuid -> {
            Entity entity = level.getEntity(uuid);
            return !(entity instanceof Mob minion) || !minion.isAlive() || minion.isRemoved();
        });
        return state.minions.size();
    }

    private static void tryLastStand(Mob mob, AbilityState state) {
        if (state.lastStandUsed) return;
        BuffMobsConfig.SpecialAbilities.LastStand cfg = BuffMobsConfig.INSTANCE.specialAbilities.lastStand;
        if (mob.getHealth() <= 0.0f) return;
        if (mob.getHealth() > mob.getMaxHealth() * cfg.healthThreshold.get()) return;
        state.lastStandUsed = true;
        mob.addEffect(new MobEffectInstance(MobEffects.RESISTANCE,
                cfg.resistanceDurationSeconds.get() * 20,
                Math.max(0, cfg.resistanceLevel.get() - 1), false, true, true));
        if (mob.level() instanceof ServerLevel serverLevel && BuffMobsConfig.INSTANCE.visualEffects.get()) {
            serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                    mob.getX(), mob.getY() + mob.getBbHeight() * 0.5, mob.getZ(),
                    20, mob.getBbWidth() * 0.5, mob.getBbHeight() * 0.5, mob.getBbWidth() * 0.5, 0.25);
        }
        BuffMobsMod.LOGGER.debug("[BuffMobs] Last Stand triggered for {}", mob.getType().getDescriptionId());
    }

    private static void tryTeleport(Mob mob, LivingEntity attacker, AbilityState state) {
        BuffMobsConfig.SpecialAbilities.Teleport cfg = BuffMobsConfig.INSTANCE.specialAbilities.Teleport;
        if (!(mob.level() instanceof ServerLevel serverLevel)) return;
        long now = serverLevel.getGameTime();
        if (now - state.lastTeleportTick < cfg.cooldownTicks.get()) return;
        if (RANDOM.nextDouble() >= cfg.chance.get()) return;

        Vec3 look = attacker.getLookAngle();
        Vec3 horizontalLook = new Vec3(look.x, 0.0, look.z);
        if (horizontalLook.lengthSqr() < 0.001) return;
        horizontalLook = horizontalLook.normalize();
        Vec3 behind = attacker.position().subtract(horizontalLook.scale(cfg.distanceBehindAttacker.get()));

        double[] yOffsets = {0.0, 1.0, -1.0, 2.0, -2.0};
        for (double yOffset : yOffsets) {
            double x = behind.x;
            double y = attacker.getY() + yOffset;
            double z = behind.z;
            if (!isSafeTeleportDestination(serverLevel, mob, x, y, z)) continue;
            spawnTeleportParticles(serverLevel, mob);
            mob.teleportTo(x, y, z);
            mob.getLookControl().setLookAt(attacker.getX(), attacker.getEyeY(), attacker.getZ());
            mob.setTarget(attacker);
            spawnTeleportParticles(serverLevel, mob);
            state.lastTeleportTick = now;
            return;
        }
    }

    private static boolean isSafeTeleportDestination(ServerLevel level, Mob mob, double x, double y, double z) {
        double halfWidth = mob.getBbWidth() * 0.5;
        AABB destinationBox = new AABB(x - halfWidth, y, z - halfWidth,
                x + halfWidth, y + mob.getBbHeight(), z + halfWidth);
        if (!level.noCollision(mob, destinationBox)) return false;
        BlockPos feetPos = BlockPos.containing(x, y, z);
        if (level.getFluidState(feetPos).is(FluidTags.LAVA)) return false;
        BlockPos belowPos = BlockPos.containing(x, y - 0.5, z);
        return level.getBlockState(belowPos).blocksMotion();
    }

    private static void spawnTeleportParticles(ServerLevel level, Mob mob) {
        if (!BuffMobsConfig.INSTANCE.visualEffects.get()) return;
        level.sendParticles(ParticleTypes.PORTAL,
                mob.getX(), mob.getY() + mob.getBbHeight() * 0.5, mob.getZ(),
                16, mob.getBbWidth() * 0.5, mob.getBbHeight() * 0.5, mob.getBbWidth() * 0.5, 0.1);
    }

    private static void triggerDeathrattle(Mob mob, ServerLevel level, LivingDeathEvent event) {
        BuffMobsConfig.SpecialAbilities.Deathrattle cfg = BuffMobsConfig.INSTANCE.specialAbilities.deathrattle;
        BuffMobsConfig.SpecialAbilities.Deathrattle.DeathrattleMode mode = cfg.mode.get();
        if (mode == BuffMobsConfig.SpecialAbilities.Deathrattle.DeathrattleMode.RANDOM) {
            mode = RANDOM_DEATHRATTLE_MODES[RANDOM.nextInt(RANDOM_DEATHRATTLE_MODES.length)];
        }
        switch (mode) {
            case EXPLOSION -> level.explode(null,
                    mob.getX(), mob.getY() + mob.getBbHeight() * 0.5, mob.getZ(),
                    cfg.explosionPower.get().floatValue(),
                    cfg.explosionBreaksBlocks.get() ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE);
            case POISON_CLOUD -> {
                AreaEffectCloud cloud = new AreaEffectCloud(level, mob.getX(), mob.getY(), mob.getZ());
                cloud.setRadius(cfg.poisonCloudRadius.get().floatValue());
                cloud.setDuration(cfg.poisonCloudDurationSeconds.get() * 20);
                cloud.setWaitTime(0);
                cloud.addEffect(new MobEffectInstance(MobEffects.POISON, 100,
                        Math.max(0, cfg.poisonCloudLevel.get() - 1)));
                level.addFreshEntity(cloud);
            }
            case Summon_MINIONS -> {
                int min = Math.max(0, cfg.minionMinCount.get());
                int max = Math.max(min, cfg.minionMaxCount.get());
                int count = min + (max > min ? RANDOM.nextInt(max - min + 1) : 0);
                LivingEntity killer = resolveAttacker(event);
                for (int i = 0; i < count; i++) {
                    Mob minion = spawnMinion(level, mob.position(), cfg.minionEntityId.get());
                    if (minion != null && killer != null) minion.setTarget(killer);
                }
            }
            case RANDOM -> {
            }
        }
        BuffMobsMod.LOGGER.debug("[BuffMobs] Deathrattle ({}) triggered for {}", mode, mob.getType().getDescriptionId());
    }

    private static Mob spawnMinion(ServerLevel level, Vec3 position, String entityId) {
        Identifier id = Identifier.tryParse(entityId);
        if (id == null) {
            BuffMobsMod.LOGGER.warn("[BuffMobs] Invalid minion entity id: '{}'", entityId);
            return null;
        }
        Optional<EntityType<?>> type = BuiltInRegistries.ENTITY_TYPE.getOptional(id);
        if (type.isEmpty()) {
            BuffMobsMod.LOGGER.warn("[BuffMobs] Unknown minion entity id: '{}'", entityId);
            return null;
        }
        Entity created = type.get().create(level, EntitySpawnReason.MOB_SUMMONED);
        if (!(created instanceof Mob minion)) return null;
        double offsetX = (RANDOM.nextDouble() - 0.5) * 2.0;
        double offsetZ = (RANDOM.nextDouble() - 0.5) * 2.0;
        minion.snapTo(position.x + offsetX, position.y, position.z + offsetZ, RANDOM.nextFloat() * 360.0f, 0.0f);
        minion.finalizeSpawn(level, level.getCurrentDifficultyAt(minion.blockPosition()), EntitySpawnReason.MOB_SUMMONED, null);
        SUMMONED_MINIONS.add(minion.getUUID());
        level.addFreshEntity(minion);
        return minion;
    }

    private static LivingEntity resolveAttacker(LivingDamageEvent.Post event) {
        Entity attacker = event.getSource().getEntity();
        if (attacker instanceof LivingEntity living) return living;
        Entity direct = event.getSource().getDirectEntity();
        if (direct instanceof Projectile projectile && projectile.getOwner() instanceof LivingEntity living) return living;
        return null;
    }

    private static LivingEntity resolveAttacker(LivingDeathEvent event) {
        Entity attacker = event.getSource().getEntity();
        if (attacker instanceof LivingEntity living) return living;
        Entity direct = event.getSource().getDirectEntity();
        if (direct instanceof Projectile projectile && projectile.getOwner() instanceof LivingEntity living) return living;
        return null;
    }

    private static class AbilityState {
        final EnumSet<Ability> abilities;
        final Set<UUID> minions = new HashSet<>();
        boolean lastStandUsed = false;
        long lastTeleportTick = -100000L;
        long nextSummonTick = 0L;

        AbilityState(EnumSet<Ability> abilities) {
            this.abilities = abilities;
        }
    }
}
