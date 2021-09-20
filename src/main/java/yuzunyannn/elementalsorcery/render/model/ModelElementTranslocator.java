package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelElementTranslocator extends ModelBase {

	protected final ModelRenderer core;
	protected final ModelRenderer base;

	public ModelElementTranslocator() {
		textureWidth = 64;
		textureHeight = 64;

		core = new ModelRenderer(this);
		core.setRotationPoint(0, -8, 0);
		core.cubeList.add(new ModelBox(core, 0, 41, -1.0F, 15.0F, -1.0F, 2, 2, 2, 0.0F, false));
		core.cubeList.add(new ModelBox(core, 0, 29, -3.0F, 13.0F, -8.0F, 6, 6, 6, 0.0F, false));
		core.cubeList.add(new ModelBox(core, 0, 17, -3.0F, 13.0F, 2.0F, 6, 6, 6, 0.0F, false));

		base = new ModelRenderer(this);
		base.cubeList.add(new ModelBox(base, 0, 0, -8.0F, 0.0F, -8.0F, 16, 1, 16, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 0, 0, -1.0F, 1.0F, -1.0F, 2, 5, 2, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 0, 0, -1.0F, 10.0F, -1.0F, 2, 5, 2, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 40, 17, -3.0F, 15.0F, -3.0F, 6, 1, 6, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 44, 25, 3.0F, 1.0F, -8.0F, 5, 1, 5, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 44, 31, -8.0F, 1.0F, 3.0F, 5, 1, 5, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float tick, float f3, float f4, float scale) {
		base.render(scale);

		float move = MathHelper.cos(tick / 50);
		core.setRotationPoint(0, -8 + move, 0);
		core.rotateAngleY = tick / 50;
		
		core.render(scale);
	}

}
