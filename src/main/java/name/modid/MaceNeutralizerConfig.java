package name.modid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.ElderGuardianEntity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MaceNeutralizerConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = Path.of("config", "mace-neutralizer.json");
	private static final ReadWriteLock lock = new ReentrantReadWriteLock();
	
	private static ConfigData data = new ConfigData();
	
	public static class ConfigData {
		// General settings
		public boolean enabled = true;
		public float damageAmount = 1.0f;
		
		// Entity category settings
		public boolean affectPassiveMobs = true;
		public boolean affectHostileMobs = true;
		public boolean affectBosses = true;
		public boolean affectPlayers = false;
		
		// Custom entity types (by class name)
		public Set<String> customAffectedEntities = new HashSet<>();
		public Set<String> customExcludedEntities = new HashSet<>();
		
		public ConfigData() {}
	}
	
	/**
	 * Loads the config from disk. Creates default config if it doesn't exist.
	 */
	public static void load() {
		lock.writeLock().lock();
		try {
			File configFile = CONFIG_PATH.toFile();
			
			// Create config directory if it doesn't exist
			if (!configFile.getParentFile().exists()) {
				configFile.getParentFile().mkdirs();
			}
			
			// Load existing config or create default
			if (configFile.exists()) {
				try (FileReader reader = new FileReader(configFile)) {
					ConfigData loadedData = GSON.fromJson(reader, ConfigData.class);
					if (loadedData != null) {
						data = loadedData;
						Maceneutralizer.LOGGER.info("Loaded Mace Neutralizer config from " + CONFIG_PATH);
					}
				} catch (Exception e) {
					Maceneutralizer.LOGGER.error("Failed to load config, using defaults", e);
					data = new ConfigData();
				}
			} else {
				// Create default config
				data = new ConfigData();
				save();
				Maceneutralizer.LOGGER.info("Created default Mace Neutralizer config at " + CONFIG_PATH);
			}
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	/**
	 * Saves the current config to disk.
	 */
	public static void save() {
		lock.readLock().lock();
		try {
			File configFile = CONFIG_PATH.toFile();
			
			// Ensure parent directory exists
			if (!configFile.getParentFile().exists()) {
				configFile.getParentFile().mkdirs();
			}
			
			try (FileWriter writer = new FileWriter(configFile)) {
				GSON.toJson(data, writer);
				Maceneutralizer.LOGGER.info("Saved Mace Neutralizer config to " + CONFIG_PATH);
			} catch (IOException e) {
				Maceneutralizer.LOGGER.error("Failed to save config", e);
			}
		} finally {
			lock.readLock().unlock();
		}
	}
	
	// General Settings
	
	public static boolean isEnabled() {
		lock.readLock().lock();
		try {
			return data.enabled;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public static void setEnabled(boolean value) {
		lock.writeLock().lock();
		try {
			data.enabled = value;
			save();
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public static void toggle() {
		lock.writeLock().lock();
		try {
			data.enabled = !data.enabled;
			save();
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public static float getDamageAmount() {
		lock.readLock().lock();
		try {
			return data.damageAmount;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public static void setDamageAmount(float amount) {
		lock.writeLock().lock();
		try {
			data.damageAmount = Math.max(0.0f, amount); // Ensure non-negative
			save();
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	// Entity Category Settings
	
	public static boolean isAffectPassiveMobs() {
		lock.readLock().lock();
		try {
			return data.affectPassiveMobs;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public static void setAffectPassiveMobs(boolean value) {
		lock.writeLock().lock();
		try {
			data.affectPassiveMobs = value;
			save();
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public static boolean isAffectHostileMobs() {
		lock.readLock().lock();
		try {
			return data.affectHostileMobs;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public static void setAffectHostileMobs(boolean value) {
		lock.writeLock().lock();
		try {
			data.affectHostileMobs = value;
			save();
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public static boolean isAffectBosses() {
		lock.readLock().lock();
		try {
			return data.affectBosses;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public static void setAffectBosses(boolean value) {
		lock.writeLock().lock();
		try {
			data.affectBosses = value;
			save();
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public static boolean isAffectPlayers() {
		lock.readLock().lock();
		try {
			return data.affectPlayers;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public static void setAffectPlayers(boolean value) {
		lock.writeLock().lock();
		try {
			data.affectPlayers = value;
			save();
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	// Custom Entity Management
	
	public static void addCustomAffectedEntity(String entityClassName) {
		lock.writeLock().lock();
		try {
			data.customAffectedEntities.add(entityClassName);
			save();
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public static void removeCustomAffectedEntity(String entityClassName) {
		lock.writeLock().lock();
		try {
			data.customAffectedEntities.remove(entityClassName);
			save();
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public static void addCustomExcludedEntity(String entityClassName) {
		lock.writeLock().lock();
		try {
			data.customExcludedEntities.add(entityClassName);
			save();
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public static void removeCustomExcludedEntity(String entityClassName) {
		lock.writeLock().lock();
		try {
			data.customExcludedEntities.remove(entityClassName);
			save();
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public static Set<String> getCustomAffectedEntities() {
		lock.readLock().lock();
		try {
			return new HashSet<>(data.customAffectedEntities);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public static Set<String> getCustomExcludedEntities() {
		lock.readLock().lock();
		try {
			return new HashSet<>(data.customExcludedEntities);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * Determines if a given entity should be affected by the mace neutralizer.
	 * Checks custom lists first, then category settings.
	 */
	public static boolean shouldAffectEntity(LivingEntity entity) {
		if (entity == null) return false;
		
		lock.readLock().lock();
		try {
			String entityClassName = entity.getClass().getName();
			
			// Check custom excluded list first (highest priority)
			if (data.customExcludedEntities.contains(entityClassName)) {
				return false;
			}
			
			// Check custom affected list (second priority)
			if (data.customAffectedEntities.contains(entityClassName)) {
				return true;
			}
			
			// Check by category
			
			// Players
			if (entity instanceof PlayerEntity) {
				return data.affectPlayers;
			}
			
			// Bosses (check before hostile since some bosses extend HostileEntity)
			if (isBoss(entity)) {
				return data.affectBosses;
			}
			
			// Passive mobs
			if (entity instanceof PassiveEntity) {
				return data.affectPassiveMobs;
			}
			
			// Hostile mobs
			if (entity instanceof HostileEntity || entity instanceof Monster) {
				return data.affectHostileMobs;
			}
			
			// Default: don't affect unknown entity types
			return false;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * Checks if an entity is a boss.
	 */
	private static boolean isBoss(LivingEntity entity) {
		// Check for common boss types
		return entity instanceof EnderDragonEntity ||
		       entity instanceof WitherEntity ||
		       entity instanceof WardenEntity ||
		       entity instanceof ElderGuardianEntity;
	}
	
	/**
	 * Gets a copy of the current config data (for display purposes).
	 */
	public static ConfigData getConfigData() {
		lock.readLock().lock();
		try {
			ConfigData copy = new ConfigData();
			copy.enabled = data.enabled;
			copy.damageAmount = data.damageAmount;
			copy.affectPassiveMobs = data.affectPassiveMobs;
			copy.affectHostileMobs = data.affectHostileMobs;
			copy.affectBosses = data.affectBosses;
			copy.affectPlayers = data.affectPlayers;
			copy.customAffectedEntities = new HashSet<>(data.customAffectedEntities);
			copy.customExcludedEntities = new HashSet<>(data.customExcludedEntities);
			return copy;
		} finally {
			lock.readLock().unlock();
		}
	}
}
