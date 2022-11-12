package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.mantra.IProgressable;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.render.effect.gui.GUIEffectBatch;
import yuzunyannn.elementalsorcery.render.effect.gui.GUIEffectBatchList;
import yuzunyannn.elementalsorcery.util.helper.Color;

@SideOnly(Side.CLIENT)
public class EffectScreenProgress extends EffectScreen implements IProgressable {

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

	@Override
	public void setProgress(double progress) {
		setProgressInTick = true;
		preProgress = this.progress;
		this.progress = (float) Math.min(1, progress);
		if (this.progress >= 1) this.isShow = false;
		else this.isShow = true;
	}

	@Override
	public double getProgress() {
		return this.progress;
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
		guiEffectList.update();
		if (progress > 0.98f) return;
		float originX = width / 2 - 256 / 2;
		float x = originX + 256 * progress + 0.5f;
		float y = height - 50 + 6.3f + rand.nextFloat() - 0.5f;
		for (int i = 0; i < 2; i++) {
			Part part = new Part();
			part.setPosition(x, y);
			part.setColor(r, g, b);
			guiEffectList.add(part);
		}
	}

	@Override
	protected void doRender(float partialTicks) {
		if (mc.gameSettings.thirdPersonView != 0) return;
		TEXTURE.bind();
		GlStateManager.depthMask(false);
		float alpha = RenderFriend.getPartialTicks(this.alpha, this.preAlpha, partialTicks);
		float progress = RenderFriend.getPartialTicks(this.progress, this.preProgress, partialTicks);
		GlStateManager.color(r, g, b, alpha);
		RenderFriend.drawTexturedModalRect(width / 2 - 256 / 2, height - 50, 0, 0, 5 + 251 * progress, 13, 256, 256);
		GlStateManager.translate(0, 0, 1);
		guiEffectList.render(partialTicks);
		GlStateManager.depthMask(true);
	}

	protected GUIEffectBatchList<Part> guiEffectList = new GUIEffectBatchList<>();

	protected class Part extends GUIEffectBatch {

		public final Color tColor = new Color();
		public float vx, vy;
		public float myAlpha = 1;

		public Part() {
			drawSize = 3;
			scale = rand.nextFloat() * 1 + 1.5f;
			lifeTime = 30;
			prevAlpha = alpha = myAlpha * EffectScreenProgress.this.alpha;
			vy = rand.nextFloat() * 2 - 1;
		}

		public void setColor(float r, float g, float b) {
			tColor.setColor(r, g, b);
			color.setColor(tColor).add(0.25f);
		}

		public void update() {
			super.update();
			this.lifeTime--;
			x += vx;
			y += vy;
			scale -= 0.05f;
			myAlpha = lifeTime / 30.0f;
			alpha = myAlpha * EffectScreenProgress.this.alpha;
			color.r = color.r + (tColor.r - color.r) * 0.1f;
			color.g = color.g + (tColor.g - color.g) * 0.1f;
			color.b = color.b + (tColor.b - color.b) * 0.1f;
		}

	}

}
