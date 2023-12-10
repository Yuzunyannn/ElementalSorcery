package yuzunyannn.elementalsorcery.computer.soft;

import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
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

	public AuthorityStorage(IComputer computer, IDeviceStorage storage, IVariableSet variableSet, APP app) {
		this.computer = computer;
		this.storage = storage;
		this.variableSet = variableSet;
		this.app = app;
	}
	
	public IDeviceStorage getStorage() {
		return storage;
	}

	protected void writeableCheck() {
		if (!isWriteable()) throw new ComputerReadOnlyException(computer, storage);
	}

	@Override
	public <T> void set(Variable<T> var, T obj) {
		writeableCheck();
		variableSet.set(var, obj);
	}

	@Override
	public <T> T get(Variable<T> var) {
		return variableSet.get(var);
	}

	@Override
	public boolean has(Variable<?> var) {
		return variableSet.has(var);
	}

	@Override
	public void remove(Variable<?> var) {
		writeableCheck();
		variableSet.remove(var);
	}

	@Override
	public Object ask(String name) {
		return variableSet.ask(name);
	}

	@Override
	public void clear() {
		writeableCheck();
		variableSet.clear();
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
