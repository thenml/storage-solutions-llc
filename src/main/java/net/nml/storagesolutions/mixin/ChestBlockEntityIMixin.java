package net.nml.storagesolutions.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.nbt.NbtCompound;

@Mixin(ChestBlockEntity.class)
public abstract interface ChestBlockEntityIMixin {
	@Invoker
	public abstract void invokeWriteNbt(NbtCompound nbt);
}
