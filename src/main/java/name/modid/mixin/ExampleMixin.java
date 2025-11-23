package name.modid.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class ExampleMixin {
	@ModifyVariable(
		at = @At("HEAD"),
		method = "damage",
		ordinal = 0,
		argsOnly = true
	)
	private float neutralizeMaceDamage(float originalAmount, ServerWorld world, DamageSource source) {
		// Check if damage is from a player attack
		if (source.getAttacker() instanceof PlayerEntity attacker) {
			// Get the item the attacker is holding
			ItemStack mainHandStack = attacker.getMainHandStack();
			
			// Check if the attacker is holding a mace
			if (mainHandStack.isOf(Items.MACE)) {
				// Set damage to a minimal fixed amount (0.5 hearts = 1.0 damage)
				// This allows wind burst to work while keeping damage minimal regardless of fall height
				return 1.0f;
			}
		}
		return originalAmount;
	}
}