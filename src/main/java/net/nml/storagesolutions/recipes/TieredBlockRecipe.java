package net.nml.storagesolutions.recipes;

import net.minecraft.block.Block;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.nml.storagesolutions.Materials;
import net.nml.storagesolutions.Utils;
import net.nml.storagesolutions.items.TieredBlockItem;
import net.nml.storagesolutions.registers.RegisterOthers;

public class TieredBlockRecipe extends SpecialCraftingRecipe {
	private final Block upgradable;
	private final Block result;

	public TieredBlockRecipe(Identifier id, CraftingRecipeCategory category, Block upgradable, Block result) {
		super(id, category);
		this.upgradable = upgradable;
		this.result = result;
	}

	@Override
	public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
		ItemStack itemStack1 = null;
		ItemStack itemStack2 = null;

		for (int j = 0; j < inventory.size(); ++j) {
			ItemStack itemStack = inventory.getStack(j);
			if (!itemStack.isEmpty()) {
				if (itemStack1 == null) {
					itemStack1 = itemStack;
				} else if (itemStack2 == null) {
					itemStack2 = itemStack;
				} else {
					return ItemStack.EMPTY;
				}
			}
		}
		if (itemStack1.getItem() instanceof BlockItem blockItem1
				&& itemStack2.getItem() instanceof BlockItem blockItem2) {
			ItemStack upgradable;
			Block baseBlock;

			if (blockItem1.getBlock() == this.upgradable || blockItem1.getBlock() == this.result) {
				upgradable = itemStack1;
				baseBlock = blockItem2.getBlock();
			} else if (blockItem2.getBlock() == this.upgradable || blockItem2.getBlock() == this.result) {
				upgradable = itemStack2;
				baseBlock = blockItem1.getBlock();
			} else {
				return ItemStack.EMPTY;
			}

			int slotCount = Materials.blocks.getOrDefault(baseBlock, 27);
			if (upgradable.getItem() instanceof TieredBlockItem) {
				if (upgradable.getSubNbt("BlockEntityTag") != null) {
					slotCount = Math.max(slotCount, upgradable.getSubNbt("BlockEntityTag").getInt("SlotCount"));
				}
			}

			ItemStack result = Utils.tieredItemStack(this.result, baseBlock, slotCount);
			return result;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height == 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RegisterOthers.MATERIAL_CHEST_RECIPE;
	}

	@Override
	public boolean matches(RecipeInputInventory inventory, World world) {
		ItemStack itemStack1 = null;
		ItemStack itemStack2 = null;

		for (int j = 0; j < inventory.size(); ++j) {
			ItemStack itemStack = inventory.getStack(j);
			if (!itemStack.isEmpty()) {
				if (itemStack1 == null) {
					itemStack1 = itemStack;
				} else if (itemStack2 == null) {
					itemStack2 = itemStack;
				} else {
					return false;
				}
			}
		}
		if (itemStack1 == null || itemStack2 == null)
			return false;
		if (!(itemStack1.getItem() instanceof BlockItem blockItem1))
			return false;
		if (!(itemStack2.getItem() instanceof BlockItem blockItem2))
			return false;

		boolean bl1 = blockItem1.getBlock() == this.upgradable || blockItem1.getBlock() == this.result;
		boolean bl2 = blockItem2.getBlock() == this.upgradable || blockItem2.getBlock() == this.result;

		return bl1 ^ bl2;
	}
}
