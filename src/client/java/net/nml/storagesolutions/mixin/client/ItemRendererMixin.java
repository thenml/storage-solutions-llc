package net.nml.storagesolutions.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.nml.storagesolutions.blocks.MaterialChests;
import net.nml.storagesolutions.chest.MaterialChestBlockEntity;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
	// > i gonna render block entities in inventory
	// > damn block entities got hands
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/BakedModel;isBuiltin()Z"), method = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", cancellable = true)
	public void renderItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded,
			MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model,
			CallbackInfo ci) {
		if (!stack.isOf(MaterialChests.MATERIAL_CHEST_BLOCK.asItem()))
			return;

		MaterialChestBlockEntity blockEntity = new MaterialChestBlockEntity(new BlockPos(0, 0, 0),
				MaterialChests.MATERIAL_CHEST_BLOCK.getDefaultState());
		if (stack.getSubNbt("BlockEntityTag") != null) {
			blockEntity
					.setBaseBlockIdentifier(new Identifier(stack.getSubNbt("BlockEntityTag").getString("BaseBlock")));
		}
		MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(blockEntity, matrices,
				vertexConsumers, light, overlay);

		matrices.pop();
		ci.cancel();
	}
}
