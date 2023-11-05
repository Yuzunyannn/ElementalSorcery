
package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelStarCPU extends ModelBase {

	private final ModelRenderer bone;

	public ModelStarCPU() {
		textureWidth = 128;
		textureHeight = 128;

		bone = new ModelRenderer(this);
		bone.cubeList.add(new ModelBox(bone, 0, 0, -8.0F, -2.0F, -8.0F, 16, 2, 16, 0.0F, false));
		bone.cubeList.add(new ModelBox(bone, 37, 24, -5.0F, -14.0F, -5.0F, 10, 12, 10, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bone.render(f5);
	}

}
