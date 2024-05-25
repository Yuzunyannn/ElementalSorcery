package yuzunyannn.elementalsorcery.util.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import yuzunyannn.elementalsorcery.util.helper.JavaHelper;

public class ItemStackHandlerLinker implements IItemHandlerModifiable {

	protected class Linker {
		Consumer<ItemStack> setter;
		Supplier<ItemStack> getter;
		Function<ItemStack, Boolean> checker;
	}

	protected List<Linker> linkers = new ArrayList<>();

	public void addLinker(Supplier<ItemStack> getter, Consumer<ItemStack> setter) {
		addLinker(getter, setter);
	}

	public void addLinker(Supplier<ItemStack> getter, Consumer<ItemStack> setter,
			Function<ItemStack, Boolean> checker) {
		Linker linker = new Linker();
		linker.getter = getter;
		linker.setter = setter;
		linker.checker = checker;
		linkers.add(linker);
	}

	@Override
	public int getSlots() {
		return linkers.size();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if (slot < 0 || slot >= linkers.size()) return ItemStack.EMPTY;
		return linkers.get(slot).getter.get();
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		if (slot < 0 || slot >= linkers.size()) return;
		linkers.get(slot).setter.accept(stack);
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
		return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) return ItemStack.EMPTY;
		if (slot < 0 || slot >= linkers.size()) return stack;
		
		Function<ItemStack, Boolean> checker = linkers.get(slot).checker;
		if (checker != null && !JavaHelper.isTrue(checker.apply(stack))) return stack;

		ItemStack existing = getStackInSlot(slot);
		int limit = getStackLimit(slot, stack);

		if (!existing.isEmpty()) {
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) return stack;
			limit -= existing.getCount();
		}

		if (limit <= 0) return stack;

		boolean reachedLimit = stack.getCount() > limit;

		if (!simulate) {
			if (existing.isEmpty())
				setStackInSlot(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
			else existing.grow(reachedLimit ? limit : stack.getCount());
			onContentsChanged(slot);
		}

		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount == 0) return ItemStack.EMPTY;
		if (slot < 0 || slot >= linkers.size()) return ItemStack.EMPTY;

		ItemStack existing = getStackInSlot(slot);
		if (existing.isEmpty()) return ItemStack.EMPTY;

		int toExtract = Math.min(amount, existing.getMaxStackSize());

		if (existing.getCount() <= toExtract) {
			if (!simulate) {
				setStackInSlot(slot, ItemStack.EMPTY);
				onContentsChanged(slot);
			}
			return existing;
		} else {
			if (!simulate) {
				setStackInSlot(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
				onContentsChanged(slot);
			}
			return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
		}
	}

	protected void onContentsChanged(int slot) {

	}
}
