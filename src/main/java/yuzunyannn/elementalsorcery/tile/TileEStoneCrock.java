package yuzunyannn.elementalsorcery.tile;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;

public class TileEStoneCrock extends TileEntityNetwork {

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return null;
	}

	public void onItemIn(EntityItem entityItem) {

	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {

		super.handleUpdateTag(tag);
	}
}
