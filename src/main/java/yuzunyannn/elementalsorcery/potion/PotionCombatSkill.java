package yuzunyannn.elementalsorcery.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import yuzunyannn.elementalsorcery.api.ESObjects;

public class PotionCombatSkill extends PotionCommon {

	public PotionCombatSkill() {
		super(false, 0xd8e45a, "combatSkill");
		this.setBeneficial();
		iconIndex = 14;
	}

	public static boolean canSkill(EntityLivingBase target, EntityLivingBase attacker, DamageSource source,
			float amount) {
		return attacker.isPotionActive(ESObjects.POTIONS.COMBAT_SKILL) && !source.isMagicDamage();
	}

	public static float doSkill(EntityLivingBase target, EntityLivingBase attacker, DamageSource source, float amount) {
		int amplifier = attacker.getActivePotionEffect(ESObjects.POTIONS.COMBAT_SKILL).getAmplifier();
		float level = 0;
		if (attacker instanceof EntityPlayer) level = ((EntityPlayer) attacker).experienceLevel;
		else level = attacker.getMaxHealth();
		float factor = (float) (Math.pow(level / 5, 1.2) / 40 * (1 + amplifier * 0.1));
		return factor;
	}

}