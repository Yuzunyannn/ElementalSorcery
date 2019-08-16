package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.tile.TileMagicPlatform;

@SideOnly(Side.CLIENT)
public class RenderTileMagicPlatform extends TileEntitySpecialRenderer<TileMagicPlatform> {

	@Override
	public void render(TileMagicPlatform tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		ItemStack stack = tile.getStack();
		if (stack.isEmpty())
			return;
		GlStateManager.pushMatrix();
		float ang = tile.roate_begin + EventClient.getGlobalRotateInRender(partialTicks) * 1.75f;
		GlStateManager.translate((float) x + 0.5F,
				(float) y + 7.0F / 16.0F + (MathHelper.sin(ang * 0.01745329f) + 1) * 0.125, (float) z + 0.5F);
		GlStateManager.rotate(ang, 0, 1, 0);
		GlStateManager.disableLighting();
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
		GlStateManager.popMatrix();
	}

}
