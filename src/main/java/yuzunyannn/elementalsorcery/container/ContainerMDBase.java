package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import yuzunyannn.elementalsorcery.tile.md.TileMDBase;

public class ContainerMDBase<T extends TileMDBase> extends ContainerNormal<T> {

	public ContainerMDBase(EntityPlayer player, T tileEntity) {
		super(player, tileEntity);
	}

	protected void addSlotItemHandler(EnumFacing facing, int index, int x, int y) {
		IItemHandler items = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
		if (items == null)
			return;
		this.addSlotToContainer(new SlotItemHandler(items, index, x, y));
		this.moreSlots++;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		this.tileEntity.detectAndSendChanges(this, this.listeners);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data) {
		this.tileEntity.setField(id, data);
	}

}
