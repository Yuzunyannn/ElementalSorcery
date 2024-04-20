package yuzunyannn.elementalsorcery.computer;

import java.util.UUID;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.target.IWorldObject;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.util.Stopwatch;

@SideOnly(Side.CLIENT)
class WideNetworkClient extends WideNetwork {

	@Override
	protected DeviceFinder createFinder(UUID uuid) {
		return new DeviceFinderClient(uuid);
	}

	@Override
	protected DeviceScanner createScanner(IWorldObject wo) {
		return new DeviceScannerClient(wo);
	}

	@Override
	protected int getIntervalTick() {
		return 10;
	}

	@Override
	protected Stopwatch getStopWatch() {
		return EventClient.bigComputeWatch;
	}
}
