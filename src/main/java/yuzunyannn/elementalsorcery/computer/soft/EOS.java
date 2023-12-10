package yuzunyannn.elementalsorcery.computer.soft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.IDisk;
import yuzunyannn.elementalsorcery.api.computer.IMemory;
import yuzunyannn.elementalsorcery.api.computer.soft.APP;
import yuzunyannn.elementalsorcery.api.computer.soft.AppDiskType;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.util.var.IVariableSet;
import yuzunyannn.elementalsorcery.api.util.var.Variable;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.computer.exception.ComputerBootException;
import yuzunyannn.elementalsorcery.computer.exception.ComputerHardwareMissingException;
import yuzunyannn.elementalsorcery.computer.exception.ComputerNewProcessException;

public abstract class EOS implements IOS {

	static public final Variable<String> BOOT = new Variable("~boot", VariableSet.STRING);

	static public final Variable<ProcessTree> PROCESS = new Variable(">process", new ProcessTree.VTProcessMap());

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
			memoryCache = new AuthorityMemory(computer, memory, memory, null);
		}
		return memoryCache;
	}

	@Override
	public IDeviceStorage getMemory(APP app) {
		String id = String.valueOf(app.getPid());
		if (appMemoryCacheMap.containsKey(id)) return appMemoryCacheMap.get(id);
		IMemory memory = this.getMemory();
		IVariableSet set = memory.getVariableSet(id);
		AuthorityMemory storage = new AuthorityMemory(computer, memory, set, app);
		appMemoryCacheMap.put(id, storage);
		return storage;
	}

	@Override
	public List<IDisk> getDisks() {
		if (disksCache == null) {
			disksCache = new ArrayList<>();
			List<IDisk> disks = computer.getDisks();
			for (IDisk disk : disks) disksCache.add(new AuthorityDisk(computer, disk, disk, null));
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

	@Override
	public int exec(String appId) {
		IMemory memory = this.getMemory();
		ProcessTree tree = memory.get(PROCESS);
		int id = tree.newProcess(appId);
		if (id == -1) throw new ComputerNewProcessException(computer, appId);
		return id;
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
		this.exec(boot);
	}

}
