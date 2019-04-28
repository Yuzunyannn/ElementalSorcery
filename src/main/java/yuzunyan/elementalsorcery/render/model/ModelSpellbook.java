package yuzunyan.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSpellbook extends ModelBase {
	public ModelRenderer coverRight;
	public ModelRenderer coverLeft;
	public ModelRenderer pagesRight;
	public ModelRenderer pagesLeft;
	public ModelRenderer flippingPageRight;
	public ModelRenderer flippingPageLeft;
	public ModelRenderer bookSpine;

	public ModelSpellbook(int scaleLevel) {
		this.coverRight = (new ModelRenderer(this)).setTextureOffset(0, 0).addBox(-6.0F, -5.0F, 0.0F, 6, 10, 0);
		this.coverLeft = (new ModelRenderer(this)).setTextureOffset(16, 0).addBox(0.0F, -5.0F, 0.0F, 6, 10, 0);
		this.pagesRight = (new ModelRenderer(this)).setTextureOffset(0, 10).addBox(0.01F, -4.0F, -1.0F + 0.01F, 5, 8,
				1);
		this.pagesLeft = (new ModelRenderer(this)).setTextureOffset(12, 10).addBox(0.01F, -4.0F, -0.01F, 5, 8, 1);
		this.flippingPageRight = (new ModelRenderer(this)).setTextureOffset(24, 10).addBox(0.01F, -4.0F, 0.0F, 5, 8, 0);
		this.flippingPageLeft = (new ModelRenderer(this)).setTextureOffset(24, 10).addBox(0.01F, -4.0F, 0.0F, 5, 8, 0);
		this.bookSpine = (new ModelRenderer(this)).setTextureOffset(12, 0).addBox(-1.0F, -5.0F, 0.0F, 2, 10, 0);
		this.coverRight.setRotationPoint(0.0F, 0.0F, -1.0F);
		this.coverLeft.setRotationPoint(0.0F, 0.0F, 1.0F);
		this.bookSpine.rotateAngleY = ((float) Math.PI / 2F);
	}

	public void render(Entity entityIn, float dtick, float flip_right, float flip_left, float spread, float headPitch,
			float scale) {
		this.setRotationAngles(dtick, flip_right, flip_left, spread, headPitch, scale, entityIn);
		this.coverRight.render(scale);
		this.coverLeft.render(scale);
		this.pagesRight.render(scale);
		this.pagesLeft.render(scale);
		this.flippingPageRight.render(scale);
		this.flippingPageLeft.render(scale);
		this.bookSpine.render(scale);
	}

	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scaleFactor, Entity entityIn) {
		float f = (MathHelper.sin(limbSwing * 0.02F) * 0.1F + 1.25F) * netHeadYaw;
		this.coverRight.rotateAngleY = (float) Math.PI + f;
		this.coverLeft.rotateAngleY = -f;
		this.pagesRight.rotateAngleY = f;
		this.pagesLeft.rotateAngleY = -f;
		this.flippingPageRight.rotateAngleY = f - f * 2.0F * limbSwingAmount;
		this.flippingPageLeft.rotateAngleY = f - f * 2.0F * ageInTicks;
		this.pagesRight.rotationPointX = MathHelper.sin(f);
		this.pagesLeft.rotationPointX = MathHelper.sin(f);
		this.flippingPageRight.rotationPointX = MathHelper.sin(f);
		this.flippingPageLeft.rotationPointX = MathHelper.sin(f);
	}
}
