package yuzunyannn.elementalsorcery.util.element;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.crafting.IDataSensitivity;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;

public abstract class ElementInventoryAdapter implements IElementInventory {

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

	@Override
	public void addInformation(World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		ElementHelper.addElementInformation(this, worldIn, tooltip, flagIn);

	}

	@Override
	public IElementInventory setSensor(IDataSensitivity sensor) {
		return this;
	}

	@Override
	public void applyUse() {

	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {

	}

	@Override
	public NBTTagCompound serializeNBT() {
		return new NBTTagCompound();
	}

	@Override
	public IElementInventory assign(IElementInventory other) {
		return this;
	}

}
