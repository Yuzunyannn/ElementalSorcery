package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelFairyCube extends ModelBase {

	private final ModelRenderer r;
	private final ModelRenderer f;
	private final ModelRenderer d;

	public ModelFairyCube() {
		textureWidth = 64;
		textureHeight = 64;

		r = new ModelRenderer(this);
		r.cubeList.add(new ModelBox(r, 0, 0, -16.0F, 8.0F, 8.0F, 8, 8, 8, 0.0F, false));
		r.cubeList.add(new ModelBox(r, 32, 0, -16.0F, 12.0F, 0.0F, 4, 4, 8, 0.0F, false));
		r.cubeList.add(new ModelBox(r, 32, 12, -8.0F, 12.0F, 12.0F, 8, 4, 4, 0.0F, false));
		r.cubeList.add(new ModelBox(r, 0, 16, -16.0F, 0.0F, 12.0F, 4, 8, 4, 0.0F, false));

		f = new ModelRenderer(this);
		f.cubeList.add(new ModelBox(f, 24, 20, -5.0F, -5.0F, -5.0F, 10, 10, 10, 0.0F, false));

		d = new ModelRenderer(this);
		d.cubeList.add(new ModelBox(d, 16, 40, -6.0F, -6.0F, -6.0F, 12, 12, 12, 0.0F, false));
	}

	public void renderRoate(Entity entity, float limbSwing, float ageInTicks, float scaleFactor) {
		GlStateManager.pushMatrix();
		float cos = MathHelper.cos(ageInTicks * 0.005f);
		float rotate = cos * cos * 180;
		boolean isY = cos > 0;
		float dis = 4 * MathHelper.sin(limbSwing * limbSwing * 3.1415f);
		if (isY) {
			float rY1 = -rotate;
			float rY2 = rotate > 90 ? -rY1 : rY1;
			GlStateManager.rotate(rY1, 0, 1, 0);
			r.offsetX = -dis;
			r.offsetY = dis;
			r.offsetZ = dis;
			for (float theta = 0; theta < 4; theta++) {
				GlStateManager.rotate(90, 0, 1, 0);
				r.render(scaleFactor);
			}
			GlStateManager.rotate(-rY1 + rY2, 0, 1, 0);
			GlStateManager.rotate(180, 1, 0, 0);
			for (float theta = 0; theta < 4; theta++) {
				GlStateManager.rotate(90, 0, 1, 0);
				r.render(scaleFactor);
			}
		} else {
			float rX1 = -rotate;
			float rX2 = rotate > 90 ? -rX1 : rX1;
			GlStateManager.rotate(rX1, 1, 0, 0);
			r.offsetX = -dis;
			r.offsetY = dis;
			r.offsetZ = dis;
			for (float theta = 0; theta < 4; theta++) {
				GlStateManager.rotate(90, 1, 0, 0);
				r.render(scaleFactor);
			}
			GlStateManager.rotate(-rX1 + rX2, 1, 0, 0);
			GlStateManager.rotate(180, 0, 1, 0);
			for (float theta = 0; theta < 4; theta++) {
				GlStateManager.rotate(90, 1, 0, 0);
				r.render(scaleFactor);
			}
		}
		GlStateManager.popMatrix();

	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scaleFactor) {
		this.renderRoate(entity, limbSwing * limbSwing, ageInTicks, scaleFactor);

		f.rotateAngleY = d.rotateAngleY = netHeadYaw / 180 * 3.1415926f;
		f.rotateAngleX = d.rotateAngleX = headPitch / 180 * 3.1415926f;

		f.render(scaleFactor);
		d.render(scaleFactor);
	}
}
