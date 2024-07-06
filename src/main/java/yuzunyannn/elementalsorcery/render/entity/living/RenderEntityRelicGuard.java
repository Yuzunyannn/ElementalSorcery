package yuzunyannn.elementalsorcery.render.entity.living;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.entity.mob.EntityRelicGuard;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.model.living.ModelRelicGuard;
import yuzunyannn.elementalsorcery.render.tile.RenderTileElementReactor;
import yuzunyannn.elementalsorcery.util.helper.Color;

@SideOnly(Side.CLIENT)
public class RenderEntityRelicGuard extends RenderLiving<EntityRelicGuard> {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ESAPI.MODID, "textures/entity/relic_guard.png");
	public static final ResourceLocation TEXTURE_MASK = new ResourceLocation(ESAPI.MODID,
			"textures/entity/relic_guard_mask.png");
	public static final ResourceLocation TEXTURE_CORE = new ResourceLocation(ESAPI.MODID,
			"textures/items/guard_core.png");
	public static final ResourceLocation TEXTURE_CORE_MASK = new ResourceLocation(ESAPI.MODID,
			"textures/items/guard_core_mask.png");

	public static final ModelRelicGuard MODEL = new ModelRelicGuard();

	public RenderEntityRelicGuard(RenderManager rendermanagerIn) {
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
				((ModelRelicGuard) this.livingEntityRenderer.getMainModel()).postRenderArm(0.0625F, side);
			}
		});
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityRelicGuard entity) {
		return TEXTURE;
	}

	@Override
	public void doRender(EntityRelicGuard entity, double x, double y, double z, float entityYaw, float partialTicks) {
		MODEL.activeRate = RenderFriend.getPartialTicks(entity.activeRate, entity.prevActiveRate, partialTicks);
		MODEL.activeTick = RenderFriend.getPartialTicks(entity.activeTick, entity.prevActiveTick, partialTicks);
		MODEL.isActive = entity.getStatus() == EntityRelicGuard.STATUS_ACTIVE;
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected void renderModel(EntityRelicGuard entity, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch, float scaleFactor) {
		super.renderModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
//		int destroyStage = 8;
//		if (destroyStage >= 0) {
//			GlStateManager.doPolygonOffset(-0.01F, -10.0F);
//			GlStateManager.enablePolygonOffset();
//			ResourceLocation[] DESTROY_STAGES = TileEntitySpecialRendererPacker.getDestroyStages();
//			this.bindTexture(DESTROY_STAGES[Math.min(destroyStage, DESTROY_STAGES.length - 1)]);
//			this.mainModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
//			GlStateManager.doPolygonOffset(0.0F, 0.0F);
//			GlStateManager.disablePolygonOffset();
//		}

		boolean flag = this.isVisible(entity);
		boolean flag1 = !flag && !entity.isInvisibleToPlayer(Minecraft.getMinecraft().player);
		if ((flag || flag1)) {
			boolean needDrawMask = MODEL.activeRate > 0.001f;
			Color color = entity.getColor();
			float alphaDrop = 1;
			if (entity.hurtTime > 0) alphaDrop = (float) (Math.random());

			if (entity.hasCore()) {
				GlStateManager.pushMatrix();
				float activeRate = MODEL.isActive ? 1 : MODEL.activeRate;
				float cos2 = MathHelper.cos(MODEL.activeTick * 0.04F);
				GlStateManager.translate(0, 0.98 - 0.65 * activeRate + 0.25 / 16 * cos2, 0);
				if (entity.getStatus() == EntityRelicGuard.STATUS_WORN) {
					float hp = entity.getHealth();
					if (hp < 10) {
						float r = hp / 10;
						GlStateManager.color(Math.min(1, r * 5), r, r);
					}
					GlStateManager.translate(Effect.rand.nextGaussian() * 0.01, 0, Effect.rand.nextGaussian() * 0.01);
				}
				GlStateManager.rotate(ageInTicks * 5, 0, 1, 0);
				GlStateManager.scale(-0.175, -0.175, -0.175);
				TextureBinder.bindTexture(TEXTURE_CORE);
				RenderTileElementReactor.MODEL_SPHERE.render();
				if (needDrawMask) {
					GlStateManager.doPolygonOffset(-0.01F, -10.0F);
					GlStateManager.enablePolygonOffset();
					RenderFriend.disableLightmap(true);
					GlStateManager.enableBlend();

					this.bindTexture(TEXTURE_CORE_MASK);
					GlStateManager.color(color.r, color.g, color.b, MODEL.activeRate * alphaDrop);
					RenderTileElementReactor.MODEL_SPHERE.render();

					GlStateManager.disableBlend();
					RenderFriend.disableLightmap(false);
					GlStateManager.doPolygonOffset(0.0F, 0.0F);
					GlStateManager.disablePolygonOffset();
				}
				GlStateManager.popMatrix();
			}

			if (needDrawMask) {
				GlStateManager.doPolygonOffset(-0.01F, -10.0F);
				GlStateManager.enablePolygonOffset();
				RenderFriend.disableLightmap(true);
				GlStateManager.enableBlend();

				this.bindTexture(TEXTURE_MASK);
				GlStateManager.color(color.r, color.g, color.b, MODEL.activeRate * alphaDrop);
				this.mainModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch,
						scaleFactor);

				GlStateManager.disableBlend();
				RenderFriend.disableLightmap(false);
				GlStateManager.doPolygonOffset(0.0F, 0.0F);
				GlStateManager.disablePolygonOffset();
			}
		}
	}

	@Override
	protected boolean setDoRenderBrightness(EntityRelicGuard entityLivingBaseIn, float partialTicks) {
		return false;
	}

	public static void drawCoreBall() {

	}

}
