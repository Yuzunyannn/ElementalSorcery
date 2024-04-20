package yuzunyannn.elementalsorcery.api.computer;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;

public interface IDeviceLinker {

	boolean isClose();

	boolean isConnecting();

	boolean isLocal();

	void close();

	@Nonnull
	UUID getRemoteUUID();

	/** is Nonnull return after isConnecting is true */
	@Nullable
	IDevice getRemoteDevice();

	@Nonnull
	CapabilityObjectRef getRemoteRef();

	boolean reconnect(IDeviceEnv env);

	boolean reconnectByOther(IDeviceEnv otherEnv);

	default void onConnectTick(IDeviceEnv env, int dtick) {
	}

	default boolean onDisconnectTick(IDeviceEnv env, int dtick) {
		return false;
	}

	public NBTTagCompound serializeNBT();

}
