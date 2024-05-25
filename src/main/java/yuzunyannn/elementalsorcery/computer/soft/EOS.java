package yuzunyannn.elementalsorcery.computer.soft;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.Consumer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.computer.DNRequest;
import yuzunyannn.elementalsorcery.api.computer.DNResult;
import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceEnv;
import yuzunyannn.elementalsorcery.api.computer.IDeviceInfo;
import yuzunyannn.elementalsorcery.api.computer.IDeviceLinker;
import yuzunyannn.elementalsorcery.api.computer.IDeviceNetwork;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.IDisk;
import yuzunyannn.elementalsorcery.api.computer.soft.App;
import yuzunyannn.elementalsorcery.api.computer.soft.AppDiskType;
import yuzunyannn.elementalsorcery.api.computer.soft.IComputerException;
import yuzunyannn.elementalsorcery.api.computer.soft.IDeviceShellExecutor;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.util.detecter.ISyncDetectable;
import yuzunyannn.elementalsorcery.api.util.detecter.ISyncWatcher;
import yuzunyannn.elementalsorcery.api.util.detecter.SyncDetectableMonitor;
import yuzunyannn.elementalsorcery.api.util.target.IObjectGetter;
import yuzunyannn.elementalsorcery.api.util.var.Variable;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.computer.exception.ComputerAppDamagedException;
import yuzunyannn.elementalsorcery.computer.exception.ComputerBootException;
import yuzunyannn.elementalsorcery.computer.exception.ComputerNewProcessException;
import yuzunyannn.elementalsorcery.computer.exception.ComputerProcessNotExistException;
import yuzunyannn.elementalsorcery.computer.soft.ProcessTree.ProcessNode;

public abstract class EOS implements IOS {

	static public final Variable<String> BOOT = new Variable("~boot", VariableSet.STRING);
	static public final String PROCESS = "#PR";
	static public final String APP = "#APP";

	public final IComputer computer;
	public List<AuthorityDisk> disksCache = null;
	public Map<String, AuthorityAppDisk> appDiskCacheMap = new HashMap<>();

	protected SyncDetectableMonitor monitor = new SyncDetectableMonitor(">os");
	protected ProcessTree processTree = new ProcessTree();
	protected boolean isRunning = false;

	public EOS(IComputer computer) {
		this.computer = computer;
		this.monitor.add(PROCESS, processTree);
		this.monitor.add(APP, new ISyncDetectable<NBTTagCompound>() {
			@Override
			public void mergeChanges(NBTTagCompound nbt) {
				for (String key : nbt.getKeySet()) {
					Integer pid = null;
					try {
						pid = Integer.parseInt(key);
					} catch (Exception e) {}
					if (pid == null) continue;
					final NBTTagCompound dat = nbt.getCompoundTag(key);
					single(pid, app -> app.mergeChanges(dat));
				}
			}

			@Override
			public NBTTagCompound detectChanges(ISyncWatcher watcher) {
				NBTTagCompound appChanges = new NBTTagCompound();
				each(app -> {
					AuthorityWatcher appWacher = new AuthorityWatcher(watcher, ">pid" + app.getPid() + "|");
					NBTTagCompound thisChanges = app.detectChanges(appWacher);
					if (thisChanges != null) appChanges.setTag(String.valueOf(app.getPid()), thisChanges);
				});
				return appChanges.isEmpty() ? null : appChanges;
			}
		}, true);

//		new RuntimeException("?? os:" + this).printStackTrace();
	}

	@Override
	public UUID getDeviceUUID() {
		return computer.device().getUDID();
	}

	@Override
	public IDeviceInfo getDeviceInfo() {
		return computer.device().getInfo();
	}

	@Override
	public IDeviceShellExecutor createShellExecutor() {
		return new DeviceShellExecutor(computer.device());
	}

	public void markDirty() {
		IDeviceEnv env = computer.getEnv();
		if (env != null) env.markDirty();
	}

