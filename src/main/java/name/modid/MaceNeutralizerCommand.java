package name.modid;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.command.DefaultPermissions;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;

public class MaceNeutralizerCommand {
	
	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("maceneutralizer")
				.requires(source -> source.getPermissions().hasPermission(DefaultPermissions.GAMEMASTERS)) // Requires OP level 2
				
				// /maceneutralizer - shows current status
				.executes(context -> {
					return showStatus(context);
				})
				
				// /maceneutralizer toggle - toggles enabled state
				.then(CommandManager.literal("toggle")
					.executes(context -> {
						MaceNeutralizerConfig.toggle();
						boolean enabled = MaceNeutralizerConfig.isEnabled();
						context.getSource().sendFeedback(() -> Text.literal("§aMace Neutralizer is now " + (enabled ? "§2enabled" : "§cdisabled")), true);
						return enabled ? 1 : 0;
					}))
				
				// /maceneutralizer set <true|false> - sets enabled state
				.then(CommandManager.literal("set")
					.then(CommandManager.argument("enabled", BoolArgumentType.bool())
						.executes(context -> {
							boolean enabled = BoolArgumentType.getBool(context, "enabled");
							MaceNeutralizerConfig.setEnabled(enabled);
							context.getSource().sendFeedback(() -> Text.literal("§aMace Neutralizer is now " + (enabled ? "§2enabled" : "§cdisabled")), true);
							return enabled ? 1 : 0;
						})))
				
				// /maceneutralizer status - shows detailed status
				.then(CommandManager.literal("status")
					.executes(context -> {
						return showStatus(context);
					}))
				
				// /maceneutralizer damage <amount> - sets damage amount
				.then(CommandManager.literal("damage")
					.then(CommandManager.argument("amount", FloatArgumentType.floatArg(0.0f))
						.executes(context -> {
							float amount = FloatArgumentType.getFloat(context, "amount");
							MaceNeutralizerConfig.setDamageAmount(amount);
							float hearts = amount / 2.0f;
							context.getSource().sendFeedback(() -> Text.literal(
								String.format("§aMace damage set to §e%.1f §a(§e%.1f hearts§a)", amount, hearts)
							), true);
							return 1;
						})))
				
				// /maceneutralizer entities - entity category management
				.then(CommandManager.literal("entities")
					// /maceneutralizer entities passive <true|false>
					.then(CommandManager.literal("passive")
						.then(CommandManager.argument("enabled", BoolArgumentType.bool())
							.executes(context -> {
								boolean enabled = BoolArgumentType.getBool(context, "enabled");
								MaceNeutralizerConfig.setAffectPassiveMobs(enabled);
								context.getSource().sendFeedback(() -> Text.literal(
									"§aPassive mobs are now " + (enabled ? "§2affected" : "§cnot affected")
								), true);
								return 1;
							})))
					
					// /maceneutralizer entities hostile <true|false>
					.then(CommandManager.literal("hostile")
						.then(CommandManager.argument("enabled", BoolArgumentType.bool())
							.executes(context -> {
								boolean enabled = BoolArgumentType.getBool(context, "enabled");
								MaceNeutralizerConfig.setAffectHostileMobs(enabled);
								context.getSource().sendFeedback(() -> Text.literal(
									"§aHostile mobs are now " + (enabled ? "§2affected" : "§cnot affected")
								), true);
								return 1;
							})))
					
					// /maceneutralizer entities bosses <true|false>
					.then(CommandManager.literal("bosses")
						.then(CommandManager.argument("enabled", BoolArgumentType.bool())
							.executes(context -> {
								boolean enabled = BoolArgumentType.getBool(context, "enabled");
								MaceNeutralizerConfig.setAffectBosses(enabled);
								context.getSource().sendFeedback(() -> Text.literal(
									"§aBosses are now " + (enabled ? "§2affected" : "§cnot affected")
								), true);
								return 1;
							})))
					
					// /maceneutralizer entities players <true|false>
					.then(CommandManager.literal("players")
						.then(CommandManager.argument("enabled", BoolArgumentType.bool())
							.executes(context -> {
								boolean enabled = BoolArgumentType.getBool(context, "enabled");
								MaceNeutralizerConfig.setAffectPlayers(enabled);
								context.getSource().sendFeedback(() -> Text.literal(
									"§aPlayers are now " + (enabled ? "§2affected" : "§cnot affected")
								), true);
								return 1;
							})))
					
					// /maceneutralizer entities list - shows current settings
					.then(CommandManager.literal("list")
						.executes(context -> {
							return showEntitySettings(context);
						})))
				
