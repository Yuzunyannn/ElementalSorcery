package yuzunyannn.elementalsorcery.computer.soft;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.IDisk;
import yuzunyannn.elementalsorcery.api.computer.soft.APP;
import yuzunyannn.elementalsorcery.api.util.var.IVariableSet;
import yuzunyannn.elementalsorcery.api.util.var.Variable;
import yuzunyannn.elementalsorcery.computer.DeviceStorage;
import yuzunyannn.elementalsorcery.computer.exception.ComputerPermissionDeniedException;

public class AuthorityAppDisk implements IDeviceStorage {

	protected final IComputer computer;
	protected final APP app;
	protected final List<AuthorityStorage> storages;
	protected final AuthorityStorage coreStorage;
	protected final IDisk coreDisk;

	public AuthorityAppDisk(IComputer computer, APP app, List<IDisk> disks, String namespace) {
		this.computer = computer;
		this.app = app;
		this.storages = new ArrayList<>();

		String id = app.getAppId().toString();
		IDisk coreDisk = disks.get(0);
		String[] paths = new String[] { namespace, id };

		for (IDisk _disk : disks) {
			IVariableSet variableSet = _disk.getVariableSet(namespace);
			if (variableSet.has(id)) {
				this.storages.add(new AuthorityStorage(computer, _disk, paths, app));
				if (_disk.isWriteable()) coreDisk = _disk;
			}
		}
		if (!coreDisk.isWriteable()) {
			for (IDisk _disk : disks) {
				if (_disk.isWriteable()) coreDisk = _disk;
			}
		}
		IVariableSet variableSet = coreDisk.getVariableSet(namespace);
		if (!variableSet.has(id)) this.storages.add(new AuthorityStorage(computer, coreDisk, paths, app));

		this.coreDisk = coreDisk;

		AuthorityStorage coreStorage = this.storages.get(0);
		Iterator<AuthorityStorage> iter = this.storages.iterator();
		while (iter.hasNext()) {
			AuthorityStorage as = iter.next();
			if (as.getStorage() == coreDisk) {
				coreStorage = as;
				iter.remove();
				break;
			}
		}

		this.coreStorage = coreStorage;
	}

	@Override
	public <T> void set(Variable<T> var, T obj) {
		coreStorage.set(var, obj);
	}

	@Override
	public void set(String key, NBTBase tag) {
		coreStorage.set(key, tag);
	}

	@Override
	public <T> T get(Variable<T> var) {
		for (AuthorityStorage storage : storages) if (storage.has(var)) return storage.get(var);
		return coreStorage.get(var);
	}

	@Override
	public NBTBase get(String key) {
		for (AuthorityStorage storage : storages) if (storage.has(key)) return storage.get(key);
		return coreStorage.get(key);
	}

	@Override
	public boolean has(String key) {
		for (AuthorityStorage storage : storages) if (storage.has(key)) return true;
		return coreStorage.has(key);
	}

	@Override
	public void remove(String key) {
		for (AuthorityStorage storage : storages) if (storage.isWriteable()) storage.remove(key);
		coreStorage.remove(key);
	}

	@Override
	public Object ask(String name) {
		for (AuthorityStorage storage : storages) if (storage.has(name)) return storage.ask(name);
		return coreStorage.ask(name);
	}

	@Override
	public void clear() {
		for (AuthorityStorage storage : storages) if (storage.isWriteable()) storage.clear();
		coreStorage.clear();
	}

	@Override
	public boolean isEmpty() {
		for (AuthorityStorage storage : storages) if (!storage.isEmpty()) return false;
		if (!coreStorage.isEmpty()) return false;
		return true;
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
		return coreDisk.setFlag(flag, has);
	}

	@Override
	public boolean hasFlag(int flag) {
		return coreDisk.hasFlag(flag);
	}

}
