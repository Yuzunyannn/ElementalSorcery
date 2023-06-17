package yuzunyannn.elementalsorcery.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.entity.EntityFloatCarpet;
import yuzunyannn.elementalsorcery.render.model.ModelFloatCarpet;

public class RenderEntityFloatCarpet extends Render<EntityFloatCarpet> {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ESAPI.MODID,
			"textures/entity/float_carpet.png");
	public static final ModelFloatCarpet MODEL = new ModelFloatCarpet();

	public RenderEntityFloatCarpet(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFloatCarpet entity) {
		return TEXTURE;
	}

	@Override
	public void doRender(EntityFloatCarpet entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();

		GlStateManager.translate((float) x, (float) y + 0.065f, (float) z);
		GlStateManager.disableCull();

		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}

		GlStateManager.scale(-0.0625 * 1.25, -0.0625 * 1.25, -0.0625 * 1.25);
		float yaw = RenderFriend.getPartialTicks(entity.rotationYaw, entity.prevRotationYaw, partialTicks);
		GlStateManager.rotate(-yaw, 0, 1, 0);
		this.bindTexture(TEXTURE);

		MODEL.render(entity, 0, 0, 0, 0, 0, 1);

		if (this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();
	}

}
