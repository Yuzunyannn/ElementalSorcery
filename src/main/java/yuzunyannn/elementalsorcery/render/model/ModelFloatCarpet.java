package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelFloatCarpet extends ModelBase {
	private final ModelRenderer main;
	private final ModelRenderer part;

	public ModelFloatCarpet() {
		textureWidth = 64;
		textureHeight = 64;

		main = new ModelRenderer(this);
		main.cubeList.add(new ModelBox(main, 0, 0, -8.0F, -3.0F, -8.0F, 16, 2, 16, 0.0F, false));
		main.cubeList.add(new ModelBox(main, 0, 18, -6.0F, -2.0F, -6.0F, 12, 2, 12, 0.0F, false));

		part = new ModelRenderer(this);
		part.cubeList.add(new ModelBox(part, 0, 32, -8.0F, -3.0F, -8.0F, 16, 3, 16, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		main.render(f5);
		part.render(f5 * 1.01f);
	}

}
