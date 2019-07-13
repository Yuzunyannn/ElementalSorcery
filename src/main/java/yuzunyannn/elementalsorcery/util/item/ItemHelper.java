package yuzunyannn.elementalsorcery.util.item;

import net.minecraft.item.ItemStack;

public class ItemHelper {

	static public boolean areItemsEqual(ItemStack stackA, ItemStack stackB) {
		return ItemStack.areItemsEqual(stackA, stackB) && ItemStack.areItemStackTagsEqual(stackA, stackB);
	}
}
