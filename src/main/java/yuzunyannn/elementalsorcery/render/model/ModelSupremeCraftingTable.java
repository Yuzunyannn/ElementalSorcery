package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBed;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSupremeCraftingTable extends ModelBase {

	ModelRenderer i;
	ModelRenderer b;
	ModelRenderer l1;
	ModelRenderer l2;
	ModelRenderer l3;
	ModelRenderer l4;

	ModelRenderer lt1;
	ModelRenderer lm1;
	ModelRenderer lb1;

	ModelRenderer lt2;
	ModelRenderer lm2;
	ModelRenderer lb2;

	ModelRenderer lt3;
	ModelRenderer lm3;
	ModelRenderer lb3;

	ModelRenderer lt4;
	ModelRenderer lm4;
	ModelRenderer lb4;

	public ModelSupremeCraftingTable() {
		textureWidth = 128;
		textureHeight = 128;

		i = new ModelRenderer(this, 0, 46);
		i.addBox(-10F, 0F, -10F, 20, 3, 20);
		i.setRotationPoint(0F, 20F, 0F);
		i.setTextureSize(128, 128);
		setRotation(i, 0F, 0F, 0F);

		b = new ModelRenderer(this, 0, 0);
		b.addBox(-16F, 0F, -16F, 32, 4, 32);
		b.setRotationPoint(0F, 16F, 0F);
		b.setTextureSize(128, 128);
		setRotation(b, 0F, 0F, 0F);

		l1 = new ModelRenderer(this, 0, 37);
		l1.addBox(-16F, 0F, -16F, 28, 4, 4);
		l1.setRotationPoint(0F, 20F, 0F);
		l1.setTextureSize(128, 128);
		setRotation(l1, 0F, 0F, 0F);

		l2 = new ModelRenderer(this, 0, 37);
		l2.addBox(-16F, 0F, -16F, 28, 4, 4);
		l2.setRotationPoint(0F, 20F, 0F);
		l2.setTextureSize(128, 128);
		setRotation(l2, 0F, 1.570796F, 0F);

		l3 = new ModelRenderer(this, 0, 37);
		l3.addBox(-16F, 0F, -16F, 28, 4, 4);
		l3.setRotationPoint(0F, 20F, 0F);
		l3.setTextureSize(128, 128);
		setRotation(l3, -3.141593F, 0F, -3.141593F);

		l4 = new ModelRenderer(this, 0, 37);
		l4.addBox(-16F, 0F, -16F, 28, 4, 4);
		l4.setRotationPoint(0F, 20F, 0F);
		l4.setTextureSize(128, 128);
		setRotation(l4, -3.141593F, 1.570796F, -3.141593F);

		lt2 = new ModelRenderer(this, 0, 70);
		lt2.addBox(-5F, 0F, -5F, 10, 2, 10);
		lt2.setRotationPoint(-10F, 14F, 10F);
		lt2.setTextureSize(128, 128);
		setRotation(lt2, 0F, 0F, 0F);

		lm2 = new ModelRenderer(this, 0, 83);
		lm2.addBox(-4F, 0F, -4F, 8, 10, 8);
		lm2.setRotationPoint(-10F, 4F, 10F);
		lm2.setTextureSize(128, 128);
		setRotation(lm2, 0F, 0F, 0F);

		lt1 = new ModelRenderer(this, 0, 70);
		lt1.addBox(-5F, 0F, -5F, 10, 2, 10);
		lt1.setRotationPoint(10F, 14F, 10F);
		lt1.setTextureSize(128, 128);
		setRotation(lt1, 0F, 0F, 0F);

		lt3 = new ModelRenderer(this, 0, 70);
		lt3.addBox(-5F, 0F, -5F, 10, 2, 10);
		lt3.setRotationPoint(-10F, 14F, -10F);
		lt3.setTextureSize(128, 128);
		setRotation(lt3, 0F, 0F, 0F);

		lt4 = new ModelRenderer(this, 0, 70);
		lt4.addBox(-5F, 0F, -5F, 10, 2, 10);
		lt4.setRotationPoint(10F, 14F, -10F);
		lt4.setTextureSize(128, 128);
		setRotation(lt4, 0F, 0F, 0F);

		lm3 = new ModelRenderer(this, 0, 83);
		lm3.addBox(-4F, 0F, -4F, 8, 10, 8);
		lm3.setRotationPoint(-10F, 4F, -10F);
		lm3.setTextureSize(128, 128);
		setRotation(lm3, 0F, 0F, 0F);

		lm4 = new ModelRenderer(this, 0, 83);
		lm4.addBox(-4F, 0F, -4F, 8, 10, 8);
		lm4.setRotationPoint(10F, 4F, -10F);
		lm4.setTextureSize(128, 128);
		setRotation(lm4, 0F, 0F, 0F);

		lm1 = new ModelRenderer(this, 0, 83);
		lm1.addBox(-4F, 0F, -4F, 8, 10, 8);
		lm1.setRotationPoint(10F, 4F, 10F);
		lm1.setTextureSize(128, 128);
		setRotation(lm1, 0F, 0F, 0F);

		lb1 = new ModelRenderer(this, 33, 83);
		lb1.addBox(-5F, 0F, -5F, 10, 4, 10);
		lb1.setRotationPoint(10F, 0F, 10F);
		lb1.setTextureSize(128, 128);
		setRotation(lb1, 0F, 0F, 0F);

		lb2 = new ModelRenderer(this, 33, 83);
		lb2.addBox(-5F, 0F, -5F, 10, 4, 10);
		lb2.setRotationPoint(-10F, 0F, 10F);
		lb2.setTextureSize(128, 128);
		setRotation(lb2, 0F, 0F, 0F);

		lb3 = new ModelRenderer(this, 33, 83);
		lb3.addBox(-5F, 0F, -5F, 10, 4, 10);
		lb3.setRotationPoint(-10F, 0F, -10F);
		lb3.setTextureSize(128, 128);
		setRotation(lb3, 0F, 0F, 0F);

		lb4 = new ModelRenderer(this, 33, 83);
		lb4.addBox(-5F, 0F, -5F, 10, 4, 10);
		lb4.setRotationPoint(10F, 0F, -10F);
		lb4.setTextureSize(128, 128);
		setRotation(lb4, 0F, 0F, 0F);
	}

	@Override
	public void render(Entity entity, float legR, float legTheta, float roate, float f0, float f1, float scale) {
		legR += 14.1421356f;
		legTheta += 45;
		legTheta = legTheta / 180.0f * 3.1514926f;
		roate = roate / 180.0f * 3.1514926f;
		this.setLegPosition(legR, legTheta, -roate);
		this.setPosition(roate);
		i.render(scale);
		b.render(scale);

		l1.render(scale);
		l2.render(scale);
		l3.render(scale);
		l4.render(scale);

		lt1.render(scale);
		lm1.render(scale);
		lb1.render(scale);

		lt2.render(scale);
		lm2.render(scale);
		lb2.render(scale);

		lt3.render(scale);
		lm3.render(scale);
		lb3.render(scale);

		lt4.render(scale);
		lm4.render(scale);
		lb4.render(scale);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public static float roateToHight(float roate) {
		return (MathHelper.sin(roate - 1.5707963f) + 1) * 8f;
	}

	private void setPosition(float roate) {
		float h = roateToHight(roate);
		i.setRotationPoint(0F, 20F + h, 0F);
		b.setRotationPoint(0F, 16F + h, 0F);
		l1.setRotationPoint(0F, 20F + h, 0F);
		l2.setRotationPoint(0F, 20F + h, 0F);
		l3.setRotationPoint(0F, 20F + h, 0F);
		l4.setRotationPoint(0F, 20F + h, 0F);

		i.rotateAngleY = roate;
		b.rotateAngleY = roate;
		l1.rotateAngleY = roate;
		l2.rotateAngleY = roate + 1.5707963f;
		l3.rotateAngleY = -roate;
		l4.rotateAngleY = -roate - 1.5707963f;
	}

	private void setLegPosition(float r, float theta, float roate) {
		float height = (r - 14.142136f);
		float h = (MathHelper.sin(roate * 1.2f - 1.5707963f) + 1) * height * 0.25f + height * 0.3f;
		this.setLegPosition(r, h, theta, roate, lt1, lm1, lb1);
		theta += 1.5707963f;
		this.setLegPosition(r, h, theta, roate, lt2, lm2, lb2);
		theta += 1.5707963f;
		this.setLegPosition(r, h, theta, roate, lt3, lm3, lb3);
		theta += 1.5707963f;
		this.setLegPosition(r, h, theta, roate, lt4, lm4, lb4);
	}

	private void setLegPosition(float r, float h, float theta, float roate, ModelRenderer lt, ModelRenderer lm,
			ModelRenderer lb) {
		float x = r * MathHelper.cos(theta);
		float z = r * MathHelper.sin(theta);
		lt.setRotationPoint(x, 14F + h, z);
		lm.setRotationPoint(x, 4F + h, z);
		lb.setRotationPoint(x, 0F + h, z);

		lt.rotateAngleY = roate;
		lm.rotateAngleY = roate;
		lb.rotateAngleY = roate;
	}

}
