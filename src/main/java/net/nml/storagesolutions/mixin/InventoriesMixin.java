package net.nml.storagesolutions.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;

@Mixin(Inventories.class)
public class InventoriesMixin {
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtCompound;putByte(Ljava/lang/String;B)V"), method = "Lnet/minecraft/inventory/Inventories;writeNbt(Lnet/minecraft/nbt/NbtCompound;Lnet/minecraft/util/collection/DefaultedList;Z)Lnet/minecraft/nbt/NbtCompound;")
	private static void putInt(NbtCompound nbtCompound, String key, byte value, @Local LocalIntRef i,
			@Local LocalRef<DefaultedList<ItemStack>> stacks) {
		if (stacks.get().size() > 255) {
			nbtCompound.putInt(key, i.get());
		} else {
			nbtCompound.putByte(key, value);
		}
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtCompound;getByte(Ljava/lang/String;)B"), method = "Lnet/minecraft/inventory/Inventories;readNbt(Lnet/minecraft/nbt/NbtCompound;Lnet/minecraft/util/collection/DefaultedList;)V")
	private static byte getInt(NbtCompound nbtCompound, String key, @Local(ordinal = 0) LocalIntRef i,
			@Local LocalRef<DefaultedList<ItemStack>> stacks) {
		if (stacks.get().size() > 255) {
			return 0;
		} else {
			return nbtCompound.getByte(key);
		}
	}

	@ModifyVariable(at = @At("STORE"), ordinal = 1, method = "Lnet/minecraft/inventory/Inventories;readNbt(Lnet/minecraft/nbt/NbtCompound;Lnet/minecraft/util/collection/DefaultedList;)V")
	private static int getInt(int j, @Local(ordinal = 1) LocalRef<NbtCompound> nbtCompound,
			@Local LocalRef<DefaultedList<ItemStack>> stacks) {
		if (stacks.get().size() > 255) {
			return nbtCompound.get().getInt("Slot");
		} else {
			return j;
		}
	}
}
