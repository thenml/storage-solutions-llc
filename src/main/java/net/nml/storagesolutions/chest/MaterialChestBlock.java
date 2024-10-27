package net.nml.storagesolutions.chest;

import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.nml.storagesolutions.Utils;

public class MaterialChestBlock extends ChestBlock {
	public MaterialChestBlock(Settings settings, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier) {
		super(settings, supplier);
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new MaterialChestBlockEntity(pos, state);
	}

	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction direction = ctx.getHorizontalPlayerFacing().getOpposite();
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		return this.getDefaultState().with(FACING, direction).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			drop(world, pos);
			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		ItemStack pickStack = super.getPickStack(world, pos, state);
		final BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof MaterialChestBlockEntity chestBlockEntity) {
			Utils.tieredItemStack(pickStack, chestBlockEntity);
			BlockEntityUpdateS2CPacket.create(chestBlockEntity);
		}
		return pickStack;
	}

	private void drop(World world, BlockPos pos) {
		ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(),
				getPickStack(world, pos, getDefaultState()));
	}

	@Override
	public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof MaterialChestBlockEntity chest)
			return chest.createScreenHandlerFactory();
		return null;
	}
}
