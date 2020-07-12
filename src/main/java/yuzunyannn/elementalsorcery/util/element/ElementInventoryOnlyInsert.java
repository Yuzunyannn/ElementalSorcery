package yuzunyannn.elementalsorcery.util.element;

import javax.annotation.Nonnull;

import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;

public class ElementInventoryOnlyInsert extends ElementInventory {

	public ElementInventoryOnlyInsert() {
		super();
	}

	public ElementInventoryOnlyInsert(int slots) {
		super(slots);
	}

	@Override
	public ElementStack extractElement(ElementStack estack, boolean simulate) {
		return estack;
	}

	@Override
	public ElementStack extractElement(int slot, @Nonnull ElementStack estack, boolean simulate) {
		return estack;
	}
}
