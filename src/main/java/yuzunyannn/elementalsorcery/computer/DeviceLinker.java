package yuzunyannn.elementalsorcery.computer;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceEnv;
import yuzunyannn.elementalsorcery.api.computer.IDeviceLinker;
import yuzunyannn.elementalsorcery.api.computer.IDeviceNetwork;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;
import yuzunyannn.elementalsorcery.computer.exception.ComputerConnectException;

public class DeviceLinker implements IDeviceLinker {

	protected boolean isClose;
	protected final UUID remoteUUID;
	protected final IDeviceNetwork network;
	protected DeviceLinkerFinder finder;
	protected CapabilityObjectRef ref;

	protected boolean badRef;

	protected int timeout;
	protected int lastTimeoutCheck;
	protected int cCheck, lastCCheck;

	protected DeviceLinker(IDeviceNetwork network, CapabilityObjectRef ref) {
		this.network = network;
		this.remoteUUID = ref.getCapability(Computer.DEVICE_CAPABILITY, null).getUDID();
		this.ref = ref;
	}

	protected DeviceLinker(IDeviceNetwork network, NBTTagCompound nbt) {
		this.network = network;
		this.remoteUUID = nbt.getUniqueId("uuid");
		this.isClose = nbt.getBoolean("cls");
		this.ref = CapabilityObjectRef.of();
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setUniqueId("uuid", remoteUUID);
		nbt.setBoolean("cls", isClose);
		return nbt;
	}

	@Override
	public boolean disconnectTick(IDeviceEnv env, int dtick) {
		if ((timeout += dtick) > 20 * 30) return false;
		if (this.finder != null) {
			int check = timeout / 5;
			if (lastTimeoutCheck != check) {
				lastTimeoutCheck = check;
				this.finder.update(env);
				if (this.finder == null) return true;
				if (this.finder.isClose()) return false;
			}
		} else {
			int check = timeout / 30;
			if (lastTimeoutCheck != check) {
				lastTimeoutCheck = check;
				reconnect(env);
				if (isClose) return false;
			}
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
		if (badRef) return false;
		if (isClose) return false;
		if (!ref.isValid()) return false;
		IDevice device = ref.getCapability(Computer.DEVICE_CAPABILITY, null);
		if (device == null) badRef = true;
		else if (!device.getUDID().equals(remoteUUID)) badRef = true;
		return !badRef;
	}

	protected boolean afterReconnect() {
		IDevice deviceRemote = getRemoteDevice();
		IDeviceNetwork networkRemote = deviceRemote.getNetwork();
		IDeviceLinker linker = networkRemote.getLinker(getRemoteUUID());
		if (linker == null || linker.isClose()) {
			onClose();
			return false;
		}
		badRef = false;
		lastTimeoutCheck = timeout = 0;
		this.finder = null;
		return true;
	}

	@Override
	public boolean reconnect(IDeviceEnv env) throws ComputerConnectException {
		if (isClose()) return false;
		if (!restore(env)) return false;
		return afterReconnect();
	}

	@Override
	public boolean reconnectByOther(IDeviceEnv otherEnv) {
		if (isClose()) return false;
		this.ref = otherEnv.createRef();
		return afterReconnect();
	}

	@Override
	public void connectTick(IDeviceEnv env, int dtick) {
		cCheck += dtick;
		if (cCheck - lastCCheck >= 20) {
			lastCCheck = cCheck;
			IDevice remoteDevice = getRemoteDevice();
			IDeviceNetwork remoteNetwork = remoteDevice.getNetwork();
			IDeviceLinker remoteLinker = remoteNetwork.getLinker(getRemoteUUID());
			if (remoteLinker.getRemoteDevice() != network.getDevice()) {
				if (ESAPI.isDevelop) {
					if (remoteLinker.isConnecting()) ESAPI.logger.warn("异常的linker链接，A和B的device不一致，但A认为链接正常，引用异常！");
				}
				remoteLinker.reconnectByOther(env);
			}
		}
	}

	@Override
	public IDevice getRemoteDevice() {
		return ref.getCapability(Computer.DEVICE_CAPABILITY, null);
	}

	@Override
	public void close() {
		if (isClose()) return;
		IDeviceLinker other = null;
		if (isConnecting()) {
			IDevice device = getRemoteDevice();
			IDeviceNetwork network = device.getNetwork();
			other = network.getLinker(getRemoteUUID());
		}
		onClose();
		if (other != null && !other.isClose()) other.close();
	}

	protected void onClose() {
		isClose = true;
	}

	protected boolean restore(IDeviceEnv env) {
		this.ref.restore(env.getWorld());
		if (this.ref.isValid()) {
			if (this.isConnecting()) return true;
		}
		this.finder = WideNetwork.instance.apply(this, env);
		this.ref = CapabilityObjectRef.of();
		return false;
	}
}
