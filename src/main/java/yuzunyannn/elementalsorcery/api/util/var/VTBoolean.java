package yuzunyannn.elementalsorcery.api.util.var;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;

public class VTBoolean implements IVariableType<Boolean> {

	@Override
	public Boolean newInstance(NBTBase base) {
		if (base instanceof NBTPrimitive) return ((NBTPrimitive) base).getByte() == 0 ? false : true;
		return false;
	}

	@Override
	public NBTBase serializable(Boolean obj) {
		return new NBTTagByte((byte) (obj ? 1 : 0));
	}

	@Override
	public Boolean cast(Object obj) {
		if (obj instanceof Boolean) return (Boolean) obj;
		if (obj instanceof Number) return ((Number) obj).byteValue() == 0 ? false : true;
		return obj != null;
	}
}
