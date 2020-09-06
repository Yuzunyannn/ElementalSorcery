package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import yuzunyannn.elementalsorcery.tile.altar.TileTranscribeInjection;

public class ContainerTranscribeInjection extends ContainerNormal<TileTranscribeInjection> {

	public ContainerTranscribeInjection(EntityPlayer player, TileEntity tileEntity) {
		super(player, (TileTranscribeInjection) tileEntity, 8, 27);
		IItemHandler items = this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
				EnumFacing.NORTH);
		for (int i = 0; i < items.getSlots(); i++)
			this.addSlotToContainer(new SlotItemHandler(items, i, 23 + 22 * i, 108));
		this.moreSlots = items.getSlots();
	}

}
