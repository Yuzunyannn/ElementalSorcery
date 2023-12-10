package yuzunyannn.elementalsorcery.computer.soft;

import yuzunyannn.elementalsorcery.api.computer.IComputer;

public class EOSServer extends EOS {

	public EOSServer(IComputer computer) {
		super(computer);
	}

	@Override
	public boolean isClient() {
		return false;
	}
}
