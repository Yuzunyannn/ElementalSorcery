package yuzunyannn.elementalsorcery.api.gfunc;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class GameFuncCarrier implements IGameFuncCarrier {

	@CapabilityInject(IGameFuncCarrier.class)
	public static Capability<IGameFuncCarrier> GAMEFUNCCARRIER_CAPABILITY;

	protected Map<String, GameFunc> map = new TreeMap<>();

	@Override
	public GameFunc getFunc(String triggerName) {
		GameFunc func = map.get(triggerName);
		return func == null ? GameFunc.NOTHING : func;
	}
	
	@Override
	public boolean hasFunc(String triggerName) {
		return map.containsKey(triggerName);
	}

	@Override
	public void setFunc(String triggerName, GameFunc func) {
		if (func == null || func == GameFunc.NOTHING) map.remove(triggerName);
		else map.put(triggerName, func);
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

	public GameFuncCarrier copy() {
		GameFuncCarrier carrier = new GameFuncCarrier();
		for (String key : getTriggers()) carrier.setFunc(key, getFunc(key).copy());
		return carrier;
	}

	public void giveTo(IGameFuncCarrier other) {
		if (other == null) return;
		for (String triggerName : this.getTriggers()) {
			other.addFunc(triggerName, this.getFunc(triggerName).copy());
		}
	}

	public void giveTo(ICapabilityProvider provider) {
		this.giveTo(provider.getCapability(GameFuncCarrier.GAMEFUNCCARRIER_CAPABILITY, null));
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
