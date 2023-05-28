package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.item.tool.ItemSimpleMaterialContainer.ItemSimpleMaterialContainerHandler;
import yuzunyannn.elementalsorcery.network.MessageSyncContainer.IContainerNetwork;

public class ContainerSimpleMaterialContainer extends Container implements IContainerNetwork {

	final public EntityPlayer player;
	final public ItemSimpleMaterialContainerHandler handler;
	final public ItemStack stack;
	public boolean isHanlderDirty = false;

	public ContainerSimpleMaterialContainer(EntityPlayer player) {
		this.player = player;

		ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
		if (stack.getItem() != ESObjects.ITEMS.SIMPLE_MATERIAL_CONTAINER) stack = player.getHeldItem(EnumHand.OFF_HAND);

		this.stack = stack;

		addPlayerSlot(8, 113);

		IItemHandler iHanlder = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		if (iHanlder instanceof ItemSimpleMaterialContainerHandler)
			handler = (ItemSimpleMaterialContainerHandler) iHanlder;
		else handler = null;

		if (handler == null) return;
	}

	@Override
	public void detectAndSendChanges() {
		for (int i = 0; i < 36; ++i) {
			Slot slot = this.inventorySlots.get(i);

			if (!slot.canTakeStack(player)) continue;

			ItemStack itemstack = slot.getStack();
			ItemStack itemstack1 = this.inventoryItemStacks.get(i);

			if (itemstack.getItem() == ESObjects.ITEMS.SIMPLE_MATERIAL_CONTAINER) continue;

			if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
				boolean clientStackChanged = !ItemStack.areItemStacksEqualUsingNBTShareTag(itemstack1, itemstack);
				itemstack1 = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack.copy();
				this.inventoryItemStacks.set(i, itemstack1);

				if (clientStackChanged) for (int j = 0; j < this.listeners.size(); ++j) {
					((IContainerListener) this.listeners.get(j)).sendSlotContents(this, i, itemstack1);
				}
			}
		}
	}

	private class ISlot extends Slot {

		public ISlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean canTakeStack(EntityPlayer playerIn) {
			return false;
		}
	}

	protected void addPlayerSlot(int xoff, int yoff) {
		// 玩家物品栏
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				int index = j + i * 9 + 9;
				if (player.inventory.getStackInSlot(index).getItem() == stack.getItem())
					this.addSlotToContainer(new ISlot(player.inventory, index, xoff + j * 18, yoff + i * 18));
				else this.addSlotToContainer(new Slot(player.inventory, index, xoff + j * 18, yoff + i * 18));
			}
		}
		// 玩家工具栏
		for (int i = 0; i < 9; ++i) {
			if (player.inventory.getStackInSlot(i).getItem() == stack.getItem())
				this.addSlotToContainer(new ISlot(player.inventory, i, xoff + i * 18, yoff + 58));
			else this.addSlotToContainer(new Slot(player.inventory, i, xoff + i * 18, yoff + 58));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return handler != null;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		Slot slot = inventorySlots.get(index);

		if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;

		ItemStack newStack = slot.getStack();
		ItemStack oldStack = newStack.copy();

		if (!newStack.isEmpty() && onClickBigSlotFast(0, newStack)) {
			newStack = ItemStack.EMPTY;
			slot.putStack(ItemStack.EMPTY);
			slot.onTake(playerIn, newStack);
			return oldStack;
		}

		boolean isMerged = false;
		final int max = 36;
		if (index < 27) isMerged = mergeItemStack(newStack, 36, max, false) || mergeItemStack(newStack, 27, 36, false);
		else if (index >= 27 && index < 36)
			isMerged = mergeItemStack(newStack, 36, max, false) || mergeItemStack(newStack, 0, 27, false);
		else isMerged = mergeItemStack(newStack, 0, 36, false);

		if (!isMerged) return ItemStack.EMPTY;

		if (newStack.isEmpty()) slot.putStack(ItemStack.EMPTY);
		slot.onTake(playerIn, newStack);

		return oldStack;
	}

	public boolean onClickBigSlotFast(int slot, ItemStack takeStack) {
		if (takeStack.isEmpty()) {
			ItemStack innerStack = handler.getStackInSlot(slot);
			if (innerStack.isEmpty()) return false;
			ItemStack stack = handler.extractItem(slot, innerStack.getMaxStackSize(), true);
			if (stack.isEmpty()) return false;
			ItemStack stackCopy = stack.copy();
			boolean isMerged = mergeItemStack(stack, 0, 36, false);
			if (isMerged) {
				handler.extractItem(slot, stackCopy.getCount() - stack.getCount(), false);
				handler.checkAndSupple(slot);
			}
			isHanlderDirty = true;
			return true;
		} else {
			if (!handler.isSimpleItem(takeStack)) return false;
			for (slot = 0; slot < handler.getSlots(); slot++) {
				ItemStack rest = handler.insertItem(slot, takeStack, true);
				if (!rest.isEmpty()) continue;
				handler.insertItem(slot, takeStack, false);
				handler.setAutoUseIndex(slot);
				isHanlderDirty = true;
				return true;
			}
		}
		return false;
	}

	public void onClickBigSlot(int slot) {
		ItemStack takeStack = player.inventory.getItemStack();
		if (takeStack.isEmpty()) {
			ItemStack innerStack = handler.getStackInSlot(slot);
			if (innerStack.isEmpty()) return;
			ItemStack stack = handler.extractItem(slot, innerStack.getMaxStackSize(), false);
			if (stack.isEmpty()) return;
			handler.checkAndSupple(slot);
			player.inventory.setItemStack(stack);
			player.inventory.markDirty();
			isHanlderDirty = true;

		} else {
			if (!handler.isSimpleItem(takeStack)) return;
			for (slot = 0; slot < handler.getSlots(); slot++) {
				ItemStack rest = handler.insertItem(slot, takeStack, true);
				if (!rest.isEmpty()) continue;
				handler.insertItem(slot, takeStack, false);
				handler.setAutoUseIndex(slot);
				player.inventory.setItemStack(ItemStack.EMPTY);
				player.inventory.markDirty();
				isHanlderDirty = true;
				break;
			}
		}
	}

	@Override
	public void recvData(NBTTagCompound nbt, Side side) {
		if (side == Side.CLIENT) return;
		int slot = nbt.getInteger("slot");
		if (nbt.getBoolean("fast")) onClickBigSlotFast(slot, ItemStack.EMPTY);
		else onClickBigSlot(slot);
	}

}
