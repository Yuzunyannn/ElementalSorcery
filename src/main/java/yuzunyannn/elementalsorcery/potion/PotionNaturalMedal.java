package yuzunyannn.elementalsorcery.potion;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraftforge.common.util.Constants;
import yuzunyannn.elementalsorcery.api.ESObjects;

public class PotionNaturalMedal extends PotionCommon {

	public final static int DEFAULT_LEVEL_TICK = 20 * 15;

	public static void growMedal(EntityLivingBase entity) {
		if (entity.world.isRemote) return;
		PotionEffect effect = entity.getActivePotionEffect(ESObjects.POTIONS.NATURAL_MEDAL);
		int amplifier = effect == null ? -1 : effect.getAmplifier();
		amplifier = Math.min(amplifier, 127);
		entity.addPotionEffect(new PotionEffect(ESObjects.POTIONS.NATURAL_MEDAL, DEFAULT_LEVEL_TICK, amplifier + 1));
	}

	public PotionNaturalMedal() {
		super(false, 0x63b91e, "naturalMedal");
		this.setBeneficial();
		iconIndex = 24;
		registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, "3A07AAC5-D52A-A888-6DD4-DBFA5A3DAB98",
				0.002, Constants.AttributeModifierOperation.ADD);
		registerPotionAttributeModifier(SharedMonsterAttributes.MAX_HEALTH, "16D2F69F-772E-8220-2349-B24CB659FE63", 0.5,
				Constants.AttributeModifierOperation.ADD);
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return duration % 20 == 0;
	}

	@Override
	public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
		if (entityLivingBaseIn.world.isRemote) return;
		if (amplifier > 0) {
			PotionEffect effect = entityLivingBaseIn.getActivePotionEffect(this);
			if (effect != null && effect.getDuration() <= 30) {
				entityLivingBaseIn.removePotionEffect(this);
				entityLivingBaseIn.addPotionEffect(new PotionEffect(this, DEFAULT_LEVEL_TICK, amplifier - 1));
			}
		}
		float maxHp = entityLivingBaseIn.getMaxHealth();
		float hp = entityLivingBaseIn.getHealth();
		float heal = Math.min(maxHp - hp, maxHp * 0.025f);
		if (heal > 0) entityLivingBaseIn.heal(heal);
		if (entityLivingBaseIn instanceof EntityPlayer) {
			FoodStats foodStats = ((EntityPlayer) entityLivingBaseIn).getFoodStats();
			if (foodStats.getFoodLevel() < 16) foodStats.addStats(1, 0.5f);
		}
	}

}