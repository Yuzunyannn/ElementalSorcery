package yuzunyannn.elementalsorcery.api.gfunc;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

public interface IGameFuncCarrier extends INBTSerializable<NBTTagCompound> {

	public List<GameFunc> getFuncList(String triggerName);

	public void setFuncList(String triggerName, List<GameFunc> list);

	public void addFunc(String triggerName, GameFunc func);

	public Collection<String> getTriggers();

	public boolean isEmpty();

	public void clear();

	default public <T extends GameFuncExecuteContext> void trigger(String name,
			BiFunction<GameFunc, Consumer<GameFunc>, T> factory) {
		List<GameFunc> list = getFuncList(name);
		if (list == null) return;
		for (int i = 0; i < list.size(); i++) {
			final int index = i;
			GameFuncExecuteContext context = factory.apply(list.get(index), n -> list.set(index, n));
			if (context == null) continue;
			context.doExecute();
		}
	}

	@Override
	default void deserializeNBT(NBTTagCompound nbt) {
		this.clear();
		for (String key : nbt.getKeySet()) setFuncList(key, GameFunc.deserializeNBTList(nbt, key));
	}

	@Override
	default NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		for (String name : getTriggers()) nbt.setTag(name, GameFunc.serializeNBTList(getFuncList(name)));
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