				// /maceneutralizer custom - custom entity management
				.then(CommandManager.literal("custom")
					// /maceneutralizer custom add <entity_class>
					.then(CommandManager.literal("add")
						.then(CommandManager.argument("entity_class", StringArgumentType.string())
							.suggests((context, builder) -> suggestEntityClasses(context, builder))
							.executes(context -> {
								String entityClass = StringArgumentType.getString(context, "entity_class");
								MaceNeutralizerConfig.addCustomAffectedEntity(entityClass);
								context.getSource().sendFeedback(() -> Text.literal(
									"§aAdded §e" + entityClass + "§a to custom affected entities"
								), true);
								return 1;
							})))
					
					// /maceneutralizer custom remove <entity_class>
					.then(CommandManager.literal("remove")
						.then(CommandManager.argument("entity_class", StringArgumentType.string())
							.suggests((context, builder) -> suggestCustomAffectedEntities(context, builder))
							.executes(context -> {
								String entityClass = StringArgumentType.getString(context, "entity_class");
								MaceNeutralizerConfig.removeCustomAffectedEntity(entityClass);
								context.getSource().sendFeedback(() -> Text.literal(
									"§aRemoved §e" + entityClass + "§a from custom affected entities"
								), true);
								return 1;
							})))
					
					// /maceneutralizer custom exclude <entity_class>
					.then(CommandManager.literal("exclude")
						.then(CommandManager.argument("entity_class", StringArgumentType.string())
							.suggests((context, builder) -> suggestEntityClasses(context, builder))
							.executes(context -> {
								String entityClass = StringArgumentType.getString(context, "entity_class");
								MaceNeutralizerConfig.addCustomExcludedEntity(entityClass);
								context.getSource().sendFeedback(() -> Text.literal(
									"§aAdded §e" + entityClass + "§a to custom excluded entities"
								), true);
								return 1;
							})))
					
					// /maceneutralizer custom unexclude <entity_class>
					.then(CommandManager.literal("unexclude")
						.then(CommandManager.argument("entity_class", StringArgumentType.string())
							.suggests((context, builder) -> suggestCustomExcludedEntities(context, builder))
							.executes(context -> {
								String entityClass = StringArgumentType.getString(context, "entity_class");
								MaceNeutralizerConfig.removeCustomExcludedEntity(entityClass);
								context.getSource().sendFeedback(() -> Text.literal(
									"§aRemoved §e" + entityClass + "§a from custom excluded entities"
								), true);
								return 1;
							})))
					
					// /maceneutralizer custom list - shows custom entities
					.then(CommandManager.literal("list")
						.executes(context -> {
							return showCustomEntities(context);
						})))
				
