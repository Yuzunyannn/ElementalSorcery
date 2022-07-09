package yuzunyannn.elementalsorcery.render.entity;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.entity.EntityBlockMove;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class RenderEntityBlockMove extends Render<EntityBlockMove> {

	public RenderEntityBlockMove(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBlockMove entity) {
		return null;
	}

	@Override
	public void doRender(EntityBlockMove entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();

		float scale = entity.getScale(partialTicks);
		GlStateManager.translate(x, y, z);
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.rotate(entity.getRoate(), 0, 1, 0);
		doRenderBlock(entity.getBlockState(), entity.getRenderItem(), partialTicks, entity.world,
				new BlockPos(entity.getTrace().getFrom()), null);

		GlStateManager.popMatrix();
	}

	private static void useItemRender(IBlockState state, ItemStack alternative, float partialTicks) {
		double scale = 1;
		Block block = state.getBlock();
		if (block instanceof IPlantable || block instanceof BlockTorch) scale = scale * 1;
		else scale = scale * 2;
		GlStateManager.translate(0, 0.5, 0);
		GlStateManager.scale(scale, scale, scale);
		Minecraft.getMinecraft().getRenderItem().renderItem(alternative, ItemCameraTransforms.TransformType.FIXED);
		GlStateManager.scale(1 / scale, 1 / scale, 1 / scale);
		GlStateManager.translate(0, -0.5, 0);
	}

	private static void useBlockModel(World world, BlockPos pos, IBlockState state) {
		boolean needBlend = state.getBlock().getRenderLayer() != BlockRenderLayer.SOLID;

		TextureBinder.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GlStateManager.pushMatrix();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();

		GlStateManager.translate(-pos.getX() - 0.5, -pos.getY(), -pos.getZ() - 0.5);

		if (needBlend) {
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
					GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		}
		bufferbuilder.begin(7, DefaultVertexFormats.BLOCK);
		BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
		blockrendererdispatcher.getBlockModelRenderer().renderModel(world,
				blockrendererdispatcher.getModelForState(state), state, pos, bufferbuilder, false,
				MathHelper.getPositionRandom(pos));
		tessellator.draw();
		if (needBlend) {
			GlStateManager.disableBlend();
		}

		GlStateManager.popMatrix();
	}

	public static void doRenderBlock(IBlockState state, ItemStack alternative, float partialTicks,
			@Nullable World world, @Nullable BlockPos pos, @Nullable TileEntity tileEntity) {

		if (world == null || pos == null) {
			useItemRender(state, alternative, partialTicks);
			return;
		}

		boolean hasTileRender = false;
		if (tileEntity != null) {
			TileEntitySpecialRenderer<TileEntity> tileentityspecialrenderer = TileEntityRendererDispatcher.instance
					.<TileEntity>getRenderer(tileEntity);
			if (tileentityspecialrenderer != null) {
				tileentityspecialrenderer.render(tileEntity, -0.5, 0, -0.5, partialTicks, -1, 1);
				hasTileRender = true;
			}
		}

		EnumBlockRenderType renderType = state.getRenderType();
		if (renderType == EnumBlockRenderType.MODEL) {
			useBlockModel(world, pos, state);
			return;
		}
		if (hasTileRender) return;

		useItemRender(state, alternative, partialTicks);
		return;
	}

}
