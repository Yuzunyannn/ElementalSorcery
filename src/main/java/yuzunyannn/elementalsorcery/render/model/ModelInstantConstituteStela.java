
package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelInstantConstituteStela extends ModelBase {
	private final ModelRenderer base;
	private final ModelRenderer wall;
	private final ModelRenderer up;
	private final ModelRenderer b1;
	private final ModelRenderer b2;

	public ModelInstantConstituteStela() {
		textureWidth = 64;
		textureHeight = 64;

		base = new ModelRenderer(this);
		base.setRotationPoint(0, 11, 0);
		base.cubeList.add(new ModelBox(base, 0, 17, -7.0F, -2.0F, -7.0F, 14, 1, 14, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 0, 0, -8.0F, -1.0F, -8.0F, 16, 1, 16, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 7, 4, -6.0F, -10.0F, -6.0F, 12, 1, 12, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 8, 49, -7.0F, -9.0F, -7.0F, 14, 1, 14, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 0, 38, -5.0F, -11.0F, -5.0F, 10, 1, 10, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 0, 22, -5.0F, -8.0F, -5.0F, 10, 6, 10, 0.0F, false));

		wall = new ModelRenderer(this);
		wall.setRotationPoint(0, 9, 0);
		wall.cubeList.add(new ModelBox(wall, 0, 0, -7.0F, -6.0F, 1.0F, 1, 6, 6, 0.0F, false));
		wall.cubeList.add(new ModelBox(wall, 52, 17, -7.0F, -6.0F, -6.0F, 1, 6, 5, 0.0F, false));
		wall.cubeList.add(new ModelBox(wall, 58, 0, -7.0F, -6.0F, -1.0F, 1, 6, 2, 0.0F, false));

		up = new ModelRenderer(this);

		b1 = new ModelRenderer(this);
		b1.setRotationPoint(0.0F, 1.0F, -5.5F);
		up.addChild(b1);
		b1.rotateAngleX = 0.5236F;
		b1.cubeList.add(new ModelBox(b1, 0, 49, -5.0F, -4.25F, -0.25F, 10, 4, 1, 0.0F, false));

		b2 = new ModelRenderer(this);
		b2.setRotationPoint(0.0F, 1.0F, 5.5F);
		up.addChild(b2);
		b2.rotateAngleX = -0.5236F;
		b2.cubeList.add(new ModelBox(b2, 0, 54, -5.0F, -4.25F, -0.75F, 10, 4, 1, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale) {
		base.render(scale);
		for (int i = 0; i < 4; i++) {
			wall.rotateAngleY = 3.1415926f / 2 * i;
			wall.render(scale);
		}
		up.rotateAngleY = 0;
		up.render(scale);
		up.rotateAngleY = 3.1415926f / 2;
		up.render(scale);

		base.setRotationPoint(0, 8, 0);
		wall.setRotationPoint(0, 6, 0);
		up.setRotationPoint(0, -3, 0);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

}
