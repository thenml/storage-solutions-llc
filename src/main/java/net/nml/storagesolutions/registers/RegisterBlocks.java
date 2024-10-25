package net.nml.storagesolutions.registers;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.nml.storagesolutions.StorageSolutionsLLC;
import net.nml.storagesolutions.chest.MaterialChestBlock;
import net.nml.storagesolutions.items.TieredBlockItem;

public class RegisterBlocks {
	public static final MaterialChestBlock MATERIAL_CHEST_BLOCK = register(
			new MaterialChestBlock(AbstractBlock.Settings.copy(Blocks.CHEST), () -> {
				return RegisterBlockTypes.MATERIAL_CHEST_BLOCK_ENTITY;
			}), "material_chest");

	public static MaterialChestBlock register(MaterialChestBlock block, String name) {
		Identifier id = Identifier.of(StorageSolutionsLLC.MOD_ID, name);
		BlockItem blockItem = new TieredBlockItem(block, new Item.Settings());
		Registry.register(Registries.ITEM, id, blockItem);
		return Registry.register(Registries.BLOCK, id, block);
	}

	public static void initialize() {
	}
}
