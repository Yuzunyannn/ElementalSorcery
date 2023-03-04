package yuzunyannn.elementalsorcery.api.gfunc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class GameFuncCarrier implements IGameFuncCarrier {

	@CapabilityInject(IGameFuncCarrier.class)
	public static Capability<IGameFuncCarrier> GAMEFUNCCARRIER_CAPABILITY;

	protected Map<String, List<GameFunc>> map = new TreeMap<>();

	@Override
	public List<GameFunc> getFuncList(String triggerName) {
		return map.get(triggerName);
	}

	@Override
	public void setFuncList(String triggerName, List<GameFunc> list) {
		if (list == null) map.remove(triggerName);
		else map.put(triggerName, list);
	}

	@Override
	public void addFunc(String triggerName, GameFunc func) {
		List<GameFunc> list = map.get(triggerName);
		if (list == null) map.put(triggerName, list = new ArrayList<>());
		list.add(func);
	}

	@Override
	public Collection<String> getTriggers() {
		return map.keySet();
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public String toString() {
		return map.toString();
	}

	public static class Provider implements ICapabilitySerializable<NBTTagCompound> {

		private IGameFuncCarrier carrier;
		public final static IStorage<IGameFuncCarrier> storage = GAMEFUNCCARRIER_CAPABILITY.getStorage();

		public Provider() {
			this(null);
		}

		public Provider(IGameFuncCarrier carrier) {
			this.carrier = carrier == null ? new GameFuncCarrier() : carrier;
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return GAMEFUNCCARRIER_CAPABILITY.equals(capability);
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if (GAMEFUNCCARRIER_CAPABILITY.equals(capability)) return (T) carrier;
			return null;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			return (NBTTagCompound) storage.writeNBT(GAMEFUNCCARRIER_CAPABILITY, carrier, null);
		}

		@Override
		public void deserializeNBT(NBTTagCompound compound) {
			storage.readNBT(GAMEFUNCCARRIER_CAPABILITY, carrier, null, compound);
		}
	}

}
