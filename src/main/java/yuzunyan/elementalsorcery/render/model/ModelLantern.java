package yuzunyan.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelLantern extends ModelBase {
	// fields
	ModelRenderer bottom;
	ModelRenderer leg1;
	ModelRenderer leg2;
	ModelRenderer leg3;
	ModelRenderer leg4;
	ModelRenderer torch;
	ModelRenderer cover;
	ModelRenderer top1;
	ModelRenderer top2;

	public ModelLantern() {
		textureWidth = 64;
		textureHeight = 64;

		bottom = new ModelRenderer(this, 0, 1);
		bottom.addBox(-8F, 0F, -8F, 16, 2, 16);
		bottom.setRotationPoint(0F, -1F, 0F);
		bottom.setTextureSize(64, 64);
		bottom.mirror = true;
		leg1 = new ModelRenderer(this, 0, 35);
		leg1.addBox(-1F, 0F, -1F, 2, 12, 2);
		leg1.setRotationPoint(-5F, 1F, -5F);
		leg1.setTextureSize(64, 64);
		leg1.mirror = true;
		leg2 = new ModelRenderer(this, 0, 35);
		leg2.addBox(-1F, 0F, -1F, 2, 12, 2);
		leg2.setRotationPoint(-5F, 1F, 5F);
		leg2.setTextureSize(64, 64);
		leg2.mirror = true;
		leg3 = new ModelRenderer(this, 0, 35);
		leg3.addBox(-1F, 0F, -1F, 2, 12, 2);
		leg3.setRotationPoint(5F, 1F, -5F);
		leg3.setTextureSize(64, 64);
		leg3.mirror = true;
		leg4 = new ModelRenderer(this, 0, 35);
		leg4.addBox(-1F, 0F, -1F, 2, 12, 2);
		leg4.setRotationPoint(5F, 1F, 5F);
		leg4.setTextureSize(64, 64);
		leg4.mirror = true;
		torch = new ModelRenderer(this, 8, 35);
		torch.addBox(-1F, 0F, -1F, 2, 9, 2);
		torch.setRotationPoint(0F, 1F, 0F);
		torch.setTextureSize(64, 64);
		torch.mirror = true;
		cover = new ModelRenderer(this, 16, 35);
		cover.addBox(-4.5F, 0F, -4.5F, 9, 10, 9);
		cover.setRotationPoint(0F, 1F, 0F);
		cover.setTextureSize(64, 64);
		cover.mirror = true;
		top1 = new ModelRenderer(this, 0, 19);
		top1.addBox(-7F, 0F, -7F, 14, 2, 14);
		top1.setRotationPoint(0F, 13F, 0F);
		top1.setTextureSize(64, 64);
		top1.mirror = true;
		top2 = new ModelRenderer(this, 0, 54);
		top2.addBox(-3F, 0F, -3F, 6, 3, 6);
		top2.setRotationPoint(0F, 15F, 0F);
		top2.setTextureSize(64, 64);
		top2.mirror = true;
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		bottom.render(f5);
		leg1.render(f5);
		leg2.render(f5);
		leg3.render(f5);
		leg4.render(f5);
		torch.render(f5);
		cover.render(f5);
		top1.render(f5);
		top2.render(f5);
	}

}
