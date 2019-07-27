package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.model.ModelAnalysisAltar;
import yuzunyannn.elementalsorcery.tile.altar.TileAnalysisAltar;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

public class RenderTileAnalysisAltar extends TileEntitySpecialRenderer<TileAnalysisAltar> implements IRenderItem {

	private TextureBinder TEXTURE = new TextureBinder("textures/blocks/analysis_altar.png");
	private final ModelAnalysisAltar MODEL = new ModelAnalysisAltar();

	@Override
	public void render(TileAnalysisAltar tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5, y, z + 0.5);
		GlStateManager.scale(0.0625, 0.0625, 0.0625);
		TEXTURE.bind();
		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		GlStateManager.popMatrix();
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.pushMatrix();
		if (IRenderItem.isGUI(stack)) {
			GlStateManager.translate(0.5, 0.225, 0.5);
			GlStateManager.scale(0.035, 0.035, 0.035);
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
