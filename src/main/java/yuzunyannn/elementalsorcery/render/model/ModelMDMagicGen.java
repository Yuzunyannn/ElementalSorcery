package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelMDMagicGen extends ModelBase {
	ModelRenderer box;
	ModelRenderer otoff;
	ModelRenderer oton;

	public ModelMDMagicGen() {
		textureWidth = 64;
		textureHeight = 32;

		box = new ModelRenderer(this, 0, 0);
		box.addBox(-6.5F, 0F, -6.5F, 13, 11, 13);
		box.setRotationPoint(0F, 5F, 0F);
		box.setTextureSize(64, 32);

		otoff = new ModelRenderer(this, 0, 25);
		otoff.addBox(-4.5F, 6F, 5.5F, 9, 3, 1);
		otoff.setRotationPoint(0F, 5F, 0F);
		otoff.setTextureSize(64, 32);

		oton = new ModelRenderer(this, 20, 25);
		oton.addBox(-4.5F, 6F, 5.5F, 9, 3, 1);
		oton.setRotationPoint(0F, 5F, 0F);
		oton.setTextureSize(64, 32);
	}

	public void render(Entity entity, float on, float f1, float f2, float f3, float f4, float scale) {
		box.render(scale);
		if (on == 0)
			ModelMDBase.render90(otoff, scale);
		else
			ModelMDBase.render90(oton, scale);
	}

}
