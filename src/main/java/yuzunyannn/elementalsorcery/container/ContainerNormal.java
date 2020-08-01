package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class ContainerNormal<T extends TileEntity> extends Container {

	final public T tileEntity;
	final public EntityPlayer player;
	final public BlockPos pos;
	protected int moreSlots = 0;

	public ContainerNormal(EntityPlayer player, T tileEntity) {
		this(player, tileEntity, 8, 84);
	}

	public ContainerNormal(EntityPlayer player, T tileEntity, boolean addPlayerInventory) {
		this(player, tileEntity, addPlayerInventory, 8, 84);
	}

	public ContainerNormal(EntityPlayer player, T tileEntity, int xoff, int yoff) {
		this(player, tileEntity, true, xoff, yoff);
	}

	public ContainerNormal(EntityPlayer player, T tileEntity, boolean addPlayerInventory, int xoff, int yoff) {
		this.tileEntity = tileEntity;
		this.player = player;
		this.pos = this.tileEntity.getPos();
		if (addPlayerInventory) this.addPlayerSlot(xoff, yoff);
	}

	void addPlayerSlot(int xoff, int yoff) {
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

		if (slot == null || !slot.getHasStack()) { return ItemStack.EMPTY; }

		ItemStack newStack = slot.getStack();
		ItemStack oldStack = newStack.copy();

		boolean isMerged = false;
		final int max = 36 + moreSlots;
		if (index < 27) isMerged = mergeItemStack(newStack, 36, max, false) || mergeItemStack(newStack, 27, 36, false);
		else if (index >= 27 && index < 36)
			isMerged = mergeItemStack(newStack, 36, max, false) || mergeItemStack(newStack, 0, 27, false);
		else isMerged = mergeItemStack(newStack, 0, 36, false);

		if (!isMerged) return ItemStack.EMPTY;

		if (newStack.isEmpty()) slot.putStack(ItemStack.EMPTY);
		slot.onTake(playerIn, newStack);

		return oldStack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return playerIn.getDistanceSq(this.tileEntity.getPos()) <= 64
				&& player.world.getTileEntity(this.pos) == this.tileEntity;
	}

}
