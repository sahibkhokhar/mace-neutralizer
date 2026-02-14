# Mace Neutralizer Configuration Guide

This guide provides detailed information about configuring the Mace Neutralizer mod.

## Table of Contents

- [Configuration File](#configuration-file)
- [Entity Categories](#entity-categories)
- [Custom Entity Lists](#custom-entity-lists)
- [Command Reference](#command-reference)
- [Use Case Examples](#use-case-examples)
- [Troubleshooting](#troubleshooting)

## Configuration File

The configuration file is located at `config/mace-neutralizer.json` in your server directory.

### File Structure

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

### Configuration Fields

#### `enabled` (boolean)
Master toggle for the entire mod.
- `true`: Mod is active
- `false`: Mod is disabled (mace deals normal damage)

#### `damageAmount` (float)
The amount of damage the mace will deal when the mod is active.
- Value is in half-hearts (2.0 = 1 heart)
- Minimum: 0.0 (no damage, but wind burst still works)
- Examples:
  - `0.0` = No damage
  - `1.0` = 0.5 hearts (default)
  - `2.0` = 1 heart
  - `4.0` = 2 hearts
  - `20.0` = 10 hearts

#### `affectPassiveMobs` (boolean)
Whether to apply the damage modification to passive/neutral mobs.
- Includes: Cows, Pigs, Sheep, Chickens, Horses, Villagers, Iron Golems, etc.
- Default: `true`

#### `affectHostileMobs` (boolean)
Whether to apply the damage modification to hostile mobs.
- Includes: Zombies, Skeletons, Creepers, Spiders, Endermen, etc.
- Default: `true`

#### `affectBosses` (boolean)
Whether to apply the damage modification to boss mobs.
- Includes: Ender Dragon, Wither, Warden, Elder Guardian
- Default: `true`

#### `affectPlayers` (boolean)
Whether to apply the damage modification when attacking players.
- Default: `false` (players take normal mace damage)

#### `customAffectedEntities` (array of strings)
List of entity class names that should always be affected, regardless of category settings.
- Overrides category settings
- Use full Java class names
- Example: `["net.minecraft.entity.passive.VillagerEntity"]`

#### `customExcludedEntities` (array of strings)
List of entity class names that should never be affected (highest priority).
- Overrides both category settings and customAffectedEntities
- Use full Java class names
- Example: `["net.minecraft.entity.passive.HorseEntity"]`

## Entity Categories

### Passive Mobs
Entities that extend `PassiveEntity`:
- Cow, Sheep, Pig, Chicken
- Horse, Donkey, Mule, Llama
- Rabbit, Fox, Wolf (when not angry)
- Cat, Parrot, Panda
- Villager, Iron Golem
- Axolotl, Frog, Turtle
- And more...

### Hostile Mobs
Entities that extend `HostileEntity` or implement `Monster`:
- Zombie, Skeleton, Creeper
- Spider, Cave Spider
- Enderman, Silverfish
- Witch, Phantom
- Drowned, Husk, Stray
- Vindicator, Pillager, Evoker
- And more...

### Bosses
Special boss entities:
- Ender Dragon
- Wither
- Warden
- Elder Guardian

### Players
All player entities (online players).

## Custom Entity Lists

Custom entity lists provide fine-grained control over specific entity types.

### Priority Order

The mod checks entities in this priority order:
1. **Custom Excluded Entities** (highest priority)
2. **Custom Affected Entities**
3. **Category Settings** (passive/hostile/bosses/players)
4. **Default** (not affected)

### Finding Entity Class Names

To find an entity's class name:

1. **In-game debug screen**: Press F3 and look at the entity
2. **Minecraft Wiki**: Most pages list the entity ID
3. **Common pattern**: `net.minecraft.entity.<type>.<Name>Entity`

### Example: Complex Configuration

Protect villagers and horses, but affect everything else:

```json
{
  "enabled": true,
  "damageAmount": 1.0,
  "affectPassiveMobs": true,
  "affectHostileMobs": true,
  "affectBosses": true,
  "affectPlayers": false,
  "customAffectedEntities": [],
  "customExcludedEntities": [
    "net.minecraft.entity.passive.VillagerEntity",
    "net.minecraft.entity.passive.HorseEntity"
  ]
}
```

Or via commands:
```
/maceneutralizer custom exclude net.minecraft.entity.passive.VillagerEntity
/maceneutralizer custom exclude net.minecraft.entity.passive.HorseEntity
```

## Command Reference

### Quick Command Guide

| Command | Description | Example |
|---------|-------------|---------|
| `/maceneutralizer` | Show status | - |
| `/maceneutralizer toggle` | Toggle on/off | - |
| `/maceneutralizer set <bool>` | Enable/disable | `/maceneutralizer set false` |
| `/maceneutralizer damage <amount>` | Set damage | `/maceneutralizer damage 2.0` |
| `/maceneutralizer entities <type> <bool>` | Toggle category | `/maceneutralizer entities passive false` |
| `/maceneutralizer custom add <class>` | Add to affected | `/maceneutralizer custom add net.minecraft.entity.passive.CowEntity` |
| `/maceneutralizer custom exclude <class>` | Add to excluded | `/maceneutralizer custom exclude net.minecraft.entity.passive.VillagerEntity` |
| `/maceneutralizer reload` | Reload config | - |

### Tab Completion

The mod provides tab completion for:
- Command structure (all subcommands)
- Boolean values (true/false)
- Common entity class names
- Currently configured custom entities (for removal)

## Use Case Examples

### Use Case 1: Peaceful Server with Mace Challenges

**Goal**: Keep mace damage normal for bosses, but minimal for everything else.

**Configuration**:
```json
{
  "enabled": true,
  "damageAmount": 1.0,
  "affectPassiveMobs": true,
  "affectHostileMobs": true,
  "affectBosses": false,
  "affectPlayers": false
}
```

**Commands**:
```
/maceneutralizer entities passive true
/maceneutralizer entities hostile true
/maceneutralizer entities bosses false
/maceneutralizer damage 1.0
```

### Use Case 2: PvP Server with Wind Burst Only

**Goal**: Mace deals no damage but wind burst enchantment works for knockback.

**Configuration**:
```json
{
  "enabled": true,
  "damageAmount": 0.0,
  "affectPassiveMobs": true,
  "affectHostileMobs": true,
  "affectBosses": true,
  "affectPlayers": true
}
```

**Commands**:
```
/maceneutralizer damage 0.0
/maceneutralizer entities players true
```

### Use Case 3: Villager Trading Protection

**Goal**: Protect villagers from accidental mace deaths, keep everything else normal.

**Configuration**:
```json
{
  "enabled": true,
  "damageAmount": 1.0,
  "affectPassiveMobs": false,
  "affectHostileMobs": false,
  "affectBosses": false,
  "affectPlayers": false,
  "customAffectedEntities": [
    "net.minecraft.entity.passive.VillagerEntity"
  ]
}
```

**Commands**:
```
/maceneutralizer entities passive false
/maceneutralizer custom add net.minecraft.entity.passive.VillagerEntity
/maceneutralizer damage 0.0
```

### Use Case 4: Mob Grinder Friendly

**Goal**: Disable mace for hostile mobs only, to prevent one-shotting in grinders.

**Configuration**:
```json
{
  "enabled": true,
  "damageAmount": 1.0,
  "affectPassiveMobs": false,
  "affectHostileMobs": true,
  "affectBosses": false,
  "affectPlayers": false
}
```

**Commands**:
```
/maceneutralizer entities hostile true
/maceneutralizer entities passive false
/maceneutralizer entities bosses false
```

### Use Case 5: Custom Modded Entity Control

**Goal**: Affect a specific modded entity that doesn't fit standard categories.

**Commands**:
```
/maceneutralizer custom add com.example.mod.CustomBossEntity
```

The entity will now always be affected, regardless of category settings.

## Troubleshooting

### Config Not Loading

**Problem**: Changes to config file aren't taking effect.

**Solutions**:
1. Check JSON syntax is valid (use a JSON validator)
2. Ensure file is saved with UTF-8 encoding
3. Run `/maceneutralizer reload` to reload from disk
4. Restart the server

### Entity Not Being Affected

**Problem**: A specific entity isn't being affected by the mod.

**Diagnosis**:
1. Check if the mod is enabled: `/maceneutralizer status`
2. Check the entity's category is enabled
3. Check if entity is in custom excluded list
4. Verify the entity is actually a `LivingEntity`

**Solution**:
```
/maceneutralizer status
/maceneutralizer entities list
/maceneutralizer custom list
```

If needed, explicitly add the entity:
```
/maceneutralizer custom add <entity_class_name>
```

### Finding Entity Class Names

**Problem**: Don't know the class name for a modded entity.

**Solutions**:
1. Check the mod's source code or documentation
2. Use a tool like NBT Explorer to examine entity data
3. Enable debug mode in Minecraft (F3) and look at entity data
4. Check the mod's GitHub repository or wiki

### Config File Corrupted

**Problem**: Config file is corrupted or has invalid JSON.

**Solution**:
1. Delete the config file: `config/mace-neutralizer.json`
2. Restart the server (a new default config will be created)
3. Reconfigure using commands or edit the new file

### Commands Not Working

**Problem**: Commands return "Unknown command" or permission errors.

**Diagnosis**:
- Commands require OP level 2 (Gamemaster permission)
- Mod must be installed correctly

**Solution**:
1. Verify you have permission: `/op <username>`
2. Check mod is loaded: `/fabric mods` (if you have Fabric API debug commands)
3. Check server logs for errors

## Advanced Tips

### Backup Your Config

Before making major changes:
```bash
cp config/mace-neutralizer.json config/mace-neutralizer.json.backup
```

### Batch Configuration

Edit the JSON file directly for complex setups:
```json
{
  "customExcludedEntities": [
    "net.minecraft.entity.passive.VillagerEntity",
    "net.minecraft.entity.passive.HorseEntity",
    "net.minecraft.entity.passive.CatEntity",
    "net.minecraft.entity.passive.WolfEntity"
  ]
}
```

Then reload: `/maceneutralizer reload`

### Performance

The mod is highly optimized:
- Thread-safe concurrent access
- No performance impact when disabled
- Minimal overhead per attack (single config check)

### Testing Changes

Create a test world or use a test server to verify configuration before applying to production.

## Support

If you encounter issues not covered here:

1. Check the [GitHub Issues](https://github.com/sahibkhokhar/mace-neutralizer/issues)
2. Review server logs for error messages
3. Open a new issue with:
   - Your configuration file
   - Server logs
   - Steps to reproduce
   - Expected vs actual behavior
