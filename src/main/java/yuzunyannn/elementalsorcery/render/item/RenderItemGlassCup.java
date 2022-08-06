package yuzunyannn.elementalsorcery.render.item;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.item.IJuice;
import yuzunyannn.elementalsorcery.api.util.client.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.item.ItemGlassCup;
import yuzunyannn.elementalsorcery.render.model.ModelGlassCup;

public class RenderItemGlassCup implements IRenderItem {

	public static final ModelGlassCup MODEL = new ModelGlassCup();
	public static final TextureBinder TEXTURE = new TextureBinder("textures/items/glass_cup.png");
	static final public TextureBinder TEXTURE_FLUID = new TextureBinder("textures/blocks/fluids/juice.png");

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		TEXTURE.bind();

		TransformType tt = IRenderItem.getTransform(stack);
		if (tt == TransformType.GUI) {
			GlStateManager.translate(0.5, 0.1, 0.5);
			GlStateManager.scale(0.04, 0.04, 0.04);
		} else {
			if (tt == TransformType.GROUND) {
				GlStateManager.translate(0.5, 0.3, 0.5);
				GlStateManager.scale(0.015, 0.015, 0.015);
			} else if (stack.getItemFrame() != null) {
				GlStateManager.translate(0.5, 0.1, 0.5);
				GlStateManager.scale(0.04, 0.04, 0.04);
			} else if (tt == TransformType.FIXED) {
				GlStateManager.rotate(-90, 1, 0, 0);
				GlStateManager.translate(0.5, -0.65, 0.5);
				GlStateManager.scale(0.04, 0.04, 0.04);
			} else if (tt == TransformType.THIRD_PERSON_LEFT_HAND || tt == TransformType.THIRD_PERSON_RIGHT_HAND) {
				GlStateManager.translate(0.5, 0.425, 0.6);
				GlStateManager.scale(0.02, 0.02, 0.02);
				GlStateManager.rotate(30, 1, 0, 0);
			} else {
				GlStateManager.translate(0.5, 0.45, 0.6);
				GlStateManager.scale(0.02, 0.02, 0.02);
			}
		}

		MODEL.render(null, 0, 0, 0, 0, 0, 1.0f);

		IJuice juice = ItemGlassCup.getJuice(stack);
		if (juice != null) {
			juice.beforeRenderJuice();
			float r = juice.getJuiceCount() / juice.getMaxJuiceCount();
			GlStateManager.disableLighting();
			drawJuiceInCup(r, EventClient.tickRender / 2);
			GlStateManager.enableLighting();
		}

		GlStateManager.popMatrix();
		GlStateManager.enableCull();
	}

	static final int fluidTextureHeight = 512;
	static final int fluidTextureWidth = 16;
	static final int fluidBlockSize = 16;
	static final float texBlockRateX = fluidBlockSize / (float) fluidTextureWidth;
	static final float texBlockRateY = fluidBlockSize / (float) fluidTextureHeight;

	static final float sideOffset = 4.95f;
	static final float topOffset = 6.05f;

	public static void drawJuiceInCup(float fullRate, int frameIndex) {
		if (fullRate < 0.0001f) return;
		float high = fullRate * 12;

		int indexX = 0;
		int indexY = frameIndex % 32;
		if (indexY < 0) indexY += 32;

		float ax = indexX * texBlockRateX;
		float ay = indexY * texBlockRateY;
		float bx = (indexX + 1) * texBlockRateX;
		float by = (indexY + 1) * texBlockRateY;
		float cy = (indexY + fullRate) * texBlockRateY;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

		// four side
		bufferbuilder.pos(-sideOffset, topOffset, sideOffset).tex(ax, cy).endVertex();
		bufferbuilder.pos(-sideOffset, high + topOffset, sideOffset).tex(ax, ay).endVertex();
		bufferbuilder.pos(sideOffset, high + topOffset, sideOffset).tex(bx, ay).endVertex();
		bufferbuilder.pos(sideOffset, topOffset, sideOffset).tex(bx, cy).endVertex();

		bufferbuilder.pos(-sideOffset, topOffset, -sideOffset).tex(ax, cy).endVertex();
		bufferbuilder.pos(-sideOffset, high + topOffset, -sideOffset).tex(ax, ay).endVertex();
		bufferbuilder.pos(sideOffset, high + topOffset, -sideOffset).tex(bx, ay).endVertex();
		bufferbuilder.pos(sideOffset, topOffset, -sideOffset).tex(bx, cy).endVertex();

		bufferbuilder.pos(sideOffset, topOffset, -sideOffset).tex(ax, cy).endVertex();
		bufferbuilder.pos(sideOffset, high + topOffset, -sideOffset).tex(ax, ay).endVertex();
		bufferbuilder.pos(sideOffset, high + topOffset, sideOffset).tex(bx, ay).endVertex();
		bufferbuilder.pos(sideOffset, topOffset, sideOffset).tex(bx, cy).endVertex();

		bufferbuilder.pos(-sideOffset, topOffset, -sideOffset).tex(ax, cy).endVertex();
		bufferbuilder.pos(-sideOffset, high + topOffset, -sideOffset).tex(ax, ay).endVertex();
		bufferbuilder.pos(-sideOffset, high + topOffset, sideOffset).tex(bx, ay).endVertex();
		bufferbuilder.pos(-sideOffset, topOffset, sideOffset).tex(bx, cy).endVertex();

		// bottom

		bufferbuilder.pos(-sideOffset, topOffset, -sideOffset).tex(ax, by).endVertex();
		bufferbuilder.pos(-sideOffset, topOffset, sideOffset).tex(ax, ay).endVertex();
		bufferbuilder.pos(sideOffset, topOffset, sideOffset).tex(bx, ay).endVertex();
		bufferbuilder.pos(sideOffset, topOffset, -sideOffset).tex(bx, by).endVertex();

		bufferbuilder.pos(-sideOffset, high + topOffset, -sideOffset).tex(ax, by).endVertex();
		bufferbuilder.pos(-sideOffset, high + topOffset, sideOffset).tex(ax, ay).endVertex();
		bufferbuilder.pos(sideOffset, high + topOffset, sideOffset).tex(bx, ay).endVertex();
		bufferbuilder.pos(sideOffset, high + topOffset, -sideOffset).tex(bx, by).endVertex();

		tessellator.draw();
	}

}
