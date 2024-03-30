package yuzunyannn.elementalsorcery.api.computer;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;

public interface IDeviceLinker {

	boolean isClose();

	boolean isConnecting();

	void close();

	@Nonnull
	UUID getRemoteUUID();

	// is Nonnull return after isConnecting is true
	@Nullable
	IDevice getRemoteDevice();

	boolean reconnect(IDeviceEnv env);

	boolean reconnectByOther(IDeviceEnv otherEnv);

	default void connectTick(IDeviceEnv env, int dtick) {
	}

	default boolean disconnectTick(IDeviceEnv env, int dtick) {
		return false;
	}

	public NBTTagCompound serializeNBT();

}
