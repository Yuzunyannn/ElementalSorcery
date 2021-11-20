package yuzunyannn.elementalsorcery.render.entity.living;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelSkeleton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.entity.mob.EntityDejectedSkeleton;
import yuzunyannn.elementalsorcery.entity.mob.EntityDejectedSkeleton.State;

@SideOnly(Side.CLIENT)
public class RenderEntityDejectedSkeleton extends RenderBiped<EntityDejectedSkeleton> {

	public static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/skeleton/skeleton.png");
	public static final ModelSkeleton MODEL = new ModelSkeleton();

	public RenderEntityDejectedSkeleton(RenderManager rendermanagerIn) {
		super(rendermanagerIn, MODEL, 0.5f);
		this.addLayer(new LayerHeldItem(this));
		this.addLayer(new LayerBipedArmor(this) {
			protected void initArmor() {
				this.modelLeggings = new ModelSkeleton(0.5F, true);
				this.modelArmor = new ModelSkeleton(1.0F, true);
			}
		});
	}

	@Override
	public void transformHeldFull3DItemLayer() {
		GlStateManager.translate(0.09375F, 0.1875F, 0.0F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityDejectedSkeleton entity) {
		return TEXTURE;
	}

	@Override
	protected void renderModel(EntityDejectedSkeleton skeleton, float limbSwing, float limbSwingAmount,
			float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		boolean isVisible = this.isVisible(skeleton);
		boolean playerCannotSee = !isVisible && !skeleton.isInvisibleToPlayer(Minecraft.getMinecraft().player);
		if (isVisible || playerCannotSee) {
			if (!this.bindEntityTexture(skeleton)) return;
			if (playerCannotSee) GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);

			this.mainModel.render(skeleton, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);

			EntityDejectedSkeleton.State state = skeleton.getState();
			if (state != State.NONE) {
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
				float sin = MathHelper.sin(ageInTicks * 0.1f);
				float scale = 1.15f + sin * 0.05f;
				GlStateManager.scale(scale, scale, scale);
				Vec3d color = state.getColor();
				GlStateManager.color((float) color.x, (float) color.y, (float) color.z, (sin + 1) / 2 * 0.7f + 0.3f);
				this.mainModel.render(skeleton, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch,
						scaleFactor);
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
						GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.disableBlend();
			}

			if (playerCannotSee) GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);

		}
	}

}
