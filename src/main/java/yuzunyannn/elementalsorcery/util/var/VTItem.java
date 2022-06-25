package yuzunyannn.elementalsorcery.util.var;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class VTItem implements IVariableType<ItemStack> {

	@Override
	public ItemStack newInstance(NBTBase base) {
		if (base instanceof NBTTagCompound) return new ItemStack((NBTTagCompound) base);
		return ItemStack.EMPTY;
	}

	@Override
	public NBTBase serializable(ItemStack obj) {
		return obj.isEmpty() ? null : obj.serializeNBT();
	}
}
