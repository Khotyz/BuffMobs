# BuffMobs

**BuffMobs** turns ordinary Minecraft mobs into a living, escalating threat. It bundles progressive difficulty scaling, smarter AI, loot and XP rewards, elite variants, elemental traits, special abilities, dynamic events, and deep per-mob customization into one highly configurable package — with a built-in in-game config screen so you never have to touch a single line of TOML if you don't want to.

> ⚔️ Works on **Fabric** and **NeoForge** • Supports **1.21.1**, **1.21.11**, and **26.X** 

---

## ✨ Feature Highlights

### 📈 Difficulty & Scaling
- **Attribute Buffs** — Globally raise mob health, damage, movement speed, attack speed, armor, and armor toughness.
- **Status Effects** — Grant mobs permanent Strength, Speed, Resistance, Regeneration, and Absorption (configurable level per effect, infinite duration optional).
- **Day Scaling** — Mobs get stronger the longer your world runs. Configure the interval, the bonus per step, and a hard cap.
- **Dimension Scaling** — Up to **5 dimension slots**, each with independent health/damage/speed/attack-speed/armor/toughness multipliers. Choose between `SCALING` (multiply base stats) or `OVERRIDE` (replace with fixed multipliers).
- **Dimension Max Health** — Up to 5 slots that *force* a fixed max health for mobs in a given dimension, with an optional mob allowlist and a denylist.
- **Adaptive Difficulty** — The world reacts to how you play. Player deaths raise difficulty; player kills slowly walk it back. Fully tunable step sizes, step counts, and a bonus cap.
- **Blood Moon** — A recurring night event (configurable interval) that multiplies all scaling on top of itself, can make mobs Glow, doubles loot and XP scaling, and can optionally duplicate natural spawns.

### 🤖 Smarter AI
- **Ranged/Melee Switching** — Ranged mobs react to a nearby player. On spawn, each mob is randomly assigned to **MELEE** (close the gap and swing), **KITE** (back away to keep distance), or **RANDOM**. Fully configurable switch distance, melee speed multiplier, hysteresis, and custom "ranged mob" lists for modded mobs.
- **Weapon Progression** — When a ranged mob switches to melee, it draws a *generated weapon* whose tier depends on the dimension and the world's day count. Overworld uses swords (Stone → Iron → Diamond → Netherite); the Nether uses axes (Gold → Diamond → Netherite); the End uses swords with a Netherite endgame.
- **Enchantment Progression** — Generated weapons roll Sharpness, Fire Aspect, Knockback, and Sweeping Edge, each with its own unlock day, max level, and days-per-level scaling.
- **Passive Mob Aggression** — Passive mobs can be turned aggressive with three modes: `OFF`, `NEUTRAL` (retaliate only), and `HOSTILE` (actively hunt players). Includes per-mob whitelist/blacklist and a configurable damage formula that can scale with max health.
- **Pack Aggression** — When a buffed mob is hit by a player, nearby mobs of the *same type* join the fight. Configurable radius, max pack size, and per-mob cooldown.
- **CombatDraft** — Low-HP mobs drink a regeneration potion mid-fight. Living mobs receive Regeneration; undead mobs receive Absorption plus a direct heal. Includes per-mob whitelist/blacklist, cooldown, and max uses per life.

### 🧬 Traits, Abilities & Elites
- **Elemental Traits** — A buffed mob can roll **exactly one** of six traits: **Flame**, **Frost**, **Toxic**, **Voltaic**, **Vampiric**, or **Armored**. Each has weighted chance, on-hit effects (ignite / slow / poison / chain lightning / lifesteal), damage immunities, passive effects, and its own particle.
- **Special Abilities** — Assign abilities to specific mobs via `modid:mob_id:ability` mappings:
    - **Deathrattle** — Explode, spawn a poison cloud, summon minions, or roll randomly on death.
    - **Last Stand** — Gain Resistance and totem particles the first time HP drops below a threshold.
    - **Teleport** — Teleport behind an attacker when damaged.
    - **Summon** — Periodically spawn minions while in combat.
- **Elite Mobs** — A scaled chance (base + per-day, capped) turns a buffed mob into an Elite. Elites get stacking health/damage multipliers, custom names (either a roguelike generated prefix/suffix or a translatable prefix), customizable name color (including hex), a continuous particle aura, proximity notifications, extra XP, and bonus loot rolls.

### 🛡️ Gear & Rewards
- **Mob Armor** — Eligible mobs can spawn wearing scaled armor. Choose `FULL_SET` or `RANDOM_PIECES` with per-slot chances and min/max guarantees. Tiers unlock over time (Leather → Gold → Iron → Diamond → Netherite), and each piece can roll Protection, Fire/Blast/Projectile Protection, Thorns, Unbreaking, Respiration, Aqua Affinity, Feather Falling, Depth Strider, and Frost Walker — plus optional curses. Supports mixed tiers and configurable drop chance.
- **Loot & XP Scaling** — Buffed mobs drop extra XP and roll their loot table additional times based on their effective health multiplier, with separate caps for XP and extra rolls. Both are doubled during a Blood Moon.

