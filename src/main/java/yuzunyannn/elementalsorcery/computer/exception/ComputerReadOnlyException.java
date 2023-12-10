package yuzunyannn.elementalsorcery.computer.exception;

import yuzunyannn.elementalsorcery.api.computer.ICalculatorObject;
import yuzunyannn.elementalsorcery.api.computer.IDevice;

@SuppressWarnings("serial")
public class ComputerReadOnlyException extends ComputerException {

	public ComputerReadOnlyException(IDevice device, ICalculatorObject obj) {
		super(device, obj);
	}

	public ComputerReadOnlyException(IDevice device, ICalculatorObject obj, String msg) {
		super(device, obj, msg);
	}

}
