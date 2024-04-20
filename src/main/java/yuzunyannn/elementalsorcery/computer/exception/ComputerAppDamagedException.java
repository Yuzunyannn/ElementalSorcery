package yuzunyannn.elementalsorcery.computer.exception;

import yuzunyannn.elementalsorcery.api.computer.ICalculatorObject;

@SuppressWarnings("serial")
public class ComputerAppDamagedException extends ComputerException {

	public ComputerAppDamagedException(ICalculatorObject device) {
		super(device);
	}

	public ComputerAppDamagedException(ICalculatorObject device, String msg) {
		super(device, msg);
	}

}
