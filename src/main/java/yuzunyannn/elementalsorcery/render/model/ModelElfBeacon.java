package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelElfBeacon extends ModelBase {

	private final ModelRenderer b1;
	private final ModelRenderer t0;
	private final ModelRenderer b2;

	public ModelElfBeacon() {
		textureWidth = 128;
		textureHeight = 128;

		b1 = new ModelRenderer(this);
		b1.setRotationPoint(0.0F, 7.0F + 6, 0.0F);
		b1.cubeList.add(new ModelBox(b1, 95, 37, -7.0F, -11.0F, 7.0F, 7, 7, 7, 0.0F, false));
		b1.cubeList.add(new ModelBox(b1, 66, 37, -14.0F, -11.0F, 0.0F, 7, 7, 7, 0.0F, false));
		b1.cubeList.add(new ModelBox(b1, 37, 37, -14.0F, -11.0F, 7.0F, 7, 7, 7, 0.0F, false));

		t0 = new ModelRenderer(this);
		t0.setRotationPoint(0.0F, 24.0F + 6, 0.0F);
		t0.cubeList.add(new ModelBox(t0, 0, 0, -16.0F, -32.0F, -16.0F, 32, 4, 32, 0.0F, false));

		b2 = new ModelRenderer(this);
		b2.setRotationPoint(0.0F, -3.0F + 6, 0.0F);
		b2.cubeList.add(new ModelBox(b2, 0, 53, -7.0F, -1.0F, -7.0F, 14, 14, 14, 0.0F, false));
		b2.cubeList.add(new ModelBox(b2, 58, 61, -5.0F, 1.0F, -5.0F, 10, 10, 10, 0.0F, false));
	}

	public void render(Entity entity, float rate, float roateTick, float f2, float f3, float f4, float scaleFactor) {
		float srate = MathHelper.sin(MathHelper.clamp(rate, 0, 1) * 3.14f / 2);
		float cosTick = MathHelper.cos(roateTick) * srate;
		float cosTick2 = MathHelper.cos(roateTick + 3.1415f / 4) * srate;
		float cosTick3 = MathHelper.cos(roateTick + 3.1415f / 3) * srate;
		t0.render(scaleFactor);
		for (int n = 0; n < 4; n++) {
			float r = n * 3.1415926F / 2;
			b1.rotateAngleY = r;
			b1.offsetY = srate * 3 + cosTick * 1.5f;
			ModelTranscribeInjection.setRotatOffset(b1, srate * 6 + cosTick3 * 2, true);
			b1.render(scaleFactor);
		}
		b2.offsetY = srate * 10 + cosTick2 * 1f;
		b2.render(scaleFactor);
	}

}
