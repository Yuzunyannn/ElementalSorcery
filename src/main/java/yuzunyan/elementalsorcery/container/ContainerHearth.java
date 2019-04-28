package yuzunyan.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import yuzunyan.elementalsorcery.tile.TileHearth;
import yuzunyan.elementalsorcery.util.IField;

public class ContainerHearth extends Container implements IField {

	protected TileHearth tile_entity;

	public ContainerHearth(EntityPlayer player, TileEntity tileEntity) {
		tile_entity = (TileHearth) tileEntity;
		// 玩家背包
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		// 玩家工具栏
		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 142));
		}
		IItemHandler items = tile_entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
		// 添加放置物品的窗口
		for (int i = 0; i < 4; i++) {
			this.addSlotToContainer(new SlotItemHandler(items, i, 33 + 31 * i, 44));
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
			isMerged = mergeItemStack(new_stack, 36, 40, false) || mergeItemStack(new_stack, 27, 36, false);
		} else if (index >= 27 && index < 36) {
			isMerged = mergeItemStack(new_stack, 36, 40, false) || mergeItemStack(new_stack, 0, 27, false);
		} else {
			isMerged = mergeItemStack(new_stack, 0, 36, false);
		}

		if (!isMerged) {
			return ItemStack.EMPTY;
		}

		slot.onTake(playerIn, new_stack);

		return old_stack;
	}

	// 服务器发消息
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (int j = 0; j < this.listeners.size(); ++j) {
			((IContainerListener) this.listeners.get(j)).sendWindowProperty(this, TileHearth.FIELD_BURN_TIME,
					tile_entity.getField(TileHearth.FIELD_BURN_TIME));
			((IContainerListener) this.listeners.get(j)).sendWindowProperty(this, TileHearth.FIELD_TOTAL_BURN_TIME,
					tile_entity.getField(TileHearth.FIELD_TOTAL_BURN_TIME));
		}
	}

	// 客户端接消息
	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		tile_entity.setField(id, data);
	}

	@Override
	public int getField(int id) {
		return tile_entity.getField(id);
	}

	@Override
	public void setField(int id, int value) {
		tile_entity.setField(id, value);
	}

	@Override
	public int getFieldCount() {
		return tile_entity.getFieldCount();
	}

	// 获取名字
	public String getUnlocalizedName() {
		return  tile_entity.getBlockUnlocalizedName();
	}

}
