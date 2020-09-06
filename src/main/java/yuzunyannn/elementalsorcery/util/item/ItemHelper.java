package yuzunyannn.elementalsorcery.util.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ItemHelper {

	static public boolean areItemsEqual(ItemStack stackA, ItemStack stackB) {
		if (stackA.isEmpty()) return stackB.isEmpty();
		return ItemStack.areItemsEqual(stackA, stackB) && ItemStack.areItemStackTagsEqual(stackA, stackB);
	}

	static public ItemStack toItemStack(IBlockState state) {
		return new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
	}

	static public boolean isEmpty(ItemStackHandler inventory) {
		for (int i = 0; i < inventory.getSlots(); i++) if (!inventory.getStackInSlot(i).isEmpty()) return false;
		return true;
	}

	static public void clear(ItemStackHandler inventory) {
		for (int i = 0; i < inventory.getSlots(); i++) inventory.setStackInSlot(i, ItemStack.EMPTY);
	}

	/** 将from添加到to中，返回值表示是否有变化 */
	static public boolean merge(ItemStack to, ItemStack from, int size) {
		if (to.getCount() >= to.getMaxStackSize() || from.isEmpty() || to.isEmpty()) return false;
		if (ItemHelper.areItemsEqual(to, from)) {
			if (size < 0) size = from.getCount();
			if (size + to.getCount() > to.getMaxStackSize()) size = to.getMaxStackSize() - to.getCount();
			from.grow(-size);
			to.setCount(to.getCount() + size);
			return true;
		}
		return false;
	}
}
