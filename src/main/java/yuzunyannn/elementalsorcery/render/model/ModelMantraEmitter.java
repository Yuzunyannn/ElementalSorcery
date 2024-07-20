
package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import yuzunyannn.elementalsorcery.nodegui.GActionEaseInOutBack;

public class ModelMantraEmitter extends ModelBase {

	private final ModelRenderer jiao;
	private final ModelRenderer core;
	private final ModelRenderer cc;
	private final ModelRenderer c1;
	private final ModelRenderer c2;
	private final ModelRenderer c3;

	public ModelMantraEmitter() {
		textureWidth = 32;
		textureHeight = 32;

		jiao = new ModelRenderer(this);
		jiao.cubeList.add(new ModelBox(jiao, 0, 0, -8.0F, -7.0F, 5.0F, 3, 7, 3, 0.0F, false));
		jiao.cubeList.add(new ModelBox(jiao, 12, 0, -8.0F, -3.0F, 2.0F, 3, 3, 3, 0.0F, false));
		jiao.cubeList.add(new ModelBox(jiao, 0, 10, -5.0F, -3.0F, 5.0F, 3, 3, 3, 0.0F, false));

		core = new ModelRenderer(this);
		core.setRotationPoint(0.0F, -8, 0.0F);
		core.cubeList.add(new ModelBox(core, 0, 16, -4.0F, -4.0F, -4.0F, 8, 8, 8, 0.0F, false));

		cc = new ModelRenderer(this);
		cc.setRotationPoint(0.0F, 0.0F, 0.0F);
		core.addChild(cc);

		c1 = new ModelRenderer(this);
		c1.setRotationPoint(0.0F, 0.0F, 0.5F);
		cc.addChild(c1);
		c1.cubeList.add(new ModelBox(c1, 18, 6, -3.0F, 2.0F, -0.5F, 6, 1, 1, 0.0F, false));
		c1.cubeList.add(new ModelBox(c1, 18, 6, -3.0F, -3.0F, -0.5F, 6, 1, 1, 0.0F, false));
		c1.cubeList.add(new ModelBox(c1, 28, 1, -3.0F, -2.0F, -0.5F, 1, 4, 1, 0.0F, false));
		c1.cubeList.add(new ModelBox(c1, 28, 1, 2.0F, -2.0F, -0.5F, 1, 4, 1, 0.0F, false));

		c2 = new ModelRenderer(this);
		c2.setRotationPoint(0.0F, 0.0F, 0.0F);
		cc.addChild(c2);
		c2.cubeList.add(new ModelBox(c2, 20, 8, -2.0F, 1.0F, -1.0F, 4, 1, 2, 0.0F, false));
		c2.cubeList.add(new ModelBox(c2, 20, 8, -2.0F, -2.0F, -1.0F, 4, 1, 2, 0.0F, false));
		c2.cubeList.add(new ModelBox(c2, 26, 11, -2.0F, -1.0F, -1.0F, 1, 2, 2, 0.0F, false));
		c2.cubeList.add(new ModelBox(c2, 26, 11, 1.0F, -1.0F, -1.0F, 1, 2, 2, 0.0F, false));

		c3 = new ModelRenderer(this);
		c3.setRotationPoint(0.0F, 0.0F, -0.5F);
		cc.addChild(c3);
		c3.cubeList.add(new ModelBox(c3, 12, 11, -1.0F, -1.0F, -1.5F, 2, 2, 3, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float tick, float yaw, float pitch, float run, float f4, float scale) {

		jiao.rotationPointY = 0;
		jiao.rotateAngleX = 0;
		for (int i = 0; i < 4; i++) {
			jiao.rotateAngleY = 3.1415926f / 2 * i;
			jiao.render(scale);
		}

		jiao.rotationPointY = -16;
		jiao.rotateAngleX = -3.1415926f;
		for (int i = 0; i < 4; i++) {
			jiao.rotateAngleY = 3.1415926f / 2 * i;
			jiao.render(scale);
		}

		float dx = MathHelper.cos(tick / 16);
		core.rotationPointY = -8 + dx * 0.5f;
		core.rotateAngleY = yaw / 180 * 3.1415926f;
		core.rotateAngleX = pitch / 180 * 3.1415926f;

		float dr = Math.min(run, 1);

		if (dr > 0) {
//			dr = (float) GActionEaseInOutBack.ease(dr);
			float rotation = tick;
			float length = 6;

			c1.rotateAngleZ = ((rotation * 0.1f) % 6.28f) * dr;
			c2.rotateAngleZ = ((rotation * 0.5f) % 6.28f) * dr;
			c3.rotateAngleZ = ((rotation * 1.0f) % 6.28f) * dr;

			c1.rotationPointZ = 0.5f - length * dr;
			c2.rotationPointZ = 0.0f - length * 1.25f * dr;
			c3.rotationPointZ = -0.5f - length * 1.75f * dr;
		} else {
			c1.rotationPointZ = 0;
			c2.rotationPointZ = 0;
			c3.rotationPointZ = 0;
		}

		core.render(scale);
	}

}
