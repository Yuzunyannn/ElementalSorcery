package yuzunyannn.elementalsorcery.render.entity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.entity.EntityPortal;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.event.IRenderClient;
import yuzunyannn.elementalsorcery.render.effect.element.EffectElement;
import yuzunyannn.elementalsorcery.util.render.Framebuffer;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.util.render.WorldScene;

@SideOnly(Side.CLIENT)
public class RenderEntityPortalWorldSecene implements IRenderClient, EntityPortal.IPortalDraw {

	public static final TextureBinder MASK_CENTER = new TextureBinder("textures/effect/portal_center.png");
	public static final TextureBinder MASK_OUTER = new TextureBinder("textures/effect/portal_outer.png");

	public Minecraft mc = Minecraft.getMinecraft();
	public Tessellator tessellator = Tessellator.getInstance();
	public BufferBuilder bufferbuilder = tessellator.getBuffer();

	private int originWidth = mc.displayWidth, originHeight = mc.displayHeight;
	private WorldScene worldScene = new WorldScene(mc.displayWidth, mc.displayHeight);
	private Framebuffer portal = new Framebuffer(mc.displayHeight, mc.displayHeight, false);

	private boolean isEnd = true;
	public boolean overDistance = false;

	private Entity portalEntityOther;
	private Entity portalEntityThis;

	public RenderEntityPortalWorldSecene() {

	}

	public void setPortal(Entity portal, Entity other) {
		portalEntityOther = other;
		portalEntityThis = portal;
	}

	@Override
	public void tick(EntityPortal entity) {
		setPortal(entity, entity.other);
		checkAndResize();
		EntityPlayerSP player = mc.player;
		if (player.getDistanceSq(entity) >= 16 * 16) overDistance = true;
		else overDistance = false;
		if (!overDistance) toTick();
		if (portalEntityOther != null) worldScene.setLooker(portalEntityOther);
		else worldScene.setLooker(portalEntityThis);
		maskTick();
	}

	private float getRaw() {
		if (portalEntityThis == null) return 0;
		Vec3d tar = portalEntityThis.getPositionVector().subtract(mc.player.getPositionVector()).normalize();
		double raw = MathHelper.atan2(tar.z, tar.x);
		raw = raw / Math.PI * 180 - 90;
		return (float) raw;
	}

	/** 进行渲染，消耗资源较大 */
	@Override
	public int onRender(float partialTicks) {
		if (this.isDispose() || overDistance) return end();
		if (portalEntityOther == null) {
			portalEntityThis.prevRotationYaw = portalEntityThis.rotationYaw;
			portalEntityThis.rotationYaw = this.getRaw();
			if (worldScene.doRenderSky()) this.doRender(partialTicks);
		} else {
			portalEntityOther.prevRotationYaw = portalEntityOther.rotationYaw;
			portalEntityOther.rotationYaw = this.getRaw();
			if (worldScene.doRenderWorld()) this.doRender(partialTicks);
		}
		return SUCCESS;
	}

	/** 进行传送门绘制 */
	private void doRender(float partialTicks) {
		GlStateManager.matrixMode(5889);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0D, mc.displayWidth, mc.displayHeight, 0.0D, 1000, 3000);

		GlStateManager.matrixMode(5888);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.translate(mc.displayWidth / 2.0f, mc.displayHeight / 2.0f, -2000);

