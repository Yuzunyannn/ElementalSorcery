package yuzunyan.elementalsorcery.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import yuzunyan.elementalsorcery.event.EventClient;
import yuzunyan.elementalsorcery.tile.TileMagicPlatform;

public class RenderTileMagicPlatform extends TileEntitySpecialRenderer<TileMagicPlatform> {

	@Override
	public void render(TileMagicPlatform tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		ItemStack stack = tile.getStack();
		if (stack.isEmpty())
			return;
		GlStateManager.pushMatrix();
		float ang = tile.roate_begin + EventClient.global_rotate + EventClient.DGLOBAL_ROTATE * partialTicks;
		GlStateManager.translate((float) x + 0.5F,
				(float) y + 7.0F / 16.0F + (MathHelper.sin(ang * 0.01745329f) + 1) * 0.125, (float) z + 0.5F);
		GlStateManager.rotate(ang, 0, 1, 0);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
		GlStateManager.popMatrix();
	}

}
