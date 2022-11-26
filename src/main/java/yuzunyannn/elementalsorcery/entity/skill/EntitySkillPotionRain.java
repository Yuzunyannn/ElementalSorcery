package yuzunyannn.elementalsorcery.entity.skill;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;

public class EntitySkillPotionRain extends EntitySkillTarget {

	public int remianTick = 0;

	public EntitySkillPotionRain(EntityLivingBase entity) {
		super(entity);
		this.setCD(20 * 30);
	}

	@Override
	public boolean checkCanUse() {
		return super.checkCanUse() && checkHPLowerThan(0.75);
	}

	@Override
	public int doSkill() {
		super.doSkill();
		this.remianTick = 20 * 2;
		return EntitySkill.SKILL_RESULT_CONTINUE;
	}

	@Override
	public int doContinueSkill() {
		if (remianTick-- < 0) return EntitySkill.SKILL_RESULT_FIN;
		if (remianTick % 5 == 0) {
			Vec3d targetForecastVec = this.getTargetForecastVec(10);
			if (targetForecastVec == null) return EntitySkill.SKILL_RESULT_FIN;
			EntitySkillPotion.throwPotion(living, targetForecastVec, null);
		}
		return EntitySkill.SKILL_RESULT_CONTINUE;
	}

}
