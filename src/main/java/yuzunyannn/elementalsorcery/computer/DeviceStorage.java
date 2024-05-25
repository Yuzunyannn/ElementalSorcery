package yuzunyannn.elementalsorcery.computer;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.StoragePath;
import yuzunyannn.elementalsorcery.api.util.var.Variable;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;

public abstract class DeviceStorage implements IDeviceStorage {

	protected int flag = 0;

	@Override
	public DeviceStorage setFlag(int flag, boolean has) {
		if (has) this.flag = this.flag | flag;
		else this.flag = this.flag & (~flag);
		return this;
	}

	@Override
	public boolean hasFlag(int flag) {
		return (this.flag & flag) != 0;
	}

	protected VariableSet set = new VariableSet();

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("#S", set.serializeNBT());
		nbt.setInteger("#F", flag);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		set.deserializeNBT(nbt.getCompoundTag("#S"));
		flag = nbt.getInteger("#F");
	}

	@Override
	public <T> void set(Variable<T> var, T obj) {
		set.set(var, obj);
	}

	@Override
	public void set(String key, NBTBase tag) {
		set.set(key, tag);
	}

	@Override
	public <T> T get(Variable<T> var) {
		return set.get(var);
	}

	@Override
	public NBTBase get(String key) {
		return set.get(key);
	}

	@Override
	public boolean has(String key) {
		return set.has(key);
	}

	@Override
	public void remove(String key) {
		set.remove(key);
	}

	@Override
	public Object ask(String name) {
		return set.ask(name);
	}

	@Override
	public void clear() {
		set.clear();
	}

	@Override
	public boolean isEmpty() {
		return set.isEmpty();
	}

	@Override
	public void markDirty(StoragePath path) {
		
	}

}
