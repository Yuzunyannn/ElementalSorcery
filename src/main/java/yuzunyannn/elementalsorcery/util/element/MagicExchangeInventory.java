package yuzunyannn.elementalsorcery.util.element;

import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;

public class MagicExchangeInventory extends ElementInventory {

	public MagicExchangeInventory() {
		super(1);
	}

	@Override
	public boolean insertElement(ElementStack estack, boolean simulate) {
		return this.insertElement(0, estack, simulate);
	}

	@Override
	public ElementStack extractElement(ElementStack estack, boolean simulate) {
		return super.extractElement(0, estack, simulate);
	}

	@Override
	public boolean insertElement(int slot, ElementStack estack, boolean simulate) {
		if (!estack.isMagic()) {
			estack = estack.copy();
			estack = estack.becomeMagic(null);
		}
		return super.insertElement(slot, estack, simulate);
	}

	@Override
	public ElementStack extractElement(int slot, ElementStack estack, boolean simulate) {
		if (!estack.isMagic()) return ElementStack.EMPTY.copy();
		return super.extractElement(slot, estack, simulate);
	}
}
