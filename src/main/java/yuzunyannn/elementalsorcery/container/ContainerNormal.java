package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerNormal<T extends TileEntity> extends Container {

	final public T tileEntity;
	final public EntityPlayer player;
	protected int moreSlots = 0;

	public ContainerNormal(EntityPlayer player, T tileEntity) {
		this(player, tileEntity, 8, 84);
	}

	public ContainerNormal(EntityPlayer player, T tileEntity, int xoff, int yoff) {
		this.tileEntity = tileEntity;
		this.player = player;
		// 玩家物品栏
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, xoff + j * 18, yoff + i * 18));
			}
		}
		// 玩家工具栏
		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(player.inventory, i, xoff + i * 18, yoff + 58));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		Slot slot = inventorySlots.get(index);

		if (slot == null || !slot.getHasStack()) {
			return ItemStack.EMPTY;
		}

		ItemStack new_stack = slot.getStack();
		ItemStack old_stack = new_stack.copy();

		boolean isMerged = false;
		final int max = 36 + moreSlots;
		if (index < 27) {
			isMerged = mergeItemStack(new_stack, 36, max, false) || mergeItemStack(new_stack, 27, 36, false);
		} else if (index >= 27 && index < 36) {
			isMerged = mergeItemStack(new_stack, 36, max, false) || mergeItemStack(new_stack, 0, 27, false);
		} else {
			isMerged = mergeItemStack(new_stack, 0, 36, false);
		}

		if (!isMerged) {
			return ItemStack.EMPTY;
		}

		if (new_stack.isEmpty())
			slot.putStack(ItemStack.EMPTY);
		slot.onTake(playerIn, new_stack);

		return old_stack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return playerIn.getDistanceSq(this.tileEntity.getPos()) <= 64;
	}

}
