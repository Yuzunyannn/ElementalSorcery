package yuzunyannn.elementalsorcery.computer;

import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceLinker;
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
	public boolean handshake(IDeviceLinker other) throws ComputerConnectException {
		return false;
	}
}
