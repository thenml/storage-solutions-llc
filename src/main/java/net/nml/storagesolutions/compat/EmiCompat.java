package net.nml.storagesolutions.compat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.api.widget.SlotWidget;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.nml.storagesolutions.Materials;
import net.nml.storagesolutions.StorageSolutionsLLC;
import net.nml.storagesolutions.Utils;
import net.nml.storagesolutions.registers.RegisterBlocks;

public class EmiCompat implements EmiPlugin {
	private EmiIngredient blocksIngredient;
	private List<Block> blocks = new ArrayList<>();

	@Override
	public void register(EmiRegistry registry) {
		EmiStack chest = EmiStack.of(RegisterBlocks.MATERIAL_CHEST_BLOCK).comparison(Comparison.compareNbt());

		blocks.clear();
		Registries.BLOCK.getKeys().forEach(key -> blocks.add(Registries.BLOCK.get(key)));
		blocksIngredient = EmiIngredient.of(Ingredient.ofStacks(blocks.stream().map(ItemStack::new)));

		Materials.blocks.entrySet().stream().sorted((b, a) -> Integer.compare(a.getValue(), b.getValue()))
				.forEach(entry -> {
					Block baseBlock = entry.getKey();

					ItemStack result = Utils.tieredItemStack(RegisterBlocks.MATERIAL_CHEST_BLOCK, baseBlock);

					registry.addEmiStackAfter(EmiStack.of(result), chest);
					registry.addRecipe(
							new TieredBlockUpgradeRecipe(EmiIngredient.of(ConventionalItemTags.CHESTS), baseBlock,
									RegisterBlocks.MATERIAL_CHEST_BLOCK, new Identifier(StorageSolutionsLLC.MOD_ID,
											"material_chest_" + Registries.BLOCK.getId(baseBlock).toTranslationKey())));
				});
		registry.removeEmiStacks(chest);

		// please someone tell me how to stop vscode's java formatter to limit lines to
		// 100 characters
		registry.addRecipe(
				new TieredBlockRecipe(EmiIngredient.of(ConventionalItemTags.CHESTS), blocksIngredient,
						RegisterBlocks.MATERIAL_CHEST_BLOCK,
						new Identifier(StorageSolutionsLLC.MOD_ID, "material_chest")));
	}

	private class TieredBlockRecipe extends EmiPatternCraftingRecipe {
		EmiIngredient upgradable;
		Block output;

		public TieredBlockRecipe(EmiIngredient upgradable, EmiIngredient block, Block output, Identifier id) {
			super(List.of(upgradable, block), EmiStack.of(output), id);
			this.upgradable = upgradable;
			this.output = output;
		}

		@Override
		public SlotWidget getInputWidget(int slot, int x, int y) {
			if (slot == 4) {
				return new SlotWidget(upgradable, x, y);
			} else if (slot == 7) {
				return new GeneratedSlotWidget(r -> {
					return EmiStack.of(getBaseBlock(r));
				}, unique, x, y);
			}
			return new SlotWidget(EmiStack.EMPTY, x, y);
		}

		@Override
		public SlotWidget getOutputWidget(int x, int y) {
			return new GeneratedSlotWidget(this::getTieredBlock, unique, x, y);
		}

		private Block getBaseBlock(Random random) {
			return blocks.get(random.nextInt(blocks.size()));
		}

		private EmiStack getTieredBlock(Random random) {
			return EmiStack.of(Utils.tieredItemStack(output, getBaseBlock(random)));
		}
	}

	private class TieredBlockUpgradeRecipe extends EmiPatternCraftingRecipe {
		EmiIngredient upgradable;
		Block baseBlock;
		Block output;

		public TieredBlockUpgradeRecipe(EmiIngredient upgradable, Block baseBlock, Block output, Identifier id) {
			super(List.of(upgradable, EmiIngredient.of(Ingredient.ofItems(baseBlock))), EmiStack.of(output), id);
			this.upgradable = upgradable;
			this.baseBlock = baseBlock;
			this.output = output;
		}

		@Override
		public SlotWidget getInputWidget(int slot, int x, int y) {
			if (slot == 4) {
				return new SlotWidget(upgradable, x, y);
			} else if (slot == 7) {
				return new SlotWidget(EmiIngredient.of(Ingredient.ofItems(baseBlock)), x, y);
			}
			return new SlotWidget(EmiStack.EMPTY, x, y);
		}

		@Override
		public SlotWidget getOutputWidget(int x, int y) {
			ItemStack result = Utils.tieredItemStack(output, baseBlock);
			return new SlotWidget(EmiStack.of(result), x, y);
		}
	}
}
