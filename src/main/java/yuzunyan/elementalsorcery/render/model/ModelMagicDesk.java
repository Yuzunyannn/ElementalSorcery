package yuzunyan.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelMagicDesk extends ModelBase {
	public ModelRenderer table;
	public ModelRenderer leg;
	public ModelRenderer bottom;

	public ModelMagicDesk() {
		this.table = new ModelRenderer(this).setTextureOffset(0, 0).addBox(-4.0F, -2.0F, -4.0F, 8, 4, 8);
		this.table.setRotationPoint(0.0f, 2.0f, 0.0f);
		this.leg = new ModelRenderer(this).setTextureOffset(0, 12).addBox(-2.0F, -5.0F, -2.0F, 4, 10, 4);
		this.leg.setRotationPoint(0.0f, -5.0f, 0.0f);
		this.bottom = new ModelRenderer(this).setTextureOffset(16, 12).addBox(-3.0F, -1.0F, -3.0F, 6, 2, 6);
		this.bottom.setRotationPoint(0.0f, -11.0f, 0.0f);

		this.table.rotateAngleX = (float) Math.PI;
		this.leg.rotateAngleX = (float) Math.PI;
		this.bottom.rotateAngleX = (float) Math.PI;
	}

	public void render(Entity entityIn, float dtick, float n1, float n2, float n3, float n4, float scale) {
		// this.setRotationAngles(dtick, n1, n2, n3, n4, scale, entityIn);
		this.table.render(scale);
		this.leg.render(scale);
		this.bottom.render(scale);
	}

	public void setRotationAngles(float dtick, float n1, float n2, float n3, float n4, float scale, Entity entityIn) {
	}
}
