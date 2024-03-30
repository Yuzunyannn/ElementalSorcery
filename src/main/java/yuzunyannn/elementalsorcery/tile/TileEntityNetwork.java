package yuzunyannn.elementalsorcery.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IAliveStatusable;
import yuzunyannn.elementalsorcery.api.tile.ICanSync;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;
import yuzunyannn.elementalsorcery.util.helper.NBTSaver;
import yuzunyannn.elementalsorcery.util.helper.NBTSender;

public class TileEntityNetwork extends TileEntity implements ICanSync, IAliveStatusable {

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTSender sender = new NBTSender();
		writeUpdateData(sender);
		if (sender.isEmpty()) return null;
		return new SPacketUpdateTileEntity(this.pos, this.getBlockMetadata(), sender.tag());
	}

	@Override
	final public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readUpdateData(new NBTSender(pkt.getNbtCompound()));
	}

	@Override
	final public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		writeUpdateData(new NBTSender(tag));
		return tag;
	}

	@Override
	final public void handleUpdateTag(NBTTagCompound tag) {
		super.readFromNBT(tag);
		readUpdateData(new NBTSender(tag));
	}

	@Override
	final public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		writeSaveData(new NBTSaver(compound));
		return compound;
	}

	@Override
	final public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readSaveData(new NBTSaver(compound));
	}

	@Override
	public void onChunkUnload() {
		this.invalidate();
	}

	@Override
	public boolean isAlive() {
		return !this.isInvalid();
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void markDirty() {
		if (world.isRemote) return;
		super.markDirty();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		final int TILE_ENTITY_RENDER_DISTANCE = TileEntityNetworkOld.TILE_ENTITY_RENDER_DISTANCE;
		if (TILE_ENTITY_RENDER_DISTANCE > 0) return TILE_ENTITY_RENDER_DISTANCE * TILE_ENTITY_RENDER_DISTANCE;
		if (TILE_ENTITY_RENDER_DISTANCE == -1) {
			int distance = RenderFriend.getRenderDistanceChunks() * 16;
			return distance * distance;
		}
		return 128 * 128;
	}

	/** 将数据更新到client端，优化有可能不是同步的 */
	@Override
	public void updateToClient() {
		if (world.isRemote) return;
		// IBlockState state = this.world.getBlockState(pos);
		// world.notifyBlockUpdate(pos, state, state, 2);
		updateToClient(new SPacketUpdateTileEntity(this.pos, this.getBlockMetadata(), getUpdateTag()));
	}

	public void updateToClient(NBTTagCompound custom) {
		if (world.isRemote) return;
		updateToClient(new SPacketUpdateTileEntity(this.pos, this.getBlockMetadata(), custom));
	}

	protected void updateToClient(SPacketUpdateTileEntity packet) {
		if (world.isRemote) return;
		WorldServer world = (WorldServer) this.world;
		int distance = world.getMinecraftServer().getPlayerList().getViewDistance();
		distance = distance * 16;
		for (EntityPlayer player : world.playerEntities) {
			if (player.getPosition().distanceSq(this.pos) > distance * distance) continue;
			((EntityPlayerMP) player).connection.sendPacket(packet);
		}
	}

	public void writeSaveData(INBTWriter writer) {

	}

	public void readSaveData(INBTReader reader) {

	}

	public void writeUpdateData(INBTWriter writer) {

	}

	public void readUpdateData(INBTReader reader) {

	}

}
