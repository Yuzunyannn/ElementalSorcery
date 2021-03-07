package yuzunyannn.elementalsorcery.render.model.md;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelMDFrequencyMapping extends ModelBase {

	private final ModelRenderer b;
	private final ModelRenderer l;
	private final ModelRenderer p;
	private final ModelRenderer i;

	public ModelMDFrequencyMapping() {
		textureWidth = 64;
		textureHeight = 32;

		b = new ModelRenderer(this);
		b.setRotationPoint(0.0F, 2 - 0.1f, 0.0F);
		b.cubeList.add(new ModelBox(b, 0, 0, -6.0F, 3.0F, -6.0F, 12, 1, 12, 0.0F, false));

		l = new ModelRenderer(this);
		l.setRotationPoint(0.0F, 2 - 0.1f, 0.0F);
		l.cubeList.add(new ModelBox(l, 0, 14, -4.0F, 4.0F, -4.0F, 1, 10, 1, 0.0F, false));
		l.cubeList.add(new ModelBox(l, 4, 14, -5.0F, 4.0F, -5.0F, 1, 5, 1, 0.0F, false));
		l.cubeList.add(new ModelBox(l, 8, 14, -6.0F, 4.0F, -6.0F, 1, 3, 1, 0.0F, false));

		p = new ModelRenderer(this);
		p.setRotationPoint(4.0F, 0, -4.0F);
		p.cubeList.add(new ModelBox(p, 16, 21, -7.0F, 10.0F, 1.0F, 6, 1, 6, 0.0F, false));

		i = new ModelRenderer(this);
		i.setRotationPoint(-4.0F, -0.5F, 4.0F);
		p.addChild(i);
		setRotationAngle(i, 0.0F, -0.7854F, 0.0F);
		i.cubeList.add(new ModelBox(i, 16, 14, -3.0F, 10.45F, -3.0F, 6, 1, 6, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float high, float f1, float f2, float f3, float f4, float f5) {
		b.render(f5);
		l.rotateAngleY = 0;
		l.render(f5);
		l.rotateAngleY = 3.1415f / 2;
		l.render(f5);
		l.rotateAngleY = 3.1415f;
		l.render(f5);
		l.rotateAngleY = 3.1415f / 2 * 3;
		l.render(f5);

		p.setRotationPoint(4, high * 3, -4);
		p.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

}
