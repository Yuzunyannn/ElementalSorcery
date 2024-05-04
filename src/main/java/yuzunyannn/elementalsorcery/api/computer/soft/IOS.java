package yuzunyannn.elementalsorcery.api.computer.soft;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.computer.DNRequest;
import yuzunyannn.elementalsorcery.api.computer.DNResult;
import yuzunyannn.elementalsorcery.api.computer.IDeviceInfo;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.api.computer.IDisk;
import yuzunyannn.elementalsorcery.api.util.detecter.ISyncDetectable;
import yuzunyannn.elementalsorcery.api.util.target.IObjectGetter;

public interface IOS extends ISyncDetectable<NBTTagCompound>, INBTSerializable<NBTTagCompound> {

	UUID getDeviceUUID();

	IDeviceInfo getDeviceInfo();

	List<IDisk> getDisks();

	@Nullable
	IDeviceStorage getDisk(App app, AppDiskType type);

	boolean isRunning();

	void markDirty(App app);

	int exec(App parent, String appId);

	int setForeground(int pid);

	int getForeground();

	void abort(int pid, IComputerException e);

	boolean exit(int pid);

	@Nullable
	App getAppInst(int pid);

	void message(App app, NBTTagCompound nbt);

	@Nonnull
	List<UUID> filterLinkedDevice(@Nonnull Capability<?> capability, @Nullable Object key);

	default List<UUID> filterLinkedDevice(@Nonnull Capability<?> capability) {
		return this.filterLinkedDevice(capability, null);
	}

	@Nonnull
	<T> IObjectGetter<T> askCapability(@Nullable UUID udid, @Nonnull Capability<T> capability, @Nullable Object key);

	DNResult notice(@Nullable UUID udid, String method, DNRequest params);

	default DNResult notice(String method, DNRequest params) {
		return this.notice(null, method, params);
	}

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

	@SideOnly(Side.CLIENT)
	default int getTopTask() {
		return -1;
	}

	boolean isRemote();

}
