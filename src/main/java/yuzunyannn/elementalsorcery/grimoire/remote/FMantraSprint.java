package yuzunyannn.elementalsorcery.grimoire.remote;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraCommon;
import yuzunyannn.elementalsorcery.init.ESInit;

public class FMantraSprint extends FMantraEntityExecute {

	public FMantraSprint(MantraCommon mantra) {
		addCanUseElementWithSameLevel(ESInit.ELEMENTS.AIR);
		setMaxCharge(new ElementStack(ESInit.ELEMENTS.AIR, 50, 30));
		setMaxRangeWithMaxFragment(8);
		setChargeSpeedRatio(0.01);
		setIconRes(mantra.getIconResource());
	}

	@Override
	protected void executeEntityAction(World world, BlockPos pos, EntityLivingBase target, double charge) {
		target.motionY += Math.min(8, charge / 800);
		target.velocityChanged = true;
	}

}
