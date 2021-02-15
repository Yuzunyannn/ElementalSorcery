package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelMagicBlastWand extends ModelBase {

	private final ModelRenderer stick;
	private final ModelRenderer l1;
	private final ModelRenderer l2;
	private final ModelRenderer l3;
	private final ModelRenderer l4;
	private final ModelRenderer around;
	private final ModelRenderer a;
	private final ModelRenderer b;
	private final ModelRenderer c;
	private final ModelRenderer d;
	private final ModelRenderer ball;

	public ModelMagicBlastWand() {
		textureWidth = 32;
		textureHeight = 32;

		stick = new ModelRenderer(this);
		stick.setRotationPoint(0, 0, 0);
		stick.cubeList.add(new ModelBox(stick, 8, 5, -2.0F, -16.0F, -2.0F, 4, 1, 4, 0, false));
		stick.cubeList.add(new ModelBox(stick, 0, 0, -1.0F, -15.0F, -1.0F, 2, 15, 2, 0, false));
		stick.cubeList.add(new ModelBox(stick, 8, 10, -3.0F, -17.0F, -3.0F, 6, 1, 6, 0, false));
		stick.cubeList.add(new ModelBox(stick, 20, 3, -1.5F, 0, -1.5F, 3, 3, 3, 0, false));

		l1 = new ModelRenderer(this);
		l1.setRotationPoint(1.5F, -17.0F, -1.5F);
		stick.addChild(l1);
		setRotationAngle(l1, 0.3491F, 0, 0.3491F);
		l1.cubeList.add(new ModelBox(l1, 20, 0, -0.5F, -2.0F, -0.5F, 1, 2, 1, 0, false));

		l2 = new ModelRenderer(this);
		l2.setRotationPoint(-1.5F, -17.0F, 1.5F);
		stick.addChild(l2);
		setRotationAngle(l2, -0.3491F, 0, -0.3491F);
		l2.cubeList.add(new ModelBox(l2, 20, 0, -0.5F, -2.0F, -0.5F, 1, 2, 1, 0, false));

		l3 = new ModelRenderer(this);
		l3.setRotationPoint(1.5F, -17.0F, 1.5F);
		stick.addChild(l3);
		setRotationAngle(l3, -0.3491F, 0, 0.3491F);
		l3.cubeList.add(new ModelBox(l3, 20, 0, -0.5F, -2.0F, -0.5F, 1, 2, 1, 0, false));

		l4 = new ModelRenderer(this);
		l4.setRotationPoint(-1.5F, -17.0F, -1.5F);
		stick.addChild(l4);
		setRotationAngle(l4, 0.3491F, 0, -0.3491F);
		l4.cubeList.add(new ModelBox(l4, 20, 0, -0.5F, -2.0F, -0.5F, 1, 2, 1, 0, false));

		around = new ModelRenderer(this);
		around.setRotationPoint(-3.0F, -16.0F, -4.0F);
		stick.addChild(around);
		around.cubeList.add(new ModelBox(around, 0, 17, -2.0F, 0, -1.0F, 1, 1, 9, 0, false));
		around.cubeList.add(new ModelBox(around, 0, 27, -1.0F, 1.0F, -1.0F, 9, 1, 1, 0, false));
		around.cubeList.add(new ModelBox(around, 0, 17, 7.0F, 0, 0, 1, 1, 9, 0, false));
		around.cubeList.add(new ModelBox(around, 0, 27, -2.0F, -1.0F, 8.0F, 9, 1, 1, 0, false));

		a = new ModelRenderer(this);
		a.setRotationPoint(3.5F, 3.5F, 1.5F);
		around.addChild(a);
		setRotationAngle(a, -0.4363F, 0, 0);
		a.cubeList.add(new ModelBox(a, 8, 0, -0.5F, -0.5F, -2.5F, 1, 1, 4, 0, false));

		b = new ModelRenderer(this);
		b.setRotationPoint(2.5F, 1.5F, 7.0F);
		around.addChild(b);
		setRotationAngle(b, 0.6109F, 0, 0);
		b.cubeList.add(new ModelBox(b, 8, 0, -0.5F, -0.5F, -2.0F, 1, 1, 4, 0, false));

		c = new ModelRenderer(this);
		c.setRotationPoint(6.0F, 3.0F, 4.5F);
		around.addChild(c);
		setRotationAngle(c, 0, 0, -0.8727F);
		c.cubeList.add(new ModelBox(c, 8, 0, -2.5F, -0.5F, -0.5F, 5, 1, 1, 0, false));

		d = new ModelRenderer(this);
		d.setRotationPoint(0, 1.5F, 3.5F);
		around.addChild(d);
		setRotationAngle(d, 0, 0, 0.6109F);
		d.cubeList.add(new ModelBox(d, 8, 2, -1.2929F, 0.2071F, -0.5F, 4, 1, 1, 0, false));

		ball = new ModelRenderer(this);
		ball.setRotationPoint(0, 0, 0);
		ball.cubeList.add(new ModelBox(ball, 16, 17, -2.0F, -22.0F, -2.0F, 4, 4, 4, 0, false));
	}

	@Override
	public void render(Entity entity, float rotate, float f1, float f2, float f3, float f4, float f5) {
		stick.render(f5);

		ball.rotateAngleY = rotate;
		ball.setRotationPoint(0, -1 * (MathHelper.sin(rotate) + 1) * 0.5f, 0);
		ball.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
