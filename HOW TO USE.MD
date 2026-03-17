# BuffMobs — User Guide

This guide covers every configurable option in BuffMobs. All settings are stored in `config/buffmobs.toml` and can also be changed through the in-game Cloth Config screen.

After changing any setting, use `/buffmobs reload` to apply them to all loaded mobs immediately.

---

## Table of Contents

1. [General](#1-general)
2. [Attributes](#2-attributes)
3. [Status Effects](#3-status-effects)
4. [Harmful Effects](#4-harmful-effects)
5. [Day Scaling](#5-day-scaling)
6. [Dimension Scaling](#6-dimension-scaling)
7. [Filters](#7-filters)
    - [Mob Filter](#mob-filter)
    - [Mod ID Filter](#mod-id-filter)
    - [Dimension Filter](#dimension-filter)
8. [Ranged/Melee Switching](#8-rangedmelee-switching)
    - [Weapon Progression](#weapon-progression)
    - [Enchantments](#enchantments)
9. [CombatDraft](#9-combatdraft)
10. [Mob Presets](#10-mob-presets)
11. [Commands](#11-commands)

---

## 1. General

| Option | Default | Description |
|---|---|---|
| `enabled` | `true` | Master switch. Disabling removes all buffs from mobs immediately. |
| `visualEffects` | `true` | Show status effect particles on buffed mobs. |

---

## 2. Attributes

These multipliers are applied to every buffed mob on top of their base values.

| Option | Default | Range | Description |
|---|---|---|---|
| `healthMultiplier` | `1.5` | 0.1 – 10.0 | Multiplies max health. `1.5` = 50% more HP. |
| `damageMultiplier` | `1.5` | 0.1 – 10.0 | Multiplies attack damage. |
| `speedMultiplier` | `1.0` | 0.1 – 10.0 | Multiplies movement speed. Capped internally at 2× to avoid unhittable mobs. |
| `attackSpeedMultiplier` | `1.0` | 0.1 – 10.0 | Multiplies attack speed. |
| `armorAddition` | `5.0` | 0.0 – 20.0 | Flat armor points added. 20 = full diamond armor equivalent. |
| `armorToughnessAddition` | `0.0` | 0.0 – 20.0 | Flat armor toughness added. Reduces effectiveness of high-damage hits. |

---

## 3. Status Effects

Permanent status effects applied to buffed mobs. Set any level to `0` to disable that effect.

| Option | Default | Range | Description |
|---|---|---|---|
| `duration` | `-1` | -1 – 7200 | Duration in seconds. `-1` = infinite (refreshed every second). |
| `strengthAmplifier` | `0` | 0 – 10 | Strength level. `1` = Strength I. |
| `speedAmplifier` | `0` | 0 – 10 | Speed level. Stacks with the speed attribute multiplier. |
| `resistanceAmplifier` | `0` | 0 – 10 | Resistance level. Reduces all incoming damage. |
| `regenerationAmplifier` | `0` | 0 – 10 | Regeneration level. Skipped for undead mobs. |
| `absorptionAmplifier` | `0` | 0 – 10 | Absorption level. Undead mobs receive this instead of Regeneration. Each level adds 4 extra absorption hearts. |

---

## 4. Harmful Effects

When enabled, buffed mobs have a chance to inflict a debuff on the player with each hit.

| Option | Default | Description |
|---|---|---|
| `enabled` | `true` | Enable or disable harmful effects entirely. |
| `chance` | `0.15` | Probability per hit. `0.15` = 15% chance. |
| `poisonDuration` | `5` | Poison duration in seconds. |
| `slownessDuration` | `3` | Slowness duration in seconds. |
| `witherDuration` | `3` | Wither duration in seconds. |

One of the three effects is chosen at random each time the roll succeeds.

---

## 5. Day Scaling

Automatically increases mob strength as in-game days pass.

| Option | Default | Description |
|---|---|---|
| `enabled` | `false` | Enable day-based difficulty scaling. |
| `interval` | `7` | Days between each difficulty increase. |
| `multiplier` | `0.1` | How much the multiplier increases per interval. `0.1` = +10% per step. |
| `maxMultiplier` | `5.0` | Maximum value the scaling multiplier can reach. |
| `showNotifications` | `true` | Broadcast a chat message when difficulty increases. |
| `notificationMode` | `EVERY_DAY` | `EVERY_DAY`: notify every day. `SCALING_INCREASE_ONLY`: only on scaling steps. |

**Example:** With `interval = 7` and `multiplier = 0.1`, mobs are 10% stronger every 7 days, capping at 5× after 280 days.

---

## 6. Dimension Scaling

Apply separate multipliers to mobs in specific dimensions. Up to 5 slots are available.

For each slot:

| Option | Default | Description |
|---|---|---|
| `dimensionName` | `""` | Dimension ID to target. Example: `minecraft:the_nether`. Leave empty to disable the slot. |
| `healthMultiplier` | `1.0` | Health multiplier for this dimension. `2.0` = double HP. |
| `damageMultiplier` | `1.0` | Damage multiplier. |
| `speedMultiplier` | `1.0` | Speed multiplier. |
| `attackSpeedMultiplier` | `1.0` | Attack speed multiplier. |
| `armorAddition` | `0` | Flat armor points added. |
| `armorToughnessAddition` | `0` | Flat armor toughness added. |

Dimension multipliers stack on top of the global attribute settings.

---

## 7. Filters

### Mob Filter

Controls which mobs are buffed by entity ID.

| Option | Default | Description |
|---|---|---|
| `useWhitelist` | `false` | If `true`, only mobs in the whitelist are buffed. If `false`, all mobs except the blacklist are buffed. |
| `blacklist` | `[]` | Mob IDs to never buff. Format: `minecraft:zombie`. |
| `whitelist` | `[]` | Mob IDs to buff (only active when `useWhitelist = true`). |

### Mod ID Filter

Controls which mobs are buffed by the mod they come from.

| Option | Default | Description |
|---|---|---|
| `useWhitelist` | `false` | If `true`, only mobs from whitelisted mods are buffed. |
| `blacklist` | `[]` | Mod IDs to never buff. Format: `minecraft`. |
| `whitelist` | `[]` | Mod IDs to buff (only active when `useWhitelist = true`). |

### Dimension Filter

Controls which dimensions have buffed mobs.

| Option | Default | Description |
|---|---|---|
| `useWhitelist` | `false` | If `true`, only buff mobs in whitelisted dimensions. |
| `blacklist` | `[]` | Dimensions where mobs are never buffed. |
| `whitelist` | `[overworld, nether, end]` | Dimensions where mobs are buffed (only active when `useWhitelist = true`). |

---

## 8. Ranged/Melee Switching

Ranged mobs (skeletons, strays, pillagers, piglins) react when a player gets too close.

| Option | Default | Description |
|---|---|---|
| `enabled` | `true` | Enable or disable the behavior entirely. |
| `behaviorMode` | `RANDOM` | `MELEE`: switch to sword. `KITE`: flee to maintain distance. `RANDOM`: each mob picks one on spawn. |
| `switchDistance` | `4.0` | Distance in blocks that triggers the behavior switch. |
| `meleeSpeedMultiplier` | `0.9` | Speed multiplier applied when a mob enters melee mode. |
| `customRangedMobs` | `[]` | Additional mob IDs to treat as ranged. Format: `modid:mob_id`. |

### Weapon Progression

Weapon tiers unlock as in-game days pass. Each tier has a configurable unlock day.

| Weapon | Default Unlock Day | Dimension |
|---|---|---|
| Stone Sword | Day 0 | Overworld, End |
| Iron Sword | Day 7 | Overworld, End |
| Diamond Sword | Day 21 | Overworld, End |
| Netherite Sword | Day 60 | End only |
| Golden Axe | Day 0 | Nether |
| Diamond Axe | Day 14 | Nether |
| Netherite Axe | Day 45 | Nether |

### Enchantments

| Option | Default | Description |
|---|---|---|
| `enchantmentsEnabled` | `true` | Allow generated weapons to receive enchantments. |
| `maxEnchantmentsPerWeapon` | `2` | Maximum number of enchantments per weapon. |
| `daysPerEnchantmentLevel` | `7` | Days after unlock for each enchantment level to increase. |

Available enchantments with their unlock days and max levels:

| Enchantment | Default Unlock Day | Max Level |
|---|---|---|
| Sharpness | Day 0 | V |
| Knockback | Day 7 | II |
| Fire Aspect | Day 14 | II |
| Sweeping Edge | Day 21 | III |

---

## 9. CombatDraft

When a mob's health drops below the configured threshold, it drinks a potion for a burst of recovery.

| Option | Default | Description |
|---|---|---|
| `enabled` | `true` | Enable or disable CombatDraft. |
| `healthThreshold` | `0.20` | HP fraction that triggers the potion. `0.20` = triggers below 20% of max HP. |
| `regenAmplifier` | `4` | Regeneration level granted. Stacks on top of the mob's existing Regen buff. |
| `regenDuration` | `10` | Duration of the effect in seconds. |
| `cooldownTicks` | `600` | Ticks before the mob can use the potion again. `600` = 30 seconds. |
| `maxUses` | `0` | Maximum uses per life. `0` = unlimited. |
| `useWhitelist` | `false` | If `true`, only mobs in the whitelist can use CombatDraft. |
| `whitelist` | `[]` | Mob IDs allowed to use CombatDraft. |
| `blacklist` | `[]` | Mob IDs that can never use CombatDraft. Slime, Magma Cube and Ghast are always excluded. |

**Undead behavior:** Regeneration harms undead mobs. Instead, they receive an Absorption boost (stacked on top of their existing level) and a direct heal proportional to the configured amplifier.

**Stacking:** If a mob already has Regen I from its permanent buffs and `regenAmplifier` is set to `3`, it temporarily gains **Regen IV** for the configured duration, then returns to Regen I automatically.

---

## 10. Mob Presets

Assign custom stat multipliers to specific mobs. Preset multipliers stack on top of the global attribute settings.

**Step 1 — Define a preset:**

```toml
[mobPresets.preset1]
presetName = "boss"
healthMultiplier = 3.0
damageMultiplier = 2.5
speedMultiplier = 1.2
attackSpeedMultiplier = 1.5
armorAddition = 10.0
armorToughnessAddition = 5.0
```

**Step 2 — Map a mob to the preset:**

```toml
mobMapping = [
    "minecraft:ender_dragon:boss",
    "minecraft:wither:boss",
    "minecraft:zombie:default"
]
```

Format: `modid:mob_id:preset_name`

Up to 5 presets are available (`preset1` through `preset5`).

---

## 11. Commands

All commands require operator permissions.

| Command | Description |
|---|---|
| `/buffmobs info` | Show current config status, mod state, and initialized mob count. |
| `/buffmobs debug` | Inspect the nearest mob — shows its ID, mod, dimension, preset, scaling multipliers, and current attribute values. |
| `/buffmobs reload` | Fully remove and reapply all buffs to every loaded mob using the current config. |
| `/buffmobs presets` | List all configured presets and their mob mappings. |

---

> Found a bug or have a suggestion? [Open an issue on GitHub](https://github.com/Khotyz/BuffMobs/issues).