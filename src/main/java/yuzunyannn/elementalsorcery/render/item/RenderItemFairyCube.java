package yuzunyannn.elementalsorcery.render.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityFairyCube;
import yuzunyannn.elementalsorcery.render.model.ModelFairyCube;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

public class RenderItemFairyCube implements IRenderItem {

	public static final ModelFairyCube MODEL = RenderEntityFairyCube.MODEL;
	public static final TextureBinder TEXTURE = RenderEntityFairyCube.TEXTURE;

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		TEXTURE.bind();

		if (IRenderItem.isGUI(stack)) {

			GlStateManager.translate(0.5, 0.5, 0.6);
			GlStateManager.scale(0.0175, 0.0175, 0.0175);
			GlStateManager.rotate(-30, 0, 1, 0);
			GlStateManager.rotate(30, 1, 0, 0);
			MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);

		} else {
			if (IRenderItem.isTransform(stack, TransformType.GROUND)) {
				GlStateManager.translate(0.5, 0.5, 0.5);
				GlStateManager.scale(0.008, 0.008, 0.008);
				MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
			} else if (IRenderItem.isTransform(stack, TransformType.FIXED)) {
				GlStateManager.translate(0.5, 0.5, 0.25);
				GlStateManager.scale(0.02, 0.02, 0.02);
				MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
			} else {
				GlStateManager.translate(0.5, 0.65, 0.6);
				GlStateManager.scale(0.01, 0.01, 0.01);
				MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
			}
		}

		GlStateManager.popMatrix();
		GlStateManager.enableCull();
	}

}
