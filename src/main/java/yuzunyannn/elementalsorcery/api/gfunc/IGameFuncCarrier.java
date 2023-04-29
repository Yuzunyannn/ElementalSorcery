package yuzunyannn.elementalsorcery.api.gfunc;

import java.util.Collection;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

public interface IGameFuncCarrier extends INBTSerializable<NBTTagCompound> {

	/**
	 * if null return GameFunc.NOTHING
	 */
	@Nonnull
	public GameFunc getFunc(String triggerName);

	public void setFunc(String triggerName, GameFunc func);

	default public boolean hasFunc(String triggerName) {
		return getFunc(triggerName) != GameFunc.NOTHING;
	}

	default public void addFunc(String triggerName, GameFunc func) {
		GameFunc ofunc = getFunc(triggerName);
		if (ofunc == GameFunc.NOTHING) {
			setFunc(triggerName, func);
			return;
		}
		if (ofunc instanceof GameFuncGroup) ((GameFuncGroup) ofunc).addFunc(ofunc);
		else {
			GameFuncGroup gfunc = (GameFuncGroup) GameFunc.create("group");
			setFunc(triggerName, gfunc);
			gfunc.addFunc(ofunc).addFunc(func);
		}
	}

	public Collection<String> getTriggers();

	public boolean isEmpty();

	public void clear();

	default public void trigger(String name, GameFuncExecuteContext context) {
		if (context == null) return;
		GameFunc func = getFunc(name);
		if (func == GameFunc.NOTHING) return;
		GameFunc nFunc = context.doExecute(func);
		if (nFunc == func) return;
		setFunc(name, nFunc);
	}

	@Override
	default void deserializeNBT(NBTTagCompound nbt) {
		this.clear();
		for (String key : nbt.getKeySet()) setFunc(key, GameFunc.create(nbt.getCompoundTag(key)));
	}

	@Override
	default NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		for (String name : getTriggers()) nbt.setTag(name, getFunc(name).serializeNBT());
		return nbt;
	}

	public static class Storage implements Capability.IStorage<IGameFuncCarrier> {

		@Override
		public NBTBase writeNBT(Capability<IGameFuncCarrier> capability, IGameFuncCarrier inst, EnumFacing side) {
			return inst.serializeNBT();
		}

		@Override
		public void readNBT(Capability<IGameFuncCarrier> capability, IGameFuncCarrier inst, EnumFacing side,
				NBTBase tag) {
			if (tag == null) return;
			inst.deserializeNBT((NBTTagCompound) tag);
		}

	}
}
