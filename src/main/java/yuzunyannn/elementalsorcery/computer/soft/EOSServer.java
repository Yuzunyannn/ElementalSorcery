package yuzunyannn.elementalsorcery.computer.soft;

import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IDeviceEnv;
import yuzunyannn.elementalsorcery.api.computer.soft.App;

public class EOSServer extends EOS {

	public EOSServer(IComputer computer) {
		super(computer);
	}

	@Override
	public boolean isRemote() {
		return false;
	}
}
