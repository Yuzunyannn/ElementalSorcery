package yuzunyannn.elementalsorcery.computer.exception;

import yuzunyannn.elementalsorcery.api.computer.ICalculatorObject;

@SuppressWarnings("serial")
public class ComputerNewProcessException extends ComputerException {

	public ComputerNewProcessException(ICalculatorObject device) {
		super(device);
	}

	public ComputerNewProcessException(ICalculatorObject device, String msg) {
		super(device, msg);
	}

}
