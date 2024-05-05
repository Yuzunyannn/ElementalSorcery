package yuzunyannn.elementalsorcery.api.computer.soft;

import java.util.List;

import yuzunyannn.elementalsorcery.api.computer.DNResult;
import yuzunyannn.elementalsorcery.api.computer.IDevice;

public interface IDeviceShellExecutor {

	IDevice getDevice();

	DNResult getResult();

	List<Object> getLogs();

	void setLogicEnabled(boolean enabled);

	void pushResult(IDeviceShell shell, DNResult result);

}
