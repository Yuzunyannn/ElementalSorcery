package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
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
import yuzunyannn.elementalsorcery.util.element.ElementAnalysisPacket;

public class ContainerAnalysisAltar extends ContainerNormal<TileAnalysisAltar> implements IContainerNetwork {

	public ContainerAnalysisAltar(EntityPlayer player, TileEntity tileEntity) {
		super(player, (TileAnalysisAltar) tileEntity);
		IItemHandler items = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		this.addSlotToContainer(new SlotItemHandler(items, 0, 136, 40));
		this.moreSlots = 1;
	}

	int lastPower = -1;
	ElementAnalysisPacket lastAns;
	boolean lastError;

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);
		ElementAnalysisPacket ans = tileEntity.getAnalysisPacket();
		if (ans != null) this.sendToClient(ans.serializeNBT(), listener);
		else {
			if (lastError) {} else {
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setBoolean("clear", true);
				this.sendToClient(nbt, listener);
			}
		}
	}

	@Override
	public void recvData(NBTTagCompound nbt, Side side) {
		if (side != Side.CLIENT) return;
		if (nbt.hasKey("error")) tileEntity.setCannotAnalysisStack(new ItemStack(nbt.getCompoundTag("error")));
		else if (nbt.hasKey("clear")) tileEntity.setAnalysisPacket(null);
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

		ElementAnalysisPacket ans = tileEntity.getAnalysisPacket();
		if (ans != lastAns) {
			lastAns = ans;
			if (ans == null) {
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setBoolean("clear", true);
				this.sendToClient(nbt, listeners);
			} else this.sendToClient(ans.serializeNBT(), listeners);
		}

		if (lastError != tileEntity.cannotAnalysis()) {
			lastError = tileEntity.cannotAnalysis();
			if (lastError) {
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setTag("error", tileEntity.getCannotAnalysisStack().serializeNBT());
				this.sendToClient(nbt, listeners);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		tileEntity.setPowerTime(data);
	}
}
