package yuzunyannn.elementalsorcery.grimoire;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectCondition;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectScreenProgress;

public class MantraDataCommon implements IMantraData {

	public final Set<Short> effectSet = new HashSet<>();
	public boolean markContinue;

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

	public EffectScreenProgress effectProgress;

	public void setProgress(float pro, int color, World world, ICaster caster) {
		if (!this.hasMarkEffect(100)) {
			effectProgress = new EffectScreenProgress(world);
			effectProgress.setColor(color);
			this.addEffect(caster, effectProgress, 100, false);
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
			if (entity instanceof EntityLivingBase)
				effect.setCondition(new ConditionEffect((EntityLivingBase) entity, this, markId, checkContinue));
		}
		Effect.addEffect(effect);
	}

	@SideOnly(Side.CLIENT)
	public void addEffect(ICaster caster, EffectCondition effect, int markId) {
		addEffect(caster, effect, markId, true);
	}

	/** 通用性的条件 */
	public static class ConditionEffect extends EffectCondition.ConditionEntityAction {

		public final MantraDataCommon data;
		public final short unmark; // 标记
		public final boolean checkContinue;

		public ConditionEffect(EntityLivingBase entity, MantraDataCommon data, int unmark, boolean checkContinue) {
			super(entity);
			this.data = data;
			this.unmark = (short) unmark;
			this.checkContinue = checkContinue;
		}

		@Override
		public Boolean apply(Void t) {
			if (isFinish) return false;
			if (checkContinue) isFinish = !data.isMarkContinue() || !entity.isHandActive();
			else isFinish = !entity.isHandActive();
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
