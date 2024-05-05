package yuzunyannn.elementalsorcery.computer.soft;

import java.util.LinkedList;
import java.util.List;

import yuzunyannn.elementalsorcery.api.computer.DNResult;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.soft.IDeviceShell;
import yuzunyannn.elementalsorcery.api.computer.soft.IDeviceShellExecutor;

public class DeviceShellExecutor implements IDeviceShellExecutor {

	public final IDevice device;
	protected boolean logEnabled;
	protected DNResult result = DNResult.unavailable();
	protected List<Object> logs;

	protected DeviceShellExecutor(IDevice device) {
		this.device = device;
	}

	@Override
	public DNResult getResult() {
		return result;
	}

	@Override
	public List<Object> getLogs() {
		return logs;
	}

	@Override
	public void setLogicEnabled(boolean enabled) {
		logEnabled = enabled;
		logs = enabled ? (logs == null ? new LinkedList() : logs) : null;
	}

	@Override
	public IDevice getDevice() {
		return device;
	}

	@Override
	public void pushResult(IDeviceShell shell, DNResult result) {
		this.result = result;
	}

}
