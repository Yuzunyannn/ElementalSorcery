package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.network.MessageSyncContainer.IContainerNetwork;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.item.ItemStackHandlerInventory;

public class ContainerElfSendParcel extends ContainerElf implements IContainerNetwork {

	ItemStackHandlerInventory inventory = new ItemStackHandlerInventory(6);
	NBTTagCompound mailData;

	public ContainerElfSendParcel(EntityPlayer player) {
		super(player);
		// 物品栏
		this.addPlayerSlot(8, 84);
		// 打包栏
		for (int i = 0; i < 6; ++i) {
			this.addSlotToContainer(new Slot(inventory, i, 34 + i * 18, 44));
		}
		moreSlots = 6;
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		this.clearContainer(playerIn, playerIn.world, inventory);
	}

	@SideOnly(Side.CLIENT)
	public void submit(String adress) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("address", adress);
		this.sendToServer(nbt);
	}

	@Override
	public void recvData(NBTTagCompound nbt, Side side) {
		if (side == Side.CLIENT) return;
		mailData = nbt;
		NBTHelper.setItemList(nbt, "items", inventory.getNonemptyListAndClear());
		this.changeUI(ESGuiHandler.GUI_ELF_TALK);
	}

	@Override
	public NBTTagCompound getShiftData() {
		return mailData;
	}

}
