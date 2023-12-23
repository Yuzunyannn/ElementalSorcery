package yuzunyannn.elementalsorcery.computer.soft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.IDisk;
import yuzunyannn.elementalsorcery.api.computer.IMemory;
import yuzunyannn.elementalsorcery.api.computer.IStorageMonitor;
import yuzunyannn.elementalsorcery.api.computer.StoragePath;
import yuzunyannn.elementalsorcery.api.computer.soft.APP;
import yuzunyannn.elementalsorcery.api.computer.soft.AppDiskType;
import yuzunyannn.elementalsorcery.api.computer.soft.IComputerException;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.util.var.Variable;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.computer.exception.ComputerAppDamagedException;
import yuzunyannn.elementalsorcery.computer.exception.ComputerBootException;
import yuzunyannn.elementalsorcery.computer.exception.ComputerHardwareMissingException;
import yuzunyannn.elementalsorcery.computer.exception.ComputerNewProcessException;
import yuzunyannn.elementalsorcery.computer.exception.ComputerProcessNotExistException;
import yuzunyannn.elementalsorcery.computer.soft.ProcessTree.ProcessNode;

public abstract class EOS implements IOS {

	static public final Variable<String> BOOT = new Variable("~boot", VariableSet.STRING);

	static public final Variable<ProcessTree> PROCESS = new Variable(">process", new ProcessTree.VTProcessMap());
	static public final StoragePath PATH_PROCESS = StoragePath.of(PROCESS.key);

	public final IComputer computer;
	public AuthorityMemory memoryCache = null;
	public Map<String, AuthorityMemory> appMemoryCacheMap = new HashMap<>();
	public List<AuthorityDisk> disksCache = null;
	public Map<String, AuthorityAppDisk> appDiskCacheMap = new HashMap<>();

	public EOS(IComputer computer) {
		this.computer = computer;
	}

	@Override
	public IMemory getMemory() {
		if (memoryCache == null) {
			IMemory memory = computer.getMemory();
			if (memory == null) throw new ComputerHardwareMissingException(this.computer, "memory is missing");
			memoryCache = new AuthorityMemory(computer, memory, null, null);
		}
		return memoryCache;
	}

	@Override
	public IMemory getMemory(APP app) {
		String id = String.format(">app#%d", app.getPid());
		if (appMemoryCacheMap.containsKey(id)) return appMemoryCacheMap.get(id);
		IMemory memory = this.getMemory();
		AuthorityMemory storage = new AuthorityMemory(computer, memory, new String[] { id }, app);
		appMemoryCacheMap.put(id, storage);
		return storage;
	}

	@Override
	public List<IDisk> getDisks() {
		if (disksCache == null) {
			disksCache = new ArrayList<>();
			List<IDisk> disks = computer.getDisks();
			for (IDisk disk : disks) disksCache.add(new AuthorityDisk(computer, disk, null, null));
		}
		return (List<IDisk>) ((Object) disksCache);
	}

	@Override
	public IDeviceStorage getDisk(APP app, AppDiskType type) {
		String key = app.getAppId().toString() + "_" + type.key;
		if (appDiskCacheMap.containsKey(key)) return appDiskCacheMap.get(key);
		List<IDisk> list = getDisks();
		if (list.isEmpty()) throw new ComputerHardwareMissingException(this.computer, "disk is missing");
		AuthorityAppDisk disk = new AuthorityAppDisk(computer, app, list, type.key);
		appDiskCacheMap.put(key, disk);
		return disk;
	}

	@Nullable
	public IStorageMonitor getMemoryMonitor() {
		return computer.getStorageMonitor(computer.getMemory());
	}

	@Override
	public int exec(APP parent, String appId) {
		IMemory memory = this.getMemory();
		ProcessTree tree = memory.get(PROCESS);
		int id = tree.newProcess(appId, parent.getPid());
		if (id == -1) throw new ComputerNewProcessException(computer, appId);
		IStorageMonitor storageMonitor = getMemoryMonitor();
		if (storageMonitor != null) storageMonitor.markDirty(PATH_PROCESS);
		onAppStartup(tree.getAppCache(this, id));
		return id;
	}

