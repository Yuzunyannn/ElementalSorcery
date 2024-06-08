package yuzunyannn.elementalsorcery.computer.files;

import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.computer.DeviceFilePath;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;

public class ConstDeviceFile extends DeviceFileAdapter {

	protected NBTTagCompound nbt;

	public ConstDeviceFile(DeviceFilePath path, NBTTagCompound data) {
		super(path);
		this.nbt = data;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public IDeviceStorage open() {
		if (this.nbt == null) return null;
		return new DeviceStorage(nbt.copy());
	}

}
