package yuzunyannn.elementalsorcery.computer;

import yuzunyannn.elementalsorcery.api.computer.StoragePath;
import yuzunyannn.elementalsorcery.api.util.var.Variable;

public interface IStorageMonitor {

	void add(StoragePath path);

	void remove(StoragePath path);

	void markDirty(StoragePath path);

	default void add(String... strings) {
		add(StoragePath.of(strings));
	}

	default public void add(Variable<?> var) {
		add(var.key);
	}

	default void remove(String... strings) {
		remove(StoragePath.of(strings));
	}

	default public void remove(Variable var) {
		remove(var.key);
	}

	default void markDirty(String... strings) {
		markDirty(StoragePath.of(strings));
	}

	default public void markDirty(Variable var) {
		markDirty(var.key);
	}
}
