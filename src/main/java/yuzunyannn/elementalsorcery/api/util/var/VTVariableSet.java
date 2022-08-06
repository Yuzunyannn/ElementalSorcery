package yuzunyannn.elementalsorcery.api.util.var;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class VTVariableSet implements IVariableType<VariableSet> {

	@Override
	public VariableSet newInstance(NBTBase base) {
		if (base instanceof NBTTagCompound) {
			VariableSet set = new VariableSet();
			set.deserializeNBT((NBTTagCompound) base);
			return set;
		}
		return new VariableSet();
	}

	@Override
	public NBTBase serializable(VariableSet obj) {
		return obj.serializeNBT();
	}
}
