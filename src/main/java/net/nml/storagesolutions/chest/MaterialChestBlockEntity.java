package net.nml.storagesolutions.chest;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.nml.storagesolutions.blocks.MaterialChestBlockEntityTypes;
import net.nml.storagesolutions.screenhandlers.DynamicSlotScreenHandler;

public class MaterialChestBlockEntity extends ChestBlockEntity {
	public static final Block DEFAULT_BASE_BLOCK_IDENTIFIER = Blocks.STONE_BRICKS;
	public static final int DEFAULT_SLOT_COUNT = 27;
	private Identifier baseBlockIdentifier;
	private int slotCount;

	public MaterialChestBlockEntity(BlockPos pos, BlockState state) {
		super(MaterialChestBlockEntityTypes.MATERIAL_CHEST_BLOCK_ENTITY, pos, state);
		this.baseBlockIdentifier = Registries.BLOCK.getId(DEFAULT_BASE_BLOCK_IDENTIFIER); // default
		this.slotCount = DEFAULT_SLOT_COUNT; // TODO: temporary
	}

	public ExtendedScreenHandlerFactory createScreenHandlerFactory() {
		ChestBlockEntity chestBlockEntity = this;
		return new ExtendedScreenHandlerFactory() {
			@Override
			public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
				buf.writeInt(chestBlockEntity.size());
			}

			@Override
			public Text getDisplayName() {
				return chestBlockEntity.getName();
			}

			@Override
			public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
				return new DynamicSlotScreenHandler(syncId, inv, chestBlockEntity.size(),
						ScreenHandlerContext.create(chestBlockEntity.getWorld(), chestBlockEntity.getPos()));
			}
		};
	}

	@Override
	public int size() {
		return slotCount;
	}

	public void resizeInventory(int oldSize, int newSize) {
		if (oldSize != newSize) {
			DefaultedList<ItemStack> inventory = DefaultedList.ofSize(newSize, ItemStack.EMPTY);
			DefaultedList<ItemStack> oldInventory = this.getInvStackList();
			// StorageSolutionsLLC.LOGGER.info("old inventory: " + oldInventory);
			// StorageSolutionsLLC.LOGGER.info("new inventory: " + inventory);
			for (int i = 0; i < inventory.size() && i < oldInventory.size(); i++) {
				inventory.set(i, oldInventory.get(i));
			}
			this.setInvStackList(inventory);
			this.slotCount = newSize;
			this.markDirty();
		}
	}

	@Override
	protected DefaultedList<ItemStack> getInvStackList() {
		// bad fix for empty inventory on place (TODO)
		DefaultedList<ItemStack> inventory = super.getInvStackList();
		if (inventory.size() == 0) {
			inventory = DefaultedList.ofSize(DEFAULT_SLOT_COUNT, ItemStack.EMPTY);
			this.setInvStackList(inventory);
		}
		return inventory;
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		if (nbt.contains("SlotCount")) {
			resizeInventory(slotCount, nbt.getInt("SlotCount"));
		} else {
			resizeInventory(slotCount, DEFAULT_SLOT_COUNT);
		}
		super.readNbt(nbt);
		if (nbt.contains("BaseBlock")) {
			baseBlockIdentifier = new Identifier(nbt.getString("BaseBlock"));
		}
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putString("BaseBlock", baseBlockIdentifier.toString());
		nbt.putInt("SlotCount", slotCount);
	}

	public Identifier getBaseBlockIdentifier() {
		return baseBlockIdentifier;
	}

	public void setBaseBlockIdentifier(Identifier baseBlockIdentifier) {
		this.baseBlockIdentifier = baseBlockIdentifier;
	}

	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return this.createNbt();
	}
}
