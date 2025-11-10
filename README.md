# 💪 Buff Mobs

[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen.svg)](https://www.minecraft.net/)
[![NeoForge](https://img.shields.io/badge/NeoForge-21.1.1+-orange.svg)](https://neoforged.net/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**Buff Mobs** is a highly configurable Minecraft mod that makes hostile and neutral mobs significantly more challenging by enhancing their attributes, adding intelligent AI behaviors, and introducing progressive difficulty scaling.

## ✨ Features

### 🎯 Core Systems

#### **Attribute Multipliers**
- Customizable health, damage, speed, and attack speed multipliers
- Flat armor and armor toughness additions
- Apply to all hostile and neutral mobs by default

#### **Status Effects**
- Grant mobs potion effects (Strength, Speed, Resistance, Regeneration)
- Configurable amplifiers and durations
- Infinite duration option with automatic refresh
- Automatic exclusion of inappropriate effects (e.g., no Regeneration for undead)

#### **Day-Based Scaling**
- Mobs progressively strengthen as in-game days pass
- Configurable scaling interval and multiplier
- Maximum cap to prevent infinite scaling
- Optional in-game notifications for scaling milestones
- Stacks with all other multipliers

#### **Dimension-Specific Scaling**
- Configure unique multipliers for each dimension
- 5 customizable dimension slots
- Perfect for making dimensions like the Nether or End more challenging
- Combines with base multipliers and day scaling

### 🎭 Mob Preset System

Create custom configurations for specific mobs with fine-tuned stats:

- **5 Preset Slots**: default, boss, elite, weak, and custom
- **Additive System**: Presets add to base multipliers for precise control
  - Base 1.5x + Preset 3.0x + Dimension 2.0x = 7.0x total
- **Easy Mapping**: Simple format `minecraft:zombie:weak`
- **Flexible**: Any mob can use any preset
- **Persistent**: Mappings survive config reloads

**Example:**
```toml
[mobPresets]
    enabled = "ENABLED"
    mobMapping = [
        "minecraft:zombie:weak",
        "minecraft:skeleton:elite", 
        "minecraft:ender_dragon:boss"
    ]
```

### ⚔️ Ranged/Melee Combat System

Transform ranged mobs (skeletons, pillagers, piglins) into dynamic threats:

#### **Melee Mode**
- Ranged mobs switch to melee weapons when players get close
- Progressive weapon tiers unlock based on world progression:
  - **Overworld/End**: Stone → Iron → Diamond → Netherite Swords
  - **Nether**: Golden → Diamond → Netherite Axes
- **Enchantment System**: Weapons gain enchantments as days pass
  - Sharpness, Fire Aspect, Knockback, Sweeping Edge
  - Levels scale with game progression

#### **Retreat Mode**
- Mobs tactically retreat while maintaining ranged attacks
- Configurable retreat speed and distance
- Intelligent pathfinding to maintain optimal distance
- No melee attacks during retreat

#### **Random Mode**
- Each mob randomly chooses between Melee or Retreat behavior
- Adds unpredictability to encounters

### 🎲 Harmful Effects on Players

Mobs can inflict random debuffs when they hit players:
- Poison (visual + damage over time)
- Slowness (reduced movement speed)
- Wither (stronger damage over time)
- Configurable chance per hit (default: 15%)
- Adjustable durations for each effect

### 🛡️ Advanced Filtering

Fine-tune which mobs are affected:

- **Mob Filter**: Whitelist or blacklist specific mobs
- **Mod ID Filter**: Target mobs from specific mods only
- **Dimension Filter**: Enable/disable in specific dimensions
- **Automatic Exclusion**: Tamed animals never buffed

## 📦 Installation

1. Download the latest version from [Releases](https://modrinth.com/mod/buff-mobs)
2. Install [NeoForge 21.1.1+](https://neoforged.net/) for Minecraft 1.21.1
3. Place the `.jar` file in your `mods` folder
4. Launch Minecraft

## ⚙️ Configuration

Configuration file: `config/buffmobs-common.toml`

### Quick Start Examples

#### **Balanced Difficulty Increase**
```toml
[general]
    enabled = true

[attributes]
    healthMultiplier = 1.5
    damageMultiplier = 1.5
    armorAddition = 5.0

[effects]
    duration = -1  # Infinite
    strengthAmplifier = 1
    resistanceAmplifier = 1
```

#### **Progressive Difficulty (Day-Based)**
```toml
[dayScaling]
    enabled = true
    interval = 7  # Every 7 days
    multiplier = 0.1  # +10% per interval
    maxMultiplier = 3.0  # Cap at 3x
    showNotifications = true
```

#### **Hardcore Nether**
```toml
[dimensionScaling.slot1]
    dimensionName = "minecraft:the_nether"
    healthMultiplier = 200  # 2x health
    damageMultiplier = 200  # 2x damage
    speedMultiplier = 150   # 1.5x speed
```

#### **Boss Preset Example**
```toml
[mobPresets]
    enabled = "ENABLED"
    mobMapping = ["minecraft:ender_dragon:boss", "minecraft:wither:boss"]
    
    [mobPresets.preset2]
        presetName = "boss"
        healthMultiplier = 3.0
        damageMultiplier = 2.5
        speedMultiplier = 1.2
        armorAddition = 10.0
```

#### **Intelligent Ranged Mobs**
```toml
[rangedMeleeSwitching]
    enabled = true
    behaviorMode = "RETREAT"  # or "MELEE" or "RANDOM"
    switchDistance = 4.0
    retreatSpeed = 1.5
```

## 🎮 Commands

All commands require operator permission (level 2).

| Command | Description |
|---------|-------------|
| `/buffmobs debug` | Show detailed stats of the nearest mob |
| `/buffmobs info` | Display current mod configuration |
| `/buffmobs presets` | List all configured presets and mappings |
| `/buffmobs testpreset` | Validate preset system configuration |
| `/buffmobs reload` | Reapply buffs to all loaded mobs |

## 🔧 Mod Compatibility

### Recommended Mods
- **[Configured](https://www.curseforge.com/minecraft/mc-mods/configured)**: In-game config editor with full support
- Works with most mob-adding mods automatically

### Known Compatible Mods
- Twilight Forest
- Alex's Mobs
- Ice and Fire
- The Abyss
- And most others!

## 📊 Performance

- Lightweight: Minimal performance impact
- Efficient mob tracking system
- Optimized buff application
- No constant recalculations
- Scales well with hundreds of mobs

## 🐛 Known Issues

- None currently! Report any issues on [GitHub](https://github.com/khotyz/buffmobs/issues)

## 🤝 Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for:
- Bug reports
- Feature requests
- Documentation improvements
- Translations

## 📝 FAQ

**Q: Will this work with modded mobs?**  
A: Yes! The mod automatically detects hostile and neutral mobs from any mod.

**Q: Can I disable buffs for specific mobs?**  
A: Yes, use the mob blacklist in the config.

**Q: Do presets replace base multipliers?**  
A: No! Presets ADD to base multipliers: `(Base + Preset - 1.0) × Dimension × Day`

**Q: Can I have different settings per dimension?**  
A: Yes, use dimension-specific scaling slots.

**Q: Will this make the game too hard?**  
A: It's fully configurable! Start with low multipliers and adjust to your preference.

**Q: Does this affect boss mobs?**  
A: Yes, but you can create special presets or blacklist them.

## 📜 License

This mod is licensed under the [MIT License](LICENSE).

## 🙏 Credits

- **Author**: Khotyz
- **NeoForge Team**: For the excellent modding platform
- **Community**: For feedback and testing

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/khotyz/buffmobs/issues)
- **Discussions**: [GitHub Discussions]()

---

⭐ **Enjoy the challenge!** If you like this mod, consider starring the repository!
