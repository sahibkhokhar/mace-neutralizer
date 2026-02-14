# Mace Neutralizer

A highly configurable Fabric mod that allows you to control mace damage on your server. Set custom damage values, enable/disable for specific entity types (passive mobs, hostile mobs, bosses, players), and manage custom entity lists - all while keeping the wind burst enchantment fully functional.

## Features

- ⚙️ **Configurable Damage**: Set any damage value for mace attacks
- 🎯 **Entity Categories**: Separate control for passive mobs, hostile mobs, bosses, and players
- 📝 **Custom Entity Lists**: Add specific entities to whitelist or blacklist
- 💾 **Persistent Config**: Settings saved to `config/mace-neutralizer.json`
- 🎮 **In-Game Commands**: Full control without editing files
- 🌪️ **Wind Burst Compatible**: Enchantment works normally
- 🔒 **Server-Side Only**: Clients don't need the mod installed

## Installation

1. Download the latest `mace-neutralizer-*.jar` from [Releases](https://github.com/sahibkhokhar/mace-neutralizer/releases)
2. Place it in your server's `mods` folder
3. Start the server - config file will be auto-generated

## Requirements

- Minecraft 1.21.11
- Fabric Loader 0.18.4+
- Fabric API 0.141.3+
- Java 21+

## Configuration

Configuration is stored in `config/mace-neutralizer.json` and can be edited directly or via commands.

### Default Configuration

```json
{
  "enabled": true,
  "damageAmount": 1.0,
  "affectPassiveMobs": true,
  "affectHostileMobs": true,
  "affectBosses": true,
  "affectPlayers": false,
  "customAffectedEntities": [],
  "customExcludedEntities": []
}
```

### Configuration Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `enabled` | boolean | `true` | Master toggle for the mod |
| `damageAmount` | float | `1.0` | Damage dealt by mace (2.0 = 1 heart) |
| `affectPassiveMobs` | boolean | `true` | Apply to passive mobs (cows, pigs, sheep, etc.) |
| `affectHostileMobs` | boolean | `true` | Apply to hostile mobs (zombies, skeletons, etc.) |
| `affectBosses` | boolean | `true` | Apply to bosses (Ender Dragon, Wither, Warden, etc.) |
| `affectPlayers` | boolean | `false` | Apply to players |
| `customAffectedEntities` | array | `[]` | Force specific entity classes to be affected |
| `customExcludedEntities` | array | `[]` | Force specific entity classes to be excluded |

## Commands

All commands require OP level 2 (Gamemaster permission).

### Basic Commands

```
/maceneutralizer
/maceneutralizer status
```
Shows current configuration and status.

```
/maceneutralizer toggle
```
Toggles the mod on/off.

```
/maceneutralizer set <true|false>
```
Explicitly enable or disable the mod.

```
/maceneutralizer reload
```
Reloads configuration from disk.

### Damage Configuration

```
/maceneutralizer damage <amount>
```
Sets the damage amount (in half-hearts). Examples:
- `/maceneutralizer damage 1.0` - 0.5 hearts
- `/maceneutralizer damage 2.0` - 1 heart
- `/maceneutralizer damage 0.0` - No damage (wind burst still works!)

### Entity Category Management

```
/maceneutralizer entities passive <true|false>
```
Enable/disable for passive mobs (cows, pigs, sheep, chickens, villagers, etc.)

```
/maceneutralizer entities hostile <true|false>
```
Enable/disable for hostile mobs (zombies, skeletons, creepers, spiders, etc.)

```
/maceneutralizer entities bosses <true|false>
```
Enable/disable for bosses (Ender Dragon, Wither, Warden, Elder Guardian)

```
/maceneutralizer entities players <true|false>
```
Enable/disable for players

```
/maceneutralizer entities list
```
Shows current entity category settings.

### Custom Entity Management

Use these commands for fine-grained control over specific entity types.

```
/maceneutralizer custom add <entity_class>
```
Force a specific entity class to be affected (overrides category settings).

Example: `/maceneutralizer custom add net.minecraft.entity.passive.VillagerEntity`

```
/maceneutralizer custom remove <entity_class>
```
Remove an entity from the custom affected list.

```
/maceneutralizer custom exclude <entity_class>
```
Force a specific entity class to be excluded (highest priority).

```
/maceneutralizer custom unexclude <entity_class>
```
Remove an entity from the custom excluded list.

```
/maceneutralizer custom list
```
Shows all custom entity lists.

### Common Entity Classes

For use with custom entity commands:

**Passive Mobs:**
- `net.minecraft.entity.passive.CowEntity`
- `net.minecraft.entity.passive.SheepEntity`
- `net.minecraft.entity.passive.PigEntity`
- `net.minecraft.entity.passive.ChickenEntity`
- `net.minecraft.entity.passive.VillagerEntity`
- `net.minecraft.entity.passive.HorseEntity`

**Hostile Mobs:**
- `net.minecraft.entity.mob.ZombieEntity`
- `net.minecraft.entity.mob.SkeletonEntity`
- `net.minecraft.entity.mob.CreeperEntity`
- `net.minecraft.entity.mob.SpiderEntity`
- `net.minecraft.entity.mob.EndermanEntity`

**Bosses:**
- `net.minecraft.entity.boss.dragon.EnderDragonEntity`
- `net.minecraft.entity.boss.WitherEntity`
- `net.minecraft.entity.mob.WardenEntity`
- `net.minecraft.entity.mob.ElderGuardianEntity`

## Usage Examples

### Example 1: PvE Server Setup
Disable mace for all mobs but keep it normal for players:
```
/maceneutralizer entities passive true
/maceneutralizer entities hostile true
/maceneutralizer entities bosses true
/maceneutralizer entities players false
/maceneutralizer damage 1.0
```

### Example 2: Boss-Only Challenge
Only affect boss mobs:
```
/maceneutralizer entities passive false
/maceneutralizer entities hostile false
/maceneutralizer entities bosses true
/maceneutralizer entities players false
```

### Example 3: Protect Villagers Only
Disable mace damage for villagers specifically:
```
/maceneutralizer custom exclude net.minecraft.entity.passive.VillagerEntity
```

### Example 4: Zero-Damage Wind Burst
Keep wind burst knockback but deal no damage:
```
/maceneutralizer damage 0.0
```

## Priority System

Entity filtering follows this priority order:

1. **Custom Excluded Entities** (highest priority) - Never affected
2. **Custom Affected Entities** - Always affected
3. **Entity Categories** - Based on passive/hostile/boss/player settings
4. **Default** - Unknown entity types are not affected

## Building from Source

```bash
git clone https://github.com/sahibkhokhar/mace-neutralizer.git
cd mace-neutralizer
./gradlew build
```

The built jar will be in `build/libs/`.

## Compatibility

- ✅ Works server-side only (clients don't need it)
- ✅ Compatible with wind burst enchantment
- ✅ Thread-safe configuration
- ✅ Multiplayer compatible
- ✅ Works with modded entities (use custom entity lists)

## Support

- **Issues**: [GitHub Issues](https://github.com/sahibkhokhar/mace-neutralizer/issues)
- **Source**: [GitHub Repository](https://github.com/sahibkhokhar/mace-neutralizer)

## License

This mod is available under the CC0-1.0 license.

## Changelog

### v1.1.0 (Upcoming)
- Added comprehensive configuration system
- Added entity category filtering (passive, hostile, bosses, players)
- Added custom entity whitelist/blacklist
- Added configurable damage values
- Added persistent JSON configuration
- Rewrote command system with extensive subcommands
- Added tab completion for entity classes
- Added detailed status displays

### v1.0.1
- Updated to Minecraft 1.21.11
- Fixed permission system for new API

### v1.0.0
- Initial release for Minecraft 1.21.10
- Basic mace damage neutralization
- Simple toggle command
