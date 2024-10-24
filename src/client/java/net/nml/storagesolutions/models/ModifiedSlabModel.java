package net.nml.storagesolutions.models;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

public class ModifiedSlabModel implements BakedModel {
	private final Vector3f from;
	private final Vector3f to;
	private final float[] uvSide;
	private final float[] uvTop;
	private final Sprite sprite;
	private final Identifier modelId;

	public ModifiedSlabModel(Sprite sprite, Identifier modelId, Vector3f from, Vector3f to, float[] uvSide,
			float[] uvTop) {
		this.sprite = sprite;
		this.modelId = modelId;
		this.from = from;
		this.to = to;
		this.uvSide = uvSide;
		this.uvTop = uvTop;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random random) {
		ModelBakeSettings settings = new ModelBakeSettings() {
			@Override
			public boolean isUvLocked() {
				return true;
			}
		};

		return CustomModelUtils.createSlabQuads(this.from, this.to, this.uvSide, this.uvTop, this.sprite, state,
				this.modelId, settings);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean hasDepth() {
		return true;
	}

	@Override
	public boolean isSideLit() {
		return false;
	}

	@Override
	public boolean isBuiltin() {
		return true;
	}

	@Override
	public Sprite getParticleSprite() {
		return this.sprite;
	}

	@Override
	public ModelOverrideList getOverrides() {
		return ModelOverrideList.EMPTY;
	}

	@Override
	public ModelTransformation getTransformation() {
		return ModelTransformation.NONE;
	}
}
