package net.nml.storagesolutions.chest;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

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
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockHitResult hit) {
		BlockEntity blockEntity = world.getBlockEntity(pos);

		if (blockEntity instanceof MaterialChestBlockEntity chestBlockEntity) {
			ItemStack heldItemStack = player.getMainHandStack();
			// TODO: replace with chests nbt
			if (heldItemStack.getItem() instanceof BlockItem blockItem) {
				Identifier blockIdentifier = Registries.BLOCK.getId(blockItem.getBlock());
				if (!player.isCreative()) {
					dropBaseBlock(world, pos);
					heldItemStack.decrement(1);
				}
				chestBlockEntity.setBaseBlockIdentifier(blockIdentifier);

				chestBlockEntity.markDirty();

				return ActionResult.SUCCESS;
			}
			if (player instanceof ServerPlayerEntity serverPlayerEntity) {
				serverPlayerEntity.openHandledScreen(chestBlockEntity.createScreenHandlerFactory());
			}
		}
		return ActionResult.SUCCESS;
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			dropBaseBlock(world, pos);

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		final ItemStack pickStack = super.getPickStack(world, pos, state);
		final BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof MaterialChestBlockEntity chestBlockEntity) {
			NbtCompound nbtCompound = new NbtCompound();
			nbtCompound.putString("BaseBlock", chestBlockEntity.getBaseBlockIdentifier().toString());
			nbtCompound.putInt("SlotCount", chestBlockEntity.size());
			pickStack.setSubNbt("BlockEntityTag", nbtCompound);
			BlockEntityUpdateS2CPacket.create(chestBlockEntity);
		}
		return pickStack;
	}

	private void dropBaseBlock(World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof MaterialChestBlockEntity chestBlockEntity) {
			Block baseBlock = Registries.BLOCK.get(chestBlockEntity.getBaseBlockIdentifier());
			if (!baseBlock.equals(MaterialChestBlockEntity.DEFAULT_BASE_BLOCK_IDENTIFIER)) {
				ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(),
						baseBlock.asItem().getDefaultStack());
			}
		}
	}
}
