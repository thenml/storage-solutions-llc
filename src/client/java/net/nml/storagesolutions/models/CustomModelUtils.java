package net.nml.storagesolutions.models;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class CustomModelUtils {
	public static List<BakedQuad> createCubeQuads(Vector3f from, Vector3f to, float[] uv, Sprite sprite,
			BlockState state, Identifier modelId, ModelBakeSettings settings) {
		List<BakedQuad> quads = new ArrayList<>();
		BakedQuadFactory factory = new BakedQuadFactory();

		for (Direction direction : Direction.values()) {
			ModelElementTexture texture = new ModelElementTexture(uv, 0);
			ModelElementFace face = new ModelElementFace(direction, -1, modelId.toString(), texture);
			BakedQuad quad = factory.bake(from, to, face, sprite, direction, settings, null, true, modelId);
			quads.add(quad);
		}

		return quads;
	}

	public static List<BakedQuad> createSlabQuads(Vector3f from, Vector3f to, float[] uvSide, float[] uvTop,
			Sprite sprite, BlockState state, Identifier modelId, ModelBakeSettings settings) {
		List<BakedQuad> quads = new ArrayList<>();
		BakedQuadFactory factory = new BakedQuadFactory();

		for (Direction direction : Direction.values()) {
			ModelElementTexture texture = new ModelElementTexture(
					direction.getAxis() == Direction.Axis.Y ? uvTop : uvSide, 0);
			ModelElementFace face = new ModelElementFace(direction, -1, modelId.toString(), texture);
			BakedQuad quad = factory.bake(from, to, face, sprite, direction, settings, null, true, modelId);
			quads.add(quad);
		}

		return quads;
	}
}
