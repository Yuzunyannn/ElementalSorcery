package yuzunyannn.elementalsorcery.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelBulletin extends ModelBase {

	private final ModelRenderer bone;

	public ModelBulletin() {
		textureWidth = 64;
		textureHeight = 64;

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 15.0385F, -0.1154F);
		bone.setTextureOffset(0, 0).addBox(-13.0F, -7.0385F, 0.1154F, 26, 14, 1);
		bone.setTextureOffset(54, 0).addBox(-15.0F, -7.0385F, -1.8846F, 2, 14, 3);
		bone.setTextureOffset(60, 17).addBox(-13.0F, -7.0385F, -0.8846F, 1, 15, 1);
		bone.setTextureOffset(60, 17).addBox(12.0F, -7.0385F, -0.8846F, 1, 14, 1);
		bone.setTextureOffset(0, 17).addBox(-13.0F, -9.0385F, -1.8846F, 26, 2, 3);
		bone.setTextureOffset(0, 17).addBox(-13.0F, 6.9615F, -1.8846F, 26, 2, 3);
		bone.setTextureOffset(54, 0).addBox(13.0F, -7.0385F, -1.8846F, 2, 14, 3);
		bone.setTextureOffset(0, 24).addBox(-12.0F, -7.0385F, -0.8846F, 24, 1, 1);
		bone.setTextureOffset(0, 24).addBox(-12.0F, 5.9615F, -0.8846F, 24, 1, 1);
		bone.setTextureOffset(0, 28).addBox(-15.0F, 6.9615F, -0.8846F, 2, 2, 3);
		bone.setTextureOffset(0, 28).addBox(13.0F, 6.9615F, -0.8846F, 2, 2, 3);
		bone.setTextureOffset(0, 28).addBox(13.0F, -9.0385F, -0.8846F, 2, 2, 3);
		bone.setTextureOffset(0, 28).addBox(-15.0F, -9.0385F, -0.8846F, 2, 2, 3);
	}

	public void render(Entity entityIn, float dtick, float n1, float n2, float n3, float n4, float scale) {
		this.bone.render(scale);
	}

}
