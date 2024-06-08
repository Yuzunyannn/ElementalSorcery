
package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelRingReader extends ModelBase {

	private final ModelRenderer di;
	private final ModelRenderer cover;

	public ModelRingReader() {
		textureWidth = 128;
		textureHeight = 128;

		di = new ModelRenderer(this);
		di.setRotationPoint(0.0F, 0, 0.0F);
		di.cubeList.add(new ModelBox(di, 72, 33, -7.0F, -2.0F, -7.0F, 14, 2, 14, 0.0F, false));
		di.cubeList.add(new ModelBox(di, 78, 28, -7.0F, -4.0F, 4.0F, 14, 2, 3, 0.0F, false));
		di.cubeList.add(new ModelBox(di, 104, 16, 5.0F, -3.0F, -5.0F, 2, 1, 9, 0.0F, false));
		di.cubeList.add(new ModelBox(di, 104, 16, -7.0F, -3.0F, -5.0F, 2, 1, 9, 0.0F, false));
		di.cubeList.add(new ModelBox(di, 96, 27, -7.0F, -3.0F, -7.0F, 14, 1, 2, 0.0F, false));

		cover = new ModelRenderer(this);
		cover.setRotationPoint(0.0F, -3, -7.0F);
		cover.cubeList.add(new ModelBox(cover, 96, 27, -7.0F, -1.0F, 0.0F, 14, 1, 2, 0.0F, false));
		cover.cubeList.add(new ModelBox(cover, 104, 16, -7.0F, -1.0F, 2.0F, 2, 1, 9, 0.0F, false));
		cover.cubeList.add(new ModelBox(cover, 104, 16, 5.0F, -1.0F, 2.0F, 2, 1, 9, 0.0F, false));
		cover.cubeList.add(new ModelBox(cover, 74, 49, -7.0F, -2.0F, 0.0F, 14, 1, 12, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float tick, float progress, float f2, float f3, float f4, float scale) {
		di.render(scale);
		cover.rotateAngleX = progress;
		cover.render(scale);
	}
}
