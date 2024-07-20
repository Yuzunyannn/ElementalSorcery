package yuzunyannn.elementalsorcery.util.element;

import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;

public class ElementInventoryInfinite extends ElementInventoryAdapter {

	public int power = 1000;

	@Override
	public void markDirty() {

	}

	@Override
	public ElementStack getStackInSlot(int slot) {
		return new ElementStack(Element.REGISTRY.getValue(slot + 1), Short.MAX_VALUE, power);
	}

	@Override
	public int getSlots() {
		return Element.REGISTRY.getKeys().size() - 1;
	}

	@Override
	public boolean insertElement(int slot, ElementStack estack, boolean simulate) {
		return true;
	}
	
	@Override
	public ElementStack extractElement(int slot, ElementStack estack, boolean simulate) {
		return estack.copy();
	}

}