	@Override
	public void markDirty(App app) {
		IDeviceEnv env = computer.getEnv();
		if (env != null) env.markDirty();
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		if (!isRunning) return nbt;
		nbt.setTag(">pro", processTree.serializeNBT());
		each(app -> {
			NBTTagCompound dat = app.serializeNBT();
			if (dat == null || dat.isEmpty()) return;
			nbt.setTag(">p|" + app.getPid(), dat);
		});
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (!nbt.hasKey(">pro")) {
			clearAll();
			return;
		}
		isRunning = true;
		processTree.deserializeNBT(nbt.getCompoundTag(">pro"));
		each(app -> {
			String key = ">p|" + app.getPid();
			if (nbt.hasKey(key)) app.deserializeNBT(nbt.getCompoundTag(key));
		});
	}

	protected void clearAll() {
		isRunning = false;
		processTree.reset();
		appDiskCacheMap.clear();
		disksCache = null;
	}

	@Override
	public List<IDisk> getDisks() {
		if (disksCache == null) {
			disksCache = new ArrayList<>();
			List<IDisk> disks = computer.getDisks();
			for (IDisk disk : disks) disksCache.add(new AuthorityDisk(computer, disk, null));
		}
		return (List<IDisk>) ((Object) disksCache);
	}

	@Override
	public IDeviceStorage getDisk(App app, AppDiskType type) {
		String appId = app.getAppId().toString();
		String key = appId + "_" + type.key;
		if (appDiskCacheMap.containsKey(key)) return appDiskCacheMap.get(key);
		List<IDisk> list = getDisks();
		if (list.isEmpty()) return null;
		AuthorityAppDisk disk = new AuthorityAppDisk(computer, appId, list, type);
		appDiskCacheMap.put(key, disk);
		return disk;
	}

	@Override
	public int exec(App parent, String appId) {
		if (processTree.map.size() > 8)
			throw new ComputerNewProcessException(computer, "new process " + appId + " over max limit");
		int id = processTree.newProcess(appId, parent.getPid());
		if (id == -1) throw new ComputerNewProcessException(computer, appId);
		monitor.markDirty(PROCESS);
		onAppStartup(processTree, processTree.getAppCache(this, id));
		markDirty();
		return id;
	}

	@Override
	public void abort(int pid, IComputerException e) {
		if (!processTree.hasProcess(pid)) return;
		remove(processTree, pid, app -> app.onAbort());
		if (ESAPI.isDevelop) ESAPI.logger.warn("computer abort: " + pid);
		if (pid == 0) throw ((RuntimeException) e);
	}

	@Override
	public void message(App app, NBTTagCompound nbt) {
		DNRequest params = new DNRequest();
		params.set("pid", app.getPid());
		params.set("data", nbt);
		computer.notice("app-message", params);
	}

	@Override
	public boolean exit(int pid) {
		if (!processTree.hasProcess(pid)) return false;
		remove(processTree, pid, app -> app.onExit());
		return true;
	}

	@Override
	public int setForeground(int pid) {
		if (!processTree.hasProcess(pid)) return -1;
		App app = processTree.getAppCache(this, pid);
		if (app == null || app.isTask()) return -1;
		processTree.setForeground(pid);
		monitor.markDirty(PROCESS);
		markDirty();
		return pid;
	}

	@Override
	public int getForeground() {
		return processTree.getForeground();
	}

	@Override
	public App getAppInst(int pid) {
		if (!processTree.hasProcess(pid))
			throw new ComputerProcessNotExistException(this.computer, String.valueOf(pid));
		App app = processTree.getAppCache(this, pid);
		if (app == null)
			throw new ComputerAppDamagedException(this.computer, String.valueOf(processTree.getProcessId(pid)));
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
		if (boot == null) throw new ComputerBootException(computer, "es.app.err.boot.cannotFind");

		clearAll();
		isRunning = true;

		int id = processTree.newProcess(boot, -1);
		if (id == -1) throw new ComputerBootException(computer, "es.app.err.boot.fail", String.valueOf(boot));

		monitor.markDirty(PROCESS);
		onAppStartup(processTree, processTree.getAppCache(this, id));
		markDirty();
	}

	@Override
	public void onClosing() {
		try {
			exit(0);
		} catch (Exception e) {
			if (e instanceof IComputerException);
			else ESAPI.logger.warn("close error", e);
		}
		clearAll();
		markDirty();
	}

