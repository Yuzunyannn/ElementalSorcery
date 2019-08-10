package yuzunyannn.elementalsorcery.util.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

public class ItemHelper {

	static public boolean areItemsEqual(ItemStack stackA, ItemStack stackB) {
		return ItemStack.areItemsEqual(stackA, stackB) && ItemStack.areItemStackTagsEqual(stackA, stackB);
	}

	static public ItemStack toItemStack(IBlockState state) {
		return new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
	}
}
