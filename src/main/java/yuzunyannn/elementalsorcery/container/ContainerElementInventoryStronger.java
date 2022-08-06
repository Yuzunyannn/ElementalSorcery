package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.tile.ICanSync;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.container.gui.GuiElementInventoryStronger;
import yuzunyannn.elementalsorcery.event.EventServer;
import yuzunyannn.elementalsorcery.network.MessageSyncContainer.IContainerNetwork;
import yuzunyannn.elementalsorcery.util.ContainerArrayDetecter;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.element.ElementInventoryStronger;

public class ContainerElementInventoryStronger extends Container implements IContainerNetwork {

	final public TileEntity tileEntity;
	final public EntityPlayer player;
	final public BlockPos pos;
	public ElementInventoryStronger stronger;
	public Object guiObj;
	public ContainerArrayDetecter<ElementStack, NBTTagIntArray> detecter = new ContainerArrayDetecter<>();

	public ContainerElementInventoryStronger(EntityPlayer player, TileEntity tileEntity) {
		this.tileEntity = tileEntity;
		this.player = player;
		this.pos = this.tileEntity.getPos();
		try {
			this.stronger = (ElementInventoryStronger) ElementHelper.getElementInventory(tileEntity);
		} catch (ClassCastException e) {
			this.stronger = null;
		}
		if (player.world.isRemote) return;
		if (this.stronger == null) return;
		if (tileEntity instanceof ICanSync) ((ICanSync) tileEntity).updateToClient();
		else {
			EventServer.addTask(() -> {
				updateAllInventoryToClient();
			});
		}
	}

	public void updateAllInventoryToClient() {
		NBTTagCompound nbt = this.stronger.serializeNBT();
		this.sendToClient(nbt, player);
	}

	@Override
	public void recvData(NBTTagCompound nbt, Side side) {
		if (stronger == null) return;
		if (side == Side.CLIENT) {
			if (nbt.hasKey("cl")) {
				detecter.recvChangeList(nbt.getTagList("cl", NBTTag.TAG_COMPOUND), stronger);
				this.updateGUI();
				return;
			}
			stronger.deserializeNBT(nbt);
			this.updateGUI();
			return;
		}
		updateInventory(nbt);
	}

	protected void updateInventory(NBTTagCompound dat) {
		stronger.setLowerLimit(dat.getInteger("ll"));
		stronger.setUpperLimit(dat.getInteger("lu"));
		tileEntity.markDirty();
		if (tileEntity.getWorld().isRemote) return;
		if (tileEntity instanceof ICanSync) ((ICanSync) tileEntity).updateToClient();
		else updateAllInventoryToClient();
	}

	public void updateInventoryToServer(NBTTagCompound dat) {
		sendToServer(dat);
		updateInventory(dat);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		NBTTagList changeList = detecter.detecte(stronger);
		if (!changeList.isEmpty()) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setTag("cl", changeList);
			this.sendToClient(nbt, player);
		}
	}

	@SideOnly(Side.CLIENT)
	public void updateGUI() {
		if (guiObj instanceof GuiElementInventoryStronger) ((GuiElementInventoryStronger) guiObj).refreshData();
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return playerIn.getDistanceSq(this.tileEntity.getPos()) <= 64
				&& player.world.getTileEntity(this.pos) == this.tileEntity && this.stronger != null;
	}

}
