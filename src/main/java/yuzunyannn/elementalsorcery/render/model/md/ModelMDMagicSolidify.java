package yuzunyannn.elementalsorcery.render.model.md;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelMDMagicSolidify extends ModelBase {
	ModelRenderer b;
	ModelRenderer t;

	public ModelMDMagicSolidify() {
		textureWidth = 64;
		textureHeight = 64;
		
		b = new ModelRenderer(this, 0, 0);
		b.addBox(-6.5F, 0F, -6.5F, 13, 7, 13);
		b.setRotationPoint(0F, 5F, 0F);
		b.setTextureSize(64, 64);

		t = new ModelRenderer(this, 0, 21);
		t.addBox(-5F, 0F, -5F, 10, 5, 10);
		t.setRotationPoint(0F, 12F, 0F);
		t.setTextureSize(64, 64);

	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		b.render(f5);
		t.render(f5);
	}

}
