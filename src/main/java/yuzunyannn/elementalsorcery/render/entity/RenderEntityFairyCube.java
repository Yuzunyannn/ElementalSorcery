package yuzunyannn.elementalsorcery.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.entity.fcube.EntityFairyCube;
import yuzunyannn.elementalsorcery.render.model.ModelFairyCube;

@SideOnly(Side.CLIENT)
public class RenderEntityFairyCube extends Render<EntityFairyCube> {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/entity/fairy_cube.png");
	public static final ModelFairyCube MODEL = new ModelFairyCube();

	public RenderEntityFairyCube(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFairyCube entity) {
		return TEXTURE.getResource();
	}

	@Override
	public void doRender(EntityFairyCube entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		GlStateManager.pushMatrix();

		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.translate(x, y, z);
		GlStateManager.scale(-0.0625, -0.0625, -0.0625);
		GlStateManager.translate(0, -1 / 0.0625, 0);
		GlStateManager.scale(0.5, 0.5, 0.5);
		GlStateManager.translate(0, 1 / 0.0625, 0);

		if (entity.deathTime > 0) {
			float time = entity.deathTime / 20f + partialTicks / 20f;
			time = time * time;
			float scale = -2.8125f * time * time + 1.8125f * time + 1;
			GlStateManager.scale(scale, scale, scale);
		}

		float yaw = -this.interpolateRotation(entity.prevRotationYaw, entity.rotationYaw, partialTicks);
		float pitch = this.interpolateRotation(entity.prevRotationPitch, entity.rotationPitch, partialTicks);
		float swing = RenderFriend.getPartialTicks(entity.swingProgress, entity.prevSwingProgress, partialTicks);

		TEXTURE.bind();
		MODEL.render(entity, swing, 0, entity.ticksExisted + partialTicks, yaw, pitch, 1);

		GlStateManager.enableLighting();
		GlStateManager.popMatrix();

	}

	protected float interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks) {
		float f;
		for (f = yawOffset - prevYawOffset; f < -180.0F; f += 360.0F);
		while (f >= 180.0F) f -= 360.0F;
		return prevYawOffset + partialTicks * f;
	}

}
