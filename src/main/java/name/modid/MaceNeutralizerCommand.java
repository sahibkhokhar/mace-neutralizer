package name.modid;

import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class MaceNeutralizerCommand {
	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("maceneutralizer")
				.requires(source -> source.hasPermissionLevel(2)) // Requires OP level 2
				.then(CommandManager.literal("toggle")
					.executes(context -> {
						MaceNeutralizerConfig.toggle();
						boolean enabled = MaceNeutralizerConfig.isEnabled();
						context.getSource().sendFeedback(() -> Text.literal("Mace Neutralizer is now " + (enabled ? "enabled" : "disabled")), true);
						return enabled ? 1 : 0;
					}))
				.then(CommandManager.literal("set")
					.then(CommandManager.argument("enabled", BoolArgumentType.bool())
						.executes(context -> {
							boolean enabled = BoolArgumentType.getBool(context, "enabled");
							MaceNeutralizerConfig.setEnabled(enabled);
							context.getSource().sendFeedback(() -> Text.literal("Mace Neutralizer is now " + (enabled ? "enabled" : "disabled")), true);
							return enabled ? 1 : 0;
						})))
				.executes(context -> {
					boolean enabled = MaceNeutralizerConfig.isEnabled();
					context.getSource().sendFeedback(() -> Text.literal("Mace Neutralizer is currently " + (enabled ? "enabled" : "disabled")), false);
					return enabled ? 1 : 0;
				}));
		});
	}
}
