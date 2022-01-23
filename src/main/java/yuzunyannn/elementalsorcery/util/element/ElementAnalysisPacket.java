package yuzunyannn.elementalsorcery.util.element;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;

public class ElementAnalysisPacket implements INBTSerializable<NBTTagCompound>, IToElementInfo {

	public ElementStack[] daEstacks = null;
	public ItemStack daStack = ItemStack.EMPTY;
	public int daComplex = 0;

	public ElementAnalysisPacket() {

	}

	public ElementAnalysisPacket(ElementStack[] daEstacks, int daComplex) {
		this.daEstacks = daEstacks;
		this.daComplex = daComplex;
	}

	public ElementAnalysisPacket(NBTTagCompound nbt) {
		this.deserializeNBT(nbt);
	}

	public void merge(IToElementInfo other) {
		if (other == null) return;
		ElementStack[] otherEstacks = ElementHelper.copy(other.element());
		if (otherEstacks == null || otherEstacks.length == 0) return;
		this.daEstacks = ElementHelper.merge(this.daEstacks, otherEstacks);
		this.daComplex = Math.max(this.daComplex, other.complex());
	}

	public void merge(ElementStack... estacks2) {
		this.daEstacks = ElementHelper.merge(this.daEstacks, estacks2);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("complex", daComplex);
		nbt.setTag("item", daStack.serializeNBT());
		nbt.setTag("einv", new ElementInventory(daEstacks).serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		daComplex = nbt.getInteger("complex");
		daStack = new ItemStack(nbt.getCompoundTag("item"));
		daEstacks = new ElementInventory(nbt.getCompoundTag("einv")).getEStacksAndClear();
	}

	@Override
	public ElementStack[] element() {
		return daEstacks;
	}

	@Override
	public int complex() {
		return daComplex;
	}
}
