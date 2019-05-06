package yuzunyan.elementalsorcery.util.render;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemStack;
import yuzunyan.elementalsorcery.item.ItemSpellbook;

public class RenderHelper {

	/** 根据相对坐标，修复物品平放在平台上 */
	static public void layItemPositionFix(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof ItemSpellbook) {
			GlStateManager.translate(0, 0.35, 0.2);
			GlStateManager.scale(0.8, 0.8, 0.8);
			GlStateManager.rotate(-90, 1, 0, 0);
		} else if (item instanceof ItemBed) {
			GlStateManager.translate(0, 0.44, 0.125);
		} else {
			Block block = Block.getBlockFromItem(stack.getItem());
			if (block == Blocks.AIR
					|| (!block.isFullCube(block.getDefaultState()) && !(block instanceof ITileEntityProvider))) {
				GlStateManager.translate(0, 0.4, -0.125);
				GlStateManager.rotate(90, 1, 0, 0);
			} else {
				if (TileEntityItemStackRenderer.instance != stack.getItem().getTileEntityItemStackRenderer()) {
					GlStateManager.translate(0, 0.475, 0);
				} else
					GlStateManager.translate(0, 0.3, 0);
			}
		}
	}

}
