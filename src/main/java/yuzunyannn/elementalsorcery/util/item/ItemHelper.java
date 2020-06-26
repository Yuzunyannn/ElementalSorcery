package yuzunyannn.elementalsorcery.util.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ItemHelper {

	static public boolean areItemsEqual(ItemStack stackA, ItemStack stackB) {
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
}
