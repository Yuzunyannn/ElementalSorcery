package yuzunyan.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelStela extends ModelBase {
	ModelRenderer bottom;
	ModelRenderer st1;
	ModelRenderer st2;

	public ModelStela() {
		textureWidth = 64;
		textureHeight = 64;

		bottom = new ModelRenderer(this, 0, 0);
		bottom.addBox(-8F, 0F, -8F, 16, 4, 16);
		bottom.setRotationPoint(0F, 0F, 0F);
		bottom.setTextureSize(64, 64);
		setRotation(bottom, 0F, 0F, 0F);
		st1 = new ModelRenderer(this, 0, 20);
		st1.addBox(-3F, 0F, -4F, 6, 12, 9);
		st1.setRotationPoint(5F, 4F, -4F);
		st1.setTextureSize(64, 64);
		setRotation(st1, 0F, 0F, 0F);
		st2 = new ModelRenderer(this, 30, 20);
		st2.addBox(-5F, 0F, -2F, 8, 4, 7);
		st2.setRotationPoint(5F, 4F, 3F);
		st2.setTextureSize(64, 64);
		setRotation(st2, 0F, 0F, 0F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		bottom.render(f5);
		st1.render(f5);
		st2.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
