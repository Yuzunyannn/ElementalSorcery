package yuzunyannn.elementalsorcery.potion;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;

public class PotionDeathWatch extends PotionCommon {

	public PotionDeathWatch() {
		super(true, 0x231a1a, "deathWatch");
		iconIndex = 26;
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return duration == 1;
	}

	@Override
	public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
		float maxHealth = entityLivingBaseIn.getMaxHealth();
		float rate = Math.min(0.96f, 0.8f + 0.03f * amplifier);
		entityLivingBaseIn.setHealth(Math.max(entityLivingBaseIn.getHealth() - maxHealth * rate, 0));
		if (entityLivingBaseIn instanceof EntityPlayer) {
			FoodStats foodStats = ((EntityPlayer) entityLivingBaseIn).getFoodStats();
			foodStats.setFoodLevel(0);
			foodStats.addExhaustion(40);
		}
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		return Collections.EMPTY_LIST;
	}

}