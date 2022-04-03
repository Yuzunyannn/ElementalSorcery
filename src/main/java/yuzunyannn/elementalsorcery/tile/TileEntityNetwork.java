package yuzunyannn.elementalsorcery.tile;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.tile.ICanUpdate;
import yuzunyannn.elementalsorcery.config.Config;
import yuzunyannn.elementalsorcery.util.NBTTag;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;

public class TileEntityNetwork extends TileEntity implements ICanUpdate {

	@Config
	static protected int TILE_ENTITY_RENDER_DISTANCE = -1;

	private boolean isNetwork = false;

	/** 判断 是否是同步更新 */
	public boolean isSending() {
		return isNetwork;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if (this.isSending()) return;
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (this.isSending()) return compound;
		return super.writeToNBT(compound);
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, this.getBlockMetadata(), this.getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		isNetwork = true;
		this.handleUpdateTag(pkt.getNbtCompound());
		isNetwork = false;
	}

	// 该函数还会被普通的调用，表明首次更新，首次调用是mc来管理
	@Override
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
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
		if (TILE_ENTITY_RENDER_DISTANCE > 0) return TILE_ENTITY_RENDER_DISTANCE * TILE_ENTITY_RENDER_DISTANCE;
		if (TILE_ENTITY_RENDER_DISTANCE == -1) {
			int distance = RenderHelper.getRenderDistanceChunks() * 16;
			return distance * distance;
		}
		return 128 * 128;
	}

	/** 将数据更新到client端，优化有可能不是同步的 */
	@Override
	public void updateToClient() {
		if (world.isRemote) return;
		// world.notifyBlockUpdate(pos, null, null, TILE_ENTITY_RENDER_DISTANCE);
		isNetwork = true;
		updateToClient(new SPacketUpdateTileEntity(this.pos, this.getBlockMetadata(), this.getUpdateTag()));
		isNetwork = false;
	}

	public void updateToClient(NBTTagCompound custom) {
		if (world.isRemote) return;
		isNetwork = true;
		SPacketUpdateTileEntity packet = new SPacketUpdateTileEntity(this.pos, this.getBlockMetadata(), custom);
		updateToClient(packet);
		isNetwork = false;
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

	/** 设置itemStack */
	public void nbtWriteItemStack(NBTTagCompound nbt, String key, ItemStack stack) {
		if (stack.isEmpty()) return;
		if (this.isSending()) nbt.setTag(key, NBTHelper.serializeItemStackForSend(stack));
		else nbt.setTag(key, stack.serializeNBT());
	}

	/** 获取itemStack */
	public ItemStack nbtReadItemStack(NBTTagCompound nbt, String key) {
		if (!nbt.hasKey(key, NBTTag.TAG_COMPOUND)) return ItemStack.EMPTY;
		if (this.isSending()) return NBTHelper.deserializeItemStackFromSend(nbt.getCompoundTag(key));
		else return new ItemStack(nbt.getCompoundTag(key));
	}

	/** 设置stack仓库 */
	public void nbtWriteItemStackHanlder(NBTTagCompound nbt, String key, ItemStackHandler inventory) {
		nbt.setTag(key, inventory.serializeNBT());
	}

	/** 获取stack仓库 */
	public boolean nbtReadItemStackHanlder(NBTTagCompound nbt, String key, ItemStackHandler inventory) {
		if (!nbt.hasKey(key, NBTTag.TAG_COMPOUND)) return false;
		inventory.deserializeNBT(nbt.getCompoundTag(key));
		return true;
	}
}
