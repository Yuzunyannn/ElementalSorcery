package yuzunyannn.elementalsorcery.api.util.render;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

public class ItemRendererModel implements IModel {

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format,
			Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		return new ItemRendererBakedModel();
	}

	public class ItemRendererBakedModel implements IBakedModel {
		public ItemCameraTransforms.TransformType cameraType;

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			return Collections.emptyList();
		}

		// 平滑光照
		@Override
		public boolean isAmbientOcclusion() {
			return false;
		}

		@Override
		public boolean isGui3d() {
			return false;
		}

		@Override
		public boolean isBuiltInRenderer() {
			// TileEntityItemStackRenderer 绘制
			return true;
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			// 方块破坏，玩家奔跑等粒子效果纹理
			return Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry("");
		}

		@Override
		public ItemOverrideList getOverrides() {
			return new ItemOverrideList(Collections.<ItemOverride>emptyList());
		}

		@Override
		public Pair<? extends IBakedModel, javax.vecmath.Matrix4f> handlePerspective(
				ItemCameraTransforms.TransformType cameraTransformType) {			
			cameraType = cameraTransformType;
			return net.minecraftforge.client.ForgeHooksClient.handlePerspective(this, cameraTransformType);
		}
	}

}