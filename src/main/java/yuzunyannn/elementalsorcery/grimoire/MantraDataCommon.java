package yuzunyannn.elementalsorcery.grimoire;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.EffectCondition;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectScreenProgress;
import yuzunyannn.elementalsorcery.util.var.VariableSet;
import yuzunyannn.elementalsorcery.util.var.Variables;
import yuzunyannn.elementalsorcery.util.var.VariableSet.Variable;

public class MantraDataCommon implements IMantraData {

	// ---动态数据----

	public final Map<Short, Effect> effectMap = new HashMap<>();

	public int speedTick;
	protected boolean markContinue;
	/** 当前进度数据 */
	protected float progress = -1;
	/** 客户端的进度条 */
	public EffectScreenProgress effectProgress;

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
	public void markEffect(int id, Effect effect) {
		effectMap.put((short) id, effect);
	}

	@SideOnly(Side.CLIENT)
	public void unmarkEffect(int id) {
		effectMap.remove((short) id);
	}

	@SideOnly(Side.CLIENT)
	public boolean hasMarkEffect(int id) {
		return effectMap.containsKey((short) id);
	}

	@SideOnly(Side.CLIENT)
	public Effect getMarkEffect(int id) {
		return effectMap.get((short) id);
	}

	@SideOnly(Side.CLIENT)
	public <T extends Effect> T getMarkEffect(int id, Class<T> cls) {
		Effect effect = getMarkEffect(id);
		if (effect == null) return null;
		if (cls.isAssignableFrom(effect.getClass())) return (T) effect;
		return null;
	}

	@SideOnly(Side.CLIENT)
	public void removeMarkEffect(int id) {
		Effect effect = effectMap.get((short) id);
		if (effect instanceof EffectCondition) {
			Function<Void, Boolean> cond = ((EffectCondition) effect).getCondition();
			if (cond instanceof EffectCondition.ConditionEntityAction)
				((EffectCondition.ConditionEntityAction) cond).isFinish = true;
		}
		this.unmarkEffect(id);
	}

	@SideOnly(Side.CLIENT)
	public void addConditionEffect(ICaster caster, EffectCondition effect, int markId, boolean checkContinue) {
		if (effect instanceof EffectCondition) {
			EffectCondition eCondition = (EffectCondition) effect;
			if (eCondition.getCondition() == null) {
				IWorldObject co = caster.iWantCaster();
				if (co.asEntity() == null) return;
				eCondition.setCondition(new ConditionEffect(co.asEntity(), this, markId, checkContinue));
			}
		}
		markEffect(markId, effect);
		Effect.addEffect(effect);
	}

	@SideOnly(Side.CLIENT)
	public void addConditionEffect(ICaster caster, EffectCondition effect, int markId) {
		addConditionEffect(caster, effect, markId, true);
	}

	/** 通用性的条件 */
	@SideOnly(Side.CLIENT)
	public static class ConditionEffect extends EffectCondition.ConditionEntityAction {

		public final MantraDataCommon data;
		public final short unmark; // 标记
		public final boolean checkContinue;

		public ConditionEffect(Entity entity, MantraDataCommon data, int unmark, boolean checkContinue) {
			super(entity);
			this.data = data;
			this.unmark = (short) unmark;
			this.checkContinue = checkContinue;
		}

		@Override
		public Boolean apply(Void t) {
			if (isFinish) return false;
			if (checkContinue) {
				isFinish = !data.isMarkContinue();
				if (isFinish) {
					data.unmarkEffect(unmark);
					return false;
				}
			}
			super.apply(t);
			if (isFinish) {
				data.unmarkEffect(unmark);
				return false;
			}
			return true;
		}
	}

	// ---进度数据----

	public float getProgress() {
		return progress;
	}

	public void setProgress(float progress) {
		this.progress = progress;
	}

	public void setProgress(float now, float total) {
		this.setProgress(Math.min(now / total, 1));
	}

	@SideOnly(Side.CLIENT)
	public void showProgress(float pro, int color, World world, ICaster caster) {
		if (!caster.iWantCaster().isClientPlayer()) return;
		if (!this.hasMarkEffect(2)) {
			effectProgress = new EffectScreenProgress(world);
			effectProgress.setColor(color);
			this.addConditionEffect(caster, effectProgress, 2, false);
		} else {
			if (effectProgress == null) return;
			effectProgress.setProgress(pro);
		}
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
		return extra.serializeNBT();
	}

	@Override
	public NBTTagCompound serializeNBTForSend() {
		NBTTagCompound nbt = extra.serializeNBT((str, obj) -> !str.startsWith("@"));
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		extra.deserializeNBT(nbt);
	}

}
