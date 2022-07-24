package yuzunyannn.elementalsorcery.util.render;

import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockTorch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.block.BlockElfFruit;
import yuzunyannn.elementalsorcery.block.BlockLifeFlower;
import yuzunyannn.elementalsorcery.item.book.ItemGrimoire;
import yuzunyannn.elementalsorcery.item.book.ItemSpellbook;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.effect.Effect;

public class RenderHelper {

	static public double getPartialTicks(double n, double prevN, float partialTicks) {
		return prevN + (n - prevN) * partialTicks;
	}

	static public float getPartialTicks(float n, float prevN, float partialTicks) {
		return prevN + (n - prevN) * partialTicks;
	}

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
			if (needLighting) GlStateManager.enableLighting();
			GlStateManager.rotate(45, 0, 1, 0);
			GlStateManager.rotate(30, 1, 0, 1);
			GlStateManager.translate(0.35, 0.26 + yoff, 0.35);
			GlStateManager.scale(scale, scale, scale);
			MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
			if (needLighting) GlStateManager.disableLighting();
		} else if (IRenderItem.isTransform(stack, ItemCameraTransforms.TransformType.FIXED)) {
			GlStateManager.translate(0.5, 0.25, 0.5);
			GlStateManager.scale(scaleGround * 2, scaleGround * 2, scaleGround * 2);
			MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		} else if (IRenderItem.isTransform(stack, ItemCameraTransforms.TransformType.GROUND)) {
			GlStateManager.translate(0.5, 0.4 + yoffGround, 0.5);
			GlStateManager.scale(scaleGround, scaleGround, scaleGround);
			MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		} else {
			GlStateManager.translate(0.5, 0.5 + yoffGround, 0.5);
			GlStateManager.rotate(60, 1, 0, 0);
			GlStateManager.scale(scaleGround, scaleGround, scaleGround);
			MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);
		}
		GlStateManager.popMatrix();
	}

	static public void startRender(double x, double y, double z, double scale, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
		GlStateManager.translate(x, y, z);
		GlStateManager.scale(scale, scale, scale);
	}

	static public void endRender() {
		GlStateManager.popMatrix();
	}

	static public void bindDestoryTexture(TextureBinder TEXTURE, int destroyStage,
			TileEntityRendererDispatcher rendererDispatcher, ResourceLocation[] DESTROY_STAGES) {
		if (RenderHelper.bindDestoryTexture(destroyStage, rendererDispatcher, DESTROY_STAGES)) TEXTURE.bind();
	}

	static public boolean bindDestoryTexture(int destroyStage, TileEntityRendererDispatcher rendererDispatcher,
			ResourceLocation[] DESTROY_STAGES) {
		if (destroyStage >= 0) {
			TextureManager texturemanager = rendererDispatcher.renderEngine;
			if (texturemanager != null) texturemanager.bindTexture(DESTROY_STAGES[destroyStage]);
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

	/** 根据相对坐标，修复物品平放在平台上 */
	static public void layItemPositionFix(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof ItemBed) {
			GlStateManager.translate(0, 0.44, 0);
			GlStateManager.rotate(90, 1, 0, 0);
			GlStateManager.scale(0.5, 0.5, 0.5);
		} else if (item instanceof ItemSpellbook) {
			GlStateManager.translate(0, 0.35, 0.2);
			GlStateManager.scale(0.8, 0.8, 0.8);
			GlStateManager.rotate(-90, 1, 0, 0);
		} else if (item instanceof ItemGrimoire) {
			GlStateManager.translate(-0.15, 0.45, 0);
			GlStateManager.rotate(-90, 1, 0, 0);
			GlStateManager.scale(0.75, 0.75, 0.75);
		} else {
			Block block = Block.getBlockFromItem(item);
			boolean canlay = false;
			if (block == Blocks.AIR) {
				canlay = true;
				canlay = canlay && (item != Items.SKULL);
			} else if (!block.getDefaultState().isFullBlock()) {
				canlay = canlay || block instanceof BlockPane;
				canlay = canlay || block instanceof BlockLadder;
				canlay = canlay || block instanceof net.minecraftforge.common.IPlantable;
				canlay = canlay || block instanceof net.minecraftforge.common.IShearable;
				canlay = canlay || block instanceof BlockTorch;
				canlay = canlay || block instanceof BlockLifeFlower;
				canlay = canlay || block instanceof BlockHopper;
				canlay = canlay || block instanceof BlockRailBase;
				canlay = canlay || block instanceof BlockElfFruit;
			}
			if (canlay) {
				GlStateManager.translate(0, 0.4, 0.0);
				GlStateManager.rotate(90, 1, 0, 0);
				GlStateManager.scale(0.5, 0.5, 0.5);
			} else {
				GlStateManager.translate(0, 0.5, 0);
				GlStateManager.scale(0.5, 0.5, 0.5);
			}
		}
	}

	static public void drawTexturedModalRect(float x, float y, float u, float v, float width, float height,
			float textureWidth, float textureHeight) {
		float f = 1.0F / textureWidth;
		float f1 = 1.0F / textureHeight;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double) x, (double) (y + height), 0.0D)
				.tex((double) (u * f), (double) ((v + (float) height) * f1)).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + height), 0.0D)
				.tex((double) ((u + (float) width) * f), (double) ((v + (float) height) * f1)).endVertex();
		bufferbuilder.pos((double) (x + width), (double) y, 0.0D)
				.tex((double) ((u + (float) width) * f), (double) (v * f1)).endVertex();
		bufferbuilder.pos((double) x, (double) y, 0.0D).tex((double) (u * f), (double) (v * f1)).endVertex();
		tessellator.draw();
	}

	public static void drawTexturedRectInCenter(float x, float y, float width, float height, float u, float v,
			float texWidth, float texHeight, float textureWidth, float textureHeight) {
		float f = 1.0F / textureWidth;
		float f1 = 1.0F / textureHeight;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		float hw = width / 2;
		float hh = height / 2;
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x - hw, y + hh, 0.0D).tex(u * f, (v + texHeight) * f1).endVertex();
		bufferbuilder.pos(x + hw, y + hh, 0.0D).tex((u + texWidth) * f, (v + texHeight) * f1).endVertex();
		bufferbuilder.pos(x + hw, y - hh, 0.0D).tex((u + texWidth) * f, v * f1).endVertex();
		bufferbuilder.pos(x - hw, y - hh, 0.0D).tex(u * f, v * f1).endVertex();
		tessellator.draw();
	}

	public static void drawTexturedRectInCenter(float x, float y, float width, float height, float u, float v,
			float texWidth, float texHeight, float textureWidth, float textureHeight, float r, float g, float b,
			float a, float anchorX, float anchorY) {
		float f = 1.0F / textureWidth;
		float f1 = 1.0F / textureHeight;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		float hw = width * anchorX;
		float hh = height * anchorY;
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos(x - hw, y + hh, 0.0D).tex(u * f, (v + texHeight) * f1);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(x + (width - hw), y + hh, 0.0D).tex((u + texWidth) * f, (v + texHeight) * f1);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(x + (width - hw), y - (height - hh), 0.0D).tex((u + texWidth) * f, v * f1);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(x - hw, y - (height - hh), 0.0D).tex(u * f, v * f1);
		bufferbuilder.color(r, g, b, a).endVertex();
		tessellator.draw();
	}

	public static void drawTexturedRectInCenter(float x, float y, float width, float height) {
		drawTexturedRectInCenter(x, y, width, height, 0, 0, 1, 1, 1, 1);
	}

	public static void disableLightmap(boolean disabled) {
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		if (disabled) GlStateManager.disableTexture2D();
		else GlStateManager.enableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	public static final Minecraft mc = Minecraft.getMinecraft();

	public static int getRenderDistanceChunks() {
		return mc.gameSettings.renderDistanceChunks;
	}

	static private Framebuffer frameBuff128 = null;

	static public Framebuffer getFrameBuff128() {
		if (frameBuff128 == null) frameBuff128 = new Framebuffer(128, 128, false);
		return frameBuff128;
	}

	public static void bindOffscreenTexture128() {
		getFrameBuff128().bindTexture();
	}

	public static void renderOffscreenTexture128(Consumer<Void> render) {
		renderOffscreenTexture128(render, getFrameBuff128());
	}

	public static void renderOffscreenTexture128(Consumer<Void> render, Framebuffer buffer) {
		buffer.bindFrame(false);

		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();

		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0D, 128, 128, 0.0D, 0, 128);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.viewport(0, 0, 128, 128);
		GlStateManager.depthMask(false);

		render.accept(null);

		GlStateManager.depthMask(true);
		GlStateManager.viewport(0, 0, Effect.mc.displayWidth, Effect.mc.displayHeight);
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.popMatrix();

		net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
		buffer.unbindFrame();
	}

	public static void enableGUIStandardItemLighting() {
		net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();

	}

}
