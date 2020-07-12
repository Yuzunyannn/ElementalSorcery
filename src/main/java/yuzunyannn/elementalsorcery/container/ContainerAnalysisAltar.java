package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import yuzunyannn.elementalsorcery.network.MessageSyncContainer.IContainerNetwork;
import yuzunyannn.elementalsorcery.tile.altar.TileAnalysisAltar;

public class ContainerAnalysisAltar extends ContainerNormal<TileAnalysisAltar> implements IContainerNetwork {

	public ContainerAnalysisAltar(EntityPlayer player, TileEntity tileEntity) {
		super(player, (TileAnalysisAltar) tileEntity);
		IItemHandler items = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		this.addSlotToContainer(new SlotItemHandler(items, 0, 136, 40));
		this.moreSlots = 1;
	}

	int lastPower = -1;
	TileAnalysisAltar.AnalysisPacket lastAns;

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);
		TileAnalysisAltar.AnalysisPacket ans = tileEntity.getAnalysisPacket();
		if (ans != null) this.sendToClient(ans.serializeNBT(), listener);
		else {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setBoolean("clear", true);
			this.sendToClient(nbt, listener);
		}
	}

	@Override
	public void recvData(NBTTagCompound nbt, Side side) {
		if (side != Side.CLIENT) return;
		if (nbt.hasKey("clear")) tileEntity.setAnalysisPacket(null);
		else tileEntity.setAnalysisPacket(nbt);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		int power = tileEntity.getPowerTime();
		if (power != lastPower) {
			lastPower = power;
			for (int j = 0; j < this.listeners.size(); ++j) {
				((IContainerListener) this.listeners.get(j)).sendWindowProperty(this, 0, tileEntity.getPowerTime());
			}
		}

		TileAnalysisAltar.AnalysisPacket ans = tileEntity.getAnalysisPacket();
		if (ans != lastAns) {
			lastAns = ans;
			if (ans == null) {
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setBoolean("clear", true);
				this.sendToClient(nbt, listeners);
			} else this.sendToClient(ans.serializeNBT(), listeners);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		tileEntity.setPowerTime(data);
	}
}
