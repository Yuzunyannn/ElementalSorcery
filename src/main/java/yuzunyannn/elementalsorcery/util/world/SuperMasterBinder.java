package yuzunyannn.elementalsorcery.util.world;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;
import yuzunyannn.elementalsorcery.api.util.target.IWorldObject;
import yuzunyannn.elementalsorcery.util.helper.NBTSaver;

public class SuperMasterBinder {

	/** 主人 */
	protected CapabilityObjectRef ref = CapabilityObjectRef.INVALID;

	/** 寻找主人，找不到的情况的cd时间记录 */
	protected long masterFindFailCD = 0;
	protected int masterFindFailTimes = 0;
	protected int maxMasterFindFailTimes = Integer.MAX_VALUE;

	protected String dataKey = "master";

	public SuperMasterBinder() {

	}

	public void setMaster(IWorldObject master) {
		if (master == null) {
			this.ref = CapabilityObjectRef.INVALID;
			return;
		}
		this.ref = master.toRef();
		masterFindFailCD = 0;
		masterFindFailTimes = 0;
	}

	public SuperMasterBinder setDataKey(String dataName) {
		this.dataKey = dataName;
		return this;
	}

	public boolean isOwnerless() {
		return ref.isInvalid();
	}

	@Nullable
	public IWorldObject getMaster() {
		return ref.toWorldObject();
	}

	public boolean is(Object obj) {
		return ref.is(obj);
	}

	public void setMaxMasterFindFailTimes(int maxMasterFindFailTimes) {
		this.maxMasterFindFailTimes = maxMasterFindFailTimes;
	}

	@Nullable
	public IWorldObject tryGetMaster(World world) {
		IWorldObject master = this.getMaster();
		if (master != null) return null;
		if (masterFindFailTimes >= maxMasterFindFailTimes) return null;
		long now = System.currentTimeMillis();
		int cdTime = 1000;
		if (masterFindFailTimes > 5) cdTime = 1000 * 5;
		if (now - masterFindFailCD < cdTime) return null;
		this.ref.restore(world);
		master = this.getMaster();
		if (master == null) {
			masterFindFailCD = now;
			masterFindFailTimes++;
		}
		return master;
	}

	public void restoreMaster(World world) {
		this.ref.restore(world);
	}

	public void writeDataToNBT(NBTTagCompound compound) {
		new NBTSaver(compound).write(this.dataKey, this.ref);
	}

	public void readDataFromNBT(NBTTagCompound compound) {
		this.ref = new NBTSaver(compound).capabilityObjectRef(this.dataKey);
	}

	public void writeSpawnData(ByteBuf buffer) {
		byte[] bytes = CapabilityObjectRef.write(ref);
		buffer.writeShort(bytes.length);
		buffer.writeBytes(bytes);
	}

	public void readSpawnData(ByteBuf additionalData) {
		short length = additionalData.readShort();
		byte[] bytes = new byte[length];
		this.ref = CapabilityObjectRef.read(bytes);
	}
}
