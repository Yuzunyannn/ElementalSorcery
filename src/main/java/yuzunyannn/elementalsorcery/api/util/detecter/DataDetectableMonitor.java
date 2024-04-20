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

		public boolean needCheck(DataDetectableMonitor self, NodeB nodeB) {
			return true;
		}
	}

	protected static class NodeADirty extends NodeA {

		public NodeADirty(IDataDetectable unit) {
			super(unit);
		}

		public boolean needCheck(DataDetectableMonitor self, NodeB nodeB) {
			return nodeB.dirtyVer != this.dirtyVer;
		}
	}

	protected static class NodeATick extends NodeA {

		public final int dTick;

		public NodeATick(IDataDetectable unit, int tick) {
			super(unit);
			this.dTick = tick;
		}

		public boolean needCheck(DataDetectableMonitor self, NodeB nodeB) {
			return self.tick % this.dTick == 0 || nodeB.dirtyVer != this.dirtyVer;
		}
	}

	protected static final class NodeB implements IDataRef {
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
	protected int tick;
	public final String detectKey;

	public DataDetectableMonitor(String detectKey) {
		this.detectKey = detectKey;
	}

	@Override
	public void add(String key, IDataDetectable unit) {
		add(key, unit, false);
	}

	public void add(String key, IDataDetectable unit, boolean always) {
		if (always) map.put(key, new NodeA(unit));
		else map.put(key, new NodeADirty(unit));
	}

	public void add(String key, IDataDetectable unit, int interval) {
		if (interval > 0) map.put(key, new NodeATick(unit, interval));
		else add(key, unit, true);
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
		tick++;
		DetectDataset dataset = watcher.getOrCreateDetectObject(detectKey, DetectDataset.class, FACTORY);
		NBTTagCompound changes = null;
		Iterator<Entry<String, NodeA>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, NodeA> entry = iter.next();
			NodeA nodeA = entry.getValue();
			NodeB nodeB = dataset.map.get(entry.getKey());
			if (nodeB == null) dataset.map.put(entry.getKey(), nodeB = new NodeB());
			if (nodeA.needCheck(this, nodeB)) {
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
