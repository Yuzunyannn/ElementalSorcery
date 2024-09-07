package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelElementTerminalCore extends ModelBase {

	ModelRenderer a;
	ModelRenderer b;

	public ModelElementTerminalCore() {
		textureWidth = 8;
		textureHeight = 8;
		a = new ModelRenderer(this, 0, 0);
		a.addBox(-1f, -1f, -1f, 2, 2, 2);
		b = new ModelRenderer(this, 0, 4);
		b.addBox(-1f, -1f, -1f, 2, 2, 2);
	}

	@Override
	public void render(Entity entity, float tick, float srt, float f2, float f3, float f4, float scaleFactor) {
		render(a, tick, srt, f2, f3, f4, scaleFactor);
	}

	public void renderCover(Entity entity, float tick, float srt, float f2, float f3, float f4, float scaleFactor) {
		render(b, tick, srt, f2, f3, f4, scaleFactor);
	}

	protected void render(ModelRenderer renderer, float tick, float srt, float f2, float f3, float f4,
			float scaleFactor) {
		float chaos = 0f;
		final int size = 2;
		final float detla = 2.5f;
		final float A = 19f * srt;
		final float k = chaos * 10;
		final float omega = 0.1f;
		for (int x = -size; x <= size; x++) {
			for (int z = -size; z <= size; z++) {
				if (A > 0) {
					float r = MathHelper.sqrt(x * x + z * z);
					float theta = (float) Math.atan2(z, x);
					float phase = omega * tick - k * r;
					float sin = MathHelper.sin(theta + phase);
					float cos = MathHelper.cos(theta + phase);
					float sinValue = Math.max(0, sin);
					float y1;
					if (cos >= 0) {
						y1 = -A * sinValue;
					} else {
						float sv = 1 - sinValue;
						y1 = -A * (1 - sv * sv);
					}
					float y2 = -Math.abs(MathHelper.sin(omega / 8 * tick + x + z)) * 0.25f;
					renderer.rotationPointY = y1 + y2;
					if (sinValue > 0) renderer.rotateAngleY = cos * 1.575f;
					else renderer.rotateAngleY = 0;

				} else {
					renderer.rotationPointY = 0;
					renderer.rotateAngleY = 0;
				}
				renderer.rotationPointX = x * detla;
				renderer.rotationPointZ = z * detla;
				renderer.render(scaleFactor);
			}
		}

	}
}
