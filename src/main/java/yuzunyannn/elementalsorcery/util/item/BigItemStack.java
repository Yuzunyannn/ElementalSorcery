package yuzunyannn.elementalsorcery.util.item;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public class BigItemStack implements INBTSerializable<NBTTagCompound> {

	private ItemStack stack;

	public BigItemStack() {
		stack = ItemStack.EMPTY;
	}

	public BigItemStack(Item item) {
		this.stack = new ItemStack(item);
	}

	public BigItemStack(Item item, int n) {
		this.stack = new ItemStack(item, n);
	}

	public BigItemStack(Item item, int n, int meta) {
		this.stack = new ItemStack(item, n, meta);
	}

	public BigItemStack(Block block) {
		this.stack = new ItemStack(block);
	}

	public BigItemStack(Block block, int n) {
		this.stack = new ItemStack(block, n);
	}

	public BigItemStack(Block block, int n, int meta) {
		this.stack = new ItemStack(block, n, meta);
	}

	public BigItemStack(ItemStack stack) {
		this.stack = stack;
	}

	public BigItemStack(NBTTagCompound nbt) {
		this.deserializeNBT(nbt);
	}

	public ItemStack getItemStack() {
		return stack;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = stack.serializeNBT();
		nbt.setInteger("Count", stack.getCount());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		int count = nbt.getInteger("Count");
		stack = new ItemStack(nbt);
		stack.setCount(count);
	}

	@Override
	public int hashCode() {
		return stack.getItem().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof BigItemStack) return ((BigItemStack) obj).getItemStack().isItemEqual(this.getItemStack());
		return false;
	}

}
