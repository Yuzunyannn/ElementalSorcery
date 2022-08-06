package yuzunyannn.elementalsorcery.tile.md;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;

public class TileMDMagicSolidify extends TileMDBase implements ITickable {

	@Override
	protected ItemStackHandler initItemStackHandler() {
		return new ItemStackHandler(1) {
			@Override
			@Nonnull
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				if (stack.hasCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, EnumFacing.NORTH))
					return super.insertItem(slot, stack, simulate);
				return stack;
			}
		};
	}

	static final public ItemStack MAGIC_STONE = new ItemStack(ESObjects.ITEMS.MAGIC_STONE);
	static final public ItemStack MAGIC_PIECE = new ItemStack(ESObjects.ITEMS.MAGIC_PIECE);

	@Override
	public void update() {
		// this.autoTransfer();
		if (tick % 10 != 0) return;
		if (this.magic.isEmpty()) return;
		ItemStack stack = inventory.getStackInSlot(0);
		if (stack.isEmpty() || stack.getItem() == MAGIC_STONE.getItem() || stack.getItem() == MAGIC_PIECE.getItem()) {
			if (this.world.isRemote) return;
			this.solidify();
			return;
		}
		IElementInventory inventory = ElementHelper.getElementInventory(stack);
		if (inventory == null) return;
		if (ElementHelper.canInsert(inventory) == false) return;
		ElementStack estack = this.magic.splitStack(Math.min(10, this.magic.getCount()));
		if (inventory.insertElement(estack, false)) {
			if (this.world.isRemote) return;
			inventory.saveState(stack);
			this.markDirty();
		} else this.magic.grow(estack);
	}

	private void solidify() {
		if (this.magic.getPower() >= 25) {

			if (this.magic.getCount() >= 100) {

				if (this.insertStack(MAGIC_STONE)) {
					this.magic.shrink(100);
					this.markDirty();
				}

			}

		} else if (this.magic.getCount() >= 50) {

			if (this.insertStack(MAGIC_PIECE)) {
				this.magic.shrink(20);
				this.markDirty();
			}

		}
	}

	private boolean insertStack(final ItemStack STACK) {
		ItemStack stack = this.inventory.getStackInSlot(0);
		if (stack.isEmpty()) {
			this.inventory.setStackInSlot(0, STACK.copy());
			return true;
		}
		if (STACK.isItemEqual(stack)) {
			if (64 - stack.getCount() >= STACK.getCount()) {
				stack.grow(STACK.getCount());
				return true;
			}
		}
		return false;
	}
}
