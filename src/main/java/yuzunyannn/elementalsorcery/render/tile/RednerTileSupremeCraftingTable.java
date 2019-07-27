package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.model.ModelSupremeCraftingTable;
import yuzunyannn.elementalsorcery.tile.altar.TileSupremeCraftingTable;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

public class RednerTileSupremeCraftingTable extends TileEntitySpecialRenderer<TileSupremeCraftingTable>
		implements IRenderItem {

	private TextureBinder TEXTURE = new TextureBinder("textures/blocks/supreme_crafting_table.png");
	private final ModelSupremeCraftingTable MODEL = new ModelSupremeCraftingTable();

	@Override
	public void render(TileSupremeCraftingTable tile, double x, double y, double z, float partialTicks,
			int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableLighting();
		GlStateManager.translate(x + 0.5, y, z + 0.5);
		GlStateManager.scale(0.03125, 0.03125, 0.03125);
		float roate = tile.prevRoate + (tile.roate - tile.prevRoate) * partialTicks;
		float legR = tile.prevLegR + (tile.legR - tile.prevLegR) * partialTicks;
		TEXTURE.bind();
		MODEL.render(null,legR, 45 + roate, roate, 0, 0, 1.0f);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.pushMatrix();
		if (IRenderItem.isGUI(stack)) {
			GlStateManager.translate(0.5, 0.225, 0.5);
			GlStateManager.scale(0.0175, 0.0175, 0.0175);
			GlStateManager.rotate(45, 0, 1, 0);
			GlStateManager.rotate(30, 1, 0, 1);
		} else {
			GlStateManager.translate(0.5, 0.4, 0.5);
			GlStateManager.scale(0.0125, 0.0125, 0.0125);
		}
		TEXTURE.bind();
		MODEL.render(null, 0f, 45f, 0, 0, 0, 1.0f);
		GlStateManager.popMatrix();
	}
}
