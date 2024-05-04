package yuzunyannn.elementalsorcery.api.computer;

import javax.annotation.Nonnull;

public interface IDeviceNoticeable {

	@Nonnull
	public DNResult notice(String method, DNRequest params);
}
