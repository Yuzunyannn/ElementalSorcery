package yuzunyannn.elementalsorcery.entity.skill;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.ESObjects;

public class EntitySkillFireBall extends EntitySkillTarget {

	public int remianTick = 0;

	public EntitySkillFireBall(EntityLivingBase entity) {
		super(entity);
		this.setCD(20 * 60);
	}

	@Override
	public boolean checkCanUse() {
		return super.checkCanUse() && checkHPLowerThan(0.5);
	}

	@Override
	public int doSkill() {
		super.doSkill();

		this.remianTick = 20 * 5;
		EntityLivingBase target = this.getAttackEntity();

		Vec3d pos = this.living.getPositionVector().add(0, 3, 0);
		if (!checkAndDestroyBlock(pos)) return EntitySkill.SKILL_RESULT_CONTINUE;
		this.fireTrace(pos, new Vec3d(0, -1, 0), target, 0.03, remianTick, 0,ESObjects.MANTRAS.FIRE_BALL,
				ESObjects.ELEMENTS.FIRE, 1000, 800, ESObjects.ELEMENTS.KNOWLEDGE, 1000, 800);

		return EntitySkill.SKILL_RESULT_CONTINUE;
	}

	@Override
	public int doContinueSkill() {
		if (remianTick-- < 0) return EntitySkill.SKILL_RESULT_FIN;
		return EntitySkill.SKILL_RESULT_CONTINUE;
	}

}
