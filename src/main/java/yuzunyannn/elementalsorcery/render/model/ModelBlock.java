package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelBlock extends ModelBase {

	ModelRenderer block;

	public ModelBlock() {
		textureWidth = 16;
		textureHeight = 16;

		block = new ModelRenderer(this, 0, 0);
		block.addBox(-8F, -8F, -8F, 16, 16, 16);
		block.setTextureSize(16, 16);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		block.render(f5);
	}
}
