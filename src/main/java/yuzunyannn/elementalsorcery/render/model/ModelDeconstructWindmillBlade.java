
package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelDeconstructWindmillBlade extends ModelBase {

	protected final ModelRenderer blade;
	protected final ModelRenderer s;
	protected final ModelRenderer core;
	protected final ModelRenderer c;

	public ModelDeconstructWindmillBlade() {
		textureWidth = 64;
		textureHeight = 64;

		blade = new ModelRenderer(this);
		blade.cubeList.add(new ModelBox(blade, 0, 0, -1.0F, -2.0F, -32.0F, 3, 4, 27, 0.0F, false));
		blade.cubeList.add(new ModelBox(blade, 0, 0, -1.0F, 0.0F, -5.0F, 2, 2, 3, 0.0F, false));

		s = new ModelRenderer(this);
		s.setRotationPoint(0.0F, 0.0F, -5.0F);
		blade.addChild(s);
		setRotationAngle(s, 0.0F, 0.0F, -0.5236F);
		s.cubeList.add(new ModelBox(s, 0, 31, -0.134F, -7.2321F, -26.0F, 2, 6, 20, 0.0F, false));

		core = new ModelRenderer(this);
		core.cubeList.add(new ModelBox(core, 46, 0, -2.5F, -2.0F, -2.0F, 5, 4, 4, 0.0F, false));

		c = new ModelRenderer(this);
		c.setRotationPoint(-1.0F, 0.0F, 1.0F);
		core.addChild(c);
		setRotationAngle(c, -0.7854F, 0.0F, 0.0F);
		c.cubeList.add(new ModelBox(c, 48, 8, -1.0F, -1.2929F, -2.7071F, 4, 4, 4, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float rotate, float f1, float f2, float f3, float f4, float scale) {

		core.render(scale);
		core.rotateAngleX = -rotate;
		for (int i = 0; i < 8; i++) {
			blade.rotateAngleX = i * 3.1415926f / 4 - rotate;
			blade.render(scale);
		}
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
