package yuzunyannn.elementalsorcery.render.model.living;

import net.minecraft.client.model.ModelZombie;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import yuzunyannn.elementalsorcery.entity.mob.EntitySubjectZombie;

public class ModelSubjectZombie extends ModelZombie {

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scaleFactor, Entity entityIn) {
		if (((EntitySubjectZombie) entityIn).isSpelling()) this.swingProgress = (MathHelper.sin(ageInTicks / 2) * 0.2f);
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

	}
}
