package yuzunyannn.elementalsorcery.api.util;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;

public interface IDataDetectable<T, N extends NBTBase> {

	T get();

	void set(T obj);

	T copy();

	@Nullable
	N detectChanges(@Nullable T temp);

	void mergeChanges(N nbt);
}
