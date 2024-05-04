package yuzunyannn.elementalsorcery.computer;

import yuzunyannn.elementalsorcery.api.computer.DNRequest;
import yuzunyannn.elementalsorcery.api.computer.IDevice;

public class DeviceProcess {

	protected DNRequest currParams;
	final public IDevice device;

	public DeviceProcess(IDevice device) {
		this.device = device;
	}

	public void log(Object displayObj) {
		if (currParams != null) currParams.log(displayObj);
	}

}
