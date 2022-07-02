package yuzunyannn.elementalsorcery.render.model.living;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelElf extends ModelBiped {
	private final ModelRenderer bipedHeadOrigin;
	private final ModelRenderer bipedHeadEyeClose;
	private final ModelRenderer headdress;
	private final ModelRenderer b;
	private final ModelRenderer a;

	public ModelElf() {
		textureWidth = 128;
		textureHeight = 128;

		bipedHeadOrigin = new ModelRenderer(this);
		bipedHeadOrigin.setRotationPoint(0.0F, -0.5F, 0.0F);
		bipedHeadOrigin.cubeList.add(new ModelBox(bipedHeadOrigin, 0, 18, -4.0F, -7.5F, -4.0F, 8, 8, 8, 0.0F, false));
		bipedHeadOrigin.cubeList.add(new ModelBox(bipedHeadOrigin, 0, 0, -4.5F, -8.0F, -4.5F, 9, 9, 9, 0.0F, false));
		bipedHeadEyeClose = new ModelRenderer(this);
		bipedHeadEyeClose.setRotationPoint(0.0F, -0.5F, 0.0F);
		bipedHeadEyeClose.cubeList
				.add(new ModelBox(bipedHeadEyeClose, 33, 18, -4.0F, -7.5F, -4.0F, 8, 8, 8, 0.0F, false));
		bipedHeadEyeClose.cubeList
				.add(new ModelBox(bipedHeadEyeClose, 0, 0, -4.5F, -8.0F, -4.5F, 9, 9, 9, 0.0F, false));
		bipedHeadwear = bipedHeadOrigin;

		headdress = new ModelRenderer(this);
		headdress.setRotationPoint(-5.0F, -6.5F, 0.0F);
		bipedHeadOrigin.addChild(headdress);
		bipedHeadEyeClose.addChild(headdress);
		setRotationAngle(headdress, 0.0F, 0.0F, -1.0472F);

		b = new ModelRenderer(this);
		b.setRotationPoint(0.0F, 0.0F, 0.0F);
		headdress.addChild(b);
		setRotationAngle(b, 0.0F, 0.4363F, 0.0F);
		b.cubeList.add(new ModelBox(b, 38, 40, -2.5F, -1.0F, -1.0F, 3, 1, 2, 0.0F, false));

		a = new ModelRenderer(this);
		a.setRotationPoint(0.0F, 0.0F, 0.0F);
		headdress.addChild(a);
		setRotationAngle(a, 0.0F, -0.4363F, 0.0F);
		a.cubeList.add(new ModelBox(a, 38, 40, -2.5F, -1.0F, -1.0F, 3, 1, 2, 0.0F, false));

		bipedRightLeg = new ModelRenderer(this);
		bipedRightLeg.setRotationPoint(-2.5F, 13.5F, 0.0F);
		bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 52, -2.0F, -1.5F, -2.0F, 4, 12, 4, 0.0F, false));
		bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 18, 54, -2.5F, 4.55F, -2.5F, 5, 6, 5, 0.0F, false));

		bipedLeftLeg = new ModelRenderer(this);
		bipedLeftLeg.setRotationPoint(2.5F, 13.5F, 0.0F);
		bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 52, -2.0F, -1.5F, -2.0F, 4, 12, 4, 0.0F, false));
		bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 18, 54, -2.5F, 4.55F, -2.5F, 5, 6, 5, 0.0F, false));

		bipedBody = new ModelRenderer(this);
		bipedBody.setRotationPoint(0.0F, 7.5F, 0.0F);
		bipedBody.cubeList.add(new ModelBox(bipedBody, 37, 0, -4.0F, -7.5F, -3.0F, 8, 12, 6, 0.0F, false));
		bipedBody.cubeList.add(new ModelBox(bipedBody, 69, 0, -4.5F, -3.5F, -3.5F, 9, 4, 7, 0.0F, false));
		bipedBody.cubeList.add(new ModelBox(bipedBody, 70, 12, -5.0F, -1.5F, -4.0F, 10, 11, 8, 0.0F, false));

		bipedLeftArm = new ModelRenderer(this);
		bipedLeftArm.setRotationPoint(5.0F, 1.0F, 0.0F);
		bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 0, 35, -1.0F, -1.0F, -2.0F, 4, 12, 4, 0.0F, false));
		bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 17, 35, -1.5F, -1.25F, -2.5F, 5, 4, 5, 0.0F, false));

		bipedRightArm = new ModelRenderer(this);
		bipedRightArm.setRotationPoint(-5.0F, 0.875F, 0.0F);
		bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 0, 35, -3.0F, -0.875F, -2.0F, 4, 12, 4, 0.0F, false));
		bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 17, 35, -3.5F, -1.125F, -2.5F, 5, 4, 5, 0.0F, false));
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scale) {
		if (((int) ageInTicks) % 80 > 3) bipedHead = bipedHeadOrigin;
		else bipedHead = bipedHeadEyeClose;
		this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
		headdress.rotateAngleZ = MathHelper.cos(ageInTicks * 0.045f) * 0.2f - 1.0472F;
		bipedHead.render(scale);
		bipedRightLeg.render(scale);
		bipedLeftLeg.render(scale);
		bipedBody.render(scale);
		bipedRightArm.render(scale);
		bipedLeftArm.render(scale);
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scaleFactor, Entity entityIn) {
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
		// EntityElfBase elf = (EntityElfBase) entityIn;
	}

}
