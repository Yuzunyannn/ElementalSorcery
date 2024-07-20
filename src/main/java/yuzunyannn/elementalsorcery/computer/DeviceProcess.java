package yuzunyannn.elementalsorcery.computer;

import yuzunyannn.elementalsorcery.api.computer.DNRequest;
import yuzunyannn.elementalsorcery.api.computer.IDevice;

public class DeviceProcess {

	protected DNRequest currParams;
	final public IDevice device;

	public DeviceProcess(IDevice device) {
		this.device = device;
	}

	public <T> T argv(String key, Class<T> cls) {
		return currParams != null ? currParams.ask(key, cls) : null;
	}

	public boolean isLogEnabled() {
		return currParams != null ? currParams.getLogList() != null : false;
	}

	public void log(Object... displayObjs) {
		if (currParams != null) {
			if (displayObjs.length == 0) return;
			if (displayObjs.length == 1) currParams.log(displayObjs[0]);
			else currParams.log(displayObjs);
		}
	}

}
