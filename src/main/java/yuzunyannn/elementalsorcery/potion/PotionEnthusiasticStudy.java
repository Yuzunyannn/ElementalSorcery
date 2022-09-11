package yuzunyannn.elementalsorcery.potion;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.item.ItemAncientPaper;

public class PotionEnthusiasticStudy extends PotionCommon {

	public PotionEnthusiasticStudy() {
		super(false, 0x5bacff, "enthusiasticStudy");
		this.setBeneficial();
		iconIndex = 21;
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return duration % 10 == 0;
	}

	@Override
	public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
		if (entityLivingBaseIn instanceof EntityPlayer && !entityLivingBaseIn.world.isRemote)
			ItemAncientPaper.eliminateFatigue((EntityPlayer) entityLivingBaseIn, false);
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		return Collections.EMPTY_LIST;
	}

}