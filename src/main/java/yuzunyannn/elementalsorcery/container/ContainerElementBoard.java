package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.item.ItemElementBoard;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.item.ItemStackHandlerInventory;

public class ContainerElementBoard extends Container {

	public IInventory slot = new ItemStackHandlerInventory(1);

	public final BlockPos pos;
	public final EntityPlayer player;

	public ElementStack sample = ElementStack.EMPTY;
	public ElementStack[] checkResults;

	public ContainerElementBoard(EntityPlayer player) {
		this.pos = player.getPosition();
		this.player = player;

		InventoryPlayer inv = player.inventory;
		for (int i = 0; i < 36; ++i) {
			Vec3i p = getPositionWithIndex(i);
			Slot slot;

			if (inv.getStackInSlot(i).getItem() instanceof ItemElementBoard)
				slot = new Slot(inv, i, 13 + p.getX(), 13 + p.getY()) {
					@Override
					public boolean canTakeStack(EntityPlayer playerIn) {
						return false;
					}
				};
			else slot = new Slot(inv, i, 13 + p.getX(), 13 + p.getY()) {
				@Override
				public void onSlotChanged() {
					super.onSlotChanged();
					onCenterSoltChange();
				}
			};
			this.addSlotToContainer(slot);
		}
		// 查询槽
		this.addSlotToContainer(new Slot(slot, 0, 85, 85) {
			@Override
			public void onSlotChanged() {
				super.onSlotChanged();
				onCenterSoltChange();
			}
		});
	}

	public static Vec3i getPositionWithIndex(int i) {
		if (i < 4) return new Vec3i((i - 0) * 18, 144, 0);
		else if (i < 8) return new Vec3i((i - -1) * 18, 144 - 18 * 0, 0);
		else if (i < 11) return new Vec3i((i - 8) * 18, 144 - 18 * 1, 0);
		else if (i < 13) return new Vec3i((i - 4) * 18, 144 - 18 * 1, 0);
		else if (i < 14) return new Vec3i((i - 13) * 18, 144 - 18 * 2, 0);
		else if (i < 16) return new Vec3i((i - 7) * 18, 144 - 18 * 2, 0);
		else if (i < 17) return new Vec3i((i - 16) * 18, 144 - 18 * 3, 0);
		else if (i < 18) return new Vec3i((i - 9) * 18, 144 - 18 * 3, 0);
		else if (i < 19) return new Vec3i((i - 18) * 18, 144 - 18 * 5, 0);
		else if (i < 20) return new Vec3i((i - 11) * 18, 144 - 18 * 5, 0);
		else if (i < 22) return new Vec3i((i - 20) * 18, 144 - 18 * 6, 0);
		else if (i < 23) return new Vec3i((i - 14) * 18, 144 - 18 * 6, 0);
		else if (i < 25) return new Vec3i((i - 23) * 18, 144 - 18 * 7, 0);
		else if (i < 28) return new Vec3i((i - 19) * 18, 144 - 18 * 7, 0);
		else if (i < 32) return new Vec3i((i - 28) * 18, 144 - 18 * 8, 0);
		else return new Vec3i((i - 27) * 18, 144 - 18 * 8, 0);
	}

	protected void onCenterSoltChange() {
		if (!player.world.isRemote) return;

		ItemStack stack = slot.getStackInSlot(0);
		if (stack.isEmpty()) {
			checkResults = null;
			return;
		}

		sample = ElementStack.EMPTY;

		IElementInventory einv = ElementHelper.getElementInventory(stack);
		if (einv == null) {
			ElementStack[] estacks = ElementMap.instance.toElementStack(stack);
			if (estacks != null && estacks.length > 0) sample = estacks[0];
		} else sample = einv.getStackInSlot(0);

		if (sample.isEmpty()) {
			checkResults = null;
			return;
		}

		checkResults = new ElementStack[36];
		InventoryPlayer inv = player.inventory;
		for (int i = 0; i < checkResults.length; ++i) {
			checkResults[i] = ElementStack.EMPTY;
			ItemStack verified = inv.getStackInSlot(i);
			if (verified.isEmpty()) continue;

			IToElementInfo info = ElementMap.instance.toElement(verified);
			if (info == null) continue;
			ElementStack[] estacks = info.element();
			for (ElementStack eStack : estacks) {
				if (eStack.areSameType(sample)) {
					checkResults[i] = eStack;
					break;
				}
			}
		}

	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return playerIn.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D,
				(double) this.pos.getZ() + 0.5D) <= 64.0D;
	}

	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		if (player.world.isRemote) return;
		this.clearContainer(player, player.world, this.slot);
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
