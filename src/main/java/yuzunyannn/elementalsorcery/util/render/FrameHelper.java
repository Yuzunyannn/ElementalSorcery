package yuzunyannn.elementalsorcery.util.render;

import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import yuzunyannn.elementalsorcery.render.effect.Effect;

public class FrameHelper {

	static Framebuffer frameBuff128 = null;

	static public Framebuffer getFrameBuff128() {
		if (FrameHelper.frameBuff128 == null) FrameHelper.frameBuff128 = new Framebuffer(128, 128, false);
		return FrameHelper.frameBuff128;
	}

	public static void bindOffscreenTexture128() {
		getFrameBuff128().bindTexture();
	}

	public static void renderOffscreenTexture128(Consumer<Void> render) {
		FrameHelper.renderOffscreenTexture128(render, getFrameBuff128());
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

}
