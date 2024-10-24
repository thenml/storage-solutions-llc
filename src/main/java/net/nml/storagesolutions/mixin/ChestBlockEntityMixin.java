package net.nml.storagesolutions.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.entity.ChestBlockEntity;

@Mixin(ChestBlockEntity.class)
public abstract class ChestBlockEntityMixin {
	@Shadow
	public abstract int size();

	// this shouldnt break anything, the original code uses a magic number
	@ModifyConstant(method = "<init>(Lnet/minecraft/block/entity/BlockEntityType;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", constant = @Constant(intValue = 27))
	private int inventorySize(int size) {
		return size();
	}
}
