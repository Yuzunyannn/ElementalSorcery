package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.model.ModelDisintegrateStela;
import yuzunyannn.elementalsorcery.tile.altar.TileDisintegrateStela;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class RenderTileDisintegrateStela extends TileEntitySpecialRenderer<TileDisintegrateStela>
		implements IRenderItem {

	static public final TextureBinder TEXTUREL = new TextureBinder("textures/blocks/disintegrate_stela.png");
	static public final TextureBinder TEXTUREL_OVERLOAD = new TextureBinder(
			"textures/blocks/disintegrate_stela_overload.png");
	static public final ModelDisintegrateStela MODEL = new ModelDisintegrateStela();

	public RenderTileDisintegrateStela() {
	}

	@Override
	public void render(TileDisintegrateStela tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		RenderHelper.bindDestoryTexture(TEXTUREL, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderHelper.startRender(x + 0.5, y, z + 0.5, -0.0625, alpha);
		GlStateManager.disableCull();

		float wakeRate = RenderHelper.getPartialTicks(tile.wakeRate, tile.prevWakeRate, partialTicks);
		float shakeRotate = 0;
		float overLoadRate = RenderHelper.getPartialTicks(tile.getOverloadRate(), tile.prevOverload, partialTicks);
		if (overLoadRate > 1) shakeRotate = MathHelper.clamp(overLoadRate, 1, 2) - 1;

		float roate = RenderHelper.getPartialTicks(tile.roate, tile.prevRoate, partialTicks);
		MODEL.render(null, shakeRotate, roate, EventClient.tickRender + partialTicks, wakeRate, 0, 1.0f);
		if (destroyStage < 0 && overLoadRate > 0.00001f) {
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.enableBlend();
			GlStateManager.disableAlpha();
			GlStateManager.color(1, 1, 1, Math.min(1, overLoadRate));
			TEXTUREL_OVERLOAD.bind();
			MODEL.render(null, shakeRotate, roate, EventClient.tickRender + partialTicks, wakeRate, 0, 1.0f);
			GlStateManager.enableAlpha();
			GlStateManager.disableBlend();
		}
		GlStateManager.enableCull();
		RenderHelper.endRender();
		RenderHelper.bindDestoryTextureEnd(destroyStage);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.disableCull();
		RenderHelper.render(stack, TEXTUREL, MODEL, true, -0.038, -0.0175, 0, 0);
		GlStateManager.enableCull();
	}

}
