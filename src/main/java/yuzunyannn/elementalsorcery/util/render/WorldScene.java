package yuzunyannn.elementalsorcery.util.render;

import java.lang.ref.WeakReference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WorldScene {

	private static boolean inRender = false;
	private Framebuffer buffer;
	private WeakReference<Entity> looker = new WeakReference(null);
	private Minecraft mc = Minecraft.getMinecraft();

	public WorldScene(int width, int height) {
		buffer = new Framebuffer(width, height, true);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		buffer.dispose();
	}

	public void dispose() {
		buffer.dispose();
	}

	public void resize(int width, int height) {
		buffer.resize(width, height);
	}

	public Framebuffer getFramebuffer() {
		return buffer;
	}

	public void setLooker(Entity looker) {
		if (this.looker != null && this.looker.get() == looker) return;
		this.looker = new WeakReference(looker);
	}

	public boolean doRender() {
		if (Minecraft.getMinecraft().world == null) return false;
		if (inRender) return false;
		Framebuffer buffer = this.buffer;
		inRender = true;
		GlStateManager.pushMatrix();

		Entity originViewEntity = mc.getRenderViewEntity();
		mc.setRenderViewEntity(looker.get());
		int originThirdPersonView = mc.gameSettings.thirdPersonView;
		mc.gameSettings.thirdPersonView = 0;
		boolean originHideUI = mc.gameSettings.hideGUI;
		mc.gameSettings.hideGUI = true;
		// boolean originAnaglyph = mc.gameSettings.anaglyph;
		// this.mc.gameSettings.anaglyph = false;

		WorldSceneEventHandle.instance.push(this);
		buffer.bindFrame();
		mc.entityRenderer.renderWorld(mc.getRenderPartialTicks(), System.nanoTime());
		buffer.unbindFrame();
		WorldSceneEventHandle.instance.pop();

		// mc.gameSettings.anaglyph = originAnaglyph;
		mc.gameSettings.hideGUI = originHideUI;
		mc.gameSettings.thirdPersonView = originThirdPersonView;
		mc.setRenderViewEntity(originViewEntity);

		GlStateManager.popMatrix();
		inRender = false;
		return true;
	}

	public void bind() {
		buffer.bindTexture();
	}

	public void unbind() {
		buffer.unbindTexture();
	}

	public static void init() {
		MinecraftForge.EVENT_BUS.register(WorldSceneEventHandle.instance);
	}

	public static class WorldSceneEventHandle {

		final static WorldSceneEventHandle instance = new WorldSceneEventHandle();

		private int i = 0;

		public void push(WorldScene scene) {
			i++;
		}

		public void pop() {
			i--;
		}

		public boolean inScene() {
			return i > 0;
		}

		@SubscribeEvent
		public void fixedFOV(EntityViewRenderEvent.FOVModifier event) {
			if (this.inScene()) event.setFOV(70);
		}

	}

}
