package yuzunyannn.elementalsorcery.api.computer.soft;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.IDisk;
import yuzunyannn.elementalsorcery.api.computer.IMemory;
import yuzunyannn.elementalsorcery.api.computer.ISyncDetectable;

public interface IOS extends ISyncDetectable {

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

	default void onStorageChange() {
		onMemoryChange();
		onDiskChange();
	}

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

	@SideOnly(Side.CLIENT)
	default void onStorageSync(IDeviceStorage storage, List<String[]> changes) {

	}

	boolean isClient();

}
