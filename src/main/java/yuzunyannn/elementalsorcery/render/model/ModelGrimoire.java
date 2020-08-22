package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelGrimoire extends ModelBase {

	public ModelRenderer coverRight;
	public ModelRenderer coverLeft;
	public ModelRenderer pagesRight;
	public ModelRenderer pagesLeft;
	public ModelRenderer flippingPageRight;
	public ModelRenderer flippingPageLeft;
	public ModelRenderer bookSpine;

	public ModelGrimoire() {
		textureWidth = 64;
		textureHeight = 64;
		final int sacle = 2;
		this.coverRight = new ModelRenderer(this).addBox(0, -5.0F, 0.0F, 6, 10, 0);
		this.coverRight.setTextureOffset(0, 0);
		this.coverLeft = new ModelRenderer(this).addBox(0, -5.0F, 0.0F, 6, 10, 0);
		this.coverLeft.setTextureOffset(16 * sacle, 0);

		this.coverRight.setRotationPoint(0.0F, 0.0F, -1.0F);
		this.coverLeft.setRotationPoint(0.0F, 0.0F, 1.0F);

		this.pagesRight = new ModelRenderer(this).addBox(0.01F, -4.0F, -1.0F + 0.01F, 5, 8, 1);
		this.pagesRight.setTextureOffset(0, 10 * sacle);
		this.pagesLeft = new ModelRenderer(this).addBox(0.01F, -4.0F, -0.01F, 5, 8, 1);
		this.pagesLeft.setTextureOffset(12 * sacle, 10 * sacle);

		this.flippingPageRight = new ModelRenderer(this).addBox(0.01F, -4.0F, 0.0F, 5, 8, 0);
		this.flippingPageRight.setTextureOffset(0 * sacle, 19 * sacle);

		this.flippingPageLeft = new ModelRenderer(this).addBox(0.01F, -4.0F, 0.0F, 5, 8, 0);
		this.flippingPageLeft.setTextureOffset(0 * sacle, 19 * sacle);

		this.bookSpine = new ModelRenderer(this).addBox(0, -5, -1, 0, 10, 2);
		this.bookSpine.setTextureOffset(12 * sacle, 0);

	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scale) {
		this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
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
		this.coverRight.rotateAngleY = f;
		this.coverLeft.rotateAngleY = -f;
		this.pagesRight.rotateAngleY = f;
		this.pagesLeft.rotateAngleY = -f;
		this.pagesRight.rotationPointX = MathHelper.sin(f);
		this.pagesLeft.rotationPointX = MathHelper.sin(f);
		this.flippingPageRight.rotateAngleY = f - f * 2.0F * limbSwingAmount;
		this.flippingPageLeft.rotateAngleY = f - f * 2.0F * ageInTicks;
		this.flippingPageRight.rotationPointX = MathHelper.sin(f);
		this.flippingPageLeft.rotationPointX = MathHelper.sin(f);
	}
}
