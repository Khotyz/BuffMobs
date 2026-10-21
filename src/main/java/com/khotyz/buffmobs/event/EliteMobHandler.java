package com.khotyz.buffmobs.event;

import com.khotyz.buffmobs.BuffMobsMod;
import com.khotyz.buffmobs.config.BuffMobsConfig;
import com.khotyz.buffmobs.util.MobBuffUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EliteMobHandler {

    private static final Identifier ELITE_HEALTH_MOD_ID = Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "elite_health");
    private static final Identifier ELITE_DAMAGE_MOD_ID = Identifier.fromNamespaceAndPath(BuffMobsMod.MOD_ID, "elite_damage");
    private static final RandomSource RANDOM = RandomSource.create();
    private static final int[] RANDOM_NAME_COLORS = {0xFFAA00, 0xFF5555, 0xAA0000, 0xFF55FF, 0x55FFFF, 0xFFFF55};

    private static final Set<UUID> EVALUATED_MOBS = new HashSet<>();
    private static final Set<UUID> ELITE_MOBS = new HashSet<>();
    private static final Set<UUID> ELITE_NAMED_MOBS = new HashSet<>();
    private static final Set<UUID> NOTIFIED_MOBS = new HashSet<>();

    public static boolean isEnabled() {
        return BuffMobsConfig.INSTANCE.enabled && BuffMobsConfig.INSTANCE.eliteMobs.enabled;
    }

    public static boolean isElite(Mob mob) {
        return ELITE_MOBS.contains(mob.getUUID());
    }

    public static void onMobInitialized(Mob mob) {
        if (!isEnabled()) return;
        if (!MobBuffUtil.isValidMob(mob)) return;
        if (!EVALUATED_MOBS.add(mob.getUUID())) return;
        if (RANDOM.nextDouble() < getEliteChance(mob)) {
            makeElite(mob);
        }
    }

    public static void onMobRemoved(Mob mob) {
        EVALUATED_MOBS.remove(mob.getUUID());
        NOTIFIED_MOBS.remove(mob.getUUID());
        removeElite(mob);
    }

    public static void clearCaches() {
        EVALUATED_MOBS.clear();
        ELITE_MOBS.clear();
        ELITE_NAMED_MOBS.clear();
        NOTIFIED_MOBS.clear();
    }

    public static void tick(Mob mob) {
        if (!ELITE_MOBS.contains(mob.getUUID())) return;
        if (!(mob.level() instanceof ServerLevel serverLevel)) return;
        if (!NOTIFIED_MOBS.contains(mob.getUUID()) && mob.tickCount % 40 == 0) {
            if (notifyNearbyPlayers(mob) > 0) NOTIFIED_MOBS.add(mob.getUUID());
        }
        if (!BuffMobsConfig.INSTANCE.visualEffects) return;
        BuffMobsConfig.EliteMobs.EliteParticle particleType = BuffMobsConfig.INSTANCE.eliteMobs.particle;
        if (particleType == BuffMobsConfig.EliteMobs.EliteParticle.NONE) return;
        if (particleType == BuffMobsConfig.EliteMobs.EliteParticle.RANDOM) particleType = MobBuffUtil.randomParticleFor(mob);
        int interval = Math.max(1, BuffMobsConfig.INSTANCE.eliteMobs.particleIntervalTicks);
        if (mob.tickCount % interval != 0) return;
        ParticleOptions particle = resolveParticle(particleType);
        serverLevel.sendParticles(particle,
                mob.getX(), mob.getY() + mob.getBbHeight() * 0.5, mob.getZ(),
                2, mob.getBbWidth() * 0.5, mob.getBbHeight() * 0.5, mob.getBbWidth() * 0.5, 0.0);
    }

    private static double getEliteChance(Mob mob) {
        BuffMobsConfig.EliteMobs cfg = BuffMobsConfig.INSTANCE.eliteMobs;
        double chance = cfg.baseChance + cfg.chanceIncreasePerDay * MobBuffUtil.getEffectiveDays(mob.level());
        return Math.min(chance, cfg.maxChance);
    }

    private static void makeElite(Mob mob) {
        BuffMobsConfig.EliteMobs cfg = BuffMobsConfig.INSTANCE.eliteMobs;
        ELITE_MOBS.add(mob.getUUID());

        float healthRatio = mob.getMaxHealth() > 0 ? mob.getHealth() / mob.getMaxHealth() : 1.0f;
        applyMultiplier(mob, ELITE_HEALTH_MOD_ID, true, cfg.healthMultiplier);
        applyMultiplier(mob, ELITE_DAMAGE_MOD_ID, false, cfg.damageMultiplier);
        mob.setHealth(Math.max(1.0f, mob.getMaxHealth() * healthRatio));

        if (mob.getCustomName() == null) {
            mob.setCustomName(buildEliteName(mob, cfg).withStyle(Style.EMPTY.withColor(resolveNameColor(mob, cfg))));
            mob.setCustomNameVisible(true);
            ELITE_NAMED_MOBS.add(mob.getUUID());
        }

        BuffMobsMod.LOGGER.debug("[BuffMobs] Elite spawned: {}", mob.getType().getDescriptionId());
        if (notifyNearbyPlayers(mob) > 0) NOTIFIED_MOBS.add(mob.getUUID());
    }

    private static void removeElite(Mob mob) {
        UUID uuid = mob.getUUID();
        if (!ELITE_MOBS.remove(uuid)) return;
        AttributeInstance health = mob.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) health.removeModifier(ELITE_HEALTH_MOD_ID);
        AttributeInstance damage = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage != null) damage.removeModifier(ELITE_DAMAGE_MOD_ID);
        if (ELITE_NAMED_MOBS.remove(uuid)) {
            mob.setCustomName(null);
            mob.setCustomNameVisible(false);
        }
    }

    private static void applyMultiplier(Mob mob, Identifier id, boolean health, double multiplier) {
        AttributeInstance instance = mob.getAttribute(health ? Attributes.MAX_HEALTH : Attributes.ATTACK_DAMAGE);
        if (instance == null) return;
        instance.removeModifier(id);
        if (multiplier > 1.0) {
            instance.addPermanentModifier(new AttributeModifier(id, multiplier - 1.0,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
    }

    private static MutableComponent buildEliteName(Mob mob, BuffMobsConfig.EliteMobs cfg) {
        Component mobName = mob.getType().getDescription();
        if (cfg.nameMode == BuffMobsConfig.EliteMobs.NameMode.PREFIX) {
            return Component.translatable("buffmobs.elite.name_format",
                    Component.translatable(resolvePrefixKey(cfg.namePrefix)), mobName);
        }
        String prefix = cfg.useGeneratedPrefix ? pickRandom(cfg.generatedNamePrefixes) : null;
        String suffix = cfg.useGeneratedSuffix ? pickRandom(cfg.generatedNameSuffixes) : null;
        if (prefix != null && suffix != null) {
            return Component.translatable("buffmobs.elite.name_format.generated_full",
                    poolEntryComponent(prefix), mobName, poolEntryComponent(suffix));
        }
        if (prefix != null) {
            return Component.translatable("buffmobs.elite.name_format", poolEntryComponent(prefix), mobName);
        }
        if (suffix != null) {
            return Component.translatable("buffmobs.elite.name_format.generated_suffix",
                    mobName, poolEntryComponent(suffix));
        }
        return mobName.copy();
    }

    private static String pickRandom(List<String> options) {
        if (options == null || options.isEmpty()) return null;
        String picked = options.get(RANDOM.nextInt(options.size()));
        return picked == null || picked.isBlank() ? null : picked;
    }

    private static Component poolEntryComponent(String entry) {
        if (entry.contains(".") && !entry.contains(" ")) return Component.translatable(entry);
        return Component.literal(entry);
    }

    private static String resolvePrefixKey(BuffMobsConfig.EliteMobs.NamePrefix prefix) {
        return switch (prefix) {
            case elite -> "buffmobs.elite.prefix.elite";
            case CHAMPION -> "buffmobs.elite.prefix.champion";
            case BRUTE -> "buffmobs.elite.prefix.brute";
            case DREAD -> "buffmobs.elite.prefix.dread";
            case RANDOM -> switch (RANDOM.nextInt(4)) {
                case 0 -> "buffmobs.elite.prefix.elite";
                case 1 -> "buffmobs.elite.prefix.champion";
                case 2 -> "buffmobs.elite.prefix.brute";
                default -> "buffmobs.elite.prefix.dread";
            };
        };
    }

    private static TextColor resolveNameColor(Mob mob, BuffMobsConfig.EliteMobs cfg) {
        return switch (cfg.nameColor) {
            case GOLD -> TextColor.fromRgb(0xFFAA00);
            case RED -> TextColor.fromRgb(0xFF5555);
            case DARK_RED -> TextColor.fromRgb(0xAA0000);
            case LIGHT_PURPLE -> TextColor.fromRgb(0xFF55FF);
            case AQUA -> TextColor.fromRgb(0x55FFFF);
            case YELLOW -> TextColor.fromRgb(0xFFFF55);
            case RANDOM -> TextColor.fromRgb(RANDOM_NAME_COLORS[Math.floorMod(mob.getUUID().hashCode(), RANDOM_NAME_COLORS.length)]);
            case CUSTOM -> parseCustomNameColor(cfg.customNameColor);
        };
    }

    private static TextColor parseCustomNameColor(String value) {
        if (value != null) {
            String normalized = value.trim();
            if (normalized.startsWith("#")) normalized = normalized.substring(1);
            if (normalized.startsWith("0x") || normalized.startsWith("0X")) normalized = normalized.substring(2);
            if (normalized.length() == 6) {
                try {
                    return TextColor.fromRgb(Integer.parseInt(normalized, 16));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        BuffMobsMod.LOGGER.warn("[BuffMobs] Invalid eliteMobs.customNameColor '{}', using gold", value);
        return TextColor.fromRgb(0xFFAA00);
    }

    private static ParticleOptions resolveParticle(BuffMobsConfig.EliteMobs.EliteParticle particle) {
        return switch (particle) {
            case ENCHANT -> ParticleTypes.ENCHANT;
            case FLAME -> ParticleTypes.FLAME;
            case SOUL_FIRE_FLAME -> ParticleTypes.SOUL_FIRE_FLAME;
            case PORTAL -> ParticleTypes.PORTAL;
            case END_ROD -> ParticleTypes.END_ROD;
            case HAPPY_VILLAGER -> ParticleTypes.HAPPY_VILLAGER;
            case HEART -> ParticleTypes.HEART;
            case SNOWFLAKE -> ParticleTypes.SNOWFLAKE;
            case ELECTRIC_SPARK -> ParticleTypes.ELECTRIC_SPARK;
            case RANDOM, NONE -> ParticleTypes.ENCHANT;
        };
    }

    private static int notifyNearbyPlayers(Mob mob) {
        BuffMobsConfig.EliteMobs cfg = BuffMobsConfig.INSTANCE.eliteMobs;
        if (!cfg.notifyNearbyPlayers) return 0;
        if (!(mob.level() instanceof ServerLevel serverLevel)) return 0;
        double radius = cfg.notifyRadius;
        double radiusSqr = radius * radius;
        Component message = Component.translatable("buffmobs.notify.elite.spawned", mob.getDisplayName());
        int notified = 0;
        for (ServerPlayer player : serverLevel.players()) {
            if (player.isSpectator()) continue;
            if (player.distanceToSqr(mob) > radiusSqr) continue;
            player.sendSystemMessage(message);
            notified++;
        }
        return notified;
    }
}
