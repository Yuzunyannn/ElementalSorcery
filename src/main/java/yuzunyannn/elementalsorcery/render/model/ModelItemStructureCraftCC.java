
package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelItemStructureCraftCC extends ModelBase {

	private ModelRenderer base;
	private ModelRenderer zi;
	private ModelRenderer pad;

	public ModelItemStructureCraftCC() {
		textureWidth = 64;
		textureHeight = 64;

		base = new ModelRenderer(this);
		base.cubeList.add(new ModelBox(base, 0, 29, -4.0F, 0.0F, -4.0F, 8, 2, 8, 0.0F, false));

		zi = new ModelRenderer(this);
		zi.cubeList.add(new ModelBox(zi, 0, 15, -2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F, false));

		pad = new ModelRenderer(this);
		pad.setRotationPoint(0.0F, 24.0F, 0.0F);
		pad.cubeList.add(new ModelBox(pad, 0, 0, -7.0F, 8.0F, -7.0F, 14, 1, 14, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float tick, float f2, float f3, float f4, float scale) {
		base.render(scale);

		float grow = MathHelper.sin(tick / 100) * 1;
		pad.setRotationPoint(0, grow, 0);
		pad.render(scale);

		float dt = tick / 12;

		for (int i = 0; i < 8; i++) {
			float dx = MathHelper.sin(dt * 1.3f) * 25;
			float dz = MathHelper.cos(dt) * 25;
			float dy = MathHelper.sin(dt) * 8 + 8;
			zi.setRotationPoint(dx, grow / 0.2f + dy + 50, dz);
			zi.render(scale * 0.2f);
			dt += 0.3415926f * 2f;
		}
	}

}
