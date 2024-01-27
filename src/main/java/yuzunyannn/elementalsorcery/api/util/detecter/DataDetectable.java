package yuzunyannn.elementalsorcery.api.util.detecter;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.nbt.NBTBase;

public abstract class DataDetectable<T, N extends NBTBase> implements IDataDetectable<T, N> {

	protected final Consumer<T> setter;
	protected final Supplier<T> getter;

	public DataDetectable(Consumer<T> setter, Supplier<T> getter) {
		this.setter = setter;
		this.getter = getter;
	}

	@Override
	public void set(T obj) {
		this.setter.accept(obj);
	}

	@Override
	public T get() {
		return getter.get();
	}

}
