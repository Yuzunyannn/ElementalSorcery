package yuzunyannn.elementalsorcery.computer.files;

import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;

public class DeviceStorage extends VariableSet implements IDeviceStorage {

	public DeviceStorage(NBTTagCompound nbt) {
		this.nbt = nbt;
	}

	@Override
	public boolean isClose() {
		return false;
	}

	@Override
	public void close() {
	}

	@Override
	public DeviceStorage markDirty() {
		return this;
	}

}
