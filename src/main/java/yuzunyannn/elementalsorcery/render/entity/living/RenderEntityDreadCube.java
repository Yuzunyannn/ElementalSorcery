package yuzunyannn.elementalsorcery.render.entity.living;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.entity.mob.EntityDreadCube;
import yuzunyannn.elementalsorcery.render.model.living.ModelDreadCube;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class RenderEntityDreadCube extends RenderLiving<EntityDreadCube> {

	public static final ModelDreadCube MODEL = new ModelDreadCube();
	public static final ResourceLocation TEXTURE = new ResourceLocation(ElementalSorcery.MODID,
			"textures/entity/dread_cube.png");
	public static final ResourceLocation TEXTURE_COVER = new ResourceLocation(ElementalSorcery.MODID,
			"textures/entity/dread_cube_lines.png");

	public RenderEntityDreadCube(RenderManager rendermanagerIn) {
		super(rendermanagerIn, MODEL, 0.5f);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityDreadCube entity) {
		return null;
	}

	@Override
	protected void renderModel(EntityDreadCube entity, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch, float scaleFactor) {
		boolean flag = this.isVisible(entity);
		boolean flag1 = !flag && !entity.isInvisibleToPlayer(Minecraft.getMinecraft().player);

		if (flag || flag1) {
			if (flag1) GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);

			float activeRate = RenderHelper.getPartialTicks(entity.activeRate, entity.prevActiveRate,
					TextureBinder.mc.getRenderPartialTicks());

			this.bindTexture(TEXTURE);
			this.mainModel.render(entity, activeRate, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);

			this.bindTexture(TEXTURE_COVER);
			GlStateManager.color(activeRate, activeRate, activeRate);
			this.mainModel.render(entity, activeRate, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);

			if (flag1) GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);

		}
	}

}
