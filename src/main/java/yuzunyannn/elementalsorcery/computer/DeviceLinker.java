package yuzunyannn.elementalsorcery.computer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceEnv;
import yuzunyannn.elementalsorcery.api.computer.IDeviceInfo;
import yuzunyannn.elementalsorcery.api.computer.IDeviceLinker;
import yuzunyannn.elementalsorcery.api.computer.IDeviceNetwork;
import yuzunyannn.elementalsorcery.api.util.render.DOItem;
import yuzunyannn.elementalsorcery.api.util.render.IDisplayable;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;
import yuzunyannn.elementalsorcery.api.util.target.WorldLocation;
import yuzunyannn.elementalsorcery.computer.exception.ComputerConnectException;

public class DeviceLinker implements IDeviceLinker, IDisplayable {

	protected boolean isClose;
	protected final UUID remoteUUID;
	protected final IDeviceNetwork network;
	protected DeviceFinder finder;
	protected CapabilityObjectRef ref;

	protected boolean badRef;

	protected int timeout;
	protected int lastTimeoutCheck;
	protected int cCheck, lastCCheck;

	protected DeviceLinker(IDeviceNetwork network, UUID udid) {
		this.network = network;
		this.remoteUUID = udid;
		this.ref = CapabilityObjectRef.INVALID;
	}

	protected DeviceLinker(IDeviceNetwork network, CapabilityObjectRef ref) {
		this.network = network;
		this.remoteUUID = ref.getCapability(Computer.DEVICE_CAPABILITY, null).getUDID();
		this.ref = ref;
	}

	protected DeviceLinker(IDeviceNetwork network, NBTTagCompound nbt) {
		this.network = network;
		this.remoteUUID = nbt.getUniqueId("uuid");
		this.isClose = nbt.getBoolean("cls");
		this.ref = CapabilityObjectRef.read(nbt.getByteArray("ref"));
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setUniqueId("uuid", remoteUUID);
		nbt.setBoolean("cls", isClose);
		nbt.setByteArray("ref", CapabilityObjectRef.write(ref));
		return nbt;
	}

	@Override
	public boolean isLocal() {
		return false;
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
	public CapabilityObjectRef getRemoteRef() {
		return ref;
	}

	@Override
	public boolean isConnecting() {
		if (badRef) return false;
		if (isClose) return false;
		if (!ref.checkReference()) return false;
		IDevice device = ref.getCapability(Computer.DEVICE_CAPABILITY, null);
		if (device == null) badRef = true;
		else if (!device.getUDID().equals(remoteUUID)) badRef = true;
		return !badRef;
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

	protected boolean afterReconnect() {
		badRef = false;
		lastTimeoutCheck = timeout = 0;
		this.finder = null;
		return true;
	}

	@Override
	public boolean onDisconnectTick(IDeviceEnv env, int dtick) {
		if ((timeout += dtick) > 20 * 30) return false;
		if (this.finder != null) {
			if (this.finder.isClose()) return false;
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
	public void onConnectTick(IDeviceEnv env, int dtick) {
		cCheck += dtick;
		if (cCheck - lastCCheck >= 20) {
			lastCCheck = cCheck;
			UUID udid = network.getDevice().getUDID();
			IDevice remoteDevice = getRemoteDevice();
			IDeviceNetwork remoteNetwork = remoteDevice.getNetwork();
			IDeviceLinker remoteLinker = remoteNetwork.getLinker(udid);
			if (remoteLinker == null || remoteLinker.isClose()) {
				boolean accpet = false;
				if (remoteNetwork.isHelpless(udid)) accpet = remoteNetwork.handshake(network.getDevice(), env, false);
				if (!accpet) close();
			} else if (remoteLinker.getRemoteDevice() != network.getDevice()) {
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
		if (!this.ref.isInvalid()) {
			this.ref.restore(env.getWorld());
			if (this.isConnecting()) return true;
		}
		badRef = true;
		WideNetwork.instance.helloWorld(network.getDevice(), env);
		this.finder = WideNetwork.instance.applyFinder(env.getWorld(), new Asker(env));
		this.ref = CapabilityObjectRef.INVALID;
		return false;
	}

	protected class Asker implements IDeviceAsker {

		final WorldLocation wl;

		public Asker(IDeviceEnv env) {
			this.wl = new WorldLocation(env.getWorld(), env.getBlockPos());
		}

		@Override
		public WorldLocation where() {
			return wl;
		}

		@Override
		public void onFind(IDeviceEnv findedEnv) {
			reconnectByOther(findedEnv);
		}

		@Override
		public void onFindFailed() {

		}

		@Override
		public boolean isUnconcerned() {
			return !badRef || isClose;
		}

		@Override
		public UUID lookFor() {
			return getRemoteUUID();
		}

	}

	@Override
	public Object toDisplayObject() {
		List<Object> list = new ArrayList<>();
		list.add("-------");
		boolean isConntect = isConnecting();
		if (isConntect) {
			IDevice device = getRemoteDevice();
			IDeviceInfo info = device.getInfo();
			String name = info.getName();
			Object nameObject;
			if (name == null || name.isEmpty()) nameObject = new TextComponentTranslation(info.getTranslationWorkKey());
			else nameObject = name;
			ItemStack icon = info.getIcon();
			if (!icon.isEmpty()) {
				Object[] objs = new Object[2];
				objs[0] = nameObject;
				objs[1] = new DOItem(icon).setScale(0.5f);
				nameObject = objs;
			}
			list.add(nameObject);
		}
		list.add(remoteUUID.toString());
		String conntect = isConntect ? "connecting" : "missing";
		list.add(new TextComponentTranslation("es.app.status", conntect));
		return list;
	}

}
