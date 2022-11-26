package yuzunyannn.elementalsorcery.entity.skill;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.ESObjects;

public class EntitySkilFrozenStorm extends EntitySkillTarget {

	public int remianTick = 0;

	public EntitySkilFrozenStorm(EntityLivingBase entity) {
		super(entity);
		this.setCD(20 * 60);
		this.range = 8;
	}

	@Override
	public boolean checkCanUse() {
		return super.checkCanUse() && checkHPLowerThan(0.6);
	}

	@Override
	public int doSkill() {
		super.doSkill();
		remianTick = 20 * 10;

		EntityLivingBase target = this.getAttackEntity();
		Vec3d pos = this.living.getPositionVector().add(0, 3, 0);
		this.fireTrace(pos, new Vec3d(1, 0, 1), target, 0.01, remianTick, 1, ESObjects.MANTRAS.FROZEN,
				ESObjects.ELEMENTS.WATER, 1000, 800, ESObjects.ELEMENTS.AIR, 1000, 500);

		return EntitySkill.SKILL_RESULT_CONTINUE;
	}

	@Override
	public int doContinueSkill() {
		if (remianTick-- < 0) return EntitySkill.SKILL_RESULT_FIN;
		return EntitySkill.SKILL_RESULT_CONTINUE;
	}
}
