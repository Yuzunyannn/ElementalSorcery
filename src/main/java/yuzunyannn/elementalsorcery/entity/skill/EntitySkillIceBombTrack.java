package yuzunyannn.elementalsorcery.entity.skill;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.ESObjects;

public class EntitySkillIceBombTrack extends EntitySkillTarget {

	public int remianTick = 0;

	public EntitySkillIceBombTrack(EntityLivingBase entity) {
		super(entity);
		this.setCD(20 * 40);
	}

	@Override
	public boolean checkCanUse() {
		return super.checkCanUse() && checkHPLowerThan(0.7);
	}

	@Override
	public int doSkill() {
		super.doSkill();
		this.remianTick = 20 * 3;
		return EntitySkill.SKILL_RESULT_CONTINUE;
	}

	@Override
	public int doContinueSkill() {
		if (remianTick-- < 0) return EntitySkill.SKILL_RESULT_FIN;
		if (remianTick % 10 == 0) {
			Random rand = getRandom();
			Vec3d targetForecastVec = this.getTargetForecastVec(0);
			if (targetForecastVec == null) return EntitySkill.SKILL_RESULT_FIN;
			targetForecastVec = targetForecastVec.add(0, 3 + rand.nextDouble() * 2, 0);
			this.fireArea(targetForecastVec, false, 30, 0,ESObjects.MANTRAS.ICE_CRYSTAL_BOMB, ESObjects.ELEMENTS.WATER,
					1000, 450);
		}
		return EntitySkill.SKILL_RESULT_CONTINUE;
	}

}
