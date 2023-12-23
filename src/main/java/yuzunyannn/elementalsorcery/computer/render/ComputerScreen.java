package yuzunyannn.elementalsorcery.computer.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.computer.soft.IAPPGui;
import yuzunyannn.elementalsorcery.api.computer.soft.IComputerException;
import yuzunyannn.elementalsorcery.container.ContainerComputer;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.util.render.Framebuffer;

@SideOnly(Side.CLIENT)
public class ComputerScreen {

	public int renderCounter = 0;
	public IAPPGui currGui;
	protected Framebuffer buffer;
	protected int frameBufferWidth, frameBufferHeight;
	protected boolean waitPoolMark;
	protected IComputerException exception;
	protected ContainerComputer container;

	public void init() {
		frameBufferWidth = 1280;
		frameBufferHeight = 720;
		buffer = new Framebuffer(frameBufferWidth, frameBufferHeight, true);
	}

	public int getHeight() {
		return frameBufferHeight / 5;
	}

	public int getWidth() {
		return frameBufferWidth / 5;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}

	void doRender(float partialTicks) {

		buffer.bindFrame(false);
		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0D, frameBufferWidth, frameBufferHeight, 0, -1000, 1000);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.viewport(0, 0, frameBufferWidth, frameBufferHeight);
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableCull();

		letRender(partialTicks);

		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
		GlStateManager.viewport(0, 0, Effect.mc.displayWidth, Effect.mc.displayHeight);
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.popMatrix();

		net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
		buffer.unbindFrame();
	}

	void letRender(float partialTicks) {
		IAPPGui gui = currGui;
		GlStateManager.clearColor(1f, 1f, 1f, 0);
		GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GlStateManager.translate(0, frameBufferHeight, 0);
		GlStateManager.scale(5, -5, 5);

		if (exception != null) {

		}

		if (gui == null) {

		} else {
			try {
				gui.render(partialTicks);
			} catch (Exception e) {
				this.exception = IComputerException.easy(e.getMessage());
				ESAPI.logger.warn("computer render crash!", e);
			}
		}
	}

	public void bindTexture() {
		renderCounter = 0;
		if (buffer == null) return;
		buffer.bindTexture();
	}

	public void setAPPGui(IAPPGui gui) {
		currGui = gui;
		exception = null;
		renderCounter = 0;
	}

	public void hold() {
		renderCounter = 0;
	}

	public void release() {
		waitPoolMark = true;
	}

	protected void close() {
		if (buffer == null) return;
		buffer.dispose();
		buffer = null;
	}

	public void onMouseEvent(Vec3d vec3d) {
		if (currGui != null) currGui.onMouseEvent(vec3d);
	}

}
