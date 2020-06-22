package yuzunyannn.elementalsorcery.render.model.md;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelMDAbsorbBox extends ModelBase {
	private final ModelRenderer a;
	private final ModelRenderer c;
	private final ModelRenderer w;
	private final ModelRenderer z;

	public ModelMDAbsorbBox() {
		textureWidth = 64;
		textureHeight = 32;

		a = new ModelRenderer(this);
		a.cubeList.add(new ModelBox(a, 0, 0, -6F, 3.0F, -6F, 12, 2, 12, 0.0F, false));

		c = new ModelRenderer(this);
		setRotationAngle(c, 0.0F, -0.7854F, 0.0F);
		c.cubeList.add(new ModelBox(c, 0, 15, -2.0F, 5.0F, -2.0F, 4, 9, 4, 0.0F, false));

		w = new ModelRenderer(this);
		w.cubeList.add(new ModelBox(w, 17, 16, -0.5F, -6F, -0.5F, 1, 12, 1, 0.0F, false));

		z = new ModelRenderer(this);
		z.cubeList.add(new ModelBox(z, 23, 16, -4, -4, -4, 8, 8, 8, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		a.render(f5);
		c.render(f5);

		setRotationAngle(w, -0.2617F, 0.0F, 0.2617F);
		w.setRotationPoint(3f, 9.5F, 3f);
		w.render(f5);
		w.setRotationPoint(-3f, 9.5F, -3f);
		setRotationAngle(w, 0.2617F, 0.0F, -0.2617F);
		w.render(f5);
		setRotationAngle(w, -0.2617F, 0.0F, -0.2617F);
		w.setRotationPoint(-3f, 9.5F, 3f);
		w.render(f5);
		w.setRotationPoint(3f, 9.5F, -3f);
		setRotationAngle(w, 0.2617F, 0.0F, 0.2617F);
		w.render(f5);

		z.setRotationPoint(0.0F, 15.0F * 4 + MathHelper.cos(f) * 1.75f, 0.0F);
		float roate = f / 180 * 3.1415926f * 5;
		setRotationAngle(z, roate, roate, roate);
		z.render(f5 * 0.25f);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

}
