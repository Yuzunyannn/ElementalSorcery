package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.network.MessageSyncContainer.IContainerNetwork;
import yuzunyannn.elementalsorcery.util.item.ItemStackHandlerInventory;

public class ContainerElfApplyAddressPlate extends ContainerElf implements IContainerNetwork {

	ItemStackHandlerInventory inventory = new ItemStackHandlerInventory(6);
	NBTTagCompound addressData;

	public ContainerElfApplyAddressPlate(EntityPlayer player) {
		super(player);
		// 物品栏
		this.addPlayerSlot(8, 84);
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
		nbt.setBoolean("apply", true);
		this.sendToServer(nbt);
	}

	@Override
	public void recvData(NBTTagCompound nbt, Side side) {
		if (side == Side.CLIENT) return;
		addressData = nbt;
		this.changeUI(ESGuiHandler.GUI_ELF_TALK);
	}

	@Override
	public NBTTagCompound getShiftData() {
		return addressData;
	}

}
