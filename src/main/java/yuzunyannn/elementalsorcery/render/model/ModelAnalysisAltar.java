package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelAnalysisAltar extends ModelBase {
	ModelRenderer t;
	ModelRenderer l1;
	ModelRenderer l2;
	ModelRenderer l3;
	ModelRenderer l4;
	ModelRenderer t1;
	ModelRenderer t2;
	ModelRenderer t3;
	ModelRenderer n;

	public ModelAnalysisAltar() {
		textureWidth = 64;
		textureHeight = 64;

		t = new ModelRenderer(this, 0, 0);
		t.addBox(-8F, -2F, -8F, 16, 2, 16);
		t.setRotationPoint(0F, 16F, 0F);
		t.setTextureSize(64, 64);
		setRotation(t, 0F, 0F, 0F);
		l1 = new ModelRenderer(this, 41, 19);
		l1.addBox(-1F, 0F, -1F, 2, 14, 2);
		l1.setRotationPoint(-7F, 0F, 7F);
		l1.setTextureSize(64, 64);
		setRotation(l1, 0F, 0F, 0F);
		l2 = new ModelRenderer(this, 41, 19);
		l2.addBox(-1F, 0F, -1F, 2, 14, 2);
		l2.setRotationPoint(7F, 0F, -7F);
		l2.setTextureSize(64, 64);
		setRotation(l2, 0F, 0F, 0F);
		l3 = new ModelRenderer(this, 41, 19);
		l3.addBox(-1F, 0F, -1F, 2, 14, 2);
		l3.setRotationPoint(7F, 0F, 7F);
		l3.setTextureSize(64, 64);
		setRotation(l3, 0F, 0F, 0F);
		l4 = new ModelRenderer(this, 41, 19);
		l4.addBox(-1F, 0F, -1F, 2, 14, 2);
		l4.setRotationPoint(-7F, 0F, -7F);
		l4.setTextureSize(64, 64);
		setRotation(l4, 0F, 0F, 0F);
		t1 = new ModelRenderer(this, 0, 19);
		t1.addBox(-5F, -2F, -5F, 10, 2, 10);
		t1.setRotationPoint(0F, 14F, 0F);
		t1.setTextureSize(64, 64);
		setRotation(t1, 0F, 0.7853982F, 0F);
		t2 = new ModelRenderer(this, 0, 32);
		t2.addBox(-4F, -1F, -4F, 8, 2, 8);
		t2.setRotationPoint(0F, 11F, 0F);
		t2.setTextureSize(64, 64);
		setRotation(t2, 0F, 0F, 0F);
		t3 = new ModelRenderer(this, 0, 43);
		t3.addBox(-2F, 0F, -2F, 4, 10, 4);
		t3.setRotationPoint(0F, 0F, 0F);
		t3.setTextureSize(64, 64);
		setRotation(t3, 0F, 0.7853982F, 0F);
		n = new ModelRenderer(this, 50, 19);
		n.addBox(-1F, 0F, -1F, 2, 13, 2);
		n.setRotationPoint(0F, 1F, 0F);
		n.setTextureSize(64, 64);
		setRotation(n, 0F, 0.7853982F, 0F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		t.render(f5);
		l1.render(f5);
		l2.render(f5);
		l3.render(f5);
		l4.render(f5);
		t1.render(f5);
		t2.render(f5);
		t3.render(f5);
		n.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
