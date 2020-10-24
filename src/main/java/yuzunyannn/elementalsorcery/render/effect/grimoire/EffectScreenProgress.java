package yuzunyannn.elementalsorcery.render.effect.grimoire;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.render.effect.StructElement2D;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

public class EffectScreenProgress extends EffectScreen {

	public static final TextureBinder TEXTURE = new TextureBinder("textures/gui/mantra/component.png");

	public EffectScreenProgress(World world) {
		super(world);
	}

	public float r = 0;
	public float g = 0;
	public float b = 0;

	boolean isShow = true;
	float alpha = 0;
	float preAlpha = alpha;
	float progress = 0;
	float preProgress = progress;
	boolean setProgressInTick = false;

	public void setColor(int c) {
		r = ((c >> 16) & 0xff) / 255f;
		g = ((c >> 8) & 0xff) / 255f;
		b = ((c >> 0) & 0xff) / 255f;
	}

	public void setProgress(float progress) {
		setProgressInTick = true;
		preProgress = this.progress;
		this.progress = Math.min(1, progress);
		if (this.progress >= 1) this.isShow = false;
		else this.isShow = true;
	}

	@Override
	public void onUpdate() {
		this.updateSize();
		preAlpha = alpha;
		isShow = isShow && !this.isEnd();
		if (isShow) {
			this.lifeTime = 10;
			this.alpha = Math.min(1, alpha + 0.1f);
		} else {
			this.alpha = Math.max(0, alpha - 0.1f);
			if (this.isEnd()) this.lifeTime--;
		}
		if (setProgressInTick) setProgressInTick = false;
		else preProgress = this.progress;
		Iterator<Part> iter = list.iterator();
		while (iter.hasNext()) {
			Part part = iter.next();
			part.update();
			if (part.lifeTime <= 0) iter.remove();
		}
		if (progress > 0.98f) return;
		float originX = width / 2 - 256 / 2;
		float x = originX + 256 * progress + 0.5f;
		float y = height - 50 + 6.3f + rand.nextFloat() - 0.5f;
		for (int i = 0; i < 2; i++) {
			Part part = new Part();
			part.setPosition(x, y);
			part.updatePrev();
			part.setColor(r, g, b);
			list.add(part);
		}
	}

	@Override
	protected void doRender(float partialTicks) {
		if (mc.gameSettings.thirdPersonView != 0) return;
		TEXTURE.bind();
		float alpha = RenderHelper.getPartialTicks(this.alpha, this.preAlpha, partialTicks);
		float progress = RenderHelper.getPartialTicks(this.progress, this.preProgress, partialTicks);
		GlStateManager.color(r, g, b, alpha);
		RenderHelper.drawTexturedModalRect(width / 2 - 256 / 2, height - 50, 0, 0, 5 + 251 * progress, 13, 256, 256);
		GlStateManager.depthMask(false);
		GlStateManager.translate(0, 0, 1);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		for (Part part : list) part.render(partialTicks, bufferbuilder, tessellator);
		GlStateManager.depthMask(true);
	}

	List<Part> list = new LinkedList<>();

	private class Part extends StructElement2D {

		public float tR, tG, tB;
		public float vx, vy;
		public float myAlpha = 1;

		public Part() {
			drawSize = 3;
			scale = rand.nextFloat() * 1 + 1.5f;
			this.lifeTime = 30;
			this.alpha = myAlpha * EffectScreenProgress.this.alpha;
			vy = rand.nextFloat() * 2 - 1;
			this.updatePrev();
		}

		@Override
		public void setColor(float r, float g, float b) {
			tR = r;
			tB = b;
			tG = g;
			this.r = Math.min(1, tR + 0.25f);
			this.g = Math.min(1, tG + 0.25f);
			this.b = Math.min(1, tB + 0.25f);
		}

		public void update() {
			this.updatePrev();
			this.lifeTime--;
			x += vx;
			y += vy;
			scale -= 0.05f;
			myAlpha = this.lifeTime / 30.0f;
			this.alpha = myAlpha * EffectScreenProgress.this.alpha;
			r = r + (tR - r) * 0.1f;
			g = g + (tG - g) * 0.1f;
			b = b + (tB - b) * 0.1f;
		}

	}

}
