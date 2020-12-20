package yuzunyannn.elementalsorcery.util;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagShort;
import net.minecraftforge.common.util.INBTSerializable;

public class VariableSet implements INBTSerializable<NBTTagCompound> {

	public static class Variable<T> {

		public final String name;

		public final IVariableType<T> type;

		public Variable(String name, IVariableType<T> type) {
			this.name = name;
			this.type = type;
		}
	}

	public static interface IVariableType<T> {

		T newInstance(@Nullable NBTBase base);

		NBTBase serializable(T obj);
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

	private NBTTagCompound map = new NBTTagCompound();

	public <T> void set(Variable<T> var, T obj) {
		if (obj == null) return;
		map.setTag(var.name, var.type.serializable(obj));
	}

	public <T> T get(Variable<T> var) {
		String key = var.name;
		NBTBase base = map.getTag(key);
		if (base == null) return var.type.newInstance(null);
		return var.type.newInstance(base);
	}

	public boolean isEmpty() {
		return map.hasNoTags();
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return map;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		map = nbt;
	}

}
