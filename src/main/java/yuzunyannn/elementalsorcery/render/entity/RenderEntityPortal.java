package yuzunyannn.elementalsorcery.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.entity.EntityPortal;

@SideOnly(Side.CLIENT)
public class RenderEntityPortal extends Render<EntityPortal> {

	public RenderEntityPortal(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityPortal entity) {
		return null;
	}

	@Override
	public void doRender(EntityPortal entity, double x, double y, double z, float entityYaw, float partialTicks) {
		if (!entity.isOpen()) return;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 1.8f, z);
		entity.getDraw().render(entity);
		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
	}
}
