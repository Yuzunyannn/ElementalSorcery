package yuzunyannn.elementalsorcery.entity.elf;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.util.EnumHand;

public class EntityAIAttackElf extends EntityAIAttackMelee {

	public EntityAIAttackElf(EntityElfBase elf) {
		super(elf, 1.0D, false);
	}

	@Override
	public boolean shouldExecute() {
		return ((EntityElfBase) attacker).getProfession().getAttackDistance() != -1 && super.shouldExecute();
	}

	@Override
	protected void checkAndPerformAttack(EntityLivingBase enemy, double distToEnemySqr) {
		double d0 = ((EntityElfBase) attacker).getProfession().getAttackDistance();
		
		if (d0 <= 0) d0 = this.getAttackReachSqr(enemy);
		else d0 = d0 * d0;
		
		if (distToEnemySqr <= d0 && this.attackTick <= 0) {
			this.attackTick = 20;
			this.attacker.swingArm(EnumHand.MAIN_HAND);
			this.attacker.attackEntityAsMob(enemy);
		}
	}

}
