package yuzunyannn.elementalsorcery.computer;

import java.util.UUID;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.Stopwatch;

@SideOnly(Side.CLIENT)
public class DeviceFinderClient extends DeviceFinder {

	public DeviceFinderClient(UUID target) {
		super(target);
	}

	@Override
	public void update(int dt, Stopwatch stopwatch) {

	}
}
