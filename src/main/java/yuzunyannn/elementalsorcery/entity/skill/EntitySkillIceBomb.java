package yuzunyannn.elementalsorcery.entity.skill;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.ESObjects;

public class EntitySkillIceBomb extends EntitySkillTarget {

	public EntitySkillIceBomb(EntityLivingBase entity) {
		super(entity);
		this.setCD(20 * 8);
	}

	@Override
	public int doSkill() {
		super.doSkill();

		Vec3d targetForecastVec = this.getTargetForecastVec(10);
		Random rand = getRandom();
		for (int i = 0; i < 2; i++) {
			double theta = rand.nextDouble() * 3.1415926 * 2;
			double x = Math.sin(theta) * (2 + rand.nextDouble() * 2);
			double z = Math.cos(theta) * (2 + rand.nextDouble() * 2);
			Vec3d vec = targetForecastVec.add(x, 5 + rand.nextDouble(), z);
			if (!checkAndDestroyBlock(vec)) continue;
			this.fireArea(vec, false, 20 * 1,0, ESObjects.MANTRAS.ICE_CRYSTAL_BOMB, ESObjects.ELEMENTS.WATER, 1000, 350);
		}

		return EntitySkill.SKILL_RESULT_FIN;
	}

}
