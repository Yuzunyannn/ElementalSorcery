package yuzunyan.elementalsorcery.render.tile;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import yuzunyan.elementalsorcery.item.ItemSpellbook;
import yuzunyan.elementalsorcery.render.IRenderItem;
import yuzunyan.elementalsorcery.render.model.ModelDeconstructAltarTable;
import yuzunyan.elementalsorcery.tile.TileDeconstructAltarTable;
import yuzunyan.elementalsorcery.util.render.TextureBinder;

public class RenderTileDeconstructAltarTable extends TileEntitySpecialRenderer<TileDeconstructAltarTable>
		implements IRenderItem {

	private TextureBinder TEXTURE = new TextureBinder("textures/blocks/deconstruct_altar_table.png");
	private final ModelDeconstructAltarTable MODEL = new ModelDeconstructAltarTable();

	@Override
	public void render(TileDeconstructAltarTable tile, double x, double y, double z, float partialTicks,
			int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		//GlStateManager.disableLighting();
		GlStateManager.enableCull();
		GlStateManager.translate(x + 0.5, y, z + 0.5);
		GlStateManager.scale(0.0625, 0.0625, 0.0625);
		TEXTURE.bind();
		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		GlStateManager.popMatrix();

		ItemStack stack = tile.getStack();
		if (stack.isEmpty())
			return;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
		yuzunyan.elementalsorcery.util.render.RenderHelper.layItemPositionFix(stack);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
		GlStateManager.popMatrix();
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.enableCull();
		if (IRenderItem.isGUI(stack)) {
			GlStateManager.translate(0.5, 0.2, 0.5);
			GlStateManager.scale(0.045, 0.045, 0.045);
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
