package yuzunyannn.elementalsorcery.grimoire.remote;

import java.util.Collection;
import java.util.function.BiFunction;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.util.WorldObjectBlock;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.element.ElementTransition;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraCommon;
import yuzunyannn.elementalsorcery.util.VariableSet;
import yuzunyannn.elementalsorcery.util.Variables;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.world.WorldLocation;

public class FMantraElementDirectLaunch extends FMantraBase {

	public final MantraCommon mantra;
	protected double[] proportionArray;
	protected ElementStack[] elementArray;
	public BiFunction<Double, VariableSet, VariableSet> parmasGenerator;

	public FMantraElementDirectLaunch(MantraCommon mantra, Collection<ElementStack> elements, double mult) {
		this.mantra = mantra;
		this.proportionArray = new double[elements.size()];
		this.elementArray = new ElementStack[elements.size()];
		double fragment = 0;
		int i = 0;
		for (ElementStack estack : elements) {
			double f = ElementHelper.toFragment(estack);
			ElementTransition et = estack.getElement().getTransition();
			if (et != null) f = ElementHelper.transitionFrom(estack.getElement(), f, et.getLevel());
			this.proportionArray[i] = f;
			this.elementArray[i] = estack.copy();
			fragment = fragment + f;
			if (i == 0) this.addCanUseElementWithSameLevel(estack.getElement());
			else this.addCanUseElement(estack.getElement());
			i++;
		}
		float toMainRatio = 0.2f;
		for (int n = 0; n < this.proportionArray.length; n++) {
			this.proportionArray[n] = this.proportionArray[n] / fragment;
			if (n > 0) {
				double t = this.proportionArray[n] * toMainRatio;
				this.proportionArray[0] += t;
				this.proportionArray[n] -= t;
			}
		}
		setMaxCharge(fragment * mult);
		setMinChargeRatio((float) (1f / mult) / (1 - toMainRatio));
		setTargetChargeLevel(1);
	}

	@Override
	public void cast(World world, BlockPos pos, WorldLocation to, VariableSet content) {
		VariableSet parmas = new VariableSet();
		double charge = content.get(CHARGE) * 2;
		for (int i = 0; i < this.proportionArray.length; i++) {
			ElementStack eStack = this.elementArray[i];
			double fragment = charge * this.proportionArray[i];
			double power = ElementHelper.fromFragmentByCount(eStack.getElement(), fragment, eStack.getCount());
			eStack = eStack.copy();
			eStack.setPower((int) power);
			parmas.set(Variables.getElementVar(eStack.getElement()), eStack);
		}
		if (parmasGenerator != null) parmas = parmasGenerator.apply(charge, parmas);
		if (parmas == null) return;
		WorldObjectBlock wo = new WorldObjectBlock(world.getTileEntity(pos));
		mantra.directLaunchMantra(to.getWorld(world), new Vec3d(to.getPos()), wo, parmas, null);
	}

}
