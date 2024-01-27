package yuzunyannn.elementalsorcery.computer;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceEnv;
import yuzunyannn.elementalsorcery.api.computer.IDeviceLinkTimeoutable;
import yuzunyannn.elementalsorcery.api.computer.IDeviceLinker;
import yuzunyannn.elementalsorcery.api.computer.IDeviceNetwork;
import yuzunyannn.elementalsorcery.computer.exception.ComputerConnectException;

public class DeviceLinker implements IDeviceLinker, IDeviceLinkTimeoutable {

	protected final int tid;
	protected boolean isClose;
	protected final UUID remoteUUID;
	protected final IDeviceNetwork network;
	protected int timeout;
	protected int lastTimeoutCheck;
	protected DeviceLinkerRef ref;
	protected boolean badRef;

	protected DeviceLinker(int tid, IDeviceNetwork network, DeviceLinkerRef ref) {
		this.tid = tid;
		this.network = network;
		this.remoteUUID = ref.getUUID();
		this.ref = ref;
	}

	protected DeviceLinker(IDeviceNetwork network, NBTTagCompound nbt) {
		this.tid = nbt.getInteger("tid");
		this.network = network;
		this.remoteUUID = nbt.getUniqueId("uuid");
		this.isClose = nbt.getBoolean("cls");
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("tid", (byte) tid);
		nbt.setUniqueId("uuid", remoteUUID);
		nbt.setBoolean("cls", isClose);
		return nbt;
	}

	@Override
	public boolean tryReconnect(IDeviceEnv env, int dtick) {
		if ((timeout += dtick) > 20 * 30) return false;
		int check = timeout / 30;
		if (lastTimeoutCheck != check) {
			lastTimeoutCheck = check;
			reconnect(env);
		}
		return true;
	}

	@Override
	public boolean isClose() {
		return isClose;
	}

	@Override
	public UUID getRemoteUUID() {
		return remoteUUID;
	}

	@Override
	public boolean isConnecting() {
		return this.getRemoteDevice() != null;
	}

	@Override
	public boolean reconnect(IDeviceEnv env) throws ComputerConnectException {
		if (isClose()) return false;
		restore(env);
		IDevice deviceRemote = getRemoteDevice();
		if (deviceRemote == null) return false;
		IDeviceNetwork networkRemote = deviceRemote.getNetwork();
		IDeviceLinker linker = networkRemote.getLinker(getRemoteUUID());
		IDevice deivce = network.getDevice();
		if (linker == null || linker.isClose()) {
			onClose();
			return false;
		} else if (linker.getRemoteDevice() != deivce) {
			linker.reconnect(env);
			if (linker.getRemoteDevice() != deivce) throw new ComputerConnectException(deivce, "repeat");
		}
		lastTimeoutCheck = timeout = 0;
		return true;
	}

	@Override
	public void close() {
		if (isClose()) return;
		IDevice device = getRemoteDevice();
		onClose();
		if (device != null) {
			IDeviceNetwork network = device.getNetwork();
			IDeviceLinker linker = network.getLinker(getRemoteUUID());
			if (linker != null && !linker.isClose()) linker.close();
		}
	}

	protected void onClose() {
		isClose = true;
	}

	protected void restore(IDeviceEnv env) {
		DeviceLinkerRef newRef = this.ref.getRestoreFunc().apply(remoteUUID, env);
		if (newRef == null) return;
		this.ref = newRef;
		badRef = false;
	}

	@Override
	public IDevice getRemoteDevice() {
		if (isClose()) return null;
		if (badRef) return null;
		IDevice device = ref.getDevice();
		if (device == null) badRef = true;
		return device;
	}
}
