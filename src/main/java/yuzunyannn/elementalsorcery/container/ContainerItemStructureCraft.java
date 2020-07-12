package yuzunyannn.elementalsorcery.container;

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import yuzunyannn.elementalsorcery.api.crafting.IItemStructure;
import yuzunyannn.elementalsorcery.crafting.element.ItemStructure;
import yuzunyannn.elementalsorcery.tile.TileItemStructureCraft;
import yuzunyannn.elementalsorcery.util.RandomHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ContainerItemStructureCraft extends Container {

	final public TileItemStructureCraft tileEntity;
	final public BlockPos pos;
	final public EntityPlayer player;

	public static ContainerItemStructureCraft vest() {
		return new ContainerItemStructureCraft();
	}

	public ContainerItemStructureCraft(EntityPlayer player, TileEntity tileEntity) {
		this.tileEntity = (TileItemStructureCraft) tileEntity;
		this.player = player;
		this.pos = tileEntity.getPos();
		this.tileEntity.initGui(this);
	}

	protected ContainerItemStructureCraft() {
		this.player = null;
		this.tileEntity = null;
		this.pos = null;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return playerIn.getDistanceSq(this.tileEntity.getPos()) <= 64
				&& player.world.getTileEntity(this.pos) == this.tileEntity;
	}

	public void addPlayerSlot(int xoff, int yoff) {
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

	public void addInputSlot(IItemHandler inv, int index, int x, int y) {
		this.addSlotToContainer(new IOSlotItemHandler(inv, index, x, y));
	}

	public void addOutputSlot(IItemHandler inv, int index, int x, int y) {
		this.addSlotToContainer(new IOSlotItemHandler(inv, index, x, y));
	}

	public class IOSlotItemHandler extends SlotItemHandler {

		public IOSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
			super(itemHandler, index, xPosition, yPosition);
		}

		@Override
		public void putStack(ItemStack stack) {
			super.putStack(stack);
			tileEntity.onSlotChange(ContainerItemStructureCraft.this);
		}

	}

	protected int dragMode = -1;
	protected final Set<Slot> dragSlots = Sets.<Slot>newHashSet();

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		ItemStack retItem = ItemStack.EMPTY;
		InventoryPlayer inventoryplayer = player.inventory;
		ItemStack dragStack = inventoryplayer.getItemStack();
		// 不同类型代表的含义不尽相同
		int dragEvent = getDragEvent(dragType);
		// 点击类型，pickup就是普通左键或右键点击某个槽位
		if (clickTypeIn == ClickType.PICKUP || clickTypeIn == ClickType.QUICK_MOVE) {
			if (slotId >= 0) {
				Slot slot = this.getSlot(slotId);

				ItemStack ioBack = null;
				if (slot instanceof IOSlotItemHandler) {
					ioBack = dragStack.copy();
					// 特殊槽位，替換物品
					if (!dragStack.isEmpty()) {
						IItemStructure is = ItemStructure.getItemStructure(dragStack);
						if (!is.isEmpty())
							dragStack = is.getStructureItem(RandomHelper.rand.nextInt(is.getItemCount()));
					}
				}
				ItemStack slotStack = slot.getStack();
				if (!ItemHelper.merge(slotStack, dragStack, dragEvent == 1 ? 1 : -1)) {
					if (dragEvent == 1 && dragStack.isEmpty())
						dragStack = slotStack.splitStack(slotStack.getCount() / 2);
					else if (dragEvent == 1 && slotStack.isEmpty()) slotStack = dragStack.splitStack(1);
					else {
						retItem = dragStack;
						dragStack = slotStack;
						slotStack = retItem;
					}
				}
				if (ioBack != null) dragStack = ioBack;
				slot.putStack(slotStack);
				inventoryplayer.setItemStack(dragStack);
				retItem = dragStack;
			} else {
				if (!dragStack.isEmpty()) {
					player.dropItem(dragStack, true);
					inventoryplayer.setItemStack(retItem);
				}
			}
		} else if (clickTypeIn == ClickType.QUICK_CRAFT) {
			if (dragEvent == 0) this.dragMode = extractDragMode(dragType);
			else if (dragEvent == 1 && slotId >= 0) dragSlots.add(this.getSlot(slotId));
			else if (dragEvent == 2) {
				int n = dragStack.getCount();
				for (Slot slot : dragSlots) {
					ItemStack dragStackCopy = dragStack.copy();
					// 特殊槽位，替换物品
					if (slot instanceof IOSlotItemHandler && !dragStackCopy.isEmpty()) {
						IItemStructure is = ItemStructure.getItemStructure(dragStack);
						if (!is.isEmpty())
							dragStackCopy = is.getStructureItem(RandomHelper.rand.nextInt(is.getItemCount()));
					}
					int j3 = slot.getHasStack() ? slot.getStack().getCount() : 0;
					computeStackSize(dragSlots, dragMode, dragStackCopy, j3);
					int k3 = Math.min(dragStackCopy.getMaxStackSize(), slot.getItemStackLimit(dragStackCopy));
					if (dragStackCopy.getCount() > k3) dragStackCopy.setCount(k3);
					if (slot instanceof IOSlotItemHandler == false) n -= dragStackCopy.getCount() - j3;
					slot.putStack(dragStackCopy);
				}
				dragStack.setCount(n);
				inventoryplayer.setItemStack(dragStack);
				retItem = dragStack;
				dragSlots.clear();
			}
		} else if (clickTypeIn == ClickType.PICKUP_ALL) {
			for (Slot slot : this.inventorySlots) {
				// io槽不允许
				if (slot instanceof IOSlotItemHandler) continue;
				ItemStack slotStack = slot.getStack();
				if (dragStack.getCount() >= dragStack.getMaxStackSize()) break;
				if (ItemHelper.merge(dragStack, slotStack, -1))
					if (dragStack.getCount() >= dragStack.getMaxStackSize()) break;
			}
			inventoryplayer.setItemStack(dragStack);
			retItem = dragStack;
		}
		return retItem;
	}
}
