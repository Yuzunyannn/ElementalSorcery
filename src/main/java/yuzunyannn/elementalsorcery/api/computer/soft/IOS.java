package yuzunyannn.elementalsorcery.api.computer.soft;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.IDisk;
import yuzunyannn.elementalsorcery.api.util.ISyncDetectable;

public interface IOS extends ISyncDetectable<NBTTagCompound>, INBTSerializable<NBTTagCompound> {

	List<IDisk> getDisks();

	IDeviceStorage getDisk(APP app, AppDiskType type);
	
	boolean isRunning();

	void markDirty(APP app);

	int exec(APP parent, String appId);

	int setForeground(int pid);

	int getForeground();

	void abort(int pid, IComputerException e);

	@Nullable
	APP getAppInst(int pid);

	default void onDiskChange(boolean onlyData) {
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
