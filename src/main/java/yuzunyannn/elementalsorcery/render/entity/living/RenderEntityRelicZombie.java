package yuzunyannn.elementalsorcery.render.entity.living;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.entity.mob.EntityRelicZombie;
import yuzunyannn.elementalsorcery.render.model.living.ModelRelicZombie;

@SideOnly(Side.CLIENT)
public class RenderEntityRelicZombie extends RenderLiving<EntityRelicZombie> {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ESAPI.MODID,
			"textures/entity/relic_zombie.png");
	public static final ModelRelicZombie MODEL = new ModelRelicZombie();

	public RenderEntityRelicZombie(RenderManager rendermanagerIn) {
		super(rendermanagerIn, MODEL, 0.5f);
		this.addLayer(new LayerHeldItem(this) {

			@Override
			public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount,
					float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(-0.04, 0, 0);
				super.doRenderLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks,
						netHeadYaw, headPitch, scale);
				GlStateManager.popMatrix();
			}

			@Override
			protected void translateToHand(EnumHandSide side) {
				((ModelRelicZombie) this.livingEntityRenderer.getMainModel()).postRenderArm(0.0625F, side);
			}
		});
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityRelicZombie entity) {
		return TEXTURE;
	}

	@Override
	public void doRender(EntityRelicZombie entity, double x, double y, double z, float entityYaw, float partialTicks) {
		MODEL.setHasCore(entity.hasCore);
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

}
