package name.modid.mixin;

import name.modid.MaceNeutralizerConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
	private float neutralizeMaceDamage(float originalAmount, DamageSource source) {
		// Check if mod is enabled
		if (!MaceNeutralizerConfig.isEnabled()) {
			return originalAmount;
		}
		
		// Check if damage is from a player attack
		if (source.getAttacker() instanceof PlayerEntity attacker) {
			// Get the item the attacker is holding
			ItemStack mainHandStack = attacker.getMainHandStack();
			
			// Check if the attacker is holding a mace
			if (mainHandStack.isOf(Items.MACE)) {
				// Check if this entity type should be affected based on config
				LivingEntity target = (LivingEntity) (Object) this;
				if (MaceNeutralizerConfig.shouldAffectEntity(target)) {
					// Set damage to configured amount
					// This allows wind burst to work while keeping damage configurable
					return MaceNeutralizerConfig.getDamageAmount();
				}
			}
		}
		return originalAmount;
	}
}