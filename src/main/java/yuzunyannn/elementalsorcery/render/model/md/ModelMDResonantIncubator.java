package yuzunyannn.elementalsorcery.render.model.md;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelMDResonantIncubator extends ModelBase {

	private final ModelRenderer z;
	private final ModelRenderer c;
	private final ModelRenderer hl;
	private final ModelRenderer zb;

	public ModelMDResonantIncubator() {
		textureWidth = 64;
		textureHeight = 32;

		z = new ModelRenderer(this);
		z.setRotationPoint(0.0F, 0.0F, 0.0F);
		z.cubeList.add(new ModelBox(z, 41, 0, 4.0F, 3.0F, 4.0F, 2, 13, 2, 0.0F, false));
		z.cubeList.add(new ModelBox(z, 41, 0, -6.0F, 3.0F, -6.0F, 2, 13, 2, 0.0F, false));

		c = new ModelRenderer(this);
		c.setRotationPoint(0.0F, 0.0F, 0.0F);
		c.cubeList.add(new ModelBox(c, 0, 0, -5.0F, 3.0F, -5.0F, 10, 2, 10, 0.0F, false));

		hl = new ModelRenderer(this);
		hl.setRotationPoint(0.0F, 16.0F, 0.0F);
		setRotationAngle(hl, 0.0F, 0.7854F, 0.0F);
		hl.cubeList.add(new ModelBox(hl, 25, 16, -1.0F, -1.0F, -6.0F, 2, 2, 12, 0.0F, false));

		zb = new ModelRenderer(this);
		zb.setRotationPoint(0.0F, 15.5F, 0.0F);
		zb.cubeList.add(new ModelBox(zb, 50, 0, -1.0F, -3.5F, -1.0F, 2, 3, 2, 0.0F, false));
		zb.cubeList.add(new ModelBox(zb, 0, 13, -3.0F, -10.5F, -3.0F, 6, 7, 6, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float zx, float zz, float f2, float f3, float f4, float f5) {
		zb.setRotationPoint(zx, 15.5F, zz);
		z.render(f5);
		c.render(f5);
		hl.render(f5);
		zb.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

}
