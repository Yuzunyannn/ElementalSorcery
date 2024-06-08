package yuzunyannn.elementalsorcery.api.util.client;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockTorch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.ESObjects;

public class RenderFriend {

	public static interface RenderCallbacks {
		default void beforeRender() {
		};

		default void endRender() {
		};
	}

	public static final Minecraft mc = Minecraft.getMinecraft();

	static public Vec3d getPartialTicks(Vec3d n, Vec3d prevN, float partialTicks) {
		return prevN.add(n.subtract(prevN).scale(partialTicks));
	}

	static public double getPartialTicks(double n, double prevN, float partialTicks) {
		return prevN + (n - prevN) * partialTicks;
	}

	static public float getPartialTicks(float n, float prevN, float partialTicks) {
		return prevN + (n - prevN) * partialTicks;
	}

	static public void renderItemLayout(ItemStack stack, double x, double y, double z) {
		renderItemLayout(stack, x, y, z, 0);
	}

	static public void renderItemLayout(ItemStack stack, double x, double y, double z, float rotation) {
		if (stack.isEmpty()) return;
		int n = MathHelper.ceil(MathHelper.sqrt(stack.getCount()) / 2);
		GlStateManager.pushMatrix();
		if (n > 1) {
			if (rotation != 0) GlStateManager.rotate(rotation, 0, 1, 0);
			layItemPositionFix(stack);
			for (int i = 0; i < n; i++) {
				if (rotation > 0) GlStateManager.rotate(rotation, 0, 1, 0);
				Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
				GlStateManager.translate(0.05, 0.005, 0.05);
			}
		} else {
			GlStateManager.translate(x, y, z);
			if (rotation != 0) GlStateManager.rotate(rotation, 0, 1, 0);
			layItemPositionFix(stack);
			Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
		}
		GlStateManager.popMatrix();
	}

	static public void renderSpecialItem(ItemStack stack, TextureBinder texture, ModelBase MODEL,
			boolean needLighting) {
		RenderFriend.renderSpecialItem(stack, texture, MODEL, needLighting, 0.038, 0.0175, 0, 0);
	}

	static public void renderSpecialItem(ItemStack stack, TextureBinder texture, ModelBase model, boolean needLighting,
			double scale, double scaleGround, double yoff, double yoffGround) {
		GlStateManager.pushMatrix();
		texture.bind();
		if (IRenderItem.isGUI(stack)) {
			if (needLighting) GlStateManager.enableLighting();
			GlStateManager.rotate(45, 0, 1, 0);
			GlStateManager.rotate(30, 1, 0, 1);
			GlStateManager.translate(0.35, 0.26 + yoff, 0.35);
			GlStateManager.scale(scale, scale, scale);
			model.render(null, 0, 0, 0, 0, 0, 1.0f);
			if (needLighting) GlStateManager.disableLighting();
		} else if (IRenderItem.isTransform(stack, ItemCameraTransforms.TransformType.FIXED)) {
			GlStateManager.translate(0.5, 0.25, 0.5);
			GlStateManager.scale(scaleGround * 2, scaleGround * 2, scaleGround * 2);
			model.render(null, 0, 0, 0, 0, 0, 1.0f);
		} else if (IRenderItem.isTransform(stack, ItemCameraTransforms.TransformType.GROUND)) {
			GlStateManager.translate(0.5, 0.4 + yoffGround, 0.5);
			GlStateManager.scale(scaleGround, scaleGround, scaleGround);
			model.render(null, 0, 0, 0, 0, 0, 1.0f);
		} else {
			GlStateManager.translate(0.5, 0.5 + yoffGround, 0.5);
			GlStateManager.rotate(60, 1, 0, 0);
			GlStateManager.scale(scaleGround, scaleGround, scaleGround);
			model.render(null, 0, 0, 0, 0, 0, 1.0f);
		}
		GlStateManager.popMatrix();
	}

