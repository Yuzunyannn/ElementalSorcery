package yuzunyannn.elementalsorcery.api.util.var;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.element.ElementStack;

public class VTElement implements IVariableType<ElementStack> {

	@Override
	public ElementStack newInstance(NBTBase base) {
		if (base instanceof NBTTagCompound) return new ElementStack((NBTTagCompound) base);
		return ElementStack.EMPTY.copy();
	}

	@Override
	public NBTBase serializable(ElementStack obj) {
		return obj.isEmpty() ? null : obj.serializeNBT();
	}
}