	@Override
	public int setForeground(int pid) {
		IMemory memory = this.getMemory();
		ProcessTree tree = memory.get(PROCESS);
		if (!tree.hasProcess(pid)) return -1;
		tree.setForeground(pid);
		IStorageMonitor storageMonitor = getMemoryMonitor();
		if (storageMonitor != null) storageMonitor.markDirty(PATH_PROCESS);
		return pid;
	}

	@Override
	public int getForeground() {
		IMemory memory = this.getMemory();
		return memory.get(PROCESS).getForeground();
	}

	@Override
	public APP getAppInst(int pid) {
		IMemory memory = this.getMemory();
		ProcessTree tree = memory.get(PROCESS);
		if (!tree.hasProcess(pid)) throw new ComputerProcessNotExistException(this.computer, String.valueOf(pid));
		APP app = tree.getAppCache(this, pid);
		if (app == null) throw new ComputerAppDamagedException(this.computer, String.valueOf(tree.getProcessId(pid)));
		return app;
	}

	@Override
	public void onStarting() {
		List<IDisk> list = this.getDisks();
		String boot = null;
		for (IDisk disk : list) {
			if (disk.has(BOOT)) {
				boot = disk.get(BOOT);
				break;
			}
		}
		if (boot == null) throw new ComputerBootException(computer, "cannot find boot");
		IMemory memory = this.getMemory();
		ProcessTree tree = memory.get(PROCESS);
		int id = tree.newProcess(boot, 0);
		if (id == -1) throw new ComputerBootException(computer, "root process fail");

		IStorageMonitor storageMonitor = getMemoryMonitor();
		if (storageMonitor != null) {
			storageMonitor.add(PATH_PROCESS);
			storageMonitor.markDirty(PATH_PROCESS);
		}

		onAppStartup(tree.getAppCache(this, id));
	}

	@Override
	public void abort(int pid, IComputerException e) {
		IMemory memory = this.getMemory();
		ProcessTree tree = memory.get(PROCESS);
		if (!tree.hasProcess(pid)) return;
		onAbort(tree, pid, e);
		tree.removeProcess(pid);
		IStorageMonitor storageMonitor = getMemoryMonitor();
		if (storageMonitor != null) storageMonitor.markDirty(PATH_PROCESS);
	}

	protected void onAbort(ProcessTree tree, int pid, IComputerException e) {
		int parentPid = tree.getParent(pid);
		if (parentPid == -1) parentPid = 0;
		tree.setForeground(parentPid);
	}

	@Override
	public void onUpdate() {
		IMemory memory = this.getMemory();
		ProcessTree tree = memory.get(PROCESS);
		Iterator<Entry<Integer, ProcessNode>> iter = tree.getIterator();
		boolean hasChange = false;
		while (iter.hasNext()) {
			Entry<Integer, ProcessNode> entry = iter.next();
			try {
				tree.getAppCache(this, entry.getKey()).onUpdate();
			} catch (Exception e) {
				if (entry.getKey() == 0) throw e;
				if (e instanceof IComputerException) {
					this.onAbort(tree, entry.getKey(), (IComputerException) e);
					iter.remove();
					hasChange = true;
				} else throw e;
			}
		}
		if (hasChange) {
			IStorageMonitor storageMonitor = getMemoryMonitor();
			if (storageMonitor != null) storageMonitor.markDirty(PATH_PROCESS);
		}
	}

	protected void onAppStartup(APP app) {
		app.onStartup();
	}

	@Override
	public void onMemoryChange() {
		memoryCache = null;
		appMemoryCacheMap.clear();
	}

	@Override
	public void onDiskChange() {
		disksCache = null;
		appDiskCacheMap.clear();
	}

}
