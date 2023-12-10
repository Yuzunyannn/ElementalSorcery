package yuzunyannn.elementalsorcery.api.util.var;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IVariableSet extends INBTSerializable<NBTTagCompound> {

	public <T> void set(Variable<T> var, T obj);

	public <T> T get(Variable<T> var);

	public boolean has(Variable<?> var);

	default boolean has(String string) {
		return has(new Variable<Object>(string, null));
	}

	public void remove(Variable<?> var);

	@Nullable
	public Object ask(String name);

	@Nullable
	default public Object ask(String name, Class<?> cls) {
		Object obj = ask(name);
		if (obj != null && cls.isAssignableFrom(obj.getClass())) return obj;
		return null;
	}

	public void clear();

	public boolean isEmpty();

	public IVariableSet copy();

	default VariableSet getVariableSet(String str) {
		return get(new Variable<VariableSet>(str, VariableSet.VAR_SET));
	}

}
