package yuzunyan.elementalsorcery.building;

import java.io.File;
import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;
import yuzunyan.elementalsorcery.ElementalSorcery;

public class BuildingSaveData extends WorldSavedData {

	final File file;
	Building building;
	long time;

	public static String randomKeyName(String user) {
		return user + System.currentTimeMillis();
	}

	BuildingSaveData(Building building) {
		super(BuildingSaveData.randomKeyName(building.getAuthor()));
		file = ElementalSorcery.data.getESFile("building/tmp", this.mapName);
		time = System.currentTimeMillis();
		building.setKeyName(this.mapName);
		this.markDirty();
	}

	BuildingSaveData(File file) throws IOException {
		super(file.getName());
		NBTTagCompound nbt = CompressedStreamTools.read(file);
		this.file = file;
		this.deserializeNBT(nbt);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = building.serializeNBT();
		return this.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		time = nbt.getLong("time");
		building = new Building();
		building.deserializeNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setLong("time", this.time);
		return nbt;
	}

	public void use() {
		this.time = System.currentTimeMillis();
		this.markDirty();
	}

	/** 处理保存还是删除 */
	public boolean deal(long now) {
		long d = now - this.time;
		d = d / (1000 * 60 * 60 * 24);
		if (ElementalSorcery.config.BUILDING_MAX_REMAIN_DAYS == 0
				|| d < ElementalSorcery.config.BUILDING_MAX_REMAIN_DAYS) {
			if (!this.isDirty())
				return true;
			this.setDirty(false);
			try {
				CompressedStreamTools.write(this.serializeNBT(), file);
			} catch (IOException e) {
				ElementalSorcery.logger.warn("建筑记录保存失败！" + file.getName());
				return false;
			}
			return true;
		} else {
			if (!file.delete()) {
				ElementalSorcery.logger.warn("清理临时建筑文件失败！" + file.getName());
			}
			return false;
		}
	}
}
