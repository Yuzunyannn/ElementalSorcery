package yuzunyan.elementalsorcery.util.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.ItemStackHandler;

public class ItemStackHandlerInventory extends ItemStackHandler implements IInventory {

	public ItemStackHandlerInventory() {
		super();
	}

	public ItemStackHandlerInventory(int size) {
		super(size);
	}

	@Override
	public String getName() {
		return "ItemStackHandlerInventory";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}

	@Override
	public int getSizeInventory() {
		return this.getSlots();
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < this.getSlots(); i++) {
			ItemStack stack = this.getStackInSlot(i);
			if (!stack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack itemstack = this.getStackInSlot(index);
		if (itemstack.isEmpty())
			return itemstack;
		itemstack = itemstack.splitStack(count);
		this.markDirty();
		return itemstack;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack itemstack = this.getStackInSlot(index);
		if (itemstack.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			this.setStackInSlot(index, ItemStack.EMPTY);
			return itemstack;
		}
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.setStackInSlot(index, stack);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return this.insertItem(index, stack, true).isEmpty();
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		stacks.clear();
	}

}
