# BuffMobs

**BuffMobs** makes hostile and neutral mobs stronger, faster, and more dangerous — with deep customization, progressive difficulty scaling, and intelligent combat behavior.

[![GitHub](https://img.shields.io/badge/GitHub-BuffMobs-blue?logo=github)](https://github.com/Khotyz/BuffMobs)

---

## Features

### Attribute Buffs
Multiply the health, damage, speed, attack speed, and armor of all hostile and neutral mobs globally. Every value is individually configurable.

### Status Effects
Grant mobs permanent Strength, Speed, Resistance, Regeneration, and Absorption effects. Undead mobs receive Absorption instead of Regeneration.

### Harmful Effects
Buffed mobs have a configurable chance to apply Poison, Slowness, or Wither to players on hit.

### Day Scaling
Progressively increase mob strength as in-game days pass. Scaling interval, multiplier per interval, and maximum cap are all configurable.

### Per-Dimension Scaling
Configure separate health, damage, speed, and armor multipliers for up to 5 dimensions independently.

### Mob Presets
Assign individual stat profiles to specific mobs by ID. Includes built-in presets (`default`, `boss`, `elite`, `weak`) that stack on top of global multipliers.

### Melee / Kite Switching
Ranged mobs (skeletons, pillagers, etc.) change behavior when a player gets too close:
- **Melee** — switch to a sword or axe and chase the player.
- **Kite** — flee to maintain a safe shooting distance.
- **Random** — each mob picks one behavior on spawn.

### CombatDraft
When a mob's health drops below a threshold, it drinks a regeneration potion (or receives Absorption if undead).

---

## Requirements

| Dependency | Version |
|---|---|
| **Minecraft** | **~1.21.11 (Mounts of Mayhem)** |
| **Fabric Loader** | **>=0.18.0** |
| **Fabric API** | ***** |
| Cloth Config *(optional)* | >=21.11.0 |
| ModMenu *(optional)* | >=17.0.0 |

---

## Configuration

The config file is located at `.minecraft/config/buffmobs.json`.

All values can be edited directly in the file or through the in-game screen via **ModMenu** if **Cloth Config** is installed.

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