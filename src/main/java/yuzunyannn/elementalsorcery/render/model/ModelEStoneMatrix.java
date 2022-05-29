package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelEStoneMatrix extends ModelBase {

	private final ModelRenderer t;
	private final ModelRenderer d1;
	private final ModelRenderer d2;
	private final ModelRenderer d3;

	public ModelEStoneMatrix() {
		textureWidth = 64;
		textureHeight = 64;

		t = new ModelRenderer(this);
		t.setRotationPoint(0.0F, 0, 0.0F);
		t.cubeList.add(new ModelBox(t, 0, 0, -6.0F, 7.0F, -6.0F, 12, 1, 12, 0.0F, false));
		t.cubeList.add(new ModelBox(t, 48, 17, -2.0F, 4.0F, -2.0F, 4, 2, 4, 0.0F, false));
		t.cubeList.add(new ModelBox(t, 0, 13, -4.0F, 6.0F, -4.0F, 8, 1, 8, 0.0F, false));

		d1 = new ModelRenderer(this);
		d1.setRotationPoint(0.0F, 8F, 0.0F);
		d1.cubeList.add(new ModelBox(d1, 0, 22, 2.0F, -3.0F, -1.0F, 1, 1, 2, 0.0F, false));

		d2 = new ModelRenderer(this);
		d2.setRotationPoint(0.0F, 8F, 0.0F);
		d2.cubeList.add(new ModelBox(d2, 0, 22, 4.0F, -2.0F, -2.0F, 1, 1, 4, 0.0F, false));

		d3 = new ModelRenderer(this);
		d3.setRotationPoint(0.0F, 8F, 0.0F);
		d3.cubeList.add(new ModelBox(d3, 0, 22, 6.0F, -1.0F, -3.0F, 1, 1, 6, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float ageInTicks, float f3, float f4, float scale) {
		t.rotateAngleX = 0f;
		t.render(scale);
		t.rotateAngleX = 3.1415926f;
		t.render(scale);

		float a = MathHelper.sin(ageInTicks / 100f);
		float b = MathHelper.sin(ageInTicks / 110f);
		float c = MathHelper.sin(ageInTicks / 120f);

		for (int i = 0; i < 4; i++) {
			d1.rotateAngleY = i * 3.1415926f / 2;
			d2.rotateAngleY = i * 3.1415926f / 2;
			d3.rotateAngleY = i * 3.1415926f / 2;

			d1.offsetY = a * 5.5f - 5.5f;
			d2.offsetY = b * 6.5f - 6.5f;
			d3.offsetY = c * 7.5f - 7.5f;

			d1.render(scale);
			d2.render(scale);
			d3.render(scale);
		}
	}

}
