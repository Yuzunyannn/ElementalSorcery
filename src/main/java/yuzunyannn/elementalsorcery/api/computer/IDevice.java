package yuzunyannn.elementalsorcery.api.computer;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface IDevice extends ICalculatorObject, IDeviceNoticeable, ICapabilityProvider {

	@Nonnull
	public List<IDisk> getDisks();

	@Nonnull
	public String getName();

	@Nonnull
	public UUID getUDID();

	@Nonnull
	IDeviceNetwork getNetwork();

}
