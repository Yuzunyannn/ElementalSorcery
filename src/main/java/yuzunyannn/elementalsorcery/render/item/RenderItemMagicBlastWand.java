package yuzunyannn.elementalsorcery.render.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.model.ModelMagicBlastWand;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

public class RenderItemMagicBlastWand implements IRenderItem {

	public static final ModelMagicBlastWand MODEL = new ModelMagicBlastWand();
	public static final TextureBinder TEXTURE_B = new TextureBinder("textures/items/magic_blast_wand.png");
	public static final TextureBinder TEXTURE_C = new TextureBinder("textures/items/collapse_wand.png");

	final TextureBinder texture;

	public RenderItemMagicBlastWand(boolean iscollapse) {
		texture = iscollapse ? TEXTURE_C : TEXTURE_B;
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		texture.bind();

		if (IRenderItem.isGUI(stack)) {

			GlStateManager.translate(0.3, 0.2, 0.6);
			GlStateManager.scale(0.04, -0.04, 0.04);
			GlStateManager.rotate(40, 0, 0, 1);
			GlStateManager.rotate(-20, 1, 0, 0);
			MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);

		} else {
			if (IRenderItem.isTransform(stack, TransformType.GROUND)) {
				GlStateManager.translate(0.5, 0.3, 0.5);
				GlStateManager.scale(0.015, -0.015, 0.015);
				MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
			} else if (IRenderItem.isTransform(stack, TransformType.FIXED)) {
				GlStateManager.translate(0.5, 0.1, 0.5);
				GlStateManager.scale(0.04, -0.04, 0.04);
				MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
			} else {
				float r = EventClient.getGlobalRotateInRender(partialTicks) / 180 * 3.1415926f * 1.5f;
				GlStateManager.translate(0.5, 0.05, 0.6);
				GlStateManager.scale(0.05, -0.05, 0.05);
				MODEL.render(null, r, 0, 0, 0, 0, 1.0f);
			}
		}

		GlStateManager.popMatrix();
		GlStateManager.enableCull();
	}

}
