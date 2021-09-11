package yuzunyannn.elementalsorcery.util.element;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;

public class ElementInventoryLimit extends ElementInventory {

	protected int upperLimit;
	protected int lowerLimit;

	public ElementInventoryLimit() {
		super();
	}

	public ElementInventoryLimit(int slots) {
		super(slots);
	}

	public ElementInventoryLimit setUpperLimit(int upper) {
		if (getLowerLimit() == 0) this.upperLimit = upper;
		else this.upperLimit = upper == 0 ? 0 : Math.max(upper, getLowerLimit());
		return this;
	}

	public ElementInventoryLimit setLowerLimit(int lower) {
		if (getUpperLimit() == 0) this.lowerLimit = lower;
		else this.lowerLimit = lower == 0 ? 0 : Math.min(lower, getUpperLimit());
		return this;
	}

	public int getUpperLimit() {
		return upperLimit;
	}

	public int getLowerLimit() {
		return lowerLimit;
	}

	public boolean meetCondition(ElementStack estack) {
		if (estack.isEmpty()) return true;
		if (getUpperLimit() > 0 && estack.getPower() > getUpperLimit()) return false;
		if (getLowerLimit() > 0 && estack.getPower() < getLowerLimit()) return false;
		return true;
	}

	@Override
	public boolean insertElement(int slot, ElementStack estack, boolean simulate) {
		if (!meetCondition(estack)) return false;
		return super.insertElement(slot, estack, simulate);
	}

	@Override
	public void writeCustomDataToNBT(NBTTagCompound nbt) {
		super.writeCustomDataToNBT(nbt);
		if (upperLimit > 0) nbt.setInteger("upper", upperLimit);
		if (lowerLimit > 0) nbt.setInteger("lower", lowerLimit);
	}

	@Override
	public void readCustomDataFromNBT(NBTTagCompound nbt) {
		super.readCustomDataFromNBT(nbt);
		upperLimit = nbt.getInteger("upper");
		lowerLimit = nbt.getInteger("lower");
	}

	@Override
	public void addInformation(World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		TextFormatting c = TextFormatting.YELLOW;
		if (getUpperLimit() > 0) tooltip.add(c + I18n.format("info.elementCube.limit.upper", getUpperLimit()));
		if (getLowerLimit() > 0) tooltip.add(c + I18n.format("info.elementCube.limit.lower", getLowerLimit()));
		super.addInformation(worldIn, tooltip, flagIn);
	}

}
