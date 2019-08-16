package yuzunyannn.elementalsorcery.tile;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityNetwork extends TileEntity {

	private boolean isNetwork = false;

	/** 判断 是否是同步更新 */
	public boolean isSending() {
		return isNetwork;
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, this.getBlockMetadata(), this.getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = this.writeToNBT(new NBTTagCompound());
		return nbt;
	}

	@Override
	public void onDataPacket(net.minecraft.network.NetworkManager net,
			net.minecraft.network.play.server.SPacketUpdateTileEntity pkt) {
		isNetwork = true;
		this.handleUpdateTag(pkt.getNbtCompound());
		isNetwork = false;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public void updateToClient() {
		if (world.isRemote)
			return;
		isNetwork = true;
		for (EntityPlayer player : world.playerEntities)
			((EntityPlayerMP) player).connection.sendPacket(this.getUpdatePacket());
		isNetwork = false;
	}
}
