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
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.computer.DNParams;
import yuzunyannn.elementalsorcery.api.computer.DNResult;
import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceLinker;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.IDisk;
import yuzunyannn.elementalsorcery.api.computer.soft.APP;
import yuzunyannn.elementalsorcery.api.computer.soft.AppDiskType;
import yuzunyannn.elementalsorcery.api.computer.soft.IComputerException;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.util.detecter.ISyncDetectable;
import yuzunyannn.elementalsorcery.api.util.detecter.ISyncWatcher;
import yuzunyannn.elementalsorcery.api.util.detecter.SyncDetectableMonitor;
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
		});

//		new RuntimeException("?? os:" + this).printStackTrace();
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
	public IDeviceStorage getDisk(APP app, AppDiskType type) {
		String appId = app.getAppId().toString();
		String key = appId + "_" + type.key;
		if (appDiskCacheMap.containsKey(key)) return appDiskCacheMap.get(key);
		List<IDisk> list = getDisks();
		if (list.isEmpty()) throw new ComputerHardwareMissingException(this.computer, "disk is missing");
		AuthorityAppDisk disk = new AuthorityAppDisk(computer, appId, list, type);
		appDiskCacheMap.put(key, disk);
		return disk;
	}

	@Override
	public int exec(APP parent, String appId) {
		if (processTree.map.size() > 8)
			throw new ComputerNewProcessException(computer, "new process " + appId + " over max limit");
		int id = processTree.newProcess(appId, parent.getPid());
		if (id == -1) throw new ComputerNewProcessException(computer, appId);
		monitor.markDirty(PROCESS);
		onAppStartup(processTree, processTree.getAppCache(this, id));
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
	public void message(APP app, NBTTagCompound nbt) {
		DNParams params = new DNParams();
		params.set("pid", app.getPid());
		params.set("data", nbt);
		computer.notice("app-message", params);
	}

	protected void exit(int pid) {
		if (!processTree.hasProcess(pid)) return;
		remove(processTree, pid, app -> app.onExit());
	}

	@Override
	public int setForeground(int pid) {
		if (!processTree.hasProcess(pid)) return -1;
		APP app = processTree.getAppCache(this, pid);
		if (app == null || app.isTask()) return -1;
		processTree.setForeground(pid);
		monitor.markDirty(PROCESS);
		return pid;
	}

	@Override
	public int getForeground() {
		return processTree.getForeground();
	}

	@Override
	public APP getAppInst(int pid) {
		if (!processTree.hasProcess(pid))
			throw new ComputerProcessNotExistException(this.computer, String.valueOf(pid));
		APP app = processTree.getAppCache(this, pid);
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
		if (boot == null) throw new ComputerBootException(computer, "cannot find boot");

		clearAll();
		isRunning = true;

		int id = processTree.newProcess(boot, -1);
		if (id == -1) throw new ComputerBootException(computer, "root process fail");

		monitor.markDirty(PROCESS);
		onAppStartup(processTree, processTree.getAppCache(this, id));
	}

	@Override
	public void onClosing() {
		clearAll();
	}

	@Override
	public void onUpdate() {
		each(app -> app.onUpdate());
	}

	protected void single(int pid, Consumer<APP> func) {
		try {
			func.accept(processTree.getAppCache(this, pid));
		} catch (Exception e) {
			if (pid == 0) throw e;
			if (e instanceof IComputerException) abort(pid, (IComputerException) e);
			else throw e;
		}
	}

	protected void each(Consumer<APP> func) {
		Iterator<Entry<Integer, ProcessNode>> iter = processTree.getIterator();
		List<Entry<Integer, IComputerException>> abortList = new LinkedList<>();
		while (iter.hasNext()) {
			Entry<Integer, ProcessNode> entry = iter.next();
			try {
				func.accept(processTree.getAppCache(this, entry.getKey()));
			} catch (Exception e) {
				if (entry.getKey() == 0) throw e;
				if (e instanceof IComputerException)
					abortList.add(new AbstractMap.SimpleEntry(entry.getKey(), (IComputerException) e));
				else throw e;
			}
		}
		for (Entry<Integer, IComputerException> entry : abortList) abort(entry.getKey(), entry.getValue());
	}

	protected void onAppStartup(ProcessTree tree, APP app) {
		app.onStartup();
	}

	protected void remove(ProcessTree tree, int rpid, Consumer<APP> func) {
		Collection<Integer> children = tree.findAllChildren(rpid);
		for (Integer pid : children) {
			try {
				func.accept(tree.getAppCache(this, pid));
			} catch (Exception e) {}
			onRemoveApp(tree, pid);
			tree.removeProcess(pid);
		}
		onRemoveApp(tree, rpid);
		tree.removeProcess(rpid);
		monitor.markDirty(PROCESS);
	}

	protected void onRemoveApp(ProcessTree tree, int pid) {
		int parentPid = tree.getParent(pid);
		if (parentPid == -1) parentPid = 0;
		tree.setForeground(parentPid);
	}

	@Override
	public void onDiskChange(boolean onlyData) {
		each(app -> app.onDiskChange());
		if (onlyData) return;

		disksCache = null;
		appDiskCacheMap.clear();
	}

	@Override
	public void markDirty(APP app) {
		this.monitor.markDirty(APP);
	}

	@Override
	public NBTTagCompound detectChanges(ISyncWatcher watcher) {
		return monitor.detectChanges(watcher);
	}

	@Override
	public void mergeChanges(NBTTagCompound nbt) {
		monitor.mergeChanges(nbt);
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public List<UUID> filterLinkedDevice(String ability) {
		List<UUID> finded = new ArrayList<>();
		Collection<IDeviceLinker> linkers = computer.getNetwork().getLinkers();
		for (IDeviceLinker linker : linkers) {
			IDevice device = linker.getRemoteDevice();
			if (device == null) continue;
			if (device.hasAbility(ability)) finded.add(linker.getRemoteUUID());
		}
		return finded;
	}

	@Override
	public CompletableFuture<DNResult> notice(UUID uuid, String method, DNParams params) {
		IDeviceLinker linker = computer.getNetwork().getLinker(uuid);
		if (linker == null) return DNResult.invalid();
		IDevice device = linker.getRemoteDevice();
		if (device == null) return DNResult.unavailable();
		return device.notice(method, params);
	}

}
