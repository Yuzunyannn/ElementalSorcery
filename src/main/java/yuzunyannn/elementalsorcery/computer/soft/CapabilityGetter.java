package yuzunyannn.elementalsorcery.computer.soft;

import net.minecraftforge.common.capabilities.Capability;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceLinker;
import yuzunyannn.elementalsorcery.api.util.target.IObjectGetter;
import yuzunyannn.elementalsorcery.logics.EventServer;

public class CapabilityGetter<T> implements IObjectGetter<T> {

	final IDeviceLinker linker;
	final Capability<T> capability;
	final Object key;
	long ts;
	T obj;

	CapabilityGetter(IDeviceLinker linker, Capability<T> capability, Object key) {
		this.linker = linker;
		this.capability = capability;
		this.key = key;
	}

	public void reset() {
		obj = null;
		ts = EventServer.chaosTimeStamp;
		if (linker.isClose()) return;
		if (!linker.isConnecting()) return;
		IDevice device = linker.getRemoteDevice();
		obj = device.getCapability(capability, null);
	}

	@Override
	public T softGet() {
		if (EventServer.chaosTimeStamp - ts > 1000 * 5) reset();
		return obj;
	}

	@Override
	public T toughGet() {
		reset();
		return obj;
	}

}
