package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ModelBuildingAltar extends ModelBase {

	ModelRenderer b0;
	ModelRenderer b1;
	ModelRenderer t0;
	ModelRenderer t1;
	ModelRenderer l1;
	ModelRenderer l2;
	ModelRenderer l3;
	ModelRenderer l4;
	ModelRenderer f1;

	public ModelBuildingAltar() {
		textureWidth = 64;
		textureHeight = 64;
		b0 = new ModelRenderer(this, 0, 0);
		b0.addBox(-8F, -1F, -8F, 16, 1, 16);
		b0.setRotationPoint(0F, 1F, 0F);
		b0.setTextureSize(64, 64);
		setRotation(b0, 0F, 0F, 0F);
		t0 = new ModelRenderer(this, 0, 0);
		t0.addBox(-8F, 0F, -8F, 16, 1, 16);
		t0.setRotationPoint(0F, 15F, 0F);
		t0.setTextureSize(64, 64);
		setRotation(t0, 0F, 0F, 0F);
		b1 = new ModelRenderer(this, 0, 17);
		b1.addBox(-5F, 0F, -5F, 10, 1, 10);
		b1.setRotationPoint(0F, 1F, 0F);
		b1.setTextureSize(64, 64);
		setRotation(b1, 0F, 0F, 0F);
		t1 = new ModelRenderer(this, 0, 17);
		t1.addBox(-5F, 0F, -5F, 10, 1, 10);
		t1.setRotationPoint(0F, 15F, 0F);
		t1.setTextureSize(64, 64);
		setRotation(t1, 0F, 0F, 3.141593F);

		l1 = new ModelRenderer(this, 41, 18);
		l1.addBox(-0.5F, 0F, -0.5F, 1, 8, 1);
		l1.setRotationPoint(7F, 1F, -7F);
		l1.setTextureSize(64, 64);
		l1.rotateAngleX = 45 / 180.0f * 3.1415926f;
		l2 = new ModelRenderer(this, 41, 18);
		l2.addBox(-0.5F, 0F, -0.5F, 1, 8, 1);
		l2.setRotationPoint(7F, 1F, 7F);
		l2.setTextureSize(64, 64);
		l2.rotateAngleZ = 45 / 180.0f * 3.1415926f;
		l3 = new ModelRenderer(this, 41, 18);
		l3.addBox(-0.5F, 0F, -0.5F, 1, 8, 1);
		l3.setRotationPoint(-7F, 1F, -7F);
		l3.setTextureSize(64, 64);
		l3.rotateAngleZ = -45 / 180.0f * 3.1415926f;
		l4 = new ModelRenderer(this, 41, 18);
		l4.addBox(-0.5F, 0F, -0.5F, 1, 8, 1);
		l4.setRotationPoint(-7F, 1F, 7F);
		l4.setTextureSize(64, 64);
		l4.rotateAngleX = -45 / 180.0f * 3.1415926f;

		f1 = new ModelRenderer(this, 0, 28);
		f1.addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3);
		f1.setRotationPoint(0F, 10F, 0F);
		f1.setTextureSize(64, 64);
		setRotation(f1, 0F, 0F, 0F);
	}

	public void render(Entity entity, float roateAt, float n2, float n3, float n4, float n5, float scale) {
		super.render(entity, roateAt, n2, n3, n4, n5, scale);
		this.setAnimeRotation(roateAt);
		b0.render(scale);
		b1.render(scale);
		t0.render(scale);
		t1.render(scale);

		l1.render(scale);
		l2.render(scale);
		l3.render(scale);
		l4.render(scale);

		f1.render(scale);

	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	private void setAnimeRotation(float roateAt) {
		roateAt = roateAt / 180.0f * 3.1514926f * 1.25f;
		ModelBuildingAltar.setAnimeRotation(f1, roateAt);

		float hight = MathHelper.sin(roateAt) * 1 + 15;
		t0.setRotationPoint(0, hight, 0);
		t1.setRotationPoint(0, hight, 0);
	}

	private static void setAnimeRotation(ModelRenderer mr, float roateAt) {
		float hight = MathHelper.sin(roateAt) * 3 + 9;
		float x = MathHelper.sin(roateAt) * 10;
		float z = MathHelper.cos(roateAt) * 10;
		mr.setRotationPoint(x, hight, z);
		mr.rotateAngleY = roateAt;
	}

	public static Vec3d getRotationPos(float roateAt) {
		float y = MathHelper.sin(roateAt) * 3 + 9;
		float x = MathHelper.sin(roateAt) * 10;
		float z = MathHelper.cos(roateAt) * 10;
		return new Vec3d(x, y, z);
	}

}
