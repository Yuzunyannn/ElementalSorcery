package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import yuzunyannn.elementalsorcery.tile.TileSmeltBox;

public class ContainerSmeltBox extends ContainerNormal<TileSmeltBox> {
	public TileSmeltBox getTileEntity() {
		return tileEntity;
	}

	public ContainerSmeltBox(EntityPlayer player, TileEntity tileEntity) {
		super(player, (TileSmeltBox) tileEntity);
		IItemHandler items;
		items = this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		// 烧炼位
		for (int i = 0; i < 2; i++) {
			this.addSlotToContainer(new SlotItemHandler(items, i, 29 + 22 * i, 26));
		}
		// 产出位
		items = this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
		for (int i = 0; i < 2; i++) {
			this.addSlotToContainer(new SlotItemHandler(items, i, 103 + 22 * i, 26) {
				@Override
				public boolean isItemValid(ItemStack stack) {
					return false;
				}

				@Override
				public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
					int i = (int) ContainerSmeltBox.this.tileEntity.getAndClearExp();
					while (i > 0) {
						int k = EntityXPOrb.getXPSplit(i);
						i -= k;
						thePlayer.world.spawnEntity(new EntityXPOrb(thePlayer.world, thePlayer.posX,
								thePlayer.posY + 0.5D, thePlayer.posZ + 0.5D, k));
					}
					return super.onTake(thePlayer, stack);
				}
			});
		}
		// 附加物品位
		this.addSlotToContainer(new SlotItemHandler(items, 2, 114, 46) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return false;
			}
		});
		// 额外槽
		if (this.tileEntity.canUseExtraItem()) {
			this.addSlotToContainer(new SlotItemHandler(this.tileEntity.getExtraItemStackHandler(), 0, 77, 46));
		}
		this.moreSlots = 2;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (int j = 0; j < this.listeners.size(); ++j) {
			((IContainerListener) this.listeners.get(j)).sendWindowProperty(this, TileSmeltBox.FIELD_BURN_TIME,
					tileEntity.getField(TileSmeltBox.FIELD_BURN_TIME));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		tileEntity.setField(id, data);
	}

}
