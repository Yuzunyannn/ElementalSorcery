
package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import yuzunyannn.elementalsorcery.model.CheckModelRenderer;

public class ModelComputerA extends ModelBase {

	private final CheckModelRenderer bone;
	private final CheckModelRenderer bone2;

	public ModelComputerA() {
		textureWidth = 128;
		textureHeight = 128;

		bone = new CheckModelRenderer(this);
		bone.setRotationPoint(0.0F, 24.0F - 24f, 0.0F);
		bone.cubeList.add(new ModelBox(bone, 0, 0, -8.0F, -2.0F, -8.0F, 16, 2, 16, 0.0F, false));
		bone.addCheckBox(1, new ModelBox(bone, 49, 0, -5.0F, -5.0F, -5.0F, 10, 3, 10, 0.0F, false));
//		bone.cubeList.add(new ModelBox(bone, 49, 0, -5.0F, -5.0F, -5.0F, 10, 3, 10, 0.0F, false));
		bone.cubeList.add(new ModelBox(bone, 0, 0, -1.0F, -13.0F, -1.0F, 2, 8, 2, 0.0F, false));

		bone2 = new CheckModelRenderer(this);
		bone2.setRotationPoint(0.0F, 11.2071F - 24f, 0.5355F);
		setRotationAngle(bone2, -0.7854F, 0.0F, 0.0F);
		bone2.addCheckBox(2, new ModelBox(bone2, 57, 14, -7.0F, -0.5F, -4.0F, 14, 1, 8, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bone.render(f5);
		bone2.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

}
