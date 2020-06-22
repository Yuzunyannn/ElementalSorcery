package yuzunyannn.elementalsorcery.render.model.md;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelMDMagiclization extends ModelBase {
	private final ModelRenderer z;
	private final ModelRenderer c;


	public ModelMDMagiclization() {
		textureWidth = 64;
		textureHeight = 32;

		z = new ModelRenderer(this);
		z.cubeList.add(new ModelBox(z, 41, 0, -1.5F, 0.0F, -1.5F, 3, 13, 3, 0.0F, false));
		c = new ModelRenderer(this);
		c.cubeList.add(new ModelBox(c, 0, 0, -5.0F, 5.0F, -5.0F, 10, 10, 10, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		c.render(f5);
		
		z.setRotationPoint(4, 3, 4);
		z.render(f5);
		z.setRotationPoint(-4, 3, 4);
		z.render(f5);
		z.setRotationPoint(-4, 3, -4);
		z.render(f5);
		z.setRotationPoint(4, 3, -4);
		z.render(f5);
	}

}
