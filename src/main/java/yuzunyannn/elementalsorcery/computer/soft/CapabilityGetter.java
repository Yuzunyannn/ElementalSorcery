package yuzunyannn.elementalsorcery.computer.soft;

import net.minecraftforge.common.capabilities.Capability;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceLinker;
import yuzunyannn.elementalsorcery.api.util.target.IObjectGetter;
import yuzunyannn.elementalsorcery.logics.EventServer;

public class CapabilityGetter<T> implements IObjectGetter<T> {

	public final static <T> CapabilityGetter<T> emtpy() {
		return (CapabilityGetter<T>) EMPTY;
	}

	final static CapabilityGetter<?> EMPTY = new CapabilityGetter() {
		@Override
		public void reset() {
		}

		@Override
		public Object softGet() {
			return null;
		}

		@Override
		public Object toughGet() {
			return null;
		}
	};

	final IDeviceLinker linker;
	final Capability<T> capability;
	final Object key;
	long ts;
	T obj;

	private CapabilityGetter() {
		this.linker = null;
		this.capability = null;
		this.key = null;
	}

	CapabilityGetter(IDeviceLinker linker, Capability<T> capability, Object key) {
		this.linker = linker;
		this.capability = capability;
		this.key = key;
	}

	public void reset() {
		obj = null;
		ts = EventServer.chaosTimeStamp;
		if (!linker.isConnecting()) return;
		IDevice device = linker.getRemoteDevice();
		obj = device.getCapability(capability, null);
	}

	@Override
	public T softGet() {
		if (EventServer.chaosTimeStamp - ts > 1000 * 2) reset();
		return obj;
	}

	@Override
	public T toughGet() {
		reset();
		return obj;
	}

}
