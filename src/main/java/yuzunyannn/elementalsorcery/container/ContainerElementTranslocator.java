package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import yuzunyannn.elementalsorcery.api.tile.IAltarWake;
import yuzunyannn.elementalsorcery.container.gui.GuiElementTranslocator;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.event.EventServer;
import yuzunyannn.elementalsorcery.network.MessageSyncContainer.IContainerNetwork;
import yuzunyannn.elementalsorcery.tile.altar.TileElementTranslocator;
import yuzunyannn.elementalsorcery.util.NBTHelper;
import yuzunyannn.elementalsorcery.util.NBTTag;

public class ContainerElementTranslocator extends ContainerNormal<TileElementTranslocator>
		implements IContainerNetwork {

	@SideOnly(Side.CLIENT)
	public GuiElementTranslocator gui;

	public ContainerElementTranslocator(EntityPlayer player, TileEntity tileEntity) {
		super(player, (TileElementTranslocator) tileEntity);

		ItemStackHandler items = this.tileEntity.getItemStackHandler();
		this.addSlotToContainer(new SlotItemHandler(items, 0, 26, 39));
		this.addSlotToContainer(new SlotItemHandler(items, 1, 134, 39));
		this.moreSlots = 2;

		if (player.world.isRemote) return;
		EventServer.addTask(() -> {
			NBTTagCompound ret = NBTHelper.serializeElementStackForSend(this.tileEntity.getElementStack());
			sendToClient(ret, this.player);
		});
	}

	public ItemStackHandler getItemStackHandler() {
		return this.tileEntity.getItemStackHandler();
	}

	@Override
	public void recvData(NBTTagCompound nbt, Side side) {
		if (side == Side.CLIENT) {
			recvDataClient(nbt);
			return;
		}

		if (!nbt.hasKey("id", NBTTag.TAG_NUMBER)) return;
		byte id = nbt.getByte("id");
		int count = nbt.getInteger("c");

		boolean result = false;
		if (id == IAltarWake.OBTAIN) result = tileEntity.doTransferInput(count);
		else if (id == IAltarWake.SEND) result = tileEntity.doTransferOutput(count);

		if (result) {
			NBTTagCompound ret = NBTHelper.serializeElementStackForSend(tileEntity.getElementStack());
			ret.setByte("cid", id);
			this.sendToClient(ret, listeners);
		}

	}

	@SideOnly(Side.CLIENT)
	public void recvDataClient(NBTTagCompound nbt) {
		if (gui == null) return;
		ElementStack estack = NBTHelper.deserializeElementStackFromSend(nbt);
		gui.updateElement(nbt.hasKey("cid") ? nbt.getByte("cid") : -1, estack);
	}

}
