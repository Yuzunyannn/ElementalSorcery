package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import yuzunyannn.elementalsorcery.tile.TileSmeltBox;

public class ContainerSmeltBox extends Container {
	protected TileSmeltBox tile_entity;

	public TileSmeltBox getTileEntity() {
		return tile_entity;
	}

	public ContainerSmeltBox(EntityPlayer player, TileEntity tileEntity) {
		tile_entity = (TileSmeltBox) tileEntity;
		// 玩家物品栏
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		// 玩家工具栏
		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 142));
		}
		IItemHandler items;
		items = tile_entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		// ��ӷ�����Ʒ�Ĵ���
		for (int i = 0; i < 2; i++) {
			this.addSlotToContainer(new SlotItemHandler(items, i, 29 + 22 * i, 26));
		}
		// ��ӽ����Ʒ�Ĵ���
		items = tile_entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
		for (int i = 0; i < 2; i++) {
			this.addSlotToContainer(new SlotItemHandler(items, i, 103 + 22 * i, 26) {
				@Override
				public boolean isItemValid(ItemStack stack) {
					return false;
				}
			});
		}
		this.addSlotToContainer(new SlotItemHandler(items, 2, 114, 46) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return false;
			}
		});
		if (tile_entity.canUseExtraItem()) {
			this.addSlotToContainer(new SlotItemHandler(tile_entity.getExtraItemStackHandler(), 0, 77, 46));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return playerIn.getDistanceSq(this.tile_entity.getPos()) <= 64;
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
			isMerged = mergeItemStack(new_stack, 36, 38, false) || mergeItemStack(new_stack, 27, 36, false);
		} else if (index >= 27 && index < 36) {
			isMerged = mergeItemStack(new_stack, 36, 38, false) || mergeItemStack(new_stack, 0, 27, false);
		} else {
			isMerged = mergeItemStack(new_stack, 0, 36, false);
		}

		if (!isMerged) {
			return ItemStack.EMPTY;
		}

		slot.onTake(playerIn, new_stack);

		return old_stack;
	}

	// ����������Ϣ
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (int j = 0; j < this.listeners.size(); ++j) {
			((IContainerListener) this.listeners.get(j)).sendWindowProperty(this, TileSmeltBox.FIELD_BURN_TIME,
					tile_entity.getField(TileSmeltBox.FIELD_BURN_TIME));
		}
	}

	// �ͻ��˽���Ϣ
	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		tile_entity.setField(id, data);
	}

}
