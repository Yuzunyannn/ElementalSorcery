
package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelMagicAlternator extends ModelBase {

	private final ModelRenderer bone;

	public ModelMagicAlternator() {
		textureWidth = 128;
		textureHeight = 128;

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 0, 0.0F);
		bone.cubeList.add(new ModelBox(bone, 0, 0, -8.0F, -2.0F, -8.0F, 16, 2, 16, 0.0F, false));
		bone.cubeList.add(new ModelBox(bone, 0, 18, -4.0F, -8.0F, -4.0F, 8, 6, 8, 0.0F, false));
		bone.cubeList.add(new ModelBox(bone, 0, 0, 4.0F, -8.0F, -1.0F, 2, 6, 2, 0.0F, false));
		bone.cubeList.add(new ModelBox(bone, 0, 0, -6.0F, -8.0F, -1.0F, 2, 6, 2, 0.0F, false));
		bone.cubeList.add(new ModelBox(bone, 0, 32, -6.0F, -12.0F, -3.0F, 12, 4, 6, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bone.render(f5);
	}

}
