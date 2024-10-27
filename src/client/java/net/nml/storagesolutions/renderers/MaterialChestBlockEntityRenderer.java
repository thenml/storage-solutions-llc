package net.nml.storagesolutions.renderers;

import org.joml.Vector3f;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.LightmapCoordinatesRetriever;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import net.nml.storagesolutions.StorageSolutionsLLCClient;
import net.nml.storagesolutions.TierTextures;
import net.nml.storagesolutions.chest.MaterialChestBlockEntity;
import net.nml.storagesolutions.models.ModifiedSlabModel;
import net.nml.storagesolutions.registers.RegisterBlocks;

@Environment(EnvType.CLIENT)
public class MaterialChestBlockEntityRenderer<T extends BlockEntity & LidOpenable> implements BlockEntityRenderer<T> {
	private final ModelPart singleChestLid;
	private final ModelPart singleChestBase;
	private final ModelPart singleChestLatch;
	private final ModelPart doubleChestLeftLid;
	private final ModelPart doubleChestLeftBase;
	private final ModelPart doubleChestLeftLatch;
	private final ModelPart doubleChestRightLid;
	private final ModelPart doubleChestRightBase;
	private final ModelPart doubleChestRightLatch;

	public MaterialChestBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
		// Initialize chest models
		ModelPart modelPart = ctx.getLayerModelPart(EntityModelLayers.CHEST);
		this.singleChestBase = modelPart.getChild("bottom");
		this.singleChestLid = modelPart.getChild("lid");
		this.singleChestLatch = modelPart.getChild("lock");
		ModelPart modelPart2 = ctx.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_LEFT);
		this.doubleChestLeftBase = modelPart2.getChild("bottom");
		this.doubleChestLeftLid = modelPart2.getChild("lid");
		this.doubleChestLeftLatch = modelPart2.getChild("lock");
		ModelPart modelPart3 = ctx.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_RIGHT);
		this.doubleChestRightBase = modelPart3.getChild("bottom");
		this.doubleChestRightLid = modelPart3.getChild("lid");
		this.doubleChestRightLatch = modelPart3.getChild("lock");

		Vector3f v = new Vector3f(0f, -0.995f, 0f);
		this.singleChestBase.translate(v);
		this.doubleChestLeftBase.translate(v);
		this.doubleChestRightBase.translate(v);
	}

	@Override
	public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
			int light, int overlay) {
		World world = entity.getWorld();
		BlockState blockState = world != null ? entity.getCachedState()
				: RegisterBlocks.MATERIAL_CHEST_BLOCK.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
		ChestType chestType = blockState.contains(ChestBlock.CHEST_TYPE) ? blockState.get(ChestBlock.CHEST_TYPE)
				: ChestType.SINGLE;
		Block block = blockState.getBlock();

		Block reference_block = Blocks.AIR;

		if (entity instanceof MaterialChestBlockEntity blockEntity) {
			reference_block = Registries.BLOCK.get(blockEntity.getBaseBlockIdentifier());

			if (block instanceof AbstractChestBlock<?> chestBlock) {
				boolean isDouble = chestType != ChestType.SINGLE;
				matrices.push();
				float rotation = blockState.get(ChestBlock.FACING).asRotation();
				matrices.translate(0.5F, 0.5F, 0.5F);
				matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-rotation));
				matrices.translate(-0.5F, -0.5F, -0.5F);
				float openFactor = 0F;
				int adjustedLight = light;

				if (world != null) {
					DoubleBlockProperties.PropertySource<? extends ChestBlockEntity> propertySource = chestBlock
							.getBlockEntitySource(blockState, world, entity.getPos(), true);
					openFactor = 1F - ((Float2FloatFunction) propertySource
							.apply(ChestBlock.getAnimationProgressRetriever(entity))).get(tickDelta);
					openFactor = 1F - openFactor * openFactor * openFactor;
					adjustedLight = ((Int2IntFunction) propertySource.apply(new LightmapCoordinatesRetriever<>()))
							.applyAsInt(light);
				}

				VertexConsumer vertexConsumerBlock = vertexConsumers
						.getBuffer(RenderLayer.getEntityCutout(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE));

				VertexConsumer vertexConsumerChest = TierTextures.getChestTexture(blockEntity.size()).getVertexConsumer(
						vertexConsumers, RenderLayer::getEntityCutout);

				if (isDouble) {
					if (chestType == ChestType.LEFT) {
						renderMaterialChest(matrices, vertexConsumerBlock, vertexConsumerChest, this.doubleChestLeftLid,
								this.doubleChestLeftLatch, this.doubleChestLeftBase, openFactor, adjustedLight, overlay,
								reference_block);
					} else {
						renderMaterialChest(matrices, vertexConsumerBlock, vertexConsumerChest,
								this.doubleChestRightLid, this.doubleChestRightLatch, this.doubleChestRightBase,
								openFactor, adjustedLight, overlay, reference_block);
					}
				} else {
					renderMaterialChest(matrices, vertexConsumerBlock, vertexConsumerChest, this.singleChestLid,
							this.singleChestLatch, this.singleChestBase, openFactor, adjustedLight, overlay,
							reference_block);
				}

				matrices.pop();
			}
		}
	}

	private void renderMaterialChest(MatrixStack matrices, VertexConsumer vertexConsumerBlock,
			VertexConsumer vertexConsumerChest, ModelPart lid, ModelPart latch, ModelPart base, float openFactor,
			int light, int overlay, Block reference_block) {
		lid.pitch = -(openFactor * 1.5707964F);
		latch.pitch = lid.pitch;

		lid.render(matrices, vertexConsumerChest, light, overlay);
		latch.render(matrices, vertexConsumerChest, light, overlay);
		base.render(matrices, vertexConsumerChest, light, overlay);

		BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
		Identifier id = Registries.BLOCK.getId(reference_block);
		BlockState state = reference_block.getDefaultState();

		if (!StorageSolutionsLLCClient.MODIFIED_MODELS.containsKey(id)) {
			Vector3f from = new Vector3f(1f, 0f, 1f);
			Vector3f to = new Vector3f(15f, 9f, 15f);
			float[] uvSide = new float[] { 0f, 7f, 16f, 16f };
			float[] uvTop = new float[] { 0f, 0f, 16f, 16f };
			ModifiedSlabModel model = new ModifiedSlabModel(blockRenderManager.getModel(state).getParticleSprite(),
					id, from, to, uvSide, uvTop);
			// StorageSolutionsLLC.LOGGER.info("Generated model for: " + id);
			StorageSolutionsLLCClient.MODIFIED_MODELS.put(id, model);
		}
		blockRenderManager.getModelRenderer().render(matrices.peek(), vertexConsumerBlock,
				state, StorageSolutionsLLCClient.MODIFIED_MODELS.get(id), 0, 0, 0, light, overlay);
	}
}
