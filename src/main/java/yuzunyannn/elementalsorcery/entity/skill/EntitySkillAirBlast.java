package yuzunyannn.elementalsorcery.entity.skill;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;

public class EntitySkillAirBlast extends EntitySkillTarget {

	public EntitySkillAirBlast(EntityLivingBase entity) {
		super(entity);
		this.setCD(20 * 10);
		this.range = 3;
	}

	@Override
	public int doSkill() {
		super.doSkill();
		Vec3d vec = this.living.getPositionEyes(0);
		ElementExplosion.doExplosion(world, vec, new ElementStack(ESObjects.ELEMENTS.AIR, 750, 750), living);

		return EntitySkill.SKILL_RESULT_FIN;
	}

}