	static public void renderSpecialItemForModelForReverseModel(ItemStack stack, TextureBinder texture, ModelBase model,
			boolean needLighting) {
		GlStateManager.pushMatrix();
		texture.bind();
		if (IRenderItem.isGUI(stack)) {
			if (needLighting) GlStateManager.enableLighting();
			GlStateManager.rotate(45, 0, 1, 0);
			GlStateManager.rotate(30, 1, 0, 1);
			GlStateManager.translate(0.35, 0.26, 0.35);
			GlStateManager.scale(-0.038, -0.038, 0.038);
			model.render(null, 0, 0, 0, 0, 0, 1.0f);
			if (needLighting) GlStateManager.disableLighting();
		} else if (IRenderItem.isTransform(stack, ItemCameraTransforms.TransformType.FIXED)) {
			GlStateManager.translate(0.5, 0.25, 0.5);
			GlStateManager.scale(-0.0175 * 2, -0.0175 * 2, 0.0175 * 2);
			model.render(null, 0, 0, 0, 0, 0, 1.0f);
		} else if (IRenderItem.isTransform(stack, ItemCameraTransforms.TransformType.GROUND)) {
			GlStateManager.translate(0.5, 0.4, 0.5);
			GlStateManager.scale(-0.0175, -0.0175, 0.0175);
			model.render(null, 0, 0, 0, 0, 0, 1.0f);
		} else {
			GlStateManager.translate(0.5, 0.5, 0.5);
			GlStateManager.rotate(60, 1, 0, 0);
			GlStateManager.scale(-0.0175, -0.0175, 0.0175);
			model.render(null, 0, 0, 0, 0, 0, 1.0f);
		}
		GlStateManager.popMatrix();
	}

	static public void startTileEntitySpecialRender(double x, double y, double z, double scale, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
		GlStateManager.translate(x, y, z);
		GlStateManager.scale(scale, scale, scale);
	}

	static public void startTileEntitySpecialRenderForReverseModel(double x, double y, double z, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
		GlStateManager.translate(x, y, z);
		GlStateManager.scale(-0.0625, -0.0625, 0.0625);
		GlStateManager.disableCull();
//		GlStateManager.enableBlend();
//		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5f);
//		GlStateManager.scale(-1, -1, 1);
	}

	static public void endTileEntitySpecialRender() {
		GlStateManager.popMatrix();
	}

	static public void endTileEntitySpecialRenderForReverseModel() {
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
	}

