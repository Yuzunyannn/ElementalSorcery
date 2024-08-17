package yuzunyannn.elementalsorcery.util.element;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.crafting.IDataSensitivity;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.util.NBTTag;

public class ElementInventoryMerge implements IElementInventory {

	final List<IElementInventory> others;

	public ElementInventoryMerge(List<IElementInventory> einvs) {
		this.others = einvs;
	}

	@Override
	public void markDirty() {
		for (IElementInventory einv : others) einv.markDirty();
	}

	@Override
	public void applyUse() {
		for (IElementInventory einv : others) einv.applyUse();
	}

	@Override
	public IElementInventory assign(IElementInventory other) {
		for (IElementInventory einv : others) einv.assign(other);
		return this;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for (IElementInventory einv : others) list.appendTag(einv.serializeNBT());
		nbt.setTag("eils", list);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		NBTTagList list = nbt.getTagList("eils", NBTTag.TAG_COMPOUND);
		int length = Math.min(others.size(), list.tagCount());
		for (int i = 0; i < length; i++) {
			others.get(i).deserializeNBT(list.getCompoundTagAt(i));
		}
	}

	@Override
	public boolean hasState(NBTTagCompound nbt) {
		return nbt.hasKey("eils");
	}

	@Override
	public void loadState(NBTTagCompound nbt) {
		this.deserializeNBT(nbt);
	}

	@Override
	public void saveState(NBTTagCompound nbt) {
	}

	@Override
	public int getSlots() {
		int slot = 0;
		for (IElementInventory einv : others) slot += einv.getSlots();
		return slot;
	}

	@Override
	public ElementStack getStackInSlot(int slot) {
		for (IElementInventory einv : others) {
			if (slot >= einv.getSlots()) slot -= einv.getSlots();
			else return einv.getStackInSlot(slot);
		}
		return ElementStack.EMPTY;
	}

	@Override
	public ElementStack setStackInSlot(int slot, ElementStack estack) {
		for (IElementInventory einv : others) {
			if (slot >= einv.getSlots()) slot -= einv.getSlots();
			else return einv.setStackInSlot(slot, estack);
		}
		return ElementStack.EMPTY;
	}

	@Override
	public boolean insertElement(int slot, ElementStack estack, boolean simulate) {
		for (IElementInventory einv : others) {
			if (slot >= einv.getSlots()) slot -= einv.getSlots();
			else {
				if (einv.insertElement(slot, estack, simulate)) {
					if (!simulate) onChange(einv, estack, false);
					return true;
				}
				return false;
			}
		}
		return false;
	}

	@Override
	public ElementStack extractElement(int slot, ElementStack estack, boolean simulate) {
		for (IElementInventory einv : others) {
			if (slot >= einv.getSlots()) slot -= einv.getSlots();
			else {
				estack = einv.extractElement(slot, estack, simulate);
				if (!simulate) onChange(einv, estack, true);
				return estack;
			}
		}
		return ElementStack.EMPTY;
	}

	@Override
	public boolean insertElement(ElementStack estack, boolean simulate) {
		for (IElementInventory einv : others) {
			if (einv.insertElement(estack, simulate)) {
				if (!simulate) onChange(einv, estack, false);
				return true;
			}
		}
		return false;
	}

	@Override
	public ElementStack extractElement(ElementStack estack, boolean simulate) {
		if (estack.isEmpty()) return ElementStack.EMPTY.copy();
		ElementStack ret = ElementStack.EMPTY.copy();
		ElementStack tmp = estack.copy();
		for (IElementInventory einv : others) {
			ElementStack _new = einv.extractElement(tmp, simulate);
			if (!simulate) onChange(einv, _new, true);
			ret.growOrBecome(_new);
			if (ret.arePowerfulAndMoreThan(estack)) return ret;
			else tmp.grow(-_new.getCount());
		}
		return ret;
	}

	@Override
	public void addInformation(World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		for (IElementInventory einv : others) einv.addInformation(worldIn, tooltip, flagIn);
	}

	@Override
	public IElementInventory setSensor(IDataSensitivity sensor) {
		for (IElementInventory einv : others) einv.setSensor(sensor);
		return this;
	}

	protected void onChange(IElementInventory einv, ElementStack eStack, boolean extract) {
		
	}

}
