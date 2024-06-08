package yuzunyannn.elementalsorcery.computer;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.computer.IDisk;
import yuzunyannn.elementalsorcery.api.item.ESItemStorageEnum;

public class DiskItem implements IDisk {

	protected ItemStack stack;

	public DiskItem(ItemStack stack) {
		this.stack = stack;
	}

	public DiskItem(NBTTagCompound nbt) {
		this.deserializeNBT(nbt);
	}

	public boolean isEmpty() {
		return stack.isEmpty();
	}

	@Override
	public NBTTagCompound getContext() {
		return stack.getOrCreateSubCompound(ESItemStorageEnum.DISK_DATA);
	}

	public ItemStack getItemStack() {
		return stack;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return stack.serializeNBT();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		stack = new ItemStack(nbt);
	}

}
