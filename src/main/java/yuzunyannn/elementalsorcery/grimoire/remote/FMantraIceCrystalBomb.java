package yuzunyannn.elementalsorcery.grimoire.remote;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.ElementTransition;
import yuzunyannn.elementalsorcery.api.util.target.WorldLocation;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraCommon;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.var.Variables;

public class FMantraIceCrystalBomb extends FMantraBase {

	public FMantraIceCrystalBomb() {
		addCanUseElementWithSameLevel(ESObjects.ELEMENTS.WATER);
		setMaxCharge(new ElementStack(ESObjects.ELEMENTS.WATER, 800, 50));
		setChargeSpeedRatio(0.002);
		setIconRes("textures/mantras/ice_crystal_bomb.png");
	}

	@Override
	public void cast(World world, BlockPos pos, WorldLocation to, VariableSet content) {
		if (world.isRemote) return;
		double chager = content.get(CHARGE);
		double eCount = ElementTransition.fromFragmentByPower(ESObjects.ELEMENTS.WATER, chager, 50);
		int count = MathHelper.ceil(eCount / 100 * 1.5f);
		World targetWorld = to.getWorld(world);
		content.set(Variables.WATER, new ElementStack(ESObjects.ELEMENTS.WATER, 100, 200));
		content.set(MantraCommon.POWERF, 0.5f);

		BlockPos center = to.getPos();

		double rotate = 0;
		double dRotate = Math.PI * 2 / count;
		for (int i = 0; i < count; i++) {
			double r = 6 * targetWorld.rand.nextGaussian();
			double x = MathHelper.sin((float) rotate) * r;
			double z = MathHelper.cos((float) rotate) * r;
			rotate += dRotate;
			BlockPos at = center.add(x, targetWorld.rand.nextGaussian() * 3, z);

			for (int j = 0; j < 8; j++) {
				if (BlockHelper.isReplaceBlock(targetWorld, at)) break;
				at = at.up();
			}

			if (!BlockHelper.isReplaceBlock(targetWorld, at)) continue;

			VariableSet set = content.copy();
			set.set(MantraCommon.VEC, new Vec3d(at).add(0.5, 0.5, 0.5));
			MantraCommon.fireMantra(targetWorld, ESObjects.MANTRAS.ICE_CRYSTAL_BOMB, null, set);
		}

	}

}
