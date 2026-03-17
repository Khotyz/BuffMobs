# BuffMobs

**BuffMobs** makes hostile and neutral mobs significantly more dangerous, with progressive difficulty scaling, smarter AI behavior, and deep per-mob customization.

---

## ✨ Features

- **Attribute Buffs** — Increase mob health, damage, speed, attack speed, armor, and armor toughness globally.
- **Status Effects** — Grant mobs permanent Strength, Speed, Resistance, Regeneration, and Absorption.
- **Harmful Effects** — Mobs have a configurable chance to inflict Poison, Slowness, or Wither on hit.
- **Day Scaling** — Mob difficulty automatically increases as in-game days pass, up to a configurable cap.
- **Dimension Scaling** — Configure separate multipliers for up to 5 different dimensions.
- **Mob Presets** — Assign custom stat profiles to specific mobs. Define up to 5 named presets and map any mob ID to one.
- **Ranged/Melee Switching** — Ranged mobs react when a player gets close: switch to a melee weapon, or kite away to maintain distance. Behavior is randomly assigned per mob on spawn, or forced to one mode.
- **Weapon Progression** — Mobs switch to generated melee weapons whose tier and enchantments scale with the number of in-game days elapsed.
- **CombatDraft** — Mobs drink a regeneration potion when they fall below a health threshold. Living mobs receive Regeneration; undead mobs receive Absorption and a direct heal.
- **Flexible Filtering** — Whitelist or blacklist mobs by entity ID, mod ID, or dimension.

---

## 🔧 Requirements

| Dependency | Version | Side |
|---|---|---|
| Minecraft | 1.21.1 | Both |
| NeoForge | 21.1.x | Both |
| [Cloth Config](https://modrinth.com/mod/cloth-config) | 15.x | Client (optional) |

Cloth Config is optional but recommended — it enables the in-game configuration GUI.

---

## ⚙️ Configuration

All settings are stored in `config/buffmobs.toml`. The easiest way to configure the mod is through the in-game GUI via the Cloth Config screen, accessible from the mod list.

For a full breakdown of every option, see the **[How To Use guide](HOW%20TO%20USE.MD)**.

---

## 🛠 Commands

| Command | Description |
|---|---|
| `/buffmobs info` | Show current config status and initialized mob count |
| `/buffmobs debug` | Inspect the nearest mob's buff status, preset, and current stats |
| `/buffmobs reload` | Remove and reapply all buffs using the current config |
| `/buffmobs presets` | List all configured presets and mob mappings |

---

## 📦 Download

- [Modrinth](https://modrinth.com/mod/buff-mobs)
- [GitHub Releases](https://github.com/Khotyz/BuffMobs/releases)

---

## 🐛 Issues & Feedback

Found a bug or have a suggestion? [Open an issue on GitHub](https://github.com/Khotyz/BuffMobs/issues).

---

## 📄 License

MIT