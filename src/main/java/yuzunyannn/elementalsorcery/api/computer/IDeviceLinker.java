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

	@Nullable
	IDevice getRemoteDevice();

	boolean reconnect(IDeviceEnv env);

	public NBTTagCompound serializeNBT();

}
