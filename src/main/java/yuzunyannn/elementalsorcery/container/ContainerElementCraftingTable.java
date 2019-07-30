package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import yuzunyannn.elementalsorcery.tile.altar.TileElementCraftingTable;

public class ContainerElementCraftingTable extends Container {

	public final TileElementCraftingTable tileEntity;
	public final boolean isBig;
	private ItemStackHandler result = new ItemStackHandler(1);

	public ContainerElementCraftingTable(EntityPlayer player, TileEntity tileEntity) {
		this.tileEntity = (TileElementCraftingTable) tileEntity;
		// 玩家物品栏
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 36 + j * 18, 160 + i * 18));
			}
		}
		// 玩家工具栏
		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(player.inventory, i, 36 + i * 18, 218));
		}
		IItemHandler items = this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		for (int i = 0; i < items.getSlots(); i++) {
			int x = ContainerParchment.crafting_relative[i * 2];
			int y = ContainerParchment.crafting_relative[i * 2 + 1];
			this.addSlotToContainer(new SlotItemHandler(items, i, 89 + x, 58 + y) {
				@Override
				public void onSlotChanged() {
					ContainerElementCraftingTable.this
							.onCraftMatrixChanged(ContainerElementCraftingTable.this.tileEntity);
					super.onSlotChanged();
				}
			});
		}
		this.addSlotToContainer(new SlotItemHandler(result, 0, 107, 130) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return false;
			}

			@Override
			public boolean canTakeStack(EntityPlayer playerIn) {
				return false;
			}

		});
		this.onCraftMatrixChanged(this.tileEntity);
		this.isBig = items.getSlots() > 9;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return playerIn.getDistanceSq(this.tileEntity.getPos()) <= 64;
	}

	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn) {
		this.tileEntity.onCraftMatrixChanged();
		this.result.setStackInSlot(0, this.tileEntity.getOutput());
		super.onCraftMatrixChanged(inventoryIn);
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
		if (index < 27) {
			isMerged = mergeItemStack(new_stack, 27, 36, false);
		} else if (index >= 27 && index < 36) {
			isMerged = mergeItemStack(new_stack, 0, 27, false);
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

}
