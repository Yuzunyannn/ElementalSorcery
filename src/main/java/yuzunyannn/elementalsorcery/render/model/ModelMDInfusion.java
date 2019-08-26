package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelMDInfusion extends ModelBase {
	ModelRenderer v;
	ModelRenderer t0;
	ModelRenderer t1;
	ModelRenderer t2;
	ModelRenderer t3;

	public ModelMDInfusion() {
		textureWidth = 64;
		textureHeight = 64;

		v = new ModelRenderer(this, 41, 18);
		v.addBox(4F, 0F, 5F, 2, 8, 2);
		v.setRotationPoint(0F, 7F, 0F);
		v.setTextureSize(64, 32);

		t0 = new ModelRenderer(this, 0, 0);
		t0.addBox(-8F, 0F, -8F, 16, 1, 16);
		t0.setRotationPoint(0F, 15F, 0F);
		t0.setTextureSize(64, 32);

		t1 = new ModelRenderer(this, 0, 18);
		t1.addBox(-5F, 0F, -5F, 10, 3, 10);
		t1.setRotationPoint(0F, 12F, 0F);
		t1.setTextureSize(64, 32);

		t2 = new ModelRenderer(this, 0, 32);
		t2.addBox(-4F, 0F, -4F, 8, 2, 8);
		t2.setRotationPoint(0F, 10F, 0F);
		t2.setTextureSize(64, 32);

		t3 = new ModelRenderer(this, 0, 43);
		t3.addBox(-3F, 0F, -3F, 6, 7, 6);
		t3.setRotationPoint(0F, 3F, 0F);
		t3.setTextureSize(64, 32);

	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale) {
		ModelMDBase.render90(v, scale);
		t0.render(scale);
		t1.render(scale);
		t2.render(scale);
		t3.render(scale);
	}

}
