package yuzunyannn.elementalsorcery.building;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.util.IOHelper;

public class BuildingSaveData implements INBTSerializable<NBTTagCompound> {

	protected final File file;
	protected Building building;
	protected long time = System.currentTimeMillis();;

	public static void debugSetKeyName(Building building) {
		building.setKeyName(BuildingSaveData.randomKeyName(building.getAuthor()));
	}

	public static String randomKeyName(String user) {
		return user + System.currentTimeMillis();
	}

	public BuildingSaveData(Building building) {
		this(building, "tmp", "");
	}

	public BuildingSaveData(Building building, String fileFolder, String suffix) {
		String key = this.getKey(building);
		file = ElementalSorcery.data.getFile("building/" + fileFolder, key + suffix);
		building.setKeyName(key);
		this.building = building;
		this.markDirty();
	}

	public BuildingSaveData(File file) throws IOException {
		this.file = file;
		this.readDataFromFile();
	}

	protected String getKey(Building building) {
		return "$" + BuildingSaveData.randomKeyName(building.getAuthor());
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = building.serializeNBT();
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		building = new Building();
		building.deserializeNBT(nbt);
	}

	private boolean dirty = false;

	public void markDirty() {
		this.setDirty(true);
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean b) {
		dirty = b;
	}

	public void use(long time) {
		if (this.time == time) return;
		this.time = time;
	}

	/** 处理保存还是删除 */
	public boolean trySave(long now) {
		long sec = (now - this.time) / (1000 * 60);
		if (BuildingLib.BUILDING_MAX_REMAIN_TIMES == 0 || sec < BuildingLib.BUILDING_MAX_REMAIN_TIMES) {
			if (!this.isDirty()) return true;
			this.setDirty(false);
			try {
				this.writeDataToFile();
			} catch (IOException e) {
				ElementalSorcery.logger.warn("建筑记录保存失败！" + file.getName());
				return false;
			}
			return true;
		} else return false;
	}

	public void readDataFromFile() throws IOException {
		InputStream istream = null;
		NBTTagCompound nbt = null;
		try {
			istream = new FileInputStream(file);
			nbt = CompressedStreamTools.readCompressed(istream);
		} finally {
			IOHelper.closeQuietly(istream);
		}
		this.deserializeNBT(nbt);
	}

	public void writeDataToFile() throws IOException {
		try (OutputStream output = new FileOutputStream(this.file)) {
			CompressedStreamTools.writeCompressed(this.serializeNBT(), output);
		}
	}

}
