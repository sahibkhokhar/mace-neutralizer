package name.modid.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class ExampleMixin {
	@Inject(
		at = @At("HEAD"),
		method = "damage",
		cancellable = true
	)
	private void neutralizeMaceDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		// Only process on server side
		LivingEntity entity = (LivingEntity)(Object)this;
		if (entity.getEntityWorld() instanceof ServerWorld) {
			// Check if damage is from a player attack
			if (source.getAttacker() instanceof PlayerEntity attacker) {
				// Get the item the attacker is holding
				ItemStack mainHandStack = attacker.getMainHandStack();
				
				// Check if the attacker is holding a mace
				if (mainHandStack.isOf(Items.MACE)) {
					// Cancel the damage - this prevents damage but allows the attack event to proceed
					// Wind burst enchantment will still work because it triggers on attack, not damage
					cir.setReturnValue(false);
				}
			}
		}
	}
}