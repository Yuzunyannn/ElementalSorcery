package yuzunyannn.elementalsorcery.computer.exception;

import yuzunyannn.elementalsorcery.api.computer.IDevice;

@SuppressWarnings("serial")
public class ComputerConnectException extends ComputerException {

	public ComputerConnectException(IDevice device) {
		super(device);
	}

	public ComputerConnectException(IDevice device, String msg) {
		super(device, msg);
	}

}
