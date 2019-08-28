package yuzunyannn.elementalsorcery.tile.md;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.init.ESInitInstance;

public class TileMDMagicSolidify extends TileMDBase implements ITickable {

	@Override
	protected ItemStackHandler initItemStackHandler() {
		return new ItemStackHandler(1) {
			@Override
			@Nonnull
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				return stack;
			}
		};
	}

	static final public ItemStack MAGIC_STONE = new ItemStack(ESInitInstance.ITEMS.MAGIC_STONE);
	static final public ItemStack MAGIC_PIECE = new ItemStack(ESInitInstance.ITEMS.MAGIC_PIECE);

	@Override
	public void update() {
		//this.autoTransfer();
		if (this.world.isRemote)
			return;
		if (this.magic.isEmpty())
			return;
		if (this.magic.getPower() >= 25) {
			if (this.magic.getCount() >= 100) {
				this.magic.shrink(100);
				this.insertStack(MAGIC_STONE);
				this.markDirty();
			}
		} else if (this.magic.getCount() >= 20) {
			this.magic.shrink(20);
			this.insertStack(MAGIC_PIECE);
			this.markDirty();
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
