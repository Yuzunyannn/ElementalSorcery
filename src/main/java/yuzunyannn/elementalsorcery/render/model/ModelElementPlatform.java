package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelElementPlatform extends ModelBase {
	private final ModelRenderer main;
	private final ModelRenderer ball;

	public ModelElementPlatform() {
		textureWidth = 64;
		textureHeight = 64;

		main = new ModelRenderer(this);
		main.setRotationPoint(0.0F, -13, 0.0F);
		main.cubeList.add(new ModelBox(main, 16, 30, -6.0F, 26.0F, -6.0F, 12, 1, 12, 0.0F, false));
		main.cubeList.add(new ModelBox(main, 0, 0, -8.0F, 13.0F, -8.0F, 16, 2, 16, 0.0F, false));
		main.cubeList.add(new ModelBox(main, 0, 18, -5.0F, 15.0F, -5.0F, 10, 2, 10, 0.0F, false));
		main.cubeList.add(new ModelBox(main, 0, 0, -2.0F, 17.0F, -2.0F, 4, 9, 4, 0.0F, false));
		main.cubeList.add(new ModelBox(main, 32, 18, -4.0F, 27.0F, -4.0F, 8, 1, 8, 0.0F, false));

		ball = new ModelRenderer(this);
		ball.cubeList.add(new ModelBox(ball, 56, 0, 5.0F, 6.0F, 5.0F, 2, 2, 2, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scale) {
		main.render(scale);

		float cos1 = MathHelper.cos(ageInTicks * 0.005f);
		ball.setRotationPoint(0.0F, 1 + cos1 * 2, 0.0F);
		ball.rotateAngleY = cos1 * 3.1415f * 3;
		ball.render(scale);

		float sin1 = MathHelper.sin(ageInTicks * 0.005f);
		ball.setRotationPoint(0.0F, 1 - cos1 * 2, 0.0F);
		ball.rotateAngleY = sin1 * 3.1415f * 3;
		ball.render(scale);

	}
}
