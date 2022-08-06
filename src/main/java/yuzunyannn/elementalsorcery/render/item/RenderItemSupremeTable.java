package yuzunyannn.elementalsorcery.render.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.util.client.IRenderItem;
import yuzunyannn.elementalsorcery.render.tile.RenderTileSupremeTable;

public class RenderItemSupremeTable implements IRenderItem {

	@Override
	public void render(ItemStack stack, float partialTicks) {
		boolean leg = stack.getMetadata() == 0;
		GlStateManager.pushMatrix();
		if (IRenderItem.isGUI(stack)) {
			if (leg) {
				GlStateManager.scale(0.03, 0.03, 0.03);
				GlStateManager.translate(17, 8, 0);
			} else {
				GlStateManager.scale(0.02, 0.02, 0.02);
				GlStateManager.translate(25, 5, 0);
			}
			GlStateManager.rotate(45, 0, 1, 0);
			GlStateManager.rotate(30, 1, 0, 1);
		} else {
			if (leg) {
				GlStateManager.scale(0.02, 0.02, 0.02);
				GlStateManager.translate(25, 25,30);
			} else {
				GlStateManager.scale(0.0125, 0.0125, 0.0125);
				GlStateManager.translate(40, 25, 40);
			}
		}
		RenderTileSupremeTable.TEXTURE.bind();
		RenderTileSupremeTable.MODEL.renderItemPart(leg);
		GlStateManager.popMatrix();
	}

}
