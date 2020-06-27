package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.tile.TileRiteTable;
import yuzunyannn.elementalsorcery.util.item.ItemStackHandlerInventory;

public class ContainerRiteManual extends Container {

	public IInventory slot = new ItemStackHandlerInventory(1);
	private final World world;
	private final BlockPos pos;
	private final EntityPlayer player;

	private int lPower = 0;
	private int lLevel = 0;
	public int power = 0;
	public int level = 0;

	public ContainerRiteManual(InventoryPlayer playerInventory, World worldIn, BlockPos posIn) {
		this.world = worldIn;
		this.pos = posIn;
		this.player = playerInventory.player;

		final int xoff = 48;
		final int yoff = 90;
		// 玩家背包
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, xoff + j * 18, yoff + i * 18));
			}
		}
		// 玩家工具栏
		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(player.inventory, i, xoff + i * 18, 58 + yoff));
		}

		// 查询槽
		this.addSlotToContainer(new Slot(slot, 0, 70, 21) {
			@Override
			public void onSlotChanged() {
				super.onSlotChanged();
				if (player.world.isRemote) return;
				ItemStack stack = this.inventory.getStackInSlot(0);
				if (stack.isEmpty()) {
					power = 0;
					return;
				}
				int power = TileRiteTable.sacrifice.getPower(stack);
				ContainerRiteManual.this.power = power;
				if (power == 0) return;
				ContainerRiteManual.this.level = TileRiteTable.sacrifice.getLevel(stack);
			}
		});

	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (lPower != power) {
			lPower = power;
			for (int j = 0; j < listeners.size(); ++j) {
				listeners.get(j).sendWindowProperty(this, 0, power);
			}
		}
		if (lLevel != level) {
			lLevel = level;
			for (int j = 0; j < listeners.size(); ++j) {
				listeners.get(j).sendWindowProperty(this, 1, level);
			}
		}
	}

	@Override
	public void updateProgressBar(int id, int data) {
		if (id == 0) power = data;
		else level = data;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return playerIn.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D,
				(double) this.pos.getZ() + 0.5D) <= 64.0D;
	}

	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		if (!this.world.isRemote) this.clearContainer(playerIn, this.world, this.slot);
	}

	public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
		return slotIn.inventory != this.slot;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		Slot slot = inventorySlots.get(index);

		if (slot == null || !slot.getHasStack()) { return ItemStack.EMPTY; }

		ItemStack new_stack = slot.getStack();
		ItemStack old_stack = new_stack.copy();

		boolean isMerged = false;
		final int max = 36 + 1;
		if (index < 27)
			isMerged = mergeItemStack(new_stack, 36, max, false) || mergeItemStack(new_stack, 27, 36, false);
		else if (index >= 27 && index < 36)
			isMerged = mergeItemStack(new_stack, 36, max, false) || mergeItemStack(new_stack, 0, 27, false);
		else isMerged = mergeItemStack(new_stack, 0, 36, false);

		if (!isMerged) { return ItemStack.EMPTY; }

		if (new_stack.isEmpty()) slot.putStack(ItemStack.EMPTY);
		slot.onTake(playerIn, new_stack);

		return old_stack;
	}

}
