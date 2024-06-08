package yuzunyannn.elementalsorcery.computer.files;

import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;

public class DiskDeviceStorage extends VariableSet implements IDeviceStorage {

	protected final DiskDeviceFile file;
	protected final NBTTagCompound folder;
	protected boolean isClose;
	protected String fileName;
	protected boolean isMarkDirty = false;

	public DiskDeviceStorage(DiskDeviceFile file, NBTTagCompound folder) {
		this.file = file;
		this.folder = folder;
		this.fileName = file.getPath().getName();
		this.nbt = folder.getCompoundTag(this.fileName);
	}

	@Override
	public boolean isClose() {
		return isClose;
	}

	@Override
	public void close() {
		this.isClose = true;
		this.folder.setTag(this.fileName, this.serializeNBT());
		if (!this.isMarkDirty) return;
		this.file.markDirty();
	}

	@Override
	public DiskDeviceStorage markDirty() {
		this.isMarkDirty = true;
		return this;
	}

}
