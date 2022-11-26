package yuzunyannn.elementalsorcery.entity.skill;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.ESObjects;

public class EntitySkillFireCharge extends EntitySkillTarget {

	public EntitySkillFireCharge(EntityLivingBase entity) {
		super(entity);
		this.setCD(20 * 2);
	}

	@Override
	public int doSkill() {
		super.doSkill();
		Vec3d targetForecastVec = this.getTargetForecastVec(20);
		Vec3d pos = this.living.getPositionVector().add(0, 3, 0);
		Vec3d orient = targetForecastVec.subtract(pos).normalize();
		if (!checkAndDestroyBlock(pos)) return EntitySkill.SKILL_RESULT_FIN;
		this.fireDirect(pos, orient, Vec3d.ZERO, 20 * 4, ESObjects.MANTRAS.FIRE_CHARGE, ESObjects.ELEMENTS.FIRE, 1000,
				20);
		Random rand = this.living.getRNG();
		if (rand.nextInt(10) < 8) return EntitySkill.SKILL_RESULT_FIN;
		for (int i = 0; i < 2; i++) {
			pos = this.living.getPositionVector().add(rand.nextGaussian(), 3 + rand.nextFloat(), rand.nextGaussian());
			if (!checkAndDestroyBlock(pos)) continue;
			orient = targetForecastVec.subtract(pos).normalize();
			this.fireDirect(pos, orient, Vec3d.ZERO, 20 * 4, ESObjects.MANTRAS.FIRE_CHARGE, ESObjects.ELEMENTS.FIRE,
					1000, 20);
		}
		return EntitySkill.SKILL_RESULT_FIN;
	}

}
