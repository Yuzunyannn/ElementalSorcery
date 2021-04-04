package yuzunyannn.elementalsorcery.crafting.element;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.crafting.IToElement;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.api.crafting.IToElementItem;

// 默认的接口转化
public class DefaultInterfaceToElement implements IToElement {

	@Override
	public IToElementInfo toElement(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof IToElementItem) return ((IToElementItem) item).toElement(stack);
		return null;
	}

}
