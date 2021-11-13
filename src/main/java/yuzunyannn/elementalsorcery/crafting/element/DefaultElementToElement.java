package yuzunyannn.elementalsorcery.crafting.element;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.crafting.IToElement;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;

public class DefaultElementToElement implements IToElement {

	@Override
	public IToElementInfo toElement(ItemStack stack) {
		IElementInventory eInv = ElementHelper.getElementInventory(stack);
		if (eInv == null) return null;

		ItemStack newStack = stack.copy();
		eInv = ElementHelper.getElementInventory(newStack);

		List<ElementStack> elements = new ArrayList<>(eInv.getSlots());
		for (int i = 0; i < eInv.getSlots(); i++) {
			ElementStack estack = eInv.getStackInSlot(i);
			if (estack.isEmpty()) continue;
			elements.add(estack);
			eInv.setStackInSlot(i, ElementStack.EMPTY);
		}

		if (elements.isEmpty()) return null;
		eInv.saveState(newStack);

		return ToElementInfoStatic.create(1, newStack, elements);
	}

}
