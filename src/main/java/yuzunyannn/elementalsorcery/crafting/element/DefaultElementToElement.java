package yuzunyannn.elementalsorcery.crafting.element;

import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.crafting.IToElement;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;

public class DefaultElementToElement implements IToElement {

	@Override
	public IToElementInfo toElement(ItemStack stack) {
		IElementInventory eInv = ElementHelper.getElementInventory(stack);
		if (eInv == null) return null;
		return ToElementInfoStatic.createWithElementContainer(stack.copy(), eInv);
	}

}
