package yuzunyannn.elementalsorcery.grimoire.remote;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.container.gui.GuiMantraShitf;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.VariableSet;
import yuzunyannn.elementalsorcery.util.VariableSet.Variable;
import yuzunyannn.elementalsorcery.util.element.ElementTransitionReactor;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

public abstract class FMantraBase implements IFragmentMantraLauncher {

	public static final Variable<Double> CHARGE = new Variable<>("charge", VariableSet.DOUBLE);

	protected Set<Element> canUseElementSet = new HashSet<>();
	protected float maxCharge;
	protected float chargetSpeed;
	protected ResourceLocation iconRes;
	protected float minCharge = 0.25f;

	public void setIconRes(ResourceLocation iconRes) {
		this.iconRes = iconRes;
	}

	public void setIconRes(String str) {
		setIconRes(TextHelper.toESResourceLocation(str));
	}

	public ResourceLocation getIconRes() {
		return iconRes;
	}

	public void setMaxCharge(float maxCharge) {
		this.maxCharge = maxCharge;
	}

	public float getMaxCharge(World world, ElementTransitionReactor core) {
		return maxCharge;
	}

	public void setMinCharge(float minCharge) {
		this.minCharge = Math.min(minCharge, 1);
	}

	public void setChargetSpeed(float chargetSpeed) {
		this.chargetSpeed = chargetSpeed;
	}

	public float getChargetSpeed(World world, ElementTransitionReactor core) {
		return chargetSpeed * 100;
	}

	public void addCanUseElement(Element element) {
		canUseElementSet.add(element);
	}

	@Override
	public boolean canUse(ElementTransitionReactor core) {
		if (canUseElementSet.isEmpty()) return true;
		return canUseElementSet.contains(core.getElement());
	}

	@Override
	public float charging(World world, ElementTransitionReactor core, VariableSet content) {
		double charge = content.get(CHARGE);
		double maxCharge = getMaxCharge(world, core);
		if (charge >= maxCharge) return (float) (charge / maxCharge);
		content.set(CHARGE, charge + core.shrink(getChargetSpeed(world, core)));
		return (float) (content.get(CHARGE) / maxCharge);
	}

	@Override
	public float getMinCanCastCharge(World world, ElementTransitionReactor core, VariableSet content) {
		return this.minCharge;
	}

	@Override
	public boolean needContinueReact(World world, ElementTransitionReactor core, VariableSet content) {
		return core.getFragment() < getMaxCharge(world, core);
	}

	@Override
	public void renderIcon(float suggestSize, float alpha, float partialTicks) {
		ResourceLocation res = getIconRes();
		if (res == null) res = RenderObjects.MANTRA_VOID;
		TextureBinder.bindTexture(GuiMantraShitf.CIRCLE);
		RenderHelper.drawTexturedRectInCenter(0, 0, suggestSize, suggestSize);
		TextureBinder.bindTexture(res);
		suggestSize = suggestSize * 0.5f;
		RenderHelper.drawTexturedRectInCenter(0, 0, suggestSize, suggestSize);
	}

}
