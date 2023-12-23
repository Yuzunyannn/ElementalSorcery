package yuzunyannn.elementalsorcery.computer;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.IStorageMonitor;
import yuzunyannn.elementalsorcery.api.computer.StoragePath;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class StorageMonitor implements IStorageMonitor, INBTSerializable<NBTTagCompound> {

	protected static class Node {
		final String key;
		final Map<String, Node> children = new HashMap<>();
		final StoragePath currPath;
		Node parent;

		int modifyVer = 1;
		int dirtyVer = 1;

		public StoragePath getPath() {
			return currPath;
		}

		Node(StoragePath currPath, Node parent) {
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

	protected Map<String, Node> nodeTree = new HashMap<>();
	protected Map<StoragePath, Node> pathMap = new HashMap<>();

	@Override
	public void add(StoragePath path) {
		if (path.isEmpty()) return;
		if (pathMap.containsKey(path)) return;
		Node parentNode = null;
		Map<String, Node> nodeTree = this.nodeTree;
		for (int i = 0; i < path.length(); i++) {
			String currKey = path.get(i);
			StoragePath currPath = path.sub(i);
			Node node = nodeTree.get(currKey);
			if (node == null) nodeTree.put(currKey, node = new Node(currPath, parentNode));
			parentNode = node;
			nodeTree = node.children;
		}
		pathMap.put(path, parentNode);
	}

	@Override
	public void remove(StoragePath path) {
		Node node = pathMap.get(path);
		if (node == null) return;
		if (node.parent != null) node.parent.children.remove(node.key);
		pathMap.remove(path);
	}

	public void clear() {
		nodeTree.clear();
		pathMap.clear();
	}

	@Override
	public void markDirty(StoragePath path) {
		Node node = pathMap.get(path);
		if (node == null) return;
		node.modifyVer++;
		node.dirtyVer++;
		while (node.parent != null) {
			node = node.parent;
			node.dirtyVer++;
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for (StoragePath path : pathMap.keySet()) list.appendTag(NBTHelper.serializeStrings(path.toStrings()));
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
			this.add(StoragePath.of(strs));
		}
	}

	public NBTTagCompound detectChanges(DetectStorageDataset dataset, IDeviceStorage storage) {
		if (dataset == null) return null;

		Detector detector = Detector.instance;
		detector.reset(storage);

		for (String str : dataset.children.keySet()) {
			if (!nodeTree.containsKey(str)) {
				Node currDatasetNode = dataset.children.get(str);
				dataset.children.remove(str);
				detector.onChange(currDatasetNode);
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
			if (nbt.hasKey("sync", NBTTag.TAG_LIST)) return nbt.getTagList("rm", NBTTag.TAG_COMPOUND);
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

	public void mergeChanges(NBTTagCompound changesNBT, IDeviceStorage storage) {
		NBTTagList list = changesNBT.getTagList("rm", NBTTag.TAG_LIST);
		for (int i = 0; i < list.tagCount(); i++)
			storage.remove(NBTHelper.deserializeStrings((NBTTagList) list.get(i)));
		list = changesNBT.getTagList("sync", NBTTag.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound data = list.getCompoundTagAt(i);
			storage.set(NBTHelper.deserializeStrings(data.getTagList("k", NBTTag.TAG_STRING)), data.getTag("v"));
		}
	}

}
