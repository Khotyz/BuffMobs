# BuffMobs

**BuffMobs** makes hostile and neutral mobs stronger, faster, and more dangerous — with deep customization, progressive difficulty scaling, and intelligent combat behavior.

[![GitHub](https://img.shields.io/badge/GitHub-BuffMobs-blue?logo=github)](https://github.com/Khotyz/BuffMobs)

---

## Features

### Attribute Buffs
Multiply the health, damage, speed, attack speed, and armor of all hostile and neutral mobs globally. Every value is individually configurable.

### Status Effects
Grant mobs permanent Strength, Speed, Resistance, Regeneration, and Absorption effects. Effects can be infinite or time-limited. Undead mobs receive Absorption instead of Regeneration.

### Harmful Effects
Buffed mobs have a configurable chance to apply Poison, Slowness, or Wither to players on hit.

### Day Scaling
Progressively increase mob strength as in-game days pass. Scaling interval, multiplier per interval, and maximum cap are all configurable. Notifications can be sent to players every day or only on scaling increases.

### Per-Dimension Scaling
Configure separate health, damage, speed, and armor multipliers for up to 5 dimensions independently.

### Mob Presets
Assign individual stat profiles to specific mobs by ID. Comes with four built-in presets (`default`, `boss`, `elite`, `weak`) and one empty slot for custom use. Presets stack on top of global multipliers.

### Melee / Kite Switching
Ranged mobs (skeletons, pillagers, piglins, strays) change behavior when a player gets too close:
- **Melee** — switch to a sword or axe and chase the player
- **Kite** — flee to maintain a safe shooting distance
- **Random** — each mob picks one behavior on spawn

Generated melee weapons scale in tier and enchantment level with world days.

### CombatDraft
When a mob's health drops below a configurable threshold, it drinks a regeneration potion. Undead mobs receive Absorption and a direct heal instead. Usage count and cooldown are configurable.

### Filters
Control exactly which mobs are buffed using:
- **Mob ID whitelist/blacklist**
- **Mod ID whitelist/blacklist**
- **Dimension whitelist/blacklist**

Tamed animals are always excluded.

---

## Requirements

| Dependency | Version |
|---|---|
| Minecraft | 1.21.1 |
| Fabric Loader | 0.18.4+ |
| Fabric API | 0.116.9+1.21.1 |
| Cloth Config *(optional)* | 15.0.127+ |
| ModMenu *(optional)* | 11.0.1+ |

Cloth Config and ModMenu are optional but required for the in-game config screen.

---

## Configuration

Config file is located at `.minecraft/config/buffmobs.json`.

All values can be edited directly in the file or through the in-game screen via ModMenu if Cloth Config is installed.

See [HOWTOUSE.md](HOWTOUSE.md) for a full breakdown of every option.

---

## Commands

| Command | Description |
|---|---|
| `/buffmobs info` | Shows current config summary and initialized mob count |
| `/buffmobs debug` | Shows detailed stats of the nearest mob |
| `/buffmobs presets` | Lists all configured presets and mob mappings |
| `/buffmobs reload` | Reloads config from disk and reapplies all buffs |

---

## Links

- [GitHub](https://github.com/Khotyz/BuffMobs)
- [Issue Tracker](https://github.com/Khotyz/BuffMobs/issues)