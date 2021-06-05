package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelFairyCubeModule extends ModelBase {

	private final ModelRenderer main;
	private final ModelRenderer cor;

	public ModelFairyCubeModule() {
		textureWidth = 64;
		textureHeight = 64;

		main = new ModelRenderer(this);
		main.setRotationPoint(0, 1, 0);
		main.cubeList.add(new ModelBox(main, 0, 0, -8.0F, -2.0F, -8.0F, 16, 2, 16, 0.0F, false));

		cor = new ModelRenderer(this);
		cor.cubeList.add(new ModelBox(cor, 16, 22, 2.0F, -1.0F, 6.0F, 4, 2, 2, 0.0F, false));
		cor.cubeList.add(new ModelBox(cor, 0, 18, 6.0F, -1.0F, 2.0F, 2, 2, 6, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale) {
		main.render(scale);
		cor.rotateAngleY = 0;
		cor.render(scale);
		cor.rotateAngleY = 3.1415926f / 2;
		cor.render(scale);
		cor.rotateAngleY = 3.1415926f;
		cor.render(scale);
		cor.rotateAngleY = 3.1415926f / 2 * 3;
		cor.render(scale);
	}
}
