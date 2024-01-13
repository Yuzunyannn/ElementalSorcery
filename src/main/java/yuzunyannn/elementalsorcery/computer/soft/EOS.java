package yuzunyannn.elementalsorcery.computer.soft;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.IDisk;
import yuzunyannn.elementalsorcery.api.computer.soft.APP;
import yuzunyannn.elementalsorcery.api.computer.soft.AppDiskType;
import yuzunyannn.elementalsorcery.api.computer.soft.IComputerException;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.util.ISyncDetectable;
import yuzunyannn.elementalsorcery.api.util.ISyncWatcher;
import yuzunyannn.elementalsorcery.api.util.var.Variable;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.computer.exception.ComputerAppDamagedException;
import yuzunyannn.elementalsorcery.computer.exception.ComputerBootException;
import yuzunyannn.elementalsorcery.computer.exception.ComputerHardwareMissingException;
import yuzunyannn.elementalsorcery.computer.exception.ComputerNewProcessException;
import yuzunyannn.elementalsorcery.computer.exception.ComputerProcessNotExistException;
import yuzunyannn.elementalsorcery.computer.soft.ProcessTree.ProcessNode;
import yuzunyannn.elementalsorcery.util.detecter.SyncDetectableMonitor;

public abstract class EOS implements IOS {

	static public final Variable<String> BOOT = new Variable("~boot", VariableSet.STRING);
	static public final String PROCESS = "#PR";
	static public final String APP = "#APP";

	public final IComputer computer;
	public List<AuthorityDisk> disksCache = null;
	public Map<String, AuthorityAppDisk> appDiskCacheMap = new HashMap<>();

	protected SyncDetectableMonitor monitor = new SyncDetectableMonitor(">os");
	protected ProcessTree processTree = new ProcessTree();

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
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
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
		processTree.deserializeNBT(nbt.getCompoundTag(">pro"));
		each(app -> {
			String key = ">p|" + app.getPid();
			if (nbt.hasKey(key)) app.deserializeNBT(nbt.getCompoundTag(key));
		});
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

	@Override
	public int exec(APP parent, String appId) {
		int id = processTree.newProcess(appId, parent.getPid());
		if (id == -1) throw new ComputerNewProcessException(computer, appId);
		monitor.markDirty(PROCESS);
		onAppStartup(processTree, processTree.getAppCache(this, id));
		return id;
	}

	@Override
	public int setForeground(int pid) {
		if (!processTree.hasProcess(pid)) return -1;
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

		int id = processTree.newProcess(boot, 0);
		if (id == -1) throw new ComputerBootException(computer, "root process fail");

		monitor.markDirty(PROCESS);
		onAppStartup(processTree, processTree.getAppCache(this, id));
	}

	@Override
	public void abort(int pid, IComputerException e) {
		if (!processTree.hasProcess(pid)) return;
		onAbort(processTree, pid, e);
		processTree.removeProcess(pid);
		monitor.markDirty(PROCESS);
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

	protected void onAppExit(ProcessTree tree, APP app) {
		onRemoveApp(tree, app.getPid());
	}

	protected void onAbort(ProcessTree tree, int pid, IComputerException e) {
		onRemoveApp(tree, pid);
	}

	protected void onRemoveApp(ProcessTree tree, int pid) {
		int parentPid = tree.getParent(pid);
		if (parentPid == -1) parentPid = 0;
		tree.setForeground(parentPid);
	}

	@Override
	public void onDiskChange() {
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

}
