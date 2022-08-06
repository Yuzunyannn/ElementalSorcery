package yuzunyannn.elementalsorcery.api.util.var;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class VTNBTTagCompound implements IVariableType<NBTTagCompound> {

	@Override
	public NBTTagCompound newInstance(NBTBase base) {
		if (base instanceof NBTTagCompound) return (NBTTagCompound) base;
		return new NBTTagCompound();
	}

	@Override
	public NBTBase serializable(NBTTagCompound obj) {
		return obj;
	}
}
