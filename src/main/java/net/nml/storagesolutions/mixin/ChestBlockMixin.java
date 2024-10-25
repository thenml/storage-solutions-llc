package net.nml.storagesolutions.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin {
	@Shadow
	public static DirectionProperty FACING;
	@Shadow
	public static EnumProperty<ChestType> CHEST_TYPE;
	@Shadow
	public static BooleanProperty WATERLOGGED;

	@Inject(cancellable = true, at = @At("HEAD"), method = "onUse(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;")
	public void replaceChest(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {

		if (world.isClient())
			return;

		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (!(blockEntity instanceof ChestBlockEntity existingChest))
			return;

		ItemStack heldItem = player.getMainHandStack();
		if (!(heldItem.getItem() instanceof BlockItem blockItem
				&& blockItem.getBlock() instanceof ChestBlock newChestBlock))
			return;

		int slotLimit = heldItem.getSubNbt("BlockEntityTag") != null
				? heldItem.getSubNbt("BlockEntityTag").getInt("SlotCount")
				: 27;

		int lastOccupiedSlot = -1;
		for (int i = existingChest.size() - 1; i >= 0; i--) {
			if (!existingChest.getStack(i).isEmpty()) {
				lastOccupiedSlot = i;
			}
		}

		if (lastOccupiedSlot > slotLimit) {
			cir.setReturnValue(ActionResult.CONSUME);
			return;
		}

		NbtCompound chestData = new NbtCompound();
		((ChestBlockEntityIMixin) existingChest).invokeWriteNbt(chestData);
		if (heldItem.getSubNbt("BlockEntityTag") != null) {
			chestData.copyFrom(heldItem.getSubNbt("BlockEntityTag"));
		}

		existingChest.clear();
		world.breakBlock(pos, true);

		BlockState newState = newChestBlock.getDefaultState()
				.with(FACING, state.get(FACING))
				.with(CHEST_TYPE, state.get(CHEST_TYPE))
				.with(WATERLOGGED, state.get(WATERLOGGED));

		world.setBlockState(pos, newState);

		ChestBlockEntity newChestEntity = (ChestBlockEntity) world.getBlockEntity(pos);
		if (newChestEntity != null) {
			newChestEntity.readNbt(chestData);
			newChestEntity.markDirty();
		}

		if (!player.isCreative()) {
			heldItem.decrement(1);
		}

		cir.setReturnValue(ActionResult.SUCCESS);
	}
}
