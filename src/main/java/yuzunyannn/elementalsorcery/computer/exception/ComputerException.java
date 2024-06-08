package yuzunyannn.elementalsorcery.computer.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.util.text.TextComponentTranslation;
import yuzunyannn.elementalsorcery.api.computer.ICalculatorObject;
import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
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

	@Override
	public boolean isGameException() {
		return true;
	}

	@Override
	public Object toDisplayObject() {
		List<Object> list = new ArrayList<>();
		UUID udid = null;
		if (device instanceof IDevice) udid = ((IDevice) device).getUDID();
		else if (device instanceof IComputer) udid = ((IComputer) device).device().getUDID();
		if (udid != null) list.add(new TextComponentTranslation("es.app.id.device").appendText(udid.toString()));
		addRenderObject(list);
		return list;
	}

	// need override
	protected void addRenderObject(List<Object> list) {
		list.add("error");
	}

}
