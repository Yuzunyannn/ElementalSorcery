package yuzunyannn.elementalsorcery.render.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.render.tile.RenderTileStoneMill;

@SideOnly(Side.CLIENT)
public class RenderItemMillHammer implements IRenderItem {

	public final static TextureBinder TEXTURE = new TextureBinder("textures/items/memory_fragment.png");

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.pushMatrix();
		RenderTileStoneMill.TEXTURE.bind();

		if (IRenderItem.isGUI(stack)) {
			GlStateManager.translate(0.9, 0.125, 0.5);
			GlStateManager.scale(0.045, 0.045, 0.045);
			GlStateManager.rotate(60, 0, 0, 1);
			GlStateManager.rotate(20, 1, 0, 0);
			renderModel(stack);
		} else {
			if (IRenderItem.isTransform(stack, TransformType.GROUND)) {
				GlStateManager.translate(0.4, 0, 0.4);
				GlStateManager.scale(0.025, 0.025, 0.025);
				renderModel(stack);
			} else if (IRenderItem.isTransform(stack, TransformType.FIXED)) {
				GlStateManager.translate(0.35, -0.6, 0.2);
//				GlStateManager.rotate(-90, 1, 0, 0);
				GlStateManager.scale(0.075, 0.075, 0.075);
				renderModel(stack);
			} else if (IRenderItem.isTransform(stack, TransformType.FIRST_PERSON_LEFT_HAND)) {
				GlStateManager.translate(0.9, 0.75, 1.5);
				GlStateManager.scale(0.05, 0.05, 0.05);
				GlStateManager.rotate(-90, 0, 1, 0);
				GlStateManager.rotate(100, 0, 0, 1);
				renderModel(stack);
			} else if (IRenderItem.isTransform(stack, TransformType.FIRST_PERSON_RIGHT_HAND)) {
				GlStateManager.translate(0.5, 0.75, 1.5);
				GlStateManager.scale(0.05, 0.05, 0.05);
				GlStateManager.rotate(-90, 0, 1, 0);
				GlStateManager.rotate(100, 0, 0, 1);
				renderModel(stack);
			} else {
				GlStateManager.translate(0.8, 0.45, 1.75);
				GlStateManager.scale(0.075, 0.075, 0.075);
				GlStateManager.rotate(-90, 0, 1, 0);
				GlStateManager.rotate(80, 0, 0, 1);
				renderModel(stack);
			}
		}

		GlStateManager.popMatrix();
	}

	private void renderModel(ItemStack stack) {
		RenderTileStoneMill.MODEL.renderHammer(0, 0, 0, 1);
	}

}
