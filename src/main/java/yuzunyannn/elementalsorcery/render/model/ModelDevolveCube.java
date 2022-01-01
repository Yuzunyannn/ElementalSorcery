package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelDevolveCube extends ModelBase {

	private final ModelRenderer c;

	private final ModelRenderer a1;
	private final ModelRenderer a2;

	private final ModelRenderer z1;
	private final ModelRenderer z2;

	public ModelDevolveCube() {
		textureWidth = 32;
		textureHeight = 32;

		c = new ModelRenderer(this);
		c.cubeList.add(new ModelBox(c, 0, 0, -2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F, false));
		c.cubeList.add(new ModelBox(c, 6, 8, -2.9F, -3.0F, -1.0F, 1, 6, 2, 0.0F, false));
		c.cubeList.add(new ModelBox(c, 6, 8, 1.9F, -3.0F, -1.0F, 1, 6, 2, 0.0F, false));
		c.cubeList.add(new ModelBox(c, 0, 8, -1.0F, -3.0F, 1.9F, 2, 6, 1, 0.0F, false));
		c.cubeList.add(new ModelBox(c, 0, 8, -1.0F, -3.0F, -2.9F, 2, 6, 1, 0.0F, false));

		z1 = new ModelRenderer(this);
		z1.cubeList.add(new ModelBox(z1, 0, 22, 5.0F, -1.5F, -8.0F, 3, 3, 3, 0.0F, false));
		z1.cubeList.add(new ModelBox(z1, 0, 22, -8.0F, -1.5F, 5.0F, 3, 3, 3, 0.0F, false));

		a1 = new ModelRenderer(this);
		a1.setRotationPoint(0.0F, 1.5F, 0.0F);
		z1.addChild(a1);
		a1.rotateAngleY = -0.7854F;
		a1.cubeList.add(new ModelBox(a1, 12, 17, -1.0F, -2.5F, 3.0F, 2, 2, 5, 0.0F, false));
		a1.cubeList.add(new ModelBox(a1, 12, 17, -1.0F, -2.5F, -8.0F, 2, 2, 5, 0.0F, false));

		z2 = new ModelRenderer(this);
		z2.cubeList.add(new ModelBox(z2, 0, 16, 5.0F, -1.5F, -8.0F, 3, 3, 3, 0.0F, false));
		z2.cubeList.add(new ModelBox(z2, 0, 16, -8.0F, -1.5F, 5.0F, 3, 3, 3, 0.0F, false));

		a2 = new ModelRenderer(this);
		a2.setRotationPoint(0.0F, 1.5F, 0.0F);
		z2.addChild(a2);
		a2.rotateAngleY = -0.7854F;
		a2.cubeList.add(new ModelBox(a2, 12, 10, -1.0F, -2.5F, 3.0F, 2, 2, 5, 0.0F, false));
		a2.cubeList.add(new ModelBox(a2, 12, 10, -1.0F, -2.5F, -8.0F, 2, 2, 5, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f0, float f1, float tick, float f3, float f4, float scale) {

		float sin = MathHelper.sin(tick / 50);
		float cos = MathHelper.cos(tick / 50);

		c.setRotationPoint(0, 8 + sin * sin * 1, 0);
		c.rotateAngleY = MathHelper.sin(tick / 500) * 3.14f * 2f;

		float rate = tick / 5;

		z1.rotateAngleY = rate + sin * cos;
		z2.rotateAngleY = rate + 3.14f / 2 * cos + sin;

		z1.setRotationPoint(0, 8 + sin * 3, 0);
		z2.setRotationPoint(0, 8 - sin * 3, 0);

		c.render(scale);
		z1.render(scale);
		z2.render(scale);
	}

}
