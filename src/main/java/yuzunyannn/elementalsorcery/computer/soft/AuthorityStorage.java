package yuzunyannn.elementalsorcery.computer.soft;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.StoragePath;
import yuzunyannn.elementalsorcery.api.computer.soft.APP;
import yuzunyannn.elementalsorcery.api.util.var.IVariableSet;
import yuzunyannn.elementalsorcery.api.util.var.Variable;
import yuzunyannn.elementalsorcery.computer.DeviceStorage;
import yuzunyannn.elementalsorcery.computer.exception.ComputerPermissionDeniedException;
import yuzunyannn.elementalsorcery.computer.exception.ComputerReadOnlyException;

public class AuthorityStorage implements IDeviceStorage {

	protected final IComputer computer;
	protected final IDeviceStorage storage;
	protected final IVariableSet variableSet;
	protected final APP app;
	protected final String[] paths;

	public AuthorityStorage(IComputer computer, IDeviceStorage storage, String[] paths, APP app) {
		this.computer = computer;
		this.storage = storage;
		this.app = app;
		this.paths = (paths == null || paths.length <= 0) ? null : paths;
		if (this.paths == null) this.variableSet = storage;
		else this.variableSet = storage.getVariableSet(this.paths);
	}

	public IDeviceStorage getStorage() {
		return storage;
	}

	protected void writeableCheck() {
		if (!isWriteable()) throw new ComputerReadOnlyException(computer, storage);
	}

	@Override
	public void markDirty(StoragePath path) {

	}

	protected void onSet(String name, Object obj) {

	}

	protected void onRemove(String name) {

	}

	protected void onClear() {

	}

	@Override
	public <T> void set(Variable<T> var, T obj) {
		writeableCheck();
		variableSet.set(var, obj);
		if (obj == null) onRemove(var.key);
		else onSet(var.key, obj);
	}

	@Override
	public void set(String key, NBTBase tag) {
		writeableCheck();
		variableSet.set(key, tag);
		if (tag == null) onRemove(key);
		else onSet(key, tag);
	}

	@Override
	public <T> T get(Variable<T> var) {
		return variableSet.get(var);
	}

	@Override
	public NBTBase get(String key) {
		return variableSet.get(key);
	}

	@Override
	public boolean has(String key) {
		return variableSet.has(key);
	}

	@Override
	public void remove(String key) {
		writeableCheck();
		variableSet.remove(key);
		onRemove(key);
	}

	@Override
	public Object ask(String name) {
		return variableSet.ask(name);
	}

	@Override
	public void clear() {
		writeableCheck();
		variableSet.clear();
		onClear();
	}

	@Override
	public boolean isEmpty() {
		return variableSet.isEmpty();
	}

	@Override
	public IVariableSet copy() {
		throw new ComputerPermissionDeniedException(computer, this);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		throw new ComputerPermissionDeniedException(computer, this);
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		throw new ComputerPermissionDeniedException(computer, this);
	}

	@Override
	public DeviceStorage setFlag(int flag, boolean has) {
		writeableCheck();
		return storage.setFlag(flag, has);
	}

	@Override
	public boolean hasFlag(int flag) {
		return storage.hasFlag(flag);
	}

}
