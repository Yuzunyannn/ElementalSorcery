package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import yuzunyannn.elementalsorcery.tile.TileInfusionBox;

public class ContainerInfusionBox extends ContainerNormal<TileInfusionBox> {

	public ContainerInfusionBox(EntityPlayer player, TileEntity tileEntity) {
		super(player, (TileInfusionBox) tileEntity);
		IItemHandler items;

		items = this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		this.addSlotToContainer(new SlotItemHandler(items, 0, 78, 61));

		items = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
		this.addSlotToContainer(new SlotItemHandler(items, 0, 53, 21));
		this.addSlotToContainer(new SlotItemHandler(items, 1, 102, 21));
		this.addSlotToContainer(new SlotItemHandler(items, 2, 23, 40));
		this.addSlotToContainer(new SlotItemHandler(items, 3, 132, 40));
		this.addSlotToContainer(new SlotItemHandler(items, 4, 48, 58));
		this.addSlotToContainer(new SlotItemHandler(items, 5, 107, 58));
		this.moreSlots = 7;
	}
}
