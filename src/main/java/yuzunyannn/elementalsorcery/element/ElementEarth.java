package yuzunyannn.elementalsorcery.element;

import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.init.ESInit;

public class ElementEarth extends ElementCommon {

	public ElementEarth() {
		super(0x785439, "earth");
	}

	@Override
	public int complexWith(ItemStack stack, ElementStack estack, ElementStack other) {
		if (other.getElement() == ESInit.ELEMENTS.METAL) return 10;
		return super.complexWith(stack, estack, other);
	}
}
