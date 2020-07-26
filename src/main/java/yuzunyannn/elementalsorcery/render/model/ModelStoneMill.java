package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelStoneMill extends ModelBase {
	ModelRenderer b;
	ModelRenderer l1;
	ModelRenderer l2;
	ModelRenderer l3;
	ModelRenderer l4;
	ModelRenderer hhead;
	ModelRenderer hhandle;

	public ModelStoneMill() {
		textureWidth = 64;
		textureHeight = 64;

		b = new ModelRenderer(this, 0, 0);
		b.addBox(-6F, 0F, -6F, 12, 2, 12);
		b.setRotationPoint(0F, 1F, 0F);
		b.setTextureSize(64, 64);

		l1 = new ModelRenderer(this, 0, 15);
		l1.addBox(-8F, 0F, -1F, 16, 14, 2);
		l1.setRotationPoint(0F, 0F, -7F);
		l1.setTextureSize(64, 64);

		l2 = new ModelRenderer(this, 0, 15);
		l2.addBox(-8F, 0F, -1F, 16, 14, 2);
		l2.setRotationPoint(0F, 0F, 7F);
		l2.setTextureSize(64, 64);

		l3 = new ModelRenderer(this, 0, 32);
		l3.addBox(-1F, 0F, -6F, 2, 14, 12);
		l3.setRotationPoint(-7F, 0F, 0F);
		l3.setTextureSize(64, 64);

		l4 = new ModelRenderer(this, 0, 32);
		l4.addBox(-1F, 0F, -6F, 2, 14, 12);
		l4.setRotationPoint(7F, 0F, 0F);
		l4.setTextureSize(64, 64);

		hhead = new ModelRenderer(this, 37, 15);
		hhead.addBox(-3F, -14.5F, -1.5F, 6, 3, 3);
		hhead.setRotationPoint(0F, 14.5F, -10F);
		hhead.setTextureSize(64, 64);

		hhandle = new ModelRenderer(this, 37, 22);
		hhandle.addBox(-1F, -11.5F, -1F, 2, 16, 2);
		hhandle.setRotationPoint(0F, 14.5F, -10F);
		hhandle.setTextureSize(64, 64);

	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale) {
		b.render(scale);
		l1.render(scale);
		l2.render(scale);
		l3.render(scale);
		l4.render(scale);
		if (f == 0) this.renderHammer(0, 0, 0, scale);
	}

	public void renderHammer(float lift, float hit, float hight, float scale) {
		float hitAt = MathHelper.sin(hit);
		float x = ((-0.75f - 0.3f * hitAt * (1.0f - hight)) * lift + -0.215f * (1.0f - lift));
		float y = (0.20f * lift + 0.025f * (1.0f - lift));
		float z = 0.25f * (1.0f - lift);
		this.setHammerPosition(x * 16, y * 16, z * 16);
		if (hitAt < 0) {
			this.setHammerRoate(3.1415926f * (0.5f + 0.3f * (1.0f - hight) * hitAt));
		} else this.setHammerRoate(3.1415926f * (0.5f + 0.3f * hitAt));
		hhead.render(scale);
		hhandle.render(scale);
	}

	public void setHammerRoate(float theta) {
		hhead.rotateAngleZ = theta;
		hhandle.rotateAngleZ = theta;
	}

	public void setHammerPosition(float x, float y, float z) {
		hhead.setRotationPoint(x, y + 14.5F, z);
		hhandle.setRotationPoint(x, y + 14.5F, z);
	}

}
