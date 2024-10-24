package net.nml.storagesolutions.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;
import net.nml.storagesolutions.screenhandlers.DynamicSlotScreenHandler;

@Mixin(targets = "net.minecraft.block.entity.ChestBlockEntity$1")
public abstract class ChestBlockEntity1Mixin {
	// easiest way to implement chest animations fr fr
	// i hate that this actually works
	// TODO: fix chest animation when single chests are next to each other
	@Inject(at = @At("RETURN"), method = "isPlayerViewing", cancellable = true)
	private void isPlayerViewing(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
		if (player.currentScreenHandler instanceof DynamicSlotScreenHandler) {
			cir.setReturnValue(true);
		}
	}
}