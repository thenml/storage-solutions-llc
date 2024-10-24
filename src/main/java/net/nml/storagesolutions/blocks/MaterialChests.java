package net.nml.storagesolutions.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.nml.storagesolutions.StorageSolutionsLLC;
import net.nml.storagesolutions.chest.MaterialChestBlock;

public class MaterialChests {
	public static final MaterialChestBlock MATERIAL_CHEST_BLOCK = register(
			new MaterialChestBlock(AbstractBlock.Settings.copy(Blocks.CHEST), () -> {
				return MaterialChestBlockEntityTypes.MATERIAL_CHEST_BLOCK_ENTITY;
			}), "material_chest");

	public static MaterialChestBlock register(MaterialChestBlock block, String name) {
		Identifier id = Identifier.of(StorageSolutionsLLC.MOD_ID, name);
		BlockItem blockItem = new BlockItem(block, new Item.Settings());
		Registry.register(Registries.ITEM, id, blockItem);
		return Registry.register(Registries.BLOCK, id, block);
	}

	public static void initialize() {
	}
}
