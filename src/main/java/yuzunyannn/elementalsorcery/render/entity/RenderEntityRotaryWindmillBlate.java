package yuzunyannn.elementalsorcery.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.item.IWindmillBlade;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.entity.EntityRotaryWindmillBlate;
import yuzunyannn.elementalsorcery.render.tile.RenderTileDeconstructWindmill;

@SideOnly(Side.CLIENT)
public class RenderEntityRotaryWindmillBlate extends Render<EntityRotaryWindmillBlate> {

	public RenderEntityRotaryWindmillBlate(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	public void doRender(EntityRotaryWindmillBlate entity, double x, double y, double z, float entityYaw,
			float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + entity.height / 2, z);
		GlStateManager.disableLighting();

		float rotate = RenderFriend.getPartialTicks(entity.bladeRotate, entity.prevBladeRotate, partialTicks);
		float scale = RenderFriend.getPartialTicks(entity.bladeScale, entity.prevBladeScale, partialTicks);

		scale = scale * 0.05f;

		ResourceLocation texture = null;
		boolean needBlend = false;

		IWindmillBlade windmillBlade = entity.getWindmillBlade();
		if (windmillBlade != null) {
			needBlend = needBlend || windmillBlade.isWindmillBladeSkinNeedBlend();
			texture = windmillBlade.getWindmillBladeSkin();
		}
		texture = texture == null ? RenderTileDeconstructWindmill.TEXTURE_BLADE_NORMAL : texture;

		if (needBlend) {
			GlStateManager.enableBlend();
			GlStateManager.disableAlpha();
		}

		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.rotate(-90, 0, 0, 90);

		this.bindTexture(texture);
		RenderTileDeconstructWindmill.MODEL_BLADE.render(null, rotate, 0, 0, 0, 0, 1.0f);

		if (needBlend) {
			GlStateManager.enableAlpha();
			GlStateManager.disableBlend();
		}

		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityRotaryWindmillBlate entity) {
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}
}
