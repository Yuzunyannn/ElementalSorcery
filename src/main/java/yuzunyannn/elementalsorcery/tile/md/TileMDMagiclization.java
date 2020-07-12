package yuzunyannn.elementalsorcery.tile.md;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;

public class TileMDMagiclization extends TileMDBase implements ITickable {

	/** 初始化仓库 */
	@Override
	protected ItemStackHandler initItemStackHandler() {
		return new ItemStackHandler(1) {
			@Override
			@Nonnull
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				if (TileMDAbsorbBox.getElementInventory(stack) != null) return super.insertItem(slot, stack, simulate);
				return stack;
			}
		};
	}

	@Override
	public int getMaxCapacity() {
		return 2000;
	}

	@Override
	public void update() {
		this.autoTransfer();
		if (this.world.isRemote) return;
		if (this.tick % 10 != 0) return;
		ItemStack stack = inventory.getStackInSlot(0);
		if (stack.isEmpty()) return;
		final int need = 3;
		if (need > this.getCurrentCapacity()) return;
		if (this.getCurrentCapacity() >= this.getMaxCapacity()) return;
		IElementInventory inventory = TileMDAbsorbBox.getElementInventory(stack);
		if (inventory == null) return;
		ElementStack estack = getFirstNotEmpty(inventory);
		if (estack.isEmpty()) return;
		estack = estack.splitStack(Math.min(5, estack.getCount()));
		estack = estack.becomeMagic(world);
		this.magicShrink(need);
		this.magic.grow(estack);
		inventory.saveState(stack);
		this.markDirty();
	}

	// 获取仓库里，第一个不是空的元素
	protected static ElementStack getFirstNotEmpty(IElementInventory inventory) {
		if (inventory == null) return ElementStack.EMPTY;
		for (int i = 0; i < inventory.getSlots(); i++) {
			ElementStack estack = inventory.getStackInSlot(i);
			if (!estack.isEmpty()) return estack;
		}
		return ElementStack.EMPTY;
	}

}
