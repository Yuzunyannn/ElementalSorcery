package yuzunyannn.elementalsorcery.computer;

import yuzunyannn.elementalsorcery.api.computer.DeviceFilePath;
import yuzunyannn.elementalsorcery.api.util.var.Variable;

public interface IStorageMonitor {

	void add(DeviceFilePath path);

	void remove(DeviceFilePath path);

	void markDirty(DeviceFilePath path);

	default void add(String... strings) {
		add(DeviceFilePath.of(strings));
	}

	default public void add(Variable<?> var) {
		add(var.key);
	}

	default void remove(String... strings) {
		remove(DeviceFilePath.of(strings));
	}

	default public void remove(Variable var) {
		remove(var.key);
	}

	default void markDirty(String... strings) {
		markDirty(DeviceFilePath.of(strings));
	}

	default public void markDirty(Variable var) {
		markDirty(var.key);
	}
}
