package yuzunyannn.elementalsorcery.render.tile.md;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.render.item.RenderItemGlassCup;
import yuzunyannn.elementalsorcery.render.model.md.ModelMDLiquidizer;
import yuzunyannn.elementalsorcery.tile.md.TileMDLiquidizer;

public class RenderTileMDLiquidizer extends RenderTileMDBase<TileMDLiquidizer> {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/md_liquidizer.png");
	public static final ModelMDLiquidizer MODEL = new ModelMDLiquidizer();

	@Override
	public void render(TileMDLiquidizer tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
		RenderFriend.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y, z + 0.5, 0.0625, alpha);
		float roate = RenderFriend.getPartialTicks(tile.rotate, tile.prevRotate, partialTicks);
		MODEL.render(null, roate, 0, 0, 0, 0, 1.0f);
		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);

		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);

		float r = tile.getJuiceRate(partialTicks);
		List<TileMDLiquidizer.RotateData> list = tile.getIngredients();

		if (!list.isEmpty()) {
			GlStateManager.pushMatrix();

			double dr = Math.max(r - 0.4, -0.125);
			GlStateManager.translate(0, Math.min(dr, 0.2), 0);
			GlStateManager.scale(0.3, 0.3, 0.3);
			GlStateManager.rotate(-roate * 180 / 3.1415926f, 0, 1, 0);
			float gr = EventClient.getGlobalRotateInRender(partialTicks);
			float drc = Math.min((float) dr + 0.125f, 0.2f) * 5 * 0.3f;
			for (TileMDLiquidizer.RotateData dat : list) {
				if (!dat.material.isMain) continue;
				float scale = Math.max(dat.remain, 0.2f);
				float high = MathHelper.sin((dat.hashCode() % 360 + gr) / 180 * 3.1415f) * drc;
				GlStateManager.translate(1, high, 0);
				GlStateManager.scale(scale, scale, scale);
				Minecraft.getMinecraft().getRenderItem().renderItem(dat.material.item,
						ItemCameraTransforms.TransformType.FIXED);
				GlStateManager.scale(1 / scale, 1 / scale, 1 / scale);
				GlStateManager.translate(-1, -high, 0);
				GlStateManager.rotate(360f / list.size(), 0, 1, 0);
			}

			GlStateManager.popMatrix();
		}
		if (r > 0.0001) {
			GlStateManager.translate(0, -0.325 - 0.5, 0);
			RenderItemGlassCup.TEXTURE_FLUID.bind();
			Vec3d color = tile.getJuiceColor();
			GlStateManager.color((float) color.x, (float) color.y, (float) color.z);
			GlStateManager.scale(0.085, 0.085, 0.085);
			GlStateManager.depthMask(false);
			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.disableAlpha();
			RenderItemGlassCup.drawJuiceInCup(r * 0.91f, EventClient.tickRender / 2);
			GlStateManager.enableAlpha();
			GlStateManager.disableBlend();
			GlStateManager.enableCull();
			GlStateManager.enableLighting();
			GlStateManager.depthMask(true);
		}
		GlStateManager.popMatrix();
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		super.render(stack, partialTicks);
		RenderFriend.renderSpecialItem(stack, TEXTURE, MODEL, false);
	}
}
