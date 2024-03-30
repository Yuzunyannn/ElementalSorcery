package yuzunyannn.elementalsorcery.computer;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;

import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceEnv;

public class WideNetwork {

	public final static int SAY_HELLO_INTERVAL = 100;
	public final static WideNetwork instance = new WideNetwork();

	protected int tick = 0;
	protected final LinkedHashMap<UUID, DeviceLinkerFinder> finderMap = new LinkedHashMap<>();

	public DeviceLinkerFinder apply(DeviceLinker linker, IDeviceEnv env) {
		onOtherDeviceCome(linker.network.getDevice(), env);
		UUID target = linker.getRemoteUUID();
		DeviceLinkerFinder finder = finderMap.get(target);
		if (finder != null) return finder.join(linker);
		finderMap.put(target, finder = new DeviceLinkerFinder(target));
		return finder.join(linker);
	}

	public void sayHello(IDevice device, IDeviceEnv env) {
		if (env.isRemote()) return;
		onOtherDeviceCome(device, env);
	}

	public void onOtherDeviceCome(IDevice device, IDeviceEnv env) {
		UUID uuid = device.getUDID();
		if (finderMap.containsKey(uuid)) {
			DeviceLinkerFinder finder = finderMap.get(uuid);
			finderMap.remove(uuid);
			finder.finsh(env);
		}
	}

	public void update() {
		if (tick++ % 100 != 0) return;
		if (finderMap.isEmpty()) return;
		Iterator<Entry<UUID, DeviceLinkerFinder>> iter = finderMap.entrySet().iterator();
		while (iter.hasNext()) {
			DeviceLinkerFinder finder = iter.next().getValue();
			if (finder.getWaitedTime() < 30 * 1000) break;
			finder.isClose = true;
			iter.remove();
		}
	}

}
