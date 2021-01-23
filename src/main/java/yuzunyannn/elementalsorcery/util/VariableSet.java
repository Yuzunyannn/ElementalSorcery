package yuzunyannn.elementalsorcery.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.element.ElementStack;

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
	}

	public static interface IVariableType<T> {

		T newInstance(@Nullable NBTBase base);

		NBTBase serializable(T obj);

		default NBTBase serializableObject(Object obj) {
			try {
				return this.serializable((T) obj);
			} catch (Exception e) {}
			return this.serializable(this.newInstance(null));
		}
	}

	public final static IVariableType<Integer> INT = new IVariableType<Integer>() {

		@Override
		public Integer newInstance(NBTBase base) {
			if (base instanceof NBTPrimitive) return ((NBTPrimitive) base).getInt();
			return 0;
		}

		@Override
		public NBTBase serializable(Integer obj) {
			return new NBTTagInt(obj);
		}

	};

	public final static IVariableType<Short> SHORT = new IVariableType<Short>() {

		@Override
		public Short newInstance(NBTBase base) {
			if (base instanceof NBTPrimitive) return ((NBTPrimitive) base).getShort();
			return 0;
		}

		@Override
		public NBTBase serializable(Short obj) {
			return new NBTTagShort(obj);
		}

	};

	public final static IVariableType<ElementStack> ELEMENT = new IVariableType<ElementStack>() {

		@Override
		public ElementStack newInstance(NBTBase base) {
			if (base instanceof NBTTagCompound) return new ElementStack((NBTTagCompound) base);
			return ElementStack.EMPTY;
		}

		@Override
		public NBTBase serializable(ElementStack obj) {
			return obj.serializeNBT();
		}

	};

	public final static IVariableType<BlockPos> BLOCK_POS = new IVariableType<BlockPos>() {

		@Override
		public BlockPos newInstance(NBTBase base) {
			if (base instanceof NBTTagIntArray) {
				NBTTagIntArray array = (NBTTagIntArray) base;
				int[] ints = array.getIntArray();
				if (ints.length < 3) return BlockPos.ORIGIN;
				return new BlockPos(ints[0], ints[1], ints[2]);
			}
			return BlockPos.ORIGIN;
		}

		@Override
		public NBTBase serializable(BlockPos obj) {
			return new NBTTagIntArray(new int[] { obj.getX(), obj.getY(), obj.getZ() });
		}

	};

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
				return (T) obj;
			} catch (Exception e) {}
		}
		NBTBase base = null;
		if (nbt != null) {
			base = nbt.getTag(key);
			nbt.removeTag(key);
			if (nbt.hasNoTags()) nbt = null;
		}
		map.put(var, obj = var.type.newInstance(base));
		return (T) obj;
	}

	public <T> boolean has(Variable<T> var) {
		if (map.containsKey(var)) return true;
		return nbt == null ? false : nbt.hasKey(var.name);
	}

	public boolean isEmpty() {
		if (map.isEmpty() && (nbt == null || nbt.hasNoTags())) return true;
		return false;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		for (Entry<Variable, Object> entry : map.entrySet()) {
			Variable<?> var = entry.getKey();
			NBTBase base = var.type.serializableObject(entry.getValue());
			if (base != null) nbt.setTag(var.name, base);
		}
		if (this.nbt == null) return nbt;
		for (String key : this.nbt.getKeySet()) if (!nbt.hasKey(key)) nbt.setTag(key, this.nbt.getTag(key));
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.map.clear();
		this.nbt = nbt;
	}
}
