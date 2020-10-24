package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.model.ModelTranscribeInjection;
import yuzunyannn.elementalsorcery.tile.altar.TileTranscribeInjection;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class RenderTileTranscribeInjection extends TileEntitySpecialRenderer<TileTranscribeInjection>
		implements IRenderItem {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/blocks/transcribe_injection.png");
	public static final ModelTranscribeInjection MODEL = new ModelTranscribeInjection();

	@Override
	public void render(TileTranscribeInjection tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		RenderHelper.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderHelper.startRender(x + 0.5, y, z + 0.5, 0.03125, alpha);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		float rate = RenderHelper.getPartialTicks(tile.animeRate, tile.prevAnimeRate, partialTicks);
		float rotate = EventClient.getGlobalRotateInRender(partialTicks);
		MODEL.render(null, rate, rotate / 180 * 3.14f * 2, 0, 0, 0, 1.0f);
		GlStateManager.disableBlend();
		RenderHelper.endRender();
		RenderHelper.bindDestoryTextureEnd(destroyStage);
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		RenderHelper.render(stack, TEXTURE, MODEL, false, 0.0175, 0.0125, 0, 0);
	}
}
