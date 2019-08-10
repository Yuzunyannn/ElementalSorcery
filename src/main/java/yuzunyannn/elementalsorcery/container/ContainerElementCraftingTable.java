package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import yuzunyannn.elementalsorcery.tile.altar.TileElementCraftingTable;

public class ContainerElementCraftingTable extends ContainerNormal<TileElementCraftingTable> {
	public final boolean isBig;
	private ItemStackHandler result = new ItemStackHandler(1);

	public ContainerElementCraftingTable(EntityPlayer player, TileEntity tileEntity) {
		super(player, (TileElementCraftingTable) tileEntity, 36, 160);

		IItemHandler items = this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		for (int i = 0; i < items.getSlots(); i++) {
			int x = ContainerSupremeCraftingTable.craftingRelative[i * 2];
			int y = ContainerSupremeCraftingTable.craftingRelative[i * 2 + 1];
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
	public void onCraftMatrixChanged(IInventory inventoryIn) {
		this.tileEntity.onCraftMatrixChanged();
		this.result.setStackInSlot(0, this.tileEntity.getOutput());
		super.onCraftMatrixChanged(inventoryIn);
	}

}
