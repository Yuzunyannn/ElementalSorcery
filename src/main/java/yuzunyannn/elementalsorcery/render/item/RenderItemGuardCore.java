package yuzunyannn.elementalsorcery.render.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.render.entity.living.RenderEntityRelicGuard;
import yuzunyannn.elementalsorcery.render.tile.RenderTileElementReactor;

@SideOnly(Side.CLIENT)
public class RenderItemGuardCore implements IRenderItem {

	public RenderItemGuardCore() {
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		TextureBinder.bindTexture(RenderEntityRelicGuard.TEXTURE_CORE);

		if (IRenderItem.isGUI(stack)) {
			GlStateManager.translate(0.5, 0.5, 0.5);
			GlStateManager.rotate(85, 0, 1, 0);
			GlStateManager.scale(0.3, -0.3, 0.3);
			RenderTileElementReactor.MODEL_SPHERE.render();
		} else {
			if (IRenderItem.isTransform(stack, TransformType.GROUND)) {
				GlStateManager.translate(0.5, 0.5, 0.5);
				GlStateManager.scale(0.15, 0.15, 0.15);
				RenderTileElementReactor.MODEL_SPHERE.render();
			} else if (IRenderItem.isTransform(stack, TransformType.FIXED)) {
				GlStateManager.translate(0.5, 0.5, 0.5);
				GlStateManager.rotate(270, 0, 1, 0);
				GlStateManager.scale(0.4, 0.4, 0.4);
				RenderTileElementReactor.MODEL_SPHERE.render();
			} else {
				GlStateManager.translate(0.5, 0.6, 0.5);
				GlStateManager.scale(0.2, 0.2, 0.2);
				RenderTileElementReactor.MODEL_SPHERE.render();
			}
		}

		GlStateManager.popMatrix();
		GlStateManager.enableCull();
	}

}
