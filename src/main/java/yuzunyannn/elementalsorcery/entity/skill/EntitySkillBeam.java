package yuzunyannn.elementalsorcery.entity.skill;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;

public class EntitySkillBeam extends EntitySkillTarget {

	protected ElementStack element = ElementStack.EMPTY;

	public EntitySkillBeam(EntityLivingBase entity, ElementStack element) {
		super(entity);
		this.setCD(20 * 8);
		this.element = element;
	}

	public EntitySkillBeam(EntityLivingBase entity, Element element, int power) {
		this(entity, new ElementStack(element, 1000, power));
	}

	@Override
	public int doSkill() {
		super.doSkill();
		Vec3d targetForecastVec = this.getTargetForecastVec(10);
		Random rand = this.living.getRNG();

		for (int i = 0; i < 2; i++) {
			Vec3d pos = this.living.getPositionVector().add(rand.nextGaussian(), 3 + rand.nextFloat(),
					rand.nextGaussian());
			if (!checkAndDestroyBlock(pos)) continue;
			Vec3d move = targetForecastVec.subtract(pos).normalize();
			Vec3d orient = this.living.getPositionVector().add(move.x, 0, move.z).subtract(pos);
			move = new Vec3d(move.x, 0, move.z).normalize();
			this.fireDirect(pos, orient, move.scale(0.2), 30, ESObjects.MANTRAS.LASER, element);
		}
		return EntitySkill.SKILL_RESULT_FIN;
	}

}
