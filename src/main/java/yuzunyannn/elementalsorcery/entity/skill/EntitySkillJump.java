package yuzunyannn.elementalsorcery.entity.skill;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;

public class EntitySkillJump extends EntitySkillTarget {

	public EntitySkillJump(EntityLivingBase entity) {
		super(entity);
		this.setCD(20 * 5);
		this.range = 128;
	}

	@Override
	public boolean checkCanUse() {
		if (!super.checkCanUse()) return false;
		EntityLivingBase target = getAttackEntity();
		if (target.posY > living.posY + 1) return true;
		return false;
	}

	@Override
	public int doSkill() {
		super.doSkill();
		Vec3d tar = getAttackEntity().getPositionEyes(0).subtract(living.getPositionVector()).normalize();

		this.living.motionY += 1.5;
		this.living.motionX = tar.x * 0.1;
		this.living.motionZ = tar.z * 0.1;
		this.living.velocityChanged = true;

		return EntitySkill.SKILL_RESULT_FIN;
	}

}
