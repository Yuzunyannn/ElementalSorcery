package yuzunyannn.elementalsorcery.grimoire;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectCondition;

public class MantraDataCommon implements IMantraData {

	public final ICaster caster;
	public final Set<Short> effectSet = new HashSet<>();
	public boolean markContinue;

	public MantraDataCommon(ICaster caster) {
		this.caster = caster;
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

	@SideOnly(Side.CLIENT)
	public void addEffect(EffectCondition effect, int markId) {
		markEffect(markId);
		if (effect.getCondition() == null) {
			Entity entity = caster.iWantCaster();
			if (entity instanceof EntityLivingBase)
				effect.setCondition(new ConditionEffect((EntityLivingBase) entity, this, markId));
		}
		Effect.addEffect(effect);
	}

	/** 通用性的条件 */
	public static class ConditionEffect extends EffectCondition.ConditionEntityAction {

		public final MantraDataCommon data;
		public final short unmark;

		public ConditionEffect(EntityLivingBase entity, MantraDataCommon data, int unmark) {
			super(entity);
			this.data = data;
			this.unmark = (short) unmark;
		}

		@Override
		public Boolean apply(Void t) {
			if (isFinish) return false;
			isFinish = !data.isMarkContinue() || !entity.isHandActive();
			if (isFinish) data.unmarkEffect(unmark);
			return !isFinish;
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return null;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
	}

}
