package yuzunyannn.elementalsorcery.computer.exception;

import yuzunyannn.elementalsorcery.api.computer.IDevice;

@SuppressWarnings("serial")
public class ComputerBootException extends ComputerException {

	public ComputerBootException(IDevice device) {
		super(device);
	}

	public ComputerBootException(IDevice device, String msg) {
		super(device, msg);
	}

}
