package yuzunyannn.elementalsorcery.computer;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceEnv;
import yuzunyannn.elementalsorcery.api.computer.IDeviceNetwork;
import yuzunyannn.elementalsorcery.api.util.target.IWorldObject;
import yuzunyannn.elementalsorcery.util.Stopwatch;

public abstract class WideNetwork {

	public final static int SAY_HELLO_INTERVAL = 100;

	public final static WideNetwork instance = new WideNetworkCommon();

	protected int tick = 0;
	protected boolean inFinderUpdate = false;
	protected final Map<UUID, DeviceFinder> finderMap = new LinkedHashMap<>();
	protected final LinkedList<DeviceScanner> scannerList = new LinkedList<>();

	public DeviceFinder applyFinder(World world, IDeviceAsker asker) {
		UUID target = asker.lookFor();
		DeviceFinder finder = finderMap.get(target);
		if (finder != null) return finder.join(asker);
		finderMap.put(target, finder = createFinder(target));
		return finder.join(asker);
	}

	public DeviceScanner applyScanner(IWorldObject wo) {
		DeviceScanner scanner = this.createScanner(wo);
		scannerList.addFirst(scanner);
		return scanner;
	}

	protected abstract DeviceFinder createFinder(UUID uuid);

	protected abstract DeviceScanner createScanner(IWorldObject wo);

	public void helloWorld(IDevice device) {
		IDeviceEnv env = device.getEnv();
		if (env != null) helloWorld(device, env);
	}

	public void helloWorld(IDevice device, IDeviceEnv env) {
		UUID uuid = device.getUDID();

		if (finderMap.containsKey(uuid)) {
			DeviceFinder finder = finderMap.get(uuid);
			finder.finsh(env);
			if (!inFinderUpdate) finderMap.remove(uuid);
		}

		IDeviceNetwork network = device.getNetwork();
		if (network.isDiscoverable()) {
			for (DeviceScanner scanner : scannerList) scanner.helloWorld(env);
		}
	}

	protected int getIntervalTick() {
		return 20;
	}

	protected abstract Stopwatch getStopWatch();

	public void update() {
		final int I_TICK = getIntervalTick();

		if (tick++ % I_TICK != 0) return;

		final Stopwatch bigComputeWatch = getStopWatch();

		if (!finderMap.isEmpty()) {
			int travelCount = 16;
			inFinderUpdate = true;
			Iterator<Entry<UUID, DeviceFinder>> iter = finderMap.entrySet().iterator();
			while (iter.hasNext()) {
				DeviceFinder finder = iter.next().getValue();
				finder.update(I_TICK, bigComputeWatch);
				if (finder.isClose() || finder.isEmpty()) {
					iter.remove();
					continue;
				}
				if (!finder.isOvertime()) {
					if (travelCount-- > 0) continue;
					break;
				}
				finder.close();
				iter.remove();
			}
			inFinderUpdate = false;
		}

		if (!scannerList.isEmpty()) {
			Iterator<DeviceScanner> iter = scannerList.iterator();
			while (iter.hasNext()) {
				DeviceScanner scanner = iter.next();
				if (!scanner.update(I_TICK)) {
					scanner.close();
					iter.remove();
				}
			}
		}

	}

}
