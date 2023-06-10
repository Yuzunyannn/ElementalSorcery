package yuzunyannn.elementalsorcery.entity.skill;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;

public class EntitySkillLiving extends EntitySkill {

	protected EntityLivingBase living;

	public EntitySkillLiving(EntityLivingBase entity) {
		super(entity);
		this.living = entity;
	}

	@Override
	public Random getRandom() {
		return living.getRNG();
	}

	public boolean checkHPLowerThan(double rate) {
//		if (ESAPI.isDevelop) return true;
		return (living.getHealth() / living.getMaxHealth()) <= rate;
	}
}