		portal.bindFrame();
		GlStateManager.clearColor(1, 1, 1, 0);
		GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ACCUM);

		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		GlStateManager.depthMask(false);
		GlStateManager.disableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.color(1, 1, 1, 1);

		// 绘制遮罩
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		drawMask(partialTicks);
		// 绘制主图
		GlStateManager.colorMask(true, true, true, false);
		GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_ZERO);
		worldScene.bind();
		drawSceneAll();
		GlStateManager.colorMask(true, true, true, true);

		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
		GlStateManager.enableAlpha();
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		portal.unbindFrame();

		GlStateManager.viewport(0, 0, mc.displayWidth, mc.displayHeight);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5889);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);

	}

	private boolean isFadeIn = true;
	public float fadeRate = 0;
	public float fadeRoate = 0;
	public List<Part> partList = new LinkedList<>();
	public static final Random rand = new Random();

	private class Part {
		public float x;
		public float y;
		public float moveX;
		public float moveY;
		public boolean isBiger = false;
		public float scale = 1.0f;
		public final float referenceScale;
		public float fade = 1.0f;
		public int lifeTime;

		public Part() {
			int size = Math.min(mc.displayWidth, mc.displayHeight) / 2;
			lifeTime = rand.nextInt(40) + 40;
			moveX = rand.nextFloat() * 5 - 2.5f;
			moveY = rand.nextFloat() * 5 - 2.5f;
			scale = rand.nextFloat() * 0.75f + 0.25f;
			Vec3d tar = new Vec3d(moveX, moveY, 0).normalize();
			x = (float) (tar.x * size / 6 * 4);
			y = (float) (tar.y * size / 6 * 4);
			referenceScale = size / 10;
		}

		public void update() {
			lifeTime--;
			x += moveX;
			y += moveY;
			if (isBiger) {
				scale += 0.015f;
				if (scale >= 1f) isBiger = false;
			} else {
				scale -= 0.015f;
				if (scale <= 0.25f) isBiger = true;
			}
			if (lifeTime < 20) fade = lifeTime / 20f;
		}

		public void draw() {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, 0);
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.color(1, 1, 1, fade);
			EffectElement.TEXTURE.bind();
			renderRect((int) referenceScale);
			GlStateManager.popMatrix();
		}
	}

	private void maskTick() {
		if (isFadeIn) {
			fadeRate += 0.01;
			if (fadeRate >= 1f) isFadeIn = false;
		} else {
			fadeRate -= 0.01;
			if (fadeRate <= 0.3f) isFadeIn = true;
		}
		fadeRoate += 0.1f;
		partList.add(new Part());
		Iterator<Part> iter = partList.iterator();
		while (iter.hasNext()) {
			Part p = iter.next();
			if (p.lifeTime < 0) iter.remove();
			else p.update();
		}
	}

	/** 画一个遮罩 */
	private void drawMask(float partialTicks) {
//		GlStateManager.clearColor(1, 1, 1, 1);
//		GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);
		int size = Math.min(mc.displayWidth, mc.displayHeight) / 2;

		for (Part p : partList) p.draw();
		GlStateManager.pushMatrix();
		GlStateManager.color(1, 1, 1, fadeRate);
		GlStateManager.rotate(fadeRoate, 0, 0, 1);
		MASK_OUTER.bind();
		renderRect(size);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.popMatrix();

		MASK_CENTER.bind();
		renderRect(size);
	}

	void renderRect(int size) {
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(-size, -size, 0).tex(0, 1).endVertex();
		bufferbuilder.pos(-size, size, 0).tex(0, 0).endVertex();
		bufferbuilder.pos(size, size, 0).tex(1, 0).endVertex();
		bufferbuilder.pos(size, -size, 0).tex(1, 1).endVertex();
		tessellator.draw();
	}

	private void drawSceneAll() {
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		float hWidth = mc.displayWidth / 2f;
		float hHeight = mc.displayHeight / 2f;
		bufferbuilder.pos(-hWidth, -hHeight, 0).tex(0, 1).endVertex();
		bufferbuilder.pos(-hWidth, hHeight, 0).tex(0, 0).endVertex();
		bufferbuilder.pos(hWidth, hHeight, 0).tex(1, 0).endVertex();
		bufferbuilder.pos(hWidth, -hHeight, 0).tex(1, 1).endVertex();
		tessellator.draw();
	}

	/** 画一次场景 */
	private void renderScene(int s) {
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		float f;
		if (mc.displayWidth > mc.displayHeight) {
			f = (1 - (mc.displayHeight / (float) mc.displayWidth)) / 2;
			bufferbuilder.pos(-s, -s, 0).tex(f, 1).endVertex();
			bufferbuilder.pos(-s, s, 0).tex(f, 0).endVertex();
			bufferbuilder.pos(s, s, 0).tex(1 - f, 0).endVertex();
			bufferbuilder.pos(s, -s, 0).tex(1 - f, 1).endVertex();
		} else {
			f = (1 - (mc.displayWidth / (float) mc.displayHeight)) / 2;
			bufferbuilder.pos(-s, -s, 0).tex(0, 1 - f).endVertex();
			bufferbuilder.pos(-s, s, 0).tex(0, f).endVertex();
			bufferbuilder.pos(s, s, 0).tex(1, f).endVertex();
			bufferbuilder.pos(s, -s, 0).tex(1, 1 - f).endVertex();
		}
		tessellator.draw();
	}

	/** 绘制传送门 */
	@Override
	public void render(EntityPortal entity) {
		if (this.isDispose()) return;
		portal.bindTexture();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.scale(0.15, 0.15, 0.15);
		GlStateManager.alphaFunc(516, 0.02F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableLighting();
		GlStateManager.rotate(-getRaw(), 0, 1, 0);
		renderScene(-16);
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.alphaFunc(516, 0.1F);
	}

	public void checkAndResize() {
		if (originWidth != mc.displayWidth || originHeight != mc.displayHeight) {
			originWidth = mc.displayWidth;
			originHeight = mc.displayHeight;
			worldScene.resize(mc.displayWidth, mc.displayHeight);
			portal.resize(mc.displayWidth, mc.displayHeight);
		}
	}

	private int end() {
		isEnd = true;
		return END;
	}

	public void toTick() {
		if (!isEnd) return;
		EventClient.addRenderTask(this);
		isEnd = false;
	}

	@Override
	public boolean isDispose() {
		return worldScene == null;
	}

	@Override
	public void dispose() {
		if (this.worldScene == null) return;
		this.worldScene.dispose();
		this.worldScene = null;
		this.portal.dispose();
		this.portal = null;
	}
}
