package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelMDRubbleRepair extends ModelBase {
	ModelRenderer g;
	ModelRenderer v;
	ModelRenderer t;
	ModelRenderer vc;
	ModelRenderer tc;

	public ModelMDRubbleRepair() {
		textureWidth = 64;
		textureHeight = 64;

		g = new ModelRenderer(this, 0, 0);
		g.addBox(-6.5F, 0F, -6.5F, 13, 9, 13);
		g.setRotationPoint(0F, 5F, 0F);
		g.setTextureSize(64, 64);

		v = new ModelRenderer(this, 53, 0);
		v.addBox(5F, 0F, 5F, 2, 10, 2);
		v.setRotationPoint(0F, 5F, 0F);
		v.setTextureSize(64, 64);

		t = new ModelRenderer(this, 0, 23);
		t.addBox(-7F, 0F, -7F, 14, 2, 14);
		t.setRotationPoint(0F, 14F, 0F);
		t.setTextureSize(64, 64);

		vc = new ModelRenderer(this, 0, 40);
		vc.addBox(-1F, 0F, -1F, 2, 4, 2);
		vc.setRotationPoint(0F, 3F, 0F);
		vc.setTextureSize(64, 64);

		tc = new ModelRenderer(this, 9, 40);
		tc.addBox(-2F, 0F, -2F, 4, 1, 4);
		tc.setRotationPoint(0F, 7F, 0F);
		tc.setTextureSize(64, 64);

	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale) {
		super.render(entity, f, f1, f2, f3, f4, scale);
		g.render(scale);
		t.render(scale);
		ModelMDBase.render90(v, scale);
		vc.render(scale);
		tc.render(scale);
	}

}
