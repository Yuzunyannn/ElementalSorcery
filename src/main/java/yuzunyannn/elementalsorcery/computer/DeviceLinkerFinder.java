package yuzunyannn.elementalsorcery.computer;

import java.util.LinkedList;
import java.util.UUID;

import yuzunyannn.elementalsorcery.api.computer.IDeviceEnv;

public class DeviceLinkerFinder {

	public final UUID uuid;
	public final LinkedList<DeviceLinker> linkers = new LinkedList<>();
	public long ts = System.currentTimeMillis();
	protected boolean isClose;

	public DeviceLinkerFinder(UUID target) {
		this.uuid = target;
	}

	public DeviceLinkerFinder join(DeviceLinker linker) {
		linkers.add(linker);
		return this;
	}

	public boolean isClose() {
		return isClose;
	}

	public long getWaitedTime() {
		return System.currentTimeMillis() - ts;
	}

	// 申请的人越多，调用update的频率越高，因为大家都在寻求
	public void update(IDeviceEnv env) {

	}

	public void finsh(IDeviceEnv env) {
		for (DeviceLinker linker : linkers) {
			if (linker.finder == this) linker.reconnectByOther(env);
		}
		linkers.clear();
	}

}
