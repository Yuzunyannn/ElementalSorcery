package yuzunyannn.elementalsorcery.api.util.var;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IVariableSet extends INBTSerializable<NBTTagCompound> {

	public <T> void set(Variable<T> var, T obj);

	public void set(String key, NBTBase tag);

	public <T> T get(Variable<T> var);

	@Nullable
	public NBTBase get(String key);

	@Nullable
	public Object ask(String key);

	@Nullable
	default public Object ask(String name, Class<?> cls) {
		Object obj = ask(name);
		if (obj != null && cls.isAssignableFrom(obj.getClass())) return obj;
		return null;
	}

	default public boolean has(Variable<?> var) {
		return has(var.key);
	}

	boolean has(String key);

	default public void remove(Variable<?> var) {
		remove(var.key);
	}

	public void remove(String key);

	public void clear();

	public boolean isEmpty();

	public IVariableSet copy();

	default IVariableSet getVariableSet(String str) {
		return get(new Variable<VariableSet>(str, VariableSet.VAR_SET));
	}

	default IVariableSet getVariableSet(String[] paths) {
		IVariableSet curr = this;
		for (String str : paths) curr = curr.getVariableSet(str);
		return curr;
	}

	default public NBTBase get(String[] paths) {
		if (paths.length == 0) return null;
		IVariableSet curr = this;
		for (int i = 0; i < paths.length - 1; i++) curr = curr.getVariableSet(paths[i]);
		return curr.get(paths[paths.length - 1]);
	}

	default public void set(String[] paths, NBTBase tag) {
		if (paths.length == 0) return;
		IVariableSet curr = this;
		for (int i = 0; i < paths.length - 1; i++) curr = curr.getVariableSet(paths[i]);
		curr.set(paths[paths.length - 1], tag);
	}

	default public void remove(String[] paths) {
		if (paths.length == 0) return;
		IVariableSet curr = this;
		for (int i = 0; i < paths.length - 1; i++) curr = curr.getVariableSet(paths[i]);
		curr.remove(paths[paths.length - 1]);
	}

}
