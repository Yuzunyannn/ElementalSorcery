package yuzunyannn.elementalsorcery.entity.skill;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.ESObjects;

public class EntitySkilFrozenDefense extends EntitySkillTarget {

	public EntitySkilFrozenDefense(EntityLivingBase entity) {
		super(entity);
		this.setCD(20 * 8);
		this.range = 6;
	}

	@Override
	public int doSkill() {
		super.doSkill();

		EntityLivingBase target = this.getAttackEntity();
		Vec3d pos = this.living.getPositionVector().add(0, 3, 0);
		this.fireTrace(pos, new Vec3d(0, -1, 0), target, 0.1, 20 * 4, 0, ESObjects.MANTRAS.FROZEN,
				ESObjects.ELEMENTS.WATER, 1000, 400, ESObjects.ELEMENTS.AIR, 1000, 500);

		return EntitySkill.SKILL_RESULT_FIN;
	}

}
