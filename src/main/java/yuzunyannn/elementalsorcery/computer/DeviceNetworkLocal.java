package yuzunyannn.elementalsorcery.computer;

import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceEnv;
import yuzunyannn.elementalsorcery.computer.exception.ComputerConnectException;

public class DeviceNetworkLocal extends DeviceNetwork {

	public DeviceNetworkLocal(IDevice device) {
		super(device);
	}

	@Override
	public boolean isDiscoverable() {
		return false;
	}

	@Override
	public boolean handshake(IDevice other, IDeviceEnv otherEnv, boolean simulate) throws ComputerConnectException {
		return false;
	}
}
