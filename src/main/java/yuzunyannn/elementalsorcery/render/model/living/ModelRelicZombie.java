package yuzunyannn.elementalsorcery.render.model.living;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelRelicZombie extends ModelBase {

	private final ModelRenderer head;
	private final ModelRenderer rightHeaddress;
	private final ModelRenderer leftHeaddress;
	private final ModelRenderer body;
	private final ModelRenderer bodyCore;
	private final ModelRenderer leftLeg;
	private final ModelRenderer leftLegC;
	private final ModelRenderer rightLeg;
	private final ModelRenderer rightLegC;
	private final ModelRenderer leftArm;
	private final ModelRenderer leftArmC;
	private final ModelRenderer rightArm;
	private final ModelRenderer rightArmC;

	public ModelRelicZombie() {
		textureWidth = 64;
		textureHeight = 64;

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, -0.3333F, 0.0F);
		head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -7.6667F, -4.0F, 8, 8, 8, 0.0F, false));

		rightHeaddress = new ModelRenderer(this);
		rightHeaddress.setRotationPoint(-4.0F, -7.9167F, 0.0F);
		head.addChild(rightHeaddress);
		rightHeaddress.cubeList.add(new ModelBox(rightHeaddress, 0, 16, -1.0F, -0.75F, -2.0F, 1, 2, 4, 0.0F, false));
		rightHeaddress.cubeList.add(new ModelBox(rightHeaddress, 0, 22, 0.0F, -0.75F, -2.0F, 1, 1, 4, 0.0F, false));

		leftHeaddress = new ModelRenderer(this);
		leftHeaddress.setRotationPoint(4.0F, -7.9167F, 0.0F);
		head.addChild(leftHeaddress);
		leftHeaddress.cubeList.add(new ModelBox(leftHeaddress, 0, 16, 0.0F, -0.75F, -2.0F, 1, 2, 4, 0.0F, false));
		leftHeaddress.cubeList.add(new ModelBox(leftHeaddress, 0, 22, -1.0F, -0.75F, -2.0F, 1, 1, 4, 0.0F, false));

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 6.6F, 0.0F);
		body.cubeList.add(new ModelBox(body, 0, 27, -5.0F, -6.6F, -3.0F, 10, 3, 6, 0.0F, false));
		body.cubeList.add(new ModelBox(body, 0, 36, -5.0F, 2.4F, -3.0F, 10, 3, 6, 0.0F, false));
		body.cubeList.add(new ModelBox(body, 16, 45, 3.0F, -2.6F, -3.0F, 2, 5, 6, 0.0F, false));
		body.cubeList.add(new ModelBox(body, 0, 45, -5.0F, -3.6F, -3.0F, 2, 5, 6, 0.0F, false));

		bodyCore = new ModelRenderer(this);
		bodyCore.setRotationPoint(0.0F, -0.6F, 0.0F);
		body.addChild(bodyCore);
		bodyCore.cubeList.add(new ModelBox(bodyCore, 10, 16, 0.0F, 0.0F, -1.0F, 1, 1, 1, 0.0F, false));
		bodyCore.cubeList.add(new ModelBox(bodyCore, 10, 16, -1.0F, -1.0F, -1.0F, 1, 1, 1, 0.0F, false));
		bodyCore.cubeList.add(new ModelBox(bodyCore, 10, 16, -1.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F, false));
		bodyCore.cubeList.add(new ModelBox(bodyCore, 10, 16, 0.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));

		leftLeg = new ModelRenderer(this);
		leftLeg.setRotationPoint(3.0F, 12.0F, 0.0F);
		leftLeg.cubeList.add(new ModelBox(leftLeg, 32, 0, -2.0F, 0.0F, -2.0F, 4, 2, 4, 0.0F, false));
		leftLeg.cubeList.add(new ModelBox(leftLeg, 48, 0, -2.0F, 8.0F, -2.0F, 4, 4, 4, 0.0F, false));

		leftLegC = new ModelRenderer(this);
		leftLegC.setRotationPoint(0.0F, 0.0F, 0.0F);
		leftLeg.addChild(leftLegC);
		leftLegC.cubeList.add(new ModelBox(leftLegC, 32, 6, -1.0F, 3.0F, -1.0F, 2, 4, 2, 0.0F, false));

		rightLeg = new ModelRenderer(this);
		rightLeg.setRotationPoint(-3.0F, 12.0F, 0.0F);
		rightLeg.cubeList.add(new ModelBox(rightLeg, 32, 0, -2.0F, 0.0F, -2.0F, 4, 2, 4, 0.0F, false));
		rightLeg.cubeList.add(new ModelBox(rightLeg, 48, 0, -2.0F, 8.0F, -2.0F, 4, 4, 4, 0.0F, false));

		rightLegC = new ModelRenderer(this);
		rightLegC.setRotationPoint(0.0F, 0.0F, 0.0F);
		rightLeg.addChild(rightLegC);
		rightLegC.cubeList.add(new ModelBox(rightLegC, 32, 6, -1.0F, 3.0F, -1.0F, 2, 4, 2, 0.0F, false));

		leftArm = new ModelRenderer(this);
		leftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		leftArm.cubeList.add(new ModelBox(leftArm, 32, 12, 0.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F, false));
		leftArm.cubeList.add(new ModelBox(leftArm, 48, 12, 0.0F, 6.0F, -2.0F, 4, 4, 4, 0.0F, false));

		leftArmC = new ModelRenderer(this);
		leftArmC.setRotationPoint(2.0F, 4.0F, 0.0F);
		leftArm.addChild(leftArmC);
		leftArmC.cubeList.add(new ModelBox(leftArmC, 48, 8, -1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));

		rightArm = new ModelRenderer(this);
		rightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		rightArm.cubeList.add(new ModelBox(rightArm, 32, 12, -4.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F, false));
		rightArm.cubeList.add(new ModelBox(rightArm, 48, 12, -4.0F, 6.0F, -2.0F, 4, 4, 4, 0.0F, false));

		rightArmC = new ModelRenderer(this);
		rightArmC.setRotationPoint(-2.0F, 4.0F, 0.0F);
		rightArm.addChild(rightArmC);
		rightArmC.cubeList.add(new ModelBox(rightArmC, 48, 8, -1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));
	}

	public void setHasCore(boolean hasCore) {
		bodyCore.isHidden = !hasCore;
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		head.render(f5);
		body.render(f5);
		rightLeg.render(f5);
		leftLeg.render(f5);
		rightArm.render(f5);
		leftArm.render(f5);
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scaleFactor, Entity entity) {
		head.rotateAngleY = netHeadYaw * 0.017453292F;
		head.rotateAngleX = headPitch * 0.017453292F;
		// 漂浮动画
		head.rotationPointY = -1.75f - (MathHelper.cos(ageInTicks * 0.06F) + 1) * 0.5f;

		float cos1 = MathHelper.cos(ageInTicks * 0.03F);
		leftHeaddress.rotationPointX = 4.5f + cos1 * 0.5f;
		leftHeaddress.rotationPointY = -8.5f - cos1 * 0.5f;
		rightHeaddress.rotationPointX = -4.5f - cos1 * 0.5f;
		rightHeaddress.rotationPointY = -8.5f - cos1 * 0.5f;

		float sin1 = MathHelper.sin(ageInTicks * 0.01F);
		rightLegC.rotateAngleY = ageInTicks * 0.01F;
		leftLegC.rotateAngleY = -ageInTicks * 0.01F;
		rightArmC.rotateAngleY = ageInTicks * 0.01F;
		leftArmC.rotateAngleY = -ageInTicks * 0.01F;
		rightLegC.rotationPointY = sin1 * 0.5f;
		leftLegC.rotationPointY = sin1 * 0.5f;
		rightArmC.rotationPointY = 4 + sin1 * 0.5f;
		leftArmC.rotationPointY = 4 + sin1 * 0.5f;

		float cos2 = MathHelper.cos(ageInTicks * 0.04F);
		body.rotationPointY = 5.5f + cos2 * 0.5f;

		float sin2 = MathHelper.sin(ageInTicks * 0.04F);
		rightArm.rotationPointY = 1.5f + sin2 * 0.5f;
		leftArm.rotationPointY = 1.5f + sin2 * 0.5f;

		bodyCore.rotationPointY = -1 + cos1;
		bodyCore.rotateAngleY = ageInTicks * 0.06F;

		rightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2 * limbSwingAmount * 0.5F;
		leftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
		rightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		leftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;

		rightArm.rotateAngleZ = 0.0F;
		leftArm.rotateAngleZ = 0.0F;
		rightArm.rotateAngleY = 0.0F;
		leftArm.rotateAngleY = 0.0F;

		if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isHandActive()) {
			rightArm.rotateAngleX = ageInTicks * 0.25f * 3.14f;
		} else if (this.swingProgress > 0.0F) {
			EnumHandSide enumhandside = this.getMainHand(entity);
			ModelRenderer modelrenderer = this.getArmForSide(enumhandside);
			float f1 = this.swingProgress;
			body.rotateAngleY = MathHelper.sin(MathHelper.sqrt(f1) * ((float) Math.PI * 2F)) * 0.2F;
			if (enumhandside == EnumHandSide.LEFT) body.rotateAngleY *= -1.0F;
			rightArm.rotationPointZ = MathHelper.sin(body.rotateAngleY) * 5.0F;
			rightArm.rotationPointX = -MathHelper.cos(body.rotateAngleY) * 5.0F;
			leftArm.rotationPointZ = -MathHelper.sin(body.rotateAngleY) * 5.0F;
			leftArm.rotationPointX = MathHelper.cos(body.rotateAngleY) * 5.0F;
			rightArm.rotateAngleY += body.rotateAngleY;
			leftArm.rotateAngleY += body.rotateAngleY;
			leftArm.rotateAngleX += body.rotateAngleY;
			f1 = 1.0F - this.swingProgress;
			f1 = f1 * f1;
			f1 = f1 * f1;
			f1 = 1.0F - f1;
			float f2 = MathHelper.sin(f1 * (float) Math.PI);
			float f3 = MathHelper.sin(this.swingProgress * (float) Math.PI) * -(head.rotateAngleX - 0.7F) * 0.75F;
			modelrenderer.rotateAngleX = modelrenderer.rotateAngleX - (f2 * 1.2f + f3);
			modelrenderer.rotateAngleY += body.rotateAngleY * 2.0F;
			modelrenderer.rotateAngleZ += MathHelper.sin(this.swingProgress * (float) Math.PI) * -0.4F;
		}

		rightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		leftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		rightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
		leftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;

		float mod = ageInTicks % 200;
		if (mod < 10) head.rotateAngleY += mod / 10 * 3.14f * 2;
	}

	protected EnumHandSide getMainHand(Entity entityIn) {
		if (entityIn instanceof EntityLivingBase) {
			EntityLivingBase entitylivingbase = (EntityLivingBase) entityIn;
			EnumHandSide enumhandside = entitylivingbase.getPrimaryHand();
			return entitylivingbase.swingingHand == EnumHand.MAIN_HAND ? enumhandside : enumhandside.opposite();
		} else {
			return EnumHandSide.RIGHT;
		}
	}

	protected ModelRenderer getArmForSide(EnumHandSide side) {
		return side == EnumHandSide.LEFT ? this.leftArm : this.rightArm;
	}

	public void postRenderArm(float scale, EnumHandSide side) {
		this.getArmForSide(side).postRender(scale);
	}

}
