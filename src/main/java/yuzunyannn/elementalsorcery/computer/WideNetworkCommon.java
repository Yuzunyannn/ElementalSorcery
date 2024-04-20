package yuzunyannn.elementalsorcery.computer;

import java.util.UUID;

import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceEnv;
import yuzunyannn.elementalsorcery.api.util.target.IWorldObject;
import yuzunyannn.elementalsorcery.logics.EventServer;
import yuzunyannn.elementalsorcery.util.LambdaReference;
import yuzunyannn.elementalsorcery.util.Stopwatch;
import yuzunyannn.elementalsorcery.util.helper.GameHelper;

public class WideNetworkCommon extends WideNetwork {

	public static final WideNetwork instanceClient;

	static {
		LambdaReference<WideNetwork> instance = LambdaReference.of(null);
		GameHelper.clientRun(() -> {
			instance.set(new WideNetworkClient());
		});
		instanceClient = instance.get();
	}

	public DeviceFinder applyFinder(World world, IDeviceAsker asker) {
		if (world.isRemote) return instanceClient.applyFinder(world, asker);
		return super.applyFinder(world, asker);
	}

	@Override
	public DeviceScanner applyScanner(IWorldObject wo) {
		if (wo.getWorld().isRemote) return instanceClient.applyScanner(wo);
		return super.applyScanner(wo);
	}

	@Override
	protected DeviceFinder createFinder(UUID uuid) {
		return new DeviceFinder(uuid);
	}

	@Override
	protected DeviceScanner createScanner(IWorldObject wo) {
		return new DeviceScanner(wo);
	}

	public void helloWorld(IDevice device, IDeviceEnv env) {
		if (env.isRemote()) instanceClient.helloWorld(device, env);
		else super.helloWorld(device, env);
	}

	@Override
	protected Stopwatch getStopWatch() {
		return EventServer.bigComputeWatch;
	}

}
