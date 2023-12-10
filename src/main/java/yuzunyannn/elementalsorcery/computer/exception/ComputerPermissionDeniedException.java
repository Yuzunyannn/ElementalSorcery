package yuzunyannn.elementalsorcery.computer.exception;

import yuzunyannn.elementalsorcery.api.computer.ICalculatorObject;
import yuzunyannn.elementalsorcery.api.computer.IDevice;

@SuppressWarnings("serial")
public class ComputerPermissionDeniedException extends ComputerException {

	public ComputerPermissionDeniedException(IDevice device, ICalculatorObject obj) {
		super(device, obj);
	}

	public ComputerPermissionDeniedException(IDevice device, ICalculatorObject obj, String msg) {
		super(device, obj, msg);
	}

	public ComputerPermissionDeniedException(IDevice device) {
		super(device);
	}

	public ComputerPermissionDeniedException(IDevice device, String msg) {
		super(device, msg);
	}

}
