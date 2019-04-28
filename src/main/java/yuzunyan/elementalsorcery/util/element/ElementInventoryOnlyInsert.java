package yuzunyan.elementalsorcery.util.element;

import javax.annotation.Nonnull;

import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.capability.ElementInventory;

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
