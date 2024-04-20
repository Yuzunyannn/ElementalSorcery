package yuzunyannn.elementalsorcery.computer.exception;

import yuzunyannn.elementalsorcery.api.computer.ICalculatorObject;
import yuzunyannn.elementalsorcery.api.computer.IDevice;

@SuppressWarnings("serial")
public class ComputerPermissionDeniedException extends ComputerException {

	public ComputerPermissionDeniedException(ICalculatorObject device, ICalculatorObject obj) {
		super(device, obj);
	}

	public ComputerPermissionDeniedException(ICalculatorObject device, ICalculatorObject obj, String msg) {
		super(device, obj, msg);
	}

	public ComputerPermissionDeniedException(ICalculatorObject device) {
		super(device);
	}

	public ComputerPermissionDeniedException(ICalculatorObject device, String msg) {
		super(device, msg);
	}

}
