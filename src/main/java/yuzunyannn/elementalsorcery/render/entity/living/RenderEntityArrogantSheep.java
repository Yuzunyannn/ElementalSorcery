package yuzunyannn.elementalsorcery.render.entity.living;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.entity.mob.EntityArrogantSheep;
import yuzunyannn.elementalsorcery.render.model.living.ModelArrogantSheep;
import yuzunyannn.elementalsorcery.render.model.living.ModelArrogantSheepBig;

@SideOnly(Side.CLIENT)
public class RenderEntityArrogantSheep extends RenderLiving<EntityArrogantSheep> {

	public static final ResourceLocation TEXTURES_SHEEP = new ResourceLocation("textures/entity/sheep/sheep.png");
	public static final ResourceLocation TEXTURE_FUR = new ResourceLocation(ESAPI.MODID,
			"textures/entity/arrogant_sheep_fur.png");

	public RenderEntityArrogantSheep(RenderManager mgr) {
		super(mgr, new ModelArrogantSheep(), 0.7F);
		this.addLayer(new LayerSheepWool());
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityArrogantSheep entity) {
		return TEXTURES_SHEEP;
	}

	@SideOnly(Side.CLIENT)
	public class LayerSheepWool implements LayerRenderer<EntityArrogantSheep> {

		public final ModelArrogantSheepBig sheepModel = new ModelArrogantSheepBig();

		public void doRenderLayer(EntityArrogantSheep entitylivingbaseIn, float limbSwing, float limbSwingAmount,
				float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
			if (!entitylivingbaseIn.getSheared() && !entitylivingbaseIn.isInvisible()) {
				RenderEntityArrogantSheep.this.bindTexture(TEXTURE_FUR);
				this.sheepModel.setModelAttributes(RenderEntityArrogantSheep.this.getMainModel());
				this.sheepModel.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
				this.sheepModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw,
						headPitch, scale);
			}
		}

		public boolean shouldCombineTextures() {
			return true;
		}
	}
}
