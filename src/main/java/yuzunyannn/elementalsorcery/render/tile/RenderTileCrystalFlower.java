package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.tile.TileCrystalFlower;

@SideOnly(Side.CLIENT)
public class RenderTileCrystalFlower extends TileEntitySpecialRenderer<TileCrystalFlower> {

	@Override
	public void render(TileCrystalFlower tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
		if (!tile.needDraw()) return;
		ItemStack stack = tile.getCrystal();
		if (stack.isEmpty()) return;
		GlStateManager.pushMatrix();
		// GlStateManager.colorMask(true, false, true, false);
		GlStateManager.enableColorLogic();
		GlStateManager.colorLogicOp(GlStateManager.LogicOp.NAND);
		GlStateManager.translate(x + 0.5, y + 0.65, z + 0.5);
		GlStateManager.scale(0.75, 0.75, 0.75);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
		GlStateManager.rotate(90, 0, 1, 0);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
		GlStateManager.disableColorLogic();
		// GlStateManager.colorMask(true, true, true, true);
		GlStateManager.popMatrix();
	}

}
