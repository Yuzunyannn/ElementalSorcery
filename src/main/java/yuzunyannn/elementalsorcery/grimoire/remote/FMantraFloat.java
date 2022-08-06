package yuzunyannn.elementalsorcery.grimoire.remote;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraCommon;

public class FMantraFloat extends FMantraEntityExecute {

	public FMantraFloat(MantraCommon mantra) {
		addCanUseElementWithSameLevel(ESObjects.ELEMENTS.AIR);
		setMaxCharge(new ElementStack(ESObjects.ELEMENTS.AIR, 10, 20));
		setMaxRangeWithMaxFragment(8);
		setChargeSpeedRatio(0.01);
		setIconRes(mantra.getIconResource());
	}

	@Override
	protected void executeEntityAction(World world, BlockPos pos, EntityLivingBase target, double charge) {
		target.addPotionEffect(
				new PotionEffect(MobEffects.LEVITATION, (int) (charge / this.maxCharge * 20 * 20 + 20 * 2)));
	}

}
