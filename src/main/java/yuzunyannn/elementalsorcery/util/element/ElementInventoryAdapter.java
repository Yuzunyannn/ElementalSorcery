package yuzunyannn.elementalsorcery.util.element;

import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;

public class ElementInventoryAdapter implements IElementInventory {

	public ElementInventoryAdapter() {
		super();
	}

	@Override
	public boolean hasState(NBTTagCompound nbt) {
		return false;
	}

	@Override
	public void loadState(NBTTagCompound nbt) {
	}

	@Override
	public void saveState(NBTTagCompound nbt) {

	}

	@Override
	public int getSlots() {
		return 0;
	}

	@Override
	public ElementStack getStackInSlot(int slot) {
		return ElementStack.EMPTY;
	}

	@Override
	public ElementStack setStackInSlot(int slot, ElementStack estack) {
		return ElementStack.EMPTY;
	}

	@Override
	public boolean insertElement(int slot, ElementStack estack, boolean simulate) {
		return false;
	}

	@Override
	public ElementStack extractElement(int slot, ElementStack estack, boolean simulate) {
		return ElementStack.EMPTY;
	}

}
