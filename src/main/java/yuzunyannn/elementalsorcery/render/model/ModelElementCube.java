package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import yuzunyannn.elementalsorcery.util.math.MathSupporter;

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

	public void render(Entity entity, float rate, float rotation, float f2, float f3, float f4, float scaleFactor) {

		rate = (float) MathSupporter.easeOutBack(rate);
		float or = 1 - rate;
		float nr = rate;
		float yoff = 14 * nr;

		if (nr > 0) {

			final float startLen = 20;
			final float startLenZ = 8;
			final float startRatio = 0.3f;
			float r = 24;
			rotation = rotation / 180 * 3.1415926f;

			core.setRotationPoint(0, MathHelper.sin(rotation) * 2 + yoff, 0);
			core.rotateAngleX = MathHelper.sin(rotation) * nr;
			core.rotateAngleZ = MathHelper.cos(rotation) * nr;

			core.render(scaleFactor);

			// part1 展开
			if (nr < startRatio) {
				nr = rate / startRatio;
				or = 1 - nr;
				b.rotateAngleY = 0;
				a.rotateAngleY = 0;
				for (int y = 0; y < 4; y++) {
					float sl = startLen + MathHelper.abs(y - 2);

					for (int x = -1; x <= 1; x += 2) {
						for (int z = -1; z <= 1; z += 2) {
							b.setRotationPoint(x * 12 * or + x * sl * nr, y * 8 - 12 + yoff, z * 12 * or + z * sl * nr);
							b.render(scaleFactor);
							a.setRotationPoint(12 * x * or + x * sl * nr, y * 8 - 12 + yoff, 4 * z);
							a.render(scaleFactor);
							a.setRotationPoint(4 * x, y * 8 - 12 + yoff, 12 * z * or + z * sl * nr);
							a.render(scaleFactor);
						}
					}
				}

				for (int x = -1; x <= 1; x += 2) {
					for (int z = -1; z <= 1; z += 2) {
						b.setRotationPoint(x * 4, 12 + nr * startLenZ + yoff, z * 4);
						b.render(scaleFactor);
						b.setRotationPoint(x * 4, -12 - nr * startLenZ + yoff, z * 4);
						b.render(scaleFactor);
					}
				}
			} else {
				// part1 旋转
				float xr = 0;
				float zr = 0;
				float dTheta = 6.2831853f / 12;
				float rDTheta = dTheta / 8;

				nr = (rate - startRatio) / (1 - startRatio);
				or = 1 - nr;

				for (int y = 0; y < 4; y++) {

					float sl = startLen + MathHelper.abs(y - 2);

					float rotationTheta = 0;
					float theta = MathHelper.cos(y * 3.1415926f / 3) * MathHelper.sin(rotation) * 2;
					if (theta < 0) theta = theta + MathHelper.ceil(-theta / 6.2831853f) * 6.2831853f;

					float theta1 = theta;
					float theta2 = theta + dTheta;
					float theta3 = theta - dTheta;

					float cos1 = MathHelper.cos(rotation * 2 + y * 3.1415926f / 2) * 0.75f;
					r = -((MathHelper.sin(y * 3.1415926f / 3 + cos1) + 1) * (4 + cos1) + 16);

					for (int x = -1; x <= 1; x += 2) {
						for (int z = -1; z <= 1; z += 2) {
							xr = MathHelper.sin(theta1) * r;
							zr = MathHelper.cos(theta1) * r;
							b.setRotationPoint(x * sl * or + xr * nr, y * 8 - 12 + yoff, z * sl * or + zr * nr);
							b.rotateAngleY = rotationTheta * nr;
							b.render(scaleFactor);
							theta1 = theta1 + dTheta * 3;
							rotationTheta = rotationTheta + rDTheta;
							xr = MathHelper.sin(theta2) * r * 1.25f;
							zr = MathHelper.cos(theta2) * r * 1.25f;
							a.setRotationPoint(sl * x * or + xr * nr, y * 8 - 12 + yoff, 4 * z * or + zr * nr);
							a.rotateAngleY = rotationTheta * nr;
							a.render(scaleFactor);
							theta2 = theta2 + dTheta;
							rotationTheta = rotationTheta + rDTheta;
							xr = MathHelper.sin(theta3) * r * 1.5f;
							zr = MathHelper.cos(theta3) * r * 1.5f;
							a.setRotationPoint(4 * x * or + xr * nr, y * 8 - 12 + yoff, sl * z * or + zr * nr);
							a.rotateAngleY = rotationTheta * nr;
							a.render(scaleFactor);
							theta3 = theta3 + dTheta * 5;
							rotationTheta = rotationTheta + rDTheta;
						}
						theta1 = theta1 + dTheta * 3;
						theta2 = theta2 + dTheta * 5;
						theta3 = theta3 + dTheta;
						dTheta = -dTheta;
					}
				}

				float yr = (MathHelper.cos(rotation) + 1) * 3 + 8 - startLenZ;
				float theta = rotation;
				if (theta < 0) theta = theta + MathHelper.ceil(-theta / 6.2831853f) * 6.2831853f;
				nr = nr * nr;

				b.rotateAngleY = theta * 1.5f * nr;
				dTheta = 6.2831853f / 4;
				r = -32;
				for (int x = -1; x <= 1; x += 2) {
					for (int z = -1; z <= 1; z += 2) {
						xr = MathHelper.sin(theta) * r;
						zr = MathHelper.cos(theta) * r;
						b.setRotationPoint(x * 4 * or + xr * nr, 12 + startLenZ + nr * yr + yoff, z * 4 * or + zr * nr);
						b.render(scaleFactor);
						b.setRotationPoint(x * 4 * or + xr * nr, -12 - startLenZ - nr * yr + yoff,
								z * 4 * or + zr * nr);
						b.render(scaleFactor);
						theta = theta + dTheta;
					}
					theta = theta + dTheta;
					dTheta = -dTheta;
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
