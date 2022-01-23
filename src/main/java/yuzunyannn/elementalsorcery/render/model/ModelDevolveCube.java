package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelDevolveCube extends ModelBase {
	private final ModelRenderer b;
	private final ModelRenderer z1;
	private final ModelRenderer a2;

	public ModelDevolveCube() {
		textureWidth = 32;
		textureHeight = 32;

		b = new ModelRenderer(this);
		b.setRotationPoint(0.0F, 24.0F, 0.0F);
		b.cubeList.add(new ModelBox(b, 0, 0, -2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F, false));
		b.cubeList.add(new ModelBox(b, 6, 8, -2.9F, -3.0F, -1.0F, 1, 6, 2, 0.0F, false));
		b.cubeList.add(new ModelBox(b, 6, 8, 1.9F, -3.0F, -1.0F, 1, 6, 2, 0.0F, false));
		b.cubeList.add(new ModelBox(b, 0, 8, -1.0F, -3.0F, 1.9F, 2, 6, 1, 0.0F, false));
		b.cubeList.add(new ModelBox(b, 0, 8, -1.0F, -3.0F, -2.9F, 2, 6, 1, 0.0F, false));

		z1 = new ModelRenderer(this);
		z1.setRotationPoint(0.0F, 24.0F, 0.0F);
		z1.cubeList.add(new ModelBox(z1, 0, 16, 5.0F, -2.0F, -8.0F, 3, 3, 3, 0.0F, false));
		z1.cubeList.add(new ModelBox(z1, 0, 22, 5.0F, -2.0F, 5.0F, 3, 3, 3, 0.0F, false));
		z1.cubeList.add(new ModelBox(z1, 12, 17, 5.5F, -1.5F, 1.0F, 2, 2, 5, 0.0F, false));
		z1.cubeList.add(new ModelBox(z1, 12, 10, 5.5F, -1.5F, -6.0F, 2, 2, 5, 0.0F, false));

		a2 = new ModelRenderer(this);
		a2.setRotationPoint(-1.0F, 0.0F, 0.0F);
		z1.addChild(a2);
		a2.rotateAngleY = -1.5708F;
		a2.cubeList.add(new ModelBox(a2, 12, 10, -7.5F, -1.5F, -7.0F, 2, 2, 5, 0.0F, false));
		a2.cubeList.add(new ModelBox(a2, 12, 17, 5.5F, -1.5F, -7.0F, 2, 2, 5, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float tick, float f3, float f4, float scale) {

		float sin = MathHelper.sin(tick / 50);
		float cos = MathHelper.cos(tick / 50);

		b.setRotationPoint(0, 8 + sin * sin * 1, 0);
		b.rotateAngleY = MathHelper.sin(tick / 500) * 3.14f * 2f;
		b.render(scale);

		float rate = tick / 5;

		z1.rotateAngleY = rate + sin;
		z1.setRotationPoint(0, 8 + sin * 3, 0);
		z1.render(scale);

//		z1.rotateAngleY = z1.rotateAngleY + 3.1415926f;

		z1.rotateAngleY = rate + 3.14f * cos;
		z1.setRotationPoint(0, 8 - sin * 3, 0);
		z1.render(scale);
	}

}
