package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelElementTerminal extends ModelBase {

	private final ModelRenderer bone;
	private final ModelRenderer jiao;

	public ModelElementTerminal() {
		textureWidth = 64;
		textureHeight = 64;

		bone = new ModelRenderer(this);
		bone.cubeList.add(new ModelBox(bone, 0, 0, -8.0F, -1.0F, -8.0F, 16, 1, 16, 0.0F, false));
		bone.cubeList.add(new ModelBox(bone, 0, 18, -4.0F, -2.0F, -4.0F, 8, 1, 8, 0.0F, false));

		jiao = new ModelRenderer(this);
		jiao.cubeList.add(new ModelBox(jiao, 52, 42, -5.0F, -2.0F, -7.0F, 3, 1, 2, 0.0F, false));
		jiao.cubeList.add(new ModelBox(jiao, 54, 37, -7.0F, -2.0F, -5.0F, 2, 1, 3, 0.0F, false));
		jiao.cubeList.add(new ModelBox(jiao, 56, 19, -6.0F, -16.0F, -6.0F, 2, 15, 2, 0.0F, false));
		jiao.cubeList.add(new ModelBox(jiao, 43, 29, -4.0F, -16.0F, -6.0F, 4, 1, 2, 0.0F, false));
		jiao.cubeList.add(new ModelBox(jiao, 43, 21, -6.0F, -16.0F, -4.0F, 2, 3, 4, 0.0F, false));
		jiao.cubeList.add(new ModelBox(jiao, 50, 46, -7.0F, -11.0F, -7.0F, 6, 1, 1, 0.0F, false));
		jiao.cubeList.add(new ModelBox(jiao, 52, 49, -7.0F, -7.0F, -7.0F, 1, 1, 5, 0.0F, false));
		jiao.cubeList.add(new ModelBox(jiao, 52, 56, -8.0F, -4.0F, -8.0F, 3, 3, 3, 0.0F, false));
		jiao.cubeList.add(new ModelBox(jiao, 52, 56, -8.0F, -16.0F, -8.0F, 3, 3, 3, 0.0F, false));
		jiao.cubeList.add(new ModelBox(jiao, 40, 33, -5.0F, -16.0F, -8.0F, 5, 1, 2, 0.0F, false));
		jiao.cubeList.add(new ModelBox(jiao, 37, 37, -8.0F, -16.0F, -5.0F, 2, 1, 5, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scaleFactor) {
		bone.render(scaleFactor);
		jiao.rotateAngleY = 0;
		jiao.render(scaleFactor);
		jiao.rotateAngleY = 3.1415926f / 2;
		jiao.render(scaleFactor);
		jiao.rotateAngleY = 3.1415926f;
		jiao.render(scaleFactor);
		jiao.rotateAngleY = 3.1415926f / 2 * 3;
		jiao.render(scaleFactor);
	}

}
