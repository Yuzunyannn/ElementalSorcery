package yuzunyannn.elementalsorcery.grimoire.remote;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.ElementTransition;
import yuzunyannn.elementalsorcery.api.element.ElementTransitionReactor;
import yuzunyannn.elementalsorcery.api.mantra.IFragmentMantraLauncher;
import yuzunyannn.elementalsorcery.api.util.client.ESResources;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet.Variable;
import yuzunyannn.elementalsorcery.util.TextHelper;

public abstract class FMantraBase implements IFragmentMantraLauncher {

	public static final Variable<Double> CHARGE = new Variable<>("charge", VariableSet.DOUBLE);

	protected Set<Element> canUseElementSet = new HashSet<>();
	protected Set<Float> canUseElementLevSet = new HashSet<>();
	protected double maxCharge;
	protected float chargeSpeedRatio;
	protected ResourceLocation iconRes;
	protected float minChargeRatio = 0.25f;
	protected float targetChargeLevel = 0;

	public void setIconRes(ResourceLocation iconRes) {
		this.iconRes = iconRes;
	}

	public void setIconRes(String str) {
		setIconRes(TextHelper.toESResourceLocation(str));
	}

	public ResourceLocation getIconRes() {
		return iconRes;
	}

	public void setMaxCharge(double maxCharge) {
		this.maxCharge = maxCharge;
	}

	public void setMaxCharge(ElementStack eStack) {
		this.maxCharge = ElementTransition.toFragment(eStack);
	}

	public double getMaxCharge(World world, ElementTransitionReactor core) {
		return maxCharge;
	}

	public void setTargetChargeLevel(float targetChargeLevel) {
		this.targetChargeLevel = targetChargeLevel;
	}

	public float getTargetChargeLevel(World world, ElementTransitionReactor core) {
		return targetChargeLevel;
	}

	public void setMinChargeRatio(double minCharge) {
		this.minChargeRatio = (float) Math.min(minCharge, 1);
	}

	public void setChargeSpeedRatio(double chargetSpeed) {
		this.chargeSpeedRatio = (float) chargetSpeed;
	}

	public float getChargetSpeedRatio(World world, ElementTransitionReactor core) {
		return chargeSpeedRatio;
	}

	public void addCanUseElement(Element element) {
		if (element == null) return;
		canUseElementSet.add(element);
	}

	public void addCanUseElementWithSameLevel(Element element) {
		if (element == null) return;
		ElementTransition et = element.getTransition();
		if (et == null) return;
		canUseElementLevSet.add(et.getLevel());
	}

	@Override
	public boolean canUse(ElementTransitionReactor core) {
//		if (ElementalSorcery.isDevelop) return true;
		if (canUseElementSet.isEmpty() && canUseElementLevSet.isEmpty()) return true;
		if (canUseElementSet.contains(core.getElement())) return true;
		ElementTransition et = core.getElement().getTransition();
		if (et == null) return false;
		return canUseElementLevSet.contains(et.getLevel());
	}

	@Override
	public float charging(World world, ElementTransitionReactor core, VariableSet content) {
		double charge = content.get(CHARGE);
		double maxCharge = getMaxCharge(world, core);
		if (charge >= maxCharge) return (float) (charge / maxCharge);
		double needCharge = getChargetSpeedRatio(world, core) * maxCharge;
		double fragment;
		float tLev = getTargetChargeLevel(world, core);
		if (tLev >= 1) fragment = chargeWithLevel(core, needCharge, tLev);
		else fragment = core.shrink(needCharge);
		content.set(CHARGE, charge + fragment);
		return (float) (content.get(CHARGE) / maxCharge);
	}

	protected double chargeWithLevel(ElementTransitionReactor core, double needCharge, float lev) {
		double fragment;
		Element element = core.getElement();
		if (lev > 1) needCharge = ElementTransition.transitionFrom(element, needCharge, lev);
		ElementTransition et = element.getTransition();
		if (et != null) {
			needCharge = ElementTransition.transitionTo(element, needCharge, et.getLevel());
			fragment = core.shrink(needCharge);
			fragment = ElementTransition.transitionFrom(element, fragment, et.getLevel());
		} else fragment = core.shrink(needCharge);
		if (lev > 1) fragment = ElementTransition.transitionTo(element, needCharge, lev);
		return fragment;
	}

	@Override
	public float getMinCanCastCharge(World world, ElementTransitionReactor core, VariableSet content) {
		return this.minChargeRatio;
	}

	@Override
	public boolean needContinueReact(World world, ElementTransitionReactor core, VariableSet content) {
		double fragment = core.getFragment();
		float tLev = getTargetChargeLevel(world, core);
		if (tLev >= 1) {
			Element element = core.getElement();
			ElementTransition et = element.getTransition();
			if (et != null) fragment = ElementTransition.transitionFrom(element, fragment, et.getLevel());
			if (tLev > 1) fragment = ElementTransition.transitionTo(element, fragment, tLev);
		}
		return fragment < getMaxCharge(world, core);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderIcon(float suggestSize, float alpha, float partialTicks) {
		ResourceLocation res = getIconRes();
		if (res == null) res = ESResources.MANTRA_VOID.getResource();
		ESResources.MANTRA_COMMON_CIRCLE.bind();
		RenderFriend.drawTexturedRectInCenter(0, 0, suggestSize, suggestSize);
		TextureBinder.bindTexture(res);
		suggestSize = suggestSize * 0.5f;
		RenderFriend.drawTexturedRectInCenter(0, 0, suggestSize, suggestSize);
	}

}
