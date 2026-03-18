# How to Use BuffMobs

BuffMobs is configured through `config/buffmobs.json` in your Minecraft instance folder. If Cloth Config and ModMenu are installed, every option is also accessible in-game via the Mods screen.

---

## General

| Option | Default | Description |
|---|---|---|
| `enabled` | `true` | Master switch. When disabled, all buffs are removed from existing mobs. |
| `visualEffects` | `true` | Show particle effects on mobs with active status effects. |

---

## Attributes

Multipliers and additions applied to every buffed mob.

| Option | Default | Description |
|---|---|---|
| `healthMultiplier` | `1.5` | Multiplies base max health. `1.5` = 50% more HP. |
| `damageMultiplier` | `1.5` | Multiplies base attack damage. |
| `speedMultiplier` | `1.0` | Multiplies movement speed. Internally capped at 2× to keep mobs hittable. |
| `attackSpeedMultiplier` | `1.0` | Multiplies attack speed. |
| `armorAddition` | `5.0` | Flat armor points added. 20 = full diamond armor equivalent. |
| `armorToughnessAddition` | `0.0` | Flat armor toughness added. Reduces effectiveness of high-damage hits. |

---

## Status Effects

| Option | Default | Description |
|---|---|---|
| `duration` | `-1` | Effect duration in seconds. `-1` = infinite (refreshed every second). |
| `strengthAmplifier` | `0` | Strength level. `0` = disabled. `1` = Strength I, etc. |
| `speedAmplifier` | `0` | Speed level. Stacks with the speed attribute multiplier. |
| `resistanceAmplifier` | `0` | Resistance level. Reduces all incoming damage. |
| `regenerationAmplifier` | `0` | Regeneration level. Skipped for undead mobs. |
| `absorptionAmplifier` | `0` | Absorption level. Undead mobs receive it as a direct shield instead. |

---

## Harmful Effects

Buffed mobs have a chance to apply a negative effect to players on hit.

| Option | Default | Description |
|---|---|---|
| `enabled` | `true` | Enable or disable harmful effects entirely. |
| `chance` | `0.15` | Probability per hit. `0.15` = 15% chance. |
| `poisonDuration` | `5` | Poison duration in seconds. |
| `slownessDuration` | `3` | Slowness duration in seconds. |
| `witherDuration` | `3` | Wither duration in seconds. |

---

## Day Scaling

Progressively increases mob strength as in-game days pass.

| Option | Default | Description |
|---|---|---|
| `enabled` | `false` | Enable day-based difficulty scaling. |
| `interval` | `7` | In-game days between each scaling increase. |
| `multiplier` | `0.1` | Multiplier added per interval. `0.1` = +10% per interval. |
| `maxMultiplier` | `5.0` | Maximum scaling multiplier cap. |
| `showNotifications` | `true` | Show a chat message when the scaling changes. |
| `notificationMode` | `EVERY_DAY` | `EVERY_DAY` or `SCALING_INCREASE_ONLY`. |

---

## Per-Dimension Scaling

Up to 5 dimension slots, each with independent multipliers. Leave `dimensionName` empty to disable a slot.

| Option | Default | Description |
|---|---|---|
| `dimensionName` | `""` | Dimension ID. Example: `minecraft:the_nether`. |
| `healthMultiplier` | `100` | Health multiplier as a percentage. `200` = double HP. |
| `damageMultiplier` | `100` | Damage multiplier as a percentage. |
| `speedMultiplier` | `100` | Speed multiplier as a percentage. |
| `attackSpeedMultiplier` | `100` | Attack speed multiplier as a percentage. |
| `armorAddition` | `0` | Flat armor points added in this dimension. |
| `armorToughnessAddition` | `0` | Flat armor toughness added in this dimension. |

---

## Mob Filter

Control which mobs are buffed by ID.

| Option | Default | Description |
|---|---|---|
| `useWhitelist` | `false` | If `true`, only mobs in the whitelist are buffed. |
| `whitelist` | `[]` | List of mob IDs to buff. Example: `["minecraft:zombie"]`. |
| `blacklist` | `["minecraft:warden"]` | List of mob IDs that are never buffed. |

---

## Mod ID Filter

Control which mods have their mobs buffed.

| Option | Default | Description |
|---|---|---|
| `useWhitelist` | `false` | If `true`, only mobs from whitelisted mods are buffed. |
| `whitelist` | `[]` | List of mod IDs to buff. Example: `["minecraft"]`. |
| `blacklist` | `[]` | List of mod IDs whose mobs are never buffed. |

---

## Dimension Filter

Control which dimensions allow buffed mobs.

| Option | Default | Description |
|---|---|---|
| `useWhitelist` | `false` | If `true`, mobs are only buffed in whitelisted dimensions. |
| `whitelist` | `["minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"]` | Dimensions where mobs are buffed when whitelist is on. |
| `blacklist` | `[]` | Dimensions where mobs are never buffed. |

---

## Ranged / Melee Switching

Ranged mobs change behavior when the player gets within range.

