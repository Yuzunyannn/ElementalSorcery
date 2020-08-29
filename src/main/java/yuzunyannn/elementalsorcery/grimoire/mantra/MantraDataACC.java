package yuzunyannn.elementalsorcery.grimoire.mantra;

import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.ICaster;

public class MantraDataACC extends MantraDataEffect {


	public MantraDataACC(ICaster caster) {
		super(caster);
	}

	protected ElementInventory elementInv = new ElementInventory(4);

	public void store(ElementStack estack) {
		if (estack.isEmpty()) return;
		elementInv.insertElement(estack, false);
	}

	public ElementStack gain(Element element) {
		return gain(new ElementStack(element));
	}

	public ElementStack gain(ElementStack estack) {
		for (int i = 0; i < elementInv.getSlots(); i++) {
			ElementStack origin = elementInv.getStackInSlot(i);
			if (origin.areSameType(estack)) return origin;
		}
		return ElementStack.EMPTY;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		elementInv.saveState(nbt);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		elementInv.loadState(nbt);
	}

}
