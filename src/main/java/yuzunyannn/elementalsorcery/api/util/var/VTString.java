package yuzunyannn.elementalsorcery.api.util.var;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;

public class VTString implements IVariableType<String> {

	@Override
	public String newInstance(NBTBase base) {
		if (base instanceof NBTTagString) return ((NBTTagString) base).getString();
		return "";
	}

	@Override
	public NBTBase serializable(String obj) {
		return new NBTTagString(obj);
	}

	@Override
	public String cast(Object obj) {
		if (obj == null) return "null";
		return obj.toString();
	}
}
