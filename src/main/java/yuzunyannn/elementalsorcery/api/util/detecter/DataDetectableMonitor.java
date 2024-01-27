package yuzunyannn.elementalsorcery.api.util.detecter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class DataDetectableMonitor implements IDataDetectableMonitor {

	protected static class NodeA {
		int dirtyVer = 1;
		IDataDetectable unit;

		public NodeA(IDataDetectable unit) {
			this.unit = unit;
		}
	}

	protected static class NodeB {
		int dirtyVer = 0;
		Object obj;
	}

	protected static class DetectDataset {
		protected Map<String, NodeB> map = new HashMap<>();
	}

	protected Map<String, NodeA> map = new HashMap<>();
	public final String detectKey;

	public DataDetectableMonitor(String detectKey) {
		this.detectKey = detectKey;
	}

	@Override
	public void add(String key, IDataDetectable unit) {
		map.put(key, new NodeA(unit));
	}

	@Override
	public void remove(String key) {
		map.remove(key);
	}

	@Override
	public void markDirty(String key) {
		NodeA node = map.get(key);
		if (node != null) node.dirtyVer++;
	}

	public static final Supplier<DetectDataset> FACTORY = () -> new DetectDataset();

	@Override
	public NBTTagCompound detectChanges(ISyncWatcher watcher) {
		DetectDataset dataset = watcher.getOrCreateDetectObject(detectKey, DetectDataset.class, FACTORY);
		NBTTagCompound changes = null;
		Iterator<Entry<String, NodeA>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, NodeA> entry = iter.next();
			NodeA nodeA = entry.getValue();
			NodeB nodeB = dataset.map.get(entry.getKey());
			if (nodeB == null) dataset.map.put(entry.getKey(), nodeB = new NodeB());
			if (nodeA.dirtyVer == -1 || nodeB.dirtyVer != nodeA.dirtyVer) {
				nodeB.dirtyVer = nodeA.dirtyVer;
				NBTBase change = nodeA.unit.detectChanges(nodeB.obj);
				if (change != null) {
					nodeB.obj = nodeA.unit.copy();
					if (changes == null) changes = new NBTTagCompound();
					changes.setTag(entry.getKey(), change);
				}
			}
		}
		return changes;
	}

	@Override
	public void mergeChanges(NBTTagCompound nbt) {
		for (String key : nbt.getKeySet()) {
			NodeA node = map.get(key);
			if (node == null) continue;
			node.unit.mergeChanges(nbt.getTag(key));
		}
	}

}
