package yuzunyannn.elementalsorcery.render.item;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.model.ModelGlassCup;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

public class RenderItemElementCrack implements IRenderItem {

	public static final ModelGlassCup MODEL = new ModelGlassCup();
	public static final TextureBinder END_SKY_TEXTURE = new TextureBinder("textures/items/element_crack.png");

	public static final FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
	public static final FloatBuffer MODELVIEW = GLAllocation.createDirectFloatBuffer(16);
	public static final FloatBuffer PROJECTION = GLAllocation.createDirectFloatBuffer(16);

	@Override
	public void render(ItemStack stack, float partialTicks) {

		GlStateManager.disableLighting();
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();

		boolean isInWorld = true;
		TransformType tt = IRenderItem.getTransform(stack);
		if (tt == TransformType.GUI) {
			isInWorld = false;
			GlStateManager.translate(0.5, 0.15, 0.5);
			GlStateManager.scale(0.032, 0.032, 0.032);
			GlStateManager.rotate(EventClient.getGlobalRotateInRender(partialTicks), 0, 1, 0);
		} else {
			if (tt == TransformType.GROUND) {
				GlStateManager.translate(0.5, 0.4, 0.5);
				GlStateManager.scale(0.015, 0.015, 0.015);
			} else if (stack.getItemFrame() != null) {
				GlStateManager.translate(0.5, 0.2, 0.5);
				GlStateManager.scale(0.04, 0.04, 0.04);
				GlStateManager.rotate(180, 0, 1, 0);
			} else if (tt == TransformType.FIXED) {
				GlStateManager.rotate(-90, 1, 0, 0);
				GlStateManager.translate(0.5, -0.55, 0.5);
				GlStateManager.scale(0.04, 0.04, 0.04);
			} else if (tt == TransformType.THIRD_PERSON_LEFT_HAND || tt == TransformType.THIRD_PERSON_RIGHT_HAND) {
				GlStateManager.translate(0.5, 0.425, 0.6);
				GlStateManager.scale(0.02, 0.02, 0.02);
				GlStateManager.rotate(30, 1, 0, 0);
				GlStateManager.rotate(45, 0, 1, 0);
			} else {
				GlStateManager.translate(0.5, 0.45, 0.6);
				GlStateManager.scale(0.02, 0.02, 0.02);
				GlStateManager.rotate(0, 0, 1, 0);
			}
		}

		END_SKY_TEXTURE.bind();
		GlStateManager.scale(25, 25, 25);

		if (isInWorld) {
			GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_EYE_LINEAR);
			GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_EYE_LINEAR);
			GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_EYE_LINEAR);

			GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_EYE_PLANE, getBuffer(1f, 0, 0, 0));
			GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_EYE_PLANE, getBuffer(0, 1f, 0, 0));
			GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_EYE_PLANE, getBuffer(0, 0, 1f, 0));

			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.S);
			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.T);
			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.R);

			GlStateManager.getFloat(GL11.GL_MODELVIEW_MATRIX, MODELVIEW);
			GlStateManager.getFloat(GL11.GL_PROJECTION_MATRIX, PROJECTION);

			GlStateManager.matrixMode(GL11.GL_TEXTURE);
			GlStateManager.pushMatrix();
			GlStateManager.loadIdentity();
			GlStateManager.multMatrix(PROJECTION);
			GlStateManager.multMatrix(MODELVIEW);
			GlStateManager.scale(4, 4, 4);
		}

		float r = 1, g = 1, b = 1, a = 1;

		double g3 = 0.866025;
		double cg3 = 0.25 / 0.866025;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();

		r = 0.75f;
		g = 0.75f;
		b = 1f;
		bufferbuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos(-0.5, 0, -cg3).tex(0, 0);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(0.5, 0, -cg3).tex(1, 0);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(0, g3, 0).tex(0.5, g3);
		bufferbuilder.color(r, g, b, a).endVertex();
		tessellator.draw();
		r = 0.75f;
		g = 1f;
		b = 0.75f;
		bufferbuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos(-0.5, 0, -cg3).tex(0, 0);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(0, 0, g3 - cg3).tex(1, 0);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(0, g3, 0).tex(0.5, g3);
		bufferbuilder.color(r, g, b, a).endVertex();
		tessellator.draw();
		r = 1f;
		g = 0.75f;
		b = 0.75f;
		bufferbuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos(0.5, 0, -cg3).tex(0, 0);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(0, 0, g3 - cg3).tex(1, 0);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(0, g3, 0).tex(0.5, g3);
		bufferbuilder.color(r, g, b, a).endVertex();
		tessellator.draw();

		if (isInWorld) {
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);

			GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
			GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
			GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);
		}

//		GlStateManager.disableTexture2D();
//		GlStateManager.glLineWidth(1);
//		r = 31 / 255f;
//		g = 31 / 255f;
//		b = 31 / 255f;
//		bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
//		bufferbuilder.pos(0, g3, 0).color(r, g, b, a).endVertex();
//		bufferbuilder.pos(-0.5, 0, -cg3).color(r, g, b, a).endVertex();
//		bufferbuilder.pos(0.5, 0, -cg3).color(r, g, b, a).endVertex();
//		bufferbuilder.pos(0, g3, 0).color(r, g, b, a).endVertex();
//		bufferbuilder.pos(0, 0, g3 - cg3).color(r, g, b, a).endVertex();
//		bufferbuilder.pos(0.5, 0, -cg3).color(r, g, b, a).endVertex();
//		bufferbuilder.pos(0, 0, g3 - cg3).color(r, g, b, a).endVertex();
//		bufferbuilder.pos(-0.5, 0, -cg3).color(r, g, b, a).endVertex();
//		tessellator.draw();
//		GlStateManager.enableTexture2D();

		GlStateManager.popMatrix();
		GlStateManager.enableCull();
	}

	public static FloatBuffer getBuffer(float x, float y, float z, float w) {
		buffer.clear();
		buffer.put(x).put(y).put(z).put(w);
		buffer.flip();
		return buffer;
	}
}