### 🎛️ Customization & Control
- **Mob Presets** — Define up to 5 named stat presets and map any mob ID to one. Presets can optionally *override* dimension scaling so hand-tuned mobs (bosses, rare mobs) ignore the global scaling chain.
- **Health Sync** — Choose `OVERRIDE` (fully heal the mob to its new max HP) or `STACK` (preserve current damage taken and add the health delta on top), fixing the classic "structure mob desync" problem.
- **Zombie Handling** — Either fully strip the vanilla leader-zombie health bonus, or exclude it from BuffMobs' multiplier math so leader zombies don't balloon out of control.
- **Flexible Filtering** — Whitelist or blacklist buffs by **mob ID**, **mod ID**, and **dimension** independently. Includes built-in neutral handling for mobs like Endermen, Piglins, Spiders, Wolves, Bees, Llamas, and more.

---

## 🔧 Requirements

| Dependency | Version | Side |
|---|---|---|
| Minecraft | 1.21.1 / 1.21.11 / 26.X | Both |
| Fabric Loader + Fabric API | Latest for your MC version | Both |
| NeoForge | 21.1.x+ (matching your MC version) | Both |

---

## ⚙️ Configuration

All settings live in `config/buffmobs.toml`. Every option is exposed through the in-game config screen (accessible from the mod list), which also supports **search-free navigation, per-field reset buttons, modified-field highlighting, tooltips, and live validation**. The GUI is fully custom and does not depend on Cloth Config.

For a full breakdown of every option, see the **[How To Use guide](HOW%20TO%20USE.MD)**.

### 📂 Config Sections
| Section | What it controls |
|---|---|
| `general` | Master on/off switch and visual effects toggle |
| `attributes` | Global health, damage, speed, attack speed, armor, toughness |
| `effects` | Permanent status effects (Strength, Speed, Resistance, Regeneration, Absorption) |
| `harmfulEffects` | On-hit Poison / Slowness / Wither |
| `dayScaling` | Progressive difficulty over time |
| `dimensionScaling` | Per-dimension multipliers (5 slots) |
| `dimensionMaxHealth` | Fixed max-health overrides per dimension (5 slots) |
| `mobFilter` / `modidFilter` / `dimensionFilter` | Whitelist / blacklist filters |
| `rangedMeleeSwitching` | MELEE/KITE/RANDOM behavior + weapon & enchant progression |
| `combatDraft` | Low-HP regeneration potion behavior |
| `mobPresets` | Named stat presets and mob mappings |
| `passiveMobAggression` | Passive-mob aggression modes |
| `zombieHandling` | Leader zombie bonus handling |
| `healthSync` | Health synchronization mode |
| `mobArmor` | Scaled armor sets for mobs |
| `eliteMobs` | Elite variants |
| `lootXpScaling` | Bonus XP and loot rolls |
| `traits` | Elemental trait system |
| `packAggression` | Pack aggro on hit |
| `specialAbilities` | Deathrattle, Last Stand, Teleport, Summon |
| `bloodMoon` | Blood Moon event |
| `adaptiveDifficulty` | Performance-based difficulty |

---

## 🛠 Commands

All commands require permission level 2 (op).

| Command | Description |
|---|---|
| `/buffmobs info` | Show current config status and initialized mob count |
| `/buffmobs debug` | Inspect the nearest mob's buff status, preset, traits, abilities, effects, and current stats |
| `/buffmobs reload` | Remove and reapply all buffs using the current config |
| `/buffmobs presets` | List all configured presets and their mob mappings |
| `/buffmobs reset all confirm` | Fully reset all BuffMobs progression (scaling, adaptive, blood moon, mob caches) |
| `/buffmobs reset scaling confirm` | Reset only day-scaling progress |
| `/buffmobs reset adaptive confirm` | Reset only adaptive difficulty counters |
| `/buffmobs reset bloodmoon confirm` | Reset the Blood Moon cycle |
| `/buffmobs reset mobs` | Rebuild the mob caches and reinitialize every loaded mob |

> `reset all`, `reset scaling`, `reset adaptive`, and `reset bloodmoon` require a `confirm` argument to prevent accidents.

---

## 📦 Download

- [Modrinth](https://modrinth.com/mod/buff-mobs)
- [Curseforge](https://www.curseforge.com/minecraft/mc-mods/buff-mobs/files/all)

---

## 🐛 Issues & Feedback

Found a bug, a balance problem, or have a feature idea? [Open an issue on GitHub](https://github.com/Khotyz/BuffMobs/issues).

---

## 📄 License

MIT
