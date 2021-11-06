package yuzunyannn.elementalsorcery.potion;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class PotionStar extends PotionCommon {

	public PotionStar() {
		super(false, 0x5bacff, "star");
		this.setBeneficial();
		iconIndex = 16;
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		return new ArrayList<>();
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void performEffect(EntityLivingBase owner, int amplifier) {
//		if (amplifier > 0) {
//			if (owner.ticksExisted % 20 == 0) {
//				Collection<PotionEffect> potionEffects = owner.getActivePotionEffects();
//				for (PotionEffect effect : potionEffects) {
//					Potion potion = effect.getPotion();
//					if (potion.isBeneficial()) {
//
//					} else if (potion.isBadEffect()) {
//
//					}
//				}
//			}
//		}
	}

}