	@Override
	public void onUpdate() {
		each(app -> app.onUpdate());
	}

	protected void single(int pid, Consumer<App> func) {
		try {
			func.accept(processTree.getAppCache(this, pid));
		} catch (Exception e) {
			if (pid == 0) throw e;
			if (e instanceof IComputerException) abort(pid, (IComputerException) e);
			else throw e;
		}
	}

	protected void each(Consumer<App> func) {
		Iterator<Entry<Integer, ProcessNode>> iter = processTree.getIterator();
		List<Entry<Integer, IComputerException>> abortList = new LinkedList<>();
		List<Integer> closingList = new LinkedList<>();
		while (iter.hasNext()) {
			Entry<Integer, ProcessNode> entry = iter.next();
			try {
				App app = processTree.getAppCache(this, entry.getKey());
				if (app.isClosing()) {
					closingList.add(app.getPid());
					continue;
				}
				func.accept(app);
			} catch (Exception e) {
				if (entry.getKey() == 0) throw e;
				if (e instanceof IComputerException)
					abortList.add(new AbstractMap.SimpleEntry(entry.getKey(), (IComputerException) e));
				else throw e;
			}
		}
		for (Entry<Integer, IComputerException> entry : abortList) abort(entry.getKey(), entry.getValue());
		for (Integer pid : closingList) exit(pid);
	}

	protected void onAppStartup(ProcessTree tree, App app) {
		app.onStartup();
	}

	protected void remove(ProcessTree tree, int rpid, Consumer<App> func) {
		Collection<Integer> children = tree.findAllChildren(rpid);
		for (Integer pid : children) {
			try {
				func.accept(tree.getAppCache(this, pid));
			} catch (Exception e) {}
			onRemoveApp(tree, pid);
			tree.removeProcess(pid);
		}
		try {
			func.accept(tree.getAppCache(this, rpid));
		} catch (Exception e) {}
		onRemoveApp(tree, rpid);
		tree.removeProcess(rpid);
		monitor.markDirty(PROCESS);
	}

	protected void onRemoveApp(ProcessTree tree, int pid) {
		int parentPid = tree.getParent(pid);
		if (parentPid == -1) parentPid = 0;
		if (tree.getForeground() == pid) tree.setForeground(parentPid);
		markDirty();
	}

	@Override
	public void onDiskChange(boolean onlyData) {
		each(app -> app.onDiskChange());
		if (onlyData) return;

		disksCache = null;
		appDiskCacheMap.clear();
	}

	@Override
	public NBTTagCompound detectChanges(ISyncWatcher watcher) {
		return monitor.detectChanges(watcher);
	}

	@Override
	public void mergeChanges(NBTTagCompound nbt) {
		this.isRunning = computer.isPowerOn();
		monitor.mergeChanges(nbt);
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public List<UUID> filterLinkedDevice(Capability<?> capability, Object key) {
		List<UUID> finded = new ArrayList<>();
		Collection<IDeviceLinker> linkers = computer.device().getNetwork().getLinkers();
		for (IDeviceLinker linker : linkers) {
			IDevice device = linker.getRemoteDevice();
			if (device == null) continue;
			if (device.hasCapability(capability, null)) finded.add(linker.getRemoteUUID());
		}
		return finded;
	}

	@Override
	public DNResult notice(UUID udid, String method, DNRequest params) {
		if (udid == null) {
			params.setSrcDevice(computer.device());
			return computer.notice(method, params);
		}
		IDeviceNetwork network = computer.device().getNetwork();
		IDeviceLinker linker = network.getLinker(udid);
		if (linker == null) return DNResult.invalid();
		params.setSrcDevice(computer.device());
		if (!linker.isConnecting()) return DNResult.unavailable();
		return linker.getRemoteDevice().notice(method, params);
	}

	@Override
	public <T> IObjectGetter<T> askCapability(UUID udid, Capability<T> capability, Object key) {
		if (udid == null) udid = getDeviceUUID();
		IDeviceNetwork network = computer.device().getNetwork();
		IDeviceLinker linker = network.getLinker(udid);
		if (linker == null) return IObjectGetter.EMPTY;
		return new CapabilityGetter(linker, capability, key);
	}

}
