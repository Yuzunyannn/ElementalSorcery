package yuzunyannn.elementalsorcery.util.item;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

public class ItemStackLimitHandler extends ItemStackHandler {
	public ItemStackLimitHandler() {
		super();
	}

	public ItemStackLimitHandler(int size) {
		super(size);
	}

	public ItemStackLimitHandler(NonNullList<ItemStack> stacks) {
		super(stacks);
	}

	public ItemStack insertItemForce(int slot, @Nonnull ItemStack stack, boolean simulate) {
		return super.insertItem(slot, stack, simulate);
	}

	public ItemStack extractItemForce(int slot, int amount, boolean simulate) {
		return super.extractItem(slot, amount, simulate);
	}

	public boolean canInsert(int slot, @Nonnull ItemStack stack) {
		return true;
	}

	public boolean canextract(int slot, int amount) {
		return true;
	}

	@Override
	@Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (this.canInsert(slot, stack))
			return super.insertItem(slot, stack, simulate);
		return stack;
	}

	@Override
	@Nonnull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (this.canextract(slot, amount))
			return super.extractItem(slot, amount, simulate);
		return ItemStack.EMPTY;
	}
}
