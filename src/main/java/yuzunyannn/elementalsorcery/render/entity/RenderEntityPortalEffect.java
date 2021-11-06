package yuzunyannn.elementalsorcery.render.entity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.entity.EntityPortal;
import yuzunyannn.elementalsorcery.render.effect.StructElement2D;
import yuzunyannn.elementalsorcery.util.helper.ColorHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

public class RenderEntityPortalEffect implements EntityPortal.IPortalDraw {

	final boolean isRenderParticle;

	public RenderEntityPortalEffect(boolean openParticle) {
		this.isRenderParticle = openParticle;
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isDispose() {
		return false;
	}

	public static final TextureBinder APERTURE = new TextureBinder("textures/effect/aperture.png");
	public static final TextureBinder LIGHT = new TextureBinder("textures/effect/light.png");

	@Override
	public void render(EntityPortal entity) {
		Minecraft mc = Minecraft.getMinecraft();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.disableLighting();

		Vec3d tar = entity.getPositionVector().subtract(mc.player.getPositionVector()).normalize();
		double raw = MathHelper.atan2(tar.z, tar.x);
		raw = raw / Math.PI * 180 - 90;
		GlStateManager.rotate((float) -raw, 0, 1, 0);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

		if (isRenderParticle) {
			GlStateManager.depthMask(false);
			for (Part p : partList) p.render(mc.getRenderPartialTicks(), bufferbuilder, tessellator);
			GlStateManager.depthMask(true);
		} else {
			APERTURE.bind();
			GlStateManager.color(204f / 255, 0f / 255, 250f / 255, 1);
			StructElement2D.renderRect(bufferbuilder, 1.75f);
			tessellator.draw();
		}
		GlStateManager.enableAlpha();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	@Override
	public void tick(EntityPortal entity) {
		partList.add(new Part(1));
		partList.add(new Part(0));
		if (lightCount < 45) partList.add(new PartLight());
		Iterator<Part> iter = partList.iterator();
		while (iter.hasNext()) {
			Part p = iter.next();
			if (p.lifeTime < 0) {
				p.dead();
				iter.remove();
			} else p.update();
		}
	}

	public final Random rand = new Random();
	public float circle = 0;

	public class Part extends StructElement2D {
		public float vx, vy;
		public boolean isFadeIn = false;
		static final float range = 1.5f;

		public Part(int mode) {
			drawSize = 0.15f;
			Vec3d color = ColorHelper.color(0xcc00fa);
			r = (float) color.x;
			g = (float) color.y;
			b = (float) color.z;
			if (mode == -1) return;
			if (mode == 1) {
				circle += rand.nextFloat() * 0.3f + 0.2f;
				x = MathHelper.cos(circle) * range;
				y = MathHelper.sin(circle) * range;
				Vec3d vec = new Vec3d(-y, x, 0).normalize().scale(0.02f);
				vx = (float) vec.x;
				vy = (float) vec.y;
				this.lifeTime = 75;
				float c = rand.nextFloat() * 0.6f;
				r += c;
				g += c;
				b += c;
			} else {
				float circle = rand.nextFloat() * 3.14f * 2;
				x = MathHelper.cos(circle) * range;
				y = MathHelper.sin(circle) * range;
				Vec3d vec = new Vec3d(-y, x, 0).normalize().scale(0.035f);
				vx = (float) vec.x;
				vy = (float) vec.y;
				this.lifeTime = rand.nextInt(20) + 60;
			}
		}

		public void update() {
			this.updatePrev();
			this.lifeTime--;
			Vec3d a = new Vec3d(-x, -y, 0).normalize().normalize().scale(0.001f);
			vx += a.x;
			vy += a.y;
			x += vx;
			y += vy;
			if (lifeTime <= 20) alpha = Math.max(0, alpha - 0.05f);
			else if (isFadeIn) {
				scale += 0.02;
				alpha += 0.025f;
				if (alpha >= 1) isFadeIn = false;
			} else {
				scale -= 0.02;
				alpha -= 0.025f;
				if (alpha <= 0.3f) isFadeIn = true;
			}
		}

		public void dead() {

		}
	}

	int lightCount = 0;

	public class PartLight extends Part {

		public PartLight() {
			super(-1);
			lightCount++;
			this.lifeTime = rand.nextInt(60) + 60;
			alpha = 0;
			vx = rand.nextFloat() * 2 + 0.5f;
			if (rand.nextInt(2) == 0) vx = -vx;
			x = rand.nextFloat() * 360;

			float c = rand.nextFloat() * 0.5f;
			r += c;
			g += c;
			b += c;
		}

		@Override
		public void dead() {
			lightCount--;
		}

		@Override
		public void update() {
			this.updatePrev();
			this.lifeTime--;
			this.x += vx;
			if (lifeTime <= 20) alpha = Math.max(0, alpha - 0.05f);
			else if (isFadeIn) {
				alpha += 0.01f;
				if (alpha >= 1f) isFadeIn = false;
			} else {
				alpha -= 0.01f;
				if (alpha <= 0.6f) isFadeIn = true;
			}
		}

		@Override
		public void render(float partialTicks, BufferBuilder bufferbuilder, Tessellator tessellator) {
			LIGHT.bind();
			GlStateManager.pushMatrix();
			GlStateManager.color(r, g, b, alpha);
			GlStateManager.rotate(x, 0, 0, 1);
			renderRect(bufferbuilder, 2f);
			tessellator.draw();
			GlStateManager.popMatrix();
		}

	}

	public List<Part> partList = new LinkedList<>();

}
