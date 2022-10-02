package yuzunyannn.elementalsorcery.render.entity.living;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.entity.mob.EntityPuppet;
import yuzunyannn.elementalsorcery.render.model.ModelPuppet;
import yuzunyannn.elementalsorcery.util.math.MathSupporter;

@SideOnly(Side.CLIENT)
public class RenderEntityPuppet extends RenderLiving<EntityPuppet> {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ESAPI.MODID, "textures/entity/puppet.png");
	public static final ModelPuppet MODEL = new ModelPuppet();

	public RenderEntityPuppet(RenderManager rendermanagerIn) {
		super(rendermanagerIn, MODEL, 0.5f);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityPuppet entity) {
		return TEXTURE;
	}

	@Override
	public void doRender(EntityPuppet entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected void applyRotations(EntityPuppet entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
		if (entityLiving.deathTime > 0) {

			float r = ((float) entityLiving.deathTime + partialTicks - 1.0F) / 20.0F;
			r = (float) (1 - MathSupporter.easeOutBack(1 - r * r));
			GlStateManager.scale(1, r * 3 + 1, 1);
			GlStateManager.translate(0, Math.max(0, r * 10), 0);

		} else super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
	}
}
