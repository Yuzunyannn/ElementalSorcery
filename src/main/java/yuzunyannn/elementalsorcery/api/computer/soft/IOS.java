package yuzunyannn.elementalsorcery.api.computer.soft;

import java.util.List;

import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.IDisk;
import yuzunyannn.elementalsorcery.api.computer.IMemory;

public interface IOS {

	IMemory getMemory();

	IDeviceStorage getMemory(APP app);

	List<IDisk> getDisks();

	IDeviceStorage getDisk(APP app, AppDiskType type);

	int exec(String appId);

	default void onMemoryChange() {
	};

	default void onDiskChange() {
	};

	default void onStarting() {
	}

	default void onClosing() {
	}

	boolean isClient();
}
