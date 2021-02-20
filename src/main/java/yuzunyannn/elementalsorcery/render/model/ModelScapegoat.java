package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelScapegoat extends ModelBase {

	public final ModelRenderer body;
	public final ModelRenderer leg;
	public final ModelBox main;

	public ModelScapegoat() {
		textureWidth = 64;
		textureHeight = 64;

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 7.0F, 0.0F);
		body.cubeList.add(new ModelBox(body, 48, 0, -1.0F, 5.0F, -1.0F, 2, 12, 2, 0.0F, false));
		body.cubeList.add(main = new ModelBox(body, 0, 0, -6.0F, -8.0F, -6.0F, 12, 14, 12, 0.0F, false));
		body.cubeList.add(new ModelBox(body, 48, 7, -1.0F, -12.0F, -1.0F, 2, 5, 2, 0.0F, false));
		body.cubeList.add(new ModelBox(body, 0, 26, -1.0F, -22.0F, -6.0F, 2, 10, 12, 0.0F, false));

		leg = new ModelRenderer(this);
		leg.setRotationPoint(0.0F, -1.0F, 0.0F);
		body.addChild(leg);
		setRotationAngle(leg, 0.2618F, 0.0F, 0.0F);
		leg.cubeList.add(new ModelBox(leg, 12, 38, -1.0F, -1.0F, -12.0F, 2, 2, 24, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		float m = MathHelper.sin(f / 180 * 3.14f) * 0.2618f;
		setRotationAngle(leg, m, 0, 0);

		body.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
