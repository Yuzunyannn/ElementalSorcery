package yuzunyannn.elementalsorcery.tile;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import yuzunyannn.elementalsorcery.util.NBTTag;

public class TileEntityNetwork extends TileEntity {

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

	/** 将数据更新到client端 */
	public void updateToClient() {
		if (world.isRemote) return;
		WorldServer world = (WorldServer) this.world;
		int distance = world.getMinecraftServer().getPlayerList().getViewDistance();
		distance = distance * 16;
		isNetwork = true;
		for (EntityPlayer player : world.playerEntities) {
			if (player.getPosition().distanceSq(this.pos) > distance * distance) continue;
			((EntityPlayerMP) player).connection.sendPacket(this.getUpdatePacket());
		}
		isNetwork = false;
	}

	/** 设置itemStack */
	public void nbtSetItemStack(NBTTagCompound nbt, String key, ItemStack stack) {
		if (stack.isEmpty()) return;
		if (this.isSending()) {
			NBTTagCompound stackNBT = new NBTTagCompound();

			stackNBT.setInteger("id", Item.REGISTRY.getIDForObject(stack.getItem()));

			if (stack.getCount() != 1) stackNBT.setByte("n", (byte) stack.getCount());
			if (stack.getItemDamage() != 0) nbt.setShort("d", (short) stack.getItemDamage());

			NBTTagCompound tag = stack.getTagCompound();
			if (tag != null && !tag.hasNoTags()) nbt.setTag("t", tag);

			nbt.setTag(key, stackNBT);
		} else nbt.setTag(key, stack.serializeNBT());
	}

	/** 获取设置itemStack */
	public ItemStack nbtGetItemStack(NBTTagCompound nbt, String key) {
		if (!nbt.hasKey(key, NBTTag.TAG_COMPOUND)) return ItemStack.EMPTY;
		if (this.isSending()) {
			NBTTagCompound stackNBT = nbt.getCompoundTag(key);
			Item item = Item.REGISTRY.getObjectById(stackNBT.getInteger("id"));
			ItemStack stack = new ItemStack(item);
			if (stackNBT.hasKey("n", NBTTag.TAG_NUMBER)) stack.setCount(stackNBT.getInteger("n"));
			if (stackNBT.hasKey("d", NBTTag.TAG_NUMBER)) stack.setItemDamage(stackNBT.getInteger("d"));
			if (stackNBT.hasKey("t", NBTTag.TAG_COMPOUND)) stack.setTagCompound(stackNBT.getCompoundTag("t"));
			return stack;
		} else return new ItemStack(nbt.getCompoundTag(key));
	}
}
