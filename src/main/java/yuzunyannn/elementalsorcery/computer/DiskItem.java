package yuzunyannn.elementalsorcery.computer;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.computer.IDisk;
import yuzunyannn.elementalsorcery.api.computer.StoragePath;
import yuzunyannn.elementalsorcery.api.item.ESItemStorageEnum;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class DiskItem extends DeviceStorage implements IDisk {

	protected ItemStack stack;
	protected boolean hasChange = false;

	public DiskItem(ItemStack stack) {
		super.deserializeNBT(stack.getOrCreateSubCompound(ESItemStorageEnum.DISK_DATA));
		this.stack = stack;
		this.stack.removeSubCompound(ESItemStorageEnum.DISK_DATA);
		this.hasChange = true;
	}

	public DiskItem(NBTTagCompound nbt) {
		this.deserializeNBT(nbt);
	}

	public boolean isEmpty() {
		return stack.isEmpty();
	}

	public ItemStack toItemStack() {
		if (!this.hasChange) return stack;
		this.hasChange = false;
		NBTTagCompound tagCompund = ItemHelper.getOrCreateTagCompound(stack);
		tagCompund.setTag(ESItemStorageEnum.DISK_DATA, super.serializeNBT());
		return stack;
	}

	public ItemStack getItemStack() {
		return stack;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return toItemStack().serializeNBT();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		stack = new ItemStack(nbt);
		super.deserializeNBT(stack.getOrCreateSubCompound(ESItemStorageEnum.DISK_DATA));
		stack.removeSubCompound(ESItemStorageEnum.DISK_DATA);
		this.hasChange = true;
	}

	@Override
	public DiskItem copy() {
		return new DiskItem(toItemStack().copy());
	}

	@Override
	public void markDirty(StoragePath path) {
		super.markDirty(path);
		this.stack.removeSubCompound(ESItemStorageEnum.DISK_DATA);
		this.hasChange = true;
	}

}
