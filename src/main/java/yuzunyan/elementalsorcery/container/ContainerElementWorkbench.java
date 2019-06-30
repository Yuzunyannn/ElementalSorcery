package yuzunyan.elementalsorcery.container;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyan.elementalsorcery.api.ability.IElementInventory;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.api.util.ElementHelper;
import yuzunyan.elementalsorcery.crafting.RecipeManagement;
import yuzunyan.elementalsorcery.init.ESInitInstance;

public class ContainerElementWorkbench extends Container {

	private yuzunyan.elementalsorcery.api.crafting.IRecipe irecipe = null;
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	public InventoryCrafting craftElement = new InventoryCrafting(this, 1, 1);
	public InventoryCraftResult craftResult = new InventoryCraftResult();
	private final World world;
	private final BlockPos pos;
	private final EntityPlayer player;

	public ContainerElementWorkbench(InventoryPlayer playerInventory, World worldIn, BlockPos posIn) {
		this.world = worldIn;
		this.pos = posIn;
		this.player = playerInventory.player;

		// 产出
		this.addSlotToContainer(
				new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 124, 35) {
					@Override
					public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
						if (irecipe == null)
							return super.onTake(thePlayer, stack);
						this.onCrafting(stack);
						boolean last_have = irecipe != null;
						// 减少元素，减少物品
						ItemStack stack_ = craftElement.getStackInSlot(0);
						IElementInventory inventory = ElementHelper.getElementInventory(stack_);
						List<ElementStack> elist = irecipe.getNeedElements();
						for (ElementStack estack : elist)
							inventory.extractElement(estack, false);
						inventory.saveState(stack_);
						irecipe.shrink(craftMatrix);
						craftElement.setInventorySlotContents(0, stack_);
						if (!world.isRemote && last_have) {
							detectAndSendChanges();
						}
						return stack;
					}
				});

		// 左边的九个
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18) {
					@Override
					public void onSlotChanged() {
						onCraftMatrixChanged(craftMatrix);
						super.onSlotChanged();
					}
				});
			}
		}

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

		// 元素槽
		this.addSlotToContainer(new Slot(craftElement, 0, 92, 53));
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		if (this.world.getBlockState(this.pos).getBlock() != ESInitInstance.BLOCKS.ELEMENT_WORKBENCH) {
			return false;
		} else {
			return playerIn.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D,
					(double) this.pos.getZ() + 0.5D) <= 64.0D;
		}
	}

	public void onCraftMatrixChanged(IInventory inventoryIn) {
		ItemStack stack = this.doElementCrafting();
		if (!stack.isEmpty()) {
			if (!this.world.isRemote) {
				this.craftResult.setInventorySlotContents(0, stack);
				EntityPlayerMP entityplayermp = (EntityPlayerMP) this.player;
				entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, 0, stack));
			}
			return;
		}
		this.irecipe = null;
		this.slotChangedCraftingGrid(this.world, this.player, this.craftMatrix, this.craftResult);
	}

	private ItemStack doElementCrafting() {
		ItemStack itemstack = ItemStack.EMPTY;
		irecipe = RecipeManagement.instance.findMatchingRecipe(craftMatrix, this.world);
		if (irecipe == null)
			return ItemStack.EMPTY;
		List<ElementStack> elist = irecipe.getNeedElements();
		if (elist != null && !elist.isEmpty()) {
			ItemStack stack = this.craftElement.getStackInSlot(0);
			IElementInventory inventory = ElementHelper.getElementInventory(stack);
			if (!ElementHelper.canExtract(inventory))
				return ItemStack.EMPTY;
			for (ElementStack estack : elist) {
				ElementStack get_stack = inventory.extractElement(estack, true);
				if (!get_stack.arePowerfulAndMoreThan(estack))
					return ItemStack.EMPTY;
			}
		}
		itemstack = irecipe.getCraftingResult(this.craftMatrix).copy();
		return itemstack;
	}

	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);

		if (!this.world.isRemote) {
			this.clearContainer(playerIn, this.world, this.craftElement);
			this.clearContainer(playerIn, this.world, this.craftMatrix);
		}
	}

	public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
		return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
	}

	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index == 0) {
				itemstack1.getItem().onCreated(itemstack1, this.world, playerIn);

				if (!this.mergeItemStack(itemstack1, 10, 46, true)) {
					return ItemStack.EMPTY;
				}
				slot.onSlotChange(itemstack1, itemstack);
			} else if (index >= 10 && index < 37) {
				if (!this.mergeItemStack(itemstack1, 37, 46, false)) {
					return ItemStack.EMPTY;
				}
			} else if (index >= 37 && index < 46) {
				if (!this.mergeItemStack(itemstack1, 10, 37, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 10, 46, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);

			if (index == 0) {
				playerIn.dropItem(itemstack2, false);
			}
		}

		return itemstack;
	}

}
