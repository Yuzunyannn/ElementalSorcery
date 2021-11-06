package yuzunyannn.elementalsorcery.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import yuzunyannn.elementalsorcery.init.ESInit;

public class PotionHealthBalance extends PotionCommon {

	public PotionHealthBalance() {
		super(false, 0xffff45, "healthBalance");
		iconIndex = 13;
	}

	@Override
	public int getLiquidColor() {
		return super.getLiquidColor();
	}

	public static void tryBalance(EntityLivingBase target, EntityLivingBase attacker, DamageSource source,
			float amount) {
		if (!attacker.isPotionActive(ESInit.POTIONS.HEALTH_BALANCE)) return;
		int amplifier = attacker.getActivePotionEffect(ESInit.POTIONS.HEALTH_BALANCE).getAmplifier();
		
		float attackerHP = attacker.getHealth();
		float targetHP = target.getHealth();

		if (attackerHP == targetHP) return;

		float hpTransfer = 0.5f + (amplifier * 0.25f) + amount / 10f * (1 + amplifier * 0.1f);
		if (attackerHP < targetHP) {

			hpTransfer = Math.min(targetHP - 0.5f, hpTransfer);
			if (hpTransfer <= 0) return;
//			Effects.spawnTreatEntity(attacker, null);
//			Effects.spawnTreatEntity(target, new int[] { 0x3fa800 });
			attacker.heal(hpTransfer);
			target.setHealth(targetHP - hpTransfer);

		} else {

			hpTransfer = Math.min(attackerHP - 0.5f, hpTransfer);
			if (hpTransfer <= 0) return;
//			Effects.spawnTreatEntity(target, null);
//			Effects.spawnTreatEntity(attacker, new int[] { 0x3fa800 });
			target.heal(hpTransfer);
			attacker.setHealth(attackerHP - hpTransfer);

		}
	}

}