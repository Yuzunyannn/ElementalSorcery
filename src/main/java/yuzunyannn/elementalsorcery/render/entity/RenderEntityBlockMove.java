package yuzunyannn.elementalsorcery.render.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.entity.EntityBlockMove;

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
		GlStateManager.translate(x, y + 0.5, z);
		Block block = entity.getBlockState().getBlock();
		if (block instanceof IPlantable || block instanceof BlockTorch) scale = scale * 1;
		else scale = scale * 2;
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.rotate(entity.getRoate(), 0, 1, 0);
		Minecraft.getMinecraft().getRenderItem().renderItem(entity.getRenderItem(),
				ItemCameraTransforms.TransformType.FIXED);
		GlStateManager.popMatrix();
	}

}
