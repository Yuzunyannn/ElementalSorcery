package yuzunyannn.elementalsorcery.api.computer;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

public interface IDevice extends ICalculatorObject, IDeviceNoticeable {

	@Nonnull
	public List<IDisk> getDisks();

	@Nonnull
	public String getName();

	@Nonnull
	public UUID getUDID();

	default public boolean hasAbility(String ability) {
		return false;
	}

	@Nonnull
	IDeviceNetwork getNetwork();

}
