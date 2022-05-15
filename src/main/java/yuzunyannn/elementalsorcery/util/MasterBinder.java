package yuzunyannn.elementalsorcery.util;

import java.lang.ref.WeakReference;
import java.util.UUID;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class MasterBinder {

	/** 主人的uuid */
	protected UUID uuid;
	/** 主人 */
	protected WeakReference<EntityLivingBase> master;
	/** 根据uuid寻找主人，找不到的情况的cd时间记录 */
	protected long masterFindFailCD = 0;
	protected int masterFindFailTimes = 0;
	protected int maxMasterFindFailTimes = Integer.MAX_VALUE;

	public MasterBinder(EntityLivingBase master) {
		this.master = new WeakReference<>(master);
		this.uuid = master.getUniqueID();
	}

	public MasterBinder(UUID playerUUID) {
		this.uuid = playerUUID;
	}

	public MasterBinder() {

	}

	public boolean isOwnerless() {
		return this.uuid == null;
	}

	@Nullable
	public EntityLivingBase getMaster() {
		return master == null ? null : master.get();
	}

	public void setMaxMasterFindFailTimes(int maxMasterFindFailTimes) {
		this.maxMasterFindFailTimes = maxMasterFindFailTimes;
	}

	public void setMaster(EntityLivingBase master) {
		if (master == null) return;
		this.master = new WeakReference<>(master);
		this.uuid = master.getUniqueID();
		masterFindFailCD = 0;
		masterFindFailTimes = 0;
	}

	public void setMaster(UUID uuid) {
		if (uuid == null) return;
		this.master = null;
		this.uuid = uuid;
	}

	@Nullable
	public EntityLivingBase tryGetMaster(World world) {
		EntityLivingBase master = this.getMaster();
		if (master != null) return master;
		if (masterFindFailTimes >= maxMasterFindFailTimes) return null;
		long now = System.currentTimeMillis();
		int cdTime = 1000;
		if (masterFindFailTimes > 5) cdTime = 1000 * 5;
		if (now - masterFindFailCD < cdTime) return null;
		master = this.restoreMaster(world);
		if (master == null) {
			masterFindFailCD = now;
			masterFindFailTimes++;
		}
		return master;
	}

	public EntityLivingBase restoreMaster(World world) {
		if (uuid == null) return null;
		EntityLivingBase master = WorldHelper.restoreLiving(world, uuid);
		this.setMaster(master);
		return master;
	}

	public void writeEntityToNBT(NBTTagCompound compound) {
		if (uuid != null) compound.setUniqueId("master", uuid);
	}

	public void readEntityFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("masterMost")) uuid = compound.getUniqueId("master");
	}

	public void writeSpawnData(ByteBuf buffer) {
		if (uuid == null) {
			buffer.writeLong(0).writeLong(0);
			return;
		}
		buffer.writeLong(uuid.getMostSignificantBits());
		buffer.writeLong(uuid.getLeastSignificantBits());
	}

	public void readSpawnData(ByteBuf additionalData) {
		long most = additionalData.readLong();
		long least = additionalData.readLong();
		if (most != 0 || least != 0) uuid = new UUID(most, least);
	}
}
