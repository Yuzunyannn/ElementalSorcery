package yuzunyannn.elementalsorcery.render.model.md;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelMDTransfer extends ModelBase {
	ModelRenderer box;

	public ModelMDTransfer() {
		textureWidth = 64;
		textureHeight = 32;
		box = new ModelRenderer(this, 0, 0);
		box.addBox(-6.5F, 0F, -6.5F, 13, 11, 13);
		box.setRotationPoint(0F, 5F, 0F);
		box.setTextureSize(64, 32);
	}

	public void render(Entity entity, float on, float f1, float f2, float f3, float f4, float scale) {
		box.render(scale);
	}

}
