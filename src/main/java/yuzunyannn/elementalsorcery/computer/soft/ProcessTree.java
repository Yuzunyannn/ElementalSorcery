package yuzunyannn.elementalsorcery.computer.soft;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.computer.soft.App;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.api.util.detecter.ISyncDetectable;
import yuzunyannn.elementalsorcery.api.util.detecter.ISyncWatcher;
import yuzunyannn.elementalsorcery.api.util.var.IVariableType;
import yuzunyannn.elementalsorcery.util.TextHelper;

public class ProcessTree implements INBTSerializable<NBTTagCompound>, ISyncDetectable<NBTTagCompound> {

	public static class VTProcessMap implements IVariableType<ProcessTree> {

		@Override
		public ProcessTree newInstance(NBTBase base) {
			ProcessTree map = new ProcessTree();
			if (base != null) map.deserializeNBT((NBTTagCompound) base);
			return map;
		}

		@Override
		public NBTBase serializable(ProcessTree obj) {
			return obj.serializeNBT();
		}

	}

	public static class ProcessNode {
		public final ResourceLocation appId;
		public final int pid;
		public final int parentPid;
		public final List<Integer> children = new ArrayList<>();

		public App appInst;

		protected ProcessNode(ResourceLocation appId, int pid, int parentPid) {
			this.appId = appId;
			this.pid = pid;
			this.parentPid = parentPid;
		}
	}

	protected int pidCounter;
	protected Map<Integer, ProcessNode> map = new HashMap<>();
	protected int foreground = 0;
	public Runnable processChangeCallback;

	public void reset() {
		pidCounter = 0;
		foreground = 0;
		map.clear();
	}

	public int size() {
		return map.size();
	}

	public int newProcess(String appId, int parentPid) {
		ProcessNode parentNode = map.get(parentPid);
		int newPid = -1;
		if (parentPid == -1) {
			if (parentNode != null) return -1;
			newPid = 0;
		} else {
			if (parentNode == null) return -1;
			newPid = pidCounter;
		}

		ResourceLocation appIdRes = TextHelper.toESResourceLocation(appId);
		Class<?> appClazz = App.REGISTRY.getValue(appIdRes);
		if (appClazz == null) return -1;

		pidCounter = pidCounter + 1;

		if (map.containsKey(newPid)) {
			if (ESAPI.isDevelop) ESAPI.logger.error("repeat pid " + newPid + " why?");
			return -1;
		}

		ProcessNode node = new ProcessNode(appIdRes, newPid, parentPid);
		addProcessNode(node);
		afterProcessNodeAdded(node);

		return newPid;
	}

	protected void addProcessNode(ProcessNode node) {
		map.put(node.pid, node);
	}

	protected void afterProcessNodeAdded(ProcessNode node) {
		if (node.parentPid == -1) return;
		ProcessNode parentNode = map.get(node.parentPid);
		parentNode.children.add(node.pid);
	}

	public void removeProcess(int pid) {
		ProcessNode node = map.get(pid);
		if (node == null) return;
		map.remove(pid);
		ProcessNode parentNode = map.get(node.parentPid);
		if (parentNode != null) parentNode.children.remove(Integer.valueOf(pid));
		if (this.foreground == pid) this.foreground = 0;
	}

	public void setForeground(int pid) {
		this.foreground = pid;
	}

	public int getForeground() {
		return foreground;
	}

	public boolean hasProcess(int pid) {
		return map.containsKey(pid);
	}

	public int getParent(int pid) {
		ProcessNode node = map.get(pid);
		return node == null ? -1 : node.parentPid;
	}

	public Collection<Integer> getChildren(int pid) {
		ProcessNode node = map.get(pid);
		return node == null ? null : node.children;
	}

	public Collection<Integer> findAllChildren(int pid) {
		LinkedList<Integer> children = new LinkedList<>();
		findAllChildren(children, pid, 32);
		return children;
	}

	private void findAllChildren(LinkedList<Integer> children, int pid, int deep) {
		if (deep <= 0) throw new RuntimeException("children find over deep");
		ProcessNode node = map.get(pid);
		if (node == null) return;
		for (int i = node.children.size() - 1; i >= 0; i--) {
			int childPid = node.children.get(i);
			findAllChildren(children, childPid, deep--);
			children.add(childPid);
		}
	}

