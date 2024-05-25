package yuzunyannn.elementalsorcery.computer.exception;

import java.util.List;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import yuzunyannn.elementalsorcery.api.computer.ICalculatorObject;

@SuppressWarnings("serial")
public class ComputerBootException extends ComputerException {

	public ITextComponent component;

	public ComputerBootException(ICalculatorObject device, ITextComponent component) {
		super(device, component.toString());
		this.component = component;
	}

	public ComputerBootException(ICalculatorObject device, String translateKey, Object... objs) {
		super(device, translateKey);
		this.component = new TextComponentTranslation(translateKey, objs);
	}

	@Override
	protected void addRenderObject(List<Object> list) {
		list.add(component);
	}
}
