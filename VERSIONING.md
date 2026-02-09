# Versioning Strategy

This document explains how versions are managed for the Mace Neutralizer mod across different Minecraft versions.

## Release Naming Convention

We use the following format for releases:
```
v{MOD_VERSION}-mc{MINECRAFT_VERSION}
```

**Examples:**
- `v1.0.0-mc1.21.10` - Mod version 1.0.0 for Minecraft 1.21.10
- `v1.0.1-mc1.21.11` - Mod version 1.0.1 for Minecraft 1.21.11

## Mod Version Numbering

We follow semantic versioning with a Minecraft-specific approach:

- **MAJOR** (X.0.0): Breaking changes or major feature additions
- **MINOR** (0.X.0): New Minecraft version support (may include API migrations)
- **PATCH** (0.0.X): Bug fixes and minor improvements for the same MC version

## Current Releases

| Mod Version | Minecraft Version | Release Tag | Status |
|-------------|-------------------|-------------|---------|
| 1.0.1 | 1.21.11 | v1.0.1-mc1.21.11 | ✅ Latest |
| 1.0.0 | 1.21.10 | v1.0.0-mc1.21.10 | Legacy |

## Creating a New Release

When updating to a new Minecraft version:

1. **Update dependencies in `gradle.properties`:**
   ```properties
   minecraft_version=1.21.XX
   yarn_mappings=1.21.XX+build.X
   loader_version=0.XX.X
   fabric_version=0.XXX.X+1.21.XX
   ```

2. **Bump the mod version:**
   - Increment MINOR version for new MC version
   - Reset PATCH to 0 if needed

3. **Update `fabric.mod.json`:**
   ```json
   "depends": {
     "fabricloader": ">=0.XX.X",
     "minecraft": "~1.21.XX",
     ...
   }
   ```

4. **Update `README.md` requirements section**

5. **Build and test:**
   ```bash
   ./gradlew clean build
   ```

6. **Commit changes:**
   ```bash
   git add -A
   git commit -m "Update to Minecraft 1.21.XX"
   git push
   ```

7. **Create GitHub release:**
   ```bash
   gh release create vX.X.X-mc1.21.XX \
     build/libs/mace-neutralizer-X.X.X.jar \
     --title "vX.X.X for Minecraft 1.21.XX" \
     --notes "Release notes here..."
   ```

## Branch Strategy

- **main**: Always contains the latest stable Minecraft version
- **cursor/**: Feature branches for updates and new features
- No separate branches for old MC versions (use releases/tags instead)

## Maintaining Old Versions

Old Minecraft version builds are preserved through GitHub releases. Users can download the appropriate version from the releases page based on their Minecraft version.

If critical bugs need fixing in an old MC version:
1. Checkout the release tag: `git checkout v1.0.0-mc1.21.10`
2. Create a fix branch: `git checkout -b fix/mc1.21.10-critical-bug`
3. Make fixes and test
4. Build and create a new patch release: `v1.0.0.1-mc1.21.10` or `v1.0.0-patch1-mc1.21.10`

## Migration Notes

### Minecraft 1.21.10 → 1.21.11
- **Breaking Change**: Permission system completely refactored
  - Old: `source.hasPermissionLevel(2)`
  - New: `source.getPermissions().hasPermission(DefaultPermissions.GAMEMASTERS)`
- Required import: `net.minecraft.command.DefaultPermissions`
- Permission levels now use constants:
  - Level 0: `DefaultPermissions.ALL`
  - Level 1: `DefaultPermissions.MODERATORS`
  - Level 2: `DefaultPermissions.GAMEMASTERS`
  - Level 3: `DefaultPermissions.ADMINS`
  - Level 4: `DefaultPermissions.OWNERS`
