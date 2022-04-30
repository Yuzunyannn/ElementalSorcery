package yuzunyannn.elementalsorcery.render.effect;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import yuzunyannn.elementalsorcery.util.render.Framebuffer;

public class EffectListBuffer extends EffectList {

	final protected Framebuffer buffer = new Framebuffer(true);
	protected int lifeTick;

	@Override
	public void update() {
		if (!buffer.isDispose()) {
			if (Effect.displayChange) buffer.resize(Effect.displayWidth, Effect.displayHeight);
			if (lifeTick <= 0) buffer.dispose();
			else lifeTick--;
		}
		if (effects.isEmpty()) return;
		if (buffer.isDispose()) buffer.resize(Effect.displayWidth, Effect.displayHeight);
		lifeTick = 20 * 60 * 1; // 一分钟没用buff就释放掉，不浪费资源
		super.update();
	}

	protected void renderCore(float partialTicks) {
		super.render(partialTicks);
	}

	protected void renderFrame() {
		buffer.bindTexture();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos(0, 0, 0).tex(0, 1).color(1, 1, 1, 1.0f).endVertex();
		bufferbuilder.pos(0, Effect.displayHeight, 0).tex(0, 0).color(1, 1, 1, 1.0f).endVertex();
		bufferbuilder.pos(Effect.displayWidth, Effect.displayHeight, 0).tex(1, 0).color(1, 1, 1, 1.0f).endVertex();
		bufferbuilder.pos(Effect.displayWidth, 0, 0).tex(1, 1).color(1, 1, 1, 1.0f).endVertex();
		tessellator.draw();
	}

	@Override
	public void render(float partialTicks) {

		if (effects.isEmpty()) return;
		if (buffer.isDispose()) return;

		// 复制深度缓冲区
		int frameId = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, frameId);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, buffer.getMCBuffer().framebufferObject);
		GL30.glBlitFramebuffer(0, 0, Effect.displayWidth, Effect.displayHeight, 0, 0, Effect.displayWidth,
				Effect.displayHeight, GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, frameId);

		buffer.bindFrame(true);
		GlStateManager.clearColor(0, 0, 0, 0);
		GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);
		renderCore(partialTicks);
		buffer.unbindFrame();

		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0D, Effect.displayWidth, Effect.displayHeight, 0.0D, 1000.0D, 3000.0D);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.translate(0.0F, 0.0F, -2000.0F);
		GlStateManager.enableBlend();

		renderFrame();

		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.popMatrix();

	}
}
