package yuzunyannn.elementalsorcery.render.entity;

import java.util.Random;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.entity.EntityScapegoat;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.model.ModelScapegoat;

@SideOnly(Side.CLIENT)
public class RenderEntityScapegoat extends Render<EntityScapegoat> {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ESAPI.MODID,
			"textures/entity/scapegoat.png");

	public static final ModelScapegoat MODEL = new ModelScapegoat();

	public RenderEntityScapegoat(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityScapegoat entity) {
		return TEXTURE;
	}

	protected boolean canRenderName(EntityScapegoat entity) {
		return entity.hasCustomName() && entity == this.renderManager.pointedEntity;
	}

	@Override
	public void doRender(EntityScapegoat entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		GlStateManager.pushMatrix();

		GlStateManager.translate(x, y, z);

		if (entity.deathTime > 0) GlStateManager.rotate(-90 * ((entity.deathTime + partialTicks) / 20f), 1, 0, 0);

		GlStateManager.translate(0, 1.425, 0);
		GlStateManager.rotate(90 - entityYaw, 0, 1, 0);

		this.renderArrow(entity, partialTicks);

		if (entity.hurtTime > 0) GlStateManager.color(1, 0.75f, 0.75f);
		else GlStateManager.color(1, 1, 1);

		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		
		GlStateManager.scale(-0.06, -0.06, -0.06);

		bindTexture(TEXTURE);
		MODEL.render(entity, EventClient.getGlobalRotateInRender(partialTicks), 0, 0, 0, 0, 1);
		
		GlStateManager.enableLighting();
		
		GlStateManager.popMatrix();
	}

	public void renderArrow(EntityLivingBase entity, float partialTicks) {
		int i = entity.getArrowCountInEntity();
		if (i <= 0) return;
		i = Math.min(i, 16);

		Entity arrow = new EntityTippedArrow(entity.world, entity.posX, entity.posY, entity.posZ);
		Random random = new Random((long) entity.getEntityId());

		for (int j = 0; j < i; ++j) {
			GlStateManager.pushMatrix();
			ModelBox modelbox = MODEL.main;
			float f = random.nextFloat();
			float f1 = random.nextFloat();
			float f2 = random.nextFloat();
			float f3 = (modelbox.posX1 + (modelbox.posX2 - modelbox.posX1) * f) / 16.0F - 0.02f;
			float f4 = (modelbox.posY1 + (modelbox.posY2 - modelbox.posY1) * f1) / 16.0F - 0.20f;
			float f5 = (modelbox.posZ1 + (modelbox.posZ2 - modelbox.posZ1) * f2) / 16.0F - 0.15f;
			GlStateManager.translate(f3, f4, f5);
			f = f * 2.0F - 1.0F;
			f1 = f1 * 2.0F - 1.0F;
			f2 = f2 * 2.0F - 1.0F;
			f = f * -1.0F;
			f1 = f1 * -1.0F;
			f2 = f2 * -1.0F;
			float f6 = MathHelper.sqrt(f * f + f2 * f2);
			arrow.rotationYaw = (float) (Math.atan2((double) f, (double) f2) * (180D / Math.PI));
			arrow.rotationPitch = (float) (Math.atan2((double) f1, (double) f6) * (180D / Math.PI));
			arrow.prevRotationYaw = arrow.rotationYaw;
			arrow.prevRotationPitch = arrow.rotationPitch;
			getRenderManager().renderEntity(arrow, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, false);
			GlStateManager.popMatrix();
		}
	}

}
