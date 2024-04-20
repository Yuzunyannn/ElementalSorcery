package yuzunyannn.elementalsorcery.computer.exception;

import yuzunyannn.elementalsorcery.api.computer.ICalculatorObject;

@SuppressWarnings("serial")
public class ComputerBootException extends ComputerException {

	public ComputerBootException(ICalculatorObject device) {
		super(device);
	}

	public ComputerBootException(ICalculatorObject device, String msg) {
		super(device, msg);
	}

}
