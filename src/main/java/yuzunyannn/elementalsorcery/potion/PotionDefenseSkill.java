package yuzunyannn.elementalsorcery.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import yuzunyannn.elementalsorcery.init.ESInit;

public class PotionDefenseSkill extends PotionCommon {

	public PotionDefenseSkill() {
		super(false, 0xd8e45a, "defenseSkill");
		this.setBeneficial();
		iconIndex = 15;
	}

	public static boolean canSkill(EntityLivingBase target, EntityLivingBase attacker, DamageSource source,
			float amount) {
		return target.isPotionActive(ESInit.POTIONS.DEFENSE_SKILL)
				&& !(source.isFireDamage() || source.isMagicDamage() || source.canHarmInCreative());
	}

	public static float doSkill(EntityLivingBase target, EntityLivingBase attacker, DamageSource source, float amount) {
		int amplifier = target.getActivePotionEffect(ESInit.POTIONS.DEFENSE_SKILL).getAmplifier();
		float level = 0;
		if (target instanceof EntityPlayer) level = ((EntityPlayer) target).experienceLevel;
		else level = target.getMaxHealth();
		float factor = (float) (Math.pow(level / 10, 1.1) / 16 * (1 + amplifier * 0.075));
		return Math.min(0.9f, factor);
	}

}