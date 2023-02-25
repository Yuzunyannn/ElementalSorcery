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
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.api.element.ElementStack;

public class VariableSet implements INBTSerializable<NBTTagCompound> {

	public static class Variable<T> {

		public final String name;

		public final IVariableType<T> type;

		public Variable(String name, IVariableType<T> type) {
			this.name = name;
			this.type = type;
		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj instanceof Variable) return this.name.equals(((Variable) obj).name);
			return false;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private NBTTagCompound nbt;
	private Map<Variable, Object> map = new HashMap<>();

	public <T> void set(Variable<T> var, T obj) {
		if (obj == null) return;
		map.put(var, obj);
	}

	public <T> T get(Variable<T> var) {
		String key = var.name;
		Object obj = map.get(var);
		if (obj != null) {
			try {
				return var.type.cast(obj);
			} catch (Exception e) {}
		}
		NBTBase base = null;
		if (nbt != null) {
			base = nbt.getTag(key);
			nbt.removeTag(key);
			if (nbt.isEmpty()) nbt = null;
		}
		map.put(var, obj = var.type.newInstance(base));
		return (T) obj;
	}

	public boolean has(Variable<?> var) {
		if (map.containsKey(var)) return true;
		return nbt == null ? false : nbt.hasKey(var.name);
	}

	public void remove(Variable<?> var) {
		map.remove(var);
		if (nbt != null) {
			nbt.removeTag(var.name);
			if (nbt.isEmpty()) nbt = null;
		}
	}

	@Nullable
	public Object ask(String name) {
		return map.get(new Variable<Object>(name, null));
	}

	@Nullable
	public Object ask(String name, Class<?> cls) {
		Object obj = ask(name);
		if (obj != null && cls.isAssignableFrom(obj.getClass())) return obj;
		return null;
	}

	public boolean isEmpty() {
		if (map.isEmpty() && (nbt == null || nbt.isEmpty())) return true;
		return false;
	}

	public void clear() {
		nbt = null;
		map.clear();
	}

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
		for (Entry<Variable, Object> entry : map.entrySet()) {
			Variable<?> var = entry.getKey();
			Object obj = entry.getValue();
			if (check.apply(var.name, obj)) {
				NBTBase base = var.type.serializableObject(obj);
				if (base != null) nbt.setTag(var.name, base);
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

	public final static IVariableType<Byte> BYTE = new VTPrimitive.VTByte();
	public final static IVariableType<Short> SHORT = new VTPrimitive.VTShort();
	public final static IVariableType<Integer> INT = new VTPrimitive.VTInt();
	public final static IVariableType<Long> LONG = new VTPrimitive.VTLong();
	public final static IVariableType<Float> FLOAT = new VTPrimitive.VTFloat();
	public final static IVariableType<Double> DOUBLE = new VTPrimitive.VTDouble();
	public final static IVariableType<Boolean> BOOL = new VTBoolean();
	public final static IVariableType<String> STRING = new VTString();
	public final static IVariableType<VariableSet> VAR_SET = new VTVariableSet();

	public final static IVariableType<NBTTagCompound> NBT_TAG = new VTNBTTagCompound();

	public final static IVariableType<ElementStack> ELEMENT = new VTElement();
	public final static IVariableType<LinkedList<ElementStack>> ELEMENT_LINKED_LIST = new VTVTLinkedList<>(ELEMENT);
	public final static IVariableType<ArrayList<ElementStack>> ELEMENT_ARRAY_LIST = new VTVTArrayList<>(ELEMENT);

	public final static IVariableType<ItemStack> ITEM = new VTItem();
	public final static IVariableType<LinkedList<ItemStack>> ITEM_LINKED_LIST = new VTVTLinkedList<>(ITEM);
	public final static IVariableType<ArrayList<ItemStack>> ITEM_ARRAY_LIST = new VTVTArrayList<>(ITEM);

	public final static IVariableType<BlockPos> BLOCK_POS = new VTBlockPos();
	public final static IVariableType<LinkedList<BlockPos>> BLOCK_POS_LINKED_LIST = new VTVTLinkedList<>(BLOCK_POS);
	public final static IVariableType<ArrayList<BlockPos>> BLOCK_POS_ARRAY_LIST = new VTVTArrayList<>(BLOCK_POS);

	public final static IVariableType<Vec3d> VEC3D = new VTVec3d();
	public final static IVariableType<LinkedList<Vec3d>> VEC3D_LINKED_LIST = new VTVTLinkedList<>(VEC3D);
	public final static IVariableType<ArrayList<Vec3d>> VEC3D_ARRAY_LIST = new VTVTArrayList<>(VEC3D);

	public final static IVariableType<UUID> UUID = new VTUUID();

}