| Option | Default | Description |
|---|---|---|
| `enabled` | `true` | Enable the melee/kite switching system. |
| `behaviorMode` | `RANDOM` | `MELEE`: switch to sword. `KITE`: flee. `RANDOM`: each mob picks one on spawn. |
| `switchDistance` | `4.0` | Distance in blocks that triggers the behavior change. |
| `meleeSpeedMultiplier` | `0.9` | Speed multiplier applied while in melee mode. |
| `customRangedMobs` | `[]` | Additional mob IDs treated as ranged. Example: `["modid:my_archer"]`. |

### Weapon Unlock Days

Weapons become available based on the number of in-game days elapsed.

| Option | Default |
|---|---|
| `stoneSwordUnlockDay` | `0` |
| `ironSwordUnlockDay` | `7` |
| `diamondSwordUnlockDay` | `21` |
| `netheriteSwordUnlockDay` | `60` |
| `goldenAxeUnlockDay` | `0` *(Nether only)* |
| `diamondAxeUnlockDay` | `14` *(Nether only)* |
| `netheriteAxeUnlockDay` | `45` *(Nether only)* |

### Enchantments

| Option | Default | Description |
|---|---|---|
| `enchantmentsEnabled` | `true` | Allow generated weapons to have enchantments. |
| `maxEnchantmentsPerWeapon` | `2` | Maximum number of enchantments per weapon. |
| `daysPerEnchantmentLevel` | `7` | Days after unlock required for each enchantment level to increase. |
| `sharpnessUnlockDay` | `0` | Day from which Sharpness can appear. Max level: 5. |
| `fireAspectUnlockDay` | `14` | Day from which Fire Aspect can appear. Max level: 2. |
| `knockbackUnlockDay` | `7` | Day from which Knockback can appear. Max level: 2. |
| `sweepingEdgeUnlockDay` | `21` | Day from which Sweeping Edge can appear. Max level: 3. |

---

## CombatDraft

When a mob's health drops below the threshold, it drinks a regeneration potion.

| Option | Default | Description |
|---|---|---|
| `enabled` | `true` | Enable CombatDraft. |
| `healthThreshold` | `0.20` | HP fraction that triggers the potion. `0.20` = below 20% max HP. |
| `regenAmplifier` | `4` | Regeneration level applied. Undead receive Absorption + direct heal instead. |
| `regenDuration` | `10` | Duration of the regeneration effect in seconds. |
| `cooldownTicks` | `600` | Ticks between uses per mob. `600` = 30 seconds. |
| `maxUses` | `0` | Maximum uses per mob per life. `0` = unlimited. |
| `useWhitelist` | `false` | If `true`, only mobs in the whitelist can use CombatDraft. |
| `whitelist` | `[]` | Mob IDs allowed to use CombatDraft. |
| `blacklist` | `[]` | Mob IDs that cannot use CombatDraft. Slime, magma cube, and ghast are always excluded. |

---

## Mob Presets

Assign individual stat profiles to specific mobs. Presets stack on top of global multipliers.

| Option | Default | Description |
|---|---|---|
| `enabled` | `false` | Enable the preset system. |
| `mobMapping` | *(see below)* | List of mappings in the format `modid:mob_id:preset_name`. |

Default mappings:
```
minecraft:zombie:default
minecraft:skeleton:default
minecraft:ender_dragon:boss
minecraft:wither:boss
```

### Preset Slots

Each preset has a name and individual multipliers. There are 5 slots (`preset1` through `preset5`).

| Field | Description |
|---|---|
| `presetName` | Internal name used in mob mappings. |
| `healthMultiplier` | Health multiplier for this preset. |
| `damageMultiplier` | Damage multiplier for this preset. |
| `speedMultiplier` | Speed multiplier for this preset. |
| `attackSpeedMultiplier` | Attack speed multiplier for this preset. |
| `armorAddition` | Flat armor added for this preset. |
| `armorToughnessAddition` | Flat armor toughness added for this preset. |

Built-in presets:

| Name | HP | DMG | SPD | ASPD | Armor | Toughness |
|---|---|---|---|---|---|---|
| `default` | 1.0× | 1.0× | 1.0× | 1.0× | +0 | +0 |
| `boss` | 3.0× | 2.5× | 1.2× | 1.5× | +10 | +5 |
| `elite` | 2.0× | 1.8× | 1.1× | 1.2× | +5 | +2 |
| `weak` | 0.5× | 0.5× | 0.9× | 0.8× | +0 | +0 |

---

## Commands

| Command | Description |
|---|---|
| `/buffmobs info` | Shows enabled state, mob count, and key config values. |
| `/buffmobs debug` | Shows full stats of the nearest mob including preset, scaling, and active effects. |
| `/buffmobs presets` | Lists all preset slots and mob mappings. |
| `/buffmobs reload` | Reloads `buffmobs.json` from disk and reapplies all buffs to existing mobs. |

---

## Links

- [GitHub](https://github.com/Khotyz/BuffMobs)
- [Issue Tracker](https://github.com/Khotyz/BuffMobs/issues)