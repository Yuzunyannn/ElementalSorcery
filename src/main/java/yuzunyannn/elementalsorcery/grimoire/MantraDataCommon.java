package yuzunyannn.elementalsorcery.grimoire;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.mantra.IMantraData;
import yuzunyannn.elementalsorcery.api.mantra.IProgressable;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet.Variable;
import yuzunyannn.elementalsorcery.util.var.Variables;

public class MantraDataCommon implements IMantraData, IProgressable {

	// ---动态数据----

	private final MantraEffectMap effectMap = new MantraEffectMap();

	public int speedTick;
	protected boolean markContinue;
	/** 当前进度数据 */
	protected float progress = -1;

	// ---持久数据----

	/** 额外数据集 */
	protected VariableSet extra = new VariableSet();

	public MantraDataCommon() {
	}

	public void markContinue(boolean yes) {
		markContinue = yes;
	}

	public boolean isMarkContinue() {
		return markContinue;
	}

	// effect
	@SideOnly(Side.CLIENT)
	public MantraEffectMap getEffectMap() {
		return effectMap;
	}

	// ---进度数据----

	@Override
	public double getProgress() {
		return progress;
	}

	@Override
	public void setProgress(double progress) {
		this.progress = (float) Math.min(1, progress);
	}

	public void setProgress(double now, double total) {
		this.setProgress(Math.min(now / total, 1));
	}

	// ---元素收集部分----

	public void add(ElementStack estack) {
		if (estack.isEmpty()) return;
		ElementStack origin = this.get(estack.getElement());
		if (origin.isEmpty()) origin.become(estack);
		else origin.grow(estack);
	}

	public ElementStack get(Element element) {
		return get(Variables.getElementVar(element));
	}

	public void remove(Element element) {
		remove(new Variable<>("E^" + element.getRegistryId(), VariableSet.ELEMENT));
	}

	public static class CollectResult {

		public final ElementStack eStack;
		public final ElementStack get;
		public final boolean isFinish;

		public CollectResult(ElementStack a, ElementStack b, boolean c) {
			eStack = a;
			get = b;
			isFinish = c;
		}

		public ElementStack getElementStack() {
			return eStack;
		}

		public ElementStack getElementStackGetted() {
			return get;
		}

		public int getStackCount() {
			return eStack.getCount();
		}
	}

	public CollectResult tryCollect(ICaster caster, Element element, int point, int power, int max) {
		ElementStack origin = this.get(element);
		if (origin.getCount() >= max) return new CollectResult(origin, ElementStack.EMPTY, true);
		ElementStack need = new ElementStack(element, point, power);
		ElementStack get = caster.iWantSomeElement(need, true);
		if (get.isEmpty()) return new CollectResult(origin, ElementStack.EMPTY, false);
		origin.growOrBecome(get);
		return new CollectResult(origin, get, origin.getCount() >= max);
	}

	// ---额外数据部分----

	public <T> void set(VariableSet.Variable<T> var, T obj) {
		extra.set(var, obj);
	}

	public <T> T get(VariableSet.Variable<T> var) {
		return extra.get(var);
	}

	public boolean has(VariableSet.Variable<?> var) {
		return extra.has(var);
	}

	public void remove(VariableSet.Variable<?> var) {
		extra.remove(var);
	}

	public void setExtra(VariableSet extra) {
		this.extra = extra;

	}

	public VariableSet getExtra() {
		return extra;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = extra.serializeNBT();
		nbt.setBoolean("_mConti", markContinue);
		return nbt;
	}

	@Override
	public NBTTagCompound serializeNBTForSend() {
		NBTTagCompound nbt = extra.serializeNBT((str, obj) -> !str.startsWith("@"));
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		extra.deserializeNBT(nbt);
		markContinue = nbt.getBoolean("_mConti");
	}

}
