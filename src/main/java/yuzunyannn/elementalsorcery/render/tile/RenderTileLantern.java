package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.model.ModelLantern;
import yuzunyannn.elementalsorcery.tile.TileLantern;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

public class RenderTileLantern extends TileEntitySpecialRenderer<TileLantern> implements IRenderItem {

	private TextureBinder TEXTURE = new TextureBinder("textures/blocks/lantern.png");
	private final ModelLantern MODEL = new ModelLantern();

	@Override
	public void render(TileLantern tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		GlStateManager.pushMatrix();
		// GlStateManager.disableLighting();
		GlStateManager.translate(x + 0.5, y +  0.0625, z + 0.5);
		TEXTURE.bind();
		GlStateManager.scale(0.0625, 0.0625, 0.0625);
		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		GlStateManager.popMatrix();
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.pushMatrix();
		if (IRenderItem.isGUI(stack)) {
			GlStateManager.translate(0.5, 0.25, 0.5);
			GlStateManager.scale(0.0375, 0.0375, 0.0375);
			GlStateManager.rotate(45, 0, 1, 0);
			GlStateManager.rotate(30, 1, 0, 1);
		} else {
			GlStateManager.translate(0.5, 0.4, 0.5);
			GlStateManager.scale(0.025, 0.025, 0.025);
		}
		TEXTURE.bind();
		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		GlStateManager.popMatrix();
	}

}
