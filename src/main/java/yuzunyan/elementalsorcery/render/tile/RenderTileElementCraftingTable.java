package yuzunyan.elementalsorcery.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import yuzunyan.elementalsorcery.render.IRenderItem;
import yuzunyan.elementalsorcery.render.model.ModelElementCraftingTable;
import yuzunyan.elementalsorcery.tile.TileElementCraftingTable;
import yuzunyan.elementalsorcery.util.render.TextureBinder;

public class RenderTileElementCraftingTable extends TileEntitySpecialRenderer<TileElementCraftingTable>
		implements IRenderItem {

	private final ModelElementCraftingTable MODEL = new ModelElementCraftingTable();
	public final TextureBinder TEXTURE = new TextureBinder("textures/blocks/element_crafting_table.png");

	@Override
	public void render(TileElementCraftingTable tile, double x, double y, double z, float partialTicks,
			int destroyStage, float alpha) {

		GlStateManager.pushMatrix();
		//GlStateManager.disableLighting();
		GlStateManager.enableCull();
		GlStateManager.translate(x + 0.5, y, z + 0.5);
		TEXTURE.bind();
		GlStateManager.scale(0.1, 0.1, 0.1);
		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		GlStateManager.popMatrix();

	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.enableCull();
		if (IRenderItem.isGUI(stack)) {
			GlStateManager.translate(0.5, 0.25, 0.5);
			GlStateManager.scale(0.05, 0.05, 0.05);
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
