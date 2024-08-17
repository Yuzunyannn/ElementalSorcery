package yuzunyannn.elementalsorcery.api.util.var;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.BiFunction;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.element.ElementStack;

public class VariableSet implements IVariableSet {

	public VariableSet() {
	}

	public VariableSet(NBTTagCompound nbt) {
		this.nbt = nbt;
	}

	private static class VariableEntry<T> {

		static VariableEntry of(IVariableType<?> type, Object obj) {
			VariableEntry entry = new VariableEntry();
			entry.type = type;
			entry.obj = obj;
			return entry;
		}

		IVariableType<T> type;
		Object obj;

		@Override
		public String toString() {
			return String.valueOf(obj);
		}
	}

	protected NBTTagCompound nbt;
	protected Map<String, VariableEntry> map = new HashMap<>();

	@Override
	public <T> void set(String key, T obj, IVariableType<T> type) {
		if (obj == null) {
			this.remove(key);
			return;
		}
		map.put(key, VariableEntry.of(type, obj));
	}

	@Override
	public void set(String key, NBTBase tag) {
		if (tag == null) {
			this.remove(key);
			return;
		}
		if (nbt == null) nbt = new NBTTagCompound();
		map.remove(key);
		nbt.setTag(key, tag);
	}

	@Override
	public <T> T get(String key, IVariableType<T> type) {
		VariableEntry<T> entry = map.get(key);
		if (entry != null) {
			try {
				return type.cast(entry.obj);
			} catch (Exception e) {}
		}
		NBTBase base = null;
		if (nbt != null) {
			base = nbt.getTag(key);
			nbt.removeTag(key);
			if (nbt.isEmpty()) nbt = null;
		}
		T obj = type.newInstance(base);
		map.put(key, VariableEntry.of(type, obj));
		return obj;
	}

	@Override
	public NBTBase get(String key) {
		if (nbt != null) {
			if (nbt.hasKey(key)) return nbt.getTag(key);
		}
		VariableEntry<?> entry = map.get(key);
		if (entry == null) return null;
		try {
			return entry.type.serializableObject(entry.obj);
		} catch (Exception e) {}
		return null;
	}

	@Override
	public boolean has(String key) {
		if (map.containsKey(key)) return true;
		return nbt == null ? false : nbt.hasKey(key);
	}

	@Override
	public void remove(String key) {
		map.remove(key);
		if (nbt != null) {
			nbt.removeTag(key);
			if (nbt.isEmpty()) nbt = null;
		}
	}

	@Override
	@Nullable
	public Object ask(String key) {
		VariableEntry<?> entry = map.get(key);
		return entry != null ? entry.obj : null;
	}

	@Override
	public boolean isEmpty() {
		if (map.isEmpty() && (nbt == null || nbt.isEmpty())) return true;
		return false;
	}

	@Override
	public void clear() {
		nbt = null;
		map.clear();
	}

	@Override
	public VariableSet copy() {
		VariableSet set = new VariableSet();
		set.deserializeNBT(this.serializeNBT());
		return set;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return serializeNBT((key, obj) -> true);
	}

	public NBTTagCompound serializeNBT(BiFunction<String, Object, Boolean> check) {
		NBTTagCompound nbt = new NBTTagCompound();
		for (Entry<String, VariableEntry> entry : map.entrySet()) {
			VariableEntry vEntry = entry.getValue();
			String key = entry.getKey();
			Object obj = vEntry.obj;
			if (check.apply(key, obj)) {
				NBTBase base = vEntry.type.serializableObject(obj);
				if (base != null) nbt.setTag(key, base);
			}
		}
		if (this.nbt == null) return nbt;
		for (String key : this.nbt.getKeySet()) {
			if (!nbt.hasKey(key)) {
				NBTBase base = this.nbt.getTag(key);
				if (check.apply(key, base)) nbt.setTag(key, base);
			}
		}
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.map.clear();
		this.nbt = nbt;
	}

	@Override
	public String toString() {
		return "map:" + this.map + " nbt:" + this.nbt;
	}

	public final static IVariableType<Byte> BYTE = new VTPrimitive.VTByte();
	public final static IVariableType<Short> SHORT = new VTPrimitive.VTShort();
	public final static IVariableType<Integer> INT = new VTPrimitive.VTInt();
	public final static IVariableType<Long> LONG = new VTPrimitive.VTLong();
	public final static IVariableType<Float> FLOAT = new VTPrimitive.VTFloat();
	public final static IVariableType<Double> DOUBLE = new VTPrimitive.VTDouble();
	public final static IVariableType<Boolean> BOOL = new VTBoolean();
	public final static IVariableType<String> STRING = new VTString();
	public final static IVariableType<Object> JOBJ = new VTJavaObject();

	public final static IVariableType<NBTTagCompound> NBT_TAG = new VTNBTTagCompound();

	public final static IVariableType<VariableSet> VAR_SET = new VTVariableSet();
	public final static IVariableType<ArrayList<VariableSet>> VAR_SET_ARRAY_LIST = new VTVTArrayList<>(VAR_SET);
	public final static IVariableType<LinkedList<VariableSet>> VAR_SET_LINKED_LIST = new VTVTLinkedList<>(VAR_SET);

	public final static IVariableType<ElementStack> ELEMENT = new VTElement();
	public final static IVariableType<ArrayList<ElementStack>> ELEMENT_ARRAY_LIST = new VTVTArrayList<>(ELEMENT);
	public final static IVariableType<LinkedList<ElementStack>> ELEMENT_LINKED_LIST = new VTVTLinkedList<>(ELEMENT);

	public final static IVariableType<ItemStack> ITEM = new VTItem();
	public final static IVariableType<ArrayList<ItemStack>> ITEM_ARRAY_LIST = new VTVTArrayList<>(ITEM);
	public final static IVariableType<LinkedList<ItemStack>> ITEM_LINKED_LIST = new VTVTLinkedList<>(ITEM);

	public final static IVariableType<BlockPos> BLOCK_POS = new VTBlockPos();
	public final static IVariableType<ArrayList<BlockPos>> BLOCK_POS_ARRAY_LIST = new VTVTArrayList<>(BLOCK_POS);
	public final static IVariableType<LinkedList<BlockPos>> BLOCK_POS_LINKED_LIST = new VTVTLinkedList<>(BLOCK_POS);

	public final static IVariableType<Vec3d> VEC3D = new VTVec3d();
	public final static IVariableType<ArrayList<Vec3d>> VEC3D_ARRAY_LIST = new VTVTArrayList<>(VEC3D);
	public final static IVariableType<LinkedList<Vec3d>> VEC3D_LINKED_LIST = new VTVTLinkedList<>(VEC3D);

	public final static IVariableType<ArrayList<String>> STRING_ARRAY_LIST = new VTVTArrayList<>(STRING);
	public final static IVariableType<LinkedList<String>> STRING_LINKED_LIST = new VTVTLinkedList<>(STRING);

	public final static IVariableType<UUID> UUID = new VTUUID();

}
