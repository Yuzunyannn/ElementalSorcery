package yuzunyannn.elementalsorcery.util.element;

import javax.annotation.Nonnull;

import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.capability.ElementInventory;

public class ElementInventoryOnlyInsert extends ElementInventory {

	protected boolean forbid = true;

	public ElementInventoryOnlyInsert() {
		super();
	}

	public ElementInventoryOnlyInsert(int slots) {
		super(slots);
	}

	public ElementInventoryOnlyInsert setForbid(boolean forbid) {
		this.forbid = forbid;
		return this;
	}

	@Override
	public ElementStack extractElement(ElementStack estack, boolean simulate) {
		if (forbid) return ElementStack.EMPTY;
		return super.extractElement(estack, simulate);
	}

	@Override
	public ElementStack extractElement(int slot, @Nonnull ElementStack estack, boolean simulate) {
		if (forbid) return ElementStack.EMPTY;
		return super.extractElement(slot, estack, simulate);
	}
}