	public Iterator<Entry<Integer, ProcessNode>> getIterator() {
		return map.entrySet().iterator();
	}

	@Nullable
	public ResourceLocation getProcessId(int pid) {
		ProcessNode node = map.get(pid);
		if (node == null) return null;
		return node.appId;
	}

	public final static Class<?>[] BUILD_TPTES = new Class[] { IOS.class, int.class };

	public App getAppCache(IOS os, int pid) {
		ProcessNode node = map.get(pid);
		if (node == null) return null;
		if (node.appInst == null) node.appInst = App.REGISTRY.newInstance(node.appId, BUILD_TPTES, os, pid);
		return node.appInst;
	}

	protected NBTTagCompound serialize(ProcessNode node) {
		NBTTagCompound dat = new NBTTagCompound();
		dat.setInteger("pid", node.pid);
		dat.setInteger("ppid", node.parentPid);
		dat.setString("appid", node.appId.toString());
		return dat;
	}

	protected ProcessNode deserializeNode(NBTTagCompound dat) {
		int pid = dat.getInteger("pid");
		int parentPid = dat.getInteger("ppid");
		ResourceLocation appId = new ResourceLocation(dat.getString("appid"));
		return new ProcessNode(appId, pid, parentPid);
	}

	protected NBTTagList serializeProcess() {
		NBTTagList list = new NBTTagList();
		for (ProcessNode node : map.values()) list.appendTag(serialize(node));
		return list;
	}

	protected void deserializeProcess(NBTTagList list) {
		this.map.clear();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound dat = list.getCompoundTagAt(i);
			addProcessNode(deserializeNode(dat));
		}
		for (ProcessNode node : map.values()) afterProcessNodeAdded(node);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("pct", pidCounter);
		nbt.setInteger("fgid", foreground);
		nbt.setTag("ls", serializeProcess());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		pidCounter = nbt.getInteger("pct");
		foreground = nbt.getInteger("fgid");
		deserializeProcess(nbt.getTagList("ls", NBTTag.TAG_COMPOUND));
	}

	protected static class DetectDataset {
		protected int foreground = 0;
		protected Set<Integer> set = new HashSet<>();
	}

	@Override
	public NBTTagCompound detectChanges(ISyncWatcher watcher) {
		DetectDataset dataset = watcher.getOrCreateDetectObject(">process", DetectDataset.class, () -> new DetectDataset());

		NBTTagCompound changes = null;

		if (dataset.foreground != foreground) {
			if (changes == null) changes = new NBTTagCompound();
			changes.setInteger("fg", foreground);
			dataset.foreground = foreground;
		}

		NBTTagList nbtList = new NBTTagList();

		Iterator<Integer> iter = dataset.set.iterator();
		while (iter.hasNext()) {
			int i = iter.next();
			if (!map.containsKey(i)) {
				iter.remove();
				NBTTagCompound dat = new NBTTagCompound();
				dat.setInteger("dpid", i);
				nbtList.appendTag(dat);
			}
		}

		for (ProcessNode node : map.values()) {
			if (!dataset.set.contains(node.pid)) {
				dataset.set.add(node.pid);
				NBTTagCompound dat = serialize(node);
				nbtList.appendTag(dat);
			}
		}

		if (!nbtList.isEmpty()) {
			if (changes == null) changes = new NBTTagCompound();
			changes.setTag("ls", nbtList);
		}

		return changes;
	}

	@Override
	public void mergeChanges(NBTTagCompound nbt) {
		if (nbt.hasKey("fg")) foreground = nbt.getInteger("fg");
		if (nbt.hasKey("ls")) {
			NBTTagList nbtList = nbt.getTagList("ls", NBTTag.TAG_COMPOUND);
			List<ProcessNode> after = new LinkedList<>();
			for (int i = 0; i < nbtList.tagCount(); i++) {
				NBTTagCompound dat = nbtList.getCompoundTagAt(i);
				if (dat.hasKey("dpid")) {
					int pid = dat.getInteger("dpid");
					removeProcess(pid);
				} else {
					ProcessNode node = deserializeNode(dat);
					addProcessNode(node);
					after.add(node);
					pidCounter = Math.max(pidCounter, node.pid + 1);
				}
			}
			for (ProcessNode node : after) afterProcessNodeAdded(node);
			if (processChangeCallback != null) processChangeCallback.run();
		}
	}

}
