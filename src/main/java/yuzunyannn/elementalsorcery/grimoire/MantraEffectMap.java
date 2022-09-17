package yuzunyannn.elementalsorcery.grimoire;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.mantra.CastStatus;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.IConditionEffect;

public class MantraEffectMap {

	public final Map<Short, Effect> effectMap = new HashMap<>();

	public void mark(int id, Effect effect) {
		if (effect instanceof IConditionEffect) {
			Function<Void, Boolean> cond = ((IConditionEffect) effect).getCondition();
			if (cond instanceof MantraCondition) ((MantraCondition) cond).unmark = (short) id;
		}
		effectMap.put((short) id, effect);
	}

	public void unmark(int id) {
		effectMap.remove((short) id);
	}

	public void removeMark(int id) {
		Effect effect = effectMap.get((short) id);
		if (effect instanceof IConditionEffect) {
			Function<Void, Boolean> cond = ((IConditionEffect) effect).getCondition();
			if (cond instanceof MantraCondition) ((MantraCondition) cond).isFinish = true;
		}
		this.unmark(id);
	}

	public void addAndMark(int id, Effect effect) {
		this.mark(id, effect);
		Effect.addEffect(effect);
	}

	public boolean hasMark(int id) {
		return effectMap.containsKey((short) id);
	}

	public Effect getMark(int id) {
		return effectMap.get((short) id);
	}

	public <T extends Effect> T getMark(int id, Class<T> cls) {
		Effect effect = getMark(id);
		if (effect == null) return null;
		if (cls.isAssignableFrom(effect.getClass())) return (T) effect;
		return null;
	}

	@SideOnly(Side.CLIENT)
	public static MantraCondition condition(ICaster caster, MantraDataCommon data) {
		MantraCondition condition = new MantraCondition(caster, data);
		return condition;
	}

	@SideOnly(Side.CLIENT)
	public static MantraCondition condition(ICaster caster, MantraDataCommon data, CastStatus status) {
		MantraCondition condition = new MantraCondition(caster, data);
		return condition.setCheckStatus(status);
	}

	@SideOnly(Side.CLIENT)
	public static class MantraCondition implements Function<Void, Boolean> {

		public final MantraDataCommon data;
		public final ICaster caster;
		protected short unmark; // 标记
		protected boolean isFinish;
		protected boolean checkContinue;
		protected CastStatus status = CastStatus.SPELLING;

		public MantraCondition(ICaster caster, MantraDataCommon data) {
			this.caster = caster;
			this.data = data;
		}

		public MantraCondition setCheckStatus(CastStatus status) {
			this.status = status;
			return this;
		}

		public MantraCondition setCheckContinue(boolean checkContinue) {
			this.checkContinue = checkContinue;
			return this;
		}

		@Override
		public Boolean apply(Void t) {
			if (isFinish) return false;
			if (checkContinue) {
				isFinish = !data.isMarkContinue();
				if (isFinish) {
					data.getEffectMap().unmark(unmark);
					return false;
				}
			}
			if (isFinish = !this.isInCasting()) {
				data.getEffectMap().unmark(unmark);
				return false;
			}
			return true;
		}

		public boolean isInCasting() {
			return caster.getCastStatus() == this.status;
		}
	}

}
