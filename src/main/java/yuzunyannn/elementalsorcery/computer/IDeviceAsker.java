package yuzunyannn.elementalsorcery.computer;

import java.util.UUID;

import javax.annotation.Nullable;

import yuzunyannn.elementalsorcery.api.computer.IDeviceEnv;
import yuzunyannn.elementalsorcery.api.util.target.WorldLocation;

public interface IDeviceAsker {

	void onFind(IDeviceEnv findedEnv);

	void onFindFailed();

	boolean isUnconcerned();

	UUID lookFor();

	@Nullable
	default WorldLocation where() {
		return null;
	}

}
