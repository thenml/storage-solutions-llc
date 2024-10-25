package net.nml.storagesolutions.items;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.nml.storagesolutions.Materials;

public class TieredBlockItem extends BlockItem {
	public TieredBlockItem(Block block, Settings settings) {
		super(block, settings);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		NbtCompound nbt = stack.getSubNbt("BlockEntityTag");
		if (nbt != null)
			if (nbt.contains("SlotCount"))
				return this.getBlock().getTranslationKey() + "." + Materials.getTier(nbt.getInt("SlotCount"));
		return this.getBlock().getTranslationKey();
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		NbtCompound nbt = stack.getSubNbt("BlockEntityTag");
		if (nbt != null) {
			if (nbt.contains("SlotCount"))
				tooltip.add(Text
						.translatable("block.storage-solutions-llc.material_chest.tooltip2", nbt.getInt("SlotCount"))
						.formatted(Formatting.LIGHT_PURPLE));
			if (nbt.contains("BaseBlock"))
				tooltip.add(Text.translatable("block.storage-solutions-llc.material_chest.tooltip1",
						Text.translatable(new Identifier(nbt.getString("BaseBlock")).toTranslationKey("block"))
								.formatted(Formatting.WHITE))
						.formatted(Formatting.GRAY));
		}
		this.getBlock().appendTooltip(stack, world, tooltip, context);
	}
}
