package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelTileIceRockNode extends ModelBase {

	private final ModelRenderer b;

	public ModelTileIceRockNode() {
		textureWidth = 16;
		textureHeight = 16;

		b = new ModelRenderer(this);
		b.cubeList.add(new ModelBox(b, 0, 0, -2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F, false));
		b.setRotationPoint(0, 8, 0);
	}

	@Override
	public void render(Entity entity, float rotation, float f1, float f2, float f3, float f4, float scale) {
		float ro = rotation / 180 * 3.1415926f;
		float len = 4 + (MathHelper.cos(ro) + 1) / 2 * 2;
		for (int i = 0; i < 4; i++) {
			b.offsetX = len * MathHelper.cos(ro + i * 3.14f / 2);
			b.offsetZ = len * MathHelper.sin(ro + i * 3.14f / 2);
			b.offsetY = len * MathHelper.sin(ro + i * 3.14f);
			b.render(scale);
		}
	}
}
