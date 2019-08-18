package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelMDBase extends ModelBase {
	ModelRenderer b;
	ModelRenderer wa;
	ModelRenderer wb;
	ModelRenderer wc;
	ModelRenderer wd;
	ModelRenderer w;

	public ModelMDBase() {
		textureWidth = 64;
		textureHeight = 32;

		b = new ModelRenderer(this, 0, 0);
		b.addBox(-6F, 0F, -6F, 12, 3, 12);
		b.setRotationPoint(0F, 0F, 0F);
		b.setTextureSize(64, 32);

		w = new ModelRenderer(this, 0, 16);
		w.addBox(-2F, 0F, -1F, 4, 7, 2);
		w.setRotationPoint(0F, 0F, 7F);
		w.setTextureSize(64, 32);

		wa = new ModelRenderer(this, 33, 16);
		wa.addBox(4F, 0F, -2F, 3, 7, 3);
		wa.setRotationPoint(0F, 0F, 7F);
		wa.setTextureSize(64, 32);

		wb = new ModelRenderer(this, 20, 16);
		wb.addBox(-7F, 0F, -2F, 3, 7, 3);
		wb.setRotationPoint(0F, 0F, 7F);
		wb.setTextureSize(64, 32);

		wc = new ModelRenderer(this, 13, 22);
		wc.addBox(-4F, 0F, -1F, 2, 5, 1);
		wc.setRotationPoint(0F, 0F, 7F);
		wc.setTextureSize(64, 32);

		wd = new ModelRenderer(this, 13, 16);
		wd.addBox(2F, 0F, -1F, 2, 5, 1);
		wd.setRotationPoint(0F, 0F, 7F);
		wd.setTextureSize(64, 32);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale) {
		super.render(entity, f, f1, f2, f3, f4, scale);
		b.render(scale);
		this.renderOnce(0, 0, 7, scale);
		this.renderOnce(1.5707963f, 7, 0, scale);
		this.renderOnce(3.1415926f, 0, -7, scale);
		this.renderOnce(4.7123889f, -7, 0, scale);
	}

	private void renderOnce(float roate, float px, float pz, float scale) {
		w.setRotationPoint(px, 0, pz);
		wa.setRotationPoint(px, 0, pz);
		wb.setRotationPoint(px, 0, pz);
		wc.setRotationPoint(px, 0, pz);
		wd.setRotationPoint(px, 0, pz);

		w.rotateAngleY = roate;
		wa.rotateAngleY = roate;
		wb.rotateAngleY = roate;
		wc.rotateAngleY = roate;
		wd.rotateAngleY = roate;

		w.render(scale);
		wa.render(scale);
		wb.render(scale);
		wc.render(scale);
		wd.render(scale);
	}

}