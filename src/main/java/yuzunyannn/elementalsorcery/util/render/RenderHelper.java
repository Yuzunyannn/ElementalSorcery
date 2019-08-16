package yuzunyannn.elementalsorcery.util.render;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockPane;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.item.ItemSpellbook;
import yuzunyannn.elementalsorcery.render.IRenderItem;

public class RenderHelper {

	/** 渲染物品IItemRender */
	static public void render(ItemStack stack, TextureBinder TEXTURE, ModelBase MODEL, boolean needLighting) {
		RenderHelper.render(stack, TEXTURE, MODEL, needLighting, 0.038, 0.0175, 0, 0);
	}

	/** 渲染物品IItemRender */
	static public void render(ItemStack stack, TextureBinder TEXTURE, ModelBase MODEL, boolean needLighting,
			double scale, double scaleGround, double yoff, double yoffGround) {
		GlStateManager.pushMatrix();
		TEXTURE.bind();
		if (IRenderItem.isGUI(stack)) {
			if (needLighting) {
				GlStateManager.pushAttrib();
				net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
			}
			GlStateManager.rotate(45, 0, 1, 0);
			GlStateManager.rotate(30, 1, 0, 1);
			GlStateManager.translate(0.35, 0.26 + yoff, 0.35);
			GlStateManager.scale(scale, scale, scale);
			MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
			if (needLighting)
				GlStateManager.popAttrib();
		} else {
			GlStateManager.translate(0.5, 0.4 + yoffGround, 0.5);
			GlStateManager.scale(scaleGround, scaleGround, scaleGround);
			MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		}
		GlStateManager.popMatrix();
	}

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

	static public boolean bindDestoryTexture(int destroyStage, TileEntityRendererDispatcher rendererDispatcher,
			ResourceLocation[] DESTROY_STAGES) {
		if (destroyStage >= 0) {
			TextureManager texturemanager = rendererDispatcher.renderEngine;
			if (texturemanager != null)
				texturemanager.bindTexture(DESTROY_STAGES[destroyStage]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scale(4.0F, 4.0F, 1.0F);
			GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
			return false;
		}
		return true;
	}

	static public void bindDestoryTextureEnd(int destroyStage) {
		if (destroyStage >= 0) {
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
	}

}
