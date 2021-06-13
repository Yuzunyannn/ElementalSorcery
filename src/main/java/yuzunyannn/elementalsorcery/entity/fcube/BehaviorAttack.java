package yuzunyannn.elementalsorcery.entity.fcube;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class BehaviorAttack extends Behavior {

	public static BehaviorAttack attack(Entity target) {
		return new BehaviorAttack("attack", "entity").setTarget(target);
	}

	protected Entity target;

	public BehaviorAttack(String type, String subType) {
		super(type, subType, 2);
	}

	public EntityLivingBase getLivingTarget() {
		if (target instanceof EntityLivingBase) return (EntityLivingBase) target;
		return null;
	}

	public Entity getTarget() {
		return target;
	}

	public BehaviorAttack setTarget(Entity target) {
		this.target = target;
		return this;
	}

}
