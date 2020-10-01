package yuzunyannn.elementalsorcery.util.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

/** 继承接口的类，重写一个getItemStackHandler就可以将其作为IInventory使用 */
public interface IItemStackHandlerInventory extends IInventory {

	public IItemHandlerModifiable getItemStackHandler();

	@Override
	default public String getName() {
		return "InventoryMultiBlock";
	}

	@Override
	default public boolean hasCustomName() {
		return false;
	}

	@Override
	default public int getSizeInventory() {
		return this.getItemStackHandler().getSlots();
	}

	@Override
	default public boolean isEmpty() {
		IItemHandlerModifiable inventory = this.getItemStackHandler();
		for (int i = 0; i < inventory.getSlots(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (!stack.isEmpty()) { return false; }
		}
		return true;
	}

	@Override
	default public ItemStack getStackInSlot(int index) {
		return this.getItemStackHandler().getStackInSlot(index);
	}

	@Override
	default public ItemStack decrStackSize(int index, int count) {
		ItemStack itemstack = this.getItemStackHandler().getStackInSlot(index);
		if (itemstack.isEmpty()) return itemstack;
		itemstack = itemstack.splitStack(count);
		this.markDirty();
		return itemstack;
	}

	@Override
	default public ItemStack removeStackFromSlot(int index) {
		IItemHandlerModifiable inventory = this.getItemStackHandler();
		ItemStack itemstack = inventory.getStackInSlot(index);
		if (itemstack.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			inventory.setStackInSlot(index, ItemStack.EMPTY);
			this.markDirty();
			return itemstack;
		}
	}

	@Override
	default public void setInventorySlotContents(int index, ItemStack stack) {
		this.getItemStackHandler().setStackInSlot(index, stack);
	}

	@Override
	default public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	default public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	default public void openInventory(EntityPlayer player) {
	}

	@Override
	default public void closeInventory(EntityPlayer player) {
	}

	@Override
	default public boolean isItemValidForSlot(int index, ItemStack stack) {
		return this.getItemStackHandler().insertItem(index, stack, true).isEmpty();
	}

	@Override
	default public int getField(int id) {
		return 0;
	}

	@Override
	default public void setField(int id, int value) {

	}

	@Override
	default public int getFieldCount() {
		return 0;
	}

	@Override
	default public void clear() {
		IItemHandlerModifiable inventory = this.getItemStackHandler();
		for (int i = 0; i < inventory.getSlots(); i++) {
			inventory.setStackInSlot(i, ItemStack.EMPTY);
		}
	}

	default public InventoryCrafting toInventoryCrafting(Container eventHandlerIn) {
		return new InventoryCraftingUseInventory(eventHandlerIn, this, this.getInventoryCraftingSize());
	}

	default public int getInventoryCraftingSize() {
		return 3;
	}

}
