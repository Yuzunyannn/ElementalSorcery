package yuzunyannn.elementalsorcery.computer.soft;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.api.computer.soft.APP;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.util.ISyncDetectable;
import yuzunyannn.elementalsorcery.api.util.ISyncWatcher;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.api.util.var.IVariableType;

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

		public APP appInst;

		protected ProcessNode(ResourceLocation appId, int pid, int parentPid) {
			this.appId = appId;
			this.pid = pid;
			this.parentPid = parentPid;
		}
	}

	protected int pidCounter;
	protected Map<Integer, ProcessNode> map = new HashMap<>();
	protected int foreground = 0;

	public void reset() {
		pidCounter = 0;
		foreground = 0;
		map.clear();
	}

	public int newProcess(String appId, int parentPid) {
		ProcessNode parentNode = map.get(parentPid);
		int newPid = -1;
		if (parentPid == 0) {
			if (parentNode != null) return -1;
			newPid = 0;
		} else {
			if (parentNode == null) return -1;
			newPid = pidCounter + 1;
		}

		ResourceLocation appIdRes = new ResourceLocation(appId);
		Class<?> appClazz = APP.REGISTRY.getValue(appIdRes);
		if (appClazz == null) return -1;

		addProcessNode(new ProcessNode(appIdRes, newPid, parentPid));
		return newPid;
	}

	protected void addProcessNode(ProcessNode node) {
		map.put(node.pid, node);
		// TODO child 链接
	}

	public void removeProcess(int pid) {
		map.remove(pid);
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

	public APP getAppCache(IOS os, int pid) {
		ProcessNode node = map.get(pid);
		if (node == null) return null;
		if (node.appInst == null) node.appInst = APP.REGISTRY.newInstance(node.appId, BUILD_TPTES, os, pid);
		return node.appInst;
	}

	protected NBTTagList serializeProcess() {
		NBTTagList list = new NBTTagList();
		for (ProcessNode node : map.values()) {
			NBTTagCompound dat = new NBTTagCompound();
			list.appendTag(dat);
			dat.setInteger("pid", node.pid);
			dat.setInteger("ppid", node.parentPid);
			dat.setString("appid", node.appId.toString());
		}
		return list;
	}

	protected void deserializeProcess(NBTTagList list) {
		this.map.clear();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound dat = list.getCompoundTagAt(i);
			int pid = dat.getInteger("pid");
			int parentPid = dat.getInteger("ppid");
			ResourceLocation appId = new ResourceLocation(dat.getString("appid"));
			addProcessNode(new ProcessNode(appId, pid, parentPid));
		}
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
		DetectDataset dataset = watcher.getOrCreateDetectObject(">process", DetectDataset.class,
				() -> new DetectDataset());

		NBTTagCompound changes = null;

		if (dataset.foreground != foreground) {
			if (changes == null) changes = new NBTTagCompound();
			changes.setInteger("fg", foreground);
			dataset.foreground = foreground;
		}

		boolean hasProcessChange = false;

		if (dataset.set.size() != map.size()) hasProcessChange = true;
		else {
			for (Integer i : dataset.set) {
				if (!map.containsKey(i)) {
					hasProcessChange = true;
					break;
				}
			}
		}

		// TODO 这里用更精准的检测，目前是全部发送
		if (hasProcessChange) {
			if (changes == null) changes = new NBTTagCompound();
			changes.setTag("ls", serializeProcess());
			dataset.set.clear();
			for (Integer i : map.keySet()) dataset.set.add(i);
		}

		return changes;
	}

	@Override
	public void mergeChanges(NBTTagCompound nbt) {
		if (nbt.hasKey("fg")) foreground = nbt.getInteger("fg");
		if (nbt.hasKey("ls")) deserializeProcess(nbt.getTagList("ls", NBTTag.TAG_COMPOUND));
	}

}
