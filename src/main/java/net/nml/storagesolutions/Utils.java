package net.nml.storagesolutions;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.MathHelper;
import net.nml.storagesolutions.chest.TieredBlockEntity;

public class Utils {
	// there are 1253 items in vanilla minecraft
	// why tf would you need to store 4x that ammount in a single container
	public static final int MAX_SLOTS = 4095;

	static public int floorSlots(int slots) {
		if (slots < 9)
			return 9;
		int rows = calculateRows(slots);
		int columns = slots / rows;
		if (columns < 3)
			columns = 3;
		return Math.min(rows * columns, MAX_SLOTS);
	}

	static public int calculateRows(int slots) {
		int rows;
		int columns;

		for (rows = 3; rows <= 9; rows++) {
			columns = slots / rows;
			if (columns <= rows * 3 + 3 && columns <= 27) {
				return rows;
			}
		}
		return slots / 27;
	}

	static public int calculateSlots(ToolMaterial material) {
		// TODO: make the slot calculation smoother
		if (material.getDurability() > 500) {
			return MathHelper.floor(material.getDurability() / 13.5f);
		} else {
			return MathHelper.floor(material.getDurability() / 4.5f);
		}
	}

	public static ItemStack tieredItemStack(Block tieredBlock, Block baseBlock, int slotCount) {
		ItemStack result = new ItemStack(tieredBlock);
		NbtCompound nbtCompound = result.getOrCreateSubNbt("BlockEntityTag");
		nbtCompound.putInt("SlotCount", slotCount);
		nbtCompound.putString("BaseBlock", Registries.BLOCK.getId(baseBlock).toString());
		return result;
	}

	public static ItemStack tieredItemStack(Block tieredBlock, Block baseBlock) {
		return tieredItemStack(tieredBlock, baseBlock, Materials.blocks.getOrDefault(baseBlock, 27));
	}

	public static void tieredItemStack(ItemStack itemStack, TieredBlockEntity tieredBlockEntity) {
		NbtCompound nbtCompound = itemStack.getOrCreateSubNbt("BlockEntityTag");
		nbtCompound.putInt("SlotCount", tieredBlockEntity.size());
		nbtCompound.putString("BaseBlock", tieredBlockEntity.getBaseBlockIdentifier().toString());
	}
}
