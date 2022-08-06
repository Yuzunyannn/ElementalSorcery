package yuzunyannn.elementalsorcery.api.util.var;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;

public interface IVariableType<T> {

	T newInstance(@Nullable NBTBase base);

	NBTBase serializable(T obj);

	default NBTBase serializableObject(Object obj) {
		try {
			return this.serializable((T) obj);
		} catch (Exception e) {}
		return this.serializable(this.newInstance(null));
	}

	default T cast(Object obj) {
		return (T) obj;
	}
}
