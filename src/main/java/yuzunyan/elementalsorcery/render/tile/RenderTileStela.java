package yuzunyan.elementalsorcery.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import yuzunyan.elementalsorcery.render.IRenderItem;
import yuzunyan.elementalsorcery.render.model.ModelStela;
import yuzunyan.elementalsorcery.tile.TileStela;
import yuzunyan.elementalsorcery.util.render.TextureBinder;

public class RenderTileStela extends TileEntitySpecialRenderer<TileStela> implements IRenderItem {

	private TextureBinder TEXTURE = new TextureBinder("textures/blocks/stela.png");
	private final ModelStela MODEL = new ModelStela();

	@Override
	public void render(TileStela tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		GlStateManager.pushMatrix();
		// GlStateManager.enableCull();
		GlStateManager.translate(x + 0.5, y, z + 0.5);
		GlStateManager.scale(0.0625, 0.0625, 0.0625);
		EnumFacing face = tile.getFace();
		switch (face) {
		case NORTH:
			GlStateManager.rotate(-90, 0, 1, 0);
			break;
		case EAST:
			GlStateManager.rotate(180, 0, 1, 0);
			break;
		case WEST:
			break;
		case SOUTH:
			GlStateManager.rotate(90, 0, 1, 0);
			break;
		default:
			break;
		}
		TEXTURE.bind();
		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		GlStateManager.popMatrix();
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.enableCull();
		if (IRenderItem.isGUI(stack)) {
			GlStateManager.translate(0.525, 0.225, 0.5);
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
