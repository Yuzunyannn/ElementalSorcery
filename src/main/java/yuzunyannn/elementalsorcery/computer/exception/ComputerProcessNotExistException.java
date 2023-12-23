package yuzunyannn.elementalsorcery.computer.exception;

import yuzunyannn.elementalsorcery.api.computer.IDevice;

@SuppressWarnings("serial")
public class ComputerProcessNotExistException extends ComputerException {

	public ComputerProcessNotExistException(IDevice device) {
		super(device);
	}

	public ComputerProcessNotExistException(IDevice device, String msg) {
		super(device, msg);
	}

}
