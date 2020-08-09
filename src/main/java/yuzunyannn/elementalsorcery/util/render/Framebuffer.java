package yuzunyannn.elementalsorcery.util.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import net.minecraft.client.renderer.OpenGlHelper;

public class Framebuffer {

	private net.minecraft.client.shader.Framebuffer buffer;
	private int originFrameName = -1;

	public Framebuffer(int width, int height, boolean useDepth) {
		buffer = new net.minecraft.client.shader.Framebuffer(width, height, useDepth);
	}

	public void resize(int width, int height) {
		buffer.createBindFramebuffer(width, height);
		buffer.framebufferClear();
	}

	public void bindFrame() {
		if (originFrameName >= 0 || buffer == null) return;
		originFrameName = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
		buffer.bindFramebuffer(true);
	}

	public void unbindFrame() {
		if (originFrameName < 0 || buffer == null) return;
		buffer.unbindFramebuffer();
		OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, originFrameName);
		originFrameName = -1;
	}

	public void bindTexture() {
		if (buffer != null) buffer.bindFramebufferTexture();
	}

	public void unbindTexture() {
		if (buffer != null) buffer.unbindFramebufferTexture();
	}

	public void dispose() {
		if (buffer == null) return;
		this.unbindFrame();
		buffer.deleteFramebuffer();
		buffer = null;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.dispose();
	}
}
