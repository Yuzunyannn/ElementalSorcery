package yuzunyannn.elementalsorcery.util.world;

import java.lang.ref.WeakReference;
import java.util.UUID;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityMasterBinder {

	/** 主人的uuid */
	protected UUID uuid;
	/** 主人 */
	protected WeakReference<EntityLivingBase> master;
	/** 根据uuid寻找主人，找不到的情况的cd时间记录 */
	protected long masterFindFailCD = 0;
	protected int masterFindFailTimes = 0;
	protected int maxMasterFindFailTimes = Integer.MAX_VALUE;

	protected String dataKey = "master";

	public EntityMasterBinder(EntityLivingBase master) {
		this.master = new WeakReference<>(master);
		this.uuid = master.getUniqueID();
	}

	public EntityMasterBinder(UUID playerUUID) {
		this.uuid = playerUUID;
	}

	public EntityMasterBinder() {

	}

	public EntityMasterBinder setDataKey(String dataName) {
		this.dataKey = dataName;
		return this;
	}

	public boolean isOwnerless() {
		return this.uuid == null;
	}

	public UUID getUUID() {
		return uuid;
	}

	@Nullable
	public EntityLivingBase getMaster() {
		return master == null ? null : master.get();
	}

	public void setMaxMasterFindFailTimes(int maxMasterFindFailTimes) {
		this.maxMasterFindFailTimes = maxMasterFindFailTimes;
	}

	public void setMaster(EntityLivingBase master) {
		if (master == null) {
			this.master = null;
			return;
		}
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
		if (master != null && !master.isDead) return master;
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

	public void writeDataToNBT(NBTTagCompound compound) {
		if (uuid != null) compound.setUniqueId(this.dataKey, uuid);
	}

	public void readDataFromNBT(NBTTagCompound compound) {
		if (compound.hasKey(this.dataKey + "Most")) uuid = compound.getUniqueId(this.dataKey);
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
