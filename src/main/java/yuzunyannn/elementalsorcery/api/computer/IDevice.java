package yuzunyannn.elementalsorcery.api.computer;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface IDevice extends ICalculatorObject, IDeviceNoticeable, ICapabilityProvider {

	@Nonnull
	public IDeviceInfo getInfo();

	@Nonnull
	public UUID getUDID();

	@Nonnull
	IDeviceNetwork getNetwork();

	@Nullable
	IDeviceEnv getEnv();

}
