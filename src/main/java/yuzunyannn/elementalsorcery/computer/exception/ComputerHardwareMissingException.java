package yuzunyannn.elementalsorcery.computer.exception;

import yuzunyannn.elementalsorcery.api.computer.IDevice;

@SuppressWarnings("serial")
public class ComputerHardwareMissingException extends ComputerException {

	public ComputerHardwareMissingException(IDevice device) {
		super(device);
	}

	public ComputerHardwareMissingException(IDevice device, String msg) {
		super(device, msg);
	}

}
