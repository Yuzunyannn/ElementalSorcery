package yuzunyannn.elementalsorcery.util.render;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.item.ItemSpellbook;

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
			boolean canlay = block == Blocks.AIR;
			// canlay = canlay || (!block.isFullCube(block.getDefaultState()) &&
			// !(block instanceof ITileEntityProvider));
			canlay = canlay || block instanceof BlockPane;
			canlay = canlay || block instanceof BlockLadder;
			canlay = canlay || block instanceof net.minecraftforge.common.IPlantable;
			canlay = canlay || block instanceof net.minecraftforge.common.IShearable;
			if (canlay) {
				GlStateManager.translate(-0.125, 0.4, 0.0);
				GlStateManager.rotate(90, 0, 1, 0);
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
