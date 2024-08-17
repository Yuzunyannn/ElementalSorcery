package yuzunyannn.elementalsorcery.tile;

import java.util.function.Function;

import javax.annotation.Nullable;

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
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.tile.ICanSync;
import yuzunyannn.elementalsorcery.api.tile.ISyncable;
import yuzunyannn.elementalsorcery.api.util.IAliveStatusable;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTSS;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;
import yuzunyannn.elementalsorcery.util.helper.NBTSaver;
import yuzunyannn.elementalsorcery.util.helper.NBTSender;

public class TileEntityNetwork extends TileEntity implements ICanSync, IAliveStatusable, INBTSS {

	public static final byte TASK_SEND_ID = 1;
	public static final byte SYNC_SEND_ID = 2;

	protected TileTaskManager taskMgr;

	protected void initTaskManager(int taskCount, Function<Integer, TileTask> factory) {

		taskMgr = new TileTaskManager(taskCount, factory) {

			@Override
			public TileTask pushTask(int tid) {
				if (world.isRemote) return super.pushTask(tid);
				TileTask task = super.pushTask(tid);
				if (task == null) return null;
				if (task.needSyncToClient(0)) {
					NBTTagCompound tag = this.createTaskUpdateData(task);
					tag.setByte("~)", TASK_SEND_ID);
					updateToClient(new SPacketUpdateTileEntity(TileEntityNetwork.this.pos, getBlockMetadata(), tag));
				}
				markDirty();
				return task;
			}

			@Override
			protected TileTask removeTask(TileTask task, boolean exit) {
				if (world.isRemote) return super.removeTask(task, exit);
				task = super.removeTask(task, exit);
				if (task == null) return null;
				if (exit && task.needSyncToClient(0) && task.needSyncToClient(1)) {
					NBTTagCompound tag = new NBTTagCompound();
					tag.setByte("~)", TASK_SEND_ID);
					tag.setByte("rm", (byte) task.rid);
					updateToClient(new SPacketUpdateTileEntity(TileEntityNetwork.this.pos, getBlockMetadata(), tag));
				}
				markDirty();
				return task;
			}

		};
	}

	@Nullable
	public TileTaskManager getTaskMgr() {
		return taskMgr;
	}

	protected ISyncable[] syncs;

	protected void initSyncObject(ISyncable... objs) {
		syncs = objs;
		for (int i = 0; i < objs.length; i++) {
			final byte id = (byte) i;
			objs[i].setSyncDispatcher(base -> {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("~)", SYNC_SEND_ID);
				tag.setByte("i", id);
				tag.setTag("d", base);
				updateToClient(new SPacketUpdateTileEntity(TileEntityNetwork.this.pos, getBlockMetadata(), tag));
			});
		}
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTSender sender = new NBTSender();
		writeFullUpdateData(sender);
		if (sender.isEmpty()) return null;
		return new SPacketUpdateTileEntity(this.pos, this.getBlockMetadata(), sender.tag());
	}

	@Override
	final public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.getNbtCompound();
		if (nbt.hasKey("~)")) recvFullUpdateData(nbt);
		else readFullUpdateData(new NBTSender(nbt));
	}

	@Override
	final public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		writeFullUpdateData(new NBTSender(tag));
		return tag;
	}

	@Override
	final public void handleUpdateTag(NBTTagCompound tag) {
		super.readFromNBT(tag);
		readFullUpdateData(new NBTSender(tag));
	}

	@Override
	final public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		writeFullSaveData(new NBTSaver(compound));
		return compound;
	}

	@Override
	final public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readFullSaveData(new NBTSaver(compound));
	}

	@Override
	public void onChunkUnload() {
		this.invalidate();
	}

	@Override
	public boolean isAlive() {
		if (world == null) return false;
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
		if (custom == null) return;
		custom.setByte("~)", (byte) 0);
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

	private void writeFullUpdateData(INBTWriter writer) {
		if (taskMgr != null) taskMgr.writeUpdateData(writer);
		writeUpdateData(writer);
	}

	private void readFullUpdateData(INBTReader reader) {
		if (taskMgr != null) taskMgr.readUpdateData(reader);
		readUpdateData(reader);
	}

	private void writeFullSaveData(INBTWriter writer) {
		if (taskMgr != null) taskMgr.writeSaveData(writer);
		writeSaveData(writer);
	}

	private void readFullSaveData(INBTReader reader) {
		if (taskMgr != null) taskMgr.readSaveData(reader);
		readSaveData(reader);
	}

	private void recvFullUpdateData(NBTTagCompound origin) {
		byte code = origin.getByte("~)");
		switch (code) {
		case 0:
			recvUpdateData(new NBTSender(origin));
			break;
		case TASK_SEND_ID:
			if (taskMgr == null) return;
			if (origin.hasKey("rm")) {
				int rid = origin.getInteger("rm");
				taskMgr.removeTaskRT(rid);
			} else taskMgr.readTaskUpdateData(origin, true);
			break;
		case SYNC_SEND_ID:
			try {
				int index = origin.getInteger("i");
				syncs[index].onRecvMessage(origin.getTag("d"));
			} catch (Exception e) {
				if (ESAPI.isDevelop) ESAPI.logger.error("sync error", e);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void writeSaveData(INBTWriter writer) {

	}

	@Override
	public void readSaveData(INBTReader reader) {

	}

	@Override
	public void writeUpdateData(INBTWriter writer) {

	}

	@Override
	public void readUpdateData(INBTReader reader) {
	}

	public void recvUpdateData(INBTReader reader) {

	}

}