				// /maceneutralizer reload - reloads config from disk
				.then(CommandManager.literal("reload")
					.executes(context -> {
						MaceNeutralizerConfig.load();
						context.getSource().sendFeedback(() -> Text.literal("§aReloaded Mace Neutralizer configuration from disk"), true);
						return 1;
					}))
			);
		});
	}
	
	private static int showStatus(CommandContext<ServerCommandSource> context) {
		MaceNeutralizerConfig.ConfigData config = MaceNeutralizerConfig.getConfigData();
		float hearts = config.damageAmount / 2.0f;
		
		context.getSource().sendFeedback(() -> Text.literal("§6§l=== Mace Neutralizer Status ==="), false);
		context.getSource().sendFeedback(() -> Text.literal(
			"§eEnabled: " + (config.enabled ? "§2✓ Yes" : "§c✗ No")
		), false);
		context.getSource().sendFeedback(() -> Text.literal(
			String.format("§eDamage Amount: §f%.1f §7(%.1f hearts)", config.damageAmount, hearts)
		), false);
		context.getSource().sendFeedback(() -> Text.literal(""), false);
		context.getSource().sendFeedback(() -> Text.literal("§6Entity Categories:"), false);
		context.getSource().sendFeedback(() -> Text.literal(
			"  §ePassive Mobs: " + (config.affectPassiveMobs ? "§2✓" : "§c✗")
		), false);
		context.getSource().sendFeedback(() -> Text.literal(
			"  §eHostile Mobs: " + (config.affectHostileMobs ? "§2✓" : "§c✗")
		), false);
		context.getSource().sendFeedback(() -> Text.literal(
			"  §eBosses: " + (config.affectBosses ? "§2✓" : "§c✗")
		), false);
		context.getSource().sendFeedback(() -> Text.literal(
			"  §ePlayers: " + (config.affectPlayers ? "§2✓" : "§c✗")
		), false);
		
		if (!config.customAffectedEntities.isEmpty() || !config.customExcludedEntities.isEmpty()) {
			context.getSource().sendFeedback(() -> Text.literal(""), false);
			context.getSource().sendFeedback(() -> Text.literal(
				"§6Custom Entities: §7(use /maceneutralizer custom list)"
			), false);
		}
		
		return 1;
	}
	
	private static int showEntitySettings(CommandContext<ServerCommandSource> context) {
		MaceNeutralizerConfig.ConfigData config = MaceNeutralizerConfig.getConfigData();
		
		context.getSource().sendFeedback(() -> Text.literal("§6§l=== Entity Category Settings ==="), false);
		context.getSource().sendFeedback(() -> Text.literal(
			"§ePassive Mobs: " + (config.affectPassiveMobs ? "§2✓ Affected" : "§c✗ Not Affected")
		), false);
		context.getSource().sendFeedback(() -> Text.literal(
			"§eHostile Mobs: " + (config.affectHostileMobs ? "§2✓ Affected" : "§c✗ Not Affected")
		), false);
		context.getSource().sendFeedback(() -> Text.literal(
			"§eBosses: " + (config.affectBosses ? "§2✓ Affected" : "§c✗ Not Affected")
		), false);
		context.getSource().sendFeedback(() -> Text.literal(
			"§ePlayers: " + (config.affectPlayers ? "§2✓ Affected" : "§c✗ Not Affected")
		), false);
		
		return 1;
	}
	
	private static int showCustomEntities(CommandContext<ServerCommandSource> context) {
		MaceNeutralizerConfig.ConfigData config = MaceNeutralizerConfig.getConfigData();
		
		context.getSource().sendFeedback(() -> Text.literal("§6§l=== Custom Entity Lists ==="), false);
		
		context.getSource().sendFeedback(() -> Text.literal("§eCustom Affected Entities:"), false);
		if (config.customAffectedEntities.isEmpty()) {
			context.getSource().sendFeedback(() -> Text.literal("  §7(none)"), false);
		} else {
			for (String entity : config.customAffectedEntities) {
				context.getSource().sendFeedback(() -> Text.literal("  §a+ §f" + entity), false);
			}
		}
		
		context.getSource().sendFeedback(() -> Text.literal(""), false);
		context.getSource().sendFeedback(() -> Text.literal("§eCustom Excluded Entities:"), false);
		if (config.customExcludedEntities.isEmpty()) {
			context.getSource().sendFeedback(() -> Text.literal("  §7(none)"), false);
		} else {
			for (String entity : config.customExcludedEntities) {
				context.getSource().sendFeedback(() -> Text.literal("  §c- §f" + entity), false);
			}
		}
		
		context.getSource().sendFeedback(() -> Text.literal(""), false);
		context.getSource().sendFeedback(() -> Text.literal("§7Note: Custom lists override category settings"), false);
		
		return 1;
	}
	
	private static CompletableFuture<Suggestions> suggestEntityClasses(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
		// Suggest common entity class names
		String[] suggestions = {
			"net.minecraft.entity.passive.CowEntity",
			"net.minecraft.entity.passive.SheepEntity",
			"net.minecraft.entity.passive.PigEntity",
			"net.minecraft.entity.passive.ChickenEntity",
			"net.minecraft.entity.passive.VillagerEntity",
			"net.minecraft.entity.mob.ZombieEntity",
			"net.minecraft.entity.mob.SkeletonEntity",
			"net.minecraft.entity.mob.CreeperEntity",
			"net.minecraft.entity.mob.SpiderEntity",
			"net.minecraft.entity.mob.EndermanEntity",
			"net.minecraft.entity.boss.WitherEntity",
			"net.minecraft.entity.boss.dragon.EnderDragonEntity",
			"net.minecraft.entity.mob.WardenEntity"
		};
		
		return CommandSource.suggestMatching(suggestions, builder);
	}
	
	private static CompletableFuture<Suggestions> suggestCustomAffectedEntities(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
		return CommandSource.suggestMatching(MaceNeutralizerConfig.getCustomAffectedEntities(), builder);
	}
	
	private static CompletableFuture<Suggestions> suggestCustomExcludedEntities(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
		return CommandSource.suggestMatching(MaceNeutralizerConfig.getCustomExcludedEntities(), builder);
	}
}
