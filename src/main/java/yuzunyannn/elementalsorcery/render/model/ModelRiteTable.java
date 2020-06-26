package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelRiteTable extends ModelBase {
	private final ModelRenderer m;
	private final ModelRenderer t;
	private final ModelRenderer b;

	public ModelRiteTable() {
		textureWidth = 64;
		textureHeight = 32;

		m = new ModelRenderer(this);
		m.setRotationPoint(0, 8, 0);
		m.cubeList.add(new ModelBox(m, 0, 0, -8.0F, 0.0F, -8.0F, 16, 2, 16, 0.0F, false));

		t = new ModelRenderer(this);
		t.cubeList.add(new ModelBox(t, 22, 22, -1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F, false));

		b = new ModelRenderer(this);
		b.cubeList.add(new ModelBox(b, 0, 9, 0.0F, 0.0F, -5.0F, 0, 13, 10, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		m.render(f5);

		t.setRotationPoint(6, 0, 6);
		t.render(f5);
		t.setRotationPoint(-6, 0, 6);
		t.render(f5);
		t.setRotationPoint(-6, 0, -6);
		t.render(f5);
		t.setRotationPoint(6, 0, -6);
		t.render(f5);

		b.setRotationPoint(8.01f, -3, 0);
		b.rotateAngleY = 3.1415926f;
		b.render(f5);
		b.setRotationPoint(-8.01f, -3, 0);
		b.rotateAngleY = 0;
		b.render(f5);
		b.setRotationPoint(0, -3, 8.01f);
		b.rotateAngleY = 1.5707963f;
		b.render(f5);
		b.setRotationPoint(0, -3, -8.01f);
		b.rotateAngleY = -1.5707963f;
		b.render(f5);
	}
}
