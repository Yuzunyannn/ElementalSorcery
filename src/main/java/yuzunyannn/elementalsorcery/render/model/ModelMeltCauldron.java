package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelMeltCauldron extends ModelBase {
	ModelRenderer leg1;
	ModelRenderer b;
	ModelRenderer leg2;
	ModelRenderer leg3;
	ModelRenderer leg4;
	ModelRenderer w1;
	ModelRenderer w2;
	ModelRenderer w3;
	ModelRenderer w4;

	public ModelMeltCauldron() {
		textureWidth = 64;
		textureHeight = 64;

		leg1 = new ModelRenderer(this, 0, 0);
		leg1.addBox(-1.5F, 0F, -1.5F, 3, 14, 3);
		leg1.setRotationPoint(6.5F, 0F, 6.5F);
		leg1.setTextureSize(64, 64);

		b = new ModelRenderer(this, 0, 18);
		b.addBox(-7F, 0F, -7F, 14, 1, 14);
		b.setRotationPoint(0F, 1F, 0F);
		b.setTextureSize(64, 64);

		leg2 = new ModelRenderer(this, 0, 0);
		leg2.addBox(-1.5F, 0F, -1.5F, 3, 14, 3);
		leg2.setRotationPoint(6.5F, 0F, -6.5F);
		leg2.setTextureSize(64, 64);

		leg3 = new ModelRenderer(this, 0, 0);
		leg3.addBox(-1.5F, 0F, -1.5F, 3, 14, 3);
		leg3.setRotationPoint(-6.5F, 0F, 6.5F);
		leg3.setTextureSize(64, 64);

		leg4 = new ModelRenderer(this, 0, 0);
		leg4.addBox(-1.5F, 0F, -1.5F, 3, 14, 3);
		leg4.setRotationPoint(-6.5F, 0F, -6.5F);
		leg4.setTextureSize(64, 64);

		w1 = new ModelRenderer(this, 13, 0);
		w1.addBox(-5F, 0F, 0F, 10, 11, 1);
		w1.setRotationPoint(0F, 2F, 6F);
		w1.setTextureSize(64, 64);

		w2 = new ModelRenderer(this, 13, 0);
		w2.addBox(-5F, 0F, 0F, 10, 11, 1);
		w2.setRotationPoint(0F, 2F, -7F);
		w2.setTextureSize(64, 64);

		w3 = new ModelRenderer(this, 13, 0);
		w3.addBox(-5F, 0F, 0F, 10, 11, 1);
		w3.setRotationPoint(-7F, 2F, 0F);
		w3.setTextureSize(64, 64);
		w3.rotateAngleY = 1.570796F;

		w4 = new ModelRenderer(this, 13, 0);
		w4.addBox(-5F, 0F, 0F, 10, 11, 1);
		w4.setRotationPoint(6F, 2F, 0F);
		w4.setTextureSize(64, 64);
		w4.rotateAngleY = 1.570796F;

	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		b.render(f5);
		leg1.render(f5);
		leg2.render(f5);
		leg3.render(f5);
		leg4.render(f5);
		w1.render(f5);
		w2.render(f5);
		w3.render(f5);
		w4.render(f5);
	}
}
