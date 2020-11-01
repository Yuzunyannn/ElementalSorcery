package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelTranscribeInjection extends ModelBase {

	protected final ModelRenderer u1;
	protected final ModelRenderer u11;
	protected final ModelRenderer u12;
	protected final ModelRenderer u2;
	protected final ModelRenderer u21;
	protected final ModelRenderer u22;
	protected final ModelRenderer u3;
	protected final ModelRenderer u31;
	protected final ModelRenderer u32;
	protected final ModelRenderer u4;
	protected final ModelRenderer b1;
	protected final ModelRenderer b2;
	protected final ModelRenderer b3;
	protected final ModelRenderer b4;
	protected final ModelRenderer b5;
	protected final ModelRenderer t0;

	public ModelTranscribeInjection() {
		textureWidth = 128;
		textureHeight = 128;

		u1 = new ModelRenderer(this);
		u1.setRotationPoint(0.0F, 3.0F, 0.0F);

		u11 = new ModelRenderer(this);
		u11.setRotationPoint(0.0F, 0.0F, 0.0F);
		u1.addChild(u11);
		u11.cubeList.add(new ModelBox(u11, 0, 78, 10.0F, -1.0F, -10.0F, 4, 1, 20, 0.0F, false));
		u11.cubeList.add(new ModelBox(u11, 21, 65, -14.0F, -1.0F, 10.0F, 28, 1, 4, 0.0F, true));

		u12 = new ModelRenderer(this);
		u12.setRotationPoint(0.0F, 0.0F, 0.0F);
		u1.addChild(u12);
		u12.rotateAngleY = 3.1416F;
		u12.cubeList.add(new ModelBox(u12, 0, 78, 10.0F, -1.0F, -10.0F, 4, 1, 20, 0.0F, false));
		u12.cubeList.add(new ModelBox(u12, 21, 65, -14.0F, -1.0F, 10.0F, 28, 1, 4, 0.0F, true));

		u2 = new ModelRenderer(this);
		u2.setRotationPoint(0.0F, 3.0F, 0.0F);

		u21 = new ModelRenderer(this);
		u21.setRotationPoint(0.0F, 0.0F, 0.0F);
		u2.addChild(u21);
		u21.cubeList.add(new ModelBox(u21, 45, 76, -10.0F, -1.0F, -10.0F, 3, 1, 20, 0.0F, false));
		u21.cubeList.add(new ModelBox(u21, 50, 71, -7.0F, -1.0F, 7.0F, 14, 1, 3, 0.0F, false));

		u22 = new ModelRenderer(this);
		u22.setRotationPoint(0.0F, 0.0F, 0.0F);
		u2.addChild(u22);
		u22.rotateAngleY = 3.1416F;
		u22.cubeList.add(new ModelBox(u22, 45, 76, -10.0F, -1.0F, -10.0F, 3, 1, 20, 0.0F, false));
		u22.cubeList.add(new ModelBox(u22, 50, 71, -7.0F, -1.0F, 7.0F, 14, 1, 3, 0.0F, false));

		u3 = new ModelRenderer(this);
		u3.setRotationPoint(0.0F, 3.0F, 0.0F);

		u31 = new ModelRenderer(this);
		u31.setRotationPoint(0.0F, 0.0F, 0.0F);
		u3.addChild(u31);
		u31.cubeList.add(new ModelBox(u31, 97, 55, 4.0F, -1.0F, -4.0F, 3, 1, 8, 0.0F, false));
		u31.cubeList.add(new ModelBox(u31, 93, 50, -7.0F, -1.0F, 4.0F, 14, 1, 3, 0.0F, false));

		u32 = new ModelRenderer(this);
		u32.setRotationPoint(0.0F, 0.0F, 0.0F);
		u3.addChild(u32);
		u32.rotateAngleY = 3.1416F;
		u32.cubeList.add(new ModelBox(u32, 97, 55, 4.0F, -1.0F, -4.0F, 3, 1, 8, 0.0F, false));
		u32.cubeList.add(new ModelBox(u32, 93, 50, -7.0F, -1.0F, 4.0F, 14, 1, 3, 0.0F, false));

		u4 = new ModelRenderer(this);
		u4.setRotationPoint(0.0F, 3.0F, 0.0F);
		u4.cubeList.add(new ModelBox(u4, 96, 65, -4.0F, -1.0F, -4.0F, 8, 1, 8, 0.0F, false));

		b1 = new ModelRenderer(this);
		b1.setRotationPoint(0.0F, 34.0F, 0.0F);
		b1.cubeList.add(new ModelBox(b1, 95, 35, -7.0F, -11.0F, 7.0F, 7, 7, 7, 0.0F, false));
		b1.cubeList.add(new ModelBox(b1, 66, 35, -14.0F, -11.0F, 0.0F, 7, 7, 7, 0.0F, false));
		b1.cubeList.add(new ModelBox(b1, 37, 35, -14.0F, -11.0F, 7.0F, 7, 7, 7, 0.0F, false));

		b2 = new ModelRenderer(this);
		b2.setRotationPoint(0.0F, 34.0F, 0.0F);
		b2.cubeList.add(new ModelBox(b2, 38, 50, -7.0F, -11.0F, 0.0F, 7, 7, 7, 0.0F, false));

		b3 = new ModelRenderer(this);
		b3.setRotationPoint(0.0F, 34.0F, 0.0F);
		b3.cubeList.add(new ModelBox(b3, 0, 35, -4.0F, -24.0F, 4.0F, 8, 13, 10, 0.0F, false));

		b4 = new ModelRenderer(this);
		b4.setRotationPoint(0.0F, 34.0F, 0.0F);
		b4.cubeList.add(new ModelBox(b4, 0, 59, -14.0F, -24.0F, 9.0F, 5, 13, 5, 0.0F, false));

		b5 = new ModelRenderer(this);
		b5.setRotationPoint(0.0F, 34.0F, 0.0F);
		b5.cubeList.add(new ModelBox(b5, 0, 59, -9.0F, -24.0F, 9.0F, 5, 13, 5, 0.0F, false));
		b5.cubeList.add(new ModelBox(b5, 0, 59, -14.0F, -24.0F, 4.0F, 5, 13, 5, 0.0F, false));

		t0 = new ModelRenderer(this);
		t0.setRotationPoint(0.0F, 32.0F, 0.0F);
		t0.cubeList.add(new ModelBox(t0, 0, 0, -16.0F, -2.0F, -16.0F, 32, 2, 32, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float rate, float roateTick, float limbSwingAmount, float netHeadYaw,
			float headPitch, float scaleFactor) {
		float srate = MathHelper.sin(MathHelper.clamp(rate, 0, 1) * 3.14f / 2);
		float arate = Math.max(0, srate - 0.2f) * 10 / 8;
		float brate = Math.max(0, srate - 0.4f) * 10 / 6;
		float crate = Math.max(0, srate - 0.6f) * 10 / 4;
//		arate = arate * arate;
//		brate = brate * brate;
//		crate = crate * crate;

		float cosTick = MathHelper.cos(roateTick) * srate;
		float cosTick2 = MathHelper.cos(roateTick + 3.1415f / 4) * srate;
		float cosTick3 = MathHelper.cos(roateTick + 3.1415f / 3) * srate;

		for (int n = 0; n < 4; n++) {
			float r = n * 3.1415926F / 2;
			b1.rotateAngleY = r;
			b2.rotateAngleY = r;
			b3.rotateAngleY = r;
			b4.rotateAngleY = r;
			b5.rotateAngleY = r;
			for (int i = 0; i < 2; i++) {
				float y = 34.0F - 20 * i;
				b1.setRotationPoint(0, y, 0);
				b2.setRotationPoint(0, y, 0);

				if (i == 1) {
					b1.offsetY = -brate * 5 + cosTick2 * 0.5f;
					b2.offsetY = crate * 3 + cosTick3 * 1f;
				} else {
					b1.offsetY = -cosTick2 * 0.2f;
					b2.offsetY = -crate * 3 - cosTick3 * 1f;
				}
				setRotatOffset(b1, brate * 12, true);
				setRotatOffset(b2, crate * 10, true);
				b1.render(scaleFactor);
				b2.render(scaleFactor);
			}

			b3.offsetY = -srate * 2.5f + cosTick2 * 1f;
			setRotatOffset(b3, srate * 16 + cosTick * 0.5f, false);
			b3.render(scaleFactor);

			b4.offsetY = -arate * 2.5f + cosTick * 1.5f;
			setRotatOffset(b4, arate * 20 + cosTick3 * 0.5f, true);
			b4.render(scaleFactor);

			b5.offsetY = -crate * 2.5f - cosTick * 0.5f;
			setRotatOffset(b5, crate * 12 + cosTick2 * 0.5f, true);
			b5.render(scaleFactor);
		}

		u1.rotateAngleY = 3.1415f / 4 * brate + cosTick * 0.2f;
		u1.offsetY = -brate * 6 - cosTick2 * 0.25f;
		u1.render(scaleFactor);

		u2.rotateAngleY = 3.1415f / 3 * brate - cosTick2 * 0.2f;
		u2.offsetY = u1.offsetY - brate * 3 + cosTick * 0.5f;
		u2.render(scaleFactor);

		u3.rotateAngleY = 3.1415f / 2 * brate + cosTick2 * 0.2f;
		u3.offsetY = u2.offsetY - brate * 3 + cosTick3 * 0.75f;
		u3.render(scaleFactor);

		u4.rotateAngleY = 3.1415f / 1 * brate - cosTick * 0.2f;
		u4.offsetY = u3.offsetY - brate * 3 - cosTick * 1f;
		u4.render(scaleFactor);

		t0.render(scaleFactor);

	}

	public static void setRotatOffset(ModelRenderer mr, float offset, boolean isOblique) {
		float rotate = mr.rotateAngleY;
		if (isOblique) rotate -= 3.1415f / 4;
		mr.offsetZ = offset * MathHelper.cos(rotate);
		mr.offsetX = offset * MathHelper.sin(rotate);
	}

}
