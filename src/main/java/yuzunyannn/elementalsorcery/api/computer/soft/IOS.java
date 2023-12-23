package yuzunyannn.elementalsorcery.api.computer.soft;

import java.util.List;

import javax.annotation.Nullable;

import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.IDisk;
import yuzunyannn.elementalsorcery.api.computer.IMemory;

public interface IOS {

	IMemory getMemory();

	IMemory getMemory(APP app);

	List<IDisk> getDisks();

	IDeviceStorage getDisk(APP app, AppDiskType type);

	int exec(APP parent, String appId);

	int setForeground(int pid);

	int getForeground();

	void abort(int pid, IComputerException e);

	@Nullable
	APP getAppInst(int pid);

	default void onMemoryChange() {
	};

	default void onDiskChange() {
	};

	default void onStarting() {
	}

	default void onClosing() {
	}

	default void onUpdate() {
	};

	boolean isClient();

}
