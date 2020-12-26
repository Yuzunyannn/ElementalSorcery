package yuzunyannn.elementalsorcery.entity.mob;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.util.EnumHand;

public class EntityAIRelicZombieAttack extends EntityAIAttackMelee {

	private final EntityRelicZombie zombie;

	public EntityAIRelicZombieAttack(EntityRelicZombie zombieIn, double speedIn) {
		super(zombieIn, speedIn, false);
		this.zombie = zombieIn;
	}

	@Override
	public boolean shouldContinueExecuting() {
		EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
		if (entitylivingbase == null) return false;
		else if (!entitylivingbase.isEntityAlive()) return false;
		else {
			RelicZombieType type = zombie.getType();
			double d0 = type.getAttackDistance();
			if (entitylivingbase.getDistanceSq(attacker) <= d0 * d0) return true;
			return !this.attacker.getNavigator().noPath();
		}
	}

	@Override
	public void updateTask() {
		EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
		RelicZombieType type = zombie.getType();
		double d0 = type.getAttackDistance();
		if (entitylivingbase.getDistanceSq(attacker) <= d0 * d0) {
			this.attacker.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
			attacker.getNavigator().clearPath();
			this.attackTick = Math.max(this.attackTick - 1, 0);
			this.checkAndPerformAttack(entitylivingbase, d0);
		} else super.updateTask();
	}

	@Override
	protected void checkAndPerformAttack(EntityLivingBase enemy, double distToEnemySqr) {
		RelicZombieType type = zombie.getType();
		double d0 = type.getAttackDistance();

		if (d0 < 0) return;// 小于零的不攻击
		d0 = d0 * d0;

		if (distToEnemySqr <= d0 && this.attackTick <= 0) {
			this.attackTick = 20;
			if (this.attacker.attackEntityAsMob(enemy)) this.attacker.swingArm(EnumHand.MAIN_HAND);
		}
	}

}
