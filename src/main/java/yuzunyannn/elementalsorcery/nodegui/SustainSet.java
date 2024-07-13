package yuzunyannn.elementalsorcery.nodegui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.api.util.ICastable;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.api.util.detecter.IDataDetectable;
import yuzunyannn.elementalsorcery.api.util.detecter.IDataRef;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTSS;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;
import yuzunyannn.elementalsorcery.util.helper.JavaHelper;

public class SustainSet implements IDataDetectable<SustainSetUnit, NBTTagCompound>, INBTSS {

	static class Data {
		IDisplaySustainable able;
		int index;
	}

	protected Set<String> digests;
	protected List<Data> sustains;
	protected List<Data> updates;
	protected int id;
	protected Side side;
	protected boolean isSustaining;

	public void setSide(Side side) {
		if (this.side != null && this.side != side) throw new RuntimeException("one ref in diff Side");
		this.side = side;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	// cannot seek when running, only can after create
	public void seek(Object displayObject) {
		findSustainable(displayObject, 0);
		isSustaining = this.isSustaining();
	}

	protected void findSustainable(Object obj, int deep) {
		if (obj == null) return;
		if (deep > 1) return;

		if (obj instanceof IDisplaySustainable) {
			if (sustains == null) sustains = new ArrayList<>();
			if (updates == null) updates = new LinkedList<>();
			IDisplaySustainable able = (IDisplaySustainable) obj;
			Data dat = new Data();
			dat.able = able;
			dat.index = sustains.size();
			sustains.add(dat);
			if (side == Side.CLIENT) return;
			if (!dat.able.isAlive()) return;
			String digest = able.digest();
			if (digest != null) {
				if (digests == null) digests = new TreeSet<>();
				if (digests.contains(digest)) return; // 重复的不要updates
				digests.add(digest);
			}
			updates.add(dat);
			return;
		}

		if (obj instanceof List) {
			for (Object _obj : (List) obj) findSustainable(_obj, deep + 1);
		} else if (JavaHelper.isArray(obj)) {
			for (Object _obj : (Object[]) obj) findSustainable(_obj, deep + 1);
		}
	}

	@Override
	public void writeUpdateData(INBTWriter writer) {
		writer.write("_is", isSustaining);
	}

	@Override
	public void readUpdateData(INBTReader reader) {
		isSustaining = reader.nboolean("_is");
	}

	public boolean isSustaining() {
		if (side == Side.CLIENT) return this.isSustaining;
		return isSustaining = (updates != null && !updates.isEmpty());
	}

	public void clear() {
		sustains = updates = null;
		digests = null;
	}

	public void close() {
		if (sustains != null) {
			Iterator<Data> iter = sustains.iterator();
			while (iter.hasNext()) {
				Data dat = iter.next();
				dat.able.setDead(side);
			}
		}
		clear();
	}

	public void updateServer(ICastable env) {
		if (updates == null) return;
		Iterator<Data> iter = updates.iterator();
		while (iter.hasNext()) {
			Data dat = iter.next();
			if (dat.able.isAlive()) dat.able.updateServer(env);
		}
		if (updates.isEmpty()) updates = null;
	}

	public void abandonByDigest(String disgest) {
		if (updates == null) return;
		Iterator<Data> iter = updates.iterator();
		while (iter.hasNext()) {
			Data dat = iter.next();
			String _digest = dat.able.digest();
			if (_digest == null || !_digest.equals(disgest)) continue;
			iter.remove();
			dat.able.abandon();
			digests.remove(disgest);
			break;
		}
	}

	private NBTTagCompound detectMap = new NBTTagCompound();

	@Override
	public NBTTagCompound detectChanges(IDataRef<SustainSetUnit> templateRef) {
		SustainSetUnit map = templateRef.get();
		if (map == null) templateRef.set(map = new SustainSetUnit());
		if (updates == null) return null;

		if (map.refCheck != updates) {
			map.refCheck = updates;
			map.clear();
		}

		NBTTagCompound updateMap = null;
		NBTTagList removeList = null;
		Set<Integer> hasSet = updates.size() < map.size() ? new HashSet<>() : null;

		Iterator<Data> iter = updates.iterator();
		while (iter.hasNext()) {
			Data dat = iter.next();
			IDataRef<Object> ref = map.get(dat.index);
			if (ref == null) map.put(dat.index, ref = new IDataRef.Simple<>());
			NBTBase changes = dat.able.detectChanges(ref);
			if (changes != null) {
				if (updateMap == null) detectMap.setTag("up", updateMap = new NBTTagCompound());
				updateMap.setTag(String.valueOf(dat.index), changes);
			}
			if (hasSet != null) hasSet.add(dat.index);
			// 同步后才能移除，下次关闭消息发给client
			if (!dat.able.isAlive()) iter.remove();
		}

		if (hasSet != null) {
			if (removeList == null) detectMap.setTag("rm", removeList = new NBTTagList());
			for (Integer index : map.keySet()) {
				if (hasSet.contains(index)) continue;
				removeList.appendTag(new NBTTagShort(index.shortValue()));
			}
		}

		if (!detectMap.isEmpty()) {
			NBTTagCompound dat = detectMap;
			detectMap = new NBTTagCompound();
			return dat;
		}
		return null;
	}

	@Override
	public void mergeChanges(NBTTagCompound nbt) {

		if (nbt.hasKey("rm") && updates != null) {
			NBTTagList removeList = nbt.getTagList("rm", NBTTag.TAG_SHORT);
			Set<Integer> removeSet = new HashSet<>();
			for (int i = 0; i < removeList.tagCount(); i++) {
				int index = ((NBTPrimitive) removeList.get(i)).getShort();
				removeSet.add(index);
			}
			Iterator<Data> iter = updates.iterator();
			while (iter.hasNext()) {
				Data dat = iter.next();
				if (!removeSet.contains(dat.index)) continue;
				iter.remove();
				dat.able.setDead(side);
			}
		}

		if (nbt.hasKey("up") && sustains != null) {
			NBTTagCompound map = nbt.getCompoundTag("up");
			for (String key : map.getKeySet()) {
				try {
					Integer index = Integer.valueOf(key);
					Data dat = sustains.get(index);
					NBTBase tag = map.getTag(key);
					dat.able.mergeChanges(tag);
					if (side == Side.CLIENT) dat.able.afterChangesMerged(tag);
				} catch (Exception e) {}
			}
		}
	}

}
