package yuzunyannn.elementalsorcery.crafting.element;

import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.crafting.IToElement;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;

public class DefaultElementToElement implements IToElement {

	@Override
	public IToElementInfo toElement(ItemStack stack) {
		return ToElementInfoStatic.createWithElementContainer(stack.copy(), null);
	}

}
