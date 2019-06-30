package yuzunyan.elementalsorcery.container;

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
import yuzunyan.elementalsorcery.tile.TileDeconstructBox;

public class ContainerDeconstructBox extends Container {

	public final TileDeconstructBox tile_entity;

	public ContainerDeconstructBox(EntityPlayer player, TileEntity tileEntity) {
		tile_entity = (TileDeconstructBox) tileEntity;
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
		// 接受工具
		items = tile_entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
		this.addSlotToContainer(new SlotItemHandler(items, 0, 107, 37));
		// 任意物品
		items = tile_entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		this.addSlotToContainer(new SlotItemHandler(items, 0, 55, 37));
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
		final int max = 36 + 2;
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

	int last_power = -1;

	// 发消息
	@Override
	public void detectAndSendChanges() {
		// 全局同步
		super.detectAndSendChanges();
		// 燃烧
		int power = tile_entity.getField(TileDeconstructBox.FIELD_POWER);
		if (power != last_power) {
			last_power = power;
			for (int j = 0; j < this.listeners.size(); ++j) {
				((IContainerListener) this.listeners.get(j)).sendWindowProperty(this, TileDeconstructBox.FIELD_POWER,
						tile_entity.getField(TileDeconstructBox.FIELD_POWER));
			}
		}
	}

	// 接受消息
	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		tile_entity.setField(id, data);
	}

}
