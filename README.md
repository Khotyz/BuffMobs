# BuffMobs

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.10-green.svg)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/Mod%20Loader-Fabric-blue.svg)](https://fabricmc.net/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![CurseForge](https://img.shields.io/badge/CurseForge-Download-orange.svg)](https://www.curseforge.com/minecraft/mc-mods/buffmobs)
[![Modrinth](https://img.shields.io/badge/Modrinth-Download-green.svg)](https://modrinth.com/mod/buffmobs)

A highly configurable server-side Fabric mod that enhances mob difficulty through stat multipliers, status effects, and AI improvements.

## Features

### Core Systems
- **Attribute Scaling** - Multiply health, damage, speed, attack speed, armor, and toughness
- **Status Effects** - Grant mobs potion effects (Strength, Speed, Resistance, Regeneration)
- **Progressive Difficulty** - Mobs automatically get stronger as in-game days pass
- **Dimension Scaling** - Configure different mob strengths per dimension
- **Mob Presets** - Create custom buff profiles for specific mobs (v2.5.0+)
- **Weapon Switching** - Ranged mobs switch to melee weapons at close range
- **Player Debuffs** - Mobs can inflict Poison, Slowness, or Wither on hit
- **Smart Filtering** - Whitelist/blacklist mobs, mods, or dimensions

### Mob Preset System (v2.5.0)
Create up to 5 custom presets with unique multipliers and assign them to specific mobs.

```json5
"mobPresets": {
  "enabled": true,
  "preset1": {
    "presetName": "boss",
    "healthMultiplier": 5.0,
    "damageMultiplier": 3.0,
    "speedMultiplier": 1.3,
    "attackSpeedMultiplier": 1.5,
    "armorAddition": 15.0,
    "armorToughnessAddition": 8.0
  },
  "mobMapping": [
    "minecraft:ender_dragon:boss",
    "minecraft:wither:boss"
  ]
}
```

## Installation

### Requirements
- Minecraft 1.21.10
- [Fabric Loader](https://fabricmc.net/use/)
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [Cloth Config API](https://modrinth.com/mod/cloth-config) (optional, for in-game config GUI)

### Steps
1. Download BuffMobs from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/buffmobs) or [Modrinth](https://modrinth.com/mod/buffmobs)
2. Place the `.jar` file in your `mods` folder
3. Install Fabric API in the same folder
4. Launch Minecraft

**Server-side only** - Clients don't need to install the mod.

## Configuration

### In-Game (Recommended)
Install Cloth Config API and configure through Mod Menu.

### Manual
Edit `config/buffmobs.json5` after first launch.

### Default Config Location
```
.minecraft/config/buffmobs.json5
```

## Commands

All commands require OP level 2.

| Command | Description |
|---------|-------------|
| `/buffmobs debug` | Show detailed info about the nearest mob |
| `/buffmobs presets` | List all configured presets and mappings |
| `/buffmobs reload` | Reapply buffs to all loaded mobs |
| `/buffmobs info` | Display current mod settings |

## Configuration Examples

### Basic Difficulty Boost
```json5
{
  "enabled": true,
  "attributes": {
    "healthMultiplier": 2.0,
    "damageMultiplier": 1.5,
    "armorAddition": 5.0
  }
}
```

### Progressive Scaling
```json5
{
  "dayScaling": {
    "enabled": true,
    "interval": 7,
    "multiplier": 0.1,
    "maxMultiplier": 5.0,
    "showNotifications": true
  }
}
```

### Dimension-Specific
```json5
{
  "dimensionScaling": {
    "slot1": {
      "dimensionName": "minecraft:the_nether",
      "healthMultiplier": 200,
      "damageMultiplier": 150
    }
  }
}
```

### Boss Presets
```json5
{
  "mobPresets": {
    "enabled": true,
    "preset1": {
      "presetName": "boss",
      "healthMultiplier": 5.0,
      "damageMultiplier": 3.0
    },
    "mobMapping": [
      "minecraft:wither:boss",
      "minecraft:ender_dragon:boss"
    ]
  }
}
```

## Building from Source

### Prerequisites
- JDK 21
- Git

### Steps
```bash
git clone https://github.com/Khotyz/buffmobs.git
cd buffmobs
./gradlew build
```

The built jar will be in `build/libs/`.

## Development

### Project Structure
```
src/main/java/com/khotyz/buffmobs/
├── command/          # Command implementations
├── config/           # Configuration classes
├── event/            # Event handlers
└── util/             # Utility classes
    ├── MobBuffUtil.java
    ├── MobPresetUtil.java
    ├── MeleeWeaponManager.java
    └── RangedMobAIManager.java
```

### Adding New Features
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## Compatibility

### Tested With
- ✅ Fabric API
- ✅ Cloth Config API
- ✅ Modded mobs (configurable)
- ✅ Custom dimensions
- ✅ Other difficulty mods

### Known Issues
- None currently reported

Report issues on the [Issues page](https://github.com/Khotyz/buffmobs/issues).

## Performance

BuffMobs is designed to be lightweight:
- Server-side only (no client processing)
- Efficient buff application on mob spawn
- Minimal tick overhead
- Smart caching and lazy evaluation

## FAQ

**Q: Do clients need to install this mod?**  
A: No, it's server-side only.

**Q: Can I use this with other difficulty mods?**  
A: Yes, BuffMobs is compatible with most mods.

**Q: How do I disable buffs for specific mobs?**  
A: Use the mob filter blacklist in the config.

**Q: Does this work with modded mobs?**  
A: Yes, all hostile/neutral mobs are supported by default.

**Q: Can I make mobs weaker instead of stronger?**  
A: Yes, use multipliers below 1.0 (e.g., 0.5 for half health).

## Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Follow existing code style
4. Add appropriate comments
5. Test your changes
6. Submit a pull request


## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Credits

**Author:** Khotyz  
**Contributors:** 

## Support

- 🐛 **Bug Reports:** [GitHub Issues](https://github.com/Khotyz/buffmobs/issues)
- 💬 **Discord:** [Does Not Exist](#)
- 📚 **Wiki:** [Documentation](https://github.com/Khotyz/buffmobs/wiki)
- ☕ **Support:** [Does Not Exist](#)

## Links

- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/buffmobs)
- [Modrinth](https://modrinth.com/mod/buffmobs)
- [GitHub](https://github.com/Khotyz/buffmobs)
- [Wiki] [Does Not Exist](#)

---

**Made with ❤️ for the Minecraft community**
