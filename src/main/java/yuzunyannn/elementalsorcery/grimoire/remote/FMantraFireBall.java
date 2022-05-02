package yuzunyannn.elementalsorcery.grimoire.remote;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.VariableSet;
import yuzunyannn.elementalsorcery.util.world.WorldLocation;

public class FMantraFireBall extends FMantraBase {

	public FMantraFireBall() {
		addCanUseElement(ESInit.ELEMENTS.FIRE);
		setMaxCharge(3000);
		setChargetSpeed(2);
		setIconRes("textures/mantras/fire_ball_f.png");
	}

	@Override
	public void cast(World world, BlockPos pos, WorldLocation to, VariableSet content) {
		if (world.isRemote) return;
		double chager = content.get(CHARGE);
		ElementStack estack = new ElementStack(ESInit.ELEMENTS.FIRE, (int) chager, (int) chager);
		ElementExplosion.doExplosion(world, to.getPos(), estack, null);
	}

}
