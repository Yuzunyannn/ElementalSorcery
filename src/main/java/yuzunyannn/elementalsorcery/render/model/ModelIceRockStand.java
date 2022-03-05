
package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelIceRockStand extends ModelBase {

	private final ModelRenderer c;
	private final ModelRenderer b;

	public ModelIceRockStand() {
		textureWidth = 64;
		textureHeight = 64;

		c = new ModelRenderer(this);
		c.setRotationPoint(0, 1, 0);
		c.cubeList.add(new ModelBox(c, 0, 17, 5.0F, 1.0F, -7.0F, 2, 8, 14, 0.0F, false));
		c.cubeList.add(new ModelBox(c, 19, 20, -5.0F, 1.0F, -7.0F, 10, 8, 2, 0.0F, false));
		c.cubeList.add(new ModelBox(c, 0, 39, 3.0F, 1.0F, -5.0F, 2, 4, 10, 0.0F, false));
		c.cubeList.add(new ModelBox(c, 15, 44, -3.0F, 1.0F, -5.0F, 6, 2, 2, 0.0F, false));
		c.cubeList.add(new ModelBox(c, 0, 53, 2.0F, 1.0F, -3.0F, 1, 1, 6, 0.0F, false));
		c.cubeList.add(new ModelBox(c, 9, 56, -2.0F, 1.0F, -3.0F, 4, 1, 1, 0.0F, false));

		b = new ModelRenderer(this);
		b.cubeList.add(new ModelBox(b, 0, 0, -8.0F, 1.0F, -8.0F, 16, 1, 16, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale) {
		b.render(scale);
		c.rotateAngleY = 0;
		c.render(scale);
		c.rotateAngleY = 3.1415926f;
		c.render(scale);
	}

}
