package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.model.ModelBuildingAltar;
import yuzunyannn.elementalsorcery.tile.TileBuildingAltar;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

public class RenderTileBuildingAltar extends TileEntitySpecialRenderer<TileBuildingAltar> implements IRenderItem {

	private TextureBinder TEXTURE = new TextureBinder("textures/blocks/building_altar.png");
	private final ModelBuildingAltar MODEL = new ModelBuildingAltar();

	@Override
	public void render(TileBuildingAltar tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.enableLighting();
		GlStateManager.translate(x + 0.5, y, z + 0.5);
		GlStateManager.scale(0.0625, 0.0625, 0.0625);
		TEXTURE.bind();
		MODEL.render(null, EventClient.getGlobalRotateInRender(partialTicks), 0, 0, 0, 0, 1.0f);
		GlStateManager.popMatrix();

		if (tile.isWorking())
			return;
		ItemStack stack = tile.getStack();
		if (stack.isEmpty())
			return;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5, y - 0.25, z + 0.5);
		yuzunyannn.elementalsorcery.util.render.RenderHelper.layItemPositionFix(stack);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
		GlStateManager.popMatrix();
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.enableCull();
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
