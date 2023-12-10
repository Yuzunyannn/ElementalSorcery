package yuzunyannn.elementalsorcery.computer.exception;

import yuzunyannn.elementalsorcery.api.computer.IDevice;

@SuppressWarnings("serial")
public class ComputerNewProcessException extends ComputerException {

	public ComputerNewProcessException(IDevice device) {
		super(device);
	}

	public ComputerNewProcessException(IDevice device, String msg) {
		super(device, msg);
	}

}
