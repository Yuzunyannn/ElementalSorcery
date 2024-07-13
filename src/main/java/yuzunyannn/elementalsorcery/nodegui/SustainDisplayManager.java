package yuzunyannn.elementalsorcery.nodegui;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.api.util.ICastable;
import yuzunyannn.elementalsorcery.api.util.detecter.IDataDetectable;
import yuzunyannn.elementalsorcery.api.util.detecter.IDataRef;
import yuzunyannn.elementalsorcery.logics.EventServer;

public class SustainDisplayManager {

	protected IdentityHashMap<Class<?>, Object> envMap;
	protected ICastable env = new ICastable() {
		@Override
		public <T> T cast(Class<?> to) {
			return envMap == null ? null : (T) envMap.get(to);
		}
	};

	protected LinkedHashMap<Integer, SustainSet> sustainMap = new LinkedHashMap<>();
	protected IDataDetectable<Map<Integer, IDataRef<SustainSetUnit>>, NBTTagList> dds = new IDataDetectable<Map<Integer, IDataRef<SustainSetUnit>>, NBTTagList>() {

		NBTTagList detectList = new NBTTagList();

		@Override
		public NBTTagList detectChanges(IDataRef<Map<Integer, IDataRef<SustainSetUnit>>> templateRef) {

			if (side == Side.SERVER) {
				if (EventServer.tick != lastUpdateTick) {
					lastUpdateTick = EventServer.tick;
					SustainDisplayManager.this.updateServer();
				}
			}

			Map<Integer, IDataRef<SustainSetUnit>> tmp = templateRef.get();
			if (tmp == null) templateRef.set(tmp = new HashMap<>());

			if (!sustainMap.isEmpty()) {
				Iterator<SustainSet> iter = sustainMap.values().iterator();
				while (iter.hasNext()) {
					SustainSet set = iter.next();
					if (!set.isSustaining()) {
						iter.remove();
						onRemove(set);
						continue;
					}
					IDataRef<SustainSetUnit> recordRef = tmp.get(set.getId());
					boolean isFirst = false;
					if (recordRef == null) {
						tmp.put(set.getId(), recordRef = new IDataRef.Simple<SustainSetUnit>());
						isFirst = true;
					}
					NBTTagCompound changes = set.detectChanges(recordRef);
					if (changes == null) continue;
					if (isFirst) continue; // todo 自动同步模式
					changes.setInteger("id", set.getId());
					detectList.appendTag(changes);
				}
			}

			if (sustainMap.size() != tmp.size()) {
				Iterator<Integer> iter = tmp.keySet().iterator();
				while (iter.hasNext()) {
					Integer id = iter.next();
					if (!sustainMap.containsKey(id)) {
						iter.remove();
						NBTTagCompound changes = new NBTTagCompound();
						changes.setBoolean("$rm", true);
						changes.setInteger("id", id);
						detectList.appendTag(changes);
					}
				}
			}

			if (detectList.isEmpty()) return null;
			NBTTagList llist = detectList;
			detectList = new NBTTagList();
			return llist;
		}

		@Override
		public void mergeChanges(NBTTagList list) {
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound nbt = list.getCompoundTagAt(i);
				int id = nbt.getInteger("id");
				SustainSet set = sustainMap.get(id);
				if (set == null) continue;
				if (nbt.getBoolean("$rm")) {
					set.close();
					remove(id);
				} else set.mergeChanges(nbt);
			}
		}

	};

	protected Map<String, Integer> digestMap;

	public final Side side;

	public SustainDisplayManager(Side side) {
		this.side = side;
	}

	public IDataDetectable<Map<Integer, IDataRef<SustainSetUnit>>, NBTTagList> getDataDetectable() {
		return dds;
	}

	public void clear() {
		sustainMap.clear();
	}

	public void add(SustainSet set) {
		if (!set.isSustaining()) return;
		set.setSide(side);
		sustainMap.put(set.getId(), set);
		onAdd(set);
	}

	public void remove(int id) {
		SustainSet set = sustainMap.remove(id);
		if (set == null) return;
		onRemove(set);
	}

	public void enableDigestDeduplication() {
		digestMap = new HashMap<>();
	}

	private void onAdd(SustainSet set) {
		if (!set.isSustaining()) return;
		updateDigest(set);
	}

	private void onRemove(SustainSet set) {
		removeDigest(set);
	}

	private void updateDigest(SustainSet set) {
		if (digestMap == null) return;
		if (set.digests == null) return;
		for (String digest : set.digests) {
			if (digestMap.containsKey(digest)) abandonOldDigest(digest);
			digestMap.put(digest, set.getId());
		}
	}

	private void removeDigest(SustainSet set) {
		Set<String> digests = set.digests;
		if (digests == null) return;
		for (String digest : digests) digestMap.remove(digest);
	}

	private void abandonOldDigest(String digest) {
		Integer id = digestMap.get(digest);
		SustainSet set = sustainMap.get(id);
		if (set == null) return;
		set.abandonByDigest(digest);
	}

	public <T, U extends T> void setEnv(Class<T> clazz, U obj) {
		if (obj == null) return;
		if (envMap == null) envMap = new IdentityHashMap<>();
		envMap.put(clazz, obj);
	}

	protected int lastUpdateTick;

	protected void updateServer() {
		for (SustainSet set : sustainMap.values()) set.updateServer(env);
	}

}
