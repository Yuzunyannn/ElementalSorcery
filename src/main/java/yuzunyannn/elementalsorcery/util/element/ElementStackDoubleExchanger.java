package yuzunyannn.elementalsorcery.util.element;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.crafting.IDataSensitivity;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.tile.IElementInventoryModifiable;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.api.util.detecter.ContainerArrayDetecter.ICanArrayDetected;

public class ElementStackDoubleExchanger implements INBTSerializable<NBTTagCompound>, IElementInventoryModifiable,
		ICanArrayDetected<ElementStackDouble, NBTTagCompound> {

	protected ElementStackDouble[] edstacks;

	public ElementStackDoubleExchanger(int count) {
		setSlots(count);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		nbt.setTag("El", list);
		for (ElementStackDouble edstack : edstacks) list.appendTag(edstack.serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		NBTTagList list = nbt.getTagList("El", NBTTag.TAG_COMPOUND);
		edstacks = new ElementStackDouble[list.tagCount()];
		for (int i = 0; i < list.tagCount(); i++) edstacks[i] = new ElementStackDouble(list.getCompoundTagAt(i));
	}

	@Override
	public boolean hasState(NBTTagCompound nbt) {
		return nbt.hasKey("eExc", NBTTag.TAG_LIST);
	}

	@Override
	public void loadState(NBTTagCompound nbt) {
		deserializeNBT(nbt.getCompoundTag("eExc"));
	}

	@Override
	public void saveState(NBTTagCompound nbt) {
		nbt.setTag("eExc", serializeNBT());
	}

	@Override
	public void setSlots(int slots) {
		edstacks = new ElementStackDouble[slots];
		for (int i = 0; i < edstacks.length; i++) edstacks[i] = new ElementStackDouble();
	}

	@Override
	public int getSlots() {
		return edstacks.length;
	}

	@Override
	public ElementStack getStackInSlot(int slot) {
		return edstacks[slot].asElementStack();
	}

	public ElementStackDouble getStackDoubleInSlot(int slot) {
		return edstacks[slot];
	}

	@Override
	public ElementStack setStackInSlot(int slot, ElementStack estack) {
		ElementStack oldStack = getStackInSlot(slot);
		edstacks[slot].become(estack);
		return oldStack;
	}

	public ElementStackDouble setStackInSlot(int slot, ElementStackDouble edstack) {
		ElementStackDouble oldStack = getStackDoubleInSlot(slot);
		edstacks[slot] = edstack;
		return oldStack;
	}

	@Override
	public int getMaxSizeInSlot(int slot) {
		return -1;
	}

	@Override
	public boolean insertElement(int slot, ElementStack estack, boolean simulate) {
		if (estack.isEmpty()) return true;
		return getStackDoubleInSlot(slot).mergeElement(estack, simulate);
	}

	@Override
	public boolean insertElement(ElementStack estack, boolean simulate) {
		return IElementInventoryModifiable.super.insertElement(estack, simulate);
	}

	@Override
	public ElementStack extractElement(int slot, ElementStack estack, boolean simulate) {
		return ElementStack.EMPTY;
	}

	@Override
	public ElementStack extractElement(ElementStack estack, boolean simulate) {
		return ElementStack.EMPTY;
	}

	@Override
	public int getSize() {
		return getSlots();
	}

	@Override
	public boolean hasChange(int index, ElementStackDouble oldValue) {
		ElementStackDouble edStack = getStackDoubleInSlot(index);
		if (edStack.isEmpty()) return !oldValue.isEmpty();
		if (oldValue.isEmpty()) return !edStack.isEmpty();
		if (edStack.getElement() != oldValue.getElement()) return true;
		if ((int) edStack.getCount() != (int) oldValue.getCount()) return true;
		if ((int) edStack.getPower() != (int) oldValue.getPower()) return true;
		return false;
	}

	@Override
	public ElementStackDouble copyCurrValue(int index) {
		return getStackDoubleInSlot(index).copy();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setSize(int size) {
		setSlots(size);
	}

	@Override
	public NBTTagCompound serializeCurrValueToSend(int index) {
		return getStackDoubleInSlot(index).serializeNBT();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void deserializeCurrValueFromSend(int index, NBTTagCompound nbtData) {
		getStackDoubleInSlot(index).deserializeNBT(nbtData);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		ElementHelper.addElementInformation(this, worldIn, tooltip, flagIn);
	}

	private IDataSensitivity sensor;

	@Override
	public void markDirty() {
		if (sensor != null) sensor.markDirty();
	}

	@Override
	public void applyUse() {
		if (sensor != null) sensor.applyUse();
	}

	@Override
	public ElementStackDoubleExchanger setSensor(IDataSensitivity sensor) {
		this.sensor = sensor;
		return this;
	}

	@Override
	public IElementInventory assign(IElementInventory other) {
		if (this == other) return this;
		setSlots(other.getSlots());
		for (int i = 0; i < Math.min(this.getSlots(), other.getSlots()); i++)
			this.setStackInSlot(i, other.getStackInSlot(i).copy());
		for (int i = other.getSlots(); i < this.getSlots(); i++) this.setStackInSlot(i, ElementStack.EMPTY);
		return this;
	}
}
