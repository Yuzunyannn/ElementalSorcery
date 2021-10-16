package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelGlassCup extends ModelBase {

	private final ModelRenderer bottom;
	private final ModelRenderer side;

	public ModelGlassCup() {
		textureWidth = 64;
		textureHeight = 64;

		bottom = new ModelRenderer(this);
		bottom.setRotationPoint(0, 3.5f, 0);
		bottom.cubeList.add(new ModelBox(bottom, 0, 0, -4.0F, -1.5F, -4.0F, 8, 1, 8, 0.0F, false));
		bottom.cubeList.add(new ModelBox(bottom, 0, 9, -5.0F, -0.5F, -5.0F, 10, 3, 10, 0.0F, false));

		side = new ModelRenderer(this);
		side.setRotationPoint(0, 0, 0);
		side.cubeList.add(new ModelBox(side, 0, 22, -5.0F, 4.0F, -7.0F, 10, 14, 2, 0.0F, false));
		side.cubeList.add(new ModelBox(side, 0, 0, -1.0F, 4.0F, -1.0F, 1, 1, 1, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale) {
		bottom.render(scale);
		for (int i = 0; i < 4; i++) {
			side.rotateAngleY = i * 3.1415926f / 2;
			side.render(scale);
		}

	}
}
