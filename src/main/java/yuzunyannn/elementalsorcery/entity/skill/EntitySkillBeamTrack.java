package yuzunyannn.elementalsorcery.entity.skill;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;

public class EntitySkillBeamTrack extends EntitySkillTarget {

	protected ElementStack element = ElementStack.EMPTY;

	public EntitySkillBeamTrack(EntityLivingBase entity, ElementStack element) {
		super(entity);
		this.setCD(20 * 15);
		this.element = element;
	}

	public EntitySkillBeamTrack(EntityLivingBase entity, Element element, int power) {
		this(entity, new ElementStack(element, 1000, power));
	}

	@Override
	public boolean checkCanUse() {
		return super.checkCanUse() && checkHPLowerThan(0.8);
	}

	@Override
	public int doSkill() {
		super.doSkill();
		EntityLivingBase target = this.getAttackEntity();
		Random rand = this.living.getRNG();
		Vec3d pos = this.living.getPositionVector().add(rand.nextGaussian(), 3 + rand.nextFloat(), rand.nextGaussian());
		if (!checkAndDestroyBlock(pos)) return EntitySkill.SKILL_RESULT_FIN;
		Vec3d move = target.getPositionEyes(0).subtract(pos).normalize();
		Vec3d orient = this.living.getPositionVector().add(move.x, 0, move.z).subtract(pos);
		this.fireTrace(pos, orient, target, 0.025, 20 * 3, 0,ESObjects.MANTRAS.LASER, element);
		return EntitySkill.SKILL_RESULT_FIN;
	}

}
