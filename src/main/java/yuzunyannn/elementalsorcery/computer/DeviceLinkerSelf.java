package yuzunyannn.elementalsorcery.computer;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceEnv;
import yuzunyannn.elementalsorcery.api.computer.IDeviceLinker;
import yuzunyannn.elementalsorcery.api.computer.IDeviceNetwork;

public class DeviceLinkerSelf implements IDeviceLinker {

	protected final IDeviceNetwork network;
	protected final IDevice device;

	public DeviceLinkerSelf(IDeviceNetwork network) {
		this.network = network;
		this.device = network.getDevice();
	}

	@Override
	public boolean isClose() {
		return false;
	}

	@Override
	public boolean isConnecting() {
		return true;
	}

	@Override
	public void close() {
	}

	@Override
	public UUID getRemoteUUID() {
		return device.getUDID();
	}

	@Override
	public IDevice getRemoteDevice() {
		return device;
	}

	@Override
	public boolean reconnect(IDeviceEnv env) {
		if (isClose()) return false;
		return true;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return null;
	}

}
