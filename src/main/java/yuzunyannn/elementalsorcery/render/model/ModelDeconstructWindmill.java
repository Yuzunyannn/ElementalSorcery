
package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelDeconstructWindmill extends ModelBase {

	private final ModelRenderer base;
	private final ModelRenderer bone5;
	private final ModelRenderer fly;
	private final ModelRenderer bone4;
	private final ModelRenderer bone3;

	public ModelDeconstructWindmill() {
		textureWidth = 32;
		textureHeight = 32;

		base = new ModelRenderer(this);
		base.cubeList.add(new ModelBox(base, 0, 0, -8.0F, 0.0F, -8.0F, 3, 3, 3, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 0, 0, 5.0F, 0.0F, 5.0F, 3, 3, 3, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 0, 0, 5.0F, 3.0F, -8.0F, 3, 3, 3, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 0, 0, -8.0F, 3.0F, 5.0F, 3, 3, 3, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 12, 0, -7.0F, 0.0F, 6.0F, 1, 3, 1, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 12, 0, 6.0F, 0.0F, -7.0F, 1, 3, 1, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 0, 6, -3.0F, 0.0F, -3.0F, 6, 1, 6, 0.0F, false));

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(0.0F, -1.5F, 0.0F);
		base.addChild(bone5);
		setRotationAngle(bone5, 0.0F, -0.7854F, 0.0F);
		bone5.cubeList.add(new ModelBox(bone5, 20, 6, -1.5F, 2.5F, -1.5F, 3, 1, 3, 0.0F, false));

		fly = new ModelRenderer(this);
		setRotationAngle(fly, 0.0F, -0.7854F, 0.0F);
		fly.cubeList.add(new ModelBox(fly, 16, 0, -2.125F, 9.75F, -2.0F, 4, 1, 4, 0.0F, false));
		fly.cubeList.add(new ModelBox(fly, 0, 13, -4.125F, 11.75F, -4.0F, 8, 1, 8, 0.0F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(-0.125F, -11.75F, 0.0F);
		fly.addChild(bone4);

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(-0.125F, -11.75F, 0.0F);
		fly.addChild(bone3);
		setRotationAngle(bone3, 0.0F, 0.8727F, 0.0F);
		bone3.cubeList.add(new ModelBox(bone3, 0, 22, -3.0F, 22.5F, -3.0F, 6, 1, 6, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float high, float f1, float ageInTicks, float f3, float f4, float scale) {
		base.render(scale);

		fly.setRotationPoint(0.0F, -5 + high, 0.0F);
		fly.render(scale);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

}
