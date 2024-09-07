package yuzunyannn.elementalsorcery.computer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.DeviceFilePath;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

@Deprecated
public class StorageMonitor implements IStorageMonitor, INBTSerializable<NBTTagCompound> {

	protected static class Node {
		final String key;
		final Map<String, Node> children = new HashMap<>();
		final DeviceFilePath currPath;
		Node parent;

		int modifyVer = 1;
		int dirtyVer = 1;

		public DeviceFilePath getPath() {
			return currPath;
		}

		Node(DeviceFilePath currPath, Node parent) {
			this.key = currPath.get(currPath.length() - 1);
			this.currPath = currPath;
			this.parent = parent;
		}

		Node copy(Node parent, boolean full) {
			Node node = new Node(this.currPath, parent);
			if (full) {
				node.modifyVer = this.modifyVer;
				node.dirtyVer = this.dirtyVer;
			}
			for (Node childrenNode : this.children.values()) {
				Node copyChildrenNode = childrenNode.copy(node, full);
				node.children.put(copyChildrenNode.key, copyChildrenNode);
			}
			return node;
		}
	}

	static public class DetectStorageDataset {

		protected Map<String, Node> children = new HashMap<>();

		Node structure(Node node, Node detectNodeParent) {
			Node myNode = node.copy(detectNodeParent, true);
			Map<String, Node> children = detectNodeParent == null ? this.children : detectNodeParent.children;
			children.put(myNode.key, myNode);
			return myNode;
		}
	}

	protected Map<String, Node> nodeTree = new HashMap<>();
	protected Map<DeviceFilePath, Node> pathMap = new HashMap<>();

	@Override
	public void add(DeviceFilePath path) {
		if (path.isEmpty()) return;
		if (pathMap.containsKey(path)) return;
		Node parentNode = null;
		Map<String, Node> nodeTree = this.nodeTree;
		for (int i = 0; i < path.length(); i++) {
			String currKey = path.get(i);
			DeviceFilePath currPath = path.sub(i);
			Node node = nodeTree.get(currKey);
			if (node == null) nodeTree.put(currKey, node = new Node(currPath, parentNode));
			parentNode = node;
			nodeTree = node.children;
		}
		pathMap.put(path, parentNode);
	}

	@Override
	public void remove(DeviceFilePath path) {
		Node node = pathMap.get(path);
		if (node == null) return;
		if (node.parent != null) node.parent.children.remove(node.key);
		pathMap.remove(path);
	}

	@Override
	public void markDirty(DeviceFilePath path) {
		Node node = pathMap.get(path);
		if (node == null) return;
		node.modifyVer++;
		node.dirtyVer++;
		while (node.parent != null) {
			node = node.parent;
			node.dirtyVer++;
		}
	}

	public void clear() {
		nodeTree.clear();
		pathMap.clear();
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for (DeviceFilePath path : pathMap.keySet()) list.appendTag(NBTHelper.serializeStrings(path.toStrings()));
		nbt.setTag("pts", list);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.clear();
		NBTTagList list = nbt.getTagList("pts", NBTTag.TAG_LIST);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagList strList = (NBTTagList) list.get(i);
			String[] strs = NBTHelper.deserializeStrings(strList);
			this.add(DeviceFilePath.of(strs));
		}
	}

	public NBTTagCompound detectChanges(DetectStorageDataset dataset, IDeviceStorage storage) {
		if (dataset == null) return null;

		Detector detector = Detector.instance;
		detector.reset(storage);

		Iterator<Entry<String, Node>> iter = dataset.children.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Node> entry = iter.next();
			String str = entry.getKey();
			if (!nodeTree.containsKey(str)) {
				detector.onChange(entry.getValue());
				iter.remove();
			}
		}

		for (Node node : nodeTree.values()) {
			Node currNode = node;
			Node currDatasetNode = dataset.children.get(currNode.key);
			detectChanges(detector, dataset, currNode, currDatasetNode, null);
		}

		return detector.build();
	}

	protected void detectChanges(Detector detector, DetectStorageDataset dataset, Node currNode, Node currDatasetNode,
			Node parentDatasetNode) {

		if (currDatasetNode == null) {
			currDatasetNode = dataset.structure(currNode, parentDatasetNode);
			detector.onChange(currNode);
			return;
		}

		if (currDatasetNode.dirtyVer == currNode.dirtyVer) return;

		if (currNode.modifyVer != currDatasetNode.modifyVer) {
			currDatasetNode = dataset.structure(currNode, parentDatasetNode);
			detector.onChange(currNode);
			return;
		}

		currDatasetNode.dirtyVer = currNode.dirtyVer;
		for (Node childNode : currNode.children.values())
			detectChanges(detector, dataset, childNode, currDatasetNode.children.get(childNode.key), currDatasetNode);
	}

	protected static class Detector {
		static final Detector instance = new Detector();

		IDeviceStorage storage;
		NBTTagCompound nbt;

		public void reset(IDeviceStorage storage) {
			this.storage = storage;
			this.nbt = null;
		}

		NBTTagCompound build() {
			return nbt;
		}

		protected NBTTagCompound getOrCreateNBT() {
			if (nbt == null) nbt = new NBTTagCompound();
			return nbt;
		}

		protected NBTTagList getOrCreateRemoveList() {
			NBTTagCompound nbt = getOrCreateNBT();
			if (nbt.hasKey("rm", NBTTag.TAG_LIST)) return nbt.getTagList("rm", NBTTag.TAG_LIST);
			NBTTagList list = new NBTTagList();
			nbt.setTag("rm", list);
			return list;
		}

		protected NBTTagList getOrCreateSyncList() {
			NBTTagCompound nbt = getOrCreateNBT();
			if (nbt.hasKey("sync", NBTTag.TAG_LIST)) return nbt.getTagList("sync", NBTTag.TAG_COMPOUND);
			NBTTagList list = new NBTTagList();
			nbt.setTag("sync", list);
			return list;
		}

		public void onChange(Node node) {
			String[] paths = node.getPath().toStrings();
			NBTBase base = storage.get(paths);
			if (base == null) {
				NBTTagList list = getOrCreateRemoveList();
				list.appendTag(NBTHelper.serializeStrings(paths));
			} else {
				NBTTagList list = getOrCreateSyncList();
				NBTTagCompound data = new NBTTagCompound();
				list.appendTag(data);
				data.setTag("k", NBTHelper.serializeStrings(paths));
				data.setTag("v", base);
			}
		}

	}

	public List<String[]> mergeChanges(NBTTagCompound changesNBT, IDeviceStorage storage) {
		NBTTagList rmList = changesNBT.getTagList("rm", NBTTag.TAG_LIST);
		NBTTagList syncList = changesNBT.getTagList("sync", NBTTag.TAG_COMPOUND);
		List<String[]> changeList = new ArrayList<>(rmList.tagCount() + syncList.tagCount() + 1);

		for (int i = 0; i < rmList.tagCount(); i++) {
			String[] path = NBTHelper.deserializeStrings((NBTTagList) rmList.get(i));
			changeList.add(path);
			storage.remove(path);
		}

		for (int i = 0; i < syncList.tagCount(); i++) {
			NBTTagCompound data = syncList.getCompoundTagAt(i);
			String[] path = NBTHelper.deserializeStrings(data.getTagList("k", NBTTag.TAG_STRING));
			changeList.add(path);
			storage.set(path, data.getTag("v"));
		}

		return changeList;
	}

}
