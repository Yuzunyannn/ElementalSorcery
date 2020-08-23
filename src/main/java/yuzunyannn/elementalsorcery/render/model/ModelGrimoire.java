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

	public final static int sacle = 2;

	public ModelGrimoire() {
		textureWidth = 64;
		textureHeight = 64;

		this.coverRight = new ModelRenderer(this).setTextureOffset(0, 0);
		this.coverRight.addBox(0, -5 * sacle, -1 * sacle, 6 * sacle, 10 * sacle, 1 * sacle);
		this.coverLeft = new ModelRenderer(this).setTextureOffset(14 * sacle, 0);
		this.coverLeft.addBox(0, -5 * sacle, 0, 6 * sacle, 10 * sacle, 1 * sacle);

		this.coverRight.setRotationPoint(0.0F, 0.0F, -1.0F * sacle);
		this.coverLeft.setRotationPoint(0.0F, 0.0F, 1.0F * sacle);

		this.pagesRight = new ModelRenderer(this).setTextureOffset(0, 11 * sacle);
		this.pagesRight.addBox(0.01f, -4 * sacle, -1 * sacle, 5 * sacle, 8 * sacle, 1 * sacle);
		this.pagesLeft = new ModelRenderer(this).setTextureOffset(14 * sacle, 11 * sacle);
		this.pagesLeft.addBox(0.01f, -4 * sacle, 0, 5 * sacle, 8 * sacle, 1 * sacle);

		this.flippingPageRight = new ModelRenderer(this).setTextureOffset(4 * sacle, 20 * sacle);
		this.flippingPageRight.addBox(0.01F, -4 * sacle, 0, 5 * sacle, 8 * sacle, 0);

		this.flippingPageLeft = new ModelRenderer(this).setTextureOffset(14 * sacle, 20 * sacle);
		this.flippingPageLeft.addBox(0.01F, -4 * sacle, 0, 5 * sacle, 8 * sacle, 0);

		this.bookSpine = new ModelRenderer(this).setTextureOffset(0, 20 * sacle);
		this.bookSpine.addBox(0, -5 * sacle, -1 * sacle, 0, 10 * sacle, 2 * sacle);

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
		this.pagesRight.rotationPointX = MathHelper.sin(f) * sacle;
		this.pagesLeft.rotationPointX = MathHelper.sin(f) * sacle;
		this.flippingPageRight.rotateAngleY = f - f * 2.0F * limbSwingAmount;
		this.flippingPageLeft.rotateAngleY = f - f * 2.0F * ageInTicks;
		this.flippingPageRight.rotationPointX = MathHelper.sin(f);
		this.flippingPageLeft.rotationPointX = MathHelper.sin(f);
	}
}
