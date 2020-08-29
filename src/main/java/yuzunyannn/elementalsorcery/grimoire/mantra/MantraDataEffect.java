package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectCondition;

public class MantraDataEffect implements IMantraData {

	public final ICaster caster;
	public final Set<Short> effectSet = new HashSet<>();
	public boolean markContinue;

	public MantraDataEffect(ICaster caster) {
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

	public boolean hasMarkEffect(int id) {
		return effectSet.contains((short) id);
	}

	@SideOnly(Side.CLIENT)
	public void addEffect(EffectCondition effect, int markId) {
		markEffect(markId);
		Effect.addEffect(effect);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return null;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
	}

}
