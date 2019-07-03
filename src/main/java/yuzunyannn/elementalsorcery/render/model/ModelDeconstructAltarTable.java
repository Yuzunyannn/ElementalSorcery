
package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelDeconstructAltarTable extends ModelBase {
	ModelRenderer bottom0;
	ModelRenderer bottom1;
	ModelRenderer leg;
	ModelRenderer table;
	ModelRenderer col1;
	ModelRenderer col2;
	ModelRenderer col3;
	ModelRenderer col4;

	public ModelDeconstructAltarTable() {
		textureWidth = 64;
		textureHeight = 64;

		bottom0 = new ModelRenderer(this, 0, 0);
		bottom0.addBox(-6F, 0F, -6F, 12, 2, 12);
		bottom0.setRotationPoint(0F, 2F, 0F);
		bottom0.setTextureSize(64, 64);
		bottom0.mirror = true;
		setRotation(bottom0, 3.141593F, 0F, 0F);
		bottom1 = new ModelRenderer(this, 18, 26);
		bottom1.addBox(-3F, 0F, -3F, 6, 2, 6);
		bottom1.setRotationPoint(0F, 4F, 0F);
		bottom1.setTextureSize(64, 64);
		bottom1.mirror = true;
		setRotation(bottom1, 3.141593F, 0F, 0F);
		leg = new ModelRenderer(this, 9, 26);
		leg.addBox(-1F, 0F, -1F, 2, 8, 2);
		leg.setRotationPoint(0F, 12F, 0F);
		leg.setTextureSize(64, 64);
		leg.mirror = true;
		setRotation(leg, 3.141593F, 0F, 0F);
		table = new ModelRenderer(this, 0, 15);
		table.addBox(-4F, 0F, -4F, 8, 2, 8);
		table.setRotationPoint(0F, 14F, 0F);
		table.setTextureSize(64, 64);
		table.mirror = true;
		setRotation(table, 3.141593F, 0F, 0F);
		col1 = new ModelRenderer(this, 0, 26);
		col1.addBox(-1F, 0F, -1F, 2, 16, 2);
		col1.setRotationPoint(7F, 16F, 0F);
		col1.setTextureSize(64, 64);
		col1.mirror = true;
		setRotation(col1, 3.141593F, -1.570796F, 0F);
		col2 = new ModelRenderer(this, 0, 26);
		col2.addBox(-1F, 0F, -1F, 2, 16, 2);
		col2.setRotationPoint(-7F, 16F, 0F);
		col2.setTextureSize(64, 64);
		col2.mirror = true;
		setRotation(col2, 3.141593F, 1.570796F, 0F);
		col3 = new ModelRenderer(this, 0, 26);
		col3.addBox(-1F, 0F, -1F, 2, 16, 2);
		col3.setRotationPoint(0F, 16F, -7F);
		col3.setTextureSize(64, 64);
		col3.mirror = true;
		setRotation(col3, 3.141593F, 0F, 0F);
		col4 = new ModelRenderer(this, 0, 26);
		col4.addBox(-1F, 0F, -1F, 2, 16, 2);
		col4.setRotationPoint(0F, 16F, 7F);
		col4.setTextureSize(64, 64);
		col4.mirror = true;
		setRotation(col4, 3.141593F, 3.141593F, 0F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		bottom0.render(f5);
		bottom1.render(f5);
		leg.render(f5);
		table.render(f5);
		col1.render(f5);
		col2.render(f5);
		col3.render(f5);
		col4.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
