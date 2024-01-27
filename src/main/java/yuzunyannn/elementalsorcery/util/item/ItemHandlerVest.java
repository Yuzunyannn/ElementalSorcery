package yuzunyannn.elementalsorcery.util.item;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ItemHandlerVest implements IItemHandler {

	IInventory inventory;

	public ItemHandlerVest(IInventory inventory) {
		this.inventory = inventory;
	}

	@Override
	public int getSlots() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot) {
		return inventory.getInventoryStackLimit();
	}

}
