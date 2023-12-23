package yuzunyannn.elementalsorcery.computer.exception;

import yuzunyannn.elementalsorcery.api.computer.IDevice;

@SuppressWarnings("serial")
public class ComputerAppDamagedException extends ComputerException {

	public ComputerAppDamagedException(IDevice device) {
		super(device);
	}

	public ComputerAppDamagedException(IDevice device, String msg) {
		super(device, msg);
	}

}
