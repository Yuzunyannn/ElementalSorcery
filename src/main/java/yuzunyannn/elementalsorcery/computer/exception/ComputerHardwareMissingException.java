package yuzunyannn.elementalsorcery.computer.exception;

import yuzunyannn.elementalsorcery.api.computer.ICalculatorObject;

@SuppressWarnings("serial")
public class ComputerHardwareMissingException extends ComputerException {

	public ComputerHardwareMissingException(ICalculatorObject device) {
		super(device);
	}

	public ComputerHardwareMissingException(ICalculatorObject device, String msg) {
		super(device, msg);
	}

}
