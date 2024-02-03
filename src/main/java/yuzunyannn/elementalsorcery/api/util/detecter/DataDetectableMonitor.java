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

	protected static class NodeB implements IDataRef {
		int dirtyVer = 0;
		Object obj;

		@Override
		public Object get() {
			return obj;
		}

		@Override
		public void set(Object t) {
			this.obj = t;
		}
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
		add(key, unit, false);
	}

	public void add(String key, IDataDetectable unit, boolean always) {
		NodeA a = new NodeA(unit);
		map.put(key, a);
		if (always) a.dirtyVer = -1;
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
				NBTBase change = nodeA.unit.detectChanges(nodeB);
				if (change != null) {
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
