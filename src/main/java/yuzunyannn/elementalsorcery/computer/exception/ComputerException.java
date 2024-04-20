package yuzunyannn.elementalsorcery.computer.exception;

import yuzunyannn.elementalsorcery.api.computer.ICalculatorObject;
import yuzunyannn.elementalsorcery.api.computer.soft.IComputerException;

@SuppressWarnings("serial")
public class ComputerException extends RuntimeException implements IComputerException {

	public final ICalculatorObject obj;
	public final ICalculatorObject device;

	public ComputerException(ICalculatorObject device, ICalculatorObject obj) {
		this.obj = obj;
		this.device = device;
	}

	public ComputerException(ICalculatorObject device, ICalculatorObject obj, String msg) {
		super(msg);
		this.obj = obj;
		this.device = device;
	}

	public ComputerException(ICalculatorObject device) {
		this.obj = device;
		this.device = device;
	}

	public ComputerException(ICalculatorObject device, String msg) {
		super(msg);
		this.obj = device;
		this.device = device;
	}

}
