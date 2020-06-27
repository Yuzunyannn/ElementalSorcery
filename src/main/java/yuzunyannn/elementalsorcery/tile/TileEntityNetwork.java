package yuzunyannn.elementalsorcery.tile;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
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
	public void onDataPacket(net.minecraft.network.NetworkManager net,
			net.minecraft.network.play.server.SPacketUpdateTileEntity pkt) {
		isNetwork = true;
		this.handleUpdateTag(pkt.getNbtCompound());
		isNetwork = false;
	}

//	/** 最后一次发送的nbt */
//	private NBTTagCompound lastSendNBT = null;
//	/** 最后一次接受的nbt */
//	private NBTTagCompound lastRecvNBT = null;

	// 该函数还会被普通的调用，表明首次更新，首次调用是mc来管理
	@Override
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
//		NBTTagCompound nbt = this.writeToNBT(new NBTTagCompound());
//		  
//		// 判断是否为调用updateToClient函数发送的，如果不是，必须返回全部 if (this.isSending()) lastSendNBT =
//		TileEntityNetwork.detectWhenSendingToRemoveRedundancy(nbt, lastSendNBT); else
//		lastSendNBT = nbt;
//		  
//		return nbt;
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		// lastRecvNBT = TileEntityNetwork.detectWhenRecvToRecoveryRedundancy(tag,
		// lastRecvNBT);
		super.handleUpdateTag(tag);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	/** 将数据更新到client端 */
	public void updateToClient() {
		if (world.isRemote) return;
		isNetwork = true;
		for (EntityPlayer player : world.playerEntities) {
			if (player.getPosition().distanceSq(this.pos) > 512 * 512) continue;
			((EntityPlayerMP) player).connection.sendPacket(this.getUpdatePacket());
		}
		isNetwork = false;
	}

	@Deprecated
	static private NBTTagCompound detectWhenSendingToRemoveRedundancy(NBTTagCompound nbt, NBTTagCompound lastNBT) {
		if (lastNBT == null) lastNBT = new NBTTagCompound();
		NBTTagCompound tmp = nbt.copy();
		for (String key : lastNBT.getKeySet()) {
			// 如果本次发送的nbt中没含有旧标签，那么需要删除
			if (!nbt.hasKey(key)) {
				if (!nbt.hasKey("Del", 9)) {
					nbt.setTag("Del", new NBTTagList());
				}
				nbt.getTagList("Del", 8).appendTag(new NBTTagString(key));
				continue;
			}
			NBTBase tag = nbt.getTag(key);
			NBTBase tagLast = lastNBT.getTag(key);
			// 如果上次发送的nbt和本次的一样，不发送
			if (tagLast.equals(tag)) {
				nbt.removeTag(key);
				continue;
			} // 对于taglist有特殊检测，只记录list的变化，不发送冗余项
			else if (tagLast.getId() == tag.getId() && tag.getId() == 9) {
				NBTTagList list = (NBTTagList) tag;
				NBTTagList listLast = (NBTTagList) tagLast;
				NBTTagList dealList = new NBTTagList();
				int i = 0;
				int j = 0;
				while (i < list.tagCount() && j < listLast.tagCount()) {
					if (list.get(i).equals(listLast.get(j))) {
						j++;
						i++;
						continue;
					}
					// 如果有槽位标签，对ItemStackHandler等的特殊处理
					if (list.getCompoundTagAt(i).hasKey("Slot")) {
						if (list.getCompoundTagAt(i).getInteger("Slot") > listLast.getCompoundTagAt(i)
								.getInteger("Slot")) {
							// 物品槽位置删除origin
							NBTTagCompound deal = new NBTTagCompound();
							deal.setInteger("Change", i);
							dealList.appendTag(deal);
							j++;
							continue;
						}
					}
					// 其余情况，认为对应位置不匹配，需更新
					NBTTagCompound deal = new NBTTagCompound();
					deal.setInteger("Change", i);
					deal.setTag("Value", list.getCompoundTagAt(i));
					i++;
					j++;
				}
				// 新入的标签长
				for (; i < list.tagCount(); i++) {
					NBTTagCompound deal = new NBTTagCompound();
					deal.setInteger("Change", i);
					deal.setTag("Value", list.getCompoundTagAt(i));
					dealList.appendTag(deal);
				}
				// 新入的标签短
				for (; j < listLast.tagCount(); j++) {
					NBTTagCompound deal = new NBTTagCompound();
					deal.setInteger("Change", j);
					dealList.appendTag(deal);
				}
				// 重置更新，使用一个NBTTagCompound来标识这个list是变化队列而不是真实数据
				NBTTagCompound dealInfo = new NBTTagCompound();
				dealInfo.setTag("Change", dealList);
				nbt.setTag(key, dealInfo);
			}
		}
		// 返回新的最后发送的nbt
		return tmp;
	}

	@Deprecated
	static private NBTTagCompound detectWhenRecvToRecoveryRedundancy(NBTTagCompound nbt, NBTTagCompound lastNBT) {
		if (lastNBT == null) return nbt.copy();
		// 先删除不需要的标签
		if (nbt.hasKey("Del", 9)) {
			NBTTagList list = nbt.getTagList("Del", 8);
			for (NBTBase base : list) {
				lastNBT.removeTag(((NBTTagString) base).getString());
			}
			nbt.removeTag("Del");
		}
		// 检查变更，恢复数据
		for (String key : lastNBT.getKeySet()) {
			// 如果有没发过来的标签，在这里恢复
			if (!nbt.hasKey(key)) {
				nbt.setTag(key, lastNBT.getTag(key));
				continue;
			}
			NBTBase tag = nbt.getTag(key);
			NBTBase tagLast = lastNBT.getTag(key);
			// 对于taglist有特殊检测，变动原有项目
			if (tagLast.getId() == 9 && tag.getId() == 10) {
				NBTTagCompound dealInfo = (NBTTagCompound) tag;
				if (!dealInfo.hasKey("Change", 9)) continue;
				NBTTagList dealList = (NBTTagList) dealInfo.getTag("Change");
				NBTTagList list = (NBTTagList) tagLast;
				// 移除标签后，之后下标的变化量
				int indexChange = 0;
				// 根据改变修改原有数据，恢复成最新
				for (NBTBase base : dealList) {
					NBTTagCompound deal = (NBTTagCompound) base;
					int change = deal.getInteger("Change") - indexChange;
					if (deal.hasKey("Value")) {
						if (change >= list.tagCount()) list.appendTag(deal.getTag("Value"));
						else list.set(change, deal.getTag("Value"));
					} else {
						list.removeTag(change);
						indexChange++;
					}
				}
				nbt.setTag(key, list);
			}
		}
		// 更新新的lastRecv
		return nbt.copy();
	}
}
