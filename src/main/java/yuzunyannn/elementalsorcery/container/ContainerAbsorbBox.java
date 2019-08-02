package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import yuzunyannn.elementalsorcery.tile.TileAbsorbBox;

public class ContainerAbsorbBox extends ContainerNormal<TileAbsorbBox> {

	public ContainerAbsorbBox(EntityPlayer player, TileEntity tileEntity) {
		super(player, (TileAbsorbBox) tileEntity);
		IItemHandler items = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		this.addSlotToContainer(new SlotItemHandler(items, 0, 80, 34));
		this.moreSlots = 1;

	}
}
