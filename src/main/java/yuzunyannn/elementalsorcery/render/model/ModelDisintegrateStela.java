
package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelDisintegrateStela extends ModelBase {
	private final ModelRenderer a;
	private final ModelRenderer b;
	private final ModelRenderer box;
	private final ModelRenderer bB;

	public ModelDisintegrateStela() {
		textureWidth = 64;
		textureHeight = 64;

		a = new ModelRenderer(this);
		a.cubeList.add(new ModelBox(a, 34, 0, -7.0F, -2.0F, 6.0F, 14, 2, 1, 0.0F, false));
		a.cubeList.add(new ModelBox(a, 34, 0, -7.0F, -2.0F, -7.0F, 14, 2, 1, 0.0F, false));
		a.cubeList.add(new ModelBox(a, 0, 11, -7.0F, -2.0F, -6.0F, 1, 2, 12, 0.0F, false));
		a.cubeList.add(new ModelBox(a, 0, 11, 6.0F, -2.0F, -6.0F, 1, 2, 12, 0.0F, false));
		a.cubeList.add(new ModelBox(a, 0, 0, -5.0F, -1.0F, -5.0F, 10, 1, 10, 0.0F, false));

		b = new ModelRenderer(this);
		b.cubeList.add(new ModelBox(b, 0, 25, -6.0F, -4.0F, -6.0F, 12, 1, 12, 0.0F, false));
		b.cubeList.add(new ModelBox(b, 28, 11, -2.0F, -2.0F, -2.0F, 4, 1, 4, 0.0F, false));
		b.cubeList.add(new ModelBox(b, 28, 16, -1.0F, -3.0F, -1.0F, 2, 1, 2, 0.0F, false));

		box = new ModelRenderer(this);
		box.cubeList.add(new ModelBox(box, 0, 38, -4.0F, -12.0F, -4.0F, 8, 8, 8, 0.0F, false));

		bB = new ModelRenderer(this);
		bB.cubeList.add(new ModelBox(bB, 0, 54, 2.0F, -1.0F, 4.0F, 2, 1, 1, 0.0F, false));
		bB.cubeList.add(new ModelBox(bB, 0, 56, 1.0F, -3.0F, 4.0F, 1, 3, 1, 0.0F, false));
		bB.cubeList.add(new ModelBox(bB, 0, 60, -1.0F, -2.0F, 4.0F, 2, 2, 1, 0.0F, false));
		bB.cubeList.add(new ModelBox(bB, 6, 54, -2.0F, -1.0F, 4.0F, 1, 1, 1, 0.0F, false));
		bB.cubeList.add(new ModelBox(bB, 6, 56, -3.0F, -4.0F, 4.0F, 1, 4, 1, 0.0F, false));
		bB.cubeList.add(new ModelBox(bB, 6, 61, -4.0F, -2.0F, 4.0F, 1, 2, 1, 0.0F, false));
		bB.cubeList.add(new ModelBox(bB, 0, 56, -5.0F, -3.0F, 4.0F, 1, 3, 1, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float shakeRate, float rotation, float tick, float wakeRate, float f4,
			float scale) {
		a.render(scale);

		float sin = MathHelper.sin(tick / 50) * wakeRate;
		float dx = 0;
		float dz = 0;
		float dy = 0;
		if (shakeRate > 0) {
			dx = MathHelper.cos(tick * 3.14f) * shakeRate * 1f;
			dz = MathHelper.sin(tick * 3.14f) * shakeRate * 1f;
			dy = dx * dz;
		}
		dy = dy + (sin * 2 - 3) * wakeRate;

		b.setRotationPoint(dx, dy, dz);
		box.setRotationPoint(dx, dy, dz);
		bB.setRotationPoint(dx, -4 + dy, dz);

		b.rotateAngleY = rotation;
		box.rotateAngleY = rotation;
		bB.rotateAngleY = rotation;

		b.render(scale);
		box.render(scale);
		for (int i = 0; i < 4; i++) {
			bB.rotateAngleY += 3.1415926f / 2f;
			bB.render(scale);
		}
	}

}
