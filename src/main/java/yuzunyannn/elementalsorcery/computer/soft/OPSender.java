package yuzunyannn.elementalsorcery.computer.soft;

import yuzunyannn.elementalsorcery.api.computer.DNRequest;
import yuzunyannn.elementalsorcery.util.helper.NBTSender;

public class OPSender extends NBTSender {

	public OPSender(String method) {
		write(":m", method);
	}

	public String args(int i) {
		return DNRequest.args(i);
	}

}