	static public void bindDestoryTexture(TextureBinder texture, int destroyStage,
			TileEntityRendererDispatcher rendererDispatcher, ResourceLocation[] destroyStages) {
		if (destroyStage >= 0) {
			TextureManager texturemanager = rendererDispatcher.renderEngine;
			if (texturemanager != null) texturemanager.bindTexture(destroyStages[destroyStage]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scale(4.0F, 4.0F, 1.0F);
			GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		} else texture.bind();
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
		if (item instanceof IRenderLayoutFix) ((IRenderLayoutFix) item).fixLauout(stack);
		else if (item instanceof ItemBed) {
			GlStateManager.translate(0, 0.44, 0);
			GlStateManager.rotate(90, 1, 0, 0);
			GlStateManager.scale(0.5, 0.5, 0.5);
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
				canlay = canlay || block == ESObjects.BLOCKS.LIFE_FLOWER;
				canlay = canlay || block instanceof BlockHopper;
				canlay = canlay || block instanceof BlockRailBase;
				canlay = canlay || block == ESObjects.BLOCKS.ELF_FRUIT;
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

	static public void drawTextureModalRect(float x, float y, float u, float v, float width, float height,
			float textureWidth, float textureHeight) {
		float f = 1.0F / textureWidth;
		float f1 = 1.0F / textureHeight;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double) x, (double) (y + height), 0.0D).tex((double) (u * f), (double) ((v + (float) height) * f1)).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + height), 0.0D).tex((double) ((u + (float) width) * f), (double) ((v + (float) height) * f1)).endVertex();
		bufferbuilder.pos((double) (x + width), (double) y, 0.0D).tex((double) ((u + (float) width) * f), (double) (v * f1)).endVertex();
		bufferbuilder.pos((double) x, (double) y, 0.0D).tex((double) (u * f), (double) (v * f1)).endVertex();
		tessellator.draw();
	}

	public static void drawTextureRectInCenter(float x, float y, float width, float height, float u, float v,
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

	public static void drawTextureRectInCenter(float x, float y, float width, float height, float u, float v,
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

	public static void drawTextureRectInCenter(float x, float y, float width, float height) {
		drawTextureRectInCenter(x, y, width, height, 0, 0, 1, 1, 1, 1);
	}

	public static final RenderRect SPLIT9_AVERAGE_RECT = new RenderRect(1 / 3f, 2 / 3f, 1 / 3f, 2 / 3f);

	public static void drawFrameInCenter(float x, float y, float width, float height, RenderTexutreFrame frame) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		float hw = width / 2;
		float hh = height / 2;
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x - hw, y + hh, 0.0D).tex(frame.x, frame.y + frame.height).endVertex();
		bufferbuilder.pos(x + hw, y + hh, 0.0D).tex(frame.x + frame.width, frame.y + frame.height).endVertex();
		bufferbuilder.pos(x + hw, y - hh, 0.0D).tex(frame.x + frame.width, frame.y).endVertex();
		bufferbuilder.pos(x - hw, y - hh, 0.0D).tex(frame.x, frame.y).endVertex();
		tessellator.draw();
	}

	public static void drawSplit9FrameInCenter(double x, double y, double width, double height,
			RenderTexutreFrame frame, RenderRect splitRect) {

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();

		double hw = width / 2;
		double hh = height / 2;
		double cw, ch, cx, cy;
		double tcw, tch, tcx, tcy;
		double top = 1 - splitRect.top;
		double bottom = 1 - splitRect.bottom;

		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

		// left top
		cx = x - hw;
		cy = y - hh;
		cw = splitRect.left * frame.width * frame.texWidth;
		ch = top * frame.height * frame.texHeight;
		tcx = frame.x;
		tcy = frame.y;
		tcw = splitRect.left * frame.width;
		tch = top * frame.height;

		bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
		bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();

		// left
		cy = cy + ch;
		ch = height - ch * 2;
		tcy = tcy + tch;
		tch = (bottom - top) * frame.height;

		bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
		bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();

		// left bottom
		cy = cy + ch;
		ch = (height - ch) / 2;
		tcy = tcy + tch;
		tch = (1 - bottom) * frame.height;

		bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
		bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();

		// bottom
		cx = cx + cw;
		cw = width - cw * 2;
		tcx = tcx + tcw;
		tcw = (splitRect.right - splitRect.left) * frame.width;

		bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
		bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();

		// right bottom
		cx = cx + cw;
		cw = (width - cw) / 2;
		tcx = tcx + tcw;
		tcw = (1 - splitRect.right) * frame.width;

		bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
		bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();

		// right
		ch = height - ch * 2;
		cy = cy - ch;
		tch = (bottom - top) * frame.height;
		tcy = tcy - tch;

		bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
		bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();

		// right top
		ch = (height - ch) / 2;
		cy = cy - ch;
		tch = top * frame.height;
		tcy = tcy - tch;

		bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
		bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();

		// top
		cw = width - cw * 2;
		cx = cx - cw;
		tcw = (splitRect.right - splitRect.left) * frame.width;
		tcx = tcx - tcw;
		bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
		bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();

		// center
		cy = cy + ch;
		ch = height - ch * 2;
		tcy = tcy + tch;
		tch = (bottom - top) * frame.height;

		bufferbuilder.pos(cx, cy, 0.0D).tex(tcx, tcy).endVertex();
		bufferbuilder.pos(cx, cy + ch, 0.0D).tex(tcx, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy + ch, 0.0D).tex(tcx + tcw, tcy + tch).endVertex();
		bufferbuilder.pos(cx + cw, cy, 0.0D).tex(tcx + tcw, tcy).endVertex();

		tessellator.draw();

	}

	public static void disableLightmap(boolean disabled) {
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		if (disabled) GlStateManager.disableTexture2D();
		else GlStateManager.enableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	public static int getRenderDistanceChunks() {
		return mc.gameSettings.renderDistanceChunks;
	}

	private static void renderFullOutlineByModelRenderer(ModelRenderer modelRenderer) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(modelRenderer.offsetX, modelRenderer.offsetY, modelRenderer.offsetZ);

		GlStateManager.translate(modelRenderer.rotationPointX / 16.0, modelRenderer.rotationPointY / 16.0, modelRenderer.rotationPointZ / 16.0);
		if (modelRenderer.rotateAngleZ != 0.0F)
			GlStateManager.rotate(modelRenderer.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
		if (modelRenderer.rotateAngleY != 0.0F)
			GlStateManager.rotate(modelRenderer.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
		if (modelRenderer.rotateAngleX != 0.0F)
			GlStateManager.rotate(modelRenderer.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);

		for (ModelBox box : modelRenderer.cubeList) {
			double minX = box.posX1 / 16.0 - 0.001D;
			double minY = box.posY1 / 16.0 - 0.001D;
			double minZ = box.posZ1 / 16.0 - 0.001D;
			double maxX = box.posX2 / 16.0 + 0.001D;
			double maxY = box.posY2 / 16.0 + 0.001D;
			double maxZ = box.posZ2 / 16.0 + 0.001D;
			RenderGlobal.drawBoundingBox(minX, minY, minZ, maxX, maxY, maxZ, 0, 0, 0, 0.4f);
		}

		if (modelRenderer.childModels != null) {
			for (int i = 0; i < modelRenderer.childModels.size(); ++i) {
				renderFullOutlineByModelRenderer(modelRenderer.childModels.get(i));
			}
		}

		GlStateManager.popMatrix();
	}

	public static void renderOutlineByModel(ModelBase model, EntityPlayer player, BlockPos pos, float partialTicks,
			boolean fullRender, RenderCallbacks callback) {
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.glLineWidth(2.0F);
		GlStateManager.disableTexture2D();
		GlStateManager.depthMask(false);

		double d3 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
		double d4 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
		double d5 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;

		double x = pos.getX() + 0.5 - d3;
		double y = pos.getY() - d4;
		double z = pos.getZ() + 0.5 - d5;

		GlStateManager.translate(x, y, z);
		if (callback != null) callback.beforeRender();

		if (fullRender) {
			for (ModelRenderer modelRenderer : model.boxList) renderFullOutlineByModelRenderer(modelRenderer);
		} else {
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
			for (ModelRenderer modelRenderer : model.boxList) {
				for (ModelBox box : modelRenderer.cubeList) {
					double minX = box.posX1 / 16.0 - 0.0020000000949949026D;
					double minY = box.posY1 / 16.0 - 0.0020000000949949026D;
					double minZ = box.posZ1 / 16.0 - 0.0020000000949949026D;
					double maxX = box.posX2 / 16.0 + 0.0020000000949949026D;
					double maxY = box.posY2 / 16.0 + 0.0020000000949949026D;
					double maxZ = box.posZ2 / 16.0 + 0.0020000000949949026D;
					RenderGlobal.drawBoundingBox(bufferbuilder, minX, minY, minZ, maxX, maxY, maxZ, 0, 0, 0, 0.4f);
				}
			}
			tessellator.draw();
		}

		if (callback != null) callback.endRender();
		GlStateManager.translate(-x, -y, -z);

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

}
