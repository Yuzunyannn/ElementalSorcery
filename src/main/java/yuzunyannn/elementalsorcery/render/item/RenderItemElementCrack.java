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
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.util.client.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.render.model.ModelGlassCup;
import yuzunyannn.elementalsorcery.util.render.FrameHelper;
import yuzunyannn.elementalsorcery.util.render.Framebuffer;
import yuzunyannn.elementalsorcery.util.render.MCFramebufferModify;
import yuzunyannn.elementalsorcery.util.render.Shaders;

public class RenderItemElementCrack implements IRenderItem {

	static private Framebuffer frameBuff128 = null;

	static public Framebuffer getFrameBuff() {
		if (frameBuff128 == null) frameBuff128 = new Framebuffer(new MCFramebufferModify(128, 128));
		return frameBuff128;
	}

	static public void bindCrackTexture() {
		updateRenderTextureFlag = true;
		getFrameBuff().bindTexture();
	}

	static public boolean updateRenderTextureFlag = false;

	public static final ModelGlassCup MODEL = new ModelGlassCup();
	public static final TextureBinder ELEMENT_SKY_TEXTURE = new TextureBinder("textures/items/element_crack.png");

	public static final FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
	public static final FloatBuffer MODELVIEW = GLAllocation.createDirectFloatBuffer(16);
	public static final FloatBuffer PROJECTION = GLAllocation.createDirectFloatBuffer(16);

	@Override
	public void render(ItemStack stack, float partialTicks) {
		updateRenderTextureFlag = true;

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

		getFrameBuff().bindTexture();

		GlStateManager.scale(25, 25, 25);
		if (isInWorld) {
			RenderItemElementCrack.startTexGen(1);
			GlStateManager.scale(4, 4, 4);
			RenderFriend.disableLightmap(true);
			GlStateManager.disableLighting();
		}

		float r = 1, g = 1, b = 1, a = 1;

		double g3 = 0.866025;
		double cg3 = 0.25 / 0.866025;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();

//		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
//		bufferbuilder.pos(0, 0, 0).tex(0, 0);
//		bufferbuilder.color(r, g, b, a).endVertex();
//		bufferbuilder.pos(0, 5, 0).tex(0, 1);
//		bufferbuilder.color(r, g, b, a).endVertex();
//		bufferbuilder.pos(5, 5, 0).tex(1, 1);
//		bufferbuilder.color(r, g, b, a).endVertex();
//		bufferbuilder.pos(5, 0, 0).tex(1, 0);
//		bufferbuilder.color(r, g, b, a).endVertex();
//		tessellator.draw();

		bufferbuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos(-0.5, 0, -cg3).tex(0, 0);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(0.5, 0, -cg3).tex(1, 0);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(0, g3, 0).tex(0.5, g3);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(-0.5, 0, -cg3).tex(0, 0);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(0, 0, g3 - cg3).tex(1, 0);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(0, g3, 0).tex(0.5, g3);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(0.5, 0, -cg3).tex(0, 0);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(0, 0, g3 - cg3).tex(1, 0);
		bufferbuilder.color(r, g, b, a).endVertex();
		bufferbuilder.pos(0, g3, 0).tex(0.5, g3);
		bufferbuilder.color(r, g, b, a).endVertex();
		tessellator.draw();

		if (isInWorld) {
			RenderFriend.disableLightmap(false);
			GlStateManager.enableLighting();
			RenderItemElementCrack.endTexGen();
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

	public static double h;
	public static Vec3d move = Vec3d.ZERO;
	public static Vec3d prevMove = Vec3d.ZERO;

	public static void updateRenderData() {
		h += 1;
		double x = Math.cos(h / 180 * Math.PI) / 5;
		double y = Math.sin(h / 180 * Math.PI) / 5;
		prevMove = move;
		move = move.add(x, y, 0);
	}

	public static void updateRenderTexture(float partialTicks) {
		FrameHelper.renderOffscreenTexture128(v -> {
			GlStateManager.clearColor(0, 0, 0, 1);
			GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);
			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.enableNormalize();

			float tick = EventClient.tickRender + partialTicks;
			double x = RenderFriend.getPartialTicks(move.x, prevMove.x, partialTicks);
			double y = RenderFriend.getPartialTicks(move.y, prevMove.y, partialTicks);
			Shaders.ElementSky.bind();

			ELEMENT_SKY_TEXTURE.bind();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();

			GlStateManager.scale(1.5, 1.5, 1.5);

			Shaders.ElementSky.setUniform("c_ratio", tick / 30);
			Shaders.ElementSky.setUniform("ratio", tick / 25);
			Shaders.ElementSky.setUniform("move", Vec3d.ZERO);
			bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			bufferbuilder.pos(0, 0, 0).tex(0, 0).endVertex();
			bufferbuilder.pos(0, 128, 0).tex(0, 1).endVertex();
			bufferbuilder.pos(128, 128, 0).tex(1, 1).endVertex();
			bufferbuilder.pos(128, 0, 0).tex(1, 0).endVertex();
			tessellator.draw();

			Shaders.ElementSky.setUniform("ratio", tick / 25 + 3.1415926);
			Shaders.ElementSky.setUniform("move", new Vec3d(0.25, -0.25, 0));
			bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			bufferbuilder.pos(0, 0, 0).tex(0, 0).endVertex();
			bufferbuilder.pos(0, 128, 0).tex(0, 1).endVertex();
			bufferbuilder.pos(128, 128, 0).tex(1, 1).endVertex();
			bufferbuilder.pos(128, 0, 0).tex(1, 0).endVertex();
			tessellator.draw();

			GlStateManager.scale(0.66666667, 0.66666667, 0.66666667);

			Shaders.ElementSky.setUniform("c_ratio", tick / 10);
			Shaders.ElementSky.setUniform("ratio", 3.1415926 + tick / 50);
			Shaders.ElementSky.setUniform("move", new Vec3d(x, y, 0));
			bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			bufferbuilder.pos(0, 0, 0).tex(0, 0).endVertex();
			bufferbuilder.pos(0, 128, 0).tex(0, 1).endVertex();
			bufferbuilder.pos(128, 128, 0).tex(1, 1).endVertex();
			bufferbuilder.pos(128, 0, 0).tex(1, 0).endVertex();
			tessellator.draw();

			Shaders.ElementSky.setUniform("ratio", 3.1415926 / 2 + tick / 50);
			Shaders.ElementSky.setUniform("move", new Vec3d(-x, -y, 0));
			bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			bufferbuilder.pos(0, 0, 0).tex(0, 0).endVertex();
			bufferbuilder.pos(0, 128, 0).tex(0, 1).endVertex();
			bufferbuilder.pos(128, 128, 0).tex(1, 1).endVertex();
			bufferbuilder.pos(128, 0, 0).tex(1, 0).endVertex();
			tessellator.draw();
			Shaders.ElementSky.unbind();

		}, getFrameBuff());
	}

	public static void startTexGen(double fScale) {
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
		if (fScale != 1) GlStateManager.scale(fScale, fScale, fScale);
		GlStateManager.multMatrix(PROJECTION);
		GlStateManager.multMatrix(MODELVIEW);
	}

	public static void endTexGen() {
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);

		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);
	}
}
