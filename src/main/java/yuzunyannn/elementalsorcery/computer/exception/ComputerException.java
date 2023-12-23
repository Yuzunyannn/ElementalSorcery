package yuzunyannn.elementalsorcery.computer.exception;

import yuzunyannn.elementalsorcery.api.computer.ICalculatorObject;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.soft.IComputerException;

@SuppressWarnings("serial")
public class ComputerException extends RuntimeException implements IComputerException {

	public final ICalculatorObject obj;
	public final IDevice device;

	public ComputerException(IDevice device, ICalculatorObject obj) {
		this.obj = obj;
		this.device = device;
	}

	public ComputerException(IDevice device, ICalculatorObject obj, String msg) {
		super(msg);
		this.obj = obj;
		this.device = device;
	}

	public ComputerException(IDevice device) {
		this.obj = device;
		this.device = device;
	}

	public ComputerException(IDevice device, String msg) {
		super(msg);
		this.obj = device;
		this.device = device;
	}

}
