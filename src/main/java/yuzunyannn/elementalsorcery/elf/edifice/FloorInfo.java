package yuzunyannn.elementalsorcery.elf.edifice;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.util.NBTHelper;

public class FloorInfo implements INBTSerializable<NBTTagCompound> {

	public static enum Status {
		PLANNING,
		CONSTRUCTING,
		COMPLETE
	}

	/** 以下是持久数据 */
	protected NBTTagCompound floorData = new NBTTagCompound();
	protected short high;
	protected ElfEdificeFloor type;
	protected BlockPos basicPos;
	protected Status status = Status.PLANNING;

	/** 以下是非持久化数据， */
	protected BuildProgress progress;

	public BuildProgress getProgress() {
		return progress;
	}

	public void startProgress(BuildProgress progress) {
		status = Status.CONSTRUCTING;
		this.progress = progress;
	}

	public FloorInfo(ElfEdificeFloor type, BlockPos basicPos) {
		this.type = type;
		this.basicPos = basicPos;
	}

	public void setFloorData(NBTTagCompound data) {
		floorData = data == null ? floorData : data;
	}

	public FloorInfo(NBTTagCompound nbt) {
		this.deserializeNBT(nbt);
	}

	public boolean isEmpty() {
		return type == null;
	}

	public Status getStatus() {
		return status;
	}
	
	public NBTTagCompound getFloorData() {
		return floorData;
	}

	public void setStatus(Status status) {
		this.status = status;
		if (status == Status.COMPLETE) this.progress = null;
	}

	public ElfEdificeFloor getType() {
		return type;
	}

	public BlockPos getBasicPos() {
		return basicPos;
	}

	public void setHigh(short high) {
		this.high = high;
	}

	public short getHigh() {
		return high;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		if (type == null) return new NBTTagCompound();
		NBTTagCompound data = new NBTTagCompound();
		data.setString("id", type.getRegistryName().toString());
		data.setTag("data", floorData);
		data.setShort("high", high);
		NBTHelper.setBlockPos(data, "pos", basicPos);
		data.setByte("status", (byte) status.ordinal());
		return data;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		type = ElfEdificeFloor.REGISTRY.getValue(new ResourceLocation(nbt.getString("id")));
		floorData = nbt.getCompoundTag("data");
		high = nbt.getShort("high");
		basicPos = NBTHelper.getBlockPos(nbt, "pos");
		status = Status.values()[nbt.getByte("status")];
	}

}
