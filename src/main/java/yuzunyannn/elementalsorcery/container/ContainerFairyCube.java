package yuzunyannn.elementalsorcery.container;

import java.lang.ref.WeakReference;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.entity.FairyCubeModule;
import yuzunyannn.elementalsorcery.entity.fcube.EntityFairyCube;
import yuzunyannn.elementalsorcery.logics.EventServer;
import yuzunyannn.elementalsorcery.network.MessageSyncContainer.IContainerNetwork;

public class ContainerFairyCube extends Container implements IContainerNetwork {

	private static WeakReference<EntityFairyCube> fairyCubeWhenOpenGui;

	public static void setFairyCubeContext(EntityFairyCube fairyCube) {
		fairyCubeWhenOpenGui = new WeakReference(fairyCube);
	}

	public static EntityFairyCube getFairyCubeContext() {
		return fairyCubeWhenOpenGui == null ? null : fairyCubeWhenOpenGui.get();
	}

	public static EntityFairyCube popFairyCubeContext() {
		if (fairyCubeWhenOpenGui == null) return null;
		EntityFairyCube fairyCube = fairyCubeWhenOpenGui.get();
		fairyCubeWhenOpenGui = null;
		return fairyCube;
	}

	public EntityFairyCube fairyCube;
	final public EntityPlayer player;
	public int updateVersion = 0;

	public ContainerFairyCube(EntityPlayer player) {
		this.player = player;
		fairyCube = popFairyCubeContext();
		World world = player.world;
		if (world.isRemote) return;

		if (fairyCube == null) return;
		EventServer.addTask(() -> {
			NBTTagCompound nbt = new NBTTagCompound();
			fairyCube.writeFairyCubeToNBT(nbt);
			nbt.setInteger("cubeId", fairyCube.getEntityId());
			this.sendToClient(nbt, player);
		});
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		if (fairyCube != null) fairyCube.talkGui = null;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		if (playerIn.world.isRemote) return true;
		return fairyCube != null && !fairyCube.isDead;
	}

	public void sendChangeStatus(int index, int key) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("I", (byte) index);
		nbt.setByte("K", (byte) key);
		this.sendToServer(nbt);
	}

	@Override
	public void recvData(NBTTagCompound nbt, Side side) {
		if (side == Side.CLIENT) {
			if (nbt.hasKey("cubeId")) {
				updateVersion++;
				fairyCube = (EntityFairyCube) player.world.getEntityByID(nbt.getInteger("cubeId"));
				if (fairyCube != null) fairyCube.readFairyCubeFromNBT(nbt);
			}
			if (nbt.hasKey("S")) {
				int status = nbt.getInteger("S");
				int index = nbt.getInteger("I");
				try {
					FairyCubeModule module = fairyCube.getModules().get(index);
					module.setStatus(status);
					updateVersion++;
				} catch (Exception e) {}
			}
		} else {
			try {
				int index = nbt.getInteger("I");
				FairyCubeModule module = fairyCube.getModules().get(index);
				int status = module.getCurrStatus();
				module.onClickOnGUI(nbt.getByte("K"), this.player);
				if (module.getCurrStatus() != status) {
					NBTTagCompound data = new NBTTagCompound();
					data.setByte("S", (byte) module.getCurrStatus());
					data.setByte("I", (byte) index);
					this.sendToClient(data, player);
				}
			} catch (Exception e) {}
		}
	}

	public void closeContainer() {
		player.closeScreen();
	}

	public void changeContainer(int modGuiId) {
		this.closeContainer();
		BlockPos pos = player.getPosition();
		player.openGui(ElementalSorcery.instance, modGuiId, player.world, pos.getX(), pos.getY(), pos.getZ());
	}
}
