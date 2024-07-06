package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.render.model.ModelDisintegrateStela;
import yuzunyannn.elementalsorcery.tile.altar.TileDisintegrateStela;

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
		RenderFriend.bindDestoryTexture(TEXTUREL, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderFriend.startTileEntitySpecialRender(x + 0.5, y, z + 0.5, -0.0625, alpha);
		GlStateManager.disableCull();

		float wakeRate = RenderFriend.getPartialTicks(tile.wakeRate, tile.prevWakeRate, partialTicks);
		float shakeRotate = 0;
		float overLoadRate = RenderFriend.getPartialTicks(tile.getOverloadRate(), tile.prevOverload, partialTicks);
		if (overLoadRate > 1) shakeRotate = MathHelper.clamp(overLoadRate, 1, 2) - 1;

		float roate = RenderFriend.getPartialTicks(tile.roate, tile.prevRoate, partialTicks);
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
		RenderFriend.endTileEntitySpecialRender();
		RenderFriend.bindDestoryTextureEnd(destroyStage);

		if (overLoadRate > 0.001 && this.rendererDispatcher.cameraHitResult != null
				&& tile.getPos().equals(this.rendererDispatcher.cameraHitResult.getBlockPos())) {
			this.setLightmapDisabled(true);
			String str = overLoadRate >= 2 ? "----!----" : String.format("%.3f", overLoadRate);
			this.drawNameplate(tile, str, x, y, z, 12);
			this.setLightmapDisabled(false);
		}
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.disableCull();
		RenderFriend.renderSpecialItem(stack, TEXTUREL, MODEL, true, -0.038, -0.0175, 0, 0);
		GlStateManager.enableCull();
	}

}
