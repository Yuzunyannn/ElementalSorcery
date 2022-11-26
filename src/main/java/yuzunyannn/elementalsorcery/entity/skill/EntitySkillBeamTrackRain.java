package yuzunyannn.elementalsorcery.entity.skill;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;

public class EntitySkillBeamTrackRain extends EntitySkillTarget {

	protected ElementStack element = ElementStack.EMPTY;
	public int remianTick = 0;

	public EntitySkillBeamTrackRain(EntityLivingBase entity, ElementStack element) {
		super(entity);
		this.setCD(20 * 50);
		this.element = element;
	}

	public EntitySkillBeamTrackRain(EntityLivingBase entity, Element element, int power) {
		this(entity, new ElementStack(element, 1000, power));
	}

	@Override
	public boolean checkCanUse() {
		return super.checkCanUse() && checkHPLowerThan(0.6);
	}

	@Override
	public int doSkill() {
		super.doSkill();

		this.remianTick = 20 * 5;
		EntityLivingBase target = this.getAttackEntity();
		Random rand = this.living.getRNG();
		for (int i = 0; i < 5; i++) {
			Vec3d pos = this.living.getPositionVector().add(rand.nextGaussian() * 3, 4, rand.nextGaussian() * 3);
			if (!checkAndDestroyBlock(pos)) continue;
			this.fireTrace(pos, new Vec3d(0, -1, 0), target, 0.015 + rand.nextDouble() * 0.01, remianTick, 0,
					ESObjects.MANTRAS.LASER, element);
		}

		return EntitySkill.SKILL_RESULT_CONTINUE;
	}

	@Override
	public int doContinueSkill() {
		if (remianTick-- < 0) return EntitySkill.SKILL_RESULT_FIN;
		return EntitySkill.SKILL_RESULT_CONTINUE;
	}

}
