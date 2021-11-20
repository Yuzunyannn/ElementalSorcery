package yuzunyannn.elementalsorcery.render.model.living;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelDreadCube extends ModelBase {

	private final ModelRenderer yun;
	private final ModelRenderer head;
	private final ModelRenderer hy;

	public ModelDreadCube() {
		textureWidth = 32;
		textureHeight = 32;

		yun = new ModelRenderer(this);
		yun.setRotationPoint(0.0F, 16.0F, 0.0F);
		yun.cubeList.add(new ModelBox(yun, 0, 14, -8.0F, 5.0F, 5.0F, 3, 3, 3, 0.0F, false));
		yun.cubeList.add(new ModelBox(yun, 0, 0, -5.0F, 7.0F, 7.0F, 9, 1, 1, 0.0F, false));
		yun.cubeList.add(new ModelBox(yun, 0, 4, -4.0F, 4.0F, 6.0F, 2, 2, 2, 0.0F, false));
		yun.cubeList.add(new ModelBox(yun, 20, 12, -8.0F, 4.0F, 1.0F, 2, 4, 4, 0.0F, false));
		yun.cubeList.add(new ModelBox(yun, 20, 6, -7.0F, 7.0F, -4.0F, 1, 1, 5, 0.0F, false));
		yun.cubeList.add(new ModelBox(yun, 22, 1, -8.0F, 6.0F, -6.0F, 1, 1, 4, 0.0F, false));
		yun.cubeList.add(new ModelBox(yun, 12, 14, -7.0F, 1.0F, 5.0F, 2, 4, 2, 0.0F, false));
		yun.cubeList.add(new ModelBox(yun, 1, 11, -8.0F, 1.0F, 5.0F, 1, 1, 2, 0.0F, false));
		yun.cubeList.add(new ModelBox(yun, 7, 10, -8.0F, 2.0F, 5.0F, 1, 3, 1, 0.0F, false));
		yun.cubeList.add(new ModelBox(yun, 10, 2, -8.0F, -4.0F, 7.0F, 1, 5, 1, 0.0F, false));
		yun.cubeList.add(new ModelBox(yun, 14, 2, -8.0F, -7.0F, 6.0F, 1, 3, 1, 0.0F, false));
		yun.cubeList.add(new ModelBox(yun, 18, 2, -7.0F, -8.0F, 7.0F, 1, 2, 1, 0.0F, false));
		yun.cubeList.add(new ModelBox(yun, 0, 2, 3.0F, 7.0F, 6.0F, 4, 1, 1, 0.0F, false));

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 16.0F, 0.0F);
		head.cubeList.add(new ModelBox(head, 0, 20, -3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F, false));

		hy = new ModelRenderer(this);
		hy.setRotationPoint(0.0F, 16.0F, 0.0F);
		hy.cubeList.add(new ModelBox(hy, 24, 28, -5.0F, -5.0F, -5.0F, 2, 2, 2, 0.0F, false));
		hy.cubeList.add(new ModelBox(hy, 20, 23, -4.0F, -2.0F, -5.0F, 1, 2, 1, 0.0F, false));
		hy.cubeList.add(new ModelBox(hy, 24, 20, -5.0F, -4.0F, -3.0F, 1, 1, 3, 0.0F, false));
		hy.cubeList.add(new ModelBox(hy, 28, 26, -4.0F, -4.0F, -6.0F, 1, 1, 1, 0.0F, false));
		hy.cubeList.add(new ModelBox(hy, 24, 24, -3.0F, -4.0F, -5.0F, 3, 1, 1, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float power, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scaleFactor) {

		float dA = MathHelper.cos(ageInTicks * 0.04f) * 0.1f * power;
		float dB = MathHelper.sin(ageInTicks * 0.04f) * 0.1f * power;
		float dH = MathHelper.cos(ageInTicks * 0.03f) * power;

		head.rotateAngleY = netHeadYaw * 0.017453292F;
		head.rotateAngleX = headPitch * 0.017453292F;
		head.setRotationPoint(0.0F, 16.0F + dH, 0.0F);
		head.render(scaleFactor);

		yun.rotateAngleX = 3.1415926f + dA;
		yun.rotateAngleY = -3.1415926f / 2 + dA;
		yun.render(scaleFactor);
		yun.rotateAngleX = dB;
		yun.rotateAngleY = dB;
		yun.render(scaleFactor);

		hy.rotateAngleX = 3.1415926f + dA;
		hy.rotateAngleY = 3.1415926f / 2 + dB;
		;
		hy.render(scaleFactor);
		hy.rotateAngleX = dB;
		hy.rotateAngleY = dA;
		hy.render(scaleFactor);
	}

}
