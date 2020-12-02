package yuzunyannn.elementalsorcery.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
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
		GlStateManager.color(1, 1, 1);
		GlStateManager.translate((float) x, (float) y - 0.25 * scale + 0.5 * (1 - scale), (float) z);
		GlStateManager.scale(4 * scale, 4 * scale, 4 * scale);
		GlStateManager.rotate(entity.getRoate(), 0, 1, 0);
		Minecraft.getMinecraft().getRenderItem().renderItem(entity.getRenderItem(),
				ItemCameraTransforms.TransformType.GROUND);
		GlStateManager.popMatrix();
	}

}
