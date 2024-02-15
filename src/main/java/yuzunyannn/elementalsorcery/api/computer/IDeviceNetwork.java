package yuzunyannn.elementalsorcery.api.computer;

import java.util.Collection;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IDeviceNetwork {

	public boolean handshake(IDeviceLinker other);

	public Collection<IDeviceLinker> getLinkers();

	@Nullable
	public IDeviceLinker getLinker(UUID uuid);

	@Nonnull
	public IDevice getDevice();

	public boolean isDiscoverable();
}
