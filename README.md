# BuffMobs

A comprehensive Minecraft Fabric mod that enhances gameplay by making hostile mobs progressively more challenging through configurable buffs and scaling mechanics.

## Features

### 🚀 **Mob Buffing System**
- **Attribute Multipliers**: Enhance mob health, damage, speed, attack speed, armor, and armor toughness
- **Potion Effects**: Apply beneficial effects like Strength, Speed, Resistance, and Regeneration to mobs
- **Visual Effects**: Configurable particle effects for buffed mobs

### 📅 **Day-Based Scaling**
- Progressive difficulty that increases over time
- Configurable scaling intervals (default: every 7 days)
- Maximum multiplier caps to prevent excessive scaling
- Real-time notifications showing current scaling status
- Two notification modes: every day or only on scaling increases

### ⚔️ **Enhanced Combat Mechanics**
- **Attack Speed Overrides**: True attack speed scaling that bypasses vanilla limitations
- **Harmful Effects**: Mobs can apply poison, slowness, or wither effects on attack
- **Projectile Enhancement**: Ranged attacks from buffed mobs can also apply harmful effects
- **Explosion Effects**: Even explosive damage from mobs can trigger harmful effects

### 🎯 **Advanced Filtering System**
- **Mob Filtering**: Whitelist/blacklist specific mobs by registry name
- **Mod Filtering**: Control which mods' mobs are affected
- **Dimension Filtering**: Restrict buffs to specific dimensions
- Smart filtering that only affects hostile and neutral mobs

## Configuration

All features are highly configurable through the mod's JSON config file located at `.minecraft/config/buffmobs.json`. The config includes helpful comments and validation. Key settings include:

### Core Settings
- `enabled`: Master switch for the entire mod
- `visualEffects`: Show/hide particle effects on buffed mobs

### Attribute Multipliers
- `healthMultiplier`: Mob health scaling (1.0-10.0)
- `damageMultiplier`: Attack damage scaling (1.0-10.0)
- `speedMultiplier`: Movement speed scaling (1.0-5.0)
- `attackSpeedMultiplier`: Attack speed scaling (1.0-10.0)
- `armorAddition`: Additional armor points (0-20)
- `armorToughnessAddition`: Additional armor toughness (0-10)

### Day Scaling System
- `dayScaling.enabled`: Enable time-based progression
- `dayScaling.interval`: Days between scaling increases (1-365)
- `dayScaling.multiplier`: Multiplier increase per interval (0.01-2.0)
- `dayScaling.maxMultiplier`: Maximum scaling cap (1.0-10.0)

### Harmful Effects
- `harmfulEffects.enabled`: Enable mob-inflicted debuffs
- `harmfulEffects.chance`: Probability of applying effects (0.0-1.0)
- Configurable durations for poison, slowness, and wither effects

### Filtering Options
- Separate whitelist/blacklist systems for mobs, mods, and dimensions
- Fine-grained control over which entities are affected

## Installation

### Requirements
- **Minecraft**: 1.19+
- **Fabric Loader**: 0.14+
- **Fabric API**: Latest version
- **Java**: 17+

### Steps
1. Download the latest release from the [Releases](../../releases) page
2. Install Fabric Loader from [FabricMC](https://fabricmc.net/use/)
3. Download Fabric API from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/fabric-api) or [Modrinth](https://modrinth.com/mod/fabric-api)
4. Place both the Fabric API and BuffMobs `.jar` files in your `mods` folder
5. Launch Minecraft with Fabric
6. Configure the mod through the JSON config file in `.minecraft/config/buffmobs.json`

## Development Setup

This mod is built using the Fabric mod development environment.

### Setting up the Development Environment

1. **Clone the Repository**
   ```bash
   git clone --branch fabric --single-branch https://github.com/khotyyz/buffmobs.git
   cd buffmobs
   ```

2. **Import into IDE**
   - **IntelliJ IDEA** (Recommended): Open the `build.gradle` file
   - **Eclipse**: Import as existing Gradle project
   - **VS Code**: Open folder with Fabric development extensions

3. **Generate Development Environment**
   ```bash
   ./gradlew genSources
   ```

4. **Refresh Dependencies**
   ```bash
   ./gradlew --refresh-dependencies
   ```

5. **Clean Build** (if needed)
   ```bash
   ./gradlew clean
   ```

### Building the Mod

```bash
./gradlew build
```

The compiled mod will be available in `build/libs/`

## Compatibility

BuffMobs is designed to work with:
- ✅ Most Fabric mob-adding mods
- ✅ Fabric dimension mods  
- ✅ Combat enhancement mods
- ✅ Difficulty progression mods
- ✅ Performance optimization mods (like Lithium, Phosphor)

The mod's filtering system allows you to exclude incompatible mobs or entire mod ecosystems if needed.

### Configuration Examples

### Hardcore Mode
```json
{
  "attributes": {
    "healthMultiplier": 3.0,
    "damageMultiplier": 2.5
  },
  "dayScaling": {
    "enabled": true,
    "interval": 3
  },
  "harmfulEffects": {
    "chance": 0.3
  }
}
```

### PvE Focus (No Harmful Effects)
```json
{
  "attributes": {
    "healthMultiplier": 2.0,
    "speedMultiplier": 1.5
  },
  "harmfulEffects": {
    "enabled": false
  },
  "general": {
    "visualEffects": false
  }
}
```

### Boss-Only Mode
```json
{
  "mobFilter": {
    "useWhitelist": true,
    "whitelist": ["minecraft:wither", "minecraft:ender_dragon"]
  },
  "attributes": {
    "healthMultiplier": 5.0
  }
}
```

## Troubleshooting

### Common Issues

**Mobs not getting buffed?**
- Check if the mod is enabled in config
- Verify the mob isn't in the blacklist
- Ensure the dimension is allowed

**Performance issues?**
- Reduce `attackSpeedMultiplier` values
- Disable `overrideAttackTimers` if needed
- Use dimension filtering to limit scope

**Config not working?**
- Delete the `buffmobs.json` file in `.minecraft/config/` and restart to regenerate defaults
- Check JSON syntax - the config file includes helpful comments
- Use a JSON validator if making manual edits

**Mod not loading?**
- Ensure Fabric API is installed
- Check Fabric Loader version compatibility
- Verify Java version (17+ required)

### Reporting Issues

Please report bugs and feature requests on our [Issues](../../issues) page. Include:
- Minecraft version
- Fabric Loader version  
- Fabric API version
- BuffMobs version
- Other installed mods (especially mob-related ones)
- Config file (if relevant)
- Crash logs or error messages

## Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details on:
- Code style and standards
- Pull request process
- Development best practices

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Credits

- Built with [Fabric](https://fabricmc.net/)
- Uses [Fabric API](https://github.com/FabricMC/fabric)
- Inspired by the Minecraft Fabric modding community
- Thanks to all beta testers and contributors

## Additional Resources

- **Fabric Documentation**: https://fabricmc.net/wiki/
- **Fabric Discord**: https://discord.gg/v6v4pMv
- **Fabric API GitHub**: https://github.com/FabricMC/fabric

---

*This mod uses standard Minecraft mappings provided by the Fabric project.*
