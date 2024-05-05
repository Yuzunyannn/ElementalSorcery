package yuzunyannn.elementalsorcery.api.computer;

import java.util.Collection;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IDeviceNetwork {

	public DNResult notice(DeviceNetworkRoute route, String method, DNRequest request);

	public boolean handshake(IDevice other, IDeviceEnv otherEnv, boolean simulate);

	public Collection<IDeviceLinker> getLinkers();

	@Nullable
	public IDeviceLinker getLinker(UUID udid);

	@Nonnull
	public IDevice getDevice();

	public boolean isDiscoverable();

	default public boolean isHelpless(UUID udid) {
		return false;
	}
}
