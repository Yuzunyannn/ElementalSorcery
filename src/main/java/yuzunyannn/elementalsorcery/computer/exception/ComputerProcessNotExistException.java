package yuzunyannn.elementalsorcery.computer.exception;

import yuzunyannn.elementalsorcery.api.computer.ICalculatorObject;

@SuppressWarnings("serial")
public class ComputerProcessNotExistException extends ComputerException {

	public ComputerProcessNotExistException(ICalculatorObject device) {
		super(device);
	}

	public ComputerProcessNotExistException(ICalculatorObject device, String msg) {
		super(device, msg);
	}

}
