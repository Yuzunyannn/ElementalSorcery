package yuzunyannn.elementalsorcery.render.model.md;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelMDLiquidizer extends ModelBase {

	private final ModelRenderer blade;
	private final ModelRenderer cover;

	public ModelMDLiquidizer() {
		textureWidth = 64;
		textureHeight = 64;

		blade = new ModelRenderer(this);
		blade.cubeList.add(new ModelBox(blade, 56, 0, -1.0F, 0F, -1.0F, 2, 1, 2, 0.0F, false));
		blade.cubeList.add(new ModelBox(blade, 52, 0, -4.0F, 1F, -2.0F, 4, 1, 2, 0.0F, false));
		blade.cubeList.add(new ModelBox(blade, 52, 0, -2.0F, 1F, 0.0F, 2, 1, 4, 0.0F, false));
		blade.cubeList.add(new ModelBox(blade, 52, 0, 0.0F, 1F, 0.0F, 4, 1, 2, 0.0F, false));
		blade.cubeList.add(new ModelBox(blade, 52, 0, 0.0F, 1F, -4.0F, 2, 1, 4, 0.0F, false));

		cover = new ModelRenderer(this);
		cover.cubeList.add(new ModelBox(cover, 0, 0, -7.0F, 0F, -7.0F, 14, 13, 14, 0.0F, false));

		blade.setRotationPoint(0, 3, 0);
		cover.setRotationPoint(0, 5, 0);
	}

	@Override
	public void render(Entity entity, float rotate, float f1, float f2, float f3, float f4, float scale) {
		blade.rotateAngleY = -rotate;
		blade.render(scale);
		cover.render(scale);
	}
}
