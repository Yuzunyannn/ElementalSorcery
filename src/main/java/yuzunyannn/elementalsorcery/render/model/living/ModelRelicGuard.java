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
import yuzunyannn.elementalsorcery.entity.mob.EntityRelicGuard;

@SideOnly(Side.CLIENT)
public class ModelRelicGuard extends ModelBase {

	private final ModelRenderer headdress;
	private final ModelRenderer head;
	private final ModelRenderer body;
	private final ModelRenderer leftLeg;
	private final ModelRenderer leftLegC;
	private final ModelRenderer rightLeg;
	private final ModelRenderer rightLegC;
	private final ModelRenderer leftArm;
	private final ModelRenderer leftArmC;
	private final ModelRenderer rightArm;
	private final ModelRenderer rightArmC;

	public float activeRate = 1;
	public float activeTick = 0;
	public boolean isActive = true;

	public ModelRelicGuard() {
		textureWidth = 64;
		textureHeight = 64;

		headdress = new ModelRenderer(this);
		headdress.setRotationPoint(0.0F, -9.625F, 0.0F);
		headdress.cubeList.add(new ModelBox(headdress, 0, 16, -5.0F, -0.375F, -2.0F, 1, 1, 4, 0.0F, false));
		headdress.cubeList.add(new ModelBox(headdress, 0, 16, 4.0F, -0.375F, -2.0F, 1, 1, 4, 0.0F, false));
		headdress.cubeList.add(new ModelBox(headdress, 0, 22, -2.0F, -0.625F, -5.0F, 4, 1, 1, 0.0F, false));
		headdress.cubeList.add(new ModelBox(headdress, 0, 22, -2.0F, -0.625F, 4.0F, 4, 1, 1, 0.0F, false));

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, -0.3333F, 0.0F);
		head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -7.6667F, -4.0F, 8, 8, 8, 0.0F, false));

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 6.6F, 0.0F);
		body.cubeList.add(new ModelBox(body, 0, 27, -5.0F, -6.6F, -3.0F, 10, 3, 6, 0.0F, false));
		body.cubeList.add(new ModelBox(body, 0, 36, -5.0F, 2.4F, -3.0F, 10, 3, 6, 0.0F, false));
		body.cubeList.add(new ModelBox(body, 16, 45, 3.0F, -3.6F, -3.0F, 2, 6, 6, 0.0F, false));
		body.cubeList.add(new ModelBox(body, 0, 45, -5.0F, -3.6F, -3.0F, 2, 6, 6, 0.0F, false));

		leftLeg = new ModelRenderer(this);
		leftLeg.setRotationPoint(3.0F, 12.0F, 0.0F);
		leftLeg.cubeList.add(new ModelBox(leftLeg, 32, 0, -2.0F, 0.0F, -2.0F, 4, 2, 4, 0.0F, false));
		leftLeg.cubeList.add(new ModelBox(leftLeg, 48, 0, -2.0F, 8.0F, -2.0F, 4, 4, 4, 0.0F, false));
		leftLeg.cubeList.add(new ModelBox(leftLeg, 48, 28, -2.0F, 2.0F, -2.0F, 4, 6, 4, 0.0F, false));

		leftLegC = new ModelRenderer(this);
		leftLegC.setRotationPoint(0.0F, 0.0F, 0.0F);
		leftLeg.addChild(leftLegC);
		leftLegC.cubeList.add(new ModelBox(leftLegC, 32, 6, -1.0F, 3.0F, -1.0F, 2, 4, 2, 0.0F, false));

		rightLeg = new ModelRenderer(this);
		rightLeg.setRotationPoint(-3.0F, 12.0F, 0.0F);
		rightLeg.cubeList.add(new ModelBox(rightLeg, 32, 0, -2.0F, 0.0F, -2.0F, 4, 2, 4, 0.0F, false));
		rightLeg.cubeList.add(new ModelBox(rightLeg, 48, 0, -2.0F, 8.0F, -2.0F, 4, 4, 4, 0.0F, false));
		rightLeg.cubeList.add(new ModelBox(rightLeg, 48, 28, -2.0F, 2.0F, -2.0F, 4, 6, 4, 0.0F, false));

		rightLegC = new ModelRenderer(this);
		rightLegC.setRotationPoint(0.0F, 0.0F, 0.0F);
		rightLeg.addChild(rightLegC);
		rightLegC.cubeList.add(new ModelBox(rightLegC, 32, 6, -1.0F, 3.0F, -1.0F, 2, 4, 2, 0.0F, false));

		leftArm = new ModelRenderer(this);
		leftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		leftArm.cubeList.add(new ModelBox(leftArm, 32, 12, 0.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F, false));
		leftArm.cubeList.add(new ModelBox(leftArm, 48, 12, 0.0F, 6.0F, -2.0F, 4, 4, 4, 0.0F, false));
		leftArm.cubeList.add(new ModelBox(leftArm, 48, 20, 0.0F, 2.0F, -2.0F, 4, 4, 4, 0.0F, false));

		leftArmC = new ModelRenderer(this);
		leftArmC.setRotationPoint(2.0F, 4.0F, 0.0F);
		leftArm.addChild(leftArmC);
		leftArmC.cubeList.add(new ModelBox(leftArmC, 48, 8, -1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));

		rightArm = new ModelRenderer(this);
		rightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		rightArm.cubeList.add(new ModelBox(rightArm, 32, 12, -4.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F, false));
		rightArm.cubeList.add(new ModelBox(rightArm, 48, 12, -4.0F, 6.0F, -2.0F, 4, 4, 4, 0.0F, false));
		rightArm.cubeList.add(new ModelBox(rightArm, 48, 20, -4.0F, 2.0F, -2.0F, 4, 4, 4, 0.0F, false));

		rightArmC = new ModelRenderer(this);
		rightArmC.setRotationPoint(-2.0F, 4.0F, 0.0F);
		rightArm.addChild(rightArmC);
		rightArmC.cubeList.add(new ModelBox(rightArmC, 48, 8, -1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale) {
		headdress.render(scale);
		head.render(scale);
		body.render(scale);
		leftLeg.render(scale);
		rightLeg.render(scale);
		leftArm.render(scale);
		rightArm.render(scale);
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scaleFactor, Entity entity) {
		float mod = ageInTicks % 200;
		ageInTicks = activeTick;

		head.rotateAngleY = netHeadYaw * 0.017453292F;
		head.rotateAngleX = headPitch * 0.017453292F;
		// 头漂浮动画
		float cos1 = (MathHelper.cos(ageInTicks * 0.06F) + 1);
		head.rotationPointY = -1f + cos1 * 0.15f;
		// 身体漂浮
		float cos2 = MathHelper.cos(ageInTicks * 0.04F);
		body.rotationPointY = 6.25f + cos2 * 0.25f;
		// 胳臂漂浮
		rightArm.rotationPointY = 1.7f + cos2 * 0.25f;
		leftArm.rotationPointY = 1.7f + cos2 * 0.25f;
		// 腿
		rightLeg.rotationPointY = 12f;
		leftLeg.rotationPointY = 12f;
		// 头饰
		headdress.rotationPointY = -12F + cos2 * cos1 * 0.5f;
		headdress.rotateAngleY = ageInTicks * 0.1f;
		if (mod < 10 || Math.random() < 0.02) headdress.rotateAngleY += Math.random() * 0.75f * activeRate;

		// 手臂腿中间的旋转
		float sin1 = MathHelper.sin(ageInTicks * 0.01F);
		rightLegC.rotateAngleY = ageInTicks * 0.01F;
		leftLegC.rotateAngleY = -ageInTicks * 0.01F;
		rightArmC.rotateAngleY = ageInTicks * 0.01F;
		leftArmC.rotateAngleY = -ageInTicks * 0.01F;
		rightLegC.rotationPointY = sin1 * 0.5f;
		leftLegC.rotationPointY = sin1 * 0.5f;
		rightArmC.rotationPointY = 4 + sin1 * 0.5f;
		leftArmC.rotationPointY = 4 + sin1 * 0.5f;

		// 姿势
		if (!isActive) {
			float drop = 10 * (1 - activeRate);
			head.rotationPointY += drop;
			rightArm.rotationPointY += drop;
			leftArm.rotationPointY += drop;
			body.rotationPointY += drop;
			leftLeg.rotationPointY += drop;
			rightLeg.rotationPointY += drop;
			headdress.rotationPointY += drop * 2.2f;

			rightArm.rotateAngleX = -((float) Math.PI / 5F);
			leftArm.rotateAngleX = -((float) Math.PI / 5F);
			rightLeg.rotateAngleX = -((float) Math.PI / 2F + 0.01f);
			rightLeg.rotateAngleY = ((float) Math.PI / 10F);
			rightLeg.rotateAngleZ = 0.07853982F;
			leftLeg.rotateAngleX = rightLeg.rotateAngleX;
			leftLeg.rotateAngleY = -((float) Math.PI / 10F);
			leftLeg.rotateAngleZ = -0.07853982F;

			rightArm.rotateAngleY = 0.0F;
			leftArm.rotateAngleY = 0.0F;
			rightArm.rotateAngleZ = 0.0F;
			leftArm.rotateAngleZ = 0.0F;

			return;
		} else {
			rightLeg.rotateAngleY = 0;
			leftLeg.rotateAngleY = 0;
			rightLeg.rotateAngleZ = 0;
			leftLeg.rotateAngleZ = 0;
		}

		// 手腿摇摆
		rightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2 * limbSwingAmount * 0.5F;
		leftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
		rightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		leftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;

		rightArm.rotateAngleZ = 0.07853982F;
		leftArm.rotateAngleZ = -0.07853982F;
		rightArm.rotateAngleY = 0.0F;
		leftArm.rotateAngleY = 0.0F;

		if (entity instanceof EntityRelicGuard && ((EntityRelicGuard) entity).isSpelling()) {
			float sw = 0.5f + MathHelper.sin(ageInTicks) * 0.1f;
			float f1 = sw;
			f1 = 1.0F - sw;
			f1 = f1 * f1;
			f1 = f1 * f1;
			f1 = 1.0F - f1;
			float f2 = MathHelper.sin(f1 * (float) Math.PI);
			float f3 = MathHelper.sin(sw * (float) Math.PI) * 2.5f;
			rightArm.rotateAngleX = rightArm.rotateAngleX - (f2 * 1.2f + f3);
			rightArm.rotateAngleZ += MathHelper.sin(sw * (float) Math.PI) * -0.75F;
			leftArm.rotateAngleX = leftArm.rotateAngleX - (f2 * 1.2f + f3);
			leftArm.rotateAngleZ += MathHelper.sin(sw * (float) Math.PI) * 0.75F;
		} else if (this.swingProgress > 0.0F) {
			float f1 = this.swingProgress;
			f1 = 1.0F - this.swingProgress;
			f1 = f1 * f1;
			f1 = f1 * f1;
			f1 = 1.0F - f1;
			float f2 = MathHelper.sin(f1 * (float) Math.PI);
			float f3 = MathHelper.sin(this.swingProgress * (float) Math.PI) * -(head.rotateAngleX - 0.7F) * 3F;
			rightArm.rotateAngleX = rightArm.rotateAngleX - (f2 * 1.2f + f3);
			rightArm.rotateAngleZ += MathHelper.sin(this.swingProgress * (float) Math.PI) * -0.75F;
			leftArm.rotateAngleX = leftArm.rotateAngleX - (f2 * 1.2f + f3);
			leftArm.rotateAngleZ += MathHelper.sin(this.swingProgress * (float) Math.PI) * 0.75F;
		}
		// 手摇摆
		rightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		leftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		rightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
		leftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;

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
