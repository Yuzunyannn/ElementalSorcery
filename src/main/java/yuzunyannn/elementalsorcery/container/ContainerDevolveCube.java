package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.api.tile.IAltarWake;
import yuzunyannn.elementalsorcery.network.MessageSyncContainer.IContainerNetwork;
import yuzunyannn.elementalsorcery.tile.altar.TileDevolveCube;
import yuzunyannn.elementalsorcery.tile.altar.TileDevolveCube.DevolveData;
import yuzunyannn.elementalsorcery.util.ContainerArrayDetecter;
import yuzunyannn.elementalsorcery.util.ContainerMapDetecter;
import yuzunyannn.elementalsorcery.util.NBTTag;
import yuzunyannn.elementalsorcery.util.element.ElementStackDouble;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class ContainerDevolveCube extends Container implements IContainerNetwork {

	final public TileDevolveCube tile;
	final public EntityPlayer player;

	public ContainerDevolveCube(EntityPlayer player, TileEntity tileEntity) {
		this.tile = (TileDevolveCube) tileEntity;
		this.player = player;
	}

	ContainerMapDetecter<BlockPos, DevolveData, NBTTagIntArray, NBTTagInt> containerDetecter = new ContainerMapDetecter<>();
	ContainerArrayDetecter<ElementStackDouble, NBTTagCompound> exchangerDetecter = new ContainerArrayDetecter<>();

	public void updateToClient(boolean mapChange, boolean elementContainerChange, boolean exchangerChange) {
		if (player.world.isRemote) return;
		NBTTagCompound nbt = new NBTTagCompound();
		if (mapChange) nbt.setByteArray("cs", tile.getColors());
		if (elementContainerChange) {
			NBTTagList changeList = containerDetecter.detecte(tile.getCanMapDetected());
			if (!changeList.isEmpty()) nbt.setTag("cl", changeList);
		}
		if (exchangerChange) {
			NBTTagList changeList = exchangerDetecter.detecte(tile.getExchanger());
			if (!changeList.isEmpty()) nbt.setTag("ec", changeList);
		}
		this.sendToClient(nbt, player);
	}

	public long lastColorVersion;
	public long lastSelectedVersion;
	public long lastExchangerVersion;
	public int tick;

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (player.world.isRemote) return;
		tick++;
		tile.tryUpdateDetectionMark = true;
		boolean mapChange = false, elementContainerChange = false, exchangerChange = false;
		if (tile.colorVersion != lastColorVersion) {
			mapChange = true;
			lastColorVersion = tile.colorVersion;
		}
		if (tile.selectedVersion != lastSelectedVersion) {
			elementContainerChange = true;
			lastSelectedVersion = tile.selectedVersion;
		}
		if (tick % 10 == 0) {
			if (tile.exchangerVersion != lastExchangerVersion) {
				exchangerChange = true;
				lastExchangerVersion = tile.exchangerVersion;
			}
		}

		if (mapChange || elementContainerChange || exchangerChange)
			updateToClient(mapChange, elementContainerChange, exchangerChange);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		BlockPos pos = tile.getPos();
		return playerIn.getDistanceSq(pos) <= 64 && player.world.getTileEntity(pos) == this.tile;
	}

	@Override
	public void recvData(NBTTagCompound nbt, Side side) {
		if (side == Side.CLIENT) {
			if (nbt.hasKey("cs")) {
				tile.setColors(nbt.getByteArray("cs"));
				tile.markColorDirty();
			}
			if (nbt.hasKey("cl")) {
				containerDetecter.recvChangeList(nbt.getTagList("cl", NBTTag.TAG_COMPOUND), tile.getCanMapDetected());
				tile.selectedVersion = System.currentTimeMillis();
			}
			if (nbt.hasKey("ec"))
				exchangerDetecter.recvChangeList(nbt.getTagList("ec", NBTTag.TAG_COMPOUND), tile.getExchanger());
			return;
		}

		if (nbt.hasKey("RT")) {
			BlockPos pos = NBTHelper.getBlockPos(nbt, "P");
			boolean isIn = nbt.getBoolean("RT");
			int op = isIn ? IAltarWake.SEND : IAltarWake.OBTAIN;
			tile.doTranfer(pos, op, Integer.MAX_VALUE, 0x2 | 0x4);
			return;
		}

		BlockPos pos = NBTHelper.getBlockPos(nbt, "P");
		tile.updateDevolveData(pos, new DevolveData(nbt.getInteger("F")));

	}

}
