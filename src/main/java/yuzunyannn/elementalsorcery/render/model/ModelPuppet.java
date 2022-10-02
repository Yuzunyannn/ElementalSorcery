package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelPuppet extends ModelBase {

	private final ModelRenderer head;
	private final ModelRenderer fly;
	private final ModelRenderer propeller;

	public ModelPuppet() {
		textureWidth = 32;
		textureHeight = 32;

		head = new ModelRenderer(this);
		head.setRotationPoint(-1.0F, 24.0F, 1.0F);
		head.cubeList.add(new ModelBox(head, 0, 14, -2.0F, -7.0F, -4.0F, 6, 6, 6, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 0, 0, -2.5F, -7.5F, -4.5F, 7, 7, 7, 0.0F, false));

		fly = new ModelRenderer(this);
		fly.setRotationPoint(0.0F, 25.0F, 0.0F);
		fly.cubeList.add(new ModelBox(fly, 0, 26, -1.0F, -11.0F, -1.0F, 2, 4, 2, 0.0F, false));

		propeller = new ModelRenderer(this);
		propeller.setRotationPoint(0.0F, 14F, 0.0F);
		propeller.rotateAngleX = -0.5236F;
		propeller.cubeList.add(new ModelBox(propeller, 8, 28, -6.0F, -1.0F, -0.5F, 6, 1, 3, 0.0F, false));
	}

	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scale) {

		propeller.offsetZ = fly.offsetZ = head.offsetZ = -limbSwingAmount * 0.25f * MathHelper.sin(limbSwing);

		head.offsetY = fly.offsetY = propeller.offsetY = MathHelper.cos(ageInTicks / 10f) * 0.02f;

		head.render(scale);
		fly.render(scale);
		propeller.rotateAngleY = ageInTicks;
		propeller.rotateAngleZ = MathHelper.sin(ageInTicks / 20f) * 3.1415926f / 10f;
		propeller.render(scale);
		for (int i = 0; i < 4; i++) {
			propeller.render(scale);
			propeller.rotateAngleY += 3.1415926f / 2;
		}

	}

}
