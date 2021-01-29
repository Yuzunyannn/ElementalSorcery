package yuzunyannn.elementalsorcery.grimoire;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectCondition;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectScreenProgress;
import yuzunyannn.elementalsorcery.util.NBTTag;
import yuzunyannn.elementalsorcery.util.VariableSet;

public class MantraDataCommon implements IMantraData {

	// ---动态数据----

	public final Set<Short> effectSet = new HashSet<>();
	public int speedTick;
	protected boolean markContinue;
	/** 当前进度数据 */
	protected float progress = -1;
	/** 客户端的进度条 */
	public EffectScreenProgress effectProgress;

	// ---持久数据----

	/** 额外数据集 */
	protected VariableSet extra = new VariableSet();
	/** 搜集的元素 */
	protected Map<Element, ElementStack> collectMap;

	public MantraDataCommon() {
	}

	public void markContinue(boolean yes) {
		markContinue = yes;
	}

	public boolean isMarkContinue() {
		return markContinue;
	}

	public void markEffect(int id) {
		effectSet.add((short) id);
	}

	public void unmarkEffect(int id) {
		effectSet.remove((short) id);
	}

	public boolean hasMarkEffect(int id) {
		return effectSet.contains((short) id);
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
		if (caster.iWantCaster() != Minecraft.getMinecraft().player) return;
		if (!this.hasMarkEffect(10)) {
			effectProgress = new EffectScreenProgress(world);
			effectProgress.setColor(color);
			this.addEffect(caster, effectProgress, 10, false);
		} else {
			if (effectProgress == null) return;
			effectProgress.setProgress(pro);
		}
	}

	@SideOnly(Side.CLIENT)
	public void addEffect(ICaster caster, EffectCondition effect, int markId, boolean checkContinue) {
		markEffect(markId);
		if (effect.getCondition() == null) {
			Entity entity = caster.iWantCaster();
			effect.setCondition(new ConditionEffect(entity, this, markId, checkContinue));
		}
		Effect.addEffect(effect);
	}

	@SideOnly(Side.CLIENT)
	public void addEffect(ICaster caster, EffectCondition effect, int markId) {
		addEffect(caster, effect, markId, true);
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
	// ---元素收集部分----

	public void addElement(ElementStack estack) {
		if (estack.isEmpty()) return;
		ElementStack origin = this.getElement(estack.getElement());
		if (origin.isEmpty()) origin.become(estack);
		else origin.grow(estack);
	}

	public ElementStack getElement(Element element) {
		if (collectMap == null) collectMap = new HashMap<>();
		ElementStack estack = collectMap.get(element);
		if (estack == null) collectMap.put(element, estack = ElementStack.EMPTY.copy());
		return estack;
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
	}

	public CollectResult tryCollect(ICaster caster, Element element, int point, int power, int max) {
		ElementStack origin = this.getElement(element);
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

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = extra.serializeNBT();
		if (collectMap != null && !collectMap.isEmpty()) {
			NBTTagList list = new NBTTagList();
			for (ElementStack estack : collectMap.values())
				if (!estack.isEmpty()) list.appendTag(estack.serializeNBT());
			nbt.setTag("$collect", list);
		}
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		extra.deserializeNBT(nbt);

		NBTTagList list = nbt.getTagList("$collect", NBTTag.TAG_COMPOUND);
		if (!list.hasNoTags()) {
			if (collectMap == null) collectMap = new HashMap<>();
			collectMap.clear();
			for (NBTBase base : list) this.addElement(new ElementStack((NBTTagCompound) base));
		}
	}

}
