package yuzunyannn.elementalsorcery.api.util.detecter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class SyncDetectableMonitor implements ISyncDetectableMonitor {

	public final String detectKey;

	public SyncDetectableMonitor(String key) {
		this.detectKey = key;
	}

	protected static class Node {
		int dirtyVer = 1;
		ISyncDetectable detectable;

		public Node(ISyncDetectable detectable) {
			this.detectable = detectable;
		}
	}

	protected static class DetectDataset {
		protected Map<String, Integer> map = new HashMap<>();
	}

	protected Map<String, Node> map = new HashMap<>();

	@Override
	public void add(String key, ISyncDetectable<?> detectable) {
		add(key, detectable, false);
	}

	public void add(String key, ISyncDetectable<?> detectable, boolean always) {
		Node node = new Node(detectable);
		if (always) node.dirtyVer = -1;
		map.put(key, node);
	}

	@Override
	public void remove(String key) {
		map.remove(key);
	}

	@Override
	public void markDirty(String key) {
		Node node = map.get(key);
		if (node != null) node.dirtyVer++;
	}

	@Override
	public NBTTagCompound detectChanges(ISyncWatcher watcher) {
		DetectDataset dataset = watcher.getDetectObject(detectKey, DetectDataset.class);
		if (dataset == null) watcher.setDetectObject(detectKey, dataset = new DetectDataset());
		NBTTagCompound changes = null;
		Iterator<Entry<String, Node>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Node> entry = iter.next();
			Node node = entry.getValue();
			if (node.dirtyVer == -1);
			else {
				Integer ver = dataset.map.get(entry.getKey());
				if (ver == null) ver = 0;
				if (ver == node.dirtyVer) continue;
				dataset.map.put(entry.getKey(), node.dirtyVer);
			}
			NBTBase subChanges = node.detectable.detectChanges(watcher);
			if (subChanges != null) {
				if (changes == null) changes = new NBTTagCompound();
				changes.setTag(entry.getKey(), subChanges);
			}
		}
		return changes;
	}

	@Override
	public void mergeChanges(NBTTagCompound nbt) {
		for (String key : nbt.getKeySet()) {
			Node node = map.get(key);
			if (node == null) continue;
			node.detectable.mergeChanges(nbt.getTag(key));
		}
	}

}
