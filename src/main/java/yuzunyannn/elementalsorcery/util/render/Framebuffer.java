package yuzunyannn.elementalsorcery.util.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import net.minecraft.client.renderer.OpenGlHelper;

public class Framebuffer {

	private net.minecraft.client.shader.Framebuffer buffer;
	private int originFrameName = -1;
	private final boolean useDepth;

	public Framebuffer(boolean useDepth) {
		this.useDepth = useDepth;
	}

	public Framebuffer(int width, int height, boolean useDepth) {
		buffer = new net.minecraft.client.shader.Framebuffer(width, height, useDepth);
		this.useDepth = useDepth;
	}

	public void resize(int width, int height) {
		if (buffer == null) buffer = new net.minecraft.client.shader.Framebuffer(width, height, useDepth);
		else {
			buffer.createBindFramebuffer(width, height);
			buffer.framebufferClear();
		}
	}

	public void bindFrame(boolean resetViewPort) {
		if (originFrameName >= 0 || buffer == null) return;
		originFrameName = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
		buffer.bindFramebuffer(resetViewPort);
	}

	public int unbindFrame() {
		if (originFrameName < 0 || buffer == null) return 0;
		int oid = originFrameName;
		buffer.unbindFramebuffer();
		OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, originFrameName);
		originFrameName = -1;
		return oid;
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

	public boolean isDispose() {
		return this.buffer == null;
	}

	public net.minecraft.client.shader.Framebuffer getMCBuffer() {
		return buffer;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.dispose();
	}
}
