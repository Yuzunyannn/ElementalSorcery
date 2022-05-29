package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.model.ModelElfBeacon;
import yuzunyannn.elementalsorcery.tile.TileElfBeacon;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class RenderTileElfBeacon extends TileEntitySpecialRenderer<TileElfBeacon> implements IRenderItem {

	public final static TextureBinder TEXTURE = new TextureBinder("textures/blocks/elf_beacon.png");
	public final static ModelElfBeacon MODEL = new ModelElfBeacon();

	@Override
	public void render(TileElfBeacon tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);

		RenderHelper.bindDestoryTexture(TEXTURE, destroyStage, rendererDispatcher, DESTROY_STAGES);
		RenderHelper.startRender(x + 0.5, y + 0.0625, z + 0.5, 0.03125, alpha);
		float rate = RenderHelper.getPartialTicks(tile.animeRate, tile.prevAnimeRate, partialTicks);
		float rotate = EventClient.getGlobalRotateInRender(partialTicks);
		MODEL.render(null, rate, rotate / 180 * 3.14f, 0, 0, 0, 1.0f);
		RenderHelper.endRender();
		RenderHelper.bindDestoryTextureEnd(destroyStage);

		ItemStack stack = tile.getStack();
		if (stack.isEmpty()) return;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.47, y + 0.14, z + 0.405);
		GlStateManager.rotate(90, 1, 0, 0);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
		GlStateManager.popMatrix();
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		RenderHelper.render(stack, TEXTURE, MODEL, true, 0.0175, 0.0125, 0, 0);
	}

}
