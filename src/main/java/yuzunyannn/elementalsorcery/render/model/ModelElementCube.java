package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelElementCube extends ModelBase {

	ModelRenderer core;
	ModelRenderer a;
	ModelRenderer b;

	public ModelElementCube() {
		textureWidth = 64;
		textureHeight = 64;

		core = new ModelRenderer(this, 0, 0);
		core.addBox(-8F, -8F, -8F, 16, 16, 16);

		a = new ModelRenderer(this, 0, 32);
		a.addBox(-4F, -4F, -4F, 8, 8, 8);

		b = new ModelRenderer(this, 32, 32);
		b.addBox(-4F, -4F, -4F, 8, 8, 8);
	}

	public void render(Entity entity, float rate, float tick, float f2, float f3, float f4, float scaleFactor) {

		float or = 1 - rate;
		float nr = rate;
		float yoff = 14 * nr;

		if (nr > 0) {

			float r = 24;
			float dTheta = 6.2831853f / 12;
			float t1 = tick / 65;
			t1 = t1 - MathHelper.floor(t1 / 6.2831853f) * 6.2831853f;
			t1 *= nr;

			float xr = 0;
			float zr = 0;

			core.setRotationPoint(0, MathHelper.sin(t1 + 3.1415926f / 7) * 2 + yoff, 0);
			core.rotateAngleX = MathHelper.sin(t1) * nr;
			core.rotateAngleZ = MathHelper.cos(t1) * nr;

			core.render(scaleFactor);

			for (int y = 0; y < 4; y++) {

				float theta = MathHelper.cos(y * 3.1415926f / 3) * t1;
				if (theta < 0) theta = theta + MathHelper.ceil(-theta / 6.2831853f) * 6.2831853f;

				float cos1 = MathHelper.cos(t1 * 2 + y * 3.1415926f / 2) * 0.75f;
				r = (MathHelper.sin(y * 3.1415926f / 3 + cos1) + 1) * (4 + cos1) + 16;

				for (int x = -1; x <= 1; x += 2) {
					for (int z = -1; z <= 1; z += 2) {
						xr = MathHelper.sin(theta) * r * 1.5f;
						zr = MathHelper.cos(theta) * r * 1.5f;
						a.setRotationPoint(4 * x * or + xr * nr, y * 8 - 12 + yoff, 12 * z * or + zr * nr);
						a.rotateAngleY = theta * nr;
						a.render(scaleFactor);
						theta = theta + dTheta;
						xr = MathHelper.sin(theta) * r * 1.25f;
						zr = MathHelper.cos(theta) * r * 1.25f;
						a.setRotationPoint(12 * x * or + xr * nr, y * 8 - 12 + yoff, 4 * z * or + zr * nr);
						a.rotateAngleY = theta * nr;
						a.render(scaleFactor);
						theta = theta + dTheta;
						xr = MathHelper.sin(theta) * r;
						zr = MathHelper.cos(theta) * r;
						b.setRotationPoint(x * 12 * or + xr * nr, y * 8 - 12 + yoff, z * 12 * or + zr * nr);
						b.rotateAngleY = theta * 1.5f * nr;
						b.render(scaleFactor);
						theta = theta + dTheta;
					}
				}
			}

			float yr = (MathHelper.cos(t1) + 1) * 3 + 8;
			float theta = t1;
			if (theta < 0) theta = theta + MathHelper.ceil(-theta / 6.2831853f) * 6.2831853f;
			nr = nr * nr;

			b.rotateAngleY = theta * 1.5f * nr;
			dTheta = 6.2831853f / 4;
			r = 32;
			for (int x = -1; x <= 1; x += 2) {
				for (int z = -1; z <= 1; z += 2) {
					xr = MathHelper.sin(theta) * r;
					zr = MathHelper.cos(theta) * r;
					b.setRotationPoint(x * 4 * or + xr * nr, 12 + nr * yr + yoff, z * 4 * or + zr * nr);
					b.render(scaleFactor);
					b.setRotationPoint(x * 4 * or + xr * nr, -12 - nr * yr + yoff, z * 4 * or + zr * nr);
					b.render(scaleFactor);
					theta = theta + dTheta;
				}
			}

			return;
		}

		a.rotateAngleY = 0;
		b.rotateAngleY = 0;
		for (int y = 0; y < 4; y++) {
			for (int x = -1; x <= 1; x += 2) {
				for (int z = -1; z <= 1; z += 2) {
					a.setRotationPoint(4 * x, y * 8 - 12 + yoff, 12 * z);
					a.render(scaleFactor);
					a.setRotationPoint(12 * x, y * 8 - 12 + yoff, 4 * z);
					a.render(scaleFactor);
					b.setRotationPoint(x * 12, y * 8 - 12 + yoff, z * 12);
					b.render(scaleFactor);
				}
			}
		}
		for (int x = -1; x <= 1; x += 2) {
			for (int z = -1; z <= 1; z += 2) {
				b.setRotationPoint(x * 4, 12 + yoff, z * 4);
				b.render(scaleFactor);
				b.setRotationPoint(x * 4, -12 + yoff, z * 4);
				b.render(scaleFactor);
			}
		}
	}
}
