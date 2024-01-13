package yuzunyannn.elementalsorcery.render.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.util.client.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.render.model.ModelMagicBlastWand;

public class RenderItemMagicBlastWand implements IRenderItem {

	public static final ModelMagicBlastWand MODEL = new ModelMagicBlastWand();
	public static final TextureBinder TEXTURE_B = new TextureBinder("textures/items/magic_blast_wand.png");
	public static final TextureBinder TEXTURE_C = new TextureBinder("textures/items/collapse_wand.png");
	public static final TextureBinder TEXTURE_D = new TextureBinder("textures/items/shock_wand.png");

	final TextureBinder texture;

	public RenderItemMagicBlastWand(int wandType) {
		if (wandType == 1) texture = TEXTURE_C;
		else if (wandType == 2) texture = TEXTURE_D;
		else texture = TEXTURE_B;
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